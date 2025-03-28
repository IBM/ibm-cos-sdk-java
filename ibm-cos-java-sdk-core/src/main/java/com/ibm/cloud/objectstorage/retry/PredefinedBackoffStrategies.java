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

import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.retry.v2.BackoffStrategy;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicyContext;
import com.ibm.cloud.objectstorage.util.ValidationUtils;

import java.util.Random;

/**
 * This class includes a set of pre-defined backoff policies.
 * See this blog for more information on the different algorithms:
 * https://www.awsarchitectureblog.com/2015/03/backoff.html
 */
public class PredefinedBackoffStrategies {

    /**
     * Default base sleep time (milliseconds) for non-throttled exceptions.
     **/
    private static final int SDK_DEFAULT_BASE_DELAY = 100;

    /**
     * Default base sleep time (milliseconds) for throttled exceptions.
     **/
    static final int SDK_DEFAULT_THROTTLED_BASE_DELAY = 500;

    /**
     * Default maximum back-off time before retrying a request
     */
    static final int SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    /**
     * Default base sleep time for DynamoDB.
     **/
    static final int DYNAMODB_DEFAULT_BASE_DELAY = 25;

    /**
     * Maximum retry limit. Avoids integer overflow issues.
     *
     * NOTE: If the value is greater than 30, there can be integer overflow
     * issues during delay calculation.
     **/
    private static final int MAX_RETRIES = 30;

    static final int STANDARD_DEFAULT_BASE_DELAY_IN_MILLISECONDS = 100;

    /**
     * Default backoff strategy used for standard and adaptive retry mode
     */
    static final V2CompatibleBackoffStrategy STANDARD_BACKOFF_STRATEGY = new FullJitterBackoffStrategy(STANDARD_DEFAULT_BASE_DELAY_IN_MILLISECONDS,
                                                                                                       SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);

    public static class FullJitterBackoffStrategy extends V2CompatibleBackoffStrategyAdapter {

        private final int baseDelay;
        private final int maxBackoffTime;
        private final Random random;

        public FullJitterBackoffStrategy(final int baseDelay,
                                         final int maxBackoffTime) {
            this(baseDelay, maxBackoffTime, new Random());
        }

        @SdkTestInternalApi
        FullJitterBackoffStrategy(int baseDelay,
                                  int maxBackoffTime,
                                  Random random) {
            this.baseDelay = ValidationUtils.assertIsPositive(baseDelay, "Base delay");
            this.maxBackoffTime = ValidationUtils.assertIsPositive(maxBackoffTime, "Max backoff");
            this.random = random;
        }


        @Override
        public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
            int ceil = calculateExponentialDelay(context.retriesAttempted(), baseDelay, maxBackoffTime);
            // add 1 ms to the random result to allow us to reach our maximum value (since the random
            // parameter is exclusive)
            return random.nextInt(ceil) + 1;
        }
    }

    public static class EqualJitterBackoffStrategy extends V2CompatibleBackoffStrategyAdapter {

        private final int baseDelay;
        private final int maxBackoffTime;
        private final Random random = new Random();

        public EqualJitterBackoffStrategy(final int baseDelay,
                                          final int maxBackoffTime) {
            this.baseDelay = ValidationUtils.assertIsPositive(baseDelay, "Base delay");
            this.maxBackoffTime = ValidationUtils.assertIsPositive(maxBackoffTime, "Max backoff");
        }

        @Override
        public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
            int ceil = calculateExponentialDelay(context.retriesAttempted(), baseDelay, maxBackoffTime);
            return (ceil / 2) + random.nextInt((ceil / 2) + 1);
        }
    }

    public static class ExponentialBackoffStrategy extends V2CompatibleBackoffStrategyAdapter {

        private final int baseDelay;
        private final int maxBackoffTime;

        public ExponentialBackoffStrategy(final int baseDelay,
                                          final int maxBackoffTime) {
            this.baseDelay = ValidationUtils.assertIsPositive(baseDelay, "Base delay");
            this.maxBackoffTime = ValidationUtils.assertIsPositive(maxBackoffTime, "Max backoff");
        }

        @Override
        public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
            return calculateExponentialDelay(context.retriesAttempted(), baseDelay, maxBackoffTime);
        }
    }

    private static int calculateExponentialDelay(int retriesAttempted, int baseDelay, int maxBackoffTime) {
        int retries = Math.min(retriesAttempted, MAX_RETRIES);
        return (int) Math.min((1L << retries) * baseDelay, maxBackoffTime);
    }


    /**
     * A private class that implements the default back-off strategy.
     **/
    public static class SDKDefaultBackoffStrategy extends V2CompatibleBackoffStrategyAdapter {

        private final BackoffStrategy fullJitterBackoffStrategy;
        private final BackoffStrategy equalJitterBackoffStrategy;

        public SDKDefaultBackoffStrategy() {
            fullJitterBackoffStrategy = new PredefinedBackoffStrategies.FullJitterBackoffStrategy(
                    SDK_DEFAULT_BASE_DELAY, SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
            equalJitterBackoffStrategy = new PredefinedBackoffStrategies.EqualJitterBackoffStrategy(
                    SDK_DEFAULT_THROTTLED_BASE_DELAY, SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS);
        }

        public SDKDefaultBackoffStrategy(final int baseDelay, final int throttledBaseDelay, final int maxBackoff) {
            fullJitterBackoffStrategy = new PredefinedBackoffStrategies.FullJitterBackoffStrategy(
                    baseDelay, maxBackoff);
            equalJitterBackoffStrategy = new PredefinedBackoffStrategies.EqualJitterBackoffStrategy(
                    throttledBaseDelay, maxBackoff);
        }

        @Override
        public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
            /*
             * We use the full jitter scheme for non-throttled exceptions and the
             * equal jitter scheme for throttled exceptions.  This gives a preference
             * to quicker response and larger retry distribution for service errors
             * and guarantees a minimum delay for throttled exceptions.
             */
            if (RetryUtils.isThrottlingException(context.exception())) {
                return equalJitterBackoffStrategy.computeDelayBeforeNextRetry(context);
            } else {
                return fullJitterBackoffStrategy.computeDelayBeforeNextRetry(context);
            }
        }
    }

}