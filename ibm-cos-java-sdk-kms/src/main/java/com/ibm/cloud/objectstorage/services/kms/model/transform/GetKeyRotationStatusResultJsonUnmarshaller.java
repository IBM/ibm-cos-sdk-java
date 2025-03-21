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

import java.math.*;

import javax.annotation.Generated;

import com.ibm.cloud.objectstorage.services.kms.model.*;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.*;
import com.ibm.cloud.objectstorage.transform.*;

import com.fasterxml.jackson.core.JsonToken;
import static com.fasterxml.jackson.core.JsonToken.*;

/**
 * GetKeyRotationStatusResult JSON Unmarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class GetKeyRotationStatusResultJsonUnmarshaller implements Unmarshaller<GetKeyRotationStatusResult, JsonUnmarshallerContext> {

    public GetKeyRotationStatusResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GetKeyRotationStatusResult getKeyRotationStatusResult = new GetKeyRotationStatusResult();

        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;

        JsonToken token = context.getCurrentToken();
        if (token == null)
            token = context.nextToken();
        if (token == VALUE_NULL) {
            return getKeyRotationStatusResult;
        }

        while (true) {
            if (token == null)
                break;

            if (token == FIELD_NAME || token == START_OBJECT) {
                if (context.testExpression("KeyRotationEnabled", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setKeyRotationEnabled(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("RotationPeriodInDays", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setRotationPeriodInDays(context.getUnmarshaller(Integer.class).unmarshall(context));
                }
                if (context.testExpression("NextRotationDate", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setNextRotationDate(DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("OnDemandRotationStartDate", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setOnDemandRotationStartDate(DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
            } else if (token == END_ARRAY || token == END_OBJECT) {
                if (context.getLastParsedParentElement() == null || context.getLastParsedParentElement().equals(currentParentElement)) {
                    if (context.getCurrentDepth() <= originalDepth)
                        break;
                }
            }
            token = context.nextToken();
        }

        return getKeyRotationStatusResult;
    }

    private static GetKeyRotationStatusResultJsonUnmarshaller instance;

    public static GetKeyRotationStatusResultJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new GetKeyRotationStatusResultJsonUnmarshaller();
        return instance;
    }
}
