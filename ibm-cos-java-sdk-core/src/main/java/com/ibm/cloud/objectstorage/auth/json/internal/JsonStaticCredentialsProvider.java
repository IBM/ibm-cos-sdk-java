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
package com.ibm.cloud.objectstorage.auth.json.internal;

import com.ibm.cloud.objectstorage.annotation.Immutable;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.JsonCredentials;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenManager;

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
