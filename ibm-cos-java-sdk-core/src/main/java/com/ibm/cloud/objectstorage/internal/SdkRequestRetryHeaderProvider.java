/*
 * Copyright 2019-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.ThreadSafe;
import com.ibm.cloud.objectstorage.auth.SdkClock;
import com.ibm.cloud.objectstorage.auth.internal.AWS4SignerUtils;
import com.ibm.cloud.objectstorage.handlers.HandlerContextKey;
import com.ibm.cloud.objectstorage.retry.ClockSkewAdjuster;
import com.ibm.cloud.objectstorage.retry.RetryPolicyAdapter;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides SDK request header "amz-sdk-request"
 */
@ThreadSafe
@SdkInternalApi
public final class SdkRequestRetryHeaderProvider {
    private static final String SDK_REQUEST_RETRY_HEADER = "amz-sdk-request";
    private final ClientConfiguration config;
    private final Integer maxErrorRetry;
    private final ClockSkewAdjuster clockSkewAdjuster;

    public SdkRequestRetryHeaderProvider(ClientConfiguration config,
                                         RetryPolicy retryPolicy,
                                         ClockSkewAdjuster clockSkewAdjuster) {
        this.config = config;
        if (retryPolicy instanceof RetryPolicyAdapter) {
            maxErrorRetry = ((RetryPolicyAdapter) retryPolicy).getMaxErrorRetry() + 1;
        } else {
            maxErrorRetry = null;
        }
        this.clockSkewAdjuster = clockSkewAdjuster;
    }

    public void addSdkRequestRetryHeader(Request<?> request, int attemptNum) {
        List<Pair> pairs = requestPairs(request, String.valueOf(attemptNum));

        StringBuilder headerValue = new StringBuilder();
        for (Pair pair: pairs) {
            headerValue.append(pair.name).append("=").append(pair.value).append(";");
        }

        String header = headerValue.toString().substring(0, headerValue.length() - 1);
        request.addHeader(SDK_REQUEST_RETRY_HEADER, header);
    }

    private List<Pair> requestPairs(Request<?> request, String attemptNum) {
        List<Pair> requestPairs = new ArrayList<Pair>();

        String optionalTtl = calculateTtl(request);
        if (optionalTtl != null) {
            requestPairs.add(new Pair("ttl", optionalTtl));
        }

        requestPairs.add(new Pair("attempt", attemptNum));

        if (maxErrorRetry != null) {
            requestPairs.add(new Pair("max", String.valueOf(maxErrorRetry)));
        }

        return requestPairs;
    }

    /**
     * Calculate the ttl and format it:
     *
     * ttl = current_time + socket_read_timeout + estimated_skew
     * estimated_skew_i = timeStampFromDateHeader_i-1 - timeClientReceivedResposne_i-1
     */
    private String calculateTtl(Request<?> request) {
        // ttl should be omitted for streaming operations
        if (isStreaming(request)) {
            return null;
        }

        Integer estimatedSkew = clockSkewAdjuster.getEstimatedSkew();
        // Calculating this value requires that we've seen at least one response from the service s
        // o the ttl SHOULD be omitted for the initial request.
        if (estimatedSkew == null) {
            return null;
        }

        long currentTimeMillis = SdkClock.Instance.get().currentTimeMillis();

        // The returned estimatedSkew is derived from (responseTime - clientTime), so -estimatedSkew = clientTime - responseTime
        long ttl = currentTimeMillis + config.getSocketTimeout() - estimatedSkew * 1000;

        return AWS4SignerUtils.formatTimestamp(ttl);
    }

    private boolean isStreaming(Request<?> request) {
        return Boolean.TRUE.equals(request.getHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT)) ||
               Boolean.TRUE.equals(request.getHandlerContext(HandlerContextKey.HAS_STREAMING_OUTPUT));
    }

    private static final class Pair {
        private String name;
        private String value;

        Pair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
