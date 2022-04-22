/*
 * Copyright (c) 2016-2019. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.http.apache.request.impl;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.ProxyAuthenticationMethod;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.handlers.HandlerContextKey;
import com.ibm.cloud.objectstorage.http.HttpMethodName;
import com.ibm.cloud.objectstorage.http.RepeatableInputStreamRequestEntity;
import com.ibm.cloud.objectstorage.http.apache.utils.ApacheUtils;
import com.ibm.cloud.objectstorage.http.request.HttpRequestFactory;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import com.ibm.cloud.objectstorage.util.FakeIOException;
import com.ibm.cloud.objectstorage.util.SdkHttpUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Responsible for creating Apache HttpClient 4 request objects.
 */
public class ApacheHttpRequestFactory implements HttpRequestFactory<HttpRequestBase> {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String QUERY_PARAM_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=" +
                                                           DEFAULT_ENCODING.toLowerCase();
    private static final List<String> ignoreHeaders = Arrays.asList(HttpHeaders.CONTENT_LENGTH, HttpHeaders.HOST);

    @Override
    public HttpRequestBase create(final Request<?> request,
                                  final HttpClientSettings settings) throws FakeIOException {

        String endpointUri = getUriEndpoint(request);
        String encodedParams = SdkHttpUtils.encodeParameters(request);
        final HttpRequestBase base;

        if (shouldMoveQueryParametersToBody(request, encodedParams)) {
            base = createPostParamsInBodyRequest(endpointUri, encodedParams);
            addHeadersToRequest(base, request);
            addContentTypeHeaderIfNeeded(base);
        } else {
            if (encodedParams != null) {
                endpointUri += "?" + encodedParams;
            }
            base = createStandardRequest(request, endpointUri);
            addHeadersToRequest(base, request);
        }

        addRequestConfig(base, request, settings);
        return base;
    }

    private String getUriEndpoint(Request<?> request) {
        URI endpoint = request.getEndpoint();

        // skipAppendUriPath is set for APIs making requests with presigned urls.
        // Otherwise a slash will be appended at the end and the request will fail
        if (request.getOriginalRequest().getRequestClientOptions().isSkipAppendUriPath()) {
            return endpoint.toString();
        }
        // HttpClient cannot handle url in pattern of "http://host//path", so we have to escape the double-slash
        // between endpoint and resource-path into "/%2F"
        return SdkHttpUtils.appendUri(endpoint.toString(), request.getResourcePath(), true);
    }

    private void addRequestConfig(final HttpRequestBase base,
                                  final Request<?> request,
                                  final HttpClientSettings settings) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig
                .custom()
                .setConnectionRequestTimeout(settings.getConnectionPoolRequestTimeout())
                .setConnectTimeout(settings.getConnectionTimeout())
                .setSocketTimeout(settings.getSocketTimeout())
                .setLocalAddress(settings.getLocalAddress());

        ApacheUtils.disableNormalizeUri(requestConfigBuilder);

        /*
         * Enable 100-continue support for PUT operations, since this is where we're potentially uploading large amounts of
         * data and want to find out as early as possible if an operation will fail. We don't want to do this for all
         * operations since it will cause extra latency in the network interaction.
         */
        if (HttpMethodName.PUT == request.getHttpMethod() && settings.isUseExpectContinue()) {
            requestConfigBuilder.setExpectContinueEnabled(true);
        }
        addProxyConfig(requestConfigBuilder, settings);
        base.setConfig(requestConfigBuilder.build());
    }

    private HttpRequestBase createStandardRequest(Request<?> request, String uri) throws FakeIOException {
        switch (request.getHttpMethod()) {
            case HEAD:
                return new HttpHead(uri);
            case GET:
                return wrapEntity(request, new HttpGetWithBody(uri));
            case DELETE:
                return new HttpDelete(uri);
            case OPTIONS:
                return new HttpOptions(uri);
            case PATCH:
                return wrapEntity(request, new HttpPatch(uri));
            case POST:
                return wrapEntity(request, new HttpPost(uri));
            case PUT:
                return wrapEntity(request, new HttpPut(uri));
            default:
                throw new SdkClientException("Unknown HTTP method name: " + request.getHttpMethod());
        }
    }

    /**
     * Otherwise use the {@link RepeatableInputStreamRequestEntity} and Apache http client will
     * set the proper header (Content-Length or Transfer-Encoding) based on whether it can find content length
     * from the input stream. This is fine as services accept both headers.
     */
    private HttpRequestBase wrapEntity(Request<?> request,
                                       HttpEntityEnclosingRequestBase entityEnclosingRequest) throws FakeIOException {

        if (HttpMethodName.POST == request.getHttpMethod()) {
            createHttpEntityForPostVerb(request, entityEnclosingRequest);
        } else {
            /*
             * We should never reuse the entity of the previous request, since reading from the buffered entity will bypass
             * reading from the original request content. And if the content contains InputStream wrappers that were added for
             * validation-purpose (e.g. Md5DigestCalculationInputStream), these wrappers would never be read and updated again
             * after AmazonHttpClient resets it in preparation for the retry. Eventually, these wrappers would
             * return incorrect validation result.
             */
            if (request.getContent() != null) {
                createHttpEntityForNonPostVerbs(request, entityEnclosingRequest);
            }
        }
        return entityEnclosingRequest;
    }

    /**
     * For POST APIs, only use buffered entity if requiresLength trait is present.
     *
     * The behavior difference for POST vs non-POST APIs is to ensure only minimal changes are made to header behavior
     * (after adding requiresLength trait) and reduce the impact radius.
     */
    private void createHttpEntityForPostVerb(Request<?> request,
                                             HttpEntityEnclosingRequestBase entityEnclosingRequest) throws FakeIOException {
        HttpEntity entity = new RepeatableInputStreamRequestEntity(request);

        if (request.getHeaders().get(HttpHeaders.CONTENT_LENGTH) == null && isRequiresLength(request)) {
            entity = ApacheUtils.newBufferedHttpEntity(entity);
        }

        entityEnclosingRequest.setEntity(entity);
    }

    /**
     * For non-POST APIs, use buffered entity if op is either
     * (a) No Streaming Input or
     * (b) hasStreamingInput and requiresLength header is present
     *
     * The behavior difference for POST vs non-POST APIs is to ensure only minimal changes are made to header behavior
     * (after adding requiresLength trait) and reduce the impact radius.
     */
    private void createHttpEntityForNonPostVerbs(Request<?> request,
                                                 HttpEntityEnclosingRequestBase entityEnclosingRequest) throws FakeIOException {
        HttpEntity entity = new RepeatableInputStreamRequestEntity(request);

        if (request.getHeaders().get(HttpHeaders.CONTENT_LENGTH) == null) {
            if (isRequiresLength(request) || !hasStreamingInput(request)) {
                entity = ApacheUtils.newBufferedHttpEntity(entity);
            }
        }
        entityEnclosingRequest.setEntity(entity);
    }

    private boolean isRequiresLength(Request<?> request) {
        return Boolean.TRUE.equals(request.getHandlerContext(HandlerContextKey.REQUIRES_LENGTH));
    }

    private boolean hasStreamingInput(Request<?> request) {
        return Boolean.TRUE.equals(request.getHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT));
    }

    /**
     * Configures the headers in the specified Apache HTTP request.
     */
    private void addHeadersToRequest(HttpRequestBase httpRequest, Request<?> request) {
        httpRequest.addHeader(HttpHeaders.HOST, getHostHeaderValue(request.getEndpoint()));
        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            // HttpClient4 fills in the Content-Length header and complains if it's already present, so we skip it here.
            // We also skip the Host header to avoid sending it twice, which will interfere with some signing schemes.
            if (!(ignoreHeaders.contains(entry.getKey()))) {
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * For all non-POST requests, and any POST requests that already have a payload,
     * we put the encoded params directly in the URI, otherwise, we'll put them in the POST request's payload.
     */
    private boolean shouldMoveQueryParametersToBody(Request<?> request, String encodedParams) {
        boolean requestIsPost = request.getHttpMethod() == HttpMethodName.POST;
        return requestIsPost && request.getContent() == null && encodedParams != null;
    }

    /**
     * When query parameters are sent in a POST body, use a StringEntity.
     */
    private HttpRequestBase createPostParamsInBodyRequest(String endpointUri, String encodedParams) {
        HttpEntityEnclosingRequestBase requestBase = new HttpPost(endpointUri);
        requestBase.setEntity(ApacheUtils.newStringEntity(encodedParams));
        return requestBase;
    }

    /**
     * When query parameters are sent in a POST body, there should be a content type.
     */
    private void addContentTypeHeaderIfNeeded(HttpRequestBase base) {
        if (base.getHeaders(HttpHeaders.CONTENT_TYPE) == null || base.getHeaders(HttpHeaders.CONTENT_TYPE).length == 0) {
            base.addHeader(HttpHeaders.CONTENT_TYPE, QUERY_PARAM_CONTENT_TYPE);
        }
    }

    private String getHostHeaderValue(final URI endpoint) {
        /*
         * Apache HttpClient omits the port number in the Host header (even if
         * we explicitly specify it) if it's the default port for the protocol
         * in use. To ensure that we use the same Host header in the request and
         * in the calculated string to sign (even if Apache HttpClient changed
         * and started honoring our explicit host with endpoint), we follow this
         * same behavior here and in the QueryString signer.
         */
        return SdkHttpUtils.isUsingNonDefaultPort(endpoint)
                ? endpoint.getHost() + ":" + endpoint.getPort()
                : endpoint.getHost();
    }

    /**
     * Update the provided request configuration builder to specify the proxy authentication schemes that should be used when
     * authenticating against the HTTP proxy.
     *
     * @see ClientConfiguration#setProxyAuthenticationMethods(List)
     */
    private void addProxyConfig(RequestConfig.Builder requestConfigBuilder, HttpClientSettings settings) {
        if (settings.isProxyEnabled() && settings.isAuthenticatedProxy() && settings.getProxyAuthenticationMethods() != null) {
            List<String> apacheAuthenticationSchemes = new ArrayList<String>();

            for (ProxyAuthenticationMethod authenticationMethod : settings.getProxyAuthenticationMethods()) {
                apacheAuthenticationSchemes.add(toApacheAuthenticationScheme(authenticationMethod));
            }
            requestConfigBuilder.setProxyPreferredAuthSchemes(apacheAuthenticationSchemes);
        }
    }

    /**
     * Convert the customer-facing authentication method into an apache-specific authentication method.
     */
    private String toApacheAuthenticationScheme(ProxyAuthenticationMethod authenticationMethod) {
        if (authenticationMethod == null) {
            throw new IllegalStateException("The configured proxy authentication methods must not be null.");
        }

        switch (authenticationMethod) {
            case NTLM: return AuthSchemes.NTLM;
            case BASIC: return AuthSchemes.BASIC;
            case DIGEST: return AuthSchemes.DIGEST;
            case SPNEGO: return AuthSchemes.SPNEGO;
            case KERBEROS: return AuthSchemes.KERBEROS;
            default: throw new IllegalStateException("Unknown authentication scheme: " + authenticationMethod);
        }
    }
}