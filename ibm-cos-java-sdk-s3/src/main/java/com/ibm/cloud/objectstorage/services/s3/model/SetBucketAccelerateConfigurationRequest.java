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

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * <p>
 * Contains options for setting the accelerate configuration for a bucket.
 * </p>
 */
public class SetBucketAccelerateConfigurationRequest extends AmazonWebServiceRequest 
//IBM unsupported
//implements ExpectedBucketOwnerRequest 
{

    /**
     * The bucket whose accelerate configuration is being set.
     */
    private String bucketName;

    /**
     * The new accelerate configuration for the specified bucket.
     */
    private BucketAccelerateConfiguration accelerateConfiguration;

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Constructs a new {@link SetBucketAccelerateConfigurationRequest} to set
     * the bucket accelerate configuration of the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket whose accelerate configuration is being
     *            set.
     * @param configuration
     *            The new accelerate configuration for the specified bucket.
     */
    public SetBucketAccelerateConfigurationRequest(
            String bucketName, BucketAccelerateConfiguration configuration) {
        this.bucketName = bucketName;
        this.accelerateConfiguration = configuration;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public SetBucketAccelerateConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * @return The name of the bucket whose accelerate configuration is being
     *         set.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket whose accelerate configuration is being set.
     *
     * @param bucketName
     *            The name of the bucket whose accelerate configuration is being
     *            set.
     *
     * @see SetBucketAccelerateConfigurationRequest#getBucketName()
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Fluent setter method for {@link #setBucketName(String)}
     *
     * @return This {@link SetBucketAccelerateConfigurationRequest} object so that
     *         additional method calls may be chained together.
     *
     * @see #setBucketName(String)
     */
    public SetBucketAccelerateConfigurationRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * @return The new accelerate configuration for the specified bucket.
     */
    public BucketAccelerateConfiguration getAccelerateConfiguration() {
        return accelerateConfiguration;
    }

    /**
     * Sets the new accelerate configuration for the specified bucket.
     *
     * @param accelerateConfiguration
     *            The new accelerate configuration for the specified bucket.
     */
    public void setAccelerateConfiguration(
            BucketAccelerateConfiguration accelerateConfiguration) {
        this.accelerateConfiguration = accelerateConfiguration;
    }

    /**
     * Fluent setter method for {@link #setAccelerateConfiguration(BucketAccelerateConfiguration)}
     *
     * @return This {@link SetBucketAccelerateConfigurationRequest} object so that
     *         additional method calls may be chained together.
     *
     * @see #setAccelerateConfiguration(BucketAccelerateConfiguration)
     */
    public SetBucketAccelerateConfigurationRequest withAccelerateConfiguration(
            BucketAccelerateConfiguration accelerateConfiguration) {
        setAccelerateConfiguration(accelerateConfiguration);
        return this;
    }

}
