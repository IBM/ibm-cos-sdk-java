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

import java.util.Map;
import java.util.List;
import javax.annotation.Generated;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.services.kms.model.*;

import com.ibm.cloud.objectstorage.protocol.*;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;

/**
 * EncryptRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class EncryptRequestMarshaller {

    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("KeyId").build();
    private static final MarshallingInfo<java.nio.ByteBuffer> PLAINTEXT_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Plaintext").build();
    private static final MarshallingInfo<Map> ENCRYPTIONCONTEXT_BINDING = MarshallingInfo.builder(MarshallingType.MAP)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionContext").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("GrantTokens").build();
    private static final MarshallingInfo<String> ENCRYPTIONALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING)
            .marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionAlgorithm").build();
    private static final MarshallingInfo<Boolean> DRYRUN_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD)
            .marshallLocationName("DryRun").build();

    private static final EncryptRequestMarshaller instance = new EncryptRequestMarshaller();

    public static EncryptRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(EncryptRequest encryptRequest, ProtocolMarshaller protocolMarshaller) {

        if (encryptRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
            protocolMarshaller.marshall(encryptRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(encryptRequest.getPlaintext(), PLAINTEXT_BINDING);
            protocolMarshaller.marshall(encryptRequest.getEncryptionContext(), ENCRYPTIONCONTEXT_BINDING);
            protocolMarshaller.marshall(encryptRequest.getGrantTokens(), GRANTTOKENS_BINDING);
            protocolMarshaller.marshall(encryptRequest.getEncryptionAlgorithm(), ENCRYPTIONALGORITHM_BINDING);
            protocolMarshaller.marshall(encryptRequest.getDryRun(), DRYRUN_BINDING);
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
