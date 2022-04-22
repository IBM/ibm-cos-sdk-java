/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


import static com.ibm.cloud.objectstorage.retry.PredefinedBackoffStrategies.SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS;
import static com.ibm.cloud.objectstorage.retry.PredefinedBackoffStrategies.STANDARD_BACKOFF_STRATEGY;
import static com.ibm.cloud.objectstorage.retry.PredefinedBackoffStrategies.STANDARD_DEFAULT_BASE_DELAY_IN_MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(Parameterized.class)
public class StandardBackoffStrategyTest {

    private final Random mockRandom = mock(Random.class);

    @Parameterized.Parameters
    public static Collection<TestCase> parameters() throws Exception {
        return Arrays.asList(
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(0)
                          .expectedMaxDelay(100)
                          .expectedMedDelay(50)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(1)
                          .expectedMaxDelay(200)
                          .expectedMedDelay(100)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(2)
                          .expectedMaxDelay(400)
                          .expectedMedDelay(200)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(3)
                          .expectedMaxDelay(800)
                          .expectedMedDelay(400)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(4)
                          .expectedMaxDelay(1600)
                          .expectedMedDelay(800)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(5)
                          .expectedMaxDelay(3200)
                          .expectedMedDelay(1600)
                          .expectedMinDelay(1),
            new TestCase().backoffStrategy(STANDARD_BACKOFF_STRATEGY)
                          .retriesAttempted(100)
                          .expectedMaxDelay(20000)
                          .expectedMedDelay(10000)
                          .expectedMinDelay(1)
        );
    }

    @Parameterized.Parameter
    public TestCase testCase;

    @Before
    public void setUp() throws Exception {
        testCase.backoffStrategy = injectMockRandom();
    }

    @Test
    public void testMaxDelay() {
        mockMaxRandom();
        test(testCase.backoffStrategy, testCase.retriesAttempted, testCase.expectedMaxDelay);
    }

    @Test
    public void testMedDelay() {
        mockMediumRandom();
        test(testCase.backoffStrategy, testCase.retriesAttempted, testCase.expectedMedDelay);
    }

    @Test
    public void testMinDelay() {
        mockMinRandom();
        test(testCase.backoffStrategy, testCase.retriesAttempted, testCase.expectedMinDelay);
    }

    private static void test(RetryPolicy.BackoffStrategy backoffStrategy, int retriesAttempted, long expectedDelay) {
        long computedDelay = backoffStrategy.delayBeforeNextRetry(null, null, retriesAttempted);
        assertEquals(expectedDelay, computedDelay);
    }

    private PredefinedBackoffStrategies.FullJitterBackoffStrategy injectMockRandom() {
        return new PredefinedBackoffStrategies.FullJitterBackoffStrategy(STANDARD_DEFAULT_BASE_DELAY_IN_MILLISECONDS, SDK_DEFAULT_MAX_BACKOFF_IN_MILLISECONDS,
                                                                         mockRandom);
    }

    private void mockMaxRandom() {
        when(mockRandom.nextInt(Mockito.anyInt())).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) returnsFirstArg().answer(invocationOnMock) - 1;
            }
        });
    }

    private void mockMinRandom() {
        when(mockRandom.nextInt(anyInt())).thenReturn(0);
    }

    private void mockMediumRandom() {
        when(mockRandom.nextInt(anyInt())).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                Integer firstArg = (Integer) returnsFirstArg().answer(invocationOnMock);
                return firstArg / 2 - 1;
            }
        });
    }

    private static class TestCase {
        private RetryPolicy.BackoffStrategy backoffStrategy;
        private int retriesAttempted;
        private int expectedMinDelay;
        private int expectedMedDelay;
        private int expectedMaxDelay;

        public TestCase backoffStrategy(RetryPolicy.BackoffStrategy backoffStrategy) {
            this.backoffStrategy = backoffStrategy;
            return this;
        }

        public TestCase retriesAttempted(int retriesAttempted) {
            this.retriesAttempted = retriesAttempted;
            return this;
        }

        public TestCase expectedMinDelay(int expectedDelayInMillis) {
            this.expectedMinDelay = expectedDelayInMillis;
            return this;
        }

        public TestCase expectedMedDelay(int expectedDelayInMillis) {
            this.expectedMedDelay = expectedDelayInMillis;
            return this;
        }

        public TestCase expectedMaxDelay(int expectedDelayInMillis) {
            this.expectedMaxDelay = expectedDelayInMillis;
            return this;
        }
    }
}
