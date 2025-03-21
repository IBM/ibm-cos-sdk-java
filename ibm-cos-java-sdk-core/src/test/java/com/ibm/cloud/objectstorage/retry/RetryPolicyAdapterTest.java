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
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicyContext;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicyContexts;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.AWS_MAX_ATTEMPTS_SYSTEM_PROPERTY;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.AWS_RETRY_MODE_SYSTEM_PROPERTY;
import static com.ibm.cloud.objectstorage.retry.PredefinedBackoffStrategies.STANDARD_BACKOFF_STRATEGY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetryPolicyAdapterTest {

    @Mock
    private RetryPolicy.RetryCondition retryCondition;

    @Mock
    private RetryPolicy.BackoffStrategy backoffStrategy;

    private RetryPolicy legacyPolicy;

    private ClientConfiguration clientConfiguration = new ClientConfiguration().withMaxErrorRetry(10);

    private RetryPolicyAdapter adapter;

    @Before
    public void setup() {
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 3, false);
        adapter = new RetryPolicyAdapter(legacyPolicy, clientConfiguration);
        System.clearProperty(AWS_MAX_ATTEMPTS_SYSTEM_PROPERTY);
        System.clearProperty(AWS_RETRY_MODE_SYSTEM_PROPERTY);
    }

    @Test
    public void getLegacyRetryPolicy_ReturnsSamePolicy() {
        assertEquals(legacyPolicy, adapter.getLegacyRetryPolicy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRetryPolicy_ThrowsException() {
        new RetryPolicyAdapter(null, clientConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullClientConfiguration_ThrowsException() {
        new RetryPolicyAdapter(legacyPolicy, null);
    }

    @Test
    public void computeDelayBeforeNextRetry_DelegatesToLegacyPolicy() {
        final RetryPolicyContext context = RetryPolicyContexts.LEGACY;
        adapter.computeDelayBeforeNextRetry(context);

        verify(backoffStrategy).delayBeforeNextRetry(
                eq((AmazonWebServiceRequest) context.originalRequest()),
                eq((AmazonClientException) context.exception()),
                eq(context.retriesAttempted()));
    }

    @Test
    public void shouldRetry_MaxErrorRetryReached() {
        assertFalse(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
    }

    @Test
    public void shouldRetry_MaxErrorInClientConfigHonored_DoesNotUseMaxErrorInPolicy() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
                .thenReturn(true);
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 3, true);
        adapter = new RetryPolicyAdapter(legacyPolicy, clientConfiguration);
        assertTrue(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
        assertFalse(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(10)));
    }

    @Test
    public void shouldRetry_MaxErrorSpecifiedInSystemProperty_DoesNotUseMaxErrorInPolicy() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        System.setProperty(AWS_MAX_ATTEMPTS_SYSTEM_PROPERTY, "10");
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 3, false);
        adapter = new RetryPolicyAdapter(legacyPolicy, clientConfiguration);
        assertTrue(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
        assertTrue(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(8)));
        assertFalse(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(9)));
    }

    @Test
    public void shouldRetry_MaxErrorNotExceeded_DelegatesToLegacyRetryCondition() {
        final RetryPolicyContext context = RetryPolicyContexts.LEGACY;
        adapter.shouldRetry(context);

        verify(retryCondition).shouldRetry(
                eq((AmazonWebServiceRequest) context.originalRequest()),
                eq((AmazonClientException) context.exception()),
                eq(context.retriesAttempted()));
    }

    @Test
    public void standardModeAndDefaultLegacyMaxError_shouldUseStandardDefaultModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        System.setProperty(AWS_RETRY_MODE_SYSTEM_PROPERTY, "standard");
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 5, false, RetryMode.STANDARD, true, false, false);
        adapter = new RetryPolicyAdapter(legacyPolicy, new ClientConfiguration());

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Test
    // Adaptive mode is a superset of standard
    public void adaptiveModeAndDefaultLegacyMaxError_shouldUseStandardDefaultModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
                .thenReturn(true);
        System.setProperty(AWS_RETRY_MODE_SYSTEM_PROPERTY, "adaptive");
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 5, false, RetryMode.ADAPTIVE, true, false, false);
        adapter = new RetryPolicyAdapter(legacyPolicy, new ClientConfiguration());

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Ignore("Fails in v1.12.137")
    @Test
    public void standardMode_shouldUseStandardDefaultModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        // maxErrorRetry in the RetryPolicy is only honored for non-standard mode,
        // so we use a high number here to assert that it's not being used
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 100, false, true, false);
        adapter = new RetryPolicyAdapter(legacyPolicy, new ClientConfiguration());

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Test
    public void standardModePredefinedDynamodbPolicy_shouldUseDefaultStandardModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDynamoDBDefaultRetryPolicy();
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.STANDARD));

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(10)));
    }

    @Test
    // Adaptive mode is a superset of standard
    public void adaptiveModePredefinedDynamodbPolicy_shouldUseDefaultStandardModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
                .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDynamoDBDefaultRetryPolicy();
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.ADAPTIVE));

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(10)));
    }

    @Test
    public void standardModePredefinedDefaultPolicy_shouldUseDefaultStandardModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDefaultRetryPolicy();
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.STANDARD));

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Test
    // Adaptive mode is a superset of standard
    public void adaptiveModePredefinedDefaultPolicy_shouldUseDefaultStandardModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
                .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDefaultRetryPolicy();
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.ADAPTIVE));

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Test
    public void nonStandardModePredefinedDynamodbDefaultPolicy_shouldUseDefaultStandardModeMaxError() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDynamoDBDefaultRetryPolicy();
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration());

        assertFalse(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(10)));
    }

    @Test
    public void standardModePredefinedDefaultPolicyWithCustomMaxError_shouldHonor() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(1);
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.STANDARD));

        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
    }

    @Test
    // Adaptive mode is a superset of standard
    public void adaptiveModePredefinedDefaultPolicyWithCustomMaxError_shouldHonor() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
                .thenReturn(true);
        RetryPolicy dynamoDBDefaultRetryPolicy = PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(1);
        adapter = new RetryPolicyAdapter(dynamoDBDefaultRetryPolicy, new ClientConfiguration().withRetryMode(RetryMode.ADAPTIVE));

        assertTrue(adapter.maxRetriesExceeded(RetryPolicyContexts.withRetriesAttempted(1)));
    }

    @Test
    public void standardMode_shouldUseStandardBackoffStrategy() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 5, false, true, true);
        adapter = new RetryPolicyAdapter(legacyPolicy, new ClientConfiguration().withRetryMode(RetryMode.STANDARD));
        assertEquals(STANDARD_BACKOFF_STRATEGY, adapter.getBackoffStrategy());
    }

    @Test
    public void adaptiveMode_shouldUseStandardBackoffStrategy() {
        when(retryCondition.shouldRetry(any(AmazonWebServiceRequest.class), any(AmazonClientException.class), anyInt()))
            .thenReturn(true);
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 5, false, true, true);
        adapter = new RetryPolicyAdapter(legacyPolicy, new ClientConfiguration().withRetryMode(RetryMode.ADAPTIVE));
        assertEquals(STANDARD_BACKOFF_STRATEGY, adapter.getBackoffStrategy());
    }
}
