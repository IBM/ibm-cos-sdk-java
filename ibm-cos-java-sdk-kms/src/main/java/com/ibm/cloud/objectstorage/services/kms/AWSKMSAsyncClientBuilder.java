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
import com.ibm.cloud.objectstorage.client.builder.AwsAsyncClientBuilder;
import com.ibm.cloud.objectstorage.client.AwsAsyncClientParams;

/**
 * Fluent builder for {@link com.ibm.cloud.objectstorage.services.kms.AWSKMSAsync}. Use of the builder is preferred over using
 * constructors of the client class.
 **/
@NotThreadSafe
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public final class AWSKMSAsyncClientBuilder extends AwsAsyncClientBuilder<AWSKMSAsyncClientBuilder, AWSKMSAsync> {

    private static final ClientConfigurationFactory CLIENT_CONFIG_FACTORY = new ClientConfigurationFactory();;

    /**
     * @return Create new instance of builder with all defaults set.
     */
    public static AWSKMSAsyncClientBuilder standard() {
        return new AWSKMSAsyncClientBuilder();
    }

    /**
     * @return Default async client using the {@link com.ibm.cloud.objectstorage.auth.DefaultAWSCredentialsProviderChain} and
     *         {@link com.ibm.cloud.objectstorage.regions.DefaultAwsRegionProviderChain} chain
     */
    public static AWSKMSAsync defaultClient() {
        return standard().build();
    }

    private AWSKMSAsyncClientBuilder() {
        super(CLIENT_CONFIG_FACTORY);
    }

    /**
     * Construct an asynchronous implementation of AWSKMSAsync using the current builder configuration.
     *
     * @param params
     *        Current builder configuration represented as a parameter object.
     * @return Fully configured implementation of AWSKMSAsync.
     */
    @Override
    protected AWSKMSAsync build(AwsAsyncClientParams params) {
        return new AWSKMSAsyncClient(params);
    }

}
