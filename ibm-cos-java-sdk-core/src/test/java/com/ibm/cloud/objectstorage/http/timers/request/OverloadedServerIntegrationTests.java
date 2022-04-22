/*
 * Copyright 2011-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.http.timers.request;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.TestPreConditions;
import com.ibm.cloud.objectstorage.http.AmazonHttpClient;
import com.ibm.cloud.objectstorage.http.OverloadedMockServerTestBase;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ApacheHttpClientFactory;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.ibm.cloud.objectstorage.http.client.HttpClientFactory;
import com.ibm.cloud.objectstorage.http.exception.HttpRequestTimeoutException;
import com.ibm.cloud.objectstorage.http.server.MockServer;
import com.ibm.cloud.objectstorage.http.server.MockServer.ServerBehavior;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.ibm.cloud.objectstorage.http.timers.ClientExecutionAndRequestTimerTestUtils.assertNumberOfRetries;
import static com.ibm.cloud.objectstorage.http.timers.ClientExecutionAndRequestTimerTestUtils.assertNumberOfTasksTriggered;
import static com.ibm.cloud.objectstorage.http.timers.ClientExecutionAndRequestTimerTestUtils.execute;
import static com.ibm.cloud.objectstorage.http.timers.TimeoutTestConstants.TEST_TIMEOUT;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

/**
 * Tests requiring an Overloaded server, that is a server that responds but can't close the connection in a timely
 * fashion
 */
public class OverloadedServerIntegrationTests extends OverloadedMockServerTestBase {

    private AmazonHttpClient httpClient;

    @BeforeClass
    public static void preConditions() {
        TestPreConditions.assumeNotJava6();
    }

    @Override
    protected MockServer buildMockServer() {
        return MockServer.createMockServer(ServerBehavior.OVERLOADED);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void requestTimeoutEnabled_HonorsRetryPolicy() throws IOException {
        int maxRetries = 2;
        ClientConfiguration config = new ClientConfiguration().withRequestTimeout(1 * 1000)
                .withMaxErrorRetry(maxRetries);
        HttpClientFactory<ConnectionManagerAwareHttpClient> httpClientFactory = new ApacheHttpClientFactory();
        ConnectionManagerAwareHttpClient rawHttpClient = spy(httpClientFactory.create(HttpClientSettings.adapt(config)));

        httpClient = new AmazonHttpClient(config, rawHttpClient, null, null);

        try {
            execute(httpClient, newGetRequest());
            fail("Exception expected");
        } catch (AmazonClientException e) {
            /* the expected exception and number of requests. */
            assertThat(e.getCause(), instanceOf(HttpRequestTimeoutException.class));
            int expectedNumberOfRequests = 1 + maxRetries;
            assertNumberOfRetries(rawHttpClient, expectedNumberOfRequests);
            assertNumberOfTasksTriggered(httpClient.getHttpRequestTimer(), expectedNumberOfRequests);
        }
    }

}