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

import static com.ibm.cloud.objectstorage.http.HttpResponseHandler.X_AMZN_REQUEST_ID_HEADER;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.transform.LegacyErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.StandardErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.util.LogCaptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Ignore;  // IBM
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Node;
import utils.http.WireMockTestBase;

@Ignore("IBM - Backend logger used in this test is no longer supported")
public class DefaultErrorResponseHandlerIntegrationTest extends WireMockTestBase {

    private static final String RESOURCE = "/some-path";
    public static final String STANDARD_TEST_ERROR_CODE = "StandardTestError";
    public static final String LEGACY_TEST_ERROR_CODE = "LegacyTestError";
    // IBM private LogCaptor logCaptor = new LogCaptor.DefaultLogCaptor(Level.DEBUG);
    private final AmazonHttpClient client = new AmazonHttpClient(new ClientConfiguration());
    private final DefaultErrorResponseHandler sut = new DefaultErrorResponseHandler(new ArrayList<Unmarshaller<AmazonServiceException, Node>>());
    private final DefaultErrorResponseHandler sutWithMap = new DefaultErrorResponseHandler(
            getUnmarshallerHashMap(), new LegacyErrorUnmarshaller(BaseException.class));

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Map<String, Unmarshaller<AmazonServiceException, Node>> getUnmarshallerHashMap() {
        HashMap<String, Unmarshaller<AmazonServiceException, Node>> stringUnmarshallerHashMap =
                new HashMap<String, Unmarshaller<AmazonServiceException, Node>>();
        stringUnmarshallerHashMap.put(STANDARD_TEST_ERROR_CODE, new StandardTestErrorUnMarshaller());
        stringUnmarshallerHashMap.put(LEGACY_TEST_ERROR_CODE, new LegacyTestErrorUnmarshaller());
        return stringUnmarshallerHashMap;
    }

    @Before
    public void setUp() {
        // IBM logCaptor.clear();
    }

    @Test
    public void invocationIdIsCapturedInTheLog() throws Exception {
        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse().withStatus(418)));

        executeRequest();

        // IBM Matcher<Iterable<? super LoggingEvent>> matcher = hasItem(hasEventWithContent("Invocation Id"));
        // IBM assertThat(debugEvents(), matcher);
	}
	
    @Test
    public void standardUnmarshaller_passedInMap_throwsMappedException() throws Exception {

        exception.expect(CustomException.class);
        exception.expectMessage("abcd");
        exception.expectMessage("No config found");

        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse()
                .withBody("<?xml version=\"1.0\"?>\n"
                        + "<ErrorResponse xmlns=\"http://awss3control.amazonaws.com/doc/2018-08-20/\">\n"
                        + "  <Error>\n"
                        + "    <Type>Sender</Type>\n"
                        + "    <Code>StandardTestError</Code>\n"
                        + "    <Message>No config found</Message>\n"
                        + "  </Error>\n"
                        + "  <RequestId>abcd</RequestId>\n"
                        + "</ErrorResponse>\n")
                .withStatus(418)));


        client.requestExecutionBuilder().errorResponseHandler(sutWithMap).request(newGetRequest(RESOURCE)).execute();

        // assertThat(debugMessages(), hasItem(containsString("Invocation Id")));
    }

    @Test
    public void legacyUnmarshaller_passedInMap_throwsMappedException() throws Exception {

        exception.expect(CustomLegacyException.class);
        exception.expectMessage("Legacy no config found");
        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse()
                .withBody("<?xml version=\"1.0\"?>\n"
                        + "<Response xmlns=\"http://awss3control.amazonaws.com/doc/2018-08-20/\">\n"
                        + "  <Errors>\n"
                        + "  <Error>\n"
                        + "    <Type>Sender</Type>\n"
                        + "    <Code>LegacyTestError</Code>\n"
                        + "    <Message>Legacy no config found</Message>\n"
                        + "  </Error>\n"
                        + "  </Errors>\n"
                        + "  <RequestId>efgh</RequestId>\n"
                        + "</Response>\n")
                .withStatus(418)));
        client.requestExecutionBuilder().errorResponseHandler(sutWithMap).request(newGetRequest(RESOURCE)).execute();
    }

    @Test
    public void noMarshallerDefined_throwsBaseException() throws Exception {

        exception.expect(BaseException.class);
        exception.expectMessage("Testing New error message.");
        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse()
                .withBody("<?xml version=\"1.0\"?>\n"
                        + "<Response xmlns=\"http://awss3control.amazonaws.com/doc/2018-08-20/\">\n"
                        + "  <Errors>\n"
                        + "  <Error>\n"
                        + "    <Type>Sender</Type>\n"
                        + "    <Code>UnknownErrorByService</Code>\n"
                        + "    <Message>Testing New error message.</Message>\n"
                        + "  </Error>\n"
                        + "  </Errors>\n"
                        + "  <RequestId>efgh</RequestId>\n"
                        + "</Response>\n")
                .withStatus(418)));
        client.requestExecutionBuilder().errorResponseHandler(sutWithMap).request(newGetRequest(RESOURCE)).execute();
    }



    @Test
    public void invalidXmlLogsXmlContentToDebug() throws Exception {
        String content = RandomStringUtils.randomAlphanumeric(10);
        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse().withStatus(418).withBody(content)));

        executeRequest();

        // IBM Matcher<Iterable<? super LoggingEvent>> matcher = hasItem(hasEventWithContent(content));
        // IBM assertThat(debugEvents(), matcher);
    }

    @Test
    public void requestIdIsLoggedWithDebugIfInTheHeader() throws Exception {
        String requestId = RandomStringUtils.randomAlphanumeric(10);

        stubFor(get(urlPathEqualTo(RESOURCE)).willReturn(aResponse().withStatus(418).withHeader(X_AMZN_REQUEST_ID_HEADER, requestId)));

        executeRequest();

        // IBM Matcher<Iterable<? super LoggingEvent>> matcher = hasItem(hasEventWithContent(requestId));
        // IBM assertThat(debugEvents(), matcher);
    }

    private void executeRequest() {
        expectException(new Runnable() {
            @Override
            public void run() {
                client.requestExecutionBuilder().errorResponseHandler(sut).request(newGetRequest(RESOURCE)).execute();
            }
        });
    }

    @SuppressWarnings("EmptyCatchBlock")
    private void expectException(Runnable r) {
        try {
            r.run();
            throw new RuntimeException("Expected exception, got none");
        } catch (Exception e) {
        }
    }

    // IBM private List<LoggingEvent> debugEvents() {
    // IBM     List<LoggingEvent> events = new ArrayList<LoggingEvent>();
    // IBM     List<LoggingEvent> loggingEvents = logCaptor.loggedEvents();
    // IBM     for (LoggingEvent le : loggingEvents) {
    // IBM         if (le.getLevel().equals(Level.DEBUG)) {
    // IBM             events.add(le);
    // IBM         }
    // IBM     }
    // IBM     return events;
    // IBM }

    // IBM private org.hamcrest.Matcher<? super LoggingEvent> hasEventWithContent(String content) {
    // IBM     return hasProperty("message", containsString(content));
    // IBM }
    public static class CustomException extends AmazonServiceException {

        public CustomException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class BaseException extends AmazonServiceException {
        public BaseException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class CustomLegacyException extends AmazonServiceException {
        public CustomLegacyException(String errorMessage) {
            super(errorMessage);
        }
    }

    class StandardTestErrorUnMarshaller extends StandardErrorUnmarshaller {
        public StandardTestErrorUnMarshaller() {
            super(CustomException.class);
        }
        @Override
        public AmazonServiceException unmarshall(Node node) throws Exception {
            String errorCode = parseErrorCode(node);
            if (errorCode == null || !errorCode.equals(STANDARD_TEST_ERROR_CODE))
                return null;

            CustomException e = (CustomException) super.unmarshall(node);
            return e;
        }
    }

    class LegacyTestErrorUnmarshaller extends LegacyErrorUnmarshaller {

        public LegacyTestErrorUnmarshaller() {
            super(CustomLegacyException.class);
        }

        @Override
        public AmazonServiceException unmarshall(Node node) throws Exception {
            String errorCode = parseErrorCode(node);
            if (errorCode == null || !errorCode.equals(LEGACY_TEST_ERROR_CODE))
                return null;
            CustomLegacyException e = (CustomLegacyException) super.unmarshall(node);
            return e;
        }
    }
}

