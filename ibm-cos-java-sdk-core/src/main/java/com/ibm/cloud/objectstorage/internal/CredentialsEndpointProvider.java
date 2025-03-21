/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.cloud.objectstorage.internal;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.retry.internal.CredentialsEndpointRetryPolicy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Abstract class to return an endpoint URI from which the credentials can be loaded.
 * </p>
 * <p>
 * By default, the request won't be retried if the request fails while computing endpoint.
 * </p>
 */
@SdkInternalApi
public abstract class CredentialsEndpointProvider {
    /**
     * Returns the URI that contains the credentials.
     * @return
     * 		URI to retrieve the credentials.
     *
     */
    public abstract URI getCredentialsEndpoint() ;

    /**
     * Allows the extending class to provide a custom retry policy.
     * The default behavior is not to retry.
     */
    public CredentialsEndpointRetryPolicy getRetryPolicy() {
        return CredentialsEndpointRetryPolicy.NO_RETRY;
    }

    /**
     * Allows passing additional headers to the request
     */
    public Map<String, String> getHeaders() {
        return new HashMap<String, String>();
    }
}
