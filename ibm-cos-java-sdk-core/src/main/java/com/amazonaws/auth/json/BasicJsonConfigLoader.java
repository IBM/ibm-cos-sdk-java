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
package com.amazonaws.auth.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.JsonCredentials;
import com.amazonaws.auth.json.BasicJsonConfigLoader;

/**
 * Class to load a json style config or credentials file. Performs only basic validation on
 * properties and profiles.
 */
@SdkInternalApi
public class BasicJsonConfigLoader {

    public static final BasicJsonConfigLoader INSTANCE = new BasicJsonConfigLoader();

    private BasicJsonConfigLoader() {
    }

    public JsonCredentials loadCredentials(File file) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "Unable to load IBM credentials: specified file is null.");
        }

        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException(
                    "IBM credential file not found in the given path: " +
                    file.getAbsolutePath());
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return new JsonCredentials(fis);
        } catch (IOException ioe) {
            throw new SdkClientException(
                    "Unable to load IBM credentials file at: " + file.getAbsolutePath(),
                    ioe);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

}
