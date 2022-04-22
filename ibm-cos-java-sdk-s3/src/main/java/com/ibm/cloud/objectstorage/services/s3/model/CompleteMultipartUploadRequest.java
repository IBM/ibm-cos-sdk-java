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
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Container for the parameters of the CompleteMultipartUpload operation.
 * <p>
 * If you are performing a complete multipart upload for <a
 * href="http://aws.amazon.com/kms/">KMS</a>-encrypted objects, you need to
 * specify the correct region of the bucket on your client and configure Amazon Web Services
 * Signature Version 4 for added security. For more information on how to do
 * this, see
 * http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingAWSSDK.html#specify
 * -signature-version
 * </p>
 * <p>
 * Required Parameters: BucketName, Key, UploadId, PartETags
 *
 * @see AmazonS3#completeMultipartUpload(CompleteMultipartUploadRequest)
 */
public class CompleteMultipartUploadRequest extends AmazonWebServiceRequest implements Serializable
//, ExpectedBucketOwnerRequest 
{

    /**
     * The name of the bucket containing the multipart upload to complete
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

    /** The key of the multipart upload to complete */
    private String key;

    /** The ID of the multipart upload to complete */
    private String uploadId;

    /** The list of part numbers and ETags to use when completing the multipart upload */
    private List<PartETag> partETags = new ArrayList<PartETag>();

    /**
     * Date on which it will be legal to delete or modify the object.
     * You can only specify this or the Retention-Period header.
     * If both are specified a 400 error will be returned.
     * If neither is specified the bucket's DefaultRetention period will be used.
     * This header should be used to calculate a retention period in seconds and then stored in that manner.
     */
    private Date retentionExpirationDate;
    
    /**
     * A single legal hold to apply to the object.
     * A legal hold is a Y character long string.
     * The object cannot be overwritten or deleted until all legal holds associated with the object are removed.
     */
    private String retentionLegalHoldId;
    
    /**
     * Retention period to store on the object in seconds.
     * If this field and Retention-Expiration-Date are specified a 400 error is returned.
     * If neither is specified the bucket's DefaultRetention period will be used.
     * 0 is a legal value assuming the bucket's minimum retention period is also 0.
     */
    private Long retentionPeriod;

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    //IBM unsupported
    //private String expectedBucketOwner;

    public CompleteMultipartUploadRequest() {}

    /**
     * Constructs a new request to complete a multipart upload.
     *
     * @param bucketName
     *            The name of the bucket containing the multipart upload to
     *            complete.
     * @param key
     *            The key of the multipart upload to complete.
     * @param uploadId
     *            The ID of the multipart upload to complete.
     * @param partETags
     *            The list of part numbers and ETags to use when completing the
     *            multipart upload.
     */
    public CompleteMultipartUploadRequest(String bucketName, String key, String uploadId, List<PartETag> partETags) {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.partETags = partETags;
    }
    
//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public CompleteMultipartUploadRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * Returns the name of the bucket containing the multipart upload to
     * complete.
     *
     * @return The name of the bucket containing the multipart upload to
     *         complete.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket containing the multipart upload to complete.
     *
     * @param bucketName
     *            The name of the bucket containing the multipart upload to
     *            complete.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of the bucket containing the multipart upload to complete,
     * and returns this updated CompleteMultipartUploadRequest so that
     * additional method calls can be chained together.
     *
     * @param bucketName
     *            The name of the bucket containing the multipart upload to
     *            complete.
     *
     * @return The updated CompleteMultipartUploadRequest.
     */
    public CompleteMultipartUploadRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * Returns the key under which the multipart upload to complete is stored.
     *
     * @return The key under which the multipart upload to complete is stored.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key under which the multipart upload to complete is stored.
     *
     * @param key
     *            The key under which the multipart upload to complete is
     *            stored.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the key under which the multipart upload to complete is stored, and
     * returns this updated CompleteMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param key
     *            The key under which the multipart upload to complete is
     *            stored.
     *
     * @return This updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the ID of the multipart upload to complete.
     *
     * @return The ID of the multipart upload to complete.
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * Sets the ID of the multipart upload to complete.
     *
     * @param uploadId
     *            The ID of the multipart upload to complete.
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * Sets the ID of the multipart upload to complete, and returns this updated
     * CompleteMultipartUploadRequest object so that additional method calls can
     * be chained together.
     *
     * @param uploadId
     *            The ID of the multipart upload to complete.
     *
     * @return This updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withUploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    /**
     * Returns the list of part numbers and ETags that identify the individual
     * parts of the multipart upload to complete.
     *
     * @return The list of part numbers and ETags that identify the individual
     *         parts of the multipart upload to complete.
     */
    public List<PartETag> getPartETags() {
        return partETags;
    }

    /**
     * Sets the list of part numbers and ETags that identify the individual
     * parts of the multipart upload to complete.
     *
     * @param partETags
     *            The list of part numbers and ETags that identify the
     *            individual parts of the multipart upload to complete.
     */
    public void setPartETags(List<PartETag> partETags) {
        this.partETags = partETags;
    }

    /**
     * Sets the list of part numbers and ETags that identify the individual
     * parts of the multipart upload to complete, and returns this updated
     * CompleteMultipartUploadRequest object so that additional method calls can be chained.
     *
     * @param partETags
     *            The list of part numbers and ETags that identify the
     *            individual parts of the multipart upload to complete.
     *
     * @return This updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withPartETags(List<PartETag> partETags) {
        setPartETags(partETags);
        return this;
    }

    /**
     * Sets the list of part numbers and ETags that identify the individual
     * parts of the multipart upload to complete based on the specified results
     * from part uploads.
     *
     * @param uploadPartResults
     *            The list of results from the individual part uploads in the
     *            multipart upload to complete.
     *
     * @return This updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withPartETags(UploadPartResult... uploadPartResults) {
        for (UploadPartResult result : uploadPartResults) {
            this.partETags.add(new PartETag(result.getPartNumber(), result.getETag()));
        }
        return this;
    }

    /**
     * Sets the list of part numbers and ETags that identify the individual
     * parts of the multipart upload to complete based on the specified results
     * from part uploads.
     *
     * @param uploadPartResultsCollection
     *            The list of results from the individual part uploads in the
     *            multipart upload to complete.
     *
     * @return This updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withPartETags(Collection<UploadPartResult> uploadPartResultsCollection) {
        for (UploadPartResult result : uploadPartResultsCollection) {
            this.partETags.add(new PartETag(result.getPartNumber(), result.getETag()));
        }
        return this;
    }

    public Date getRetentionExpirationDate() {
        return retentionExpirationDate;
    }

    public void setRetentionExpirationDate(Date retentionExpirationDate) {
        this.retentionExpirationDate = retentionExpirationDate;
    }

    public CompleteMultipartUploadRequest withRetentionExpirationDate(Date retentionExpirationDate) {
        this.retentionExpirationDate = retentionExpirationDate;
        return this;
    }

    public String getRetentionLegalHoldId() {
        return retentionLegalHoldId;
    }

    public void setRetentionLegalHoldId(String retentionLegalHoldId) {
        this.retentionLegalHoldId = retentionLegalHoldId;
    }

    public CompleteMultipartUploadRequest withRetentionLegalHoldId(String retentionLegalHoldId) {
        this.retentionLegalHoldId = retentionLegalHoldId;
        return this;
}

    public Long getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(Long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public CompleteMultipartUploadRequest withRetentionPeriod(Long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
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
     *         conducting this operation from Requester Pays Bucket.
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
     * updated CompleteMultipartUploadRequest object so that additional method calls can be
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
     * @return The updated CompleteMultipartUploadRequest object.
     */
    public CompleteMultipartUploadRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }

}
