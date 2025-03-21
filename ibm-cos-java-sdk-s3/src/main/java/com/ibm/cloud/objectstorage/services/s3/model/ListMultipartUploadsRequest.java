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
 * Container for the parameters of the ListMultipartUploads operation.
 * <p>
 * Required Parameters: BucketName
 *
 * @see AmazonS3#listMultipartUploads(ListMultipartUploadsRequest)
 */
public class ListMultipartUploadsRequest extends AmazonWebServiceRequest implements
        WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest
        {

    /**
     * The name of the bucket containing the uploads to list.
     *
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

	/**
	 * Optional parameter that causes multipart uploads for keys that contain
	 * the same string between the prefix and the first occurrence of the
	 * delimiter to be rolled up into a single result element in the
	 * {@link MultipartUploadListing#getCommonPrefixes()} list. These rolled-up
	 * keys are not returned elsewhere in the response. The most commonly used
	 * delimiter is "/", which simulates a hierarchical organization similar to
	 * a file system directory structure.
	 */
    private String delimiter;

	/**
	 * Optional parameter restricting the response to multipart uploads for keys
	 * which begin with the specified prefix. You can use prefixes to separate a
	 * bucket into different sets of keys in a way similar to how a file system
	 * uses folders.
	 */
    private String prefix;

    /** The optional maximum number of uploads to return. */
    private Integer maxUploads;

    /**
     * The optional key marker indicating where in the results to begin listing.
     * <p>
     * Together with the upload ID marker, specifies the multipart upload after
     * which listing should begin.
     * <p>
     * If the upload ID marker is not specified, only the keys lexicographically
     * greater than the specified key-marker will be included in the list.
     * <p>
     * If the upload ID marker is specified, any multipart uploads for a key
     * equal to the key-marker may also be included, provided those multipart
     * uploads have upload IDs lexicographically greater than the specified
     * marker.
     */
    private String keyMarker;

    /**
     * The optional upload ID marker indicating where in the results to begin
     * listing.
     * <p>
     * Together with the key marker, specifies the multipart upload after which
     * listing should begin. If no key marker is specified, the upload ID marker
     * is ignored. Otherwise, any multipart uploads for a key equal to the key
     * marker may be included in the list only if they have an upload ID
     * lexicographically greater than the specified marker.
     */
    private String uploadIdMarker;

    /**
     * Optional parameter indicating the encoding method to be applied on the
     * response. An object key can contain any Unicode character; however, XML
     * 1.0 parser cannot parse some characters, such as characters with an ASCII
     * value from 0 to 10. For characters that are not supported in XML 1.0, you
     * can add this parameter to request that Amazon S3 encode the keys in the
     * response.
     */
    private String encodingType;

    //IBM unsupported
    //private String expectedBucketOwner;

    // IBM-Specific
    /**
     * Optional parameter setting the mirror-destination on a WORM enabled bucket.
     */
    private String wormMirrorDestination;
	    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
//    IBM Unsupported
//    private boolean isRequesterPays;




    /**
     * Constructs a new ListMultipartUploadsRequest to list the multipart
     * uploads from the specified bucket.
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
     *        The name of the bucket, or access point ARN, containing the uploads to list.
     *        <p>
     *        When using this action with an access point, you must direct requests to the access point hostname.
     *        The access point hostname takes the form
     *        <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *        action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *        place of the bucket name. For more information about access point ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *        points</a> in the <i>Amazon S3 User Guide</i>.
     *        </p>
     *        <p>
     *        When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *        hostname. The S3 on Outposts hostname takes the form
     *        <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *        When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *        access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *        in the <i>Amazon S3 User Guide</i>.
     */
    public ListMultipartUploadsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public ListMultipartUploadsRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * The name of the bucket to which the multipart upload was initiated.
     * </p>
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
     * @return The name of the bucket to which the multipart upload was initiated. </p>
     *         <p>
     *         When using this action with an access point, you must direct requests to the access point hostname.
     *         The access point hostname takes the form
     *         <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *         action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *         place of the bucket name. For more information about access point ARNs, see <a
     *         href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *         points</a> in the <i>Amazon S3 User Guide</i>.
     *         </p>
     *         <p>
     *         When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *         hostname. The S3 on Outposts hostname takes the form
     *         <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *         When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *         access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *         href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *         in the <i>Amazon S3 User Guide</i>.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * <p>
     * The name of the bucket to which the multipart upload was initiated.
     * </p>
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
     *        The name of the bucket to which the multipart upload was initiated. </p>
     *        <p>
     *        When using this action with an access point, you must direct requests to the access point hostname.
     *        The access point hostname takes the form
     *        <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *        action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *        place of the bucket name. For more information about access point ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *        points</a> in the <i>Amazon S3 User Guide</i>.
     *        </p>
     *        <p>
     *        When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *        hostname. The S3 on Outposts hostname takes the form
     *        <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *        When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *        access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *        in the <i>Amazon S3 User Guide</i>.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * <p>
     * The name of the bucket to which the multipart upload was initiated.
     * </p>
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
     *        The name of the bucket to which the multipart upload was initiated. </p>
     *        <p>
     *        When using this action with an access point, you must direct requests to the access point hostname.
     *        The access point hostname takes the form
     *        <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *        action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *        place of the bucket name. For more information about access point ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *        points</a> in the <i>Amazon S3 User Guide</i>.
     *        </p>
     *        <p>
     *        When you use this action with Amazon S3 on Outposts, you must direct requests to the S3 on Outposts
     *        hostname. The S3 on Outposts hostname takes the form
     *        <code> <i>AccessPointName</i>-<i>AccountId</i>.<i>outpostID</i>.s3-outposts.<i>Region</i>.amazonaws.com</code>.
     *        When you use this action using S3 on Outposts through the Amazon Web Services SDKs, you provide the Outposts
     *        access point ARN in place of the bucket name. For more information about S3 on Outposts ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/S3onOutposts.html">What is S3 on Outposts</a>
     *        in the <i>Amazon S3 User Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */
    public ListMultipartUploadsRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * Returns the optional maximum number of uploads to return, or null if no
     * maximum number of uploads has been set for this request.
     *
     * @return The optional maximum number of uploads to return.
     */
    public Integer getMaxUploads() {
        return maxUploads;
    }

    /**
     * Sets the optional maximum number of uploads to return.
     *
     * @param maxUploads
     *            The maximum number of uploads to return.
     */
    public void setMaxUploads(Integer maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * Sets the optional maximum number of uploads to return and returns this
     * updated ListMultipartUploadsRequest object so that additional method
     * calls can be chained together.
     *
     * @param maxUploadsInt
     *            The optional maximum number of uploads to return.
     *
     * @return This updated ListMultipartUploadsRequest object.
     */
    public ListMultipartUploadsRequest withMaxUploads(int maxUploadsInt) {
        this.maxUploads = maxUploadsInt;
        return this;
    }

    /**
     * Returns the optional key marker indicating where in the results to begin
     * listing.
     * <p>
     * Together with the upload ID marker, specifies the multipart upload after
     * which listing should begin.
     * <p>
     * If the upload ID marker is not specified, only the keys lexicographically
     * greater than the specified key-marker will be included in the list.
     * <p>
     * If the upload ID marker is specified, any multipart uploads for a key
     * equal to the key-marker may also be included, provided those multipart
     * uploads have upload IDs lexicographically greater than the specified
     * marker.
     *
     * @return The optional key marker indicating where in the results to begin
     *         listing.
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * Sets the optional key marker indicating where in the results to begin
     * listing.
     * <p>
     * Together with the upload ID marker, specifies the multipart upload after
     * which listing should begin.
     * <p>
     * If the upload ID marker is not specified, only the keys lexicographically
     * greater than the specified key-marker will be included in the list.
     * <p>
     * If the upload ID marker is specified, any multipart uploads for a key
     * equal to the key-marker may also be included, provided those multipart
     * uploads have upload IDs lexicographically greater than the specified
     * marker.
     *
     * @param keyMarker
     *            The optional key marker indicating where in the results to
     *            begin listing.
     */
    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    /**
     * Sets the KeyMarker property for this request.
     *
     * @param keyMarker
     *            The value that KeyMarker is set to
     * @return the request with the KeyMarker set
     */
    public ListMultipartUploadsRequest withKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
        return this;
    }

    /**
     * Returns the optional upload ID marker indicating where in the results to
     * begin listing.
     * <p>
     * Together with the key marker, specifies the multipart upload after which
     * listing should begin. If no key marker is specified, the upload ID marker
     * is ignored. Otherwise, any multipart uploads for a key equal to the key
     * marker may be included in the list only if they have an upload ID
     * lexicographically greater than the specified marker.
     *
     * @return The optional upload ID marker indicating where in the results to
     *         begin listing.
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    /**
     * Sets the optional upload ID marker indicating where in the results to
     * begin listing.
     * <p>
     * Together with the key marker, specifies the multipart upload after which
     * listing should begin. If no key marker is specified, the upload ID marker
     * is ignored. Otherwise, any multipart uploads for a key equal to the key
     * marker may be included in the list only if they have an upload ID
     * lexicographically greater than the specified marker.
     *
     * @param uploadIdMarker
     *            The optional upload ID marker indicating where in the results
     *            to begin listing.
     */
    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    /**
     * Sets the optional upload ID marker indicating where in the results to
     * begin listing and returns this updated ListMultipartUploadsRequest object
     * so that additional methods can be chained together.
     * <p>
     * Together with the key marker, specifies the multipart upload after which
     * listing should begin. If no key marker is specified, the upload ID marker
     * is ignored. Otherwise, any multipart uploads for a key equal to the key
     * marker may be included in the list only if they have an upload ID
     * lexicographically greater than the specified marker.
     *
     * @param uploadIdMarker
     *            The optional upload ID marker indicating where in the results
     *            to begin listing.
     *
     * @return This updated ListMultipartUploadsRequest object.
     */
    public ListMultipartUploadsRequest withUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
        return this;
    }

	/**
	 * Returns the optional delimiter parameter that causes multipart uploads for
	 * keys that contain the same string between the prefix and the first
	 * occurrence of the delimiter to be combined into a single result element
	 * in the {@link MultipartUploadListing#getCommonPrefixes()} list. These
	 * combined keys are not returned elsewhere in the response. The most
	 * commonly used delimiter is "/", which simulates a hierarchical
	 * organization similar to a file system directory structure.
	 *
	 * @return The optional delimiter parameter that causes multipart uploads
	 *         for keys that contain the same string between the prefix and the
	 *         first occurrence of the delimiter to be combined into a single
	 *         result element in the {@link MultipartUploadListing#getCommonPrefixes()}
	 *         list.
	 */
    public String getDelimiter() {
        return delimiter;
    }

	/**
	 * Sets the optional delimiter parameter that causes multipart uploads for
	 * keys that contain the same string between the prefix and the first
	 * occurrence of the delimiter to be combined into a single result element
	 * in the {@link MultipartUploadListing#getCommonPrefixes()} list.
	 *
	 * @param delimiter
	 *            The optional delimiter parameter that causes multipart uploads
	 *            for keys that contain the same string between the prefix and
	 *            the first occurrence of the delimiter to be combined into a
	 *            single result element in the
	 *            {@link MultipartUploadListing#getCommonPrefixes()} list.
	 */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

	/**
	 * Sets the optional delimiter parameter that causes multipart uploads for
	 * keys that contain the same string between the prefix and the first
	 * occurrence of the delimiter to be rolled up into a single result element
	 * in the {@link MultipartUploadListing#getCommonPrefixes()} list. Returns
	 * this {@link ListMultipartUploadsRequest}, enabling additional method
	 * calls to be chained together.
	 *
	 * @param delimiter
	 *            The optional delimiter parameter that causes multipart uploads
	 *            for keys that contain the same string between the prefix and
	 *            the first occurrence of the delimiter to be rolled up into a
	 *            single result element in the
	 *            {@link MultipartUploadListing#getCommonPrefixes()} list.
	 *
	 * @return This {@link ListMultipartUploadsRequest}, enabling additional
	 *         method calls to be chained together.
	 */
    public ListMultipartUploadsRequest withDelimiter(String delimiter) {
        setDelimiter(delimiter);
        return this;
    }

	/**
	 * Returns the optional prefix parameter that restricts the response to
	 * multipart uploads for keys that begin with the specified prefix. Use
	 * prefixes to separate a bucket into different sets of keys, similar to how
	 * a file system organizes files into directories.
	 *
	 * @return The optional prefix parameter restricting the response to
	 *         multipart uploads for keys that begin with the specified prefix.
	 */
    public String getPrefix() {
        return prefix;
    }

	/**
	 * Sets the optional prefix parameter, restricting the response to multipart
	 * uploads for keys that begin with the specified prefix.
	 *
	 * @param prefix
	 *            The optional prefix parameter, restricting the response to
	 *            multipart uploads for keys that begin with the specified
	 *            prefix.
	 */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

	/**
	 * Sets the optional prefix parameter restricting the response to multipart
	 * uploads for keys that begin with the specified prefix. Returns this
	 * {@link ListMultipartUploadsRequest}, enabling additional method calls to
	 * be chained together.
	 *
	 * @param prefix
	 *            The optional prefix parameter restricting the response to
	 *            multipart uploads for keys that begin with the specified
	 *            prefix.
	 *
	 * @return This {@link ListMultipartUploadsRequest}, enabling additional
	 *         method calls to be chained together.
	 */
    public ListMultipartUploadsRequest withPrefix(String prefix) {
        setPrefix(prefix);
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
     * Returns this {@link ListMultipartUploadsRequest}, enabling additional method calls
     * to be chained together.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public ListMultipartUploadsRequest withEncodingType(String encodingType) {
        setEncodingType(encodingType);
        return this;
    }

    // IBM-Specific
    /**
     * Returns the optional mirror-destination value for WORM mirroring
     *
     * @return The optional mirror-destination value
     */
    @Override
    public String getWormMirrorDestination() {
        return wormMirrorDestination;
    }

    // IBM-Specific
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

    // IBM-Specific
    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * and returns the updated ListMultipartUploadsRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link ListMultipartUploadsRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListMultipartUploadsRequest withWormMirrorDestination(
            String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }
	/**
     * Returns whether the requester knows that they will be charged for the request.
     *
     * @return true if the user has enabled Requester Pays option for
     *         conducting this operation from Requester Pays Bucket.
     */
//    IBM Unsupported
//    public boolean isRequesterPays() {
//        return isRequesterPays;
//    }

    /**
     * Confirms whether the requester knows that they will be charged for the request. Bucket owners need not specify this
     * parameter in their requests.
     *
     * @param isRequesterPays if Requester Pays option is enabled for the operation.
     */
//    IBM Unsupported	 
//    public void setRequesterPays(boolean isRequesterPays) {
//        this.isRequesterPays = isRequesterPays;
//    }

    /**
     * Confirms whether the requester knows that they will be charged for the request. Bucket owners need not specify this
     * parameter in their requests.
     *
     * @param isRequesterPays if Requester Pays option is enabled for the operation.
     *
     * @return The updated ListMultipartUploadsRequest object.
     */
//    IBM Unsupported	 
//    public ListMultipartUploadsRequest withRequesterPays(boolean isRequesterPays) {
//        setRequesterPays(isRequesterPays);
//        return this;
//    }
}
