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
import java.io.Serializable;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * Request object containing all the options for setting a bucket's Access Control List (ACL).
 */
public class SetBucketAclRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{
	/**
	 * The name of the bucket whose ACL is being set.
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

	/** The custom ACL to apply to the specified bucket. */
	private AccessControlList acl;

	/** The canned ACL to apply to the specified bucket. */
	private CannedAccessControlList cannedAcl;

	//IBM unsupported
	//private String expectedBucketOwner;

	/**
	 * Constructs a new SetBucketAclRequest object, ready to set the specified
	 * ACL on the specified bucket when this request is executed.
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
	 *            The name of the bucket, or access point ARN, whose ACL will be set by this request.
	 * @param acl
	 *            The custom Access Control List containing the access rules to
	 *            apply to the specified bucket when this request is executed.
	 */
	public SetBucketAclRequest(String bucketName, AccessControlList acl) {
		this.bucketName = bucketName;
		this.acl = acl;
		this.cannedAcl = null;
	}

	/**
	 * Constructs a new SetBucketAclRequest object, ready to set the specified
	 * canned ACL on the specified bucket when this request is executed.
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
	 *            The name of the bucket, or access point ARN, whose ACL will be set by this request.
	 * @param acl
	 *            The Canned Access Control List to apply to the specified
	 *            bucket when this request is executed.
	 */
	public SetBucketAclRequest(String bucketName, CannedAccessControlList acl) {
		this.bucketName = bucketName;
		this.acl = null;
		this.cannedAcl = acl;
	}

	/**
	 * Returns the name of the bucket whose ACL will be modified by this request
	 * when executed.
	 *
	 * @return The name of the bucket whose ACL will be modified by this request
	 *         when executed.
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Returns the custom ACL to be applied to the specified bucket when this
	 * request is executed. A request can use either a custom ACL or a canned
	 * ACL, but not both.
	 *
	 * @return The custom ACL to be applied to the specified bucket when this
	 *         request is executed.
	 */
	public AccessControlList getAcl() {
		return acl;
	}

	/**
	 * Returns the canned ACL to be applied to the specified bucket when this
	 * request is executed. A request can use either a custom ACL or a canned
	 * ACL, but not both.
	 *
	 * @return The canned ACL to be applied to the specified bucket when this
	 *         request is executed.
	 */
	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

//IBM unsupported
//	public String getExpectedBucketOwner() {
//		return expectedBucketOwner;
//	}
//
//	public SetBucketAclRequest withExpectedBucketOwner(String expectedBucketOwner) {
//		this.expectedBucketOwner = expectedBucketOwner;
//		return this;
//	}
//
//	public void setExpectedBucketOwner(String expectedBucketOwner) {
//		withExpectedBucketOwner(expectedBucketOwner);
//	}
}
