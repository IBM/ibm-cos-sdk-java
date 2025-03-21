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
 * GenerateRandomRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class GenerateRandomRequestMarshaller {

    private static final MarshallingInfo<Integer> NUMBEROFBYTES_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("NumberOfBytes").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    // IBM Unsupported
    // private static final MarshallingInfo<StructuredPojo> RECIPIENT_BINDING = MarshallingInfo.builder(MarshallingType.STRUCTURED)
    //         .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Recipient").build();

    private static final GenerateRandomRequestMarshaller instance = new GenerateRandomRequestMarshaller();

    public static GenerateRandomRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(GenerateRandomRequest generateRandomRequest, ProtocolMarshaller protocolMarshaller) {

        if (generateRandomRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            protocolMarshaller.marshall(generateRandomRequest.getNumberOfBytes(), NUMBEROFBYTES_BINDING);
            protocolMarshaller.marshall(generateRandomRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
            // IBM Unsupported
            // protocolMarshaller.marshall(generateRandomRequest.getRecipient(), RECIPIENT_BINDING);
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
