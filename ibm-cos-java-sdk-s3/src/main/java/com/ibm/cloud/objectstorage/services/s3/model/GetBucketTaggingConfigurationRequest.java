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
 * Request object for the parameters to get a bucket's tagging configuration.
 *
 * @see AmazonS3#getBucketTaggingConfiguration(GetBucketTaggingConfigurationRequest)
 */
public class GetBucketTaggingConfigurationRequest extends GenericBucketRequest implements
        WormMirrorDestinationProvider, Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest
        {

    //IBM unsupported
    //private String expectedBucketOwner;

    /**
     * Creates request object, ready to be executed to fetch the tagging
     * configuration for the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket whose tagging configuration is being
     *            fetched.
     */
    public GetBucketTaggingConfigurationRequest(String bucketName) {
        super(bucketName);
    }

    // IBM-Specific
    /**
     * The optional destination-mirror value to use for WORM mirroring
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
     * and returns the updated GetBucketTaggingConfigurationRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link GetBucketTaggingConfigurationRequest}, enabling additional method
     *         calls to be chained together.
     */
    public GetBucketTaggingConfigurationRequest withWormMirrorDestination(String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }

//IBM unsupported
//    public String getExpectedBucketOwner() {
//        return expectedBucketOwner;
//    }
//
//    public void setExpectedBucketOwner(String expectedBucketOwner) {
//        withExpectedBucketOwner(expectedBucketOwner);
//    }
}
