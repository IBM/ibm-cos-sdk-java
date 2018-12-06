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
 * Request object containing all the options for adding a legal hold to an object.
 *
 */
public class AddLegalHoldRequest extends AmazonWebServiceRequest implements Serializable {
 
    /**
     * The name of the bucket containing the reference to the object to add a legal hold to.
     */
    private String bucketName;
 
    /**
     * The key, the name of the reference to the object to add a legal hold to.
     */
    private String key;
 
    /**
     * If enabled, the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private String legalHoldId;
 
    /**
     * <p>
     * Constructs a new AddLegalHoldRequest.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket containing the reference to the object
     *            to add a legal hold to.
     * @param key
     *            The key, the name of the reference to the object to add a legal hold to.
     * @param legalHoldId
     *            The id of the legal hold to add.
     *
     */
    public AddLegalHoldRequest(String bucketName, String key, String legalHoldId) {
        this.bucketName = bucketName;
        this.key = key;
        this.legalHoldId = legalHoldId;
    }
 
    /**
     * Returns the name of the bucket containing the reference to the object to
     * add a legal hold to.
     *
     * @see AddLegalHoldRequest#setBucketName(String)
     * @see AddLegalHoldRequest#withBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }
 
    /**
     * Sets the name of the bucket containing the reference to the object to
     * add a legal hold to, and returns a reference to this
     * object(AddLegalHoldRequest) for method chaining.
     *
     * @see AddLegalHoldRequest#setBucketName(String)
     * @see AddLegalHoldRequest#getBucketName()
     */
    public AddLegalHoldRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
 
    /**
     * Sets the name of the bucket containing the reference to the object to
     * add a legal hold to.
     *
     * @see AddLegalHoldRequest#getBucketName()
     * @see AddLegalHoldRequest#withBucketName(String)
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
 
    /**
     * Gets the key, the name of the reference to the object to add a legal hold to.
     *
     * @see AddLegalHoldRequest#setKey(String)
     * @see AddLegalHoldRequest#withKey(String)
     */
    public String getKey() {
        return key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to add a legal hold to.
     *
     * @see AddLegalHoldRequest#getKey()
     * @see AddLegalHoldRequest#withKey(String)
     */
    public void setKey(String key) {
        this.key = key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to add a legal hold to.
     * Returns a reference to this object(AddLegalHoldRequest) for method chaining.
     *
     * @see AddLegalHoldRequest#getKey()
     * @see AddLegalHoldRequest#setKey(String)
     */
    public AddLegalHoldRequest withKey(String key) {
        this.key = key;
        return this;
    }
 
    /**
     * Returns the legal hold id to be added.
     */
    public String getLegalHoldId() {
        return legalHoldId;
    }
 
    /**
     * Sets the legal hold id to be added.
     */
    public void setLegalHoldId(String legalHoldId) {
        this.legalHoldId = legalHoldId;
    }
 
    /**
     * Sets the legal hold id to be added and returns a reference to
     * this object for method chaining.
     */
    public AddLegalHoldRequest withLegalHoldId(String legalHoldId) {
        this.legalHoldId = legalHoldId;
        return this;
    }
}
