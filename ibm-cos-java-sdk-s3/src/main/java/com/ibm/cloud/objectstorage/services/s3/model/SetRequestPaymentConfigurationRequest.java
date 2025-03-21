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
 * Request object for setting the request payment configuration associated with
 * an Amazon S3 bucket.
 */
public class SetRequestPaymentConfigurationRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    /** The name of the Amazon S3 bucket.*/
    private String bucketName;

    /** The configuration associated with the Amazon S3 bucket.*/
    private RequestPaymentConfiguration configuration;

    //IBM unsupported
    //private String expectedBucketOwner;

    public SetRequestPaymentConfigurationRequest(String bucketName,
            RequestPaymentConfiguration configuration) {
        this.setBucketName(bucketName);
        this.configuration = configuration;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public SetRequestPaymentConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    public RequestPaymentConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RequestPaymentConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
