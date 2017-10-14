package com.amazonaws.services.s3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.ibm.oauth.BasicIBMOAuthCredentials;
import com.ibm.oauth.TokenManager;

@SuppressWarnings("deprecation")
public class AmazonS3ClientTest {

	
	/**
	 * Test Service Instance Id is added to the Request Object and
	 * the runtime parameter takes precedence over any set by the
	 * CredentialProvider
	 * 
	 */	
	@Test
	public void testServiceInstanceRuntimeParamTakesPrecedence() {
		
		String serviceInstanceId = "12345";
		
		CreateBucketRequest request = new CreateBucketRequest("testbucket").withServiceInstanceId(serviceInstanceId);
		Request<CreateBucketRequest> defaultRequest = new DefaultRequest(Constants.S3_SERVICE_DISPLAY_NAME);
		AmazonS3Client s3Client = new AmazonS3Client(new BasicIBMOAuthCredentials(new TokenMangerUtilTest(), "54321"));
		defaultRequest = s3Client.addIAMHeaders(defaultRequest, request);
		
		assertEquals(defaultRequest.getHeaders().get(Headers.IBM_SERVICE_INSTANCE_ID), serviceInstanceId);
	}
	
	/**
	 * Test Service Instance Id is added to the Request Object by 
	 * the CredentialProvider
	 * 
	 */	
	@Test
	public void testServiceInstanceHeaderIsAddedByCredentialProvdier() {
		
		String serviceInstanceId = "12345";
		
		CreateBucketRequest request = new CreateBucketRequest("testbucket");
		Request<CreateBucketRequest> defaultRequest = new DefaultRequest(Constants.S3_SERVICE_DISPLAY_NAME);
		AmazonS3Client s3Client = new AmazonS3Client(new BasicIBMOAuthCredentials(new TokenMangerUtilTest(), serviceInstanceId));
		defaultRequest = s3Client.addIAMHeaders(defaultRequest, request);
		
		assertEquals(defaultRequest.getHeaders().get(Headers.IBM_SERVICE_INSTANCE_ID), serviceInstanceId);
	}
	
	/**
	 * Test No IAM Headers are added & no null pointers are thrown
	 * 
	 */	
	@Test
	public void testNoIAMHeadersAreAdded() {
				
		CreateBucketRequest request = new CreateBucketRequest("testbucket");
		Request<CreateBucketRequest> defaultRequest = new DefaultRequest(Constants.S3_SERVICE_DISPLAY_NAME);
		AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("987654321", "123456789"));
		defaultRequest = s3Client.addIAMHeaders(defaultRequest, request);
		
		assertEquals(defaultRequest.getHeaders().get(Headers.IBM_SERVICE_INSTANCE_ID), null);
	}
	
	/**
	 * TokenMangerUtilTest Util class to provide a runtime implementation of TokenManger
	 * for IAM tests
	 *
	 */
	public class TokenMangerUtilTest implements TokenManager {

		@Override
		public String getToken() {

			return "newToken";
		}
		
	}
}
