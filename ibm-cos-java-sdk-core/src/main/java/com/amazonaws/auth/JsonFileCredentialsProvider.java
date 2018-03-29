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
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.JsonCredentials;
import com.ibm.oauth.DefaultTokenManager;
import com.ibm.oauth.TokenManager;

import java.io.File;
import java.io.IOException;

/**
 * {@link AWSCredentialsProvider} implementation that loads IBM security
 * credentials from a json file provided on initialization.
 * <p>
 * The IBM API key is expected to be in the <code>apiKey</code>
 * property and the IBM resource instance id is expected to be in the
 * <code>resource_instance_id</code> property.
 */
public class JsonFileCredentialsProvider implements
        AWSCredentialsProvider {

    private final String credentialsFilePath;
    private TokenManager tokenManager = null;

    /**
     * Creates a new PropertiesFileCredentialsProvider that will attempt to load
     * a custom file from the path specified to read IBM security credentials.
     * 
     * @param credentialsFilePath
     *            The custom classpath resource path to a json file from
     *            which the IBM security credentials should be loaded.
     * 
     *            For example,
     *            <ul>
     *            <li>/etc/somewhere/cos_credentials</li>
     *            </ul>
     */
    public JsonFileCredentialsProvider(String credentialsFilePath) {
        if (credentialsFilePath == null)
            throw new IllegalArgumentException(
                    "Credentials file path cannot be null");
        this.credentialsFilePath = credentialsFilePath;
    }

    public AWSCredentials getCredentials() {
        try {

        	JsonCredentials credentials = new JsonCredentials(new File(this.credentialsFilePath));
        	if(credentials.getApiKey() != null && tokenManager == null){
        		tokenManager = new DefaultTokenManager(credentials.getApiKey());

        	}
    		credentials.setTokenManager(tokenManager);
        	
            return credentials;
            

        	
        } catch (IOException e) {
            throw new SdkClientException(
                    "Unable to load IBM credentials from the "
                            + credentialsFilePath + " file", e);
        }
    }

    public void refresh() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + credentialsFilePath + ")";
    }
}