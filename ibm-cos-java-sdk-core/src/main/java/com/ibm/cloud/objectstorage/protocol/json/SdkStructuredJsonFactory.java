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

package com.ibm.cloud.objectstorage.protocol.json;

import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.http.JsonErrorResponseHandler;
import com.ibm.cloud.objectstorage.http.JsonResponseHandler;
import com.ibm.cloud.objectstorage.transform.JsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;

import java.util.List;

/**
 * Common interface for creating generators (writers) and protocol handlers for JSON like protocols.
 * Current implementations include {@link SdkStructuredPlainJsonFactory} and {@link
 * SdkStructuredCborFactory}
 */
@SdkProtectedApi
public interface SdkStructuredJsonFactory {

    /**
     * Returns the {@link StructuredJsonGenerator} to be used for marshalling the request.
     *
     * @param contentType Content type to send for requests.
     */
    StructuredJsonGenerator createWriter(String contentType);

    /**
     * Returns the response handler to be used for handling a successfull response.
     *
     * @param operationMetadata Additional context information about an operation to create the
     *                          appropriate response handler.
     */
    <T> JsonResponseHandler<T> createResponseHandler(JsonOperationMetadata operationMetadata,
                                                     Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller);

    /**
     * Returns the error response handler for handling a error response.
     *
     * @param errorUnmarshallers Response unmarshallers to unamrshall the error responses.
     *
     * @deprecated Use {@link #createErrorResponseHandler(JsonErrorResponseMetadata, List)} instead
     */
    @Deprecated
    JsonErrorResponseHandler createErrorResponseHandler(
            List<JsonErrorUnmarshaller> errorUnmarshallers, String customErrorCodeFieldName);

    JsonErrorResponseHandler createErrorResponseHandler(
            JsonErrorResponseMetadata jsonErrorResponseMetadata,
            List<JsonErrorUnmarshaller> errorUnmarshallers
    );
}
