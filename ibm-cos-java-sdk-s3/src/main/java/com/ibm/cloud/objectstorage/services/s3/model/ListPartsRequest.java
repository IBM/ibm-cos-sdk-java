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
import java.io.Serializable;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Container for the the parameters of the ListParts operation.
 * <p>
 * Required Parameters: BucketName, Key, UploadId
 *
 * @see AmazonS3#listParts(ListPartsRequest)
 */
public class ListPartsRequest extends AmazonWebServiceRequest implements 
        WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest 
        {

    /**
     * The name of the bucket containing the multipart upload whose parts are
     * being listed.
     *
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The
     * access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this action
     * with an access point through the Amazon Web Services SDKs, you provide the access point ARN in place of the
     * bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a>
     * in the <i>Amazon S3 User Guide</i>.
     * </p>
     * <p>
     * When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts hostname. The
     * S3 on Outposts hostname takes the form
     * <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     * When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     * access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a> in the
     * <i>Amazon S3 User Guide</i>.
     * </p>
     */
    private String bucketName;

    /** The key of the associated multipart upload whose parts are being listed. */
    private String key;

    /** The ID of the multipart upload whose parts are being listed. */
    private String uploadId;

    /** The optional maximum number of parts to be returned in the part listing. */
    private Integer maxParts;

    /** The optional part number marker indicating where in the results to being listing parts. */
    private Integer partNumberMarker;

    /**
     * Optional parameter indicating the encoding method to be applied on the
     * response. An object key can contain any Unicode character; however, XML
     * 1.0 parser cannot parse some characters, such as characters with an ASCII
     * value from 0 to 10. For characters that are not supported in XML 1.0, you
     * can add this parameter to request that Amazon S3 encode the keys in the
     * response.
     */
    private String encodingType;

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    // IBM-specific
    /**
     * Optional parameter setting the mirror-destination on a WORM enabled bucket.
     */
    private String wormMirrorDestination;
	
    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Constructs a new ListPartsRequest from the required parameters bucket
     * name, key and upload ID.
     *
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The
     * access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this action
     * with an access point through the Amazon Web Services SDKs, you provide the access point ARN in place of the
     * bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a>
     * in the <i>Amazon S3 User Guide</i>.
     * </p>
     * <p>
     * When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts hostname. The
     * S3 on Outposts hostname takes the form
     * <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     * When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     * access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a> in the
     * <i>Amazon S3 User Guide</i>.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket, or access point ARN, containing the parts to list.
     *            <p>
     *            When using this action with an access point, you must direct requests to the access point hostname.
     *            The access point hostname takes the form
     *            <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *            action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *            place of the bucket name. For more information about access point ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *            points</a> in the <i>Amazon S3 User Guide</i>.
     *            </p>
     *            <p>
     *            When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *            hostname. The S3 on Outposts hostname takes the form
     *            <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *            When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *            access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *            in the <i>Amazon S3 User Guide</i>.
     * @param key
     *            The key of the associated multipart upload whose parts are
     *            being listed.
     * @param uploadId
     *            The ID of the multipart upload whose parts are being listed.
     */
    public ListPartsRequest(String bucketName, String key, String uploadId) {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public ListPartsRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * Returns the name of the bucket containing the multipart upload whose
     * parts are being listed.
     *
     * @return The name of the bucket containing the multipart upload whose
     *         parts are being listed.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket containing the multipart upload whose parts
     * are being listed.
     *
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The
     * access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this action
     * with an access point through the Amazon Web Services SDKs, you provide the access point ARN in place of the
     * bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a>
     * in the <i>Amazon S3 User Guide</i>.
     * </p>
     * <p>
     * When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts hostname. The
     * S3 on Outposts hostname takes the form
     * <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     * When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     * access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a> in the
     * <i>Amazon S3 User Guide</i>.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket, or access point ARN, containing the multipart upload whose
     *            parts are being listed.
     *            <p>
     *            When using this action with an access point, you must direct requests to the access point hostname.
     *            The access point hostname takes the form
     *            <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *            action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *            place of the bucket name. For more information about access point ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *            points</a> in the <i>Amazon S3 User Guide</i>.
     *            </p>
     *            <p>
     *            When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *            hostname. The S3 on Outposts hostname takes the form
     *            <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *            When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *            access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *            in the <i>Amazon S3 User Guide</i>.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the BucketName property for this request.
     *
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The
     * access point hostname takes the form
     * <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this action
     * with an access point through the Amazon Web Services SDKs, you provide the access point ARN in place of the
     * bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a>
     * in the <i>Amazon S3 User Guide</i>.
     * </p>
     * <p>
     * When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts hostname. The
     * S3 on Outposts hostname takes the form
     * <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     * When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     * access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a> in the
     * <i>Amazon S3 User Guide</i>.
     * </p>
     *
     * @param bucketName
     *            The value that BucketName is set to
     *            <p>
     *            When using this action with an access point, you must direct requests to the access point hostname.
     *            The access point hostname takes the form
     *            <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *            action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *            place of the bucket name. For more information about access point ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *            points</a> in the <i>Amazon S3 User Guide</i>.
     *            </p>
     *            <p>
     *            When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *            hostname. The S3 on Outposts hostname takes the form
     *            <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *            When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *            access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *            href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *            in the <i>Amazon S3 User Guide</i>.
     * @return the request with the BucketName set
     */
    public ListPartsRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * Returns the key of the associated multipart upload whose parts are being
     * listed.
     *
     * @return The key of the associated multipart upload whose parts are being
     *         listed.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the associated multipart upload whose parts are being
     * listed.
     *
     * @param key
     *            The key of the associated multipart upload whose parts are
     *            being listed.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the key of the associated multipart upload whose parts are being
     * listed, and returns this updated ListPartsRequest object so that
     * additional method calls can be chained together.
     *
     * @param key
     *            The key of the associated multipart upload whose parts are
     *            being listed.
     *
     * @return This updated ListPartsRequest object.
     */
    public ListPartsRequest withKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the ID of the multipart upload whose parts are being listed.
     *
     * @return The ID of the multipart upload whose parts are being listed.
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * Sets the ID of the multipart upload whose parts are being listed.
     *
     * @param uploadId
     *            The ID of the multipart upload whose parts are being listed.
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * Sets the ID of the multipart upload whose parts are being listed, and
     * returns this updated ListPartsRequest object so that additional method
     * calls can be chained together.
     *
     * @param uploadId
     *            The ID of the multipart upload whose parts are being listed.
     *
     * @return This updated ListPartsRequest object.
     */
    public ListPartsRequest withUploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    /**
     * Returns the optional maximum number of parts to be returned in the part
     * listing.
     *
     * @return The optional maximum number of parts to be returned in the part
     *         listing.
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * Sets the optional maximum number of parts to be returned in the part
     * listing.
     *
     * @param maxParts
     *            The optional maximum number of parts to be returned in the
     *            part listing.
     */
    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * Sets the optional maximum number of parts to be returned in the part
     * listing and returns this updated ListPartsRequest objects so that
     * additional method calls can be chained together.
     *
     * @param maxParts
     *            The optional maximum number of parts to be returned in the
     *            part listing.
     *
     * @return This updated ListPartsRequest object.
     */
    public ListPartsRequest withMaxParts(int maxParts) {
        this.maxParts = maxParts;
        return this;
    }

    /**
     * Returns the optional part number marker indicating where in the results
     * to being listing parts.
     *
     * @return The optional part number marker indicating where in the results
     *         to being listing parts.
     */
    public Integer getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * Sets the optional part number marker indicating where in the results to
     * being listing parts.
     *
     * @param partNumberMarker
     *            The optional part number marker indicating where in the
     *            results to being listing parts.
     */
    public void setPartNumberMarker(Integer partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
    }

    /**
     * Sets the optional part number marker indicating where in the results to
     * being listing parts, and returns this updated ListPartsRequest object so
     * that additional method calls can be chained together.
     *
     * @param partNumberMarker
     *            The optional part number marker indicating where in the
     *            results to being listing parts.
     *
     * @return This updated ListPartsRequest object.
     */
    public ListPartsRequest withPartNumberMarker(Integer partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
        return this;
    }

    /**
     * Gets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response.
     *
     * @return The encoding method to be applied on the response.
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * Sets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response. An object key can contain
     * any Unicode character; however, XML 1.0 parser cannot parse some
     * characters, such as characters with an ASCII value from 0 to 10. For
     * characters that are not supported in XML 1.0, you can add this parameter
     * to request that Amazon S3 encode the keys in the response.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * Sets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response. An object key can contain
     * any Unicode character; however, XML 1.0 parser cannot parse some
     * characters, such as characters with an ASCII value from 0 to 10. For
     * characters that are not supported in XML 1.0, you can add this parameter
     * to request that Amazon S3 encode the keys in the response.
     * Returns this {@link ListPartsRequest}, enabling additional method calls
     * to be chained together.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public ListPartsRequest withEncodingType(String encodingType) {
        setEncodingType(encodingType);
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
     * updated ListPartsRequest object so that additional method calls can be
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
     * @return The updated ListPartsRequest object.
     */
    public ListPartsRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }

    // IBM-specific
    /**
     * Returns the optional mirror-destination value for WORM mirroring
     *
     * @return The optional mirror-destination value
     */
    @Override
    public String getWormMirrorDestination() {
        return wormMirrorDestination;
    }

    // IBM-specific
    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * 
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     */
    @Override
    public void setWormMirrorDestination(String wormMirrorDestination) {
        this.wormMirrorDestination = wormMirrorDestination;
    }

    // IBM-specific
    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * and returns the updated ListPartsRequest so that additional 
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link ListPartsRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListPartsRequest withWormMirrorDestination(
            String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }

}
