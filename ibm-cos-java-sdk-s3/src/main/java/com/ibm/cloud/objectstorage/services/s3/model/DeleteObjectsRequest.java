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

import java.util.ArrayList;
import java.util.List;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Provides options for deleting multiple objects in a specified bucket. Once
 * deleted, the object(s) can only be restored if versioning was enabled when
 * the object(s) was deleted.You may specify up to <a href=
 * "http://docs.aws.amazon.com/AmazonS3/latest/API/multiobjectdeleteapi.html"
 * >1000 keys</a>. </p>
 *
 * @see AmazonS3#deleteObjects(DeleteObjectsRequest)
 */
public class DeleteObjectsRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    /**
     * The name of the Amazon S3 bucket containing the object(s) to delete.
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
     * Whether to enable quiet mode for the response. In quiet mode, only errors
     * are reported. Defaults to false.
     */
    private boolean quiet;

    /**
     * The optional Multi-Factor Authentication information to include with this
     * request. Multi-Factor Authentication is required when deleting a version
     * from a bucket that has enabled MFA Delete in its bucket versioning
     * configuration. See
     * {@link BucketVersioningConfiguration#setMfaDeleteEnabled(Boolean)} for
     * more information on MFA Delete.
     */
    private MultiFactorAuthentication mfa;

    /**
     * List of keys to delete, with optional versions.
     */
    private final List<KeyVersion> keys = new ArrayList<KeyVersion>();

    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterPays;

    //IBM unsupported
    //private boolean bypassGovernanceRetention;
    //private String expectedBucketOwner;

    /**
     * Constructs a new {@link DeleteObjectsRequest}, specifying the objects'
     * bucket name.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket containing the object(s) to
     *            delete.
     */
    public DeleteObjectsRequest(String bucketName) {
        setBucketName(bucketName);
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public DeleteObjectsRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * <p>
     * The bucket name containing the objects to delete.
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
     * @return The bucket name containing the objects to delete. </p>
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
     * @see DeleteObjectsRequest#setBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * <p>
     * The bucket name containing the objects to delete.
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
     *        The bucket name containing the objects to delete. </p>
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
     * @see DeleteObjectsRequest#getBucketName()
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * <p>
     * The bucket name containing the objects to delete.
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
     *        The bucket name containing the objects to delete. </p>
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
     * @return The updated {@link DeleteObjectsRequest} object, enabling
     *         additional method calls to be chained together.
     */
    public DeleteObjectsRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * <p>
     * Gets the optional Multi-Factor Authentication information included
     * with this request.
     * </p>
     * <p>
     * Multi-Factor Authentication is required when deleting an object version
     * from a bucket which has MFADelete enabled in its bucket versioning
     * configuration.
     * </p>
     * <p>
     * See {@link BucketVersioningConfiguration#setMfaDeleteEnabled(Boolean)}
     * for more information on MFADelete.
     * </p>
     *
     * @return The optional Multi-Factor Authentication information included
     *         with this request.
     */
    public MultiFactorAuthentication getMfa() {
        return mfa;
    }

    /**
     * <p>
     * Sets the optional Multi-Factor Authentication information to include with
     * this request.
     * </p>
     * <p>
     * Multi-Factor Authentication is required when deleting an object version
     * from a bucket which has MFADelete enabled in its bucket versioning
     * configuration.
     * </p>
     * <p>
     * See {@link BucketVersioningConfiguration#setMfaDeleteEnabled(Boolean)}
     * for more information on MFADelete.
     * </p>
     *
     * @param mfa
     *            The optional Multi-Factor Authentication information to
     *            include with this request.
     */
    public void setMfa(MultiFactorAuthentication mfa) {
        this.mfa = mfa;
    }

    /**
     * <p>
     * Sets the optional Multi-Factor Authentication information to include with
     * this request
     * Returns this, enabling additional method
     * calls to be chained together.
     * </p>
     * <p>
     * Multi-Factor Authentication is required when deleting an object version
     * from a bucket which has MFADelete enabled in its bucket versioning
     * configuration
     * </p>
     * <p>
     * See {@link BucketVersioningConfiguration#setMfaDeleteEnabled(Boolean)}
     * for more information on MFADelete.
     * </p>
     *
     * @param mfa
     *            The optional Multi-Factor Authentication information to
     *            include with this request.
     *
     * @return this, enabling additional method
     *         calls to be chained together.
     */
    public DeleteObjectsRequest withMfa(MultiFactorAuthentication mfa) {
        setMfa(mfa);
        return this;
    }

    /**
     * Sets the quiet element for this request. When true, only errors will be
     * returned in the service response.
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * Returns the quiet element for this request. When true, only errors will be
     * returned in the service response.
     */
    public boolean getQuiet() {
        return quiet;
    }

    /**
     * Sets the quiet element for this request. When true, only errors will be
     * returned in the service response.
     *
     * @return this, to chain multiple calls together.
     */
    public DeleteObjectsRequest withQuiet(boolean quiet) {
        this.setQuiet(quiet);
        return this;
    }

    /**
     * Sets the list of keys to delete from this bucket, clearing any existing
     * list of keys.
     *
     * @param keys
     *            The list of keys to delete from this bucket
     */
    public void setKeys(List<KeyVersion> keys) {
        this.keys.clear();
        this.keys.addAll(keys);
    }

    /**
     * Sets the list of keys to delete from this bucket, clearing any existing
     * list of keys.
     *
     * @param keys
     *            The list of keys to delete from this bucket
     *
     * @return this, to chain multiple calls togethers.
     */
    public DeleteObjectsRequest withKeys(List<KeyVersion> keys) {
        setKeys(keys);
        return this;
    }

    /**
     * Returns the list of keys to delete from this bucket.
     */
    public List<KeyVersion> getKeys() {
        return keys;
    }

    /**
     * Convenience method to specify a set of keys without versions.
     *
     * @see DeleteObjectsRequest#withKeys(List)
     */
    public DeleteObjectsRequest withKeys(String... keys) {
        List<KeyVersion> keyVersions = new ArrayList<KeyVersion>(keys.length);
        for (String key : keys) {
            keyVersions.add(new KeyVersion(key));
        }
        setKeys(keyVersions);
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
     * updated DeleteObjectsRequest object so that additional method calls can be
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
     * @return The updated DeleteObjectsRequest object.
     */
    public DeleteObjectsRequest withRequesterPays(boolean isRequesterPays) {
        setRequesterPays(isRequesterPays);
        return this;
    }

    /**
     * <p>Specifies whether you want to delete this object even if it has a Governance-type Object Lock in place. To use this
     * header, you must have the <code>s3:PutBucketPublicAccessBlock</code> permission.</p>
     */
    //IBM unsupported	 
    //public boolean getBypassGovernanceRetention() {
    //    return bypassGovernanceRetention;
    //}

    /**
     * <p>Specifies whether you want to delete this object even if it has a Governance-type Object Lock in place. To use this
     * header, you must have the <code>s3:PutBucketPublicAccessBlock</code> permission.</p>
     */
    //IBM unsupported	 
    //public DeleteObjectsRequest withBypassGovernanceRetention(boolean bypassGovernanceRetention) {
    //    this.bypassGovernanceRetention = bypassGovernanceRetention;
    //    return this;
    //}

    /**
     * <p>Specifies whether you want to delete this object even if it has a Governance-type Object Lock in place. To use this
     * header, you must have the <code>s3:PutBucketPublicAccessBlock</code> permission.</p>
     */
    //IBM unsupported
    //public void setBypassGovernanceRetention(boolean bypassGovernanceRetention) {
    //    withBypassGovernanceRetention(bypassGovernanceRetention);
    //}

    /**
     * A key to delete, with an optional version attribute.
     */
    public static class KeyVersion implements Serializable {

        private final String key;
        private final String version;

        /**
         * Constructs a key without a version.
         */
        public KeyVersion(String key) {
            this(key, null);
        }

        /**
         * Constructs a key-version pair.
         */
        public KeyVersion(String key, String version) {
            this.key = key;
            this.version = version;
        }

        public String getKey() {
            return key;
        }

        public String getVersion() {
            return version;
        }
    }
}
