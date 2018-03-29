/*
 * Copyright 2016-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.object.storage.auth.json;

import org.junit.Test;

import com.amazonaws.auth.json.JsonConfigFile;

import java.io.File;

/**
 * Unit Tests for {@link ProfilesConfigFile}
 */
public class JsonConfigFileTest {

    @Test(expected = IllegalArgumentException.class)
    public void file_is_null_throws_exception() {
        new JsonConfigFile((File) null);
    }

}
