/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * Request object for fetching the request payment configuration associated with
 * an Amazon S3 bucket.
 */
public class GetRequestPaymentConfigurationRequest extends
        AmazonWebServiceRequest implements WormMirrorDestinationProvider, Serializable {

    /** The name of the Amazon S3 bucket. */
    private String bucketName;

    // IBM-Specific
    /**
     * The optional destination-mirror value to use for WORM mirroring
     */
    private String wormMirrorDestination;

    public GetRequestPaymentConfigurationRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

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
     * and returns the updated GetRequestPaymentConfigurationRequest so that additional
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link GetRequestPaymentConfigurationRequest}, enabling additional method
     *         calls to be chained together.
     */
    public GetRequestPaymentConfigurationRequest withWormMirrorDestination(
            String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }
}
