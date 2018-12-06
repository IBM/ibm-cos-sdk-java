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

/**
 * Request object for the parameters to get a bucket's protection configuration.
 *
 * @see AmazonS3#getBucketProtectionConfiguration(GetBucketProtectionConfigurationRequest)
 */
public class GetBucketProtectionConfigurationRequest extends GenericBucketRequest implements Serializable {
 
    /**
     * Creates a request object, ready to be executed to fetch the protection
     * configuration of the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket whose protection configuration is
     *            being fetched.
     */
    public GetBucketProtectionConfigurationRequest(String bucketName) {
        super(bucketName);
    }
 
}
