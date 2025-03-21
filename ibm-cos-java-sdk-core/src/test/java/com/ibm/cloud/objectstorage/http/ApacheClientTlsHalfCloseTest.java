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


import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.Response;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.http.server.MockServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Assume;

public class ApacheClientTlsHalfCloseTest extends ClientTlsAuthTestBase {

    private static TlsKeyManagersProvider tlsKeyManagersProvider;
    private static MockServer mockServer;
    private static final int TWO_MB = 2 * 1024 * 1024;
    private static final byte[] CONTENT = new byte[TWO_MB];

    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void errorWhenServerHalfClosesSocketWhileStreamIsOpened() throws Exception {
        mockServer = MockServer.createMockServer(MockServer.ServerBehavior.HALF_CLOSE);
        mockServer.startServer(tlsKeyManagersProvider);
        AmazonHttpClient amazonHttpClient = new AmazonHttpClient(new ClientConfiguration()
                .withTlsKeyManagersProvider(tlsKeyManagersProvider));
        try {
            makeRequestWithClient(amazonHttpClient);
            fail("Expected SdkClientException");
        } catch (SdkClientException e) {
            assertThat(e.getMessage(), containsString("Unable to execute HTTP request: Remote end is closed"));
        }
    }

    @Test
    public void successfulRequestForFullCloseSocketAtTheEnd() throws Exception {
        mockServer = MockServer.createMockServer(MockServer.ServerBehavior.FULL_CLOSE_AT_THE_END);
        mockServer.startServer(tlsKeyManagersProvider);
        AmazonHttpClient amazonHttpClient = new AmazonHttpClient(new ClientConfiguration()
                .withTlsKeyManagersProvider(tlsKeyManagersProvider));
        Response response = makeRequestWithClient(amazonHttpClient);
        assertEquals(200, response.getHttpResponse().getStatusCode());//        thrown.expect(IOException.class);
    }

    @Test
    public void errorWhenServerHalfClosesSocketWhileStreamIsOpenedWithCertCheckDisabled() throws Exception {
        System.setProperty(DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        mockServer = MockServer.createMockServer(MockServer.ServerBehavior.HALF_CLOSE);
        mockServer.startServer(tlsKeyManagersProvider);
        AmazonHttpClient amazonHttpClient = new AmazonHttpClient(new ClientConfiguration()
                .withTlsKeyManagersProvider(tlsKeyManagersProvider));
        try {
            makeRequestWithClient(amazonHttpClient);
            fail("Expected SdkClientException");
        } catch (SdkClientException e) {
            assertThat(e.getMessage(), containsString("Unable to execute HTTP request: Remote end is closed"));
        }
    }

    @Test
    public void successfulRequestForFullCloseSocketAtTheEndWithCertCheckDisabled() throws Exception {
        System.setProperty(DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        mockServer = MockServer.createMockServer(MockServer.ServerBehavior.FULL_CLOSE_AT_THE_END);
        mockServer.startServer(tlsKeyManagersProvider);
        AmazonHttpClient amazonHttpClient = new AmazonHttpClient(new ClientConfiguration()
                .withTlsKeyManagersProvider(tlsKeyManagersProvider));
        Response response = makeRequestWithClient(amazonHttpClient);
        assertEquals(200, response.getHttpResponse().getStatusCode());//        thrown.expect(IOException.class);
    }

    @After
    public void tearDown() {
        System.clearProperty(DISABLE_CERT_CHECKING_SYSTEM_PROPERTY);

        if (mockServer != null) {
            mockServer.stopServer();
        }
    }

    @BeforeClass
    public static void checkCondition() {
        boolean shouldRunTests = isJavaVersionSupported();
        Assume.assumeTrue("Skipping test since the Java version does not support TLS1.3 half close"
                , shouldRunTests);
        try {
            ClientTlsAuthTestBase.setUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.setProperty("javax.net.ssl.trustStore", serverKeyStore.getAbsolutePath());
        System.setProperty("javax.net.ssl.trustStorePassword", STORE_PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        tlsKeyManagersProvider = new FileStoreTlsKeyManagersProvider(clientKeyStore, CLIENT_STORE_TYPE, STORE_PASSWORD);
    }

    @AfterClass
    public static void clear() {
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");
        System.clearProperty("javax.net.ssl.trustStoreType");
    }

    private Response makeRequestWithClient(AmazonHttpClient client) throws Exception {
        DefaultRequest<Void> request = new DefaultRequest<Void>(null, "service");
        request.setHttpMethod(HttpMethodName.PUT);
        request.setContent(new ByteArrayInputStream(CONTENT));
        request.setEndpoint(URI.create("https://localhost:" + mockServer.getPort()));
        ExecutionContext executionContext = new ExecutionContext();
        AmazonWebServiceResponse mockResponse = mock(AmazonWebServiceResponse.class);
        HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        when(responseHandler.handle(Mockito.any(HttpResponse.class))).thenReturn(mockResponse);
        HttpResponseHandler mockErrorResponseHandler = mock(HttpResponseHandler.class);
        return client.execute(request, responseHandler, mockErrorResponseHandler, executionContext, null);
    }

    public static boolean isJavaVersionSupported() {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion.startsWith("1.7")) {
            return false;
        }
        if (javaVersion.startsWith("1.8.0")) {
            try {
                int buildNumber = Integer.parseInt(javaVersion.split("_")[1].split("-")[0]);
                return buildNumber > 341;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}