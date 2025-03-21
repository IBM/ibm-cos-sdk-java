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
package com.ibm.cloud.objectstorage.auth.presign;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.annotation.Immutable;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.Presigner;
import com.ibm.cloud.objectstorage.internal.auth.SignerProvider;
import com.ibm.cloud.objectstorage.internal.auth.SignerProviderContext;
import com.ibm.cloud.objectstorage.util.CredentialUtils;
import com.ibm.cloud.objectstorage.util.RuntimeHttpUtils;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Really thin facade over {@link Presigner} to deal with some common concerns like credential resolution, adding custom headers
 * and query params to be included in signing, and conversion to a usable URL.
 */
@Immutable
@SdkProtectedApi
public final class PresignerFacade {

    private final AWSCredentialsProvider credentialsProvider;
    private final SignerProvider signerProvider;

    public PresignerFacade(PresignerParams presignerParams) {
        this.credentialsProvider = presignerParams.credentialsProvider();
        this.signerProvider = presignerParams.signerProvider();
    }

    public URL presign(Request<?> request, Date expirationDate) {
        final Presigner presigner = (Presigner) signerProvider.getSigner(SignerProviderContext.builder()
                                                                                 .withIsRedirect(false)
                                                                                 .withRequest(request)
                                                                                 .withUri(request.getEndpoint())
                                                                                 .build());
        if (request.getOriginalRequest() != null) {
            addCustomQueryParams(request);
            addCustomHeaders(request);
        }
        final AWSCredentialsProvider credentialsProvider = resolveCredentials(request);
        presigner.presignRequest(request, credentialsProvider.getCredentials(), expirationDate);
        return RuntimeHttpUtils.convertRequestToUrl(request, true, false);
    }

    private void addCustomQueryParams(Request<?> request) {
        final Map<String, List<String>> queryParameters = request.getOriginalRequest().getCustomQueryParameters();
        if (queryParameters == null || queryParameters.isEmpty()) {
            return;
        }
        for (Map.Entry<String, List<String>> param : queryParameters.entrySet()) {
            request.addParameters(param.getKey(), param.getValue());
        }
    }

    private void addCustomHeaders(Request<?> request) {
        final Map<String, String> headers = request.getOriginalRequest().getCustomRequestHeaders();
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
    }

    private AWSCredentialsProvider resolveCredentials(Request<?> request) {
        return CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), this.credentialsProvider);
    }

    /**
     * Empty request object useful for setting request level credentials without having the user facing presiging request extend
     * from {@link AmazonWebServiceRequest}.
     */
    public static class PresigningRequest extends AmazonWebServiceRequest {

        public PresigningRequest withRequestCredentialsProvider(AWSCredentialsProvider credentialsProvider) {
            setRequestCredentialsProvider(credentialsProvider);
            return this;
        }
    }
}
