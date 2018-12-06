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
 * Request object containing all the options for deleting a legal hold from an object.
 *
 */
public class DeleteLegalHoldRequest extends AmazonWebServiceRequest implements Serializable {
 
    /**
     * The name of the bucket containing the reference to the object to delete a legal hold from.
     */
    private String bucketName;
 
    /**
     * The key, the name of the reference to the object to delete a legal hold from.
     */
    private String key;
 
    /**
     * The id of the legal hold to delete.
     */
    private String legalHoldId;
 
    /**
     * <p>
     * Constructs a new DeleteLegalHoldRequest.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket containing the reference to the object
     *            to delete a legal hold from.
     * @param key
     *            The key, the name of the reference to the object to delete a legal hold from.
     * @param legalHoldId
     *            The id of the legal hold to delete.
     *
     */
    public DeleteLegalHoldRequest(String bucketName, String key, String legalHoldId) {
        this.bucketName = bucketName;
        this.key = key;
        this.legalHoldId = legalHoldId;
    }
 
    /**
     * Returns the name of the bucket containing the reference to the object to
     * delete a legal hold from.
     *
     * @see DeleteLegalHoldRequest#setBucketName(String)
     * @see DeleteLegalHoldRequest#withBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }
 
    /**
     * Sets the name of the bucket containing the reference to the object to
     * delete a legal hold from, and returns a reference to this
     * object(DeleteLegalHoldRequest) for method chaining.
     *
     * @see DeleteLegalHoldRequest#setBucketName(String)
     * @see DeleteLegalHoldRequest#getBucketName()
     */
    public DeleteLegalHoldRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
 
    /**
     * Sets the name of the bucket containing the reference to the object to
     * delete a legal hold from.
     *
     * @see DeleteLegalHoldRequest#getBucketName()
     * @see DeleteLegalHoldRequest#withBucketName(String)
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
 
    /**
     * Gets the key, the name of the reference to the object to delete a legal hold from.
     *
     * @see DeleteLegalHoldRequest#setKey(String)
     * @see DeleteLegalHoldRequest#withKey(String)
     */
    public String getKey() {
        return key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to delete a legal hold from.
     *
     * @see DeleteLegalHoldRequest#getKey()
     * @see DeleteLegalHoldRequest#withKey(String)
     */
    public void setKey(String key) {
        this.key = key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to delete a legal hold from.
     * Returns a reference to this object(DeleteLegalHoldRequest) for method chaining.
     *
     * @see DeleteLegalHoldRequest#getKey()
     * @see DeleteLegalHoldRequest#setKey(String)
     */
    public DeleteLegalHoldRequest withKey(String key) {
        this.key = key;
        return this;
    }
 
    /**
     * Returns the legal hold id to be deleted.
     */
    public String getLegalHoldId() {
        return legalHoldId;
    }
 
    /**
     * Sets the legal hold id to be deleted.
     */
    public void setLegalHoldId(String legalHoldId) {
        this.legalHoldId = legalHoldId;
    }
 
    /**
     * Sets the legal hold id to be deleted and returns a reference to
     * this object for method chaining.
     */
    public DeleteLegalHoldRequest withLegalHoldId(String legalHoldId) {
        this.legalHoldId = legalHoldId;
        return this;
    }
}