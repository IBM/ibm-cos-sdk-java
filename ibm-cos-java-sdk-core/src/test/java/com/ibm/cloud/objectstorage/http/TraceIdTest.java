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

package com.ibm.cloud.objectstorage.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.Request;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import utils.EnvironmentVariableHelper;
import utils.http.WireMockTestBase;


public class TraceIdTest extends WireMockTestBase {

    private static final String RESOURCE_PATH = "/trace-id/";
    private static final EnvironmentVariableHelper environment = new EnvironmentVariableHelper();

    @Before
    public void restoreOriginal() {
        environment.reset();
    }

    @Test
    public void requestWithoutTraceId_setsTraceId() throws Exception {
        stubFor(get(urlEqualTo(RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        environment.set("AWS_LAMBDA_FUNCTION_NAME", "foo");
        environment.set("_X_AMZN_TRACE_ID", "bar");
        Request<?> request = newGetRequest(RESOURCE_PATH);
        executeRequest(request);
        assertTraceIdSet("bar");
    }

    @Test
    public void requestWithTraceId_preservesTraceId() throws Exception {
        stubFor(get(urlEqualTo(RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        environment.set("AWS_LAMBDA_FUNCTION_NAME", "foo");
        environment.set("_X_AMZN_TRACE_ID", "bar");
        Request<?> request = newGetRequest(RESOURCE_PATH);
        request.addHeader("X-Amzn-Trace-Id", "baz");
        executeRequest(request);
        assertTraceIdSet("baz");
    }

    @Test
    public void requestWithoutEnvVars_doesNotSetTraceId() throws Exception {
        stubFor(get(urlEqualTo(RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        Request<?> request = newGetRequest(RESOURCE_PATH);
        executeRequest(request);
        assertTraceIdNotSet();
    }

    private void assertTraceIdSet(String traceId) {
        List<LoggedRequest> loggedRequests = findAll(getRequestedFor(urlEqualTo(RESOURCE_PATH)));
        assertNotNull(loggedRequests.get(0).getHeader("X-Amzn-Trace-Id"));
        assertEquals(traceId, loggedRequests.get(0).getHeader("X-Amzn-Trace-Id"));
    }

    private void assertTraceIdNotSet() {
        List<LoggedRequest> loggedRequests = findAll(getRequestedFor(urlEqualTo(RESOURCE_PATH)));
        assertNull(loggedRequests.get(0).getHeader("X-Amzn-Trace-Id"));
    }

    private void executeRequest(Request<?> request) throws Exception {
        AmazonHttpClient httpClient = new AmazonHttpClient(new ClientConfiguration());
        try {
            httpClient.requestExecutionBuilder()
                      .request(request)
                      .executionContext(ExecutionContext.builder().build())
                      .errorResponseHandler(stubErrorHandler())
                      .execute();
            fail("Expected exception");
        } catch (AmazonServiceException expected) {
        }
    }
}
