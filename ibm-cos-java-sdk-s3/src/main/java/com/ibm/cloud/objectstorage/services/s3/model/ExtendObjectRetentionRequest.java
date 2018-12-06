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
import java.util.Date;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
 
/**
 * Request object containing all the options for extending the retention period
 * of a protected object.
 *
 */
public class ExtendObjectRetentionRequest extends AmazonWebServiceRequest implements Serializable {
 
    /**
     * The name of the bucket containing the reference to the object to add a legal hold to.
     */
    private String bucketName;
 
    /**
     * The key, the name of the reference to the object to add a legal hold to.
     */
    private String key;
 
    /**
     * Additional Retention Period
     */
    private Long additionalRetentionPeriod;
    
    /**
     * Retention Period in seconds for the object.
     * The Retention will be enforced from the current time until current time + the value in this header.
     * This header will only be supported for objects that are currently stored with an indefinite retention period (-1).
     * This value has to be within the ranges defined for the bucket.
     */
    private Long extendRetentionFromCurrentTime;
    
    /**
     * New retention expiration date.
     */
    private Date newRetentionExpirationDate;
    
    /**
     * Retention period, in seconds, to use for the object in place of the existing retention period stored for the object.
     * If this value is less than the existing value stored for the object, a 400 error will be returned.
     * If this field and Additional-Retention-Period and/or New-Retention-Expiration-Date are specified, a 400 error will be returned.
     * If none of the Request Headers are specified, a 400 error will be returned.
     */
    private Long newRetentionPeriod;
    
    /**
     * <p>
     * Constructs a new ExtendObjectRetentionRequest.
     * </p>
     */
    public ExtendObjectRetentionRequest() {}
    
    /**
     * <p>
     * Constructs a new ExtendObjectRetentionRequest.
     * </p>
     * 
     * *
     * @param bucketName
     *            The name of the bucket containing the reference to the object
     *            to add a legal hold to.
     * @param key
     *            The key, the name of the reference to the object to add a legal hold to.
     */
    public ExtendObjectRetentionRequest(String bucketName, String key) {
    	super();
		this.bucketName = bucketName;
		this.key = key;
    }
 
    /**
     * <p>
     * Constructs a new ExtendObjectRetentionRequest.
     * </p>
     *
     * @param bucketName
     *            The name of the bucket containing the reference to the object
     *            to add a legal hold to.
     * @param key
     *            The key, the name of the reference to the object to add a legal hold to.
     * @param additionalRetentionPeriod
     *            The additional retention period in seconds.
     * @param extendRetentionFromCurrentTime
     * 			  The extended retention period in seconds.
     * @param newRetentionExpirationDate
     *            The new retention period.      
     * @param newRetentionPeriod
     *            The new retention period in seconds.
     */
    public ExtendObjectRetentionRequest(String bucketName, String key, Long additionalRetentionPeriod,
			Long extendRetentionFromCurrentTime, Date newRetentionExpirationDate, Long newRetentionPeriod) {
		super();
		this.bucketName = bucketName;
		this.key = key;
		this.additionalRetentionPeriod = additionalRetentionPeriod;
		this.extendRetentionFromCurrentTime = extendRetentionFromCurrentTime;
		this.newRetentionExpirationDate = newRetentionExpirationDate;
		this.newRetentionPeriod = newRetentionPeriod;
	}
 
    /**
     * Returns the name of the bucket containing the reference to the object to
     * add a legal hold to.
     *
     * @see ExtendObjectRetentionRequest#setBucketName(String)
     * @see ExtendObjectRetentionRequest#withBucketName(String)
     */
    public String getBucketName() {
        return bucketName;
    }

	/**
     * Sets the name of the bucket containing the reference to the object to
     * add a legal hold to, and returns a reference to this
     * object(AddLegalHoldRequest) for method chaining.
     *
     * @see ExtendObjectRetentionRequest#setBucketName(String)
     * @see ExtendObjectRetentionRequest#getBucketName()
     */
    public ExtendObjectRetentionRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
 
    /**
     * Sets the name of the bucket containing the reference to the object to
     * add a legal hold to.
     *
     * @see ExtendObjectRetentionRequest#getBucketName()
     * @see ExtendObjectRetentionRequest#withBucketName(String)
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
 
    /**
     * Gets the key, the name of the reference to the object to add a legal hold to.
     *
     * @see ExtendObjectRetentionRequest#setKey(String)
     * @see ExtendObjectRetentionRequest#withKey(String)
     */
    public String getKey() {
        return key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to add a legal hold to.
     *
     * @see ExtendObjectRetentionRequest#getKey()
     * @see ExtendObjectRetentionRequest#withKey(String)
     */
    public void setKey(String key) {
        this.key = key;
    }
 
    /**
     * Sets the key, the name of the reference to the object to add a legal hold to.
     * Returns a reference to this object(AddLegalHoldRequest) for method chaining.
     *
     * @see ExtendObjectRetentionRequest#getKey()
     * @see ExtendObjectRetentionRequest#setKey(String)
     */
    public ExtendObjectRetentionRequest withKey(String key) {
        this.key = key;
        return this;
    }

	public Long getAdditionalRetentionPeriod() {
		return additionalRetentionPeriod;
	}

	public void setAdditionalRetentionPeriod(Long additionalRetentionPeriod) {
		this.additionalRetentionPeriod = additionalRetentionPeriod;
	}
	
	public ExtendObjectRetentionRequest withAdditionalRetentionPeriod(Long additionalRetentionPeriod) {
		this.additionalRetentionPeriod = additionalRetentionPeriod;
		return this;
	}

	public Long getExtendRetentionFromCurrentTime() {
		return extendRetentionFromCurrentTime;
	}

	public void setExtendRetentionFromCurrentTime(Long extendRetentionFromCurrentTime) {
		this.extendRetentionFromCurrentTime = extendRetentionFromCurrentTime;
	}
	
	public ExtendObjectRetentionRequest withExtendRetentionFromCurrentTime(Long extendRetentionFromCurrentTime) {
		this.extendRetentionFromCurrentTime = extendRetentionFromCurrentTime;
		return this;
	}

	public Date getNewRetentionExpirationDate() {
		return newRetentionExpirationDate;
	}

	public void setNewRetentionExpirationDate(Date newRetentionExpirationDate) {
		this.newRetentionExpirationDate = newRetentionExpirationDate;
	}
	
	public ExtendObjectRetentionRequest withNewRetentionExpirationDate(Date newRetentionExpirationDate) {
		this.newRetentionExpirationDate = newRetentionExpirationDate;
		return this;
	}

	public Long getNewRetentionPeriod() {
		return newRetentionPeriod;
	}

	public void setNewRetentionPeriod(Long newRetentionPeriod) {
		this.newRetentionPeriod = newRetentionPeriod;
	}
	
	public ExtendObjectRetentionRequest withNewRetentionPeriod(Long newRetentionPeriod) {
		this.newRetentionPeriod = newRetentionPeriod;
		return this;
	}
}
