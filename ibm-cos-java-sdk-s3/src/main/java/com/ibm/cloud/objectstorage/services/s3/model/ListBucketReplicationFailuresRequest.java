/* 
* Copyright 2025 IBM Corp. All Rights Reserved. 
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
import com.ibm.cloud.objectstorage.util.json.Jackson;

public class ListBucketReplicationFailuresRequest extends
        AmazonWebServiceRequest implements Serializable
        //IBM unsupported
        //, ExpectedBucketOwnerRequest 
        {

    /**
     * The name of Amazon S3 bucket to which the replication configuration is
     * set.
     */
    private String bucketName;

    /**
     * Obfuscated string token for pagination. This is returned on incomplete listing responses.
     */
    private String continuationToken;

    /**
     * Maximum number of entries to return (default 1000).
     */
    private Integer maxKeys;

    /**
     * Epoch ms time from which to start the listing (inclusive if time exactly matches an entry). 
     * The failures are sorted by the time at which the syncs were originally triggered on the source bucket.
     */
    private String firstSyncAttemptedBefore;

    /**
     * Encoding type to use for Key. Only valid value is url.
     */
    private String encodingType;
 
    /**
     * Creates a new ListBucketReplicationFailuresRequest.
     */
    public ListBucketReplicationFailuresRequest() { }

    /**
     * Creates a new ListBucketReplicationFailuresRequest.
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            configuration is set.
     */
    public ListBucketReplicationFailuresRequest(String bucketName) {
        this.bucketName = bucketName;
    }



    /**
     * Returns the name of Amazon S3 bucket.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of Amazon S3 bucket for replication configuration.
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            configuration is set.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of Amazon S3 bucket for replication configuration. Returns
     * the updated object. .
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            configuration is set.
     * @return The updated {@link ListBucketReplicationFailuresRequest}
     *         object.
     */
    public ListBucketReplicationFailuresRequest withBucketName(
            String bucketName) {
        setBucketName(bucketName);
        return this;
    }
     /**
     * Gets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @return The optional continuation token associated with this request.
     */
    public String getContinuationToken() {
        return continuationToken;
    }

    /**
     * Sets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @param continuationToken
     *                     The optional continuation token to associate with this request.
     */
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    /**
     * Sets the optional continuation token.  Continuation token allows a list to be
     * continued from a specific point. ContinuationToken is provided in truncated list results.
     *
     * @param continuationToken
     *                     The optional continuation token to associate with this request.
     *
     * @return This {@link ListBucketAnalyticsConfigurationsRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresRequest withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
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
     * Returns this {@link ListBucketReplicationFailuresRequest}, enabling additional method
     * calls to be chained together.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     *
     * @return This {@link ListBucketReplicationFailuresRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

       /**
     * Returns optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @return the optional firstSyncAttemptedBefore parameter
     */
    public String getFirstSyncAttemptedBefore() { return firstSyncAttemptedBefore; }

    /**
     * Sets the optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @param firstSyncAttemptedBefore
     *                The optional firstSyncAttemptedBefore parameter.  This can be any key in the bucket.
     */
    public void setFirstSyncAttemptedBefore(String firstSyncAttemptedBefore) { this.firstSyncAttemptedBefore = firstSyncAttemptedBefore; }

    /**
     * Sets the optional parameter indicating where you want Amazon S3 to start the object
     * listing from.  This can be any key in the bucket.
     *
     * @param firstSyncAttemptedBefore
     *                The optional firstSyncAttemptedBefore parameter.  This can be any key in the bucket.
     *
     * @return This {@link ListBucketReplicationFailuresRequest}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresRequest withFirstSyncAttemptedBefore(String firstSyncAttemptedBefore) {
        setFirstSyncAttemptedBefore(firstSyncAttemptedBefore);
        return this;
    }

    /**
     * Gets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response.
     *
     * @return The encoding method to be applied on the response.
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * Sets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response. An object key can contain
     * any Unicode character; however, XML 1.0 parser cannot parse some
     * characters, such as characters with an ASCII value from 0 to 10. For
     * characters that are not supported in XML 1.0, you can add this parameter
     * to request that Amazon S3 encode the keys in the response.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * Sets the optional <code>encodingType</code> parameter indicating the
     * encoding method to be applied on the response. An object key can contain
     * any Unicode character; however, XML 1.0 parser cannot parse some
     * characters, such as characters with an ASCII value from 0 to 10. For
     * characters that are not supported in XML 1.0, you can add this parameter
     * to request that Amazon S3 encode the keys in the response.
     * Returns this {@link ListBucketReplicationFailuresRequest}, enabling additional method calls
     * to be chained together.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public ListBucketReplicationFailuresRequest withEncodingType(String encodingType) {
        setEncodingType(encodingType);
        return this;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
