/*
 * Copyright 2015-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

/**
 * Request object containing all the options for setting a object's Access
 * Control List (ACL).
 */
public class SetObjectAclRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//,ExpectedBucketOwnerRequest 
{

    /**
     * The name of the bucket containing the object whose ACL is being set.
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
    private final String bucketName;

    /** The name of the object whose ACL is being set. */
    private final String key;

    /** The version ID of the object version whose ACL is being set. */
    private final String versionId;

    /** The custom ACL to apply to the specified object. */
    private final AccessControlList acl;

    /** The canned ACL to apply to the specified object. */
    private final CannedAccessControlList cannedAcl;

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified
     * ACL on the specified object when this request is executed.
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
     *            The name of the bucket, or access point ARN, containing the object whose ACL is
     *            being set.
     * @param key
     *            The name of the object whose ACL is being set.
     * @param acl
     *            The custom Access Control List containing the access rules to
     *            apply to the specified bucket when this request is executed.
     */
    public SetObjectAclRequest(String bucketName, String key,
            AccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = null;

        this.acl = acl;
        this.cannedAcl = null;
    }

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified
     * ACL on the specified object when this request is executed.
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
     *            The name of the bucket, or access point ARN, containing the object whose ACL is
     *            being set.
     * @param key
     *            The name of the object whose ACL is being set.
     * @param acl
     *            The Canned Access Control List to apply to the specified
     *            bucket when this request is executed.
     */
    public SetObjectAclRequest(String bucketName, String key,
            CannedAccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = null;

        this.acl = null;
        this.cannedAcl = acl;
    }

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified
     * ACL on the specified object when this request is executed.
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
     *            The name of the bucket, or access point ARN, containing the object whose ACL is
     *            being set.
     * @param key
     *            The name of the object whose ACL is being set.
     * @param versionId
     *            The version ID of the object version whose ACL is being set.
     * @param acl
     *            The custom Access Control List containing the access rules to
     *            apply to the specified bucket when this request is executed.
     */
    public SetObjectAclRequest(String bucketName, String key, String versionId,
            AccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = versionId;

        this.acl = acl;
        this.cannedAcl = null;
    }

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified
     * ACL on the specified object when this request is executed.
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
     *            The name of the bucket, or access point ARN, containing the object whose ACL is
     *            being set.
     * @param key
     *            The name of the object whose ACL is being set.
     * @param versionId
     *            The version ID of the object version whose ACL is being set.
     * @param acl
     *            The Canned Access Control List to apply to the specified
     *            bucket when this request is executed.
     */
    public SetObjectAclRequest(String bucketName, String key, String versionId,
            CannedAccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = versionId;

        this.acl = null;
        this.cannedAcl = acl;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public SetObjectAclRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * The bucket name that contains the object to which you want to attach the ACL.
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
     * @return The bucket name that contains the object to which you want to attach the ACL. </p>
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
     * Key for which the PUT action was initiated.
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
     * @return Key for which the PUT action was initiated.</p>
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
    public String getKey() {
        return key;
    }

    /**
     * Returns the version ID of the object version whose ACL is being set.
     *
     * @return The version ID of the object version whose ACL is being set.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Returns the custom ACL to be applied to the specified object when this
     * request is executed. A request can use either a custom ACL or a canned
     * ACL, but not both.
     *
     * @return The custom ACL to be applied to the specified bucket when this
     *         request is executed, or null if the request is to be executed
     *         with a canned ACL.
     */
    public AccessControlList getAcl() {
        return acl;
    }

    /**
     * Returns the canned ACL to be applied to the specified object when this
     * request is executed. A request can use either a custom ACL or a canned
     * ACL, but not both.
     *
     * @return The canned ACL to be applied to the specified bucket when this
     *         request is executed, or null if the request is to be executed
     *         with a custom ACL.
     */
    public CannedAccessControlList getCannedAcl() {
        return cannedAcl;
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
     * updated SetObjectAclRequest object so that additional method calls can be
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
     * @return The updated SetObjectAclRequest object.
     */
    public SetObjectAclRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }
}
