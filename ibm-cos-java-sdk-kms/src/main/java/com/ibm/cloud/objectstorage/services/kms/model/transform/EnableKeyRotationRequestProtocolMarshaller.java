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
 * EnableKeyRotationRequest Marshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class EnableKeyRotationRequestProtocolMarshaller implements Marshaller<Request<EnableKeyRotationRequest>, EnableKeyRotationRequest> {

    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/")
            .httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.EnableKeyRotation")
            .serviceName("AWSKMS").build();

    private final com.ibm.cloud.objectstorage.protocol.json.SdkJsonProtocolFactory protocolFactory;

    public EnableKeyRotationRequestProtocolMarshaller(com.ibm.cloud.objectstorage.protocol.json.SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public Request<EnableKeyRotationRequest> marshall(EnableKeyRotationRequest enableKeyRotationRequest) {

        if (enableKeyRotationRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            final ProtocolRequestMarshaller<EnableKeyRotationRequest> protocolMarshaller = protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING,
                    enableKeyRotationRequest);

            protocolMarshaller.startMarshalling();
            EnableKeyRotationRequestMarshaller.getInstance().marshall(enableKeyRotationRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
