package com.amazonaws.services.s3.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CreateBucketRequestTest {

	
	/**
	 * Test Service Instance Id is added to the Request Object 
	 * 
	 */	
	@Test
	public void testServiceInstanceIdIsSetCorrect() {
		
		String serviceInstanceId = "12345";
		
		CreateBucketRequest request = new CreateBucketRequest("testBucket").withServiceInstanceId(serviceInstanceId);
		
		assertEquals(request.getServiceInstanceId(), serviceInstanceId);
	}
}
