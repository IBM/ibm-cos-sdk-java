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
package com.ibm.cloud.objectstorage.profile.path.cred;

import java.io.File;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.profile.path.AwsProfileFileLocationProvider;

/**
 * If {@value #CREDENTIAL_JSON_FILE_ENVIRONMENT_VARIABLE} environment variable is set then the
 * shared credentials file is source from it's location.
 */
@SdkInternalApi
public class IBMJsonCredentialsEnvVarOverrideLocationProvider implements AwsProfileFileLocationProvider {

    private static final String CREDENTIAL_JSON_FILE_ENVIRONMENT_VARIABLE = "IBM_CREDENTIAL_JSON_FILE";

    @Override
    public File getLocation() {
        String credentialProfilesFileOverride = System
                .getenv(CREDENTIAL_JSON_FILE_ENVIRONMENT_VARIABLE);
        if (credentialProfilesFileOverride == null) {
            return null;
        } else {
            return new File(credentialProfilesFileOverride);
        }
    }
}
