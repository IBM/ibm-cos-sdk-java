/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.http.impl.client;

import com.ibm.cloud.objectstorage.annotation.ThreadSafe;

import java.io.IOException;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

@ThreadSafe
public class HttpRequestNoRetryHandler extends DefaultHttpRequestRetryHandler {
    public static final HttpRequestNoRetryHandler Singleton = new HttpRequestNoRetryHandler();
    private HttpRequestNoRetryHandler() {}

    @Override public boolean retryRequest(
            final IOException exception,
            int executionCount,
            final HttpContext context) {
        return false;
    }
}
