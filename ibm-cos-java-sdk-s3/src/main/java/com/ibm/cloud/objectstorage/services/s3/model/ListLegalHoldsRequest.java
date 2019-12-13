/* 
* Copyright 2018 IBM Corp. All Rights Reserved. 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with 
* the License. You may obtain a copy of the License at 
* 
* http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
* specific language governing permissions and limitations under the License. 
*/ 
package com.ibm.cloud.objectstorage.services.s3.model;
import java.io.Serializable;
 
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
 
/**
 * Request object containing all the options for requesting an object's legal holds.
 */
public class ListLegalHoldsRequest extends AmazonWebServiceRequest implements 
        WormMirrorDestinationProvider, Serializable {
    /** The name of the bucket which contains the specified object. */
    private String bucketName;
     
    /** The name of the specified object to perform the list against. */
    private String key;

    /**
     * Optional parameter setting the mirror-destination on a WORM enabled bucket.
     */
    private String wormMirrorDestination;
 
    /**
     * Constructs a new ListLegalHoldsRequest object.
     *
     * @param bucketName
     *            The name of the bucket which contains the specified object.
     */
    public ListLegalHoldsRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }
 
    /**
     * Returns the name of the bucket which contains the specified object.
     *
     * @return The name of the bucket which contains the specified object.
     */
    public String getBucketName() {
        return bucketName;
    }
     
    /**
     * Returns the name of the object to perform the list against.
     *
     * @return The name of the object to perform the list against.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the optional mirror-destination value for WORM mirroring
     *
     * @return The optional mirror-destination value
     */
    @Override
    public String getWormMirrorDestination() {
        return wormMirrorDestination;
    }

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

    /**
     * Sets the optional mirror-destination value for WORM mirroring
     * and returns the updated ListLegalHoldsRequest so that additional 
     * method calls may be chained together.
     *
     * @param wormMirrorDestination
     *            The optional mirror-destination value for WORM mirroring
     *
     * @return This {@link ListLegalHoldsRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListLegalHoldsRequest withWormMirrorDestination(String wormMirrorDestination) {
        setWormMirrorDestination(wormMirrorDestination);
        return this;
    }
}