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
package com.ibm.cloud.objectstorage.http.timers.client;

import static com.ibm.cloud.objectstorage.http.timers.TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT;
import static com.ibm.cloud.objectstorage.http.timers.TimeoutTestConstants.SLOW_REQUEST_HANDLER_TIMEOUT;
import static com.ibm.cloud.objectstorage.http.timers.TimeoutTestConstants.TEST_TIMEOUT;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.TestPreConditions;
import com.ibm.cloud.objectstorage.handlers.RequestHandler2;
import com.ibm.cloud.objectstorage.http.AmazonHttpClient;
import com.ibm.cloud.objectstorage.http.ExecutionContext;
import com.ibm.cloud.objectstorage.http.MockServerTestBase;
import com.ibm.cloud.objectstorage.http.request.RequestHandlerTestUtils;
import com.ibm.cloud.objectstorage.http.request.SlowRequestHandler;
import com.ibm.cloud.objectstorage.http.response.NullErrorResponseHandler;
import com.ibm.cloud.objectstorage.http.response.NullResponseHandler;
import com.ibm.cloud.objectstorage.http.response.UnresponsiveErrorResponseHandler;
import com.ibm.cloud.objectstorage.http.server.MockServer;

/**
 * Tests that use a server that returns a predetermined error response within the timeout limit
 */
public class DummyErrorResponseServerIntegrationTests extends MockServerTestBase {

    private static final int STATUS_CODE = 500;
    private AmazonHttpClient httpClient;

    @BeforeClass
    public static void preConditions() {
        TestPreConditions.assumeNotJava6();
    }

    @Override
    protected MockServer buildMockServer() {
        return new MockServer(
                MockServer.DummyResponseServerBehavior.build(STATUS_CODE, "Internal Server Failure", "Dummy response"));
    }

    @Test(timeout = TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowErrorResponseHandler_ThrowsClientExecutionTimeoutException()
            throws Exception {
        httpClient = new AmazonHttpClient(
                new ClientConfiguration().withClientExecutionTimeout(CLIENT_EXECUTION_TIMEOUT));

        httpClient.requestExecutionBuilder().request(newGetRequest()).errorResponseHandler(new UnresponsiveErrorResponseHandler()).execute();
    }

    @Test(timeout = TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowAfterErrorRequestHandler_ThrowsClientExecutionTimeoutException()
            throws Exception {
        httpClient = new AmazonHttpClient(
                new ClientConfiguration().withClientExecutionTimeout(CLIENT_EXECUTION_TIMEOUT));

        List<RequestHandler2> requestHandlers = RequestHandlerTestUtils.buildRequestHandlerList(
                new SlowRequestHandler().withAfterErrorWaitInSeconds(SLOW_REQUEST_HANDLER_TIMEOUT));

        httpClient.requestExecutionBuilder()
                .request(newGetRequest())
                .errorResponseHandler(new NullErrorResponseHandler())
                .executionContext(ExecutionContext.builder().withRequestHandler2s(requestHandlers).build())
                .execute();
    }

}
