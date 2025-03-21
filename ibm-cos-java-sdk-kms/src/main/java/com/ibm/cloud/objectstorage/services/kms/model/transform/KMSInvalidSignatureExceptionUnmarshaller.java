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
 * KMSInvalidSignatureException JSON Unmarshaller
 */
@Generated("com.ibm.cloud.objectstorage:ibm-cos-java-sdk-code-generator")
public class KMSInvalidSignatureExceptionUnmarshaller extends EnhancedJsonErrorUnmarshaller {
    private KMSInvalidSignatureExceptionUnmarshaller() {
        super(com.ibm.cloud.objectstorage.services.kms.model.KMSInvalidSignatureException.class, "KMSInvalidSignatureException");
    }

    @Override
    public com.ibm.cloud.objectstorage.services.kms.model.KMSInvalidSignatureException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        com.ibm.cloud.objectstorage.services.kms.model.KMSInvalidSignatureException kMSInvalidSignatureException = new com.ibm.cloud.objectstorage.services.kms.model.KMSInvalidSignatureException(
                null);

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
        return kMSInvalidSignatureException;
    }

    private static KMSInvalidSignatureExceptionUnmarshaller instance;

    public static KMSInvalidSignatureExceptionUnmarshaller getInstance() {
        if (instance == null)
            instance = new KMSInvalidSignatureExceptionUnmarshaller();
        return instance;
    }
}
