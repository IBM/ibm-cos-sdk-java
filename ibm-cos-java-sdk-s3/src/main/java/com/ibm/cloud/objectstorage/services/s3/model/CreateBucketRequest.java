/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.s3.model;
//IBM unsupported
//import com.ibm.cloud.objectstorage.services.s3.model.ownership.ObjectOwnership;
import java.io.Serializable;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * Provides options for creating an Amazon S3 bucket.
 *
 * @see DeleteBucketRequest
 * @see CopyObjectRequest
 */
public class CreateBucketRequest extends AmazonWebServiceRequest implements
        Serializable, S3AccelerateUnsupported {

    /** The name of the Amazon S3 bucket to create. */
    private String bucketName;

    /** The IBM Service Instance Id to apply to the bucket */ 
    private String serviceInstanceId; 


    /** The encryptionType of S3 bucket to create. */
    private EncryptionType encryptionType;

    /** The name of the Amazon S3 region in which to create this bucket. */
    @Deprecated
    private String region;

    /**
     * The optional Canned ACL to set for the new bucket. Ignored in favor of
     * accessControlList, if present
     */
    private CannedAccessControlList cannedAcl;

    /**
     * An optional access control list to apply to the new object. If specified,
     * cannedAcl will be ignored.
     */
    private AccessControlList accessControlList;
   
    private boolean objectLockEnabled;

// IBM unsupported
//     private String objectOwnership;

    /**
     * Constructs a new {@link CreateBucketRequest},
     * ready to be executed to create the
     * specified bucket in the <code>US_Standard</code> region.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket to create.
     *
     * @see CreateBucketRequest#CreateBucketRequest(String, Region)
     * @see CreateBucketRequest#CreateBucketRequest(String, String)
     */
    public CreateBucketRequest(String bucketName) {
        this(bucketName, Region.US_Standard);
    }

    /**
     * Constructs a new {@link CreateBucketRequest},
     * ready to be executed to create the
     * specified bucket in the specified region.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket to create.
     * @param region
     *            The region in which to create this bucket. This must match the
     *            region of the endpoint the client is configured against unless
     *            the client is configured against the US Standard endpoint
     *            (s3.amazonaws.com).
     *
     * @see CreateBucketRequest#CreateBucketRequest(String)
     * @see CreateBucketRequest#CreateBucketRequest(String, String)
     */
    public CreateBucketRequest(String bucketName, Region region) {
        this(bucketName, region.toString());
    }

    /**
     * Constructs a new {@link CreateBucketRequest},
     * ready to be executed and create the
     * specified bucket in the specified region.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket to create.
     * @param region
     *            The region in which to create this bucket. This must match the
     *            region of the endpoint the client is configured against unless
     *            the client is configured against the US Standard endpoint
     *            (s3.amazonaws.com).
     *
     * @see CreateBucketRequest#CreateBucketRequest(String)
     * @see CreateBucketRequest#CreateBucketRequest(String, Region)
     */
    public CreateBucketRequest(String bucketName, String region) {
        setBucketName(bucketName);
        setRegion(region);
    }

    /**
     * Sets the name of the Amazon S3 bucket to create.
     *
     * @param bucketName
     *              The name of the Amazon S3 bucket to create.
     *
     * @see CreateBucketRequest#getBucketName()
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Gets the name of the Amazon S3 bucket to create.
     *
     * @return The name of the Amazon S3 bucket to create.
     *
     * @see CreateBucketRequest#setBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Gets the encryptionType of the S3 bucket to create.
     *
     * @return The encryptionType of the S3 bucket to create.
     *
     * @see CreateBucketRequest#setEncryptionType(EncryptionType)
     */
    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    /**
     * Sets the encryptionType bucket to create.
     *
     * @param encryptionType
     *              The encryptionType of the S3 bucket to create.
     *
     * @see CreateBucketRequest#getEncryptionType()
     */
    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public CreateBucketRequest withEncryptionType(EncryptionType encryptionType) {
        setEncryptionType(encryptionType);
        return this;
    }

    /**
     * Sets the name of the Amazon S3 region in which this bucket will be created.
     *
     * @param region
     *            The region in which to create this bucket. This must match the
     *            region of the endpoint the client is configured against unless
     *            the client is configured against the US Standard endpoint
     *            (s3.amazonaws.com).
     *
     * @deprecated Use regional client instead to create bucket in that region.
     *
     * @see CreateBucketRequest#getRegion()
     */
    @Deprecated
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Gets the name of the Amazon S3 region in which this bucket will be created.
     *
     * @return The name of the Amazon S3 region in which this bucket will be created.
     *
     * @see CreateBucketRequest#setRegion(String)
     */
    @Deprecated
    public String getRegion() {
        return region;
    }

    /**
     * Gets the IBM service instance Id
     *
     * @return The IBM Service Instance Id
     */
     public String getServiceInstanceId() {
	 return serviceInstanceId;
     }

    /**
     * Set the IBM service instance Id used on the header for the
     * CreateBucketRequest
     *
     * @param serviceInstanceId
     * 	      IBM service instance Id
     */ 
    public void setServiceInstanceId(String serviceInstanceId) {
	this.serviceInstanceId = serviceInstanceId;
    }

    /**
     * Returns the optional Canned ACL to set for the new bucket.
     *
     * @return The optional Canned ACL to set for the new bucket.
     */
    public CannedAccessControlList getCannedAcl() {
        return cannedAcl;
    }

    /**
     * Sets the optional Canned ACL to set for the new bucket.
     *
     * @param cannedAcl
     *            The optional Canned ACL to set for the new bucket.
     */
    public void setCannedAcl(CannedAccessControlList cannedAcl) {
        this.cannedAcl = cannedAcl;
    }

    /**
     * Sets the optional Canned ACL to set for the new bucket, and returns this
     * updated object so that additional method calls can be chained together.
     *
     * @param cannedAcl
     *            The optional Canned ACL to set for the new bucket.
     *
     * @return This updated object, so that additional method calls can be
     *         chained together.
     */
    public CreateBucketRequest withCannedAcl(CannedAccessControlList cannedAcl) {
        setCannedAcl(cannedAcl);
        return this;
    }

    /**
     * Returns the optional access control list for the new bucket. If
     * specified, cannedAcl will be ignored.
     */
    public AccessControlList getAccessControlList() {
        return accessControlList;
    }

    /**
     * Sets the optional access control list for the new bucket. If specified,
     * cannedAcl will be ignored.
     *
     * @param accessControlList
     *            The access control list for the new bucket.
     */
    public void setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
    }

    /**
     * Sets the optional access control list for the new bucket. If specified,
     * cannedAcl will be ignored. Returns this {@link CreateBucketRequest},
     * enabling additional method calls to be chained together.
     *
     * @param accessControlList
     *            The access control list for the new bucket.
     */
    public CreateBucketRequest withAccessControlList(AccessControlList accessControlList) {
        setAccessControlList(accessControlList);
        return this;
    }
	
	/**
     * Specifies whether you want S3 Object Lock to be enabled for the new bucket.
     */
    public boolean getObjectLockEnabledForBucket() {
        return objectLockEnabled;
    }

    /**
     * Specifies whether you want S3 Object Lock to be enabled for the new bucket.
     */
    public CreateBucketRequest withObjectLockEnabledForBucket(boolean objectLockEnabled) {
        this.objectLockEnabled = objectLockEnabled;
        return this;
    }

    /**
     * Specifies whether you want S3 Object Lock to be enabled for the new bucket.
     */
    public void setObjectLockEnabledForBucket(boolean objectLockEnabled) {
        withObjectLockEnabledForBucket(objectLockEnabled);
    }

    /**
     * Sets the serviceInstanceId on the Request Object.
     * This value will supersede any already set on by CredentialProvider
     *
     * @param serviceInstanceId
     *        The IBM Service Instance Id
     *
     * @return This updated object, so that additional method calls can be
     *         chained together.
     */
    public CreateBucketRequest withServiceInstanceId(String serviceInstanceId) {
        setServiceInstanceId(serviceInstanceId);
        return this;
    }

    /**
     * Returns the optional object ownership for the new bucket.
     */
//IBM unsupported
//    public String getObjectOwnership() {
//         return objectOwnership;
//     }

    /**
     * Sets the optional object ownership for the new bucket. Returns this {@link CreateBucketRequest},
     * enabling additional method calls to be chained together.
     */
//IBM unsupported
//    public CreateBucketRequest withObjectOwnership(String objectOwnership) {
//        setObjectOwnership(objectOwnership);
//        return this;
//    }

    /**
     * Sets the optional object ownership for the new bucket. Returns this {@link CreateBucketRequest},
     * enabling additional method calls to be chained together.
     */
//IBM unsupported
//    public CreateBucketRequest withObjectOwnership(ObjectOwnership objectOwnership) {
//        setObjectOwnership(objectOwnership);
//        return this;
//    }

    /**
     * Sets the optional object ownership for the new bucket.
     */
//IBM unsupported
//    public void setObjectOwnership(String objectOwnership) {
//        this.objectOwnership = objectOwnership;
//    }

    /**
     * Sets the optional object ownership for the new bucket.
     */
//IBM unsupported
//    public void setObjectOwnership(ObjectOwnership objectOwnership) {
//        setObjectOwnership(objectOwnership.toString());
//    }
}
