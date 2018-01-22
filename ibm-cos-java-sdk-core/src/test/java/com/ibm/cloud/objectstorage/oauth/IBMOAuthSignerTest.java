package com.ibm.cloud.objectstorage.oauth;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.ibm.cloud.objectstorage.SignableRequest;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.oauth.IBMOAuthSigner;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;

public class IBMOAuthSignerTest {
	
	private SignableRequest<?> request;
	
	@Test
	public void shouldAddBearerToken() {
		
		request = MockRequestBuilder.create()
                .withContent(new ByteArrayInputStream("{\"TableName\": \"foo\"}".getBytes()))
                .withHeader("Host", "demo.us-east-1.amazonaws.com")
                .withHeader("x-amz-archive-description", "test  test")
                .withPath("/")
                .withEndpoint("http://demo.us-east-1.amazonaws.com").build();
		
		TokenProvider tokenProvider = new TokenProviderUtil();
		AWSCredentialsProvider testCredProvider = new CredentialProviderUtil(tokenProvider);
				
		IBMOAuthSigner signer = new IBMOAuthSigner();
		
		signer.sign(request, testCredProvider.getCredentials());

		assertEquals("Bearer ProviderAccessToken", request.getHeaders().get("Authorization"));
		
	}
}
