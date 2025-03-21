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
package com.ibm.cloud.objectstorage.internal;

import java.net.URI;

import com.ibm.cloud.objectstorage.regions.Region;

/**
 * More of a No-Op endpoint builder that just returns the same endpoint it was constructed with
 */
public class IdentityEndpointBuilder extends ServiceEndpointBuilder {

    private final URI endpoint;

    public IdentityEndpointBuilder(URI endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public URI getServiceEndpoint() {
        return endpoint;
    }

    @Override
    public DefaultServiceEndpointBuilder withRegion(Region region) {
        // No-op
        return null;
    }

    @Override
    public Region getRegion() {
        return null;
    }
}
