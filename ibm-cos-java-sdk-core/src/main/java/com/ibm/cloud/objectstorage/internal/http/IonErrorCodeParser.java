/*
 * Copyright 2016-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.internal.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.protocol.json.JsonContent;
import com.ibm.cloud.objectstorage.util.IOUtils;

import com.amazon.ion.IonReader;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonType;

@SdkInternalApi
public class IonErrorCodeParser implements ErrorCodeParser {
    private static final Log log = LogFactory.getLog(IonErrorCodeParser.class);

    private static final String TYPE_PREFIX = "aws-type:";
    private static final String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";

    private final IonSystem ionSystem;

    public IonErrorCodeParser(IonSystem ionSystem) {
        this.ionSystem = ionSystem;
    }

    @Override
    public String parseErrorCode(HttpResponse response, JsonContent jsonContents) {
        IonReader reader = ionSystem.newReader(jsonContents.getRawContent());
        try {
            IonType type = reader.next();
            if (type != IonType.STRUCT) {
                throw new SdkClientException(String.format("Can only get error codes from structs (saw %s), request id %s", type, getRequestId(response)));
            }

            boolean errorCodeSeen = false;
            String errorCode = null;
            String[] annotations = reader.getTypeAnnotations();
            for (String annotation : annotations) {
                if (annotation.startsWith(TYPE_PREFIX)) {
                    if (errorCodeSeen) {
                        throw new SdkClientException(String.format("Multiple error code annotations found for request id %s", getRequestId(response)));
                    } else {
                        errorCodeSeen = true;
                        errorCode = annotation.substring(TYPE_PREFIX.length());
                    }
                }
            }

            return errorCode;
        } finally {
            IOUtils.closeQuietly(reader, log);
        }
    }

    private static String getRequestId(HttpResponse response) {
        return response.getHeaders().get(X_AMZN_REQUEST_ID_HEADER);
    }
}
