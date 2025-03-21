/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.retry;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class SDKDefaultRetryConditionTest {

    private static final RetryPolicy.RetryCondition defaultRetryCondition =
            PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;
    private static final Random random = new Random();

    @Test
    public void shouldRetryIOExceptionAce() {
        Assert.assertTrue(shouldRetry(getAce(new IOException())));
    }

    @Test
    public void shouldNotRetryNonIOExceptionAce() {
        Assert.assertFalse(shouldRetry(getAce(new Exception())));
    }

    @Test
    public void shouldAlwaysRetryOnRetryableStatusCodes() {
        for (int statusCode : RetryUtils.RETRYABLE_STATUS_CODES) {
            Assert.assertTrue(shouldRetry(getAse(statusCode, "IrrelevantCode")));
        }
    }

    @Test
    public void shouldNotRetryNonRetryable5xx() {
        Assert.assertFalse(shouldRetry(getAse(550, "IrrelevantCode")));
    }

    @Test
    public void shouldAlwaysRetryClockSkewCodes() {
        for (String errorCode : RetryUtils.CLOCK_SKEW_ERROR_CODES) {
            Assert.assertTrue(shouldRetry(getAse(random.nextInt(), errorCode)));
        }
    }

    @Test
    public void shouldAlwaysRetryThrottlingCodes() {
        for (String errorCode : RetryUtils.THROTTLING_ERROR_CODES) {
            Assert.assertTrue(shouldRetry(getAse(random.nextInt(), errorCode)));
        }
    }

    @Test
    public void shouldNotRetryBad4xxErrorCodeAseExcept429() {
        // Try all 4xx status codes except 429 which should be retryable
        for (int i = 400; i < 500; ++i) {
            if (i != 429) {
                Assert.assertFalse("Status code " + i + " should not be retryable",
                                   shouldRetry(getAse(i, "BogusException")));
            }
        }
    }

    @Test
    public void shouldRetryBad429ErrorCodeAse() {
        Assert.assertTrue("Status code 429 should be retryable", shouldRetry(getAse(429, "BogusException")));
    }

    @Test
    public void shouldRetry_EC2ThrottledException() {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setErrorCode("EC2ThrottledException");

        Assert.assertTrue(shouldRetry(ase));
    }

    private boolean shouldRetry(AmazonClientException ace) {
        return defaultRetryCondition.shouldRetry(null, ace, 0);
    }

    private AmazonClientException getAce(Throwable cause) {
        return new AmazonClientException("Foo", cause);
    }

    private AmazonServiceException getAse(int statusCode, String errorCode) {
        AmazonServiceException exception = new AmazonServiceException(errorCode);
        exception.setStatusCode(statusCode);
        exception.setErrorCode(errorCode);
        return exception;
    }
}
