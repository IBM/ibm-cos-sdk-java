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

import static com.fasterxml.jackson.core.JsonToken.*;

/**
 * DeleteAliasResult JSON Unmarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class DeleteAliasResultJsonUnmarshaller implements Unmarshaller<DeleteAliasResult, JsonUnmarshallerContext> {

    public DeleteAliasResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DeleteAliasResult deleteAliasResult = new DeleteAliasResult();

        return deleteAliasResult;
    }

    private static DeleteAliasResultJsonUnmarshaller instance;

    public static DeleteAliasResultJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new DeleteAliasResultJsonUnmarshaller();
        return instance;
    }
}
