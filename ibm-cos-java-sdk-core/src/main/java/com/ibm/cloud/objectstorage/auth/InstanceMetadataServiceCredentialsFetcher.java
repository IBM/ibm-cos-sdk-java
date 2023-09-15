/*
 * Copyright 2011-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.auth;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.internal.EC2ResourceFetcher;
import com.ibm.cloud.objectstorage.internal.InstanceMetadataServiceResourceFetcher;
import com.ibm.cloud.objectstorage.retry.internal.CredentialsEndpointRetryParameters;
import com.ibm.cloud.objectstorage.retry.internal.CredentialsEndpointRetryPolicy;
import com.ibm.cloud.objectstorage.util.EC2MetadataUtils;
import java.net.URI;

/**
 * Fetches credential from EC2 instance metadata service.
 */
@SdkInternalApi
final class InstanceMetadataServiceCredentialsFetcher extends BaseCredentialsFetcher implements CredentialsEndpointRetryPolicy {

    private final EC2ResourceFetcher resourceFetcher;

    InstanceMetadataServiceCredentialsFetcher() {
        super(SdkClock.STANDARD, true);
        this.resourceFetcher = InstanceMetadataServiceResourceFetcher.getInstance();
    }

    @SdkTestInternalApi
    InstanceMetadataServiceCredentialsFetcher(SdkClock clock, EC2ResourceFetcher resourceFetcher) {
        super(clock, true);
        this.resourceFetcher = resourceFetcher;
    }

    @Override
    protected String getCredentialsResponse() {
        URI credentialsEndpoint = getCredentialsEndpoint();
        return resourceFetcher.readResource(credentialsEndpoint, this);
    }

    @Override
    public String toString() {
        return "InstanceMetadataServiceCredentialsFetcher";
    }

    private URI getCredentialsEndpoint() {
        String host = EC2MetadataUtils.getHostAddressForEC2MetadataService();

        String securityCredentialsList = resourceFetcher.readResource(URI.create(host + EC2MetadataUtils.SECURITY_CREDENTIALS_RESOURCE), this);

        String[] securityCredentials = securityCredentialsList.trim().split("\n");
        if (securityCredentials.length == 0) {
            throw new SdkClientException("Unable to load credentials path");
        }

        return URI.create(host + EC2MetadataUtils.SECURITY_CREDENTIALS_RESOURCE + securityCredentials[0]);
    }

    @Override
    public boolean shouldRetry(int retriesAttempted, CredentialsEndpointRetryParameters retryParams) {
        return false;
    }
}