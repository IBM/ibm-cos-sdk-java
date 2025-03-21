/*
 * Copyright 2019-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.kms.model.transform;

import com.fasterxml.jackson.core.JsonToken;
import com.ibm.cloud.objectstorage.services.kms.model.DryRunOperationException;
import com.ibm.cloud.objectstorage.transform.EnhancedJsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;

import javax.annotation.Generated;

import static com.fasterxml.jackson.core.JsonToken.*;

/**
 * DryRunOperationException JSON Unmarshaller
 */
@Generated("com.ibm.cloud.objectstorage:ibm-cos-java-sdk-code-generator")
public class DryRunOperationExceptionUnmarshaller extends EnhancedJsonErrorUnmarshaller {
    private DryRunOperationExceptionUnmarshaller() {
        super(DryRunOperationException.class, "DryRunOperationException");
    }

    @Override
    public DryRunOperationException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        DryRunOperationException dryRunOperationException = new DryRunOperationException(null);

        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;

        JsonToken token = context.getCurrentToken();
        if (token == null)
            token = context.nextToken();
        if (token == VALUE_NULL) {
            return null;
        }

        while (true) {
            if (token == null)
                break;

            if (token == FIELD_NAME || token == START_OBJECT) {
            } else if (token == END_ARRAY || token == END_OBJECT) {
                if (context.getLastParsedParentElement() == null || context.getLastParsedParentElement().equals(currentParentElement)) {
                    if (context.getCurrentDepth() <= originalDepth)
                        break;
                }
            }
            token = context.nextToken();
        }
        return dryRunOperationException;
    }

    private static DryRunOperationExceptionUnmarshaller instance;

    public static DryRunOperationExceptionUnmarshaller getInstance() {
        if (instance == null)
            instance = new DryRunOperationExceptionUnmarshaller();
        return instance;
    }
}
