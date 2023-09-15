/*
 * Copyright 2011-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.profile.path;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.profile.path.config.SharedConfigDefaultLocationProvider;
import com.ibm.cloud.objectstorage.profile.path.config.ConfigEnvVarOverrideLocationProvider;
import com.ibm.cloud.objectstorage.profile.path.cred.CredentialsDefaultLocationProvider;
import com.ibm.cloud.objectstorage.profile.path.cred.CredentialsEnvVarOverrideLocationProvider;
import com.ibm.cloud.objectstorage.profile.path.cred.CredentialsLegacyConfigLocationProvider;

import java.io.File;
import com.ibm.cloud.objectstorage.profile.path.cred.IBMJsonCredentialsDefaultLocationProvider;
import com.ibm.cloud.objectstorage.profile.path.cred.IBMJsonCredentialsEnvVarOverrideLocationProvider;

/**
 * Provides the location of both the AWS Shared credentials file (~/.aws/credentials) or the AWS
 * Shared config file (~/.aws/config).
 */
@SdkInternalApi
public interface AwsProfileFileLocationProvider {

    /**
     * Location provider for the shared AWS credentials file. Checks the environment variable override
     * first, then checks the default location (~/.aws/credentials), and finally falls back to the
     * legacy config file (~/.aws/config) that we still support loading credentials from.
     */
    AwsProfileFileLocationProvider DEFAULT_CREDENTIALS_LOCATION_PROVIDER = new AwsProfileFileLocationProviderChain(
            new CredentialsEnvVarOverrideLocationProvider(), new CredentialsDefaultLocationProvider(),
            new CredentialsLegacyConfigLocationProvider());

    /**
     * Location provider for the shared AWS Config file. Checks environment variable override first then
     * falls back to the default location (~/.aws/config) if not present.
     */
    AwsProfileFileLocationProvider DEFAULT_CONFIG_LOCATION_PROVIDER = new AwsProfileFileLocationProviderChain(
            new ConfigEnvVarOverrideLocationProvider(), new SharedConfigDefaultLocationProvider());

    /**
     * Location provider for the shared IBM credentials file. Checks the environment variable override
     * first, then checks the default location (~/.bluemix/cos_credentials).
     */
    AwsProfileFileLocationProvider IBM_CREDENTIALS_LOCATION_PROVIDER = new AwsProfileFileLocationProviderChain(
            new IBMJsonCredentialsEnvVarOverrideLocationProvider(), new IBMJsonCredentialsDefaultLocationProvider());

    /**
     * @return Location of file containing profile data. Null if implementation cannot provide the
     * location.
     */
    File getLocation();
}
