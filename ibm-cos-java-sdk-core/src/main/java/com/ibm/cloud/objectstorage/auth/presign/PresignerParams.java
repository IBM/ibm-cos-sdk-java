/*
 * Copyright 2011-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.auth.presign;

import com.ibm.cloud.objectstorage.annotation.Immutable;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.SdkClock;
import com.ibm.cloud.objectstorage.internal.auth.SignerProvider;

import java.net.URI;

/**
 * Params object passed to the presigner extension class for a service client and also for {@link PresignerFacade}.
 */
@Immutable
@SdkProtectedApi
public class PresignerParams {

    private final URI endpoint;
    private final AWSCredentialsProvider credentialsProvider;
    private final SignerProvider signerProvider;
    private final SdkClock clock;

    public PresignerParams(URI endpoint,
                           AWSCredentialsProvider credentialsProvider,
                           SignerProvider signerProvider,
                           SdkClock clock) {
        this.endpoint = endpoint;
        this.credentialsProvider = credentialsProvider;
        this.signerProvider = signerProvider;
        this.clock = clock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public URI endpoint() {
        return endpoint;
    }

    public AWSCredentialsProvider credentialsProvider() {
        return credentialsProvider;
    }

    public SignerProvider signerProvider() {
        return signerProvider;
    }

    /**
     * @return Clock providing the current time in milliseconds.
     */
    public SdkClock clock() {
        return this.clock;
    }

    public static class Builder {

        private URI endpoint;
        private AWSCredentialsProvider credentialsProvider;
        private SignerProvider signerProvider;
        private SdkClock clock;

        private Builder() {
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentialsProvider(AWSCredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public Builder signerProvider(SignerProvider signerProvider) {
            this.signerProvider = signerProvider;
            return this;
        }

        @SdkTestInternalApi
        public Builder clock(SdkClock clock) {
            this.clock = clock;
            return this;
        }

        public PresignerParams build() {
            return new PresignerParams(endpoint, credentialsProvider, signerProvider, resolveClock());
        }

        private SdkClock resolveClock() {
            return clock == null ? SdkClock.STANDARD : clock;
        }
    }
}
