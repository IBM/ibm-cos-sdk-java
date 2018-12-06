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
 
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.services.s3.model.analytics.AnalyticsConfiguration;
 
import java.io.Serializable;
 
/**
 * Request object to set protection configuration to a bucket.
 */
public class SetBucketProtectionConfigurationRequest extends AmazonWebServiceRequest implements Serializable {
 
    private String bucketName;
    private BucketProtectionConfiguration protectionConfiguration;
 
    public SetBucketProtectionConfigurationRequest() { }
 
    public SetBucketProtectionConfigurationRequest(String bucketName, BucketProtectionConfiguration protectionConfiguration) {
        this.bucketName = bucketName;
        this.protectionConfiguration = protectionConfiguration;
    }
 
    /**
     * Returns the name of the bucket to which a protection configuration is stored.
     */
    public String getBucketName() {
        return bucketName;
    }
 
    /**
     * Sets the name of the bucket to which a protection configuration is stored.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
 
    /**
     * Sets the name of the bucket to which a protection configuration is stored
     * and returns this object for method chaining.
     */
    public SetBucketProtectionConfigurationRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }
 
    /**
     * Returns the {@link BucketProtectionConfiguration} object.
     */
    public BucketProtectionConfiguration getProtectionConfiguration() {
        return protectionConfiguration;
    }
 
    /**
     * Sets the {@link BucketProtectionConfiguration} object.
     */
    public void setProtectionConfiguration(BucketProtectionConfiguration protectionConfiguration) {
        this.protectionConfiguration = protectionConfiguration;
    }
 
    /**
     * Sets the {@link BucketProtectionConfiguration} object and
     * returns this object for method chaining.
     */
    public SetBucketProtectionConfigurationRequest withProtectionConfiguration(BucketProtectionConfiguration protectionConfiguration) {
        setProtectionConfiguration(protectionConfiguration);
        return this;
    }
}
