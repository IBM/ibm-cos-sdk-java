/*
 * Copyright 2015-2024 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.s3.model;
import java.io.Serializable;

import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Request object for the parameters to get a bucket's versioning configuration.
 *
 * @see AmazonS3#getBucketVersioningConfiguration(GetBucketVersioningConfigurationRequest)
 */
public class GetBucketVersioningConfigurationRequest extends GenericBucketRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Creates a request object, ready to be executed to fetch the versioning
     * configuration for the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket whose versioning configuration is being
     *            fetched.
     */
    public GetBucketVersioningConfigurationRequest(String bucketName) {
        super(bucketName);
    }
//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public GetBucketVersioningConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

}
