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

import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AbstractAWSSigner;
import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;

import static com.amazonaws.auth.internal.SignerConstants.AUTHORIZATION;

/**
 * Signer implementation that signs requests with the OAuth 2.0 signing protocol.
 * The Bearer token is added to the Authorization header of the request
 */
public class IBMOAuthSigner extends AbstractAWSSigner  {

    protected static final InternalLogApi log = InternalLogFactory.getLog(IBMOAuthSigner.class);

    @Override
    public void sign(SignableRequest<?> request, AWSCredentials credentials) {

    	log.debug("++ OAuth signer");
    	
    	IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)credentials;

    	request.addHeader(
                AUTHORIZATION,"Bearer " + oAuthCreds.getTokenManager().getToken());
    }


	@Override
	protected void addSessionCredentials(SignableRequest<?> request, AWSSessionCredentials credentials) {
		// TODO Auto-generated method stub
		
	}

}
