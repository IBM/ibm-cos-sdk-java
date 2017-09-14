package com.ibm.oauth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

public class CredentialProviderUtil implements AWSCredentialsProvider{
	
	TokenManager tokenManager;
	  
    public CredentialProviderUtil(TokenProvider tokenProvider) {
        this.tokenManager = new DefaultTokenManager(tokenProvider);
    }
 
    public CredentialProviderUtil(TokenManager tokenManger) {
        this.tokenManager = tokenManger;
    }
 
    @Override
    public AWSCredentials getCredentials() {
        BasicIBMOAuthCredentials oAuthCredentials = new BasicIBMOAuthCredentials(tokenManager);
  
        return oAuthCredentials;
    }
 
    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }
}
