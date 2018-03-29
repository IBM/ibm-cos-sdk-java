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
package com.ibm.cloud.objectstorage.auth.json;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.cloud.objectstorage.auth.JsonCredentials;
import com.ibm.cloud.objectstorage.auth.json.JsonCredentialsProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonCredentialsProviderTest {

    private static File credentialsLocation = null;

    @BeforeClass
    public static void setUp() {
        credentialsLocation = JsonResourceLoader.credentialsContainingHmacAndOAuth().asFile();
    }

    private JsonCredentialsProvider newProvider() {
        return new JsonCredentialsProvider(credentialsLocation.getAbsolutePath());
    }

    @Test
    public void testHmac() {
        JsonCredentialsProvider provider = newProvider();

        JsonCredentials credentials = (JsonCredentials) provider.getCredentials();

        // When credentials file contains hmac and oauth credentials
        // only hmac should be exposed.
        // credentialsContainingHmacAndOAuth.json
        Assert.assertEquals("vaws_access_key_id", credentials.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key", credentials.getAWSSecretKey());
        Assert.assertEquals(null, credentials.getApiKey());
        Assert.assertEquals(null, credentials.getServiceInstanceId());
    }
    
    @Test
    public void testUpdate() throws Exception {
    	File modifiable = File.createTempFile("UpdatableCredentials", ".json");
    	copyFile(JsonResourceLoader.basicCredentials().asFile(), modifiable);
    	
    	JsonCredentialsProvider testProvider = new JsonCredentialsProvider(modifiable.getPath());
        JsonCredentials credentials = (JsonCredentials) testProvider.getCredentials();

        Assert.assertEquals("vaws_access_key_id", credentials.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key", credentials.getAWSSecretKey());
        
        //Sleep to ensure that the timestamp on the file (when we modify it) is
        //distinguishably later from the original write.
        Thread.sleep(2000);
        
        copyFile(JsonResourceLoader.basicCredentials2().asFile(), modifiable);
       
        testProvider.refresh();
        JsonCredentials updated = (JsonCredentials) testProvider.getCredentials();
        Assert.assertEquals("vaws_access_key_id_2", updated.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key_2", updated.getAWSSecretKey());
        
        modifiable.delete();
    }

    @Test
    public void testForcedRefresh() throws Exception {
    	File modifiable = File.createTempFile("UpdatableCredentials", ".json");
    	copyFile(JsonResourceLoader.basicCredentials().asFile(), modifiable);
    	
    	JsonCredentialsProvider testProvider = new JsonCredentialsProvider(modifiable.getPath());
        JsonCredentials credentials = (JsonCredentials) testProvider.getCredentials();

        Assert.assertEquals("vaws_access_key_id", credentials.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key", credentials.getAWSSecretKey());
        
        
        //Sleep to ensure that the timestamp on the file (when we modify it) is
        //distinguishably later from the original write.
        Thread.sleep(1000);
        
        copyFile(JsonResourceLoader.basicCredentials2().asFile(), modifiable);
        
        testProvider.setRefreshForceIntervalNanos(1l);
        JsonCredentials updated = (JsonCredentials) testProvider.getCredentials();
        Assert.assertEquals("vaws_access_key_id_2", updated.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key_2", updated.getAWSSecretKey());
        
        modifiable.delete();
    }
    
    @Test
    public void testRefresh() throws Exception {
    	File modifiable = File.createTempFile("UpdatableCredentials", ".json");
    	copyFile(JsonResourceLoader.basicCredentials().asFile(), modifiable);
    	
    	JsonCredentialsProvider testProvider = new JsonCredentialsProvider(modifiable.getPath());
        JsonCredentials credentials = (JsonCredentials) testProvider.getCredentials();

        Assert.assertEquals("vaws_access_key_id", credentials.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key", credentials.getAWSSecretKey());
        
        
        //Sleep to ensure that the timestamp on the file (when we modify it) is
        //distinguishably later from the original write.
        Thread.sleep(1000);
        
        copyFile(JsonResourceLoader.basicCredentials2().asFile(), modifiable);
        
        testProvider.setRefreshIntervalNanos(1l);
        JsonCredentials updated = (JsonCredentials) testProvider.getCredentials();
        Assert.assertEquals("vaws_access_key_id_2", updated.getAWSAccessKeyId());
        Assert.assertEquals("vaws_secret_access_key_2", updated.getAWSSecretKey());
    }

    private static void copyFile(File src, File dst) throws IOException {
        if(!dst.exists()) {
            dst.createNewFile();
        }

        InputStream in = new FileInputStream(src);
	    try {
	        OutputStream out = new FileOutputStream(dst);
	        try {
	            // Transfer bytes from in to out
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	        } finally {
	            out.close();
	        }
	    } finally {
	        in.close();
	    }
    }
}
