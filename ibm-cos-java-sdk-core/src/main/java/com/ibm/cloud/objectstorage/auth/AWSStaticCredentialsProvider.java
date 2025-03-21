/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.auth;

import com.ibm.cloud.objectstorage.util.ValidationUtils;

/**
 * Simple implementation of AWSCredentialsProvider that just wraps static AWSCredentials.
 * <p>
 * <p>
 * See <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-client-credentials.html">Migration Guide</a>
 * for more information.
 */
public class AWSStaticCredentialsProvider implements AWSCredentialsProvider {

    private final AWSCredentials credentials;

    public AWSStaticCredentialsProvider(AWSCredentials credentials) {
        this.credentials = ValidationUtils.assertNotNull(credentials, "credentials");
    }

    public AWSCredentials getCredentials() {
        return credentials;
    }

    public void refresh() {
    }

}
