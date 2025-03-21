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
package com.ibm.cloud.objectstorage.services.kms;

import javax.annotation.Generated;

import com.ibm.cloud.objectstorage.ClientConfigurationFactory;

import com.ibm.cloud.objectstorage.annotation.NotThreadSafe;
import com.ibm.cloud.objectstorage.client.builder.AwsSyncClientBuilder;
import com.ibm.cloud.objectstorage.client.AwsSyncClientParams;

/**
 * Fluent builder for {@link com.ibm.cloud.objectstorage.services.kms.AWSKMS}. Use of the builder is preferred over using constructors
 * of the client class.
 **/
@NotThreadSafe
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public final class AWSKMSClientBuilder extends AwsSyncClientBuilder<AWSKMSClientBuilder, AWSKMS> {

    private static final ClientConfigurationFactory CLIENT_CONFIG_FACTORY = new ClientConfigurationFactory();

    /**
     * @return Create new instance of builder with all defaults set.
     */
    public static AWSKMSClientBuilder standard() {
        return new AWSKMSClientBuilder();
    }

    /**
     * @return Default client using the {@link com.ibm.cloud.objectstorage.auth.DefaultAWSCredentialsProviderChain} and
     *         {@link com.ibm.cloud.objectstorage.regions.DefaultAwsRegionProviderChain} chain
     */
    public static AWSKMS defaultClient() {
        return standard().build();
    }

    private AWSKMSClientBuilder() {
        super(CLIENT_CONFIG_FACTORY);
    }

    /**
     * Construct a synchronous implementation of AWSKMS using the current builder configuration.
     *
     * @param params
     *        Current builder configuration represented as a parameter object.
     * @return Fully configured implementation of AWSKMS.
     */
    @Override
    protected AWSKMS build(AwsSyncClientParams params) {
        return new AWSKMSClient(params);
    }

}
