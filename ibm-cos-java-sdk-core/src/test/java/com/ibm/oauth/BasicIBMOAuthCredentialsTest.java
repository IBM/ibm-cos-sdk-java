package com.ibm.oauth;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BasicIBMOAuthCredentialsTest {
	
	@Test
	public void constructorShouldAcceptStrings() {
	
		String apiKey = "MyNewApiKey";
		String serviceInstance = "1234567890";
		IBMOAuthCredentials oAuthCreds = new BasicIBMOAuthCredentials(apiKey, serviceInstance);
		
		assertTrue(oAuthCreds.getApiKey().equals(apiKey));
		assertTrue(oAuthCreds.getServiceInstanceId().equals(serviceInstance));
	}
	
	@Test
	public void constructorShouldAcceptTokenManager() {
	
		TokenManager tokenManger = new TokenManagerUtil();
		IBMOAuthCredentials oAuthCreds = new BasicIBMOAuthCredentials(tokenManger);
		
		assertTrue(oAuthCreds.getTokenManager().getToken().equals("TokenManagerAccessToken"));
	}
	
	@Test
	public void constructorShouldAcceptTokenProvider() {
	
		TokenProvider tokenProvider = new TokenProviderUtil();
		IBMOAuthCredentials oAuthCreds = new BasicIBMOAuthCredentials(tokenProvider);
		
		assertTrue(oAuthCreds.getTokenManager().getToken().equals("ProviderAccessToken"));
	}

}
