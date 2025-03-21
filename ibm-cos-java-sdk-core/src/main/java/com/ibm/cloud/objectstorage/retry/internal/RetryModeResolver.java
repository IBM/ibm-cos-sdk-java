/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.ibm.cloud.objectstorage.retry.internal;

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.AWS_RETRY_MODE_ENV_VAR;
import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.AWS_RETRY_MODE_SYSTEM_PROPERTY;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.auth.profile.internal.BasicProfile;
import com.ibm.cloud.objectstorage.auth.profile.internal.BasicProfileConfigFileLoader;
import com.ibm.cloud.objectstorage.internal.config.InternalConfig;
import com.ibm.cloud.objectstorage.profile.path.AwsProfileFileLocationProvider;
import com.ibm.cloud.objectstorage.retry.RetryMode;

/**
 * Resolves the retryMode in the following order:
 *
 * <ul>
 *   <li>Environment Variable</li>
 *   <li>Java System Properties</li>
 *   <li>Credential config file at the default location (~/.aws/config) shared by all AWS SDKs and the AWS CLI</li>
 * </ul>
 */
@SdkInternalApi
public final class RetryModeResolver {
    private static final String PROFILE_PROPERTY = "retry_mode";
    private static final RetryMode SDK_DEFAULT_RETRY_MODE = RetryMode.LEGACY;

    private final BasicProfileConfigFileLoader configFileLoader;
    private final InternalConfig internalConfig;
    private final RetryMode retryMode;

    public RetryModeResolver() {
        this(BasicProfileConfigFileLoader.INSTANCE, InternalConfig.Factory.getInternalConfig());
    }

    @SdkTestInternalApi
    RetryModeResolver(BasicProfileConfigFileLoader configFileLoader, InternalConfig internalConfig) {
        this.configFileLoader = configFileLoader;
        this.internalConfig = internalConfig;
        this.retryMode = resolveRetryMode();
    }

    /**
     * @return the resolved retry mode. If not found, {@link RetryMode#LEGACY} will be returned
     */
    public RetryMode retryMode() {
        return retryMode;
    }

    private RetryMode systemProperty() {
        return RetryMode.fromName(System.getProperty(AWS_RETRY_MODE_SYSTEM_PROPERTY));
    }

    private RetryMode envVar() {
        return RetryMode.fromName(System.getenv(AWS_RETRY_MODE_ENV_VAR));
    }

    private RetryMode internalDefault() {
        return RetryMode.fromName(internalConfig.getDefaultRetryMode());
    }

    private RetryMode resolveRetryMode() {
        RetryMode mode = envVar();

        if (mode != null) {
            return mode;
        }

        mode = systemProperty();
        if (mode != null) {
            return mode;
        }

        mode = profile();
        if (mode != null) {
            return mode;
        }
        
        mode = internalDefault();
        if (mode != null) {
            return mode;
        }
        
        return SDK_DEFAULT_RETRY_MODE;
    }

    private RetryMode profile() {
        BasicProfile profile = configFileLoader.getProfile();
        if (profile == null) {
            return null;
        }
        String val = profile.getPropertyValue(PROFILE_PROPERTY);

        return RetryMode.fromName(val);
    }
}
