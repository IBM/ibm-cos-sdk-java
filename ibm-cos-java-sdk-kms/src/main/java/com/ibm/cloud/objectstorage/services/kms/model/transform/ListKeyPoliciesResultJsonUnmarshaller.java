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
 * ListKeyPoliciesResult JSON Unmarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class ListKeyPoliciesResultJsonUnmarshaller implements Unmarshaller<ListKeyPoliciesResult, JsonUnmarshallerContext> {

    public ListKeyPoliciesResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ListKeyPoliciesResult listKeyPoliciesResult = new ListKeyPoliciesResult();

        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;

        JsonToken token = context.getCurrentToken();
        if (token == null)
            token = context.nextToken();
        if (token == VALUE_NULL) {
            return listKeyPoliciesResult;
        }

        while (true) {
            if (token == null)
                break;

            if (token == FIELD_NAME || token == START_OBJECT) {
                if (context.testExpression("PolicyNames", targetDepth)) {
                    context.nextToken();
                    listKeyPoliciesResult.setPolicyNames(new ListUnmarshaller<String>(context.getUnmarshaller(String.class))

                    .unmarshall(context));
                }
                if (context.testExpression("NextMarker", targetDepth)) {
                    context.nextToken();
                    listKeyPoliciesResult.setNextMarker(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Truncated", targetDepth)) {
                    context.nextToken();
                    listKeyPoliciesResult.setTruncated(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (token == END_ARRAY || token == END_OBJECT) {
                if (context.getLastParsedParentElement() == null || context.getLastParsedParentElement().equals(currentParentElement)) {
                    if (context.getCurrentDepth() <= originalDepth)
                        break;
                }
            }
            token = context.nextToken();
        }

        return listKeyPoliciesResult;
    }

    private static ListKeyPoliciesResultJsonUnmarshaller instance;

    public static ListKeyPoliciesResultJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new ListKeyPoliciesResultJsonUnmarshaller();
        return instance;
    }
}
