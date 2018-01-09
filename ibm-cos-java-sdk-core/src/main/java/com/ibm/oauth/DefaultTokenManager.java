/* 
* Copyright 2017 IBM Corp. All Rights Reserved. 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with 
* the License. You may obtain a copy of the License at 
* 
* http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
* specific language governing permissions and limitations under the License. 
*/
package com.ibm.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.http.conn.ssl.SdkTLSSocketFactory;
import com.amazonaws.http.apache.client.impl.ApacheConnectionManagerFactory.TrustingX509TrustManager;
import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import com.ibm.oauth.DefaultTokenProvider;
import com.ibm.oauth.OAuthServiceException;
import com.ibm.oauth.Token;
import com.ibm.oauth.TokenManager;
import com.ibm.oauth.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of Token Manager. This design will allow the class to
 * be created with multiple constructors, yet only one instance can be created.
 * The DefaultTokenProvider is used to retrieve the token from the IAM Service
 * which uses the api key method. A client can also use their own implementation
 * of TokenHandler
 * 
 *
 */
public class DefaultTokenManager implements TokenManager {

	protected static final InternalLogApi log = InternalLogFactory.getLog(DefaultTokenManager.class);

	// Http paramaters
	private static final String BASIC_AUTH = "Basic Yng6Yng=";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String ACCEPT = "application/json";

	private static final String REFRESH_GRANT_TYPE = "refresh_token";
	private static final String RESPONSE_TYPE = "cloud_iam";

	private TokenProvider provider;

	private Token token;
	// flag to signify if an async refresh process has already started
	private boolean asyncInProgress = false;

	/** variable to overwrite the global SDKGlobalConfiguration.IAM_ENDPOINT **/
	private String iamEndpoint = SDKGlobalConfiguration.IAM_ENDPOINT;

	/**
	 * variable to overwrite the global SDKGlobalConfiguration.IAM_MAX_RETRY
	 **/
	private int iamMaxRetry = SDKGlobalConfiguration.IAM_MAX_RETRY;

	/**
	 * variable to overwrite the global SDKGlobalConfiguration.IAM_MAX_RETRY
	 **/
	private double iamRefreshOffset = SDKGlobalConfiguration.IAM_REFRESH_OFFSET;
	
	// Executor service for token refresh
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Future<Token> refreshToken;
	
	/**
	 * Set of HTTP response codes that should attempt retry.
	 */
	private static final Set<Integer> NON_RETRYABLE_STATUS_CODES = new HashSet<Integer>(4);

	static {
		NON_RETRYABLE_STATUS_CODES.add(HttpStatus.SC_BAD_REQUEST);
		NON_RETRYABLE_STATUS_CODES.add(HttpStatus.SC_UNAUTHORIZED);
		NON_RETRYABLE_STATUS_CODES.add(HttpStatus.SC_FORBIDDEN);
		NON_RETRYABLE_STATUS_CODES.add(HttpStatus.SC_NOT_FOUND);
	}

	/**
	 * Default constructor using the apiKey. This instance will use the
	 * DefaultTokenProvider to retrieve the access token.
	 * 
	 * @param apiKey
	 *            The IBM API Key
	 */
	public DefaultTokenManager(String apiKey) {

		log.debug("DefaultTokenManager api key constructor");
		this.provider = new DefaultTokenProvider(apiKey);
	}

	/**
	 * Constructor which will allow a custom TokenHandler to retrieve the token
	 * from the IAM Service
	 * 
	 * @param provider
	 *            The Token Provider which will retrieve the Token Object from
	 *            the IAM service
	 */
	public DefaultTokenManager(TokenProvider provider) {

		this.provider = provider;
	}

	/**
	 * Over write the default IAM endpoint. This should only be done in a
	 * development or staging environment
	 * 
	 * @param iamEndpoint
	 *            The http endpoint to retrieve the token
	 */
	public void setIamEndpoint(String iamEndpoint) {
		this.iamEndpoint = iamEndpoint;
	}

	/**
	 * Over write the default IAM refresh offset. This should only be done in a
	 * development or staging environment
	 * 
	 * @param offset
	 *            The percentage of token life that token should be refreshed
	 *            before expiration.
	 */
	public void setIamRefreshOffset(double offset) {
		this.iamRefreshOffset = offset;
	}

	/**
	 * Over write the default IAM max retry count. This should only be done in a
	 * development or staging environment
	 * 
	 * @param offset
	 *            The max number of times to attempt IAM token retrieval.
	 */
	public void setIamMaxRetry(int count) {
		this.iamMaxRetry = count;
	}

	/**
	 * Return the TokenProvider used by the TokenManager
	 * 
	 */
	public TokenProvider getProvider() {
		return provider;
	}

	/**
	 * Retrieve the access token String from the OAuth2 token object
	 * 
	 */
	@Override
	public String getToken() {

		log.debug("DefaultTokenManager getToken()");

		if (this.refreshToken != null && this.refreshToken.isDone() && !this.refreshToken.isCancelled()) {
			try {
				Token refreshedToken = this.refreshToken.get();
				if(refreshedToken != null)
					cacheToken(refreshedToken);
				this.refreshToken = null;
			} catch (InterruptedException e) {
				log.debug("IAM Token refresh interrupted.", e);
			} catch (ExecutionException e) {
				log.debug("ExecutionException during IAM Token refresh.", e);
			}
		}

		if (!checkCache()) {
			retrieveToken();
		}

		// retrieve from cache
		if (token == null) {
			token = retrieveTokenFromCache();
		}

		// check if expired
		if (hasTokenExpired(token)) {
			token = retrieveTokenFromCache();
		}

		// check if token should be refreshed
		if (isTokenExpiring(token) && this.refreshToken == null) {
			submitRefreshTask();
		}

		if (token.getAccess_token() != null && !token.getAccess_token().isEmpty()) {
			return token.getAccess_token();
		} else if (token.getIms_token() != null && !token.getIms_token().isEmpty()) {
			return token.getIms_token();
		} else {
			return token.getUaa_token();
		}

	}

	/**
	 * Check if cache has a Token object stored
	 * 
	 * @return boolean Indicates if the Token has been cached
	 */
	protected boolean checkCache() {

		log.debug("OAuthTokenManager.checkCache()");

		boolean tokenExists = getCachedToken() == null ? false : true;

		return tokenExists;
	}

	/**
	 * Add the Token object to in-memory cache
	 * 
	 * @param token
	 *            The IAM Token object
	 */
	protected void cacheToken(final Token token) {

		log.debug("OAuthTokenManager.cacheToken");

		// Parse token expires in seconds.
		int tokenExpiresInSecs;
		try {
			tokenExpiresInSecs = Integer.parseInt(token.getExpires_in());
		} catch (NumberFormatException exception) {
			tokenExpiresInSecs = 0;
		}

		// Parse token expiration time
		long tokenExpirationTime;
		try {
			tokenExpirationTime = Long.parseLong(token.getExpiration());
		} catch (NumberFormatException exception) {
			tokenExpirationTime = 0;
		}

		// Calculate token refresh time based on lifespan percentage offset.
		long refreshBeforeExpirySecs = (long) (tokenExpiresInSecs * this.iamRefreshOffset);
		long tokenRefreshTime = tokenExpirationTime - refreshBeforeExpirySecs;

		token.setRefreshTime(tokenRefreshTime);
		token.setExpirationTime(tokenExpirationTime);

		setTokenCache(token);
	}

	/**
	 * Retrieve the IAM token from cache storage
	 * 
	 * @return Token The IAM Token object
	 */
	protected Token retrieveTokenFromCache() {

		log.debug("OAuthTokenManager.retrieveTokenFromCache");

		return getCachedToken();
	}

	/**
	 * Check if the current cached token has expired. If it has a synchronous
	 * http call is made to the IAM service to retrieve & store a new token
	 * 
	 * @param token
	 *            The IAM Token object
	 * 
	 * @return boolean Indicates if the currently cached token has expired
	 */
	protected boolean hasTokenExpired(final Token token) {

		log.debug("OAuthTokenManager.hasTokenExpired");

		final long currentTime = System.currentTimeMillis() / 1000L;

		if (Long.valueOf(token.getExpiration()) < currentTime) {
			retrieveToken();
			return true;
		}

		return false;
	}

	/**
	 * Check if the current cached token is expiring in less than the given
	 * offset. If it is, an asynchronous call is made to the IAM service to
	 * update the cache.
	 * 
	 * @param token
	 *            The IAM Token object
	 * 
	 * @return boolean Indicates if the currently cached token is due refresh
	 */
	protected boolean isTokenExpiring(final Token token) {

		log.debug("OAuthTokenManager.isTokenExpiring");

		final long currentTime = System.currentTimeMillis() / 1000L;

		if (currentTime > token.getRefreshTime()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean currently cached Token object
	 */
	protected Token getCachedToken() {
		return this.token;
	}

	/**
	 * 
	 * @param token
	 *            Sets the Token object in cache
	 */
	protected void setTokenCache(Token token) {
		this.token = token;
	}

	/**
	 * retrieve token from provider. Ensures each thread checks the token is
	 * null prior to making the callout to IAM
	 * 
	 */
	protected synchronized void retrieveToken() {

		log.debug("OAuthTokenManager.retrieveToken");

		if (token == null || (Long.valueOf(token.getExpiration()) < System.currentTimeMillis() / 1000L)) {

			log.debug("Token is null, retrieving initial token from provider");
			boolean tokenRequest = true;
			int retryCount = 0;

			while (tokenRequest && retryCount < this.iamMaxRetry) {
				try {
					++retryCount;
					token = provider.retrieveToken();
					tokenRequest = false;
				} catch (OAuthServiceException exception) {
					log.debug("Exception retrieving IAM token. Returned status code " + exception.getStatusCode()
							+ "Retry attempt " + retryCount);
					tokenRequest = shouldRetry(exception.getStatusCode()) ? true : false;
					if (!tokenRequest || retryCount == this.iamMaxRetry)
						throw exception;
				}
			}

			cacheToken(token);
		}
	}
	
	/**
	 * Submits a token refresh task
	 * 
	 * @return void
	 */
	protected void submitRefreshTask() {
		TokenRefreshTask tokenRefreshTask = new TokenRefreshTask(iamEndpoint, token.getRefresh_token(),
				token.getExpirationTime());
		this.refreshToken = executor.submit(tokenRefreshTask);		
	}

	/**
	 * boolean value to signal if the async refresh method is already in use
	 * 
	 * @return boolean
	 */
	protected boolean isAsyncInProgress() {

		return asyncInProgress;
	}

	private boolean shouldRetry(int statusCode) {
		if (NON_RETRYABLE_STATUS_CODES.contains(statusCode))
			return false;
		else
			return true;
	}
	
	@Override
    protected void finalize() throws Throwable {
		if(this.refreshToken != null && !this.refreshToken.isDone() && !this.refreshToken.isCancelled())
			refreshToken.cancel(true);
		executor.shutdown();
	}

	class TokenRefreshTask implements Callable<Token> {
		private String iamEndpoint;
		private String refreshToken;
		private long tokenExpirationTime;
		private Token token;

		TokenRefreshTask(String iamEndpoint, String refreshToken, long tokenExpirationTime) {
			this.iamEndpoint = iamEndpoint;
			this.refreshToken = refreshToken;
			this.tokenExpirationTime = tokenExpirationTime;
		}

		@Override
		public Token call() throws Exception {

			while ((this.token == null) && (System.currentTimeMillis() / 1000 < tokenExpirationTime)) {
				try {
					token = retrieveIAMToken(refreshToken);
				} catch (OAuthServiceException exception) {
					log.info("Exception refreshing IAM token. Returned status code " + exception.getStatusCode());
				}

				if (token == null) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return token;
		}

		/**
		 * Retrieve the Token from IAM using a HttpClient synchronously. The
		 * token will replace the currently cached token
		 * 
		 * @param refreshToken
		 */
		protected Token retrieveIAMToken(String refreshToken) {

			log.debug("OAuthTokenManager.retrieveIAMToken");

			try {

				SSLContext sslContext;
				/*
				 * If SSL cert checking for endpoints has been explicitly
				 * disabled, register a new scheme for HTTPS that won't cause
				 * self-signed certs to error out.
				 */
				if (SDKGlobalConfiguration.isCertCheckingDisabled()) {
					if (log.isWarnEnabled()) {
						log.warn("SSL Certificate checking for endpoints has been " + "explicitly disabled.");
					}
					sslContext = SSLContext.getInstance("TLS");
					sslContext.init(null, new TrustManager[] { new TrustingX509TrustManager() }, null);
				} else {
					sslContext = SSLContexts.createDefault();
				}

				SSLConnectionSocketFactory sslsf = new SdkTLSSocketFactory(sslContext, new DefaultHostnameVerifier());

				HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
				HttpPost post = new HttpPost(iamEndpoint);
				post.setHeader("Authorization", BASIC_AUTH);
				post.setHeader("Content-Type", CONTENT_TYPE);
				post.setHeader("Accept", ACCEPT);

				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("grant_type", REFRESH_GRANT_TYPE));
				urlParameters.add(new BasicNameValuePair("response_type", RESPONSE_TYPE));
				urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));

				post.setEntity(new UrlEncodedFormEntity(urlParameters));

				final HttpResponse response = client.execute(post);

				if (response.getStatusLine().getStatusCode() / 100 != 2) {
					log.info("Response code= " + response.getStatusLine().getStatusCode() + ", Reason= "
							+ response.getStatusLine().getReasonPhrase() + ".Throwing OAuthServiceException");
					OAuthServiceException exception = new OAuthServiceException(
							"Token retrival from IAM service failed with refresh token");
					exception.setStatusCode(response.getStatusLine().getStatusCode());
					exception.setStatusMessage(response.getStatusLine().getReasonPhrase());
					throw exception;
				}

				HttpEntity entity = response.getEntity();
				String resultStr = EntityUtils.toString(entity);

				ObjectMapper mapper = new ObjectMapper();

				Token token = mapper.readValue(resultStr, Token.class);

				return token;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
