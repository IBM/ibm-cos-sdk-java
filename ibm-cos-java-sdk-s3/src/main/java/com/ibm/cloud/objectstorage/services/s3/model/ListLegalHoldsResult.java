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
import java.util.List;
 
 
/**
 * Result object to contain the response returned from
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3Client#listLegalHolds(ListLegalHoldsRequest)}
 * operation.
 */
public class ListLegalHoldsResult implements Serializable {
 
    /** Creation time for the object */
    private Date createTime;
 
    /** A List of legal holds that have been added to the object */
    private List<LegalHold> legalHolds;
 
    /** Retention period for the object, in seconds */
    private long retentionPeriod;
 
    /** Date on which the retention period will expire */
    private Date retentionExpirationDate;
 
    /**
     * Gets creation time for the object/
     *
     * @return Creation time for the object
     */
    public Date getCreateTime() {
        return createTime;
    }
 
    /**
     * Sets the creation time for the object.
     *
     * @param createTime The time at which the object was created.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
 
    /**
     * Sets the creation time for the object and returns
     * {@link ListLegalHoldsResult} object for method chaining.
     *
     * @param createTime The creation time for the object
     * @return this object for method chaining
     */
    public ListLegalHoldsResult withCreateTime(Date createTime) {
        setCreateTime(createTime);
        return this;
    }
 
    /**
     * Gets the list of legal holds for the object.
     */
    public List<LegalHold> getLegalHolds() {
        return legalHolds;
    }
 
    /**
     * Sets the list of legal holds for the object.
     */
    public void setLegalHolds(List<LegalHold> legalHolds) {
        this.legalHolds = legalHolds;
    }
 
    /**
     * Sets the list of legal holds for the object and returns
     * {@link ListLegalHoldsResult} object for method chaining.
     */
    public ListLegalHoldsResult withLegalHolds(List<LegalHold> legalHolds) {
        setLegalHolds(legalHolds);
        return this;
    }
 
    /**
     * Gets the retention period for the object.
     *
     * @return the object's retention period
     */
    public long getRetentionPeriod() {
        return retentionPeriod;
    }
 
    /**
     * Sets the retention period for the object.
     *
     * @param retentionPeriod The object's retention period
     */
    public void setRetentionPeriod(long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }
 
    /**
     * Sets the retention period for the object and returns
     * {@link ListLegalHoldsResult} object for method chaining.
     *
     * @param retentionPeriod The object's retention period
     * @return this object for method chaining
     */
    public ListLegalHoldsResult withRetentionPeriod(long retentionPeriod) {
        setRetentionPeriod(retentionPeriod);
        return this;
    }
 
    /**
     * Gets the retention expiration date for the object.
     *
     * @return The object's retention expiration date
     */
    public Date getRetentionExpirationDate() {
        return retentionExpirationDate;
    }
 
    /**
     * Sets the retention expiration date for the object.
     *
     * @param retentionExpirationDate The objects' retention expiration date
     */
    public void setRetentionExpirationDate(Date retentionExpirationDate) {
        this.retentionExpirationDate = retentionExpirationDate;
    }
 
    /**
     * Sets the retention expiration date for the object and returns
     * {@link ListLegalHoldsResult} object for method chaining.
     *
     * @param retentionExpirationDate The object's retention expiration date
     * @return this object for method chaining
     */
    public ListLegalHoldsResult withExpirationDate(Date retentionExpirationDate) {
        setRetentionExpirationDate(retentionExpirationDate);
        return this;
    }
}