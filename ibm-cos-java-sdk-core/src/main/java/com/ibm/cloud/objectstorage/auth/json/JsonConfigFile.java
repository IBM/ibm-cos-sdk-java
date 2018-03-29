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

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.JsonCredentials;
import com.ibm.cloud.objectstorage.auth.json.internal.JsonStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.profile.path.AwsProfileFileLocationProvider;
import com.ibm.cloud.objectstorage.util.ValidationUtils;

public class JsonConfigFile {

    private final File credentialFile;
    /**
     * Cache credential provider as credentials are requested.
     */
    private AWSCredentialsProvider credentialProvider;
    private volatile long credentialFileLastModified;
    private volatile JsonCredentials credentials;

    /**
     * Loads the IBM credentials file from the default location (~/.bluemix/cos_credentials) or from
     * an alternate location if <code>IBM_CREDENTIAL_JSON_FILE</code> is set.
     */
    public JsonConfigFile() throws SdkClientException {
        this(getCredentialsJsonFile());
    }

    /**
     * Loads the IBM credentials from the file. The path of the file is specified as a
     * parameter to the constructor.
     */
    public JsonConfigFile(String filePath) {
        this(new File(validateFilePath(filePath)));
    }

    private static String validateFilePath(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException(
                    "Unable to load IBM credentials: specified file path is null.");
        }
        return filePath;
    }

    /**
     * Loads the IBM credentials from the file. The reference to the file is specified as a
     * parameter to the constructor.
     */
    public JsonConfigFile(File file) throws SdkClientException {
        credentialFile = ValidationUtils.assertNotNull(file, "IBM credential file");
        credentialFileLastModified = credentialFile.lastModified();
        credentials = loadCredentials(file);
    }

    /**
     * Returns the IBM credentials.
     */
    public AWSCredentials getCredentials() {
        if (credentialProvider != null) {
            return credentialProvider.getCredentials();
        } else {
            credentialProvider = new JsonStaticCredentialsProvider(credentials);
            return credentialProvider.getCredentials();
        }
    }

    /**
     * Reread data from disk.
     */
    public void refresh() {
        if (credentialFile.lastModified() > credentialFileLastModified) {
            credentialFileLastModified = credentialFile.lastModified();
            credentials = loadCredentials(credentialFile);
        }
        credentialProvider = new JsonStaticCredentialsProvider(credentials);
    }

    private static File getCredentialsJsonFile() {
        return AwsProfileFileLocationProvider.IBM_CREDENTIALS_LOCATION_PROVIDER.getLocation();
    }
    
    private static JsonCredentials loadCredentials(File file) {
        return BasicJsonConfigLoader.INSTANCE.loadCredentials(file);
    }
  
}
