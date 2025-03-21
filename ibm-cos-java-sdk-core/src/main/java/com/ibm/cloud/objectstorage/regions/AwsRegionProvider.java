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

import com.ibm.cloud.objectstorage.SdkClientException;

/**
 * Interface for providing AWS region information. Implementations are free to use any strategy for
 * providing region information.
 */
public abstract class AwsRegionProvider {

    /**
     * @return Region name to use or null if region information is not available.
     */
    public abstract String getRegion() throws SdkClientException;

}
