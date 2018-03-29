/* 
* Copyright 2017 IBM Corp. All Rights Reserved. 
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
package com.ibm.cloud.objectstorage.auth.json.internal;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;

/**
 * Keys the Java SDK uses in the JSON credentials and config files.
 */
@SdkInternalApi
public class JsonKeyConstants {
	
	/**
     * Property name for specifying the HMAC Keys
     */
    public static final String IBM_HMAC_KEYS = "cos_hmac_keys";

    /**
     * Property name for specifying the Access Key
     */
    public static final String IBM_ACCESS_KEY_ID = "access_key_id";

    /**
     * Property name for specifying the Secret Access Key
     */
    public static final String IBM_SECRET_ACCESS_KEY = "secret_access_key";
    
    /**
     * Property name for specifying the API Key
     */
    public static final String IBM_API_KEY = "apikey";
    
    /**
     * Property name for specifying the Resource Instance Id
     */
    public static final String IBM_RESOURCE_INSTANCE_ID = "resource_instance_id";
    
    
}
