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
package com.ibm.cloud.objectstorage.util;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.Protocol;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;

import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.retry.RetryMode;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RuntimeHttpUtils {
    private static final String COMMA = ", ";
    private static final String SPACE = " ";

    private static final String AWS_EXECUTION_ENV_PREFIX = "exec-env/";
    private static final String AWS_EXECUTION_ENV_NAME = "AWS_EXECUTION_ENV";

    private static final String RETRY_MODE_PREFIX = "cfg/retry-mode/";
    private static final String PROVIDER_NAME_PREFIX = "cfg/auth-source#";

    private static final String TRACE_ID_ENVIRONMENT_VARIABLE = "_X_AMZN_TRACE_ID";
    private static final String TRACE_ID_SYSTEM_PROPERTY = "com.ibm.cloud.objectstorage.xray.traceHeader";
    private static final String LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE = "AWS_LAMBDA_FUNCTION_NAME";


    /**
     * Fetches a file from the URI given and returns an input stream to it.
     *
     * @param uri the uri of the file to fetch
     * @param config optional configuration overrides
     * @return an InputStream containing the retrieved data
     * @throws IOException on error
     */
    @SuppressWarnings("deprecation")
    public static InputStream fetchFile(
            final URI uri,
            final ClientConfiguration config) throws IOException {

        HttpParams httpClientParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent(
                httpClientParams, getUserAgent(config, null));

        HttpConnectionParams.setConnectionTimeout(
                httpClientParams, getConnectionTimeout(config));
        HttpConnectionParams.setSoTimeout(
                httpClientParams, getSocketTimeout(config));

        DefaultHttpClient httpclient = new DefaultHttpClient(httpClientParams);

        if (config != null) {
            String proxyHost = config.getProxyHost();
            int proxyPort = config.getProxyPort();

            if (proxyHost != null && proxyPort > 0) {

                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                httpclient.getParams().setParameter(
                        ConnRoutePNames.DEFAULT_PROXY, proxy);

                if (config.getProxyUsername() != null
                    && config.getProxyPassword() != null) {

                    httpclient.getCredentialsProvider().setCredentials(
                            new AuthScope(proxyHost, proxyPort),
                            new NTCredentials(config.getProxyUsername(),
                                              config.getProxyPassword(),
                                              config.getProxyWorkstation(),
                                              config.getProxyDomain()));
                }
            }
        }

        HttpResponse response = httpclient.execute(new HttpGet(uri));

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Error fetching file from " + uri + ": "
                                  + response);
        }

        return new HttpClientWrappingInputStream(
                httpclient,
                response.getEntity().getContent());
    }

    public static String getUserAgent(final ClientConfiguration config, final String userAgentMarker) {
        return getUserAgent(config, userAgentMarker, null);
    }

    public static String getUserAgent(final ClientConfiguration config, final String userAgentMarker,
                                      AWSCredentials credentials) {

        String userDefinedPrefix = "";
        String userDefinedSuffix = "";
        String retryModeName = "";
        String awsExecutionEnvironment = getEnvironmentVariable(AWS_EXECUTION_ENV_NAME);

        if (config != null) {
            userDefinedPrefix = config.getUserAgentPrefix();
            userDefinedSuffix = config.getUserAgentSuffix();
            RetryMode retryMode = config.getRetryMode() == null ? config.getRetryPolicy().getRetryMode() : config.getRetryMode();
            retryModeName = retryMode != null ? retryMode.getName() : "";
        }

        StringBuilder userAgent = new StringBuilder(userDefinedPrefix.trim());

        if(!ClientConfiguration.DEFAULT_USER_AGENT.equals(userDefinedPrefix)) {
            userAgent.append(COMMA).append(ClientConfiguration.DEFAULT_USER_AGENT);
        }

        if(StringUtils.hasValue(retryModeName)) {
            userAgent.append(SPACE).append(RETRY_MODE_PREFIX).append(retryModeName.trim());
        }

        if(StringUtils.hasValue(userDefinedSuffix)) {
            userAgent.append(COMMA).append(userDefinedSuffix.trim());
        }

        if(StringUtils.hasValue(awsExecutionEnvironment)) {
            userAgent.append(SPACE).append(AWS_EXECUTION_ENV_PREFIX).append(awsExecutionEnvironment.trim());
        }

        if(StringUtils.hasValue(userAgentMarker)) {
            userAgent.append(SPACE).append(userAgentMarker.trim());
        }

        return userAgent.toString();
    }

    private static String getEnvironmentVariable(String environmentVariableName) {
        try {
            return System.getenv(environmentVariableName);
        } catch (Exception e) {
            // Return an empty string if unable to get environment variable
            return "";
        }
    }

    private static String getSystemProperty(String systemProperty) {
        try {
            return System.getProperty(systemProperty);
        } catch (Exception e) {
            return null;
        }
    }

    private static int getConnectionTimeout(final ClientConfiguration config) {
        if (config != null) {
            return config.getConnectionTimeout();
        }
        return ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT;
    }

    private static int getSocketTimeout(final ClientConfiguration config) {
        if (config != null) {
            return config.getSocketTimeout();
        }
        return ClientConfiguration.DEFAULT_SOCKET_TIMEOUT;
    }

    /**
     * Returns an URI for the given endpoint.
     * Prefixes the protocol if the endpoint given does not have it.
     *
     * @throws IllegalArgumentException if the inputs are null.
     */
    public static URI toUri(String endpoint, ClientConfiguration config) {

        if (config == null) {
            throw new IllegalArgumentException("ClientConfiguration cannot be null");
        }
        return toUri(endpoint, config.getProtocol());
    }

    /**
     * Returns an URI for the given endpoint.
     * Prefixes the protocol if the endpoint given does not have it.
     *
     * @throws IllegalArgumentException if the inputs are null.
     */
    public static URI toUri(String endpoint, Protocol protocol) {
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint cannot be null");
        }

        /*
         * If the endpoint doesn't explicitly specify a protocol to use, then
         * we'll defer to the default protocol specified in the client
         * configuration.
         */
        if (!endpoint.contains("://")) {
            endpoint = protocol.toString() + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Converts the specified request object into a URL, containing all the specified parameters, the specified request endpoint,
     * etc.
     *
     * @param request                          The request to convert into a URL.
     * @param removeLeadingSlashInResourcePath Whether the leading slash in resource-path should be removed before appending to
     *                                         the endpoint.
     * @param urlEncode                        True if request resource path should be URL encoded
     * @return A new URL representing the specified request.
     * @throws SdkClientException If the request cannot be converted to a well formed URL.
     */
    @SdkProtectedApi
    public static URL convertRequestToUrl(Request<?> request,
                                          boolean removeLeadingSlashInResourcePath,
                                          boolean urlEncode) {
        String resourcePath = urlEncode ?
                SdkHttpUtils.urlEncode(request.getResourcePath(), true)
                : request.getResourcePath();

        // Removed the padding "/" that was already added into the request's resource path.
        if (removeLeadingSlashInResourcePath
            && resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        // Some http client libraries (e.g. Apache HttpClient) cannot handle
        // consecutive "/"s between URL authority and path components.
        // So we escape "////..." into "/%2F%2F%2F...", in the same way as how
        // we treat consecutive "/"s in AmazonS3Client#presignRequest(...)

        String urlPath = "/" + resourcePath;
        urlPath = urlPath.replaceAll("(?<=/)/", "%2F");
        StringBuilder url = new StringBuilder(request.getEndpoint().toString());
        url.append(urlPath);

        StringBuilder queryParams = new StringBuilder();
        Map<String, List<String>> requestParams = request.getParameters();
        for (Map.Entry<String, List<String>> entry : requestParams.entrySet()) {
            for (String value : entry.getValue()) {
                queryParams = queryParams.length() > 0 ? queryParams
                        .append("&") : queryParams.append("?");
                queryParams.append(SdkHttpUtils.urlEncode(entry.getKey(), false))
                        .append("=")
                        .append(SdkHttpUtils.urlEncode(value, false));
            }
        }
        url.append(queryParams.toString());

        try {
            return new URL(url.toString());
        } catch (MalformedURLException e) {
            throw new SdkClientException(
                    "Unable to convert request to well formed URL: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the value of the trace id environment variable, if it exists and if there is also
     * a lambda function name environment variable, indicating that the code executes within a lambda
     */
    public static String getLambdaEnvironmentTraceId() {
        String lambdafunctionName = getEnvironmentVariable(LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE);
        String traceId = traceId();
        if (!StringUtils.isNullOrEmpty(lambdafunctionName) && !StringUtils.isNullOrEmpty(traceId)) {
            return traceId;
        };
        return null;
    }

    static String traceId() {
        String traceId = getSystemProperty(TRACE_ID_SYSTEM_PROPERTY);
        if (traceId == null) {
            traceId = getEnvironmentVariable(TRACE_ID_ENVIRONMENT_VARIABLE);
        }
        return traceId;
    }
}
