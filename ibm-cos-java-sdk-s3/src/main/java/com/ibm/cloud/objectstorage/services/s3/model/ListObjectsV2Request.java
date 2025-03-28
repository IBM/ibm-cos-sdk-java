/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Request to retrieve a listing of objects in an S3 bucket.
 */
public class ListObjectsV2Request extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    /**
     * The name of the Amazon S3 bucket to list.
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
     * Optional parameter that causes keys that contain the same string between
     * the prefix and the first occurrence of the delimiter to be rolled up into
     * a single result element in the
     * {@link ListObjectsV2Result#getCommonPrefixes()} list. These rolled-up keys
     * are not returned elsewhere in the response. The most commonly used
     * delimiter is "/", which simulates a hierarchical organization similar to
     * a file system directory structure.
     */
    private String delimiter;

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
     * Optional parameter indicating the maximum number of keys to include in
     * the response. Amazon S3 might return fewer than this, but will not return
     * more. Even if maxKeys is not specified, Amazon S3 will limit the number
     * of results in the response.
     */
    private Integer maxKeys;

    /**
     * Optional parameter restricting the response to keys which begin with the
     * specified prefix. You can use prefixes to separate a bucket into
     * different sets of keys in a way similar to how a file system uses
     * folders.
     */
    private String prefix;

    /**
     * Optional parameter which allows list to be continued from a specific point.
     * ContinuationToken is provided in truncated list results.
     */
    private String continuationToken;

    /**
     * The owner field is not present in ListObjectsV2 results by default. If this flag is
     * set to true the owner field will be included.
     */
    private boolean fetchOwner;

    /**
     * Optional parameter indicating where you want Amazon S3 to start the object listing
     * from.  This can be any key in the bucket.
     */
    private String startAfter;

    
//IBM unsupported
//    private boolean isRequesterPays;
//    private String expectedBucketOwner;

    /**
     * Optional parameter indicating to include some attributes of an object in
     * the response.
     */
//    IBM Unsupported
//    private List<String> optionalObjectAttributes;

//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public ListObjectsV2Request withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * Bucket name to list.
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
     * @return Bucket name to list. </p>
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
     * Bucket name to list.
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
     *        Bucket name to list. </p>
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
     * Bucket name to list.
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
     *        Bucket name to list. </p>
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
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     */
    public ListObjectsV2Request withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * Gets the optional delimiter parameter that causes keys that contain
     * the same string between the prefix and the first occurrence of the
     * delimiter to be combined into a single result element in the
     * {@link ListObjectsV2Result#getCommonPrefixes()} list. These combined keys
     * are not returned elsewhere in the response. The most commonly used
     * delimiter is "/", which simulates a hierarchical organization similar to
     * a file system directory structure.
     *
     * @return The optional delimiter parameter that causes keys that contain
     *         the same string between the prefix and the first occurrence of
     *         the delimiter to be combined into a single result element in the
     *         {@link ListObjectsV2Result#getCommonPrefixes()} list.
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the optional delimiter parameter that causes keys that contain the
     * same string between the prefix and the first occurrence of the delimiter
     * to be combined into a single result element in the
     * {@link ListObjectsV2Result#getCommonPrefixes()} list.
     *
     * @param delimiter
     *            The optional delimiter parameter that causes keys that contain
     *            the same string between the prefix and the first occurrence of
     *            the delimiter to be combined into a single result element in
     *            the {@link ListObjectsV2Result#getCommonPrefixes()} list.
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the optional delimiter parameter that causes keys that contain the
     * same string between the prefix and the first occurrence of the delimiter
     * to be rolled up into a single result element in the
     * {@link ListObjectsV2Result#getCommonPrefixes()} list.
     * Returns this {@link ListObjectsV2Request}, enabling additional method
     * calls to be chained together.
     *
     * @param delimiter
     *            The optional delimiter parameter that causes keys that contain
     *            the same string between the prefix and the first occurrence of
     *            the delimiter to be rolled up into a single result element in
     *            the {@link ListObjectsV2Result#getCommonPrefixes()} list.
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     */
    public ListObjectsV2Request withDelimiter(String delimiter) {
        setDelimiter(delimiter);
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
     * Returns this {@link ListObjectsV2Request}, enabling additional method calls
     * to be chained together.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public ListObjectsV2Request withEncodingType(String encodingType) {
        setEncodingType(encodingType);
        return this;
    }

    /**
     * Gets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response. Amazon S3 might return fewer keys than specified, but will
     * never return more. Even if the optional parameter is not specified,
     * Amazon S3 will limit the number of results in the response.
     *
     * @return The optional parameter indicating the maximum number of keys to
     *         include in the response.
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     */
    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     * Returns this {@link ListObjectsV2Request}, enabling additional method
     * calls to be chained together.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     *
     * @see ListObjectsV2Request#getMaxKeys()
     * @see ListObjectsV2Request#setMaxKeys(Integer)
     */
    public ListObjectsV2Request withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    /**
     * Gets the optional prefix parameter and restricts the response to keys
     * that begin with the specified prefix. Use prefixes to separate a
     * bucket into different sets of keys, similar to how a file system organizes files
     * into directories.
     *
     * @return The optional prefix parameter restricting the response to keys
     *         that begin with the specified prefix.
     *
     * @see ListObjectsV2Request#setPrefix(String)
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the optional prefix parameter, restricting the response to keys that
     * begin with the specified prefix.
     *
     * @param prefix
     *            The optional prefix parameter, restricting the response to keys
     *            that begin with the specified prefix.
     *
     * @see ListObjectsV2Request#getPrefix()
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the optional prefix parameter restricting the response to keys that
     * begin with the specified prefix.
     * Returns this {@link ListObjectsV2Request}, enabling additional method
     * calls to be chained together.
     *
     * @param prefix
     *            The optional prefix parameter restricting the response to keys
     *            that begin with the specified prefix.
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     *
     * @see ListObjectsV2Request#getPrefix()
     * @see ListObjectsV2Request#setPrefix(String)
     */
    public ListObjectsV2Request withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Gets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @return The optional continuation token associated with this request.
     */
    public String getContinuationToken() { return continuationToken; }

    /**
     * Sets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @param continuationToken
     *                     The optional continuation token to associate with this request.
     */
    public void setContinuationToken(String continuationToken) { this.continuationToken = continuationToken; }

    /**
     * Sets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @param continuationToken
     *                     The optional continuation token to associate with this request.
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     */
    public ListObjectsV2Request withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
    }

    /**
     * Returns if fetch owner is set.  The owner field is not present in ListObjectsV2
     * results by default.  If this flag is set to true the owner field will be included.

     * @return whether fetchOwner is set
     */
    public boolean isFetchOwner() { return fetchOwner; }

    /**
     * Sets the optional fetch owner flag.  The owner field is not present in ListObjectsV2
     * results by default.  If this flag is set to true the owner field will be included.

     * @param fetchOwner
     *               Set to true if the owner field should be included in results
     */
    public void setFetchOwner(boolean fetchOwner) { this.fetchOwner = fetchOwner; }

    /**
     * Sets the optional fetch owner flag.  The owner field is not present in ListObjectsV2
     * results by default.  If this flag is set to true the owner field will be included.

     * @param fetchOwner
     *               Set to true if the owner field should be included in results
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     */
    public ListObjectsV2Request withFetchOwner(boolean fetchOwner) {
        setFetchOwner(fetchOwner);
        return this;
    }

    /**
     * Returns optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @return the optional startAfter parameter
     */
    public String getStartAfter() { return startAfter; }

    /**
     * Sets the optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @param startAfter
     *                The optional startAfter parameter.  This can be any key in the bucket.
     */
    public void setStartAfter(String startAfter) { this.startAfter = startAfter; }

    /**
     * Sets the optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @param startAfter
     *                The optional startAfter parameter.  This can be any key in the bucket.
     *
     * @return This {@link ListObjectsV2Request}, enabling additional method
     *         calls to be chained together.
     */
    public ListObjectsV2Request withStartAfter(String startAfter) {
        setStartAfter(startAfter);
        return this;
    }
}
