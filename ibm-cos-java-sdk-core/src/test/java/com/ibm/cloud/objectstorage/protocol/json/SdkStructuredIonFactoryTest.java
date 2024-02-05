/*
 * Copyright 2016-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.protocol.json;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.ibm.cloud.objectstorage.transform.JsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.http.JsonErrorResponseHandler;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonWriter;
import com.amazon.ion.Timestamp;
import com.amazon.ion.system.IonSystemBuilder;

public class SdkStructuredIonFactoryTest {
    private static final String ERROR_PREFIX = "aws-type:";
    private static final String ERROR_TYPE = "InvalidParameterException";
    private static final String ERROR_MESSAGE = "foo";

    private static final String NO_SERVICE_NAME = null;
    private static final HttpRequestBase NO_HTTP_REQUEST = null;

    private static IonSystem system;

    @BeforeClass
    public static void beforeClass() {
        system = IonSystemBuilder.standard().build();
    }

    @Test
    public void handlesErrorsUsingHttpHeader() throws Exception {
        IonStruct payload = createPayload();

        HttpResponse error = createResponse(payload);
        error.addHeader("x-amzn-ErrorType", ERROR_TYPE);

        AmazonServiceException exception = handleError(error);
        assertThat(exception, instanceOf(InvalidParameterException.class));
        assertEquals(ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test
    public void handlesErrorsUsingMagicField() throws Exception {
        IonStruct payload = createPayload();
        payload.add("__type", system.newString(ERROR_TYPE));

        HttpResponse error = createResponse(payload);

        AmazonServiceException exception = handleError(error);
        assertThat(exception, instanceOf(InvalidParameterException.class));
        assertEquals(ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test
    public void handlesErrorsUsingAnnotation() throws Exception {
        IonStruct payload = createPayload();
        payload.addTypeAnnotation(ERROR_PREFIX + ERROR_TYPE);

        HttpResponse error = createResponse(payload);

        AmazonServiceException exception = handleError(error);
        assertThat(exception, instanceOf(InvalidParameterException.class));
        assertEquals(ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test(expected = AmazonClientException.class)
    public void rejectPayloadsWithMultipleErrorAnnotations() throws Exception {
        IonStruct payload = createPayload();
        payload.addTypeAnnotation(ERROR_PREFIX + ERROR_TYPE);
        payload.addTypeAnnotation(ERROR_PREFIX + "foo");

        HttpResponse error = createResponse(payload);

        handleError(error);
    }

    @Test
    public void handlesErrorsWithMutipleAnnotations() throws Exception {
        IonStruct payload = createPayload();
        payload.addTypeAnnotation("foo");
        payload.addTypeAnnotation(ERROR_PREFIX + ERROR_TYPE);
        payload.addTypeAnnotation("bar");

        HttpResponse error = createResponse(payload);

        AmazonServiceException exception = handleError(error);
        assertThat(exception, instanceOf(InvalidParameterException.class));
        assertEquals(ERROR_MESSAGE, exception.getErrorMessage());
    }

    private static IonStruct createPayload() {
        IonStruct payload = system.newEmptyStruct();
        payload.add("NotValidJson", system.newTimestamp(Timestamp.nowZ()));
        payload.add("ErrorMessage", system.newString(ERROR_MESSAGE));
        return payload;
    }

    private static HttpResponse createResponse(IonStruct payload) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IonWriter writer = system.newBinaryWriter(bytes);
        payload.writeTo(writer);
        writer.close();

        HttpResponse error = new HttpResponse(new DefaultRequest(NO_SERVICE_NAME), NO_HTTP_REQUEST);
        error.setContent(new ByteArrayInputStream(bytes.toByteArray()));
        return error;
    }

    private AmazonServiceException handleError(HttpResponse error) throws Exception {
        List<JsonErrorUnmarshaller> unmarshallers = new LinkedList<JsonErrorUnmarshaller>();
        unmarshallers.add(new InvalidParameterExceptionUnmarshaller(ERROR_TYPE));

        JsonErrorResponseHandler handler = SdkStructuredIonFactory
                .SDK_ION_BINARY_FACTORY
                .createErrorResponseHandler(new JsonErrorResponseMetadata(), unmarshallers);
        return handler.handle(error);
    }

    private static class InvalidParameterException extends AmazonServiceException {
        private static final long serialVersionUID = 0;

        public InvalidParameterException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static class InvalidParameterExceptionUnmarshaller extends JsonErrorUnmarshaller {

        public InvalidParameterExceptionUnmarshaller(String handledErrorCode) {
            super(InvalidParameterException.class, handledErrorCode);
        }

        @Override
        public InvalidParameterException unmarshall(JsonNode in) throws Exception {
            return new InvalidParameterException(null);
        }
    }
}
