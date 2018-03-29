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
package com.ibm.cloud.objectstorage.auth;

import java.io.IOException;
import java.io.InputStream;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenManager;

/**
 * {@link AWSCredentialsProvider} implementation that loads AWS security
 * credentials from a json file on the classpath. The default
 * constructor creates a credentials provider that loads the credentials
 * from a file named <code>cos_credentials</code> on the
 * classpath, but which file to use from the classpath can also be controlled
 * through the one-argument constructor.
 * <p>
 * The IBM API key is expected to be in the <code>apiKey</code>
 * property and the IBM resource instance id is expected to be in the
 * <code>resource_instance_id</code> property.
 */
public class ClasspathJsonFileCredentialsProvider implements AWSCredentialsProvider {

    /** The name of the properties file to check for credentials */
    private static String DEFAULT_PROPERTIES_FILE = "cos_credentials";

    private final String credentialsFilePath;
    private TokenManager tokenManager = null;

    /**
     * Creates a new ClasspathJsonFileCredentialsProvider that will
     * attempt to load the <code>cos_credentials</code> file from
     * the classpath to read IBM security credentials.
     */
    public ClasspathJsonFileCredentialsProvider() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    /**
     * Creates a new ClasspathJsonFileCredentialsProvider that will
     * attempt to load a custom file from the classpath to read IBM security
     * credentials.
     *
     * @param credentialsFilePath
     *            The custom classpath resource path to a json file
     *            from which the IBM security credentials should be loaded.
     *
     *            For example,
     *            <ul>
     *              <li>com/mycompany/credentials.json</li>
     *              <li>beta-credentials.json</li>
     *              <li>cos_credentials</li>
     *            </ul>
     */
    public ClasspathJsonFileCredentialsProvider(String credentialsFilePath) {
        if (credentialsFilePath == null)
            throw new IllegalArgumentException("Credentials file path cannot be null");

        // Make sure the path is absolute
        if (!credentialsFilePath.startsWith("/")) {
            this.credentialsFilePath = "/" + credentialsFilePath;
        } else {
            this.credentialsFilePath = credentialsFilePath;
        }
    }

    public AWSCredentials getCredentials() {
        InputStream inputStream = getClass().getResourceAsStream(credentialsFilePath);
        if (inputStream == null) {
            throw new SdkClientException("Unable to load AWS credentials from the " + credentialsFilePath + " file on the classpath");
        }

        try {
            JsonCredentials credentials = new JsonCredentials(inputStream);
        	if(credentials.getApiKey() != null && tokenManager == null){
        		tokenManager = new DefaultTokenManager(credentials.getApiKey());

        	}
    		credentials.setTokenManager(tokenManager);
        	
            return credentials;
        } catch (IOException e) {
            throw new SdkClientException("Unable to load IBM credentials from the " + credentialsFilePath + " file on the classpath", e);
        }
    }

    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + credentialsFilePath + ")";
    }
}