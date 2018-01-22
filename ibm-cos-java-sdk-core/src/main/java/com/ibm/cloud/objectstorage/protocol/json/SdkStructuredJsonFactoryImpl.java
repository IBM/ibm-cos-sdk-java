/*
 * Copyright 2011-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.fasterxml.jackson.core.JsonFactory;
import com.ibm.cloud.objectstorage.http.JsonErrorResponseHandler;
import com.ibm.cloud.objectstorage.http.JsonResponseHandler;
import com.ibm.cloud.objectstorage.internal.http.ErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.JsonErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.JsonErrorMessageParser;
import com.ibm.cloud.objectstorage.transform.JsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext.UnmarshallerType;

import java.util.List;
import java.util.Map;

/**
 * Generic implementation of a structured JSON factory that is pluggable for different variants of
 * JSON. See {@link SdkStructuredPlainJsonFactory#SDK_JSON_FACTORY} and {@link
 * SdkStructuredCborFactory#SDK_CBOR_FACTORY}.
 */
public abstract class SdkStructuredJsonFactoryImpl implements SdkStructuredJsonFactory {

    private final JsonFactory jsonFactory;
    private final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> unmarshallers;
    private final Map<UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeMarshallers;

    public SdkStructuredJsonFactoryImpl(JsonFactory jsonFactory,
                                        Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> unmarshallers,
                                        Map<UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeMarshallers) {
        this.jsonFactory = jsonFactory;
        this.unmarshallers = unmarshallers;
        this.customTypeMarshallers = customTypeMarshallers;
    }

    @Override
    public StructuredJsonGenerator createWriter(String contentType) {
        return createWriter(jsonFactory, contentType);
    }

    protected abstract StructuredJsonGenerator createWriter(JsonFactory jsonFactory,
                                                            String contentType);

    @Override
    public <T> JsonResponseHandler<T> createResponseHandler(JsonOperationMetadata operationMetadata,
                                                            Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller) {
        return new JsonResponseHandler(responseUnmarshaller, unmarshallers, customTypeMarshallers, jsonFactory,
                                       operationMetadata.isHasStreamingSuccessResponse(),
                                       operationMetadata.isPayloadJson());
    }

    @Override
    public JsonErrorResponseHandler createErrorResponseHandler(
            final List<JsonErrorUnmarshaller> errorUnmarshallers, String customErrorCodeFieldName) {
        return new JsonErrorResponseHandler(errorUnmarshallers,
                                            getErrorCodeParser(customErrorCodeFieldName),
                                            JsonErrorMessageParser.DEFAULT_ERROR_MESSAGE_PARSER,
                                            jsonFactory);
    }

    protected ErrorCodeParser getErrorCodeParser(String customErrorCodeFieldName) {
        return new JsonErrorCodeParser(customErrorCodeFieldName);
    }

}
