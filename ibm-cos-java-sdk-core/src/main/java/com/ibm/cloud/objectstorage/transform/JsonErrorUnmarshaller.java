/*
 * Copyright 2015-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.transform;

import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.ThreadSafe;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * Unmarshaller for JSON error responses from AWS services.
 */
@SdkInternalApi
@ThreadSafe
public class JsonErrorUnmarshaller extends AbstractErrorUnmarshaller<JsonNode> {

    public static final JsonErrorUnmarshaller DEFAULT_UNMARSHALLER = new JsonErrorUnmarshaller(
            AmazonServiceException.class, null);

    private static final ObjectMapper MAPPER;

    private final String handledErrorCode;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // If a customer is using an older Jackson version than 2.12.x, it will throw an error
        // and an upgrade to a newer version above 2.16.x is recommended
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
    }

    /**
     * @param exceptionClass   Exception class this unmarshaller will attempt to deserialize error response into
     * @param handledErrorCode AWS error code that this unmarshaller handles. Pass null to handle all exceptions
     */
    public JsonErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass, String handledErrorCode) {
        super(exceptionClass);
        this.handledErrorCode = handledErrorCode;
    }

    /**
     * @param jsonContent The {@link JsonNode} for the error to un-marshall. Can be null.
     * @return The {@link AmazonServiceException} created from the jsonContent or null if one couldn't be found.
     * @throws Exception If there are issues processing the jsonContent.
     */
    @Override
    public AmazonServiceException unmarshall(JsonNode jsonContent) throws Exception {
        if (jsonContent == null || NullNode.instance.equals(jsonContent)) {
            return null;
        }
        return MAPPER.treeToValue(jsonContent, exceptionClass);
    }

    /**
     * @param actualErrorCode Actual AWS error code found in the error response.
     * @return True if the actualErrorCode can be handled by this unmarshaller, false otherwise
     */
    public boolean matchErrorCode(String actualErrorCode) {
        if (handledErrorCode == null) {
            return true;
        }
        return handledErrorCode.equals(actualErrorCode);
    }

}
