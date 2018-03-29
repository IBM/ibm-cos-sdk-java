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

import java.io.File;
import java.net.URL;

/**
 * Loads json related resource files and exposes static factory methods of the known resource
 * files for convenient access in tests.
 */
public class JsonResourceLoader {

    private static final String PREFIX = "/resources/jsonconfig/";

    private final String resourceName;

    public JsonResourceLoader(String fileName) {
        this.resourceName = fileName;
    }

    /**
     * Load resource as a {@link File} object
     */
    public File asFile() {
    	try {
    		return new File(asUrl().getFile());
    	} catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    /**
     * Load resource as a {@link URL}
     */
    public URL asUrl() {
        return getClass().getResource(PREFIX + resourceName);
    }

    public static JsonResourceLoader accessKeyNotSpecified() {
        return new JsonResourceLoader("CredentialsWithAccessKeyNotSpecified.json");
    }

    public static JsonResourceLoader basicCredentials() {
        return new JsonResourceLoader("BasicCredentials.json");
    }

    public static JsonResourceLoader basicCredentials2() {
        return new JsonResourceLoader("BasicCredentials2.json");
    }

    public static JsonResourceLoader profileNameWithNoBraces() {
        return new JsonResourceLoader("ProfileNameWithNoBraces.tst");
    }

    public static JsonResourceLoader profileNameWithNoClosingBraces() {
        return new JsonResourceLoader("ProfileNameWithNoClosingBraces.tst");
    }

    public static JsonResourceLoader profileNameWithNoOpeningBraces() {
        return new JsonResourceLoader("ProfileNameWithNoOpeningBraces.tst");
    }

    public static JsonResourceLoader profileNameWithSpaces() {
        return new JsonResourceLoader("ProfileNameWithSpaces.tst");
    }

    public static JsonResourceLoader credentialsContainingHmacAndOAuth() {
        return new JsonResourceLoader("CredentialsContainingHmacAndOAuth.json");
    }

    public static JsonResourceLoader profilesWithComments() {
        return new JsonResourceLoader("ProfilesWithComments.tst");
    }

    public static JsonResourceLoader credentialsWithSecretAccessKeyNotSpecified() {
        return new JsonResourceLoader("CredentialsWithSecretAccessKeyNotSpecified.tst");
    }

    public static JsonResourceLoader profilesWithTwoAccessKeyUnderSameProfile() {
        return new JsonResourceLoader("ProfilesWithTwoAccessKeyUnderSameProfile.tst");
    }

    public static JsonResourceLoader credentialsWithEmptyAccessKey() {
        return new JsonResourceLoader("CredentialsWithEmptyAccessKey.json");
    }

    public static JsonResourceLoader credentialsWithEmptySecretKey() {
        return new JsonResourceLoader("CredentialsWithEmptySecretAccessKey.json");
    }
    
    public static JsonResourceLoader credentialsWithHmacAndOAuthNotSpecified() {
        return new JsonResourceLoader("CredentialsWithHmacAndOAuthNotSpecified.json");
    }

}
