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
package com.ibm.cloud.objectstorage.services.s3;

import com.ibm.cloud.objectstorage.ClientConfigurationFactory;
import com.ibm.cloud.objectstorage.annotation.NotThreadSafe;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.client.AwsSyncClientParams;
import com.ibm.cloud.objectstorage.internal.SdkFunction;
import com.ibm.cloud.objectstorage.regions.AwsRegionProvider;

/**
 * Fluent builder for AmazonS3. Capable of building synchronous and asynchronous clients. Use of the
 * builder is preferred over using constructors of the client class.
 **/
@NotThreadSafe
public final class AmazonS3ClientBuilder extends AmazonS3Builder<AmazonS3ClientBuilder, AmazonS3> {

    private AmazonS3ClientBuilder() {
        super();
    }

    @SdkTestInternalApi
    AmazonS3ClientBuilder(SdkFunction<AmazonS3ClientParamsWrapper, AmazonS3> clientFactory,
                          ClientConfigurationFactory clientConfigFactory,
                          AwsRegionProvider regionProvider) {
        super(clientFactory, clientConfigFactory, regionProvider);
    }

    /**
     * @return Create new instance of builder with all defaults set.
     */
    public static AmazonS3ClientBuilder standard() {
        return new AmazonS3ClientBuilder().withCredentials(new S3CredentialsProviderChain());
    }

    /**
     * @return Default client using the {@link com.ibm.cloud.objectstorage.regions.DefaultAwsRegionProviderChain}
     * chain.
     */
    public static AmazonS3 defaultClient() {
        return standard().build();
    }

    /**
     * Construct a synchronous implementation of AmazonS3 using the current builder configuration.
     *
     * @return Fully configured implementation of AmazonS3.
     */
    @Override
    protected AmazonS3 build(AwsSyncClientParams clientParams) {
        return clientFactory.apply(new AmazonS3ClientParamsWrapper(clientParams, resolveS3ClientOptions()));
    }
}
