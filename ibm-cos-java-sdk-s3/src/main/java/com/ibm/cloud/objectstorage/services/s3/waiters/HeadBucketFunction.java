/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ibm.cloud.objectstorage.services.s3.waiters;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.waiters.SdkFunction;
import com.ibm.cloud.objectstorage.services.s3.model.HeadBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.HeadBucketResult;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

@SdkInternalApi
public class HeadBucketFunction implements
        SdkFunction<HeadBucketRequest, HeadBucketResult> {

    /**
     * Represents the service client
     */
    private final AmazonS3 client;

    /**
     * Constructs a new HeadBucketFunction with the given client
     * 
     * @param client
     *        Service client
     */
    public HeadBucketFunction(AmazonS3 client) {
        this.client = client;
    }

    /**
     * Makes a call to the operation specified by the waiter by taking the
     * corresponding request and returns the corresponding result
     * 
     * @param headBucketRequest
     *        Corresponding request for the operation
     * @return Corresponding result of the operation
     */
    @Override
    public HeadBucketResult apply(HeadBucketRequest headBucketRequest) {
        return client.headBucket(headBucketRequest);
    }
}
