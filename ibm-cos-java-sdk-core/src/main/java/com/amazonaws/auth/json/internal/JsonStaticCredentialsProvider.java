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
package com.amazonaws.auth.json.internal;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.JsonCredentials;
import com.amazonaws.auth.JsonCredentials;
import com.ibm.oauth.DefaultTokenManager;
import com.ibm.oauth.TokenManager;

/**
 * Serves credentials defined in a {@link BasicJsonCredentials}. Does validation that both access key and
 * secret key exists and are non empty.
 */
@SdkInternalApi
@Immutable
public class JsonStaticCredentialsProvider implements AWSCredentialsProvider {

    private final AWSCredentialsProvider credentialsProvider;
    private TokenManager tokenManager = null;

    // HMAC takes precedence over IAM if both provided.
    public JsonStaticCredentialsProvider(JsonCredentials credentials) {
    	if((credentials.getAWSAccessKeyId() == null || credentials.getAWSSecretKey() == null)
    		&& (credentials.getApiKey() != null && tokenManager == null)){
    		tokenManager = new DefaultTokenManager(credentials.getApiKey());

    	}
		credentials.setTokenManager(tokenManager);
        this.credentialsProvider = new AWSStaticCredentialsProvider(credentials);
    }

    @Override
    public AWSCredentials getCredentials() {
        return credentialsProvider.getCredentials();
    }

    @Override
    public void refresh() {
        // No Op
    }
}
