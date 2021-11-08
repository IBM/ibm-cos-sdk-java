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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ApacheConnectionManagerFactory.TrustingX509TrustManager;
import com.ibm.cloud.objectstorage.http.conn.ssl.SdkTLSSocketFactory;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.ibm.cloud.objectstorage.oauth.OAuthServiceException;
import com.ibm.cloud.objectstorage.oauth.Token;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation to retrieve token from the IAM service using the api
 * key
 *
 */
public class DefaultTokenProvider implements TokenProvider {

    protected static final InternalLogApi log = InternalLogFactory.getLog(DefaultTokenProvider.class);

    // Http parameters
    private static final String BASIC_AUTH = "Basic Yng6Yng=";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT = "application/json";

    private static final String GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey";
    private static final String REFRESH_GRANT_TYPE = "refresh_token";
    private static final String RESPONSE_TYPE = "cloud_iam";

    private String apiKey;

    /** variable to overwrite the global SDKGlobalConfiguration.IAM_ENDPOINT **/
    private String iamEndpoint = SDKGlobalConfiguration.IAM_ENDPOINT;

    /** The client http setting */
    private HttpClientSettings httpClientSettings;

    /**
     * Default implmentation will use the apiKey to retrieve the Token from the
     * IAM Service
     *
     * @param apiKey
     *            The IBM apiKey
     */
    public DefaultTokenProvider(String apiKey) {
        this.apiKey = apiKey;
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
    }

    /**
     * Apply http Settings when available to match those set on the s3Client.
     * This is needed for proxy host & port config
     *
     * @param httpClientSettings
     */
    public void setHttpClientSettings(HttpClientSettings httpClientSettings) {
        this.httpClientSettings = httpClientSettings;
    }

    /**
     * Retrieve the token using the Apache httpclient in a synchronous manner
     */
    @Override
    public Token retrieveToken() {
        log.debug("DefaultTokenProvider retrieveToken()");
        return retrieveTokenHelper(null);
    }

    /**
     * Retrieve a token using the refresh token instead of the IBM apiKey
     */
    public Token retrieveTokenWithRefresh(String refreshToken) {
        log.debug("DefaultTokenProvider retrieveTokenWithRefresh()");
        return retrieveTokenHelper(refreshToken);
    }

    /**
     * Helper function for retrieving a new token
     *
     * @param refreshToken
     *        Use the passed refresh token instead of the apiKey
     *        If null, the apiKey will be used
     */
    private Token retrieveTokenHelper(String refreshToken) {
        log.debug("DefaultTokenProvider retrieveTokenHelper()");

        try {
            SSLContext sslContext;
            /*
             * If SSL cert checking for endpoints has been explicitly disabled,
             * register a new scheme for HTTPS that won't cause self-signed
             * certs to error out.
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

            HttpClientBuilder builder = HttpClientBuilder.create();
            if (httpClientSettings != null){
                DefaultTokenManager.addProxyConfig(builder, httpClientSettings);
            }

            HttpClient client = builder.setSSLSocketFactory(sslsf).build();

            HttpPost post = new HttpPost(iamEndpoint);
            post.setHeader("Authorization", BASIC_AUTH);
            post.setHeader("Content-Type", CONTENT_TYPE);
            post.setHeader("Accept", ACCEPT);

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("response_type", RESPONSE_TYPE));
            if (refreshToken != null) {
                urlParameters.add(new BasicNameValuePair("grant_type", REFRESH_GRANT_TYPE));
                urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
            } else {
                urlParameters.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
                urlParameters.add(new BasicNameValuePair("apikey", apiKey));
            }

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            final HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != 200) {
                log.info("Response code= " + response.getStatusLine().getStatusCode()
                        + ", Reason= " + response.getStatusLine().getReasonPhrase()
                        + ".Throwing OAuthServiceException");
                OAuthServiceException exception = new OAuthServiceException("Token retrieval from IAM service failed");
                exception.setStatusCode(response.getStatusLine().getStatusCode());
                exception.setStatusMessage(response.getStatusLine().getReasonPhrase());
                throw exception;
            }

            final HttpEntity entity = response.getEntity();
            final String resultStr = EntityUtils.toString(entity);

            final ObjectMapper mapper = new ObjectMapper();

            final Token token = mapper.readValue(resultStr, Token.class);

            if (token == null) {
                throw new OAuthServiceException("Parsing this response mapped to a null Token object with no exceptions thrown: " + response);
            }

            return token;

        } catch (UnsupportedEncodingException e) {
            OAuthServiceException exception = new OAuthServiceException("Received " + e.toString() + " retrieving IAM token (" + e.getCause() + ")");
            exception.setStatusMessage(e.toString());
            throw exception;
        } catch (ClientProtocolException e) {
            OAuthServiceException exception = new OAuthServiceException("Received " + e.toString() + " retrieving IAM token (" + e.getCause() + ")");
            throw exception;
        } catch (IOException e) {
            OAuthServiceException exception = new OAuthServiceException("Received " + e.toString() + " retrieving IAM token (" + e.getCause() + ")");
            exception.setStatusMessage(e.toString());
            throw exception;
        } catch (NoSuchAlgorithmException e) {
            OAuthServiceException exception = new OAuthServiceException("Received " + e.toString() + " retrieving IAM token (" + e.getCause() + ")");
            exception.setStatusMessage(e.toString());
            throw exception;
        } catch (KeyManagementException e) {
            OAuthServiceException exception = new OAuthServiceException("Received " + e.toString() + " retrieving IAM token (" + e.getCause() + ")");
            exception.setStatusMessage(e.toString());
            throw exception;
        }
    }
}
