package com.ibm.cloud.objectstorage.services.s3.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ibm.cloud.objectstorage.services.s3.model.CreateBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.EncryptionType;

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

	/**
	 * Test the IBM_SSE_KMS_ENABLED & IBM_SSE_KMS_CRK are set in the ObjectLIsting
	 * response object
	 * 
	 */	
	@Test
	public void testEncryptionTypeIsSetCorrect() {
		
		String rootKeyCrn = "RootKeyCrn";
		String algorithm = "AES256";
		
		EncryptionType encryptionType = new EncryptionType();
		encryptionType.setIBMSSEKMSCustomerRootKeyCrn(rootKeyCrn);
		encryptionType.setKmsEncryptionAlgorithm(algorithm);
		CreateBucketRequest request = new CreateBucketRequest("testBucket").withEncryptionType(encryptionType);
		
		assertEquals(request.getEncryptionType().getIBMSSEKMSCustomerRootKeyCrn(), rootKeyCrn);
		assertEquals(request.getEncryptionType().getKmsEncryptionAlgorithm(), algorithm);
	}
}
