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
package com.ibm.cloud.objectstorage.services.kms.model;

import javax.annotation.Generated;

/**
 * <p>
 * From the <a>Decrypt</a> or <a>ReEncrypt</a> operation, the request was rejected because the specified ciphertext, or
 * additional authenticated data incorporated into the ciphertext, such as the encryption context, is corrupted,
 * missing, or otherwise invalid.
 * </p>
 * <p>
 * From the <a>ImportKeyMaterial</a> operation, the request was rejected because KMS could not decrypt the encrypted
 * (wrapped) key material.
 * </p>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class InvalidCiphertextException extends com.ibm.cloud.objectstorage.services.kms.model.AWSKMSException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InvalidCiphertextException with the specified error message.
     *
     * @param message
     *        Describes the error encountered.
     */
    public InvalidCiphertextException(String message) {
        super(message);
    }

}
