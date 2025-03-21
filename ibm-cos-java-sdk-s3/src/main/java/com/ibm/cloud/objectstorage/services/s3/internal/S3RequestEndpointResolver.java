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
package com.ibm.cloud.objectstorage.services.s3.internal;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.internal.ServiceEndpointBuilder;
import com.ibm.cloud.objectstorage.regions.Region;
import com.ibm.cloud.objectstorage.regions.RegionUtils;
import com.ibm.cloud.objectstorage.util.SdkHttpUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Sets endpoint and resource path on a request object
 */
public class S3RequestEndpointResolver {

    private final ServiceEndpointBuilder endpointBuilder;
    private final boolean isPathStyleAccess;
    private final String bucketName;
    private final String key;

    public S3RequestEndpointResolver(ServiceEndpointBuilder endpointBuilder, boolean isPathStyleAccess,
                                     String bucketName, String key) {
        this.endpointBuilder = endpointBuilder;
        this.isPathStyleAccess = isPathStyleAccess;
        this.bucketName = bucketName;
        this.key = key;
    }

    static boolean isValidIpV4Address(String ipAddr) {
        if (ipAddr == null) {
            return false;
        }
        String[] tokens = ipAddr.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String token : tokens) {
            try {
                int tokenInt = Integer.parseInt(token);
                if (tokenInt < 0 || tokenInt > 255) {
                    return false;
                }
            } catch (NumberFormatException ase) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts the current endpoint set for this client into virtual addressing style, by placing
     * the name of the specified bucket before the S3 service endpoint.
     *
     * @param bucketName The name of the bucket to use in the virtual addressing style of the returned URI.
     * @return A new URI, creating from the current service endpoint URI and the specified bucket.
     */
    private static URI convertToVirtualHostEndpoint(URI endpoint, String bucketName) {
        try {
            return new URI(String.format("%s://%s.%s", endpoint.getScheme(), bucketName, endpoint.getAuthority()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid bucket name: " + bucketName, e);
        }
    }

    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * Set the request's endpoint and resource path with the same region it was originally
     * configured for
     *
     * @param request Request to set endpoint for
     */
    public void resolveRequestEndpoint(Request<?> request) {
        resolveRequestEndpoint(request, null);
    }

    /**
     * Set the request's endpoint and resource path with the new region provided
     *
     * @param request Request to set endpoint for
     * @param regionString  New region to determine endpoint to hit
     */
    public void resolveRequestEndpoint(Request<?> request, String regionString) {
        if (regionString != null) {
            final Region r = RegionUtils.getRegion(regionString);

            if (r == null) {
                throw new SdkClientException("Not able to determine region" +
                        " for " + regionString + ".Please upgrade to a newer " +
                        "version of the SDK");
            }

            endpointBuilder.withRegion(r);
        }
        final URI endpoint = endpointBuilder.getServiceEndpoint();
        if (endpoint.getHost() == null) {
            throw new IllegalArgumentException("Endpoint does not contain a valid host name: " + request.getEndpoint());
        }
        if (shouldUseVirtualAddressing(endpoint)) {
            request.setEndpoint(convertToVirtualHostEndpoint(endpoint, bucketName));
            request.setResourcePath(SdkHttpUtils.urlEncode(getHostStyleResourcePath(), true));
        } else {
            request.setEndpoint(endpoint);
            request.setResourcePath(getPathStyleResourcePath());
        }
    }

    private boolean shouldUseVirtualAddressing(final URI endpoint) {
        return !isPathStyleAccess && BucketNameUtils.isDNSBucketName(bucketName)
                && !isValidIpV4Address(endpoint.getHost());
    }

    private String getHostStyleResourcePath() {
        return keyForBaseOfPath();
    }

    private String getPathStyleResourcePath() {
        if (bucketName == null) {
            return SdkHttpUtils.urlEncode(keyForBaseOfPath(), true);
        }

        String encodedBucketName = SdkHttpUtils.urlEncode(bucketName, false);
        return encodedBucketName + "/" + SdkHttpUtils.urlEncode(key == null ? "" : key, true);
    }

    private String keyForBaseOfPath() {
        if (key == null) {
            return "";
        }

        // If the key name starts with a slash, prepend it with "/" so that it doesn't get treated as a redundant slash and get
        // pruned out in later path normalization logic.
        if (key.startsWith("/")) {
            return "/" + key;
        }

        return key;
    }
}
