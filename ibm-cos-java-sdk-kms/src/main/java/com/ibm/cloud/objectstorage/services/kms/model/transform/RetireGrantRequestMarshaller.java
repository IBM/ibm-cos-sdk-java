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
import com.ibm.cloud.objectstorage.services.kms.model.*;

import com.ibm.cloud.objectstorage.protocol.*;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;

/**
 * RetireGrantRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class RetireGrantRequestMarshaller {

    private static final MarshallingInfo<String> GRANTTOKEN_BINDING = MarshallingInfo.builder(MarshallingType.STRING)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantToken").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> GRANTID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("GrantId").build();
    private static final MarshallingInfo<Boolean> DRYRUN_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("DryRun").build();

    private static final RetireGrantRequestMarshaller instance = new RetireGrantRequestMarshaller();

    public static RetireGrantRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(RetireGrantRequest retireGrantRequest, ProtocolMarshaller protocolMarshaller) {

        if (retireGrantRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            protocolMarshaller.marshall(retireGrantRequest.getGrantToken(), GRANTTOKEN_BINDING);
            protocolMarshaller.marshall(retireGrantRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(retireGrantRequest.getGrantId(), GRANTID_BINDING);
            protocolMarshaller.marshall(retireGrantRequest.getDryRun(), DRYRUN_BINDING);
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
