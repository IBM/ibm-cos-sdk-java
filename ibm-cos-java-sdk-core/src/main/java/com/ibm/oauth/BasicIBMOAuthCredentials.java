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

import com.ibm.oauth.DefaultTokenManager;
import com.ibm.oauth.TokenManager;
import com.ibm.oauth.TokenProvider;

/**
 * Basic implementation of the IBMOAuthCredentials interface that allows callers to
 * pass in the IBM api key and service instance id in the constructor.
 */
public class BasicIBMOAuthCredentials implements IBMOAuthCredentials {

	private TokenManager tokenManager;
	private String apiKey;
	private String serviceInstanceId;
    
	/**
     * Constructs a new BasicIBMOAuthCredentials object, with the specified IBM
     * api key and service instance id.
     *
     * @param apiKey
     *            The API key.
     * @param serviceInstanceId
     *            The service instance id.
     */
    public BasicIBMOAuthCredentials(String apiKey, String serviceInstanceId) {

    	this.tokenManager = new DefaultTokenManager(apiKey);
    	this.apiKey = apiKey;
    	this.serviceInstanceId = serviceInstanceId;
    }
    
    /**
     * Constructor using a custom TokenManager
     * 
     * @param tokenManager
     * 			An instance of Token Manager
     */
    public BasicIBMOAuthCredentials(TokenManager tokenManager) {

    	this.tokenManager = tokenManager;
    }
    
    /**
     * Constructor using a custom TokenManager
     * 
     * @param tokenManager
     * 			An instance of Token Manager
     * @param serviceInstanceId
     * 			serviceInstanceId used for create & list buckets requests
     */
    public BasicIBMOAuthCredentials(TokenManager tokenManager, String serviceInstanceId) {

    	this.tokenManager = tokenManager;
    	this.serviceInstanceId = serviceInstanceId;
    }
    
    /**
     * Constructor using a custom Token Provider, this implementation will
     * use the DefaultTokenManager as the Token Manager.
     * 
     * @param tokenProvider
     * 			An instance of TokenProvider
     */
    public BasicIBMOAuthCredentials(TokenProvider tokenProvider) {
    	this.tokenManager = new DefaultTokenManager(tokenProvider);
    }

	@Override
	public String getAWSAccessKeyId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAWSSecretKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenManager getTokenManager() {
		
		return tokenManager;
	}

	@Override
	public String getApiKey() {
		
		return this.apiKey;
	}

	@Override
	public String getServiceInstanceId() {
		
		return this.serviceInstanceId;
	}

}
