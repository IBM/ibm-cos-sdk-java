package com.ibm.cloud.objectstorage.services.s3.model;

/**
 * A set of attributes primarily used to define the encryption type of an s3 request.
 * These values will be applied to specific headers on IO requests. 
 *
 */
public class EncryptionType {
	
	/***The encryption algorithm used in ibm-sse-kms-encryption-algorithm header. Default value assigned*/
	private String kmsEncryptionAlgorithm = "AES256";
	
	/**ibm-sse-kms-customer-root-key-crn header**/
	private String IBMSSEKMSCustomerRootKeyCrn;
	
	/**
	 * The encryption algorithm that will be used for objects stored in the newly created bucket
	 * @return encryption algorithm
	 */
	public String getKmsEncryptionAlgorithm() {
		return kmsEncryptionAlgorithm;
	}
	
	/**
	 * The encryption algorithm that will be used for objects stored in the newly created bucket
	 * @param kmsEncryptionAlgorithm
	 */
	public void setKmsEncryptionAlgorithm(String kmsEncryptionAlgorithm) {
		this.kmsEncryptionAlgorithm = kmsEncryptionAlgorithm;
	}

	/**
	 * return the IBMSSEKMSCustomerRootKeyCrn header value
	 * @return IBMSSEKMSCustomerRootKeyCrn
	 */
	public String getIBMSSEKMSCustomerRootKeyCrn() {
		return IBMSSEKMSCustomerRootKeyCrn;
	}

	/**
	 * Set the IBMSSEKMSCustomerRootKeyCrn. The string must comply with the correct CRN format for the version 
	 * of the api, otherwise the request will be rejected by COS
	 * 
	 * @param iBMSSEKMSCustomerRootKeyCrn
	 */
	public void setIBMSSEKMSCustomerRootKeyCrn(String iBMSSEKMSCustomerRootKeyCrn) {
		IBMSSEKMSCustomerRootKeyCrn = iBMSSEKMSCustomerRootKeyCrn;
	}

}
