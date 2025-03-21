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
package com.ibm.cloud.objectstorage.protocol.json;

import com.ibm.cloud.objectstorage.annotation.NotThreadSafe;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;

import java.util.List;

/**
 * Contains various metadata needed to create an appropriate {@link com.ibm.cloud.objectstorage.http.JsonErrorResponseHandler}
 * for the client.
 */
@NotThreadSafe
@SdkProtectedApi
public class JsonErrorResponseMetadata {

    /**
     * Custom error code field name in exceptional responses. Currently only used by glacier. If not
     * set the default error code parser will be used.
     */
    private String customErrorCodeFieldName;

    /**
     * Error shapes scoped to a specific operation.
     */
    private List<JsonErrorShapeMetadata> errorShapes;

    /**
     * Correlates to AwsQueryCompatible trait
     */
    private boolean hasAwsQueryCompatible;

    public String getCustomErrorCodeFieldName() {
        return customErrorCodeFieldName;
    }

    public JsonErrorResponseMetadata withCustomErrorCodeFieldName(String errorCodeFieldName) {
        this.customErrorCodeFieldName = errorCodeFieldName;
        return this;
    }

    public List<JsonErrorShapeMetadata> getErrorShapes() {
        return errorShapes;
    }

    public JsonErrorResponseMetadata withErrorShapes(List<JsonErrorShapeMetadata> errorShapes) {
        this.errorShapes = errorShapes;
        return this;
    }

    public boolean getAwsQueryCompatible() {
        return hasAwsQueryCompatible;
    }

    public JsonErrorResponseMetadata withAwsQueryCompatible(Boolean hasAwsQueryCompatible) {
        this.hasAwsQueryCompatible = hasAwsQueryCompatible != null && hasAwsQueryCompatible;
        return this;
    }
}
