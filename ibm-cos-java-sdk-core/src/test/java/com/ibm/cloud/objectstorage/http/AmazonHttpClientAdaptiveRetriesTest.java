/*
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ibm.cloud.objectstorage.http;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.ibm.cloud.objectstorage.internal.TokenBucket;
import com.ibm.cloud.objectstorage.retry.PredefinedRetryPolicies;
import com.ibm.cloud.objectstorage.retry.RetryMode;
import com.ibm.cloud.objectstorage.retry.RetryPolicy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AmazonHttpClientAdaptiveRetriesTest {

    @Mock
    public ConnectionManagerAwareHttpClient mockApache;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TokenBucket tokenBucket;
    private AmazonHttpClient httpClient;

    @Before
    public void setup() {
        when(mockApache.getHttpClientConnectionManager()).thenReturn(mock(HttpClientConnectionManager.class));
        tokenBucket = spy(new TokenBucket());
        ClientConfiguration cfg = new ClientConfiguration()
                .withRetryMode(RetryMode.ADAPTIVE);
        httpClient = new AmazonHttpClient(cfg, mockApache, null, tokenBucket);
    }

    @After
    public void teardown() {
        if (httpClient != null) {
            httpClient.shutdown();
        }
    }

    @Test
    public void execute_acquiresToken() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(createBasicHttpResponse(200, "OK"));

        httpClient.execute(request, handler, null, executionContext);

        verify(tokenBucket).acquire(1, false);
    }

    @Test
    public void execute_fastFailEnabled_propagatesSettingToBucket() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(createBasicHttpResponse(200, "OK"));

        ClientConfiguration cfg = new ClientConfiguration()
                .withRetryPolicy(createRetryPolicy(RetryMode.ADAPTIVE, true));
        httpClient = new AmazonHttpClient(cfg, mockApache, null, tokenBucket);

        httpClient.execute(request, handler, null, executionContext);

        verify(tokenBucket).acquire(1, true);
    }

    @Test
    public void execute_retryModeNotAdaptive_doesNotAcquireToken() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(createBasicHttpResponse(200, "OK"));

        httpClient = new AmazonHttpClient(new ClientConfiguration().withRetryMode(RetryMode.LEGACY), mockApache, null, tokenBucket);
        httpClient.execute(request, handler, null, executionContext);

        verify(tokenBucket, never()).acquire(anyDouble());
    }

    @Test
    public void execute_acquireReturnsFalse_throws() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(createBasicHttpResponse(200, "OK"));

        when(tokenBucket.acquire(anyDouble(), eq(true))).thenReturn(false);

        ClientConfiguration cfg = new ClientConfiguration()
                .withRetryPolicy(createRetryPolicy(RetryMode.ADAPTIVE, true));

        httpClient = new AmazonHttpClient(cfg, mockApache, null, tokenBucket);

        thrown.expect(SdkClientException.class);
        thrown.expectMessage("Unable to acquire enough send tokens");

        httpClient.execute(request, handler, null, executionContext);
    }

    @Test
    public void execute_responseSuccessful_updatesWithThrottlingFalse() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(createBasicHttpResponse(200, "OK"));

        httpClient.execute(request, handler, null, executionContext);

        verify(tokenBucket).updateClientSendingRate(false);
    }

    @Test
    public void execute_nonThrottlingServiceException_doesNotUpdateRate() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        AmazonServiceException ase = new AmazonServiceException("Internal server error");
        ase.setErrorCode("InternalException");
        HttpResponseHandler<AmazonServiceException> errorHandler = createStubErrorResponseHandler(ase);

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class)))
                .thenReturn(createBasicHttpResponse(500, "Internal server error"));

        thrown.expect(AmazonServiceException.class);
        try {
            httpClient.execute(request, handler, errorHandler, executionContext);
        } finally {
            verify(tokenBucket, never()).updateClientSendingRate(anyBoolean());
        }
    }

    @Test
    public void execute_ioException_doesNotUpdateRate() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        Request<?> request = mockRequest();
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();

        when(mockApache.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenThrow(new IOException("BOOM"));
        thrown.expect(SdkClientException.class);
        thrown.expectCause(Matchers.<IOException>instanceOf(IOException.class));
        try {
            httpClient.execute(request, handler, null, executionContext);
        } finally {
            verify(tokenBucket, never()).updateClientSendingRate(anyBoolean());
        }
    }

    private BasicHttpResponse createBasicHttpResponse(int statusCode, String reason) {
        return createBasicHttpResponse(statusCode, reason, new ByteArrayInputStream(new byte[0]));
    }

    private BasicHttpResponse createBasicHttpResponse(int statusCode, String reason, InputStream content) {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(content);

        BasicHttpResponse response = new BasicHttpResponse(
                new ProtocolVersion("http", 1, 1),
                statusCode,
                reason);
        response.setEntity(entity);
        return response;
    }

    private Request<?> mockRequest() {
        return mockRequest("TestService", HttpMethodName.PUT, "http://testsvc.region.amazonaws.com", false);
    }

    private Request<?> mockRequest(String serverName, HttpMethodName methodName, String uri, boolean hasCL) {
        Request<?> request = new DefaultRequest<Object>(null, serverName);
        request.setHttpMethod(methodName);
        request.setContent(new ByteArrayInputStream(new byte[100]));
        request.setEndpoint(URI.create(uri));
        if (hasCL) request.addHeader("Content-Length", "100");

        return request;
    }

    private HttpResponseHandler<AmazonWebServiceResponse<Object>> createStubResponseHandler() throws Exception {
        AmazonWebServiceResponse<Object> response = new AmazonWebServiceResponse<Object>();

        HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        when(responseHandler.handle(any(HttpResponse.class))).thenReturn(response);
        when(responseHandler.needsConnectionLeftOpen()).thenReturn(false);

        return responseHandler;
    }

    public HttpResponseHandler<AmazonServiceException> createStubErrorResponseHandler(AmazonServiceException e) throws Exception {
        HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);

        when(responseHandler.handle(any(HttpResponse.class))).thenReturn(e);
        when(responseHandler.needsConnectionLeftOpen()).thenReturn(false);

        return responseHandler;
    }

    private RetryPolicy createRetryPolicy(RetryMode retryMode, boolean fastFail) {
        return RetryPolicy.builder()
                .withRetryCondition(PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION)
                .withBackoffStrategy(PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY)
                .withMaxErrorRetry(3)
                .withHonorMaxErrorRetryInClientConfig(true)
                .withRetryMode(retryMode)
                .withFastFailRateLimiting(fastFail)
                .build();
    }
}
