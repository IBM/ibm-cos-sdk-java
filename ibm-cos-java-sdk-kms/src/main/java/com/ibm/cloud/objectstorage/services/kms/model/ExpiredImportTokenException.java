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
 * The request was rejected because the specified import token is expired. Use <a>GetParametersForImport</a> to get a
 * new import token and public key, use the new public key to encrypt the key material, and then try the request again.
 * </p>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class ExpiredImportTokenException extends com.ibm.cloud.objectstorage.services.kms.model.AWSKMSException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ExpiredImportTokenException with the specified error message.
     *
     * @param message
     *        Describes the error encountered.
     */
    public ExpiredImportTokenException(String message) {
        super(message);
    }

}
