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

import static com.ibm.cloud.objectstorage.auth.internal.SignerConstants.AUTHORIZATION;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SignableRequest;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSSessionCredentials;
import com.ibm.cloud.objectstorage.auth.AbstractAWSSigner;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;

/**
 * Signer implementation that signs requests with the OAuth 2.0 signing protocol.
 * The Bearer token is added to the Authorization header of the request
 */
public class IBMOAuthSigner extends AbstractAWSSigner  {

    protected static final InternalLogApi log = InternalLogFactory.getLog(IBMOAuthSigner.class);
    
    /** The client configuration */
    private ClientConfiguration clientConfiguration;

    /**
     * Public constructor to accept clientconfiguration parameter, which is then passed 
     * through to DefaultTokenManager and used for proxy config on IAM calls for token retrieval
     *   
     * @param clientConfiguration
     * 			This config is used in IAM token retrieval to determine if request should go through a proxy
     */
    public IBMOAuthSigner (ClientConfiguration clientConfiguration) {
    	this.clientConfiguration = clientConfiguration;
    }

    /**
     * Default public constructor
     */
    public IBMOAuthSigner () {

    }

    @Override
    public void sign(SignableRequest<?> request, AWSCredentials credentials) {

    	log.debug("++ OAuth signer");
    	
    	IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)credentials;
    	if (oAuthCreds.getTokenManager() instanceof DefaultTokenManager) {
    		DefaultTokenManager tokenManager = (DefaultTokenManager)oAuthCreds.getTokenManager();
    		tokenManager.setClientConfiguration(clientConfiguration);
        	request.addHeader(
                    AUTHORIZATION,"Bearer " + tokenManager.getToken());
    	} else {
        	request.addHeader(
                    AUTHORIZATION,"Bearer " + oAuthCreds.getTokenManager().getToken());
    	}

    }


	@Override
	protected void addSessionCredentials(SignableRequest<?> request, AWSSessionCredentials credentials) {
		// TODO Auto-generated method stub
		
	}

}
