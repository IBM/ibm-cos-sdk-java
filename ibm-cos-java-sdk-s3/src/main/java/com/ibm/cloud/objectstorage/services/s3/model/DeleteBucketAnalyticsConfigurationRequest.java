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

import java.io.Serializable;

/**
 * Request object to delete an analytics configuration.
 */
public class DeleteBucketAnalyticsConfigurationRequest extends AmazonWebServiceRequest implements Serializable, ExpectedBucketOwnerRequest {

    private String bucketName;
    private String id;
    private String expectedBucketOwner;

    public DeleteBucketAnalyticsConfigurationRequest() { }

    public DeleteBucketAnalyticsConfigurationRequest(String bucketName, String id) {
        this.bucketName = bucketName;
        this.id = id;
    }

    public String getExpectedBucketOwner() {
        return expectedBucketOwner;
    }

    public DeleteBucketAnalyticsConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    public void setExpectedBucketOwner(String expectedBucketOwner) {
        withExpectedBucketOwner(expectedBucketOwner);
    }

    /**
     * Returns the name of the bucket from which an analytics configuration is deleted.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket from which an analytics configuration is deleted.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of the bucket from which an analytics configuration is deleted
     * and returns this object for method chaining.
     */
    public DeleteBucketAnalyticsConfigurationRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * Returns the identifier used to represent an analytics configuration.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier used to represent an analytics configuration.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the identifier used to represent an analytics configuration
     * and returns this object for method chaining.
     */
    public DeleteBucketAnalyticsConfigurationRequest withId(String id) {
        setId(id);
        return this;
    }
}
