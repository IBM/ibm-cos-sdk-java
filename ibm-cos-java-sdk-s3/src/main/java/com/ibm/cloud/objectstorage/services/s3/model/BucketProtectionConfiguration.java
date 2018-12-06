/* 
 * (C) Copyright 2018 IBM Corp. All Rights Reserved. 
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
 
/**
 * Represents the protection configuration for a bucket.
 */
public class BucketProtectionConfiguration implements Serializable {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -6828176835100426767L;
	
	/** The current protection configuration status */
    private String status;
    private Integer minimumRetentionInDays;
    private Integer defaultRetentionInDays;
    private Integer maximumRetentionInDays;
    private Boolean permanentRetentionEnabled;
    
    public BucketProtectionConfiguration() {}
    
    
     
    public BucketProtectionConfiguration(String status, Integer minimumRetentionInDays, Integer defaultRetentionInDays,
			Integer maximumRetentionInDays, Boolean permanentRetentionEnabled) {
		super();
		this.status = status;
		this.minimumRetentionInDays = minimumRetentionInDays;
		this.defaultRetentionInDays = defaultRetentionInDays;
		this.maximumRetentionInDays = maximumRetentionInDays;
		this.permanentRetentionEnabled = permanentRetentionEnabled;
	}

	/**
     * @return The current status of the protection configuration for this bucket, or null if the
     *         status has not been configured.
     */
    public String getStatus() {
        return status;
    }
 
    /**
     * Sets the desired bucket protection status for this configuration object.
     *
     * @param status
     *            The desired bucket protection status for this configuration object. See
     *            {@link BucketProtectionStatus} for a list of valid values.
     */
    public void setStatus(String status) {
        this.status = status;
    }
 
    /**
     * Sets the desired bucket protection status for this configuration object.
     *
     * @param status
     *            The desired bucket protection status for this configuration object. See
     *            {@link BucketProtectionStatus} for a list of valid values.
     */
    public void setStatus(BucketProtectionStatus status) {
        setStatus(status.toString());
    }
    
    /**
     * Fluent setter method for {@link #setStatus(String)}
     *
     * @return This {@link BucketProtectionConfiguration} object so that additional method calls may
     *         be chained together.
     * @see #setStatus(String)
     */
    public BucketProtectionConfiguration withStatus(BucketProtectionStatus status) {
        setStatus(status);
        return this;
    }
 
    /**
     * @return The current minimum retention of the protection configuration for this bucket, or null if the
     *         minimum retention has not been configured.
     */
    public Integer getMinimumRetentionInDays() {
        return minimumRetentionInDays;
    }
 
    /**
     * Sets the desired bucket protection minimum retention for this configuration object.
     *
     * @param minimumRetentionInDays
     *            The desired bucket protection minimum retention for this configuration object.
     */
    public void setMinimumRetentionInDays(Integer minimumRetentionInDays) {
        this.minimumRetentionInDays = minimumRetentionInDays;
    }
 
    /**
     * Fluent setter method for {@link #setMinimumRetentionInDays(Integer)}
     *
     * @return This {@link BucketProtectionConfiguration} object so that additional method calls may
     *         be chained together.
     * @see #setMinimumRetentionInDays(Integer)
     */
    public BucketProtectionConfiguration withMinimumRetentionInDays(Integer minimumRetentionInDays) {
        this.minimumRetentionInDays = minimumRetentionInDays;
        return this;
    }
     
    /**
     * @return The current default retention of the protection configuration for this bucket, or null if the
     *         default retention has not been configured.
     */
    public Integer getDefaultRetentionInDays() {
        return defaultRetentionInDays;
    }
 
    /**
     * Sets the desired bucket protection default retention for this configuration object.
     *
     * @param defaultRetentionInDays
     *            The desired bucket protection default retention for this configuration object.
     */
    public void setDefaultRetentionInDays(Integer defaultRetentionInDays) {
        this.defaultRetentionInDays = defaultRetentionInDays;
    }
 
    /**
     * Fluent setter method for {@link #setDefaultRetentionInDays(Integer)}
     *
     * @return This {@link BucketProtectionConfiguration} object so that additional method calls may
     *         be chained together.
     * @see #setDefaultRetentionInDays(Integer)
     */
    public BucketProtectionConfiguration withDefaultRetentionInDays(Integer defaultRetentionInDays) {
        this.defaultRetentionInDays = defaultRetentionInDays;
        return this;
    }
     
    /**
     * @return The current maximum retention of the protection configuration for this bucket, or null if the
     *         maximum retention has not been configured.
     */
    public Integer getMaximumRetentionInDays() {
        return maximumRetentionInDays;
    }
 
    /**
     * Sets the desired bucket protection maximum retention for this configuration object.
     *
     * @param maximumRetentionInDays
     *            The desired bucket protection maximum retention for this configuration object.
     */
    public void setMaximumRetentionInDays(Integer maximumRetentionInDays) {
        this.maximumRetentionInDays = maximumRetentionInDays;
    }
 
    /**
     * Fluent setter method for {@link #setMaximumRetentionInDays(Integer)}
     *
     * @return This {@link BucketProtectionConfiguration} object so that additional method calls may
     *         be chained together.
     * @see #setMaximumRetentionInDays(Integer)
     */
    public BucketProtectionConfiguration withMaximumRetentionInDays(Integer maximumRetentionInDays) {
        this.maximumRetentionInDays = maximumRetentionInDays;
        return this;
    }

    /**
     * @return Whether or not permanent retention is enabled on the bucket.
     */
	public Boolean isPermanentRetentionEnabled() {
		return permanentRetentionEnabled;
	}

	/**
     * Sets permanent retention true or false on the bucket.
     *
     * @param enablePermanentRetention
     *            Sets permanent retention enablement.
     */
	public void setPermanentRetentionEnabled(Boolean permanentRetentionEnabled) {
		this.permanentRetentionEnabled = permanentRetentionEnabled;
	}
    
	/**
     * Fluent setter method for {@link #setEnablePermanentRetention(Boolean)}
     *
     * @return This {@link BucketProtectionConfiguration} object so that additional method calls may
     *         be chained together.
     * @see #setPermanentRetentionEnabled(Boolean)
     */
	public BucketProtectionConfiguration withPermanentRetentionEnabled(Boolean permanentRetentionEnabled) {
		this.permanentRetentionEnabled = permanentRetentionEnabled;
		return this;
	}
}
