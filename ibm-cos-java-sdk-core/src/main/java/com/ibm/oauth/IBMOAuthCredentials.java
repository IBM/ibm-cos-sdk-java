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

import com.amazonaws.auth.AWSCredentials;
import com.ibm.oauth.TokenManager;

/**
 * Provides access to the AWS OAuth credentials used for accessing IBM services: IBM
 * api key & service instance id. These credentials are used to securely obtain an 
 * access token & sign requests to IBM COS services.
 * <p>
 * A basic implementation of this interface is provided in
 * {@link BasicIBMOAuthCredentials}, but callers are free to provide their own
 * implementation, for example, to load AWS credentials from an encrypted file.
 * <p>
 */
public interface IBMOAuthCredentials extends AWSCredentials {

    /**
     * Returns the IBM API Key for this credentials object. 
     * 
     * @return String 
     * 			The IBM API Key for this credentials object. 
     */
    public String getApiKey();

    /**
     * Returns the IBM Service Instance Id for this credentials object.
     * 
     * @return String
     * 			The IBM Service Instance Id for this credentials object.
     */
    public String getServiceInstanceId();

    /**
     * Returns the IAM TokenManager.
     * 
     * @return token
     * 			The TokenManager implementation
     */
    public TokenManager getTokenManager();

}
