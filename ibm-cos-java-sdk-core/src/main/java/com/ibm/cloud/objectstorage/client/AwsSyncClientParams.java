/*
 * Copyright 2011-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.client;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.monitoring.MonitoringListener;
import com.ibm.cloud.objectstorage.handlers.RequestHandler2;
import com.ibm.cloud.objectstorage.internal.auth.SignerProvider;
import com.ibm.cloud.objectstorage.metrics.RequestMetricCollector;
import com.ibm.cloud.objectstorage.retry.RetryPolicyAdapter;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicy;

import java.net.URI;
import java.util.List;

/**
 * Provides access to all params needed in a synchronous AWS service client constructor. Abstract
 * to allow additions to the params while maintaining backwards compatibility.
 */
@SdkProtectedApi
public abstract class AwsSyncClientParams {

    public abstract AWSCredentialsProvider getCredentialsProvider();

    public abstract ClientConfiguration getClientConfiguration();

    public abstract RequestMetricCollector getRequestMetricCollector();

    public abstract List<RequestHandler2> getRequestHandlers();

    //IBM unsupported
    //public abstract CsmConfigurationProvider getClientSideMonitoringConfigurationProvider();

    public abstract MonitoringListener getMonitoringListener();

    //IBM unsupported
    // public AdvancedConfig getAdvancedConfig() {
    //     return AdvancedConfig.EMPTY;
    // }

    public SignerProvider getSignerProvider() {
        // Not currently used by AWS clients. The builder uses setRegion to configure endpoint
        // and signer and does not support custom endpoints or signers.
        return null;
    }

    public URI getEndpoint() {
        // Not currently used by AWS clients. The builder uses setRegion to configure endpoint
        // and signer and does not support custom endpoints or signers.
        return null;
    }

    public RetryPolicy getRetryPolicy() {
        final ClientConfiguration config = getClientConfiguration();
        return new RetryPolicyAdapter(config.getRetryPolicy(), config);
    }
}
