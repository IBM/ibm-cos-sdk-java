/*
 * Copyright 2012-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.ACCESS_KEY_ENV_VAR;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.ALTERNATE_ACCESS_KEY_ENV_VAR;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.ALTERNATE_SECRET_KEY_ENV_VAR;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.AWS_SESSION_TOKEN_ENV_VAR;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.SECRET_KEY_ENV_VAR;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.util.StringUtils;

/**
 * {@link AWSCredentialsProvider} implementation that provides credentials by looking at the: <code>AWS_ACCESS_KEY_ID</code> (or
 * <code>AWS_ACCESS_KEY</code>) and <code>AWS_SECRET_KEY</code> (or <code>AWS_SECRET_ACCESS_KEY</code>) environment variables. If
 * the <code>AWS_SESSION_TOKEN</code> environment variable is also set then temporary credentials will be used.
 */
public class EnvironmentVariableCredentialsProvider implements AWSCredentialsProvider {
    BasicIBMOAuthCredentials oAuthCredentials;

    @Override
    public AWSCredentials getCredentials() {
        if (System.getenv(SDKGlobalConfiguration.IBM_API_KEY) != null) {
            String apiKey = System.getenv(SDKGlobalConfiguration.IBM_API_KEY);
            String serviceInstanceId = System.getenv(SDKGlobalConfiguration.IBM_SERVICE_INSTANCE_ID);
            if (oAuthCredentials == null) {
                oAuthCredentials = new BasicIBMOAuthCredentials(apiKey, serviceInstanceId);
            }
            return oAuthCredentials;
        } else {
            String accessKey = System.getenv(ACCESS_KEY_ENV_VAR);
            if (accessKey == null) {
                accessKey = System.getenv(ALTERNATE_ACCESS_KEY_ENV_VAR);
            }
    
            String secretKey = System.getenv(SECRET_KEY_ENV_VAR);
            if (secretKey == null) {
                secretKey = System.getenv(ALTERNATE_SECRET_KEY_ENV_VAR);
            }
    
            accessKey = StringUtils.trim(accessKey);
            secretKey = StringUtils.trim(secretKey);

        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey)) {

            throw new SdkClientException(
                    "Unable to load AWS credentials from environment variables " +
                    "(" + ACCESS_KEY_ENV_VAR + " (or " + ALTERNATE_ACCESS_KEY_ENV_VAR + ") and " +
                    SECRET_KEY_ENV_VAR + " (or " + ALTERNATE_SECRET_KEY_ENV_VAR + "))");
        }

        String sessionToken = StringUtils.trim(System.getenv(AWS_SESSION_TOKEN_ENV_VAR));
        return StringUtils.isNullOrEmpty(sessionToken) ?
                new BasicAWSCredentials(accessKey, secretKey)
                :
                new BasicSessionCredentials(accessKey, secretKey, sessionToken);
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}