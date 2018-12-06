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
 
/**
 * Model class representing a legal hold for an object.
 */
public class LegalHold implements Serializable {
 
    /** creation date for the legal hold */
    private Date date;
 
    /** legal hold id */
    private String id;
 
    /**
     * Returns the date for this {@link LegalHold}.
     *
     * @return Date for this {@link LegalHold}
     */
    public Date getDate() {
        return date;
    }
 
    /**
     * Sets the date for this {@link LegalHold}.
     *
     * @param date
     *            New date for this {@link LegalHold}.
     */
    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("LegalHold Date is a required argument");
        }
        this.date = date;
    }
 
    /**
     * Sets the date for this {@link LegalHold} and returns this object for method chaining.
     *
     * @param date
     *            New date for this {@link LegalHold}.
     * @return This object for method chaining
     */
    public LegalHold withName(Date date) {
        setDate(date);
        return this;
    }
 
    /**
     * Returns the id for this {@link LegalHold}
     *
     * @return Id for this {@link LegalHold}
     */
    public String getId() {
        return id;
    }
 
    /**
     * Sets the id for this {@link LegalHold}
     *
     * @param id
     *            New id for this {@link LegalHold}
     */
    public void setId(String id) {
        this.id = id;
    }
 
    /**
     * Sets the id for this {@link LegalHold} and returns this object for method chaining
     *
     * @param id
     *            New id for this {@link LegalHold}
     * @return This object for method chaining
     */
    public LegalHold withId(String id) {
        setId(id);
        return this;
    }
}