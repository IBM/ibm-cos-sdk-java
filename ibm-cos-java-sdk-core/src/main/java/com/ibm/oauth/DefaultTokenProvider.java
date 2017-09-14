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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import com.ibm.oauth.OAuthServiceException;
import com.ibm.oauth.Token;
import com.ibm.oauth.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation to retrieve token from the IAM service using 
 * the api key
 *
 */
public class DefaultTokenProvider implements TokenProvider {

	protected static final InternalLogApi log = InternalLogFactory.getLog(DefaultTokenProvider.class);

	//Http paramaters	
	private static final String BASIC_AUTH = "Basic Yng6Yng=";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String ACCEPT = "application/json";
	
	private static final String GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey";
	private static final String RESPONSE_TYPE = "cloud_iam";
	
	private String apiKey;
	
	/**
	 * Default implmentation will use the apiKey to retrieve the Token from the
	 * IAM Service
	 * 
	 * @param apiKey
	 * 			The IBM apiKey 
	 */
	public DefaultTokenProvider(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Retrieve the token using the Apache httpclient in a synchronous manner
	 */
	@Override
	public Token retrieveToken() {

		log.debug("DefaultTokenProvider retrieveToken()");

		try {
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(SDKGlobalConfiguration.IAM_ENDPOINT);
			post.setHeader("Authorization", BASIC_AUTH);
			post.setHeader("Content-Type", CONTENT_TYPE);
			post.setHeader("Accept", ACCEPT);
			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
			urlParameters.add(new BasicNameValuePair("response_type", RESPONSE_TYPE));
			urlParameters.add(new BasicNameValuePair("apikey", apiKey));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			final HttpResponse response = client.execute(post);

			if(response.getStatusLine().getStatusCode() != 200) {
				log.debug("Repsonse code = " + response.getStatusLine().getStatusCode() + ", throwing OAuthServiceException");
				throw new OAuthServiceException("Token retrival from IAM service failed with api key");
			}

			final HttpEntity entity = response.getEntity();
	        final String resultStr = EntityUtils.toString(entity);
	
	        final ObjectMapper mapper = new ObjectMapper();
	
	        final Token token = mapper.readValue(resultStr, Token.class);
	        
	        return token;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

}
