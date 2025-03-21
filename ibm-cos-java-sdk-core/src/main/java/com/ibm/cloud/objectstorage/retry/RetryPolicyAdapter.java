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
package com.ibm.cloud.objectstorage.retry;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.retry.internal.MaxAttemptsResolver;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicyContext;

import static com.ibm.cloud.objectstorage.retry.PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY_STANDARD_MODE;
import static com.ibm.cloud.objectstorage.util.ValidationUtils.assertNotNull;

/**
 * Adapts a legacy {@link RetryPolicy} to the new {@link com.ibm.cloud.objectstorage.retry.v2.RetryPolicy}. This class is intended for internal
 * use by the SDK.
 */
@SdkInternalApi
public class RetryPolicyAdapter implements com.ibm.cloud.objectstorage.retry.v2.RetryPolicy {
    private final RetryPolicy legacyRetryPolicy;
    private final ClientConfiguration clientConfiguration;
    private final int maxErrorRetry;
    private final RetryPolicy.BackoffStrategy backoffStrategy;

    public RetryPolicyAdapter(RetryPolicy legacyRetryPolicy, ClientConfiguration clientConfiguration) {
        this.legacyRetryPolicy = assertNotNull(legacyRetryPolicy, "legacyRetryPolicy");
        this.clientConfiguration = assertNotNull(clientConfiguration, "clientConfiguration");
        this.maxErrorRetry = resolveMaxErrorRetry();
        this.backoffStrategy = resolveBackoffStrategy();
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return backoffStrategy.delayBeforeNextRetry(
                (AmazonWebServiceRequest) context.originalRequest(),
                (AmazonClientException) context.exception(),
                context.retriesAttempted());
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return !maxRetriesExceeded(context) && isRetryable(context);
    }

    public boolean isRetryable(RetryPolicyContext context) {
        return legacyRetryPolicy.getRetryCondition().shouldRetry(
            (AmazonWebServiceRequest) context.originalRequest(),
            (AmazonClientException) context.exception(),
            context.retriesAttempted());
    }

    public RetryPolicy getLegacyRetryPolicy() {
        return this.legacyRetryPolicy;
    }

    private RetryPolicy.BackoffStrategy resolveBackoffStrategy() {
        if (legacyRetryPolicy.isBackoffStrategyInRetryModeHonored()) {
            return backoffStrategyByRetryMode();
        }

        return legacyRetryPolicy.getBackoffStrategy();
    }

    private RetryPolicy.BackoffStrategy backoffStrategyByRetryMode() {
        RetryMode retryMode = clientConfiguration.getRetryMode() == null ? legacyRetryPolicy.getRetryMode()
                                                                         : clientConfiguration.getRetryMode();

        return PredefinedRetryPolicies.getDefaultBackoffStrategy(retryMode);
    }

    private int resolveMaxErrorRetry() {
        if(legacyRetryPolicy.isMaxErrorRetryInClientConfigHonored() && clientConfiguration.getMaxErrorRetry() >= 0) {
            return clientConfiguration.getMaxErrorRetry();
        }

        Integer resolvedMaxAttempts = new MaxAttemptsResolver().maxAttempts();

        if (resolvedMaxAttempts != null) {
            return resolvedMaxAttempts - 1;
        }

        if (shouldUseStandardModeDefaultMaxRetry()) {
            return DEFAULT_MAX_ERROR_RETRY_STANDARD_MODE;
        }

        // default to use legacyRetryPolicy.getMaxErrorRetry() because it's always present
        return legacyRetryPolicy.getMaxErrorRetry();
    }

    /**
     * We should use the default standard maxErrorRetry for standard mode if the maxErrorRetry is not from sdk
     * default predefined retry policies.
     */
    private boolean shouldUseStandardModeDefaultMaxRetry() {
        RetryMode retryMode = clientConfiguration.getRetryMode() == null ? legacyRetryPolicy.getRetryMode()
                                                                         : clientConfiguration.getRetryMode();

        return (retryMode.equals(RetryMode.STANDARD) || retryMode.equals(RetryMode.ADAPTIVE))
                && legacyRetryPolicy.isDefaultMaxErrorRetryInRetryModeHonored();
    }

    public boolean maxRetriesExceeded(RetryPolicyContext context) {
        return context.retriesAttempted() >= maxErrorRetry;
    }

    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    public RetryPolicy.BackoffStrategy getBackoffStrategy() {
        return backoffStrategy;
    }
}
