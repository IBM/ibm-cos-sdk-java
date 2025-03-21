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

/**
 * <p>
 * Provides options for deleting a specified bucket. Amazon S3 buckets can only be deleted
 * when empty.
 * </p>
 * <p>
 * Note: When attempting to delete a bucket that does not exist,
 * Amazon S3 returns
 * a success message, not an error message.
 * </p>
 */
public class DeleteBucketRequest extends AmazonWebServiceRequest implements
        Serializable, S3AccelerateUnsupported
        //IBM unsupported
        //, ExpectedBucketOwnerRequest 
        {

    /**
     * The name of the Amazon S3 bucket to delete.
     */
    private String bucketName;

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Constructs a new {@link DeleteBucketRequest},
     * ready to be executed to delete the
     * specified bucket.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket to delete.
     */
    public DeleteBucketRequest(String bucketName) {
        setBucketName(bucketName);
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public DeleteBucketRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * Sets the name of the Amazon S3 bucket to delete.
     *
     * @param bucketName
     *            The name of the Amazon S3 bucket to delete.
     *
     * @see DeleteBucketRequest#getBucketName()
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Gets the name of the Amazon S3 bucket to delete.
     *
     * @return The name of the Amazon S3 bucket to delete.
     *
     * @see DeleteBucketRequest#setBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }
}
