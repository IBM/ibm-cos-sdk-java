/*
 * Copyright 2015-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.http.client;

import com.ibm.cloud.objectstorage.annotation.Beta;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;


/**
 * Factory interface that can be used for creating the underlying http client
 * for request execution.
 */
@Beta
public interface HttpClientFactory<T> {

    T create(HttpClientSettings settings);

}