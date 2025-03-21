/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.regions;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;

/**
 * Loads region information from the '{@value com.ibm.cloud.objectstorage.SDKGlobalConfiguration#AWS_REGION_SYSTEM_PROPERTY}' JVM system
 * property.
 */
public class AwsSystemPropertyRegionProvider extends AwsRegionProvider {

    @Override
    public String getRegion() throws SdkClientException {
        return System.getProperty(SDKGlobalConfiguration.AWS_REGION_SYSTEM_PROPERTY);
    }
}
