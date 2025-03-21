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
 * CreateGrantResult JSON Unmarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class CreateGrantResultJsonUnmarshaller implements Unmarshaller<CreateGrantResult, JsonUnmarshallerContext> {

    public CreateGrantResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        CreateGrantResult createGrantResult = new CreateGrantResult();

        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;

        JsonToken token = context.getCurrentToken();
        if (token == null)
            token = context.nextToken();
        if (token == VALUE_NULL) {
            return createGrantResult;
        }

        while (true) {
            if (token == null)
                break;

            if (token == FIELD_NAME || token == START_OBJECT) {
                if (context.testExpression("GrantToken", targetDepth)) {
                    context.nextToken();
                    createGrantResult.setGrantToken(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("GrantId", targetDepth)) {
                    context.nextToken();
                    createGrantResult.setGrantId(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (token == END_ARRAY || token == END_OBJECT) {
                if (context.getLastParsedParentElement() == null || context.getLastParsedParentElement().equals(currentParentElement)) {
                    if (context.getCurrentDepth() <= originalDepth)
                        break;
                }
            }
            token = context.nextToken();
        }

        return createGrantResult;
    }

    private static CreateGrantResultJsonUnmarshaller instance;

    public static CreateGrantResultJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new CreateGrantResultJsonUnmarshaller();
        return instance;
    }
}
