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
package com.ibm.cloud.objectstorage.oauth;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClientBuilder;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.http.apache.SdkProxyRoutePlanner;
import com.ibm.cloud.objectstorage.http.apache.utils.ApacheUtils;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenProvider;
import com.ibm.cloud.objectstorage.oauth.OAuthServiceException;
import com.ibm.cloud.objectstorage.oauth.Token;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;

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

	private TokenProvider provider;

	private volatile Token token;
	// flag to signify if an async refresh process has already started
	private volatile boolean asyncInProgress = false;

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

	/** The client configuration */
	private ClientConfiguration clientConfiguration;

	/** The client http setting */
	private HttpClientSettings httpClientSettings;

	// Executor service for token refresh
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
	 * Overwrite the default IAM endpoint. This should only be done in a
	 * development or staging environment
	 *
	 * @param iamEndpoint
	 *            The http endpoint to retrieve the token
	 */
	public void setIamEndpoint(String iamEndpoint) {
		this.iamEndpoint = iamEndpoint;
		/* The TokenManager's variable does nothing, so if it is set, pass it down */
		if (getProvider() instanceof DefaultTokenProvider) {
			DefaultTokenProvider defaultProvider = (DefaultTokenProvider)getProvider();
			defaultProvider.setIamEndpoint(iamEndpoint);
		}
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

		// check if token should be refreshed. If a refreshtoken is not present, the token manager will call upon the original tokenprovider retrieve a fresh token
		if (isTokenExpiring(token) && !isAsyncInProgress()) {
			if (null != token.getRefresh_token() && "not_supported" != token.getRefresh_token()) {
				this.asyncInProgress = true;
				submitRefreshTask();
			} else {
				retrieveToken();
				token = retrieveTokenFromCache();
			}
		}

		if (token.getAccess_token() != null && !token.getAccess_token().isEmpty()) {
			return token.getAccess_token();
		} else if (token.getDelegated_refresh_token()!= null && !token.getDelegated_refresh_token().isEmpty()) {
			return token.getDelegated_refresh_token();
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
	protected synchronized void cacheToken(final Token token) {

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
			log.debug("Token is expiring");
			return true;
		} else {
			log.debug("Token is not expiring." + token.getRefreshTime() + " > " + currentTime);
			return false;
		}
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

		// If we have no token or if the token we have has expired, get a new token
		if (token == null || (Long.valueOf(token.getExpiration()) < System.currentTimeMillis() / 1000L)) {
			log.debug("Token needs to be refreshed, retrieving from provider");
			for (int attempt = 1; attempt <= this.iamMaxRetry; attempt++) {
				try {
					token = provider.retrieveToken();
					break;  // We received a token; no exceptions were thrown
				} catch (OAuthServiceException exception) {
					log.debug("Exception retrieving IAM token on attempt " + attempt
						+ ". Returned status code " + exception.getStatusCode()
						+ ". Error Message: " + exception.getErrorMessage()
						+ ". Status Message: " + exception.getStatusMessage());
					// Check if we've run out of retries and need to rethrow this exception
					if (attempt >= this.iamMaxRetry) {
						throw exception;
					}
				}
			}

			// Retrieving tokens should always return a non-null value,
			//  so even if token was originally null, it should not be now.
			if (token == null) {
				throw new OAuthServiceException("TokenProvider.retrieveToken() " +
					" returned null and instead of throwing an exception. This is a bug." +
					" Custom TokenProvider classes should return a token or thrown exceptions.");
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
		TokenRefreshTask tokenRefreshTask = new TokenRefreshTask(this);
		executor.execute(tokenRefreshTask);
		log.debug("Submitted token refresh task");
	}

	/**
	 * boolean value to signal if the async refresh method is already in use
	 *
	 * @return boolean
	 */
	protected boolean isAsyncInProgress() {
		log.debug("Aysnchrnonous job in progress : " + asyncInProgress);
		return asyncInProgress;
	}
	/**
	 * Client config to customise the IAM Client
	 *
	 * @return
	 */
	public ClientConfiguration getClientConfiguration() {
		return clientConfiguration;
	}

	/**
	 * Set the client config that is been used on the s3client
	 *
	 * @param clientConfiguration
	 */
	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
		if (clientConfiguration != null) {
			this.httpClientSettings = HttpClientSettings.adapt(clientConfiguration);
			if (getProvider() instanceof DefaultTokenProvider) {
				DefaultTokenProvider defaultProvider = (DefaultTokenProvider)getProvider();
				defaultProvider.setHttpClientSettings(httpClientSettings);
			}
			if (getProvider() instanceof DelegateTokenProvider) {
				DelegateTokenProvider delegateProvider = (DelegateTokenProvider)getProvider();
				delegateProvider.setHttpClientSettings(httpClientSettings);
			}
		}
	}

	private boolean shouldRetry(int statusCode) {
		if (NON_RETRYABLE_STATUS_CODES.contains(statusCode)) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			executor.shutdown();
		} catch (Throwable t) {
			throw t;
		} finally {
			super.finalize();
		}
	}

	class TokenRefreshTask implements Runnable {
		private DefaultTokenManager tokenManager;
		private Token refreshedToken = null;

		TokenRefreshTask(DefaultTokenManager tokenManager) {
			this.tokenManager = tokenManager;
		}

		@Override
		public void run() {
			try {
				if (!(tokenManager.getProvider() instanceof DefaultTokenProvider)) {
					log.info("OAuthTokenManager.TokenRefreshTask: Token Provider is not of type DefaultTokenProvider, so using the refresh token is not supported.");
					return;
				}

				// Try to get a new token as long as it has not yet expired
				while ((this.refreshedToken == null)
						&& (System.currentTimeMillis() / 1000 < tokenManager.token.getExpirationTime())) {
					try {
						log.info("OAuthTokenManager.TokenRefreshTask: Attempting to retrieve refresh token");
						refreshedToken = ((DefaultTokenProvider)tokenManager.getProvider()).retrieveTokenWithRefresh(tokenManager.token.getRefresh_token());
						break;  // We received a token; no exceptions were thrown
					} catch (OAuthServiceException exception) {
						log.info("OAuthTokenManager.TokenRefreshTask: Exception retrieving IAM token"
							+ ". Returned status code " + exception.getStatusCode()
							+ ". Error Message: " + exception.getErrorMessage()
							+ ". Status Message: " + exception.getStatusMessage());
						// Wait before retrying
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							log.info("Token refresh task interrupted: " + e.getMessage());
						}
						if (System.currentTimeMillis() / 1000 >= tokenManager.token.getExpirationTime()) {
							// The token has expired, so we want to rethrow this exception as the failure reason
							throw exception;
						}
					}
				}

				if (refreshedToken != null) {
					tokenManager.cacheToken(refreshedToken);
					log.info("OAuthTokenManager.TokenRefreshTask: Token refreshed");
				} else if (System.currentTimeMillis() / 1000 < tokenManager.token.getExpirationTime()) {
					// Retrieving tokens should always return a non-null value, so if we are here,
					//   we broke the loop because of the 'break', not because the token expired.
					log.info("OAuthTokenManager.TokenRefreshTask: Refresh aborted before token expiration");
					throw new OAuthServiceException("OAuthTokenManager.TokenRefreshTask: "
						+ " TokenProvider.retrieveTokenWithRefresh()"
						+ " returned null and instead of throwing an exception. This is a bug."
						+ " Custom TokenProvider classes should return a token or thrown exceptions.");
				} else {
					log.info("OAuthTokenManager.TokenRefreshTask: Failed to refresh token");
				}
			} finally {
				tokenManager.asyncInProgress = false;
			}
		}
	}

	/**
	 * Add a proxy to the http request if it has been set within settings
	 *
	 * @param builder
	 * 			Builder used to create the http client
	 * @param settings
	 * 			Settings which contain any proxy configuration details
	 */
	public static void addProxyConfig(HttpClientBuilder builder,
			HttpClientSettings settings) {
		if (settings.isProxyEnabled()) {

			log.info("Configuring Proxy. Proxy Host: " + settings.getProxyHost() + " " +
					"Proxy Port: " + settings.getProxyPort());

			builder.setRoutePlanner(new SdkProxyRoutePlanner(
					settings.getProxyHost(), settings.getProxyPort(), settings.getNonProxyHosts()));

			if (settings.isAuthenticatedProxy()) {
				builder.setDefaultCredentialsProvider(ApacheUtils.newProxyCredentialsProvider(settings));
			}
		}
	}
}
