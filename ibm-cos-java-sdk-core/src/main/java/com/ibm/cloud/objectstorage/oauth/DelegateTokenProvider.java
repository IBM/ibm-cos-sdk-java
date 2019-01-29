/*
* Copyright 2018 IBM Corp. All Rights Reserved.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ApacheConnectionManagerFactory.TrustingX509TrustManager;
import com.ibm.cloud.objectstorage.http.conn.ssl.SdkTLSSocketFactory;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.ibm.cloud.objectstorage.oauth.OAuthServiceException;
import com.ibm.cloud.objectstorage.oauth.Token;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;

/**
 * This implementation will retrieve a delegate refresh token from the
 * Token service to be used by the Aspera service when completing its
 * own transfer process to S3.  
 * 
 */

public class DelegateTokenProvider implements TokenProvider {

	protected static final InternalLogApi log = InternalLogFactory.getLog(DelegateTokenProvider.class);

	// Http paramaters
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey";
	private static final String RESPONSE_TYPE = "delegated_refresh_token";
	
	// Delegate token timeout
	private static final String TOKEN_TIMEOUT = "518400";

	/** apiKey to retrieve a delegate token **/
	private String apiKey;

	/** IAM Endpoint to retrieve a token **/
	private String iamEndpoint = SDKGlobalConfiguration.IAM_ENDPOINT;

	/** The client http setting */
	private HttpClientSettings httpClientSettings;
	
	/** The Client Id for the delegated token **/
	private String receiverClientId = "aspera_ats";

	/**
	 * Default implementation will use the apiKey to retrieve the Token from the
	 * IAM Service
	 * 
	 * @param apiKey
	 *            The IBM apiKey
	 */
	public DelegateTokenProvider(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Over write the default IAM endpoint. This should only be done in a
	 * development or staging environment
	 * 
	 * @param iamEndpoint
	 *            The http endpoint to retrieve the token
	 */
	private void setIamEndpoint(String iamEndpoint) {
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
	 * Over write the default receiverClientId
	 * 
	 * @param receiverClientId
	 *            The Client the delegated token is for
	 */
	private void setReceiverClientId(String receiverClientId) {
		this.receiverClientId = receiverClientId;
	}
	
	public DelegateTokenProvider withIamEndpoint(String iamEndpoint) {
		setIamEndpoint(iamEndpoint);
		return this;
	}
	
	public DelegateTokenProvider withReceiverClientId(String receiverClientId) {
		setReceiverClientId(receiverClientId);
		return this;
	}

	/**
	 * Retrieve the delegate token using the Apache httpclient in a synchronous manner
	 */
	@Override
	public Token retrieveToken() {

		log.debug("AsperaDelegateTokenProvider retrieveToken()");

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
			post.setHeader("Content-Type", CONTENT_TYPE);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
			urlParameters.add(new BasicNameValuePair("response_type", RESPONSE_TYPE));
			urlParameters.add(new BasicNameValuePair("receiver_client_ids", receiverClientId));
			urlParameters.add(new BasicNameValuePair("apikey", apiKey));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			final HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("Response code= " + response.getStatusLine().getStatusCode() + ", Reason= "
						+ response.getStatusLine().getReasonPhrase() + ".Throwing OAuthServiceException");
				OAuthServiceException exception = new OAuthServiceException(
						"Token retrival from IAM service failed token");
				exception.setStatusCode(response.getStatusLine().getStatusCode());
				exception.setStatusMessage(response.getStatusLine().getReasonPhrase());
				throw exception;
			}

			final HttpEntity entity = response.getEntity();
			final String resultStr = EntityUtils.toString(entity);

			final ObjectMapper mapper = new ObjectMapper();

			final Token token = mapper.readValue(resultStr, Token.class);
			// 6days = 518,400 seconds
			token.setExpires_in(TOKEN_TIMEOUT);
			token.setExpiration(Long.toString((System.currentTimeMillis()/1000) + 518400L));

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

