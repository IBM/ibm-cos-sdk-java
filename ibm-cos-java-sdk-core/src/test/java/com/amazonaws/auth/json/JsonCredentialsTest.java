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
package com.ibm.cloud.object.storage.auth.json;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.auth.JsonCredentials;
import com.amazonaws.auth.json.JsonConfigFile;
import com.amazonaws.auth.json.JsonResourceLoader;

import static org.junit.Assert.fail;

import java.io.File;

public class JsonCredentialsTest {

    
    /**
     * Tests loading json credentials with Access Key not specified.
     * OAuth credentials should be used.
     */
    @Test
    public void testCredentialsWithAccessKeyNotSpecified() throws Exception {
    	File resource = JsonResourceLoader.accessKeyNotSpecified().asFile();
    	
    	JsonConfigFile configFile = new JsonConfigFile(resource);
        JsonCredentials credentials = (JsonCredentials) configFile.getCredentials();
        Assert.assertEquals("vibm_api_key_id", credentials.getApiKey());
        Assert.assertEquals("vibm_service_instance_id", credentials.getServiceInstanceId());
    }
    
    /**
     * Tests loading json credentials with empty Access.
     * OAuth credentials should be used.
     */
    @Test
    public void testCredentialsWithEmptyAccessKey() throws Exception {
    	File resource = JsonResourceLoader.credentialsWithEmptyAccessKey().asFile();
    	
    	JsonConfigFile configFile = new JsonConfigFile(resource);
        JsonCredentials credentials = (JsonCredentials) configFile.getCredentials();
        Assert.assertEquals("vibm_api_key_id", credentials.getApiKey());
        Assert.assertEquals("vibm_service_instance_id", credentials.getServiceInstanceId());
    }
    
    /**
     * Tests loading json credentials with secret key not specified.
     * OAuth credentials should be used.
     */
    @Test
    public void testCredentialsWithEmptySecretKey() throws Exception {
    	File resource = JsonResourceLoader.credentialsWithEmptySecretKey().asFile();
    	
    	JsonConfigFile configFile = new JsonConfigFile(resource);
        JsonCredentials credentials = (JsonCredentials) configFile.getCredentials();
        Assert.assertEquals("vibm_api_key_id", credentials.getApiKey());
        Assert.assertEquals("vibm_service_instance_id", credentials.getServiceInstanceId());
    }
    
    /**
     * Tests loading json credentials with secret key not specified.
     * OAuth credentials should be used.
     */
    @Test
    public void testCredentialsWithHmacAndOAuthNotSpecified() throws Exception {
    	File resource = JsonResourceLoader.credentialsWithHmacAndOAuthNotSpecified().asFile();
    	
    	try {
        	new JsonConfigFile(resource);
    		fail("Should not create config file if no credentials in source.");
    	} catch(IllegalArgumentException expected) {
    	}

    }

}
