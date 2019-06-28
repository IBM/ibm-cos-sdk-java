/* 
* Copyright 2019 IBM Corp. All Rights Reserved. 
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
 * Request to retrieve a listing of buckets from Cloud Object Storage.
 */
public class ListBucketsExtendedRequest extends AmazonWebServiceRequest implements Serializable {
    private static final long serialVersionUID = 7228466262563220189L;

    /**
     * Optional parameter indicating the maximum number of keys to include in
     * the response. COS S3 might return fewer than this, but will not return
     * more.
     */
    private Integer maxKeys;

    /**
     * Optional parameter restricting the response to keys which begin with the
     * specified prefix.
     */
    private String prefix;

    /**
     * Optional parameter indicating where to begin listing. The
     * list will only include keys that occur lexicographically after the
     * marker. This enables pagination; to get the next page of results use the
     * current value as the marker for the next request to list buckets.
     */
    private String marker;

    /**
     * Constructs a new {@link ListBucketsExtendedRequest} object and
     * initializes all required and optional object fields.
     *
     * @param prefix
     *            The prefix restricting what keys will be listed.
     * @param marker
     *            The key marker indicating where listing results should begin.
     * @param maxKeys
     *            The maximum number of results to return.
     *
     * @see ListBucketsExtendedRequest#ListBucketsExtendedRequest()
     */
    public ListBucketsExtendedRequest(String prefix, String marker, Integer maxKeys) {
        this.prefix = prefix;
        this.marker = marker;
        this.maxKeys = maxKeys;
    }

    /**
     * Constructs a new {@link ListBucketsExtendedRequest} object 
     *
     * @see ListObjectsRequest#ListObjectsRequest()
     */
    public ListBucketsExtendedRequest() {

    }

    /**
     * Gets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response. COS S3 might return fewer keys than specified, but will
     * never return more.
     *
     * @return The optional parameter indicating the maximum number of keys to
     *         include in the response.
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     */
    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     * Returns this {@link ListBucketsExtendedRequest}, enabling additional method
     * calls to be chained together.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     *
     * @return This {@link ListBucketsExtendedRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see ListBucketsExtendedRequest#getMaxKeys()
     * @see ListBucketsExtendedRequest#setMaxKeys(Integer)
     */
    public ListBucketsExtendedRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    /**
     * Gets the optional prefix parameter and restricts the response to keys
     * that begin with the specified prefix. Use prefixes to separate a
     * bucket into different sets of keys, similar to how a file system organizes files
     * into directories.
     *
     * @return The optional prefix parameter restricting the response to keys
     *         that begin with the specified prefix.
     *
     * @see ListBucketsExtendedRequest#setPrefix(String)
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the optional prefix parameter, restricting the response to keys that
     * begin with the specified prefix.
     *
     * @param prefix
     *            The optional prefix parameter, restricting the response to keys
     *            that begin with the specified prefix.
     *
     * @see ListBucketsExtendedRequest#getPrefix()
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the optional prefix parameter restricting the response to keys that
     * begin with the specified prefix.
     * Returns this {@link ListBucketsExtendedRequest}, enabling additional method
     * calls to be chained together.
     *
     * @param prefix
     *            The optional prefix parameter restricting the response to keys
     *            that begin with the specified prefix.
     *
     * @return This {@link ListBucketsExtendedRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see ListBucketsExtendedRequest#getPrefix()
     * @see ListBucketsExtendedRequest#setPrefix(String)
     */
    public ListBucketsExtendedRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Gets the optional marker parameter indicating where in the bucket to begin
     * listing. The list will only include keys that occur lexicographically
     * after the marker.
     *
     * @return The optional marker parameter indicating where in the bucket to begin
     *         listing. The list will only include keys that occur
     *         lexicographically after the marker.
     *
     * @see ListBucketsExtendedRequest#setMarker(String)
     * @see ListBucketsExtendedRequest#withMarker(String)
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Sets the optional marker parameter indicating where to begin
     * listing. The list will only include keys that occur lexicographically
     * after the marker.
     *
     * @param marker
     *            The optional marker parameter indicating where in the bucket to begin
     *            listing. The list will only include keys that occur
     *            lexicographically after the marker.
     *
     * @see ListBucketsExtendedRequest#getMarker()
     * @see ListBucketsExtendedRequest#withMarker(String)
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * Sets the optional marker parameter indicating where to begin
     * listing.
     * Returns this {@link ListBucketsExtendedRequest}, enabling additional method
     * calls to be chained together.
     * The list will only include keys that occur lexicographically
     * after the marker.
     *
     * @param marker
     *            The optional parameter indicating where in the bucket to begin
     *            listing. The list will only include keys that occur
     *            lexicographically after the marker.
     *
     * @return This {@link ListBucketsExtendedRequest}, enabling additional method
     *         calls to be chained together.
     *
     * @see ListBucketsExtendedRequest#getMarker()
     * @see ListBucketsExtendedRequest#setMarker(String)
     */
    public ListBucketsExtendedRequest withMarker(String marker) {
        setMarker(marker);
        return this;
    }
}
