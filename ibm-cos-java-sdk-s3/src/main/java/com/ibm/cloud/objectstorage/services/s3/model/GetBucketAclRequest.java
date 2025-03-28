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
 * Request object containing all the options for requesting a bucket's Access Control List (ACL).
 */
public class GetBucketAclRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{
	/** The name of the bucket whose ACL is being retrieved. */
	private String bucketName;

	//IBM unsupported
	//private String expectedBucketOwner;

	/**
	 * Constructs a new GetBucketAclRequest object, ready to retrieve the ACL
	 * for the specified bucket when executed.
	 *
	 * @param bucketName
	 *            The name of the bucket whose ACL will be retrieved by this
	 *            request when executed.
	 */
	public GetBucketAclRequest(String bucketName) {
		this.bucketName = bucketName;
	}

//IBM unsupported
//	public String getExpectedBucketOwner() {
//		return expectedBucketOwner;
//	}
//
//	public GetBucketAclRequest withExpectedBucketOwner(String expectedBucketOwner) {
//		this.expectedBucketOwner = expectedBucketOwner;
//		return this;
//	}
//
//	public void setExpectedBucketOwner(String expectedBucketOwner) {
//		withExpectedBucketOwner(expectedBucketOwner);
//	}

	/**
	 * Returns the name of the bucket whose ACL will be retrieved by this
	 * request, when executed.
	 * 
	 * @return The name of the bucket whose ACL will be retrieved by this
	 *         request, when executed.
	 */
	public String getBucketName() {
		return bucketName;
	}
}
