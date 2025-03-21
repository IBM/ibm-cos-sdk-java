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

package com.ibm.cloud.objectstorage.retry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ibm.cloud.objectstorage.AmazonServiceException;
import org.junit.Test;

public class RetryUtilsTest {

    @Test
    public void isThrottlingException_TrueWhenErrorCodeMatchesKnownCodes() throws Exception {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setErrorCode("ThrottlingException");
        assertTrue("ThrottlingException error code should be true", RetryUtils.isThrottlingException(ase));
    }

    @Test
    public void isThrottlingException_TrueWhenStatusCodeIs429() throws Exception {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setStatusCode(429);
        assertTrue("ThrottlingException error code should be true", RetryUtils.isThrottlingException(ase));
    }

    @Test
    public void isThrottlingException_FalseWhenErrorAndStatusCodeDoNotMatch() throws Exception {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setStatusCode(500);
        ase.setErrorCode("InternalFailure");
        assertFalse("InternalFailure error code should be false", RetryUtils.isThrottlingException(ase));
    }

    @Test
    public void retriesOnPriorRequestNotCompleteErrorCode() {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setStatusCode(500);
        ase.setErrorCode("PriorRequestNotComplete");
        assertTrue("PriorRequestNotComplete should be retried", RetryUtils.isRetryableServiceException(ase));

    }

    @Test
    public void isThrottlingExceptions_True_When_EC2ThrottledException() {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setErrorCode("EC2ThrottledException");

        assertTrue(RetryUtils.isThrottlingException(ase));
    }

    @Test
    public void isThrottlingException_WhenReasonPhraseIsErrorCode() {
        AmazonServiceException ase = new AmazonServiceException("msg");
        ase.setStatusCode(503);
        ase.setErrorCode("503 Slow Down");
        assertTrue("503 Slow Down error code should be considered throttling", RetryUtils.isThrottlingException(ase));
    }
}