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
import java.util.ArrayList;
import java.util.List;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.model.replication.ContentList;
import com.ibm.cloud.objectstorage.util.json.Jackson;

public class ListBucketReplicationFailuresResult extends
        AmazonWebServiceRequest implements Serializable
        {

   /**
     * The name of Amazon S3 bucket to which has the bucket replication failures.
     */
    private String name;

   /**
     * If <code>ContinuationToken</code> was sent with the request, it is included in the response. 
     * You can use the returned <code>ContinuationToken</code> for pagination of the list response.
     * You can use this <code>ContinuationToken</code> for pagination of the list results.</p>
     */
    private String continuationToken;

   /**
     * Value provided via the <code> max-keys </code> request param. Maximum accepted value is 1000.
     */
    private Integer maxKeys;

   /**
     * Value provided via the <code> first-sync-attempted-before </code> request param.
     */
    private String firstSyncAttemptedBefore;

   /**
     * Encoding type, if specified in the request.
     */
    private String encodingType;
    
   /**
     * Whether or not the results are truncated.
     */
    private boolean isTruncated;

   /**
     * <p><code>KeyCount</code> is the number of keys returned with this request. <code>KeyCount</code> will always be less than or equal to the <code>MaxKeys</code> field. 
     * For example, if you ask for 50 keys, your result will include 50 keys or fewer.</p>
     */
    private Integer keyCount;

   /**
     * NextContinuationToken is sent when isTruncated is true, Next continuation token. 
     * Present if this result was truncated
     */
    private String nextContinuationToken;

   /**
     * Metadata about each object returned.
     */
    private ContentList content;

    /**
     * Creates a new ListBucketReplicationFailuresResult.
     */
    public ListBucketReplicationFailuresResult() { }

    /**
     * Creates a new ListBucketReplicationFailuresResult.
     *
     * @param name
     *            The name of Amazon S3 bucket to which has the bucket replication failures..
     */
    public ListBucketReplicationFailuresResult(String bucketName) {
        this.name = bucketName;
    }

    /**
     * Returns the name of Amazon S3 bucket.
     */
    public String getBucketName() {
        return name;
    }

    /**
     * Sets the name of Amazon S3 bucket has the bucket replication failures.
     *
     * @param name
     *            The name of Amazon S3 bucket to which has the bucket replication failures.
     */
    public void setBucketName(String bucketName) {
        this.name = bucketName;
    }

    /**
     * Sets the name of Amazon S3 bucket that has the bucket replication failures. 
     *
     * @param name
     *            The name of Amazon S3 bucket to which has the bucket replication failures.
     * @return The updated {@link ListBucketReplicationFailuresResult}
     *         object.
     */
    public ListBucketReplicationFailuresResult withBucketName(
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
     * @return 
     */
    public ListBucketReplicationFailuresResult withContinuationToken(String continuationToken) {
        setContinuationToken(continuationToken);
        return this;
    }

    /**
      * Get whether or not the results are truncated.
     *
     */
    public boolean getIsTruncated() {
        return isTruncated;
    }

    /**
     * @param isTruncated
     *                     The optional continuation token to associate with this request.
     */
    public void setIsTruncated(Boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    /**
     * @param isTruncated
     *                     The optional continuation token to associate with this request.
     *
     * @return 
     */
    public ListBucketReplicationFailuresResult withIsTruncated(Boolean isTruncated) {
        setIsTruncated(isTruncated);
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
     * Returns this {@link ListBucketReplicationFailuresResult}, enabling additional method
     * calls to be chained together.
     *
     * @param maxKeys
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     *
     * @return This {@link ListBucketReplicationFailuresResult}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresResult withMaxKeys(Integer maxKeys) {
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
     * @return This {@link ListBucketReplicationFailuresResult}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresResult withFirstSyncAttemptedBefore(String firstSyncAttemptedBefore) {
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
     * Returns this {@link ListBucketReplicationFailuresResult}, enabling additional method calls
     * to be chained together.
     *
     * @param encodingType
     *            The encoding method to be applied on the response. Valid
     *            values: null (not encoded) or "url".
     */
    public ListBucketReplicationFailuresResult withEncodingType(String encodingType) {
        setEncodingType(encodingType);
        return this;
    }

   /**
     * KeyCount is the number of keys returned with this request. 
     * It will always be less than or equal to the MaxKeys field. 
     * For example, if you ask for 50 keys, your result will include 50 keys or fewer.
     *
     * @return The optional parameter indicating the maximum number of keys to
     *         include in the response.
     */
    public Integer getKeyCount() {
        return keyCount;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     *
     * @param keyCount
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     */
    public void setKeyCount(Integer keyCount) {
        this.keyCount = keyCount;
    }

    /**
     * Sets the optional <code>maxKeys</code> parameter indicating the maximum number of keys to
     * include in the response.
     * Returns this {@link ListBucketReplicationFailuresResult}, enabling additional method
     * calls to be chained together.
     *
     * @param keyCount
     *            The optional parameter indicating the maximum number of keys
     *            to include in the response.
     *
     * @return This {@link ListBucketReplicationFailuresResult}, enabling additional method
     *         calls to be chained together.
     */
    public ListBucketReplicationFailuresResult withSetKeyCount(Integer keyCount) {
        setKeyCount(keyCount);
        return this;
    }

    /**
     * Gets the optional next continuation token.
     *
     * @return 
     */
    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    /**
     * Sets the optional next continuation token.
     * @param nextContinuationToken
     */
    public void setNextContinuationToken(String nextContinuationToken) {
        this.nextContinuationToken = nextContinuationToken;
    }

    /**
     * Sets the optional next continuation token. 
     *
     * @param nextContinuationToken
     *
     * @return 
     */
    public ListBucketReplicationFailuresResult withNextContinuationToken(String nextContinuationToken) {
        setNextContinuationToken(nextContinuationToken);
        return this;
    }

    private List<ContentList> contents = new ArrayList<>();

    /**
     * Returns the list of ContentList objects representing failed replications.
     */
    public List<ContentList> getContents() {
        return contents;
    }

    /**
     * Sets the list of ContentList objects representing failed replications.
     */
    public void setContents(List<ContentList> contents) {
        this.contents = contents;
    }

    /**
     * Adds a ContentList item to the list.
     */
    public ListBucketReplicationFailuresResult withContent(ContentList content) {
        this.contents.add(content);
        return this;
    }

    @Override
        public String toString() {
            return Jackson.toJsonString(this);
    }
}
