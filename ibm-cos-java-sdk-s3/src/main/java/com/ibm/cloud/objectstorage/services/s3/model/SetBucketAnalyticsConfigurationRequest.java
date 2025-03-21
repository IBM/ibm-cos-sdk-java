/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.model.analytics.AnalyticsConfiguration;

import java.io.Serializable;

/**
 * Request object to set analytics configuration to a bucket.
 */
public class SetBucketAnalyticsConfigurationRequest extends AmazonWebServiceRequest implements Serializable
//IBM unsupported
//, ExpectedBucketOwnerRequest 
{

    private String bucketName;
    private AnalyticsConfiguration analyticsConfiguration;
    //IBM unsupported
    //private String expectedBucketOwner;

    public SetBucketAnalyticsConfigurationRequest() { }

    public SetBucketAnalyticsConfigurationRequest(String bucketName, AnalyticsConfiguration analyticsConfiguration) {
        this.bucketName = bucketName;
        this.analyticsConfiguration = analyticsConfiguration;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public SetBucketAnalyticsConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }

    /**
     * Returns the name of the bucket to which an analytics configuration is stored.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket to which an analytics configuration is stored.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of the bucket to which an analytics configuration is stored
     * and returns this object for method chaining.
     */
    public SetBucketAnalyticsConfigurationRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * Returns the {@link AnalyticsConfiguration} object.
     */
    public AnalyticsConfiguration getAnalyticsConfiguration() {
        return analyticsConfiguration;
    }

    /**
     * Sets the {@link AnalyticsConfiguration} object.
     */
    public void setAnalyticsConfiguration(AnalyticsConfiguration analyticsConfiguration) {
        this.analyticsConfiguration = analyticsConfiguration;
    }

    /**
     * Sets the {@link AnalyticsConfiguration} object and
     * returns this object for method chaining.
     */
    public SetBucketAnalyticsConfigurationRequest withAnalyticsConfiguration(AnalyticsConfiguration analyticsConfiguration) {
        setAnalyticsConfiguration(analyticsConfiguration);
        return this;
    }
}
