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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.http.conn.ssl.SdkTLSSocketFactory;
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
    
    /**variable to overwrite the global SDKGlobalConfiguration.IAM_ENDPOINT **/ 
    private String iamEndpoint = SDKGlobalConfiguration.IAM_ENDPOINT; 


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
	// check if expires in < 15secs
	if (isTokenExpiring(token)) {
	    retrieveIAMToken(token.getRefresh_token());
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
     * Check if the current cached token is expiring in less than 15 seconds. If
     * it is, an asynchronous call is made to the IAM service to update the
     * cache.
     * 
     * @param token
     *            The IAM Token object
     * 
     * @return boolean Indicates if the currently cached token is about to
     *         expire in 15 seconds
     */
    protected boolean isTokenExpiring(final Token token) {

	log.debug("OAuthTokenManager.isTokenExpiring");

	final long currentTime = System.currentTimeMillis() / 1000L;

	if (Long.valueOf(token.getExpiration()) - currentTime < 15) {
	    return true;
	}
	return false;
    }

    /**
     * Retrieve the Token from IAM using a HttpClient asynchronously. The token
     * will replace the currently cached token
     * 
     * @param refreshToken
     */
    protected void retrieveIAMTokenAsync(String refreshToken) {

	log.debug("OAuthTokenManager.retrieveIAMTokenAsync");

	CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
	if (!isAsyncInProgress()) {
	    try {
		asyncInProgress = true;
		httpclient.start();
		HttpPost post = new HttpPost(SDKGlobalConfiguration.IAM_ENDPOINT);
		post.setHeader("Authorization", BASIC_AUTH);
		post.setHeader("Content-Type", CONTENT_TYPE);
		post.setHeader("Accept", ACCEPT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", REFRESH_GRANT_TYPE));
		urlParameters.add(new BasicNameValuePair("response_type", RESPONSE_TYPE));
		urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		Future<HttpResponse> future = httpclient.execute(post, null);
		HttpResponse response = future.get();

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
		    log.debug("Repsonse code= " + response.getStatusLine().getStatusCode()
			    + ", throwing OAuthServiceException");
		    throw new OAuthServiceException("Token retrival from IAM service failed with refresh token");
		}

		HttpEntity entity = response.getEntity();
		String resultStr = EntityUtils.toString(entity);

		ObjectMapper mapper = new ObjectMapper();

		Token token = mapper.readValue(resultStr, Token.class);

		cacheToken(token);

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (ExecutionException e) {
		e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    } catch (ClientProtocolException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		asyncInProgress = false;
		try {
		    httpclient.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * Retrieve the Token from IAM using a HttpClient synchronously. The token
     * will replace the currently cached token
     * 
     * @param refreshToken
     */
    protected void retrieveIAMToken(String refreshToken) {

	log.debug("OAuthTokenManager.retrieveIAMToken");

	try {

	    SSLContext sslContext = SSLContexts.createDefault();

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
		log.debug("Repsonse code= " + response.getStatusLine().getStatusCode()
			+ ", throwing OAuthServiceException");
		throw new OAuthServiceException("Token retrival from IAM service failed with refresh token");
	    }

	    HttpEntity entity = response.getEntity();
	    String resultStr = EntityUtils.toString(entity);

	    ObjectMapper mapper = new ObjectMapper();

	    Token token = mapper.readValue(resultStr, Token.class);

	    cacheToken(token);

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
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

	if (token == null || (Long.valueOf(token.getExpiration()) < System.currentTimeMillis() / 1000L)) {

	    log.debug("Token is null, retrieving initial token from provider");
	    token = provider.retrieveToken();
	    cacheToken(token);
	}
    }

    /**
     * boolean value to signal if the async refresh method is already in use
     * 
     * @return boolean
     */
    protected boolean isAsyncInProgress() {

	return asyncInProgress;
    }
}
