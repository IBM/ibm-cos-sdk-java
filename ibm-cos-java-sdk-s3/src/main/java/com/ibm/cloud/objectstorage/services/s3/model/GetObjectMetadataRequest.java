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
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;

/**
 * <p>The HEAD action retrieves metadata from an object without returning the object itself. This action is useful if
 * you're only interested in an object's metadata. To use HEAD, you must have READ access to the object.</p>
 *
 * <p>A <code>HEAD</code> request has the same options as a <code>GET</code> action on an object. The response is identical
 * to the <code>GET</code> response except that there is no response body. Because of this, if the <code>HEAD</code> request
 * generates an error, it returns a generic <code>404 Not Found</code> or <code>403 Forbidden</code> code. It is not possible
 * to retrieve the exact exception beyond these error codes.</p>
 *
 * <p>If you encrypt an object by using server-side encryption with customer-provided encryption keys (SSE-C) when you store
 * the object in Amazon S3, then when you retrieve the metadata from the object, you must use the following headers:</p>
 *
 * <ul>
 *     <li> <p>x-amz-server-side-encryption-customer-algorithm</p> </li>
 *     <li> <p>x-amz-server-side-encryption-customer-key</p> </li> <li>
 *         <p>x-amz-server-side-encryption-customer-key-MD5</p>
 *     </li>
 * </ul>
 *
 * <p>For more information about SSE-C, see
 * <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/ServerSideEncryptionCustomerKeys.html\">Server-Side Encryption
 * (Using Customer-Provided Encryption Keys)</a>.</p>
 *
 * <note>
 *     <ul>
 *         <li> <p>Encryption request headers, like <code>x-amz-server-side-encryption</code>, should not be sent for GET
 *         requests if your object uses server-side encryption  with CMKs stored in Amazon Web Services KMS (SSE-KMS) or
 *         server-side encryption with Amazon S3–managed encryption keys (SSE-S3). If your object does use these types of
 *         keys, you’ll get an HTTP 400 BadRequest error.</p> </li>
 *         <li> <p> The last modified property in this case is the creation date of the object.</p> </li>
 *     </ul>
 * </note>
 *
 * <p>Request headers are limited to 8 KB in size. For more information, see
 * <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/API/RESTCommonRequestHeaders.html\">Common Request Headers</a>.</p>
 *
 * <p>Consider the following when using request headers:</p>
 *
 * <ul>
 *     <li> <p> Consideration 1 – If both of the <code>If-Match</code> and <code>If-Unmodified-Since</code> headers are
 *     present in the request as follows:</p>
 *     <ul>
 *         <li> <p> <code>If-Match</code> condition evaluates to <code>true</code>, and;</p> </li>
 *         <li> <p> <code>If-Unmodified-Since</code> condition evaluates to <code>false</code>;</p> </li>
 *     </ul>
 *
 *         <p>Then Amazon S3 returns <code>200 OK</code> and the data requested.</p></li>
 *     <li> <p> Consideration 2 – If both of the <code>If-None-Match</code> and <code>If-Modified-Since</code> headers are
 *     present in the request as follows:</p>
 *     <ul>
 *         <li> <p> <code>If-None-Match</code> condition evaluates to <code>false</code>, and;</p> </li>
 *         <li> <p> <code>If-Modified-Since</code> condition evaluates to <code>true</code>;</p> </li>
 *     </ul>
 *
 *     <p>Then Amazon S3 returns the <code>304 Not Modified</code> response code.</p> </li>
 * </ul>
 *
 * <p>For more information about conditional requests, see <a href=\"https://tools.ietf.org/html/rfc7232\">RFC 7232</a>.</p>
 *
 * <p> <b>Permissions</b> </p> <
 *
 * p>You need the relevant read object (or version) permission for this operation. For more information, see
 * <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/dev/using-with-s3-actions.html\">Specifying
 * Permissions in a Policy</a>. If the object you request does not exist, the error Amazon S3 returns depends on whether
 * you also have the s3:ListBucket permission.</p>
 *
 * <ul>
 *     <li> <p>If you have the <code>s3:ListBucket</code> permission on the bucket, Amazon S3 returns an HTTP status code
 *     404 (\"no such key\") error.</p> </li>
 *     <li> <p>If you don’t have the <code>s3:ListBucket</code> permission, Amazon S3 returns an HTTP status code 403
 *     (\"access denied\") error.</p> </li>
 * </ul>
 *
 * <p>The following action is related to <code>HeadObject</code>:</p>
 *
 * <ul>
 *     <li> <p> <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObject.html\">GetObject</a> </p> </li>
 * </ul>
 *
 * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String, String)
 * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String, String, String)
 * @see GetObjectRequest
 */
public class GetObjectMetadataRequest extends AmazonWebServiceRequest implements
        SSECustomerKeyProvider, WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest
        {
    /**
     * The name of the bucket containing the object's whose metadata is being
     * retrieved.
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

    /**
     * The key of the object whose metadata is being retrieved.
     */
    private String key;

    /**
     * The optional version ID of the object version whose metadata is being
     * retrieved. If not specified, the latest version will be used.
     */
    private String versionId;

    /**
     * If enabled, the requester is charged for downloading the metadata from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    /**
     * The optional customer-provided server-side encryption key to use when
     * retrieving the metadata of a server-side encrypted object.
     */
    private SSECustomerKey sseCustomerKey;

    /**
     * The optional part number to find the number of parts of an object.
     */
    private Integer partNumber;

    //IBM unsupported
    //private String expectedBucketOwner;

    // IBM-Specific
    /**
     * The optional destination-mirror value to use for WORM mirroring
     */
    private String wormMirrorDestination;

    /**
     * Constructs a new
     * {@link GetObjectMetadataRequest}
     * used to retrieve a specified
     * object's metadata.
     *
     * @param bucketName
     *            The name of the bucket containing the object whose metadata
     *            is being retrieved.
     * @param key
     *            The key of the object whose metadata is being retrieved.
     *
     * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String bucketName, String key, String versionId)
     */
    public GetObjectMetadataRequest(String bucketName, String key) {
        setBucketName(bucketName);
        setKey(key);
    }

    /**
     * Constructs a new
     * {@link GetObjectMetadataRequest}
     * with basic options.
     *
     * @param bucketName
     *            The name of the bucket containing the object whose metadata
     *            is being retrieved.
     * @param key
     *            The key of the object whose metadata is being retrieved.
     * @param versionId
     *            The version ID of the object version whose metadata is being
     *            retrieved.
     *
     * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String bucketName, String key)
     */
    public GetObjectMetadataRequest(String bucketName, String key, String versionId) {
        this(bucketName, key);
        setVersionId(versionId);
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public GetObjectMetadataRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * The name of the bucket containing the object.
     * </p>
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The access
     * point hostname takes the form <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * When using this action with an access point through the Amazon Web Services SDKs, you provide the access point
     * ARN in place of the bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a> in
     * the <i>Amazon S3 User Guide</i>.
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
     * @return The name of the bucket containing the object.</p>
     *         <p>
     *         When using this action with an access point, you must direct requests to the access point hostname. The
     *         access point hostname takes the form
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
     *
     * @see GetObjectMetadataRequest#setBucketName(String bucketName)
     * @see GetObjectMetadataRequest#withBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket containing the object whose metadata is
     * being retrieved.
     *
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The access
     * point hostname takes the form <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * When using this action with an access point through the Amazon Web Services SDKs, you provide the access point
     * ARN in place of the bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a> in
     * the <i>Amazon S3 User Guide</i>.
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
     *            The name of the bucket, or access point ARN, containing the object's whose metadata
     *            is being retrieved.
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
     *
     * @see GetObjectMetadataRequest#getBucketName()
     * @see GetObjectMetadataRequest#withBucketName(String)
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * <p>
     * The name of the bucket containing the object.
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
     *        The name of the bucket containing the object.</p>
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
     *
     * @return This {@link GetObjectMetadataRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectMetadataRequest#getBucketName()
     * @see GetObjectMetadataRequest#setBucketName(String bucketName)
     */
    public GetObjectMetadataRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * Gets the key of the object whose metadata is being retrieved.
     *
     * @return The key of the object whose metadata is being retrieved.
     *
     * @see GetObjectMetadataRequest#setKey(String)
     * @see GetObjectMetadataRequest#withKey(String)
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the object whose metadata is being retrieved.
     *
     * @param key
     *            The key of the object whose metadata is being retrieved.
     *
     * @see GetObjectMetadataRequest#getKey()
     * @see GetObjectMetadataRequest#withKey(String)
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the key of the object whose metadata is being retrieved.
     * Returns this {@link GetObjectMetadataRequest}, enabling additional method
     * calls to be chained together.
     *
     * @param key
     *            The key of the object whose metadata is being retrieved.
     *
     * @return This {@link GetObjectMetadataRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectMetadataRequest#getKey()
     * @see GetObjectMetadataRequest#setKey(String)
     */
    public GetObjectMetadataRequest withKey(String key) {
        setKey(key);
        return this;
    }

    /**
     * Gets the optional version ID of the object version whose metadata is
     * being retrieved. If not specified, the latest version will be used.
     *
     * @return The optional version ID of the object version whose metadata is
     *         being retrieved. If not specified, the latest version will be
     *         used.
     *
     * @see GetObjectMetadataRequest#setVersionId(String)
     * @see GetObjectMetadataRequest#withVersionId(String)
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Sets the optional version ID of the object version whose metadata is
     * being retrieved. If not specified, the latest version will be used.
     *
     * @param versionId
     *            The optional version ID of the object version whose metadata
     *            is being retrieved. If not specified, the latest version will
     *            be used.
     *
     * @see GetObjectMetadataRequest#getVersionId()
     * @see GetObjectMetadataRequest#withVersionId(String)
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * Sets the optional version ID of the object version whose metadata is
     * being retrieved.
     * Returns this {@link GetObjectMetadataRequest}, enabling additional method
     * calls to be chained together.
     * If not specified, the latest version will be used.
     *
     * @param versionId
     *            The optional version ID of the object version whose metadata
     *            is being retrieved.
     *
     * @return This {@link GetObjectMetadataRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectMetadataRequest#getVersionId()
     * @see GetObjectMetadataRequest#setVersionId(String)
     */
    public GetObjectMetadataRequest withVersionId(String versionId) {
        setVersionId(versionId);
        return this;
    }


    /**
     * Returns true if the user has enabled Requester Pays option when
     * downloading the object metadata from Requester Pays Bucket; else false.
     *
     * <p>
     * If a bucket is enabled for Requester Pays, then any attempt to read an
     * object from it without Requester Pays enabled will result in a 403 error
     * and the bucket owner will be charged for the request.
     *
     * <p>
     * Enabling Requester Pays disables the ability to have anonymous access to
     * this bucket
     *
     * @return true if the user has enabled Requester Pays option for
     *         downloading the object metadata from Requester Pays Bucket.
     */
    public boolean isRequesterPays() {
        return isRequesterPays;
    }

    /**
     * Used for downloading an Amazon S3 Object metadata from a Requester Pays Bucket. If
     * set the requester is charged for downloading the data from the bucket.
     *
     * <p>
     * If a bucket is enabled for Requester Pays, then any attempt to read an
     * object metadata from it without Requester Pays enabled will result in a 403 error
     * and the bucket owner will be charged for the request.
     *
     * <p>
     * Enabling Requester Pays disables the ability to have anonymous access to
     * this bucket
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
     * updated GetObjectMetadataRequest object so that additional method calls can be
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
     * @return The updated GetObjectMetadataRequest object.
     */
    public GetObjectMetadataRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }

    @Override
    public SSECustomerKey getSSECustomerKey() {
        return sseCustomerKey;
    }

    /**
     * Sets the optional customer-provided server-side encryption key to use
     * when retrieving the metadata of a server-side encrypted object.
     *
     * @param sseKey
     *            The optional customer-provided server-side encryption key to
     *            use when retrieving the metadata of a server-side encrypted
     *            object.
     */
    public void setSSECustomerKey(SSECustomerKey sseKey) {
        this.sseCustomerKey = sseKey;
    }

    /**
     * Sets the optional customer-provided server-side encryption key to use
     * when retrieving the metadata of a server-side encrypted object, and
     * retuns the updated request object so that additional method calls can be
     * chained together.
     *
     * @param sseKey
     *            The optional customer-provided server-side encryption key to
     *            use when retrieving the metadata of a server-side encrypted
     *            object.
     *
     * @return This updated request object so that additional method calls can
     *         be chained together.
     */
    public GetObjectMetadataRequest withSSECustomerKey(SSECustomerKey sseKey) {
        setSSECustomerKey(sseKey);
        return this;
    }

    /**
     * <p>
     * Returns the optional part number that indicates a part in multipart object.
     * </p>
     *
     * @return The part number representing a part in a multipart object.
     *
     * @see GetObjectMetadataRequest#setPartNumber(Integer)
     * @see GetObjectMetadataRequest#withPartNumber(Integer)
     */
    public Integer getPartNumber() {
        return partNumber;
    }

    /**
     * <p>
     * Sets the optional part number to find the number of parts of an object.
     * </p>
     * <p>
     * To find the number of parts of an object, set partNumber to 1 and observe the x-amz-mp-parts-count response.
     * If the object exists and x-amz-mp-parts-count is missing it's implicitly 1.
     * Otherwise number of parts is equal to the value returned by x-amz-mp-parts-count.
     * </p>
     * <p>
     * The valid range for part number is 1 - 10000 inclusive.
     * For partNumber < 1, an AmazonS3Exception is thrown with response code 400 bad request
     * For partNumber larger than actual part count,  an AmazonS3Exception is thrown with response code 416 Request Range Not Satisfiable
     * </p>
     *
     * @param partNumber
     *            The part number representing a part in a multipart object.
     *
     * @see GetObjectMetadataRequest#getPartNumber()
     * @see GetObjectMetadataRequest#withPartNumber(Integer)
     */
    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * <p>
     * Sets the optional part number to find the number of parts of an object.
     * </p>
     * <p>
     * To find the number of parts of an object, set partNumber to 1 and observe the x-amz-mp-parts-count response.
     * If the object exists and x-amz-mp-parts-count is missing it's implicitly 1.
     * Otherwise number of parts is equal to the value returned by x-amz-mp-parts-count.
     * </p>
     * <p>
     * The valid range for part number is 1 - 10000 inclusive.
     * For partNumber < 1, an AmazonS3Exception is thrown with response code 400 bad request
     * For partNumber larger than actual part count,  an AmazonS3Exception is thrown with response code 416 Request Range Not Satisfiable
     * </p>
     *
     * @param partNumber
     *            The part number representing a part in a multipart object.
     *
     * @return This {@link GetObjectRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectMetadataRequest#getPartNumber()
     * @see GetObjectMetadataRequest#setPartNumber(Integer)
     */
    public GetObjectMetadataRequest withPartNumber(Integer partNumber) {
        setPartNumber(partNumber);
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
     * and returns the updated GetObjectRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link GetObjectMetadataRequest}, enabling additional method
     *         calls to be chained together.
     */
    public GetObjectMetadataRequest withWormMirrorDestination(String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }

}
