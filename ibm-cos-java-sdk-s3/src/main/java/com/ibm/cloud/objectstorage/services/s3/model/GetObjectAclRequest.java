/*
 * Copyright 2016-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.internal.Constants;
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * <p>
 * Provide options to get an object ACL.
 * </p>
 * <p>
 * Each bucket and object in Amazon S3 has an ACL that defines its access
 * control policy. When a request is made, Amazon S3 authenticates the request
 * using its standard authentication procedure and then checks the ACL to verify
 * the sender was granted access to the bucket or object. If the sender is
 * approved, the request proceeds. Otherwise, Amazon S3 returns an error.
 * </p>
 *
 * @see AmazonS3#getObjectAcl(String, String)
 * @see AmazonS3#getObjectAcl(String, String, String)
 * @see AmazonS3#getObjectAcl(GetObjectAclRequest)
 */
public class GetObjectAclRequest extends AmazonWebServiceRequest implements
        WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest
        {

    /**
     * Builder of an S3 object identifier. This member field is never null.
     */
    private S3ObjectIdBuilder s3ObjectIdBuilder = new S3ObjectIdBuilder();

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    // IBM-Specific
    /**
     * Optional parameter setting the mirror-destination on a WORM enabled bucket.
     */
    private String wormMirrorDestination;

    //IBM unsupported
    //private String expectedBucketOwner;

    public GetObjectAclRequest(String bucketName, String key) {
        this(bucketName, key, null);
    }
    public GetObjectAclRequest(String bucketName, String key, String versionId) {
        setBucketName(bucketName);
        setKey(key);
        setVersionId(versionId);
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public GetObjectAclRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * The bucket name that contains the object for which to get the ACL information.
     * </p>
     * <p>
     * When using this action with an access point, you must direct requests to the access point hostname. The access
     * point hostname takes the form <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com.
     * When using this action with an access point through the Amazon Web Services SDKs, you provide the access point
     * ARN in place of the bucket name. For more information about access point ARNs, see <a
     * href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access points</a> in
     * the <i>Amazon S3 User Guide</i>.
     * </p>
     *
     * @return The bucket name that contains the object for which to get the ACL information. </p>
     *         <p>
     *         When using this action with an access point, you must direct requests to the access point hostname. The
     *         access point hostname takes the form
     *         <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *         action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *         place of the bucket name. For more information about access point ARNs, see <a
     *         href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *         points</a> in the <i>Amazon S3 User Guide</i>.
     *
     * @see GetObjectAclRequest#setBucketName(String)
     * @see GetObjectAclRequest#withBucket(String)
     */
    public String getBucketName() {
        return s3ObjectIdBuilder.getBucket();
    }

    /**
     * <p>
     * The bucket name that contains the object for which to get the ACL information.
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
     *
     * @param bucketName
     *        The bucket name that contains the object for which to get the ACL information. </p>
     *        <p>
     *        When using this action with an access point, you must direct requests to the access point hostname.
     *        The access point hostname takes the form
     *        <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *        action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *        place of the bucket name. For more information about access point ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *        points</a> in the <i>Amazon S3 User Guide</i>.
     * @see GetObjectAclRequest#getBucketName()
     * @see GetObjectAclRequest#withBucket(String)
     */
    public void setBucketName(String bucketName) {
        s3ObjectIdBuilder.setBucket(bucketName);
    }
    /**
     * <p>
     * The bucket name that contains the object for which to get the ACL information.
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
     *
     * @param bucketName
     *        The bucket name that contains the object for which to get the ACL information. </p>
     *        <p>
     *        When using this action with an access point, you must direct requests to the access point hostname.
     *        The access point hostname takes the form
     *        <i>AccessPointName</i>-<i>AccountId</i>.s3-accesspoint.<i>Region</i>.amazonaws.com. When using this
     *        action with an access point through the Amazon Web Services SDKs, you provide the access point ARN in
     *        place of the bucket name. For more information about access point ARNs, see <a
     *        href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-access-points.html">Using access
     *        points</a> in the <i>Amazon S3 User Guide</i>.
     * @return This {@link GetObjectAclRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectAclRequest#getBucketName()
     * @see GetObjectAclRequest#setBucketName(String)
     */
    public GetObjectAclRequest withBucket(String bucketName) {
        setBucketName(bucketName);
        return this;
    }
    /**
     * Gets the key under which the object whose ACL to be retrieved is stored.
     *
     * @return The key under which the object whose ACL to be retrieved is stored.
     *
     * @see GetObjectAclRequest#setKey(String)
     * @see GetObjectAclRequest#withKey(String)
     */
    public String getKey() {
        return s3ObjectIdBuilder.getKey();
    }

    /**
     * Sets the key under which the object whose ACL to be retrieved is stored.
     *
     * @param key
     *            The key under which the object whose ACL to be retrieved is stored.
     *
     * @see GetObjectAclRequest#getKey()
     * @see GetObjectAclRequest#withKey(String)
     */
    public void setKey(String key) {
        s3ObjectIdBuilder.setKey(key);
    }

    /**
     * Sets the key under which the object whose ACL to be retrieved is stored.
     * Returns this {@link GetObjectAclRequest}, enabling additional method
     * calls to be chained together.
     *
     * @param key
     *            The key under which the object whose ACL to be retrieved is stored.
     *
     * @return This {@link GetObjectAclRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see GetObjectAclRequest#getKey()
     * @see GetObjectAclRequest#setKey(String)
     */
    public GetObjectAclRequest withKey(String key) {
        setKey(key);
        return this;
    }

    /**
     * <p>
     * Gets the optional version ID specifying which version of the object whose ACL to
     * be retrieved. If not specified, the most recent version's ACL will be retrieved.
     * </p>
     * <p>
     * Objects created before versioning was enabled or when versioning is
     * suspended are given the default <code>null</code> version ID (see
     * {@link Constants#NULL_VERSION_ID}). Note that the
     * <code>null</code> version ID is a valid version ID and is not the
     * same as not having a version ID.
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link AmazonS3#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
     * </p>
     *
     * @return The optional version ID specifying which version of the object whose ACL
     *         to be retrieved. If not specified, the most recent version will be
     *         retrieved.
     *
     * @see GetObjectAclRequest#setVersionId(String)
     * @see GetObjectAclRequest#withVersionId(String)
     */
    public String getVersionId() {
        return s3ObjectIdBuilder.getVersionId();
    }

    /**
     * Sets the optional version ID specifying which version of the object whose ACL to
     * be retrieved. If not specified, the most recent version's ACL will be retrieved.
     * <p>
     * Objects created before versioning was enabled or when versioning is
     * suspended will be given the default <code>null</code> version ID (see
     * {@link Constants#NULL_VERSION_ID}). Note that the
     * <code>null</code> version ID is a valid version ID and is not the
     * same as not having a version ID.
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link AmazonS3#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
     * </p>
     *
     * @param versionId
     *            The optional version ID specifying which version of the object whose ACL
     *            to be retrieved.
     *
     * @see GetObjectAclRequest#getVersionId()
     * @see GetObjectAclRequest#withVersionId(String)
     */
    public void setVersionId(String versionId) {
        s3ObjectIdBuilder.setVersionId(versionId);
    }

    /**
     * <p>
     * Sets the optional version ID specifying which version of the object whose ACL to be
     * retrieved and returns this {@link GetObjectAclRequest}, enabling additional method calls to be
     * chained together. If not specified, the most recent version's ACL will be
     * retrieved.
     * </p>
     * <p>
     * Objects created before versioning was enabled or when versioning is
     * suspended will be given the default or <code>null</code> version ID (see
     * {@link Constants#NULL_VERSION_ID}). Note that the
     * <code>null</code> version ID is a valid version ID and is not the
     * same as not having a version ID.
     * </p>
     * <p>
     * For more information about enabling versioning for a bucket, see
     * {@link AmazonS3#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
     * </p>
     *
     * @param versionId
     *            The optional version ID specifying which version of the object whose ACL is
     *            to be retrieved.
     *
     * @return The updated request object, enabling additional method calls to be
     * chained together.
     *
     * @see GetObjectAclRequest#getVersionId()
     * @see GetObjectAclRequest#setVersionId(String)
     */
    public GetObjectAclRequest withVersionId(String versionId) {
        setVersionId(versionId);
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
     * updated GetObjectAclRequest object so that additional method calls can be
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
     * @return The updated GetObjectAclRequest object.
     */
    public GetObjectAclRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
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

    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * and returns the updated GetObjectAclRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link GetObjectAclRequest}, enabling additional method
     *         calls to be chained together.
     */
    public GetObjectAclRequest withWormMirrorDestination(String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }

}
