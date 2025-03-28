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
 * The request was rejected because the signature verification failed. Signature verification fails when it cannot
 * confirm that signature was produced by signing the specified message with the specified KMS key and signing
 * algorithm.
 * </p>
 */
@Generated("com.ibm.cloud.objectstorage:ibm-cos-java-sdk-code-generator")
public class KMSInvalidSignatureException extends AWSKMSException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new KMSInvalidSignatureException with the specified error message.
     *
     * @param message
     *        Describes the error encountered.
     */
    public KMSInvalidSignatureException(String message) {
        super(message);
    }

}
