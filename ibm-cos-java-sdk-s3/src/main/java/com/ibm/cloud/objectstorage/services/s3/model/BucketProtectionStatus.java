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
 
/**
 * Represents the current protection status for a bucket.
 * @see BucketProtectionConfiguration
 */
public enum BucketProtectionStatus {
 
    Retention("Retention"),
    Disabled("Disabled");
 
    public static BucketProtectionStatus fromValue(String statusString) throws IllegalArgumentException {
        for (BucketProtectionStatus protectionStatus : BucketProtectionStatus.values()) {
            if (protectionStatus.toString().equals(statusString)) {
                return protectionStatus;
            }
        }
 
        throw new IllegalArgumentException("Cannot create enum from " + statusString + " value!");
    }
 
    private final String protectionStatus;
 
    private BucketProtectionStatus(String status) {
        this.protectionStatus = status;
    }
 
    @Override
    public String toString() {
        return protectionStatus;
    }
}
