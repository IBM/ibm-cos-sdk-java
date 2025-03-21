/*
 * Copyright 2020-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.util.endpoint;

import com.ibm.cloud.objectstorage.util.AwsHostNameUtils;

/**
 * A default implementation of {@link RegionFromEndpointResolver} that only considers what is available in the host
 * name itself.
 *
 * @see AwsHostNameUtils#parseRegion(String, String)
 */
public class DefaultRegionFromEndpointResolver implements RegionFromEndpointResolver {
    @Override
    public String guessRegionFromEndpoint(String host, String serviceHint) {
        return AwsHostNameUtils.parseRegion(host, serviceHint);
    }
}
