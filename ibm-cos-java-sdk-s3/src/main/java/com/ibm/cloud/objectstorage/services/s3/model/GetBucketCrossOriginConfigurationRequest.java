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
 * Request object for the parameters to get a bucket's cross origin configuration.
 *
 * @see AmazonS3#getBucketCrossOriginConfiguration(GetBucketCrossOriginConfigurationRequest)
 */
public class GetBucketCrossOriginConfigurationRequest extends GenericBucketRequest implements
        WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest
        {

    //IBM unsupported
    //private String expectedBucketOwner;
    /**
     * Creates a request object, ready to be executed to fetch the cross origin
     * configuration of the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket whose cross origin configuration is
     *            being fetched.
     */
    public GetBucketCrossOriginConfigurationRequest(String bucketName) {
        super(bucketName);
    }

    // IBM-Specific
    /**
     * Optional parameter setting the mirror-destination on a WORM enabled bucket.
     */
    private String wormMirrorDestination;

    // IBM-Specific
    /**
     * Returns the optional mirror-destination value for WORM mirroring
     *
     * @return The optional mirror-destination value
     */
    @Override
    public String getWormMirrorDestination() {
        return wormMirrorDestination;
    }

    // IBM-Specific
    /**
     * Sets the optional mirror-destination value for WORM mirroring
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     */
    @Override
    public void setWormMirrorDestination(String wormMirrorDestination) {
        this.wormMirrorDestination = wormMirrorDestination;
    }

    // IBM-Specific
    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * and returns the updated GetBucketCrossOriginConfigurationRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link GetBucketCrossOriginConfigurationRequest}, enabling additional method
     *         calls to be chained together.
     */
    public GetBucketCrossOriginConfigurationRequest withWormMirrorDestination(String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public GetBucketCrossOriginConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
//        this.expectedBucketOwner = expectedBucketOwner;
//        return this;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }
}
