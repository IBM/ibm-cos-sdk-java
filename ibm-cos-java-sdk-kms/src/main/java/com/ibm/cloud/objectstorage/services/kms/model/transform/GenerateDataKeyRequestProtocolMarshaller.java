/*
 * Copyright 2017-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import javax.annotation.Generated;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.Request;

import com.ibm.cloud.objectstorage.http.HttpMethodName;
import com.ibm.cloud.objectstorage.services.kms.model.*;
import com.ibm.cloud.objectstorage.transform.Marshaller;

import com.ibm.cloud.objectstorage.protocol.*;
import com.ibm.cloud.objectstorage.protocol.Protocol;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;

/**
 * GenerateDataKeyRequest Marshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class GenerateDataKeyRequestProtocolMarshaller implements Marshaller<Request<GenerateDataKeyRequest>, GenerateDataKeyRequest> {

    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/")
            .httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.GenerateDataKey")
            .serviceName("AWSKMS").build();

    private final com.ibm.cloud.objectstorage.protocol.json.SdkJsonProtocolFactory protocolFactory;

    public GenerateDataKeyRequestProtocolMarshaller(com.ibm.cloud.objectstorage.protocol.json.SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public Request<GenerateDataKeyRequest> marshall(GenerateDataKeyRequest generateDataKeyRequest) {

        if (generateDataKeyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            final ProtocolRequestMarshaller<GenerateDataKeyRequest> protocolMarshaller = protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING,
                    generateDataKeyRequest);

            protocolMarshaller.startMarshalling();
            GenerateDataKeyRequestMarshaller.getInstance().marshall(generateDataKeyRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
