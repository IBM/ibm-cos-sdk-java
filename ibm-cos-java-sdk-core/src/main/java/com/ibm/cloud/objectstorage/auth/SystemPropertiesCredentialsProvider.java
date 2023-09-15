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

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.util.StringUtils;

/**
 * {@link AWSCredentialsProvider} implementation that provides credentials by
 * looking at the <code>aws.accessKeyId</code> and <code>aws.secretKey</code>
 * Java system properties.
 */
public class SystemPropertiesCredentialsProvider implements AWSCredentialsProvider {

    private TokenManager tokenManager = null;

    @Override
    public AWSCredentials getCredentials() {
        //Load IBM properties

        String accessKey = StringUtils.trim(System.getProperty(ACCESS_KEY_SYSTEM_PROPERTY));

        String secretKey = StringUtils.trim(System.getProperty(SECRET_KEY_SYSTEM_PROPERTY));

        String apiKey = StringUtils.trim(System.getProperty(SDKGlobalConfiguration.IBM_API_KEY_SYSTEM_PROPERTY));
        String serviceInstance = StringUtils.trim(System.getProperty(SDKGlobalConfiguration.IBM_SERVICE_INSTANCE_ID_SYSTEM_PROPERTY));

        if (!StringUtils.isNullOrEmpty(apiKey) && tokenManager == null) {
            BasicIBMOAuthCredentials oAuthCreds = new BasicIBMOAuthCredentials(apiKey, serviceInstance);
            tokenManager = oAuthCreds.getTokenManager();
            return oAuthCreds;
        } else if ((!StringUtils.isNullOrEmpty(apiKey) && tokenManager != null)) {
            return new BasicIBMOAuthCredentials(tokenManager, serviceInstance);
        }

        if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey)) {
            throw new SdkClientException(
                    "Unable to load AWS credentials from Java system "
                    + "properties (" + ACCESS_KEY_SYSTEM_PROPERTY + " and "
                    + SECRET_KEY_SYSTEM_PROPERTY + ")");
        }
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Override
    public void refresh() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}