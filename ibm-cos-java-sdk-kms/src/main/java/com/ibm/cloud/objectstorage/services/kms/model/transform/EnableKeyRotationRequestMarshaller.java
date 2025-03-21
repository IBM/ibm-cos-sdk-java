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
 * EnableKeyRotationRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class EnableKeyRotationRequestMarshaller {

    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("KeyId").build();
    private static final MarshallingInfo<Integer> ROTATIONPERIODINDAYS_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("RotationPeriodInDays").build();

    private static final EnableKeyRotationRequestMarshaller instance = new EnableKeyRotationRequestMarshaller();

    public static EnableKeyRotationRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(EnableKeyRotationRequest enableKeyRotationRequest, ProtocolMarshaller protocolMarshaller) {

        if (enableKeyRotationRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            protocolMarshaller.marshall(enableKeyRotationRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(enableKeyRotationRequest.getRotationPeriodInDays(), ROTATIONPERIODINDAYS_BINDING);
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
