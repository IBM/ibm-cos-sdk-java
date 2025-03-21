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
 * ImportKeyMaterialRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class ImportKeyMaterialRequestMarshaller {

    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("KeyId").build();
    private static final MarshallingInfo<java.nio.ByteBuffer> IMPORTTOKEN_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ImportToken").build();
    private static final MarshallingInfo<java.nio.ByteBuffer> ENCRYPTEDKEYMATERIAL_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptedKeyMaterial").build();
    private static final MarshallingInfo<java.util.Date> VALIDTO_BINDING = MarshallingInfo.builder(MarshallingType.DATE)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ValidTo").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<String> EXPIRATIONMODEL_BINDING = MarshallingInfo.builder(MarshallingType.STRING)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ExpirationModel").build();

    private static final ImportKeyMaterialRequestMarshaller instance = new ImportKeyMaterialRequestMarshaller();

    public static ImportKeyMaterialRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(ImportKeyMaterialRequest importKeyMaterialRequest, ProtocolMarshaller protocolMarshaller) {

        if (importKeyMaterialRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            protocolMarshaller.marshall(importKeyMaterialRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getImportToken(), IMPORTTOKEN_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getEncryptedKeyMaterial(), ENCRYPTEDKEYMATERIAL_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getValidTo(), VALIDTO_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getExpirationModel(), EXPIRATIONMODEL_BINDING);
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
