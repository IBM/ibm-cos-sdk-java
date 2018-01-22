package com.ibm.cloud.objectstorage.oauth;

import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;

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
