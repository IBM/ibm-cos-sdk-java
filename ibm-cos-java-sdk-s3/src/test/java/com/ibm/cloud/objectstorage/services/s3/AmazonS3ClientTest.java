package com.ibm.cloud.objectstorage.services.s3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;
import com.ibm.cloud.objectstorage.services.s3.Headers;
import com.ibm.cloud.objectstorage.services.s3.internal.Constants;
import com.ibm.cloud.objectstorage.services.s3.model.CreateBucketRequest;

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
