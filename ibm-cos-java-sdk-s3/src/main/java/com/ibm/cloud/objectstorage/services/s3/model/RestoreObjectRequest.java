/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

import java.io.Serializable;

/**
 * <p>
 * Request object containing all the options for restoring an object, which was transitioned to the Amazon Glacier
 * from S3 when it was expired.
 * </p>
 * <p>
 * All <code>RestoreObjectRequest</code> must specify a bucket name and key,
 * along with expiration time.
 * </p>
 *
 * @see RestoreObjectRequest#RestoreObjectRequest(String, String, int)
 * @see RestoreObjectRequest#RestoreObjectRequest(String, String)
 */
public class RestoreObjectRequest extends AmazonWebServiceRequest implements Serializable, Cloneable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    /**
     * Lifetime of the active copy in days. Do not use with restores that specify OutputLocation.
     */
    private int expirationInDays;

    /**
     * The name of the bucket containing the reference to the object to restore
     * which is now stored in Amazon Glacier.
     *
     * <p>
     * When using this API with an access point, you must direct requests
     * to the access point hostname. The access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * </p>
     * <p>
     * When using this operation using an access point through the Amazon Web Services SDKs, you provide
     * the access point ARN in place of the bucket name. For more information about access point
     * ARNs, see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
     * Using access points</a> in the <i>Amazon Simple Storage Service Developer Guide</i>.
     * </p>
     */
    private String bucketName;

    /**
     * The key, the name of the reference to the object to restore, which is now
     * stored in Amazon Glacier.
     */
    private String key;

    /**
     * Optional version ID specifying which version of the object to restore.
     * If not specified, the most recent version will be restored.
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link AmazonS3#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
     * For more information about enabling lifecycle versioning for a bucket,
     * see {@link AmazonS3#setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest)}.
     */
    private String versionId;

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    /**
     * Glacier related parameters pertaining to this job.
     * <p>This should not be used for restores with a specified OutputLocation.</p>
     */
    private GlacierJobParameters glacierJobParameters;

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * <p>
     * Constructs a new RestoreObjectRequest.
     * </p>
     *
     * <p>
     * When using this API with an access point, you must direct requests
     * to the access point hostname. The access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * </p>
     * <p>
     * When using this operation using an access point through the Amazon Web Services SDKs, you provide
     * the access point ARN in place of the bucket name. For more information about access point
     * ARNs, see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
     * Using access points</a> in the <i>Amazon Simple Storage Service Developer Guide</i>.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket, or access point ARN, containing the reference to the object
     *            to restore which is now stored in Amazon Glacier.
     * @param key
     *            The key, the name of the reference to the object to restore, which
     *            is now stored in Amazon Glacier.
     *
     * @see RestoreObjectRequest#RestoreObjectRequest(String, String, int)
     */
    public RestoreObjectRequest(String bucketName, String key) {
        this(bucketName, key, -1);
    }

    /**
     * <p>
     * Constructs a new RestoreObjectRequest.
     * </p>
     *
     * <p>
     * When using this API with an access point, you must direct requests
     * to the access point hostname. The access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * </p>
     * <p>
     * When using this operation using an access point through the Amazon Web Services SDKs, you provide
     * the access point ARN in place of the bucket name. For more information about access point
     * ARNs, see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
     * Using access points</a> in the <i>Amazon Simple Storage Service Developer Guide</i>.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket, or access point ARN, containing the reference to the object
     *            to restore which is now stored in Amazon Glacier.
     * @param key
     *            The key, the name of the reference to the object to restore, which
     *            is now stored in Amazon Glacier.
     * @param expirationInDays
     *            The time, in days, between when an object is restored to the
     *            bucket and when it expires
     *
     * @see RestoreObjectRequest#RestoreObjectRequest(String, String)
     */
    public RestoreObjectRequest(String bucketName, String key, int expirationInDays) {
        this.bucketName = bucketName;
        this.key = key;
        this.expirationInDays = expirationInDays;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public RestoreObjectRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * Returns the name of the bucket containing the reference to the object to
     * restore which is now stored in Amazon Glacier.
     *
     * @see RestoreObjectRequest#setBucketName(String)
     * @see RestoreObjectRequest#withBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket containing the reference to the object to
     * restore which is now stored in Amazon Glacier, and returns a reference
     * to this object(RestoreObjectRequest) for method chaining.
     *
     * <p>
     * When using this API with an access point, you must direct requests
     * to the access point hostname. The access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * </p>
     * <p>
     * When using this operation using an access point through the Amazon Web Services SDKs, you provide
     * the access point ARN in place of the bucket name. For more information about access point
     * ARNs, see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
     * Using access points</a> in the <i>Amazon Simple Storage Service Developer Guide</i>.
     * </p>
     *
     * @see RestoreObjectRequest#setBucketName(String)
     * @see RestoreObjectRequest#getBucketName()
     */
    public RestoreObjectRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * Sets the name of the bucket containing the reference to the object to
     * restore which is now stored in Amazon Glacier.
     *
     * <p>
     * When using this API with an access point, you must direct requests
     * to the access point hostname. The access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * </p>
     * <p>
     * When using this operation using an access point through the Amazon Web Services SDKs, you provide
     * the access point ARN in place of the bucket name. For more information about access point
     * ARNs, see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-access-points.html\">
     * Using access points</a> in the <i>Amazon Simple Storage Service Developer Guide</i>.
     * </p>
     *
     * @see RestoreObjectRequest#getBucketName()
     * @see RestoreObjectRequest#withBucketName(String)
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Gets the key, the name of the reference to the object to restore, which is
     * now stored in Amazon Glacier.
     *
     * @see RestoreObjectRequest#setKey(String)
     * @see RestoreObjectRequest#withKey(String)
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key, the name of the reference to the object to restore, which
     * is now stored in Amazon Glacier.
     *
     * @see RestoreObjectRequest#getKey()
     * @see RestoreObjectRequest#withKey(String)
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the key, the name of the reference to the object to restore, which
     * is now stored in Amazon Glacier. returns a reference to this object(RestoreObjectRequest)
     * for method chaining.
     *
     * @see RestoreObjectRequest#getKey()
     * @see RestoreObjectRequest#setKey(String)
     */
    public RestoreObjectRequest withKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the id of the version to be restored.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Sets the id of the version to be restored.
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * Sets the id of the version to be restored and returns a reference to
     * this object for method chaining.
     */
    public RestoreObjectRequest withVersionId(String versionId) {
        this.versionId = versionId;
        return this;
    }

    /**
     * Set the lifetime of the active copy in days. Do not use with restores that specify <code>OutputLocation</code>.
     * <p>
     * The Days element is required for regular restores, and must not be provided for select requests.
     */
    public void setExpirationInDays(int expirationInDays) {
        this.expirationInDays = expirationInDays;
    }

    /**
     * Returns the lifetime of the active copy in days. Do not use with restores that specify
     * <code>OutputLocation</code>.
     * <p>
     * The Days element is required for regular restores, and must not be provided for select requests.
     */
    public int getExpirationInDays() {
        return expirationInDays;
    }

    /**
     * Set the lifetime of the active copy in days. Do not use with restores that specify <code>OutputLocation</code>.
     * <p>
     * The Days element is required for regular restores, and must not be provided for select requests.
     */
    public RestoreObjectRequest withExpirationInDays(int expirationInDays) {
        this.expirationInDays = expirationInDays;
        return this;
    }

    /**
     * Returns true if the user has enabled Requester Pays option when
     * conducting this operation from Requester Pays Bucket; else false.
     *
     * <p>
     * If a bucket is enabled for Requester Pays, then any attempt to upload or
     * download an object from it without Requester Pays enabled will result in
     * a 403 error and the bucket owner will be charged for the request.
     *
     * <p>
     * Enabling Requester Pays disables the ability to have anonymous access to
     * this bucket
     *
     * @return true if the user has enabled Requester Pays option for
     * conducting this operation from Requester Pays Bucket.
     */
    public boolean isRequesterPays() {
        return isRequesterPays;
    }

    /**
     * Used for conducting this operation from a Requester Pays Bucket. If
     * set the requester is charged for requests from the bucket.
     *
     * <p>
     * If a bucket is enabled for Requester Pays, then any attempt to upload or
     * download an object from it without Requester Pays enabled will result in
     * a 403 error and the bucket owner will be charged for the request.
     *
     * <p>
     * Enabling Requester Pays disables the ability to have anonymous access to
     * this bucket.
     *
     * @param isRequesterPays
     *            Enable Requester Pays option for the operation.
     */
    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    /**
     * Used for conducting this operation from a Requester Pays Bucket. If
     * set the requester is charged for requests from the bucket. It returns this
     * updated RestoreObjectRequest object so that additional method calls can be
     * chained together.
     *
     * <p>
     * If a bucket is enabled for Requester Pays, then any attempt to upload or
     * download an object from it without Requester Pays enabled will result in
     * a 403 error and the bucket owner will be charged for the request.
     *
     * <p>
     * Enabling Requester Pays disables the ability to have anonymous access to
     * this bucket.
     *
     * @param isRequesterPays
     *            Enable Requester Pays option for the operation.
     *
     * @return The updated RestoreObjectRequest object.
     */
    public RestoreObjectRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }

    /**
     * @return Glacier related parameters pertaining to this job.
     */
    public GlacierJobParameters getGlacierJobParameters() {
        return glacierJobParameters;
    }

    /**
     * Sets Glacier related parameters pertaining to this job.
     *
     * <p>This should not be used for restores with a specified OutputLocation.</p>
     *
     * @param glacierJobParameters New value for Glacier job parameters.
     */
    public void setGlacierJobParameters(GlacierJobParameters glacierJobParameters) {
        this.glacierJobParameters = glacierJobParameters;
    }

    /**
     * Sets Glacier related parameters pertaining to this job.
     *
     * <p>This should not be used for restores with a specified OutputLocation.</p>
     *
     * @param glacierJobParameters New value for Glacier job parameters.
     * @return This object for method chaining.
     */
    public RestoreObjectRequest withGlacierJobParameters(GlacierJobParameters glacierJobParameters) {
        setGlacierJobParameters(glacierJobParameters);
        return this;
    }
}
