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
package com.ibm.cloud.objectstorage.http;

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.PROFILING_SYSTEM_PROPERTY;
import static com.ibm.cloud.objectstorage.event.SDKProgressPublisher.publishProgress;
import static com.ibm.cloud.objectstorage.event.SDKProgressPublisher.publishRequestContentLength;
import static com.ibm.cloud.objectstorage.event.SDKProgressPublisher.publishResponseContentLength;
import static com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field.HttpClientPoolAvailableCount;
import static com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field.HttpClientPoolLeasedCount;
import static com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field.HttpClientPoolPendingCount;
import static com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field.ThrottledRetryCount;
import static com.ibm.cloud.objectstorage.util.AwsClientSideMonitoringMetrics.MaxRetriesExceeded;
import static com.ibm.cloud.objectstorage.util.IOUtils.closeQuietly;

import com.ibm.cloud.objectstorage.AbortedException;
import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.Protocol;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.RequestClientOptions;
import com.ibm.cloud.objectstorage.RequestClientOptions.Marker;
import com.ibm.cloud.objectstorage.RequestConfig;
import com.ibm.cloud.objectstorage.ResetException;
import com.ibm.cloud.objectstorage.Response;
import com.ibm.cloud.objectstorage.ResponseMetadata;
import com.ibm.cloud.objectstorage.SDKGlobalTime;
import com.ibm.cloud.objectstorage.SdkBaseException;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.annotation.ThreadSafe;
import com.ibm.cloud.objectstorage.auth.AWS4Signer;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.CanHandleNullCredentials;
import com.ibm.cloud.objectstorage.auth.Signer;
import com.ibm.cloud.objectstorage.event.ProgressEventType;
import com.ibm.cloud.objectstorage.event.ProgressInputStream;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.handlers.CredentialsRequestHandler;
import com.ibm.cloud.objectstorage.handlers.HandlerAfterAttemptContext;
import com.ibm.cloud.objectstorage.handlers.HandlerBeforeAttemptContext;
import com.ibm.cloud.objectstorage.handlers.HandlerContextKey;
import com.ibm.cloud.objectstorage.handlers.RequestHandler2;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ApacheHttpClientFactory;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.ibm.cloud.objectstorage.http.apache.request.impl.ApacheHttpRequestFactory;
import com.ibm.cloud.objectstorage.http.apache.utils.ApacheUtils;
import com.ibm.cloud.objectstorage.http.client.HttpClientFactory;
import com.ibm.cloud.objectstorage.http.exception.HttpRequestTimeoutException;
import com.ibm.cloud.objectstorage.http.request.HttpRequestFactory;
import com.ibm.cloud.objectstorage.http.response.AwsResponseHandlerAdapter;
import com.ibm.cloud.objectstorage.http.settings.HttpClientSettings;
import com.ibm.cloud.objectstorage.http.timers.client.ClientExecutionAbortTrackerTask;
import com.ibm.cloud.objectstorage.http.timers.client.ClientExecutionTimeoutException;
import com.ibm.cloud.objectstorage.http.timers.client.ClientExecutionTimer;
import com.ibm.cloud.objectstorage.http.timers.client.SdkInterruptedException;
import com.ibm.cloud.objectstorage.http.timers.request.HttpRequestAbortTaskTracker;
import com.ibm.cloud.objectstorage.http.timers.request.HttpRequestTimer;
import com.ibm.cloud.objectstorage.internal.AmazonWebServiceRequestAdapter;
import com.ibm.cloud.objectstorage.internal.CRC32MismatchException;
import com.ibm.cloud.objectstorage.internal.ReleasableInputStream;
import com.ibm.cloud.objectstorage.internal.ResettableInputStream;
import com.ibm.cloud.objectstorage.internal.SdkBufferedInputStream;
import com.ibm.cloud.objectstorage.internal.TokenBucket;
import com.ibm.cloud.objectstorage.internal.auth.SignerProviderContext;
import com.ibm.cloud.objectstorage.metrics.AwsSdkMetrics;
import com.ibm.cloud.objectstorage.metrics.RequestMetricCollector;
import com.ibm.cloud.objectstorage.monitoring.internal.ClientSideMonitoringRequestHandler;
import com.ibm.cloud.objectstorage.retry.ClockSkewAdjuster;
import com.ibm.cloud.objectstorage.retry.ClockSkewAdjuster.AdjustmentRequest;
import com.ibm.cloud.objectstorage.retry.ClockSkewAdjuster.ClockSkewAdjustment;
import com.ibm.cloud.objectstorage.retry.RetryMode;
import com.ibm.cloud.objectstorage.retry.RetryPolicyAdapter;
import com.ibm.cloud.objectstorage.retry.RetryUtils;
import com.ibm.cloud.objectstorage.internal.SdkRequestRetryHeaderProvider;
import com.ibm.cloud.objectstorage.retry.internal.AuthErrorRetryStrategy;
import com.ibm.cloud.objectstorage.retry.internal.AuthRetryParameters;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicy;
import com.ibm.cloud.objectstorage.retry.v2.RetryPolicyContext;
import com.ibm.cloud.objectstorage.util.AWSRequestMetrics;
import com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field;
import com.ibm.cloud.objectstorage.util.AwsClientSideMonitoringMetrics;
import com.ibm.cloud.objectstorage.util.CapacityManager;
import com.ibm.cloud.objectstorage.util.CollectionUtils;
import com.ibm.cloud.objectstorage.util.CountingInputStream;
import com.ibm.cloud.objectstorage.util.FakeIOException;
import com.ibm.cloud.objectstorage.util.ImmutableMapParameter;
import com.ibm.cloud.objectstorage.util.MetadataCache;
import com.ibm.cloud.objectstorage.util.NullResponseMetadataCache;
import com.ibm.cloud.objectstorage.util.ResponseMetadataCache;
import com.ibm.cloud.objectstorage.util.RuntimeHttpUtils;
import com.ibm.cloud.objectstorage.util.SdkHttpUtils;
import com.ibm.cloud.objectstorage.util.StringUtils;
import com.ibm.cloud.objectstorage.util.UnreliableFilterInputStream;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;

@ThreadSafe
public class AmazonHttpClient {

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_SDK_TRANSACTION_ID = "amz-sdk-invocation-id";
    public static final String HEADER_SDK_RETRY_INFO = "amz-sdk-retry";
    private static final String TRACE_ID_HEADER = "X-Amzn-Trace-Id";

    /**
     * Logger for more detailed debugging information, that might not be as useful for end users
     * (ex: HTTP client configuration, etc).
     */
    static final Log log = LogFactory.getLog(AmazonHttpClient.class);

    /**
     * Logger providing detailed information on requests/responses. Users can enable this logger to
     * get access to AWS request IDs for responses, individual requests and parameters sent to AWS,
     * etc.
     */
    @SdkInternalApi
    public static final Log requestLog = LogFactory.getLog("com.ibm.cloud.objectstorage.request");

    private static final HttpClientFactory<ConnectionManagerAwareHttpClient> httpClientFactory = new
            ApacheHttpClientFactory();
    /**
     * Used for testing via failure injection.
     */
    private static UnreliableTestConfig unreliableTestConfig;

    /**
     * When throttled retries are enabled, each retry attempt will consume this much capacity.
     * Successful retry attempts will release this capacity back to the pool while failed retries
     * will not.  Successful initial (non-retry) requests will always release 1 capacity unit to the
     * pool.
     */
    private static final int THROTTLED_RETRY_COST = 5;

    /**
     * The capacity to acquire for a connection timeout or socket timeout error.
     */
    private static final int TIMEOUT_RETRY_COST = 10;

    static {
        // Customers have reported XML parsing issues with the following
        // JVM versions, which don't occur with more recent versions, so
        // if we detect any of these, give customers a heads up.
        // https://bugs.openjdk.java.net/browse/JDK-8028111
        List<String> problematicJvmVersions = Arrays
                .asList("1.6.0_06", "1.6.0_13", "1.6.0_17", "1.6.0_65", "1.7.0_45");
        String jvmVersion = System.getProperty("java.version");
        if (problematicJvmVersions.contains(jvmVersion)) {
            log.warn("Detected a possible problem with the current JVM version (" + jvmVersion +
                     ").  " +
                     "If you experience XML parsing problems using the SDK, try upgrading to a more recent JVM update.");
        }
    }

    private final ClockSkewAdjuster clockSkewAdjuster = new ClockSkewAdjuster();

    private final HttpRequestFactory<HttpRequestBase> httpRequestFactory =
            new ApacheHttpRequestFactory();

    /**
     * Internal client for sending HTTP requests
     */
    private ConnectionManagerAwareHttpClient httpClient;
    /**
     * Client configuration options, such as proxy httpClientSettings, max retries, etc.
     */
    private final ClientConfiguration config;

    private final RetryPolicy retryPolicy;

    /**
     * Client configuration options, such as proxy httpClientSettings, max retries, etc.
     */
    private final HttpClientSettings httpClientSettings;
    /**
     * Cache of metadata for recently executed requests for diagnostic purposes
     */
    private final MetadataCache responseMetadataCache;
    /**
     * Timer to enforce HTTP request timeouts.
     */
    private final HttpRequestTimer httpRequestTimer;

    /**
     * Retry capacity manager, used to manage throttled retry resource
     */
    private final CapacityManager retryCapacity;

    /**
     * Token bucket used for rate limiting when {@link RetryMode#ADAPTIVE} retry mode is enabled.
     */
    private TokenBucket tokenBucket;

    /**
     * Timer to enforce timeouts on the whole execution of the request (request handlers, retries,
     * backoff strategy, unmarshalling, etc)
     */
    private final ClientExecutionTimer clientExecutionTimer;
    /**
     * A request metric collector used specifically for this httpClientSettings client; or null if
     * there is none. This collector, if specified, always takes precedence over the one specified
     * at the AWS SDK level.
     *
     * @see AwsSdkMetrics
     */
    private final RequestMetricCollector requestMetricCollector;

    /**
     * Used to generate UUID's for client transaction id. This gives a higher probability of id
     * clashes but is more performant then using {@link UUID#randomUUID()} which uses SecureRandom
     * internally.
     **/
    private final Random random = new Random();

    /**
     * The time difference in seconds between this client and AWS.
     */
    private volatile int timeOffset = SDKGlobalTime.getGlobalTimeOffset();

    private final RetryMode retryMode;

    private final SdkRequestRetryHeaderProvider sdkRequestHeaderProvider;

    /**
     * Constructs a new AWS client using the specified client configuration options (ex: max retry
     * attempts, proxy httpClientSettings, etc).
     *
     * @param config Configuration options specifying how this client will communicate with AWS (ex:
     *               proxy httpClientSettings, retry count, etc.).
     */
    public AmazonHttpClient(ClientConfiguration config) {
        this(config, null);
    }

    /**
     * Constructs a new AWS client using the specified client configuration options (ex: max retry
     * attempts, proxy httpClientSettings, etc), and request metric collector.
     *
     * @param config                 Configuration options specifying how this client will
     *                               communicate with AWS (ex: proxy httpClientSettings, retry
     *                               count, etc.).
     * @param requestMetricCollector client specific request metric collector, which takes
     *                               precedence over the one at the AWS SDK level; or null if there
     *                               is none.
     */
    public AmazonHttpClient(ClientConfiguration config,
                            RequestMetricCollector requestMetricCollector) {
        this(config, requestMetricCollector, false);
    }

    /**
     * Constructs a new AWS client using the specified client configuration options (ex: max retry
     * attempts, proxy httpClientSettings, etc), and request metric collector.
     *
     * @param config                 Configuration options specifying how this client will
     *                               communicate with AWS (ex: proxy httpClientSettings, retry
     *                               count, etc.).
     * @param requestMetricCollector client specific request metric collector, which takes
     *                               precedence over the one at the AWS SDK level; or null if there
     *                               is none.
     */
    public AmazonHttpClient(ClientConfiguration config,
                            RequestMetricCollector requestMetricCollector,
                            boolean useBrowserCompatibleHostNameVerifier) {
        this(config, requestMetricCollector, useBrowserCompatibleHostNameVerifier, false);
    }

    /**
     * Constructs a new AWS client using the specified client configuration options (ex: max retry
     * attempts, proxy httpClientSettings, etc), and request metric collector.
     *
     * @param config                           Configuration options specifying how this client will
     *                                         communicate with AWS (ex: proxy httpClientSettings,
     *                                         retry count, etc.).
     * @param requestMetricCollector           client specific request metric collector, which takes
     *                                         precedence over the one at the AWS SDK level; or null
     *                                         if there is none.
     * @param calculateCRC32FromCompressedData The flag indicating whether the CRC32 checksum is
     *                                         calculated from compressed data or not. It is only
     *                                         applicable when the header "x-amz-crc32" is set in
     *                                         the response.
     */
    public AmazonHttpClient(ClientConfiguration config,
                            RequestMetricCollector requestMetricCollector,
                            boolean useBrowserCompatibleHostNameVerifier,
                            boolean calculateCRC32FromCompressedData) {
        this(config,
             null,
             requestMetricCollector,
             useBrowserCompatibleHostNameVerifier,
             calculateCRC32FromCompressedData);
    }

    private AmazonHttpClient(ClientConfiguration config,
                             RetryPolicy retryPolicy,
                             RequestMetricCollector requestMetricCollector,
                             boolean useBrowserCompatibleHostNameVerifier,
                             boolean calculateCRC32FromCompressedData) {
        this(config,
             retryPolicy,
             requestMetricCollector,
             HttpClientSettings.adapt(config, useBrowserCompatibleHostNameVerifier, calculateCRC32FromCompressedData));
        this.httpClient = httpClientFactory.create(this.httpClientSettings);
    }

    /**
     * Package-protected constructor for unit test purposes.
     */
    @SdkTestInternalApi
    public AmazonHttpClient(ClientConfiguration clientConfig,
                            ConnectionManagerAwareHttpClient httpClient,
                            RequestMetricCollector requestMetricCollector,
                            TokenBucket tokenBucket) {
        this(clientConfig,
             null,
             requestMetricCollector,
             HttpClientSettings.adapt(clientConfig, false));
        this.httpClient = httpClient;
        this.tokenBucket = tokenBucket;
    }

    private AmazonHttpClient(ClientConfiguration clientConfig,
                             RetryPolicy retryPolicy,
                             RequestMetricCollector requestMetricCollector,
                             HttpClientSettings httpClientSettings) {
        this.config = clientConfig;
        this.retryPolicy =
                retryPolicy == null ? new RetryPolicyAdapter(clientConfig.getRetryPolicy(), clientConfig) : retryPolicy;
        this.retryMode =
            clientConfig.getRetryMode() == null ? clientConfig.getRetryPolicy().getRetryMode() : clientConfig.getRetryMode();
        this.httpClientSettings = httpClientSettings;
        this.requestMetricCollector = requestMetricCollector;
        this.responseMetadataCache =
                clientConfig.getCacheResponseMetadata() ?
                        new ResponseMetadataCache(clientConfig.getResponseMetadataCacheSize()) :
                        new NullResponseMetadataCache();
        this.httpRequestTimer = new HttpRequestTimer();
        this.clientExecutionTimer = new ClientExecutionTimer();

        // When enabled, total retry capacity is computed based on retry cost
        // and desired number of retries.
        int throttledRetryMaxCapacity = clientConfig.useThrottledRetries()
                ? THROTTLED_RETRY_COST * config.getMaxConsecutiveRetriesBeforeThrottling() : -1;
        this.retryCapacity = new CapacityManager(throttledRetryMaxCapacity);
        this.tokenBucket = new TokenBucket();
        this.sdkRequestHeaderProvider = new SdkRequestRetryHeaderProvider(config, this.retryPolicy, clockSkewAdjuster);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ClientConfiguration clientConfig;
        private RetryPolicy retryPolicy;
        private RequestMetricCollector requestMetricCollector;
        private boolean useBrowserCompatibleHostNameVerifier;
        private boolean calculateCRC32FromCompressedData;

        private Builder() {
        }

        public Builder clientConfiguration(ClientConfiguration clientConfig) {
            this.clientConfig = clientConfig;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder requestMetricCollector(RequestMetricCollector requestMetricCollector) {
            this.requestMetricCollector = requestMetricCollector;
            return this;
        }

        public Builder useBrowserCompatibleHostNameVerifier(boolean useBrowserCompatibleHostNameVerifier) {
            this.useBrowserCompatibleHostNameVerifier = useBrowserCompatibleHostNameVerifier;
            return this;
        }

        public Builder calculateCRC32FromCompressedData(boolean calculateCRC32FromCompressedData) {
            this.calculateCRC32FromCompressedData = calculateCRC32FromCompressedData;
            return this;
        }

        public AmazonHttpClient build() {
            return new AmazonHttpClient(clientConfig,
                                        retryPolicy,
                                        requestMetricCollector,
                                        useBrowserCompatibleHostNameVerifier,
                                        calculateCRC32FromCompressedData);
        }
    }

    private static boolean isTemporaryRedirect(org.apache.http.HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status == HttpStatus.SC_TEMPORARY_REDIRECT && response.getHeaders("Location") != null
               && response.getHeaders("Location").length > 0;
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown();
        super.finalize();
    }

    /**
     * Shuts down this HTTP client object, releasing any resources that might be held open. This is
     * an optional method, and callers are not expected to call it, but can if they want to
     * explicitly release any open resources. Once a client has been shutdown, it cannot be used to
     * make more requests.
     */
    public void shutdown() {
        clientExecutionTimer.shutdown();
        httpRequestTimer.shutdown();
        IdleConnectionReaper.removeConnectionManager(httpClient.getHttpClientConnectionManager());
        httpClient.getHttpClientConnectionManager().shutdown();
    }

    /**
     * Used to configure the test conditions for injecting intermittent failures to the content
     * input stream.
     *
     * @param config unreliable test configuration for failure injection; or null to disable such
     *               test.
     */
    static void configUnreliableTestConditions(UnreliableTestConfig config) {
        unreliableTestConfig = config;
    }

    /**
     * Package protected for unit-testing
     */
    @SdkTestInternalApi
    public HttpRequestTimer getHttpRequestTimer() {
        return this.httpRequestTimer;
    }

    /**
     * Package protected for unit-testing
     */
    @SdkTestInternalApi
    public ClientExecutionTimer getClientExecutionTimer() {
        return this.clientExecutionTimer;
    }

    /**
     * Returns additional response metadata for an executed request. Response metadata isn't
     * considered part of the standard results returned by an operation, so it's accessed instead
     * through this diagnostic interface. Response metadata is typically used for troubleshooting
     * issues with AWS support staff when services aren't acting as expected.
     *
     * @param request A previously executed AmazonWebServiceRequest object, whose response metadata
     *                is desired.
     * @return The response metadata for the specified request, otherwise null if there is no
     * response metadata available for the request.
     */
    public ResponseMetadata getResponseMetadataForRequest(AmazonWebServiceRequest request) {
        return responseMetadataCache.get(request);
    }

    /**
     * Returns the httpClientSettings client specific request metric collector; or null if there is
     * none.
     */
    public RequestMetricCollector getRequestMetricCollector() {
        return requestMetricCollector;
    }

    /**
     * Returns the time difference in seconds between this client and AWS.
     */
    public int getTimeOffset() {
        return timeOffset;
    }

    /**
     * Executes the request and returns the result.
     *
     * @param request              The AmazonWebServices request to send to the remote server
     * @param responseHandler      A response handler to accept a successful response from the
     *                             remote server
     * @param errorResponseHandler A response handler to accept an unsuccessful response from the
     *                             remote server
     * @param executionContext     Additional information about the context of this web service
     *                             call
     * @deprecated Use {@link #requestExecutionBuilder()} to configure and execute a HTTP request.
     */
    @Deprecated
    public <T> Response<T> execute(Request<?> request,
                                   HttpResponseHandler<AmazonWebServiceResponse<T>> responseHandler,
                                   HttpResponseHandler<AmazonServiceException> errorResponseHandler,
                                   ExecutionContext executionContext) {
        return execute(request, responseHandler, errorResponseHandler, executionContext,
                       new AmazonWebServiceRequestAdapter(request.getOriginalRequest()));
    }

    @SdkInternalApi
    public <T> Response<T> execute(Request<?> request,
                                   HttpResponseHandler<AmazonWebServiceResponse<T>> responseHandler,
                                   HttpResponseHandler<AmazonServiceException> errorResponseHandler,
                                   ExecutionContext executionContext,
                                   RequestConfig requestConfig) {
        HttpResponseHandler<T> adaptedRespHandler = new AwsResponseHandlerAdapter<T>(
            getNonNullResponseHandler(responseHandler),
            request,
            executionContext.getAwsRequestMetrics(),
            responseMetadataCache);
        executionContext.setClientProtocol(this.config.getProtocol());
        return requestExecutionBuilder()
            .request(request)
            .requestConfig(requestConfig)
            .errorResponseHandler(new AwsErrorResponseHandler(errorResponseHandler, executionContext.getAwsRequestMetrics(), config))
            .executionContext(executionContext)
            .execute(adaptedRespHandler);
    }

    /**
     * Ensures the response handler is not null. If it is this method returns a dummy response
     * handler.
     *
     * @return Either original response handler or dummy response handler.
     */
    private <T> HttpResponseHandler<T> getNonNullResponseHandler(
            HttpResponseHandler<T> responseHandler) {
        if (responseHandler != null) {
            return responseHandler;
        } else {
            // Return a Dummy, No-Op handler
            return new HttpResponseHandler<T>() {

                @Override
                public T handle(HttpResponse response) throws Exception {
                    return null;
                }

                @Override
                public boolean needsConnectionLeftOpen() {
                    return false;
                }
            };
        }
    }

    /**
     * @return A builder used to configure and execute a HTTP request.
     */
    public RequestExecutionBuilder requestExecutionBuilder() {
        return new RequestExecutionBuilderImpl();
    }

    /**
     * Interface to configure a request execution and execute the request.
     */
    public interface RequestExecutionBuilder {

        /**
         * Fluent setter for {@link Request}
         *
         * @param request Request object
         * @return This builder for method chaining.
         */
        RequestExecutionBuilder request(Request<?> request);

        /**
         * Fluent setter for the error response handler
         *
         * @param errorResponseHandler Error response handler
         * @return This builder for method chaining.
         */
        RequestExecutionBuilder errorResponseHandler(
                HttpResponseHandler<? extends SdkBaseException> errorResponseHandler);

        /**
         * Fluent setter for the execution context
         *
         * @param executionContext Execution context
         * @return This builder for method chaining.
         */
        RequestExecutionBuilder executionContext(ExecutionContext executionContext);

        /**
         * Fluent setter for {@link RequestConfig}
         *
         * @param requestConfig Request config object
         * @return This builder for method chaining.
         */
        RequestExecutionBuilder requestConfig(RequestConfig requestConfig);

        /**
         * Executes the request with the given configuration.
         *
         * @param responseHandler Response handler that outputs the actual result type which is
         *                        preferred going forward.
         * @param <Output>        Result type
         * @return Unmarshalled result type.
         */
        <Output> Response<Output> execute(HttpResponseHandler<Output> responseHandler);

        /**
         * Executes the request with the given configuration; not handling response.
         *
         * @return Void response
         */
        Response<Void> execute();

    }

    private class RequestExecutionBuilderImpl implements RequestExecutionBuilder {

        private Request<?> request;
        private RequestConfig requestConfig;
        private HttpResponseHandler<? extends SdkBaseException> errorResponseHandler;
        private ExecutionContext executionContext = new ExecutionContext();

        @Override
        public RequestExecutionBuilder request(Request<?> request) {
            this.request = request;
            return this;
        }

        @Override
        public RequestExecutionBuilder errorResponseHandler(
                HttpResponseHandler<? extends SdkBaseException> errorResponseHandler) {
            this.errorResponseHandler = errorResponseHandler;
            return this;
        }

        @Override
        public RequestExecutionBuilder executionContext(
                ExecutionContext executionContext) {
            this.executionContext = executionContext;
            return this;
        }

        @Override
        public RequestExecutionBuilder requestConfig(RequestConfig requestConfig) {
            this.requestConfig = requestConfig;
            return this;
        }

        @Override
        public <Output> Response<Output> execute(HttpResponseHandler<Output> responseHandler) {
            RequestConfig config = requestConfig != null ? requestConfig : new AmazonWebServiceRequestAdapter(request.getOriginalRequest());
            return new RequestExecutor<Output>(request,
                                               config,
                                               getNonNullResponseHandler(errorResponseHandler),
                                               getNonNullResponseHandler(responseHandler),
                                               executionContext,
                                               getRequestHandlers()
            ).execute();
        }

        @Override
        public Response<Void> execute() {
            return execute(null);
        }

        private List<RequestHandler2> getRequestHandlers() {
            List<RequestHandler2> requestHandler2s = executionContext.getRequestHandler2s();
            if (requestHandler2s == null) {
                return Collections.emptyList();
            }
            return requestHandler2s;
        }

    }

    private class RequestExecutor<Output> {
        private final Request<?> request;
        private final RequestConfig requestConfig;
        private final HttpResponseHandler<? extends SdkBaseException> errorResponseHandler;
        private final HttpResponseHandler<Output> responseHandler;
        private final ExecutionContext executionContext;
        private final List<RequestHandler2> requestHandler2s;
        private final AWSRequestMetrics awsRequestMetrics;
        private final Protocol clientProtocol;
        //TODO: Call CSMRequestHandler directly in this class since it's CSM aware now
        private RequestHandler2 csmRequestHandler;

        private RequestExecutor(Request<?> request, RequestConfig requestConfig,
                                HttpResponseHandler<? extends SdkBaseException> errorResponseHandler,
                                HttpResponseHandler<Output> responseHandler,
                                ExecutionContext executionContext,
                                List<RequestHandler2> requestHandler2s) {
            this.request = request;
            this.requestConfig = requestConfig;
            this.errorResponseHandler = errorResponseHandler;
            this.responseHandler = responseHandler;
            this.executionContext = executionContext;
            this.requestHandler2s = requestHandler2s;
            this.awsRequestMetrics = executionContext.getAwsRequestMetrics();
            this.clientProtocol = executionContext.getClientProtocol();
            for (RequestHandler2 requestHandler2 : requestHandler2s) {
                if (requestHandler2 instanceof ClientSideMonitoringRequestHandler) {
                    csmRequestHandler = requestHandler2;
                    break;
                }
            }
        }

        /**
         * Executes the request and returns the result.
         */
        private Response<Output> execute() {
            if (executionContext == null) {
                throw new SdkClientException(
                        "Internal SDK Error: No execution context parameter specified.");
            }
            try {
                return executeWithTimer();
            } catch (InterruptedException ie) {
                throw handleInterruptedException(ie);
            } catch (AbortedException ae) {
                throw handleAbortedException(ae);
            } finally {
                if (executionContext.getClientExecutionTrackerTask().hasTimeoutExpired()) {
                    // There might be a race condition that the timeout tracker executed before the call to cancel(),
                    // which means it set this thread's interrupt flag, so just clear the interrupt flag
                    Thread.interrupted();
                }
            }
        }

        /**
         * Start and end client execution timer around the execution of the request. It's important
         * that the client execution task is canceled before the InterruptedExecption is handled by
         * {@link #execute()} so * the interrupt status doesn't leak out to the callers code
         */
        private Response<Output> executeWithTimer() throws InterruptedException {
            ClientExecutionAbortTrackerTask clientExecutionTrackerTask =
                    clientExecutionTimer.startTimer(getClientExecutionTimeout(requestConfig));
            Response<Output> outputResponse;

            try {
                executionContext.setClientExecutionTrackerTask(clientExecutionTrackerTask);
                outputResponse = doExecute();

            } finally {
                // Cancel the timeout tracker, guaranteeing that if it hasn't already executed and set this thread's
                // interrupt flag, it won't do so later. Every code path executed after this line *must* call
                // timeoutTracker.hasTimeoutExpired() and appropriately clear the interrupt flag if it returns true.
                executionContext.getClientExecutionTrackerTask().cancelTask();
            }

            return outputResponse;
        }

        private Response<Output> doExecute() throws InterruptedException {
            runBeforeRequestHandlers();
            setSdkTransactionId(request);
            setUserAgent(request);
            setTraceId(request);

            ProgressListener listener = requestConfig.getProgressListener();
            // add custom headers
            request.getHeaders().putAll(config.getHeaders());
            request.getHeaders().putAll(requestConfig.getCustomRequestHeaders());
            // add custom query parameters
            mergeQueryParameters(requestConfig.getCustomQueryParameters());
            Response<Output> response = null;
            final InputStream origContent = request.getContent();
            final InputStream toBeClosed = beforeRequest(); // for progress tracking
            // make "notCloseable", so reset would work with retries
            final InputStream notCloseable = (toBeClosed == null) ? null
                    : ReleasableInputStream.wrap(toBeClosed).disableClose();
            request.setContent(notCloseable);
            try {
                publishProgress(listener, ProgressEventType.CLIENT_REQUEST_STARTED_EVENT);
                response = executeHelper();
                publishProgress(listener, ProgressEventType.CLIENT_REQUEST_SUCCESS_EVENT);
                awsRequestMetrics.endEvent(AwsClientSideMonitoringMetrics.ApiCallLatency);
                awsRequestMetrics.getTimingInfo().endTiming();
                afterResponse(response);
                return response;
            } catch (AmazonClientException e) {
                publishProgress(listener, ProgressEventType.CLIENT_REQUEST_FAILED_EVENT);

                awsRequestMetrics.endEvent(AwsClientSideMonitoringMetrics.ApiCallLatency);
                // Exceptions generated here will block the rethrow of e.
                afterError(response, e);
                throw e;
            } finally {
                // Always close so any progress tracking would get the final events propagated.
                closeQuietlyForRuntimeExceptions(toBeClosed, log);
                request.setContent(origContent); // restore the original content
            }
        }

        private void closeQuietlyForRuntimeExceptions(Closeable c, Log log) {
            try {
                closeQuietly(c, log);
            } catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to close closeable", e);
                }
            }
        }

        private void runBeforeRequestHandlers() {
            AWSCredentials credentials = getCredentialsFromContext();
            request.addHandlerContext(HandlerContextKey.AWS_CREDENTIALS, credentials);
            request.addHandlerContext(HandlerContextKey.CLIENT_PROTOCOL, this.clientProtocol);
            // Apply any additional service specific request handlers that need to be run
            for (RequestHandler2 requestHandler2 : requestHandler2s) {
                // If the request handler is a type of CredentialsRequestHandler, then set the credentials in the request handler.
                if (requestHandler2 instanceof CredentialsRequestHandler) {
                    ((CredentialsRequestHandler) requestHandler2).setCredentials(credentials);
                }
                requestHandler2.beforeRequest(request);
            }
        }

        /**
         * Determine if an interrupted exception is caused by the client execution timer
         * interrupting the current thread or some other task interrupting the thread for another
         * purpose.
         *
         * @return {@link ClientExecutionTimeoutException} if the {@link InterruptedException} was
         * caused by the {@link ClientExecutionTimer}. Otherwise re-interrupts the current thread
         * and returns a {@link SdkClientException} wrapping an {@link InterruptedException}
         */
        private RuntimeException handleInterruptedException(InterruptedException e) {
            if (e instanceof SdkInterruptedException) {
                if (((SdkInterruptedException) e).getResponse() != null) {
                    ((SdkInterruptedException) e).getResponse().getHttpResponse().getHttpRequest().abort();
                }
            }
            if (executionContext.getClientExecutionTrackerTask().hasTimeoutExpired()) {
                // Clear the interrupt status
                Thread.interrupted();
                ClientExecutionTimeoutException exception = new ClientExecutionTimeoutException();
                reportClientExecutionTimeout(exception);
                return exception;
            } else {
                Thread.currentThread().interrupt();
                return new AbortedException(e);
            }
        }

        /**
         * Determine if an aborted exception is caused by the client execution timer interrupting
         * the current thread. If so throws {@link ClientExecutionTimeoutException} else throws the
         * original {@link AbortedException}
         *
         * @param ae aborted exception that occurred
         * @return {@link ClientExecutionTimeoutException} if the {@link AbortedException} was
         * caused by the {@link ClientExecutionTimer}. Otherwise throws the original {@link
         * AbortedException}
         */
        private RuntimeException handleAbortedException(final AbortedException ae) {
            if (executionContext.getClientExecutionTrackerTask().hasTimeoutExpired()) {
                // Clear the interrupt status
                Thread.interrupted();
                ClientExecutionTimeoutException exception = new ClientExecutionTimeoutException();
                reportClientExecutionTimeout(exception);
                return exception;
            } else {
                Thread.currentThread().interrupt();
                return ae;
            }
        }

        private void reportClientExecutionTimeout(ClientExecutionTimeoutException exception) {
            if (csmRequestHandler != null) {
                csmRequestHandler.afterError(request, null, exception);
            }
        }

        /**
         * Check if the thread has been interrupted. If so throw an {@link InterruptedException}.
         * Long running tasks should be periodically checked if the current thread has been
         * interrupted and handle it appropriately
         *
         * @throws InterruptedException If thread has been interrupted
         */
        private void checkInterrupted() throws InterruptedException {
            checkInterrupted(null);
        }

        /**
         * Check if the thread has been interrupted. If so throw an {@link InterruptedException}.
         * Long running tasks should be periodically checked if the current thread has been
         * interrupted and handle it appropriately
         *
         * @param response Response to be closed before returning control to the caller to avoid
         *                 leaking the connection.
         * @throws InterruptedException If thread has been interrupted
         */
        private void checkInterrupted(Response<?> response) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new SdkInterruptedException(response);
            }
        }

        /**
         * Merge query parameters into the given request.
         */
        private void mergeQueryParameters(Map<String, List<String>> params) {
            Map<String, List<String>> existingParams = request.getParameters();
            for (Entry<String, List<String>> param : params.entrySet()) {
                String pName = param.getKey();
                List<String> pValues = param.getValue();
                existingParams.put(pName, CollectionUtils.mergeLists(existingParams.get(pName), pValues));
            }
        }

        /**
         * Publishes the "request content length" event, and returns an input stream, which will be
         * made mark-and-resettable if possible, for progress tracking purposes.
         *
         * @return an input stream, which will be made mark-and-resettable if possible, for progress
         * tracking purposes; or null if the request doesn't have an input stream
         */
        private InputStream beforeRequest() {
            ProgressListener listener = requestConfig.getProgressListener();
            reportContentLength(listener);
            if (request.getContent() == null) {
                return null;
            }
            final InputStream content = monitorStreamProgress(listener,
                                                              buffer(
                                                                      makeResettable(
                                                                              request.getContent())));
            if (AmazonHttpClient.unreliableTestConfig == null) {
                return content;
            }
            return wrapWithUnreliableStream(content);
        }

        /**
         * If content length is present on the request, report it to the progress listener.
         *
         * @param listener Listener to notify.
         */
        private void reportContentLength(ProgressListener listener) {
            Map<String, String> headers = request.getHeaders();
            String contentLengthStr = headers.get("Content-Length");
            if (contentLengthStr != null) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    publishRequestContentLength(listener, contentLength);
                } catch (NumberFormatException e) {
                    log.warn("Cannot parse the Content-Length header of the request.");
                }
            }
        }

        /**
         * Make input stream resettable if possible.
         *
         * @param content Input stream to make resettable
         * @return ResettableInputStream if possible otherwise original input stream.
         */
        private InputStream makeResettable(InputStream content) {
            if (!content.markSupported()) {
                // try to wrap the content input stream to become
                // mark-and-resettable for signing and retry purposes.
                if (content instanceof FileInputStream) {
                    try {
                        // ResettableInputStream supports mark-and-reset without
                        // memory buffering
                        return new ResettableInputStream((FileInputStream) content);
                    } catch (IOException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("For the record; ignore otherwise", e);
                        }
                    }
                }
            }
            return content;
        }

        /**
         * Buffer input stream if possible.
         *
         * @param content Input stream to buffer
         * @return SdkBufferedInputStream if possible, otherwise original input stream.
         */
        private InputStream buffer(InputStream content) {
            if (!content.markSupported()) {
                content = new SdkBufferedInputStream(content);
            }
            return content;
        }

        /**
         * Wrap with a {@link ProgressInputStream} to report request progress to listener.
         *
         * @param listener Listener to report to
         * @param content  Input stream to monitor progress for
         * @return Wrapped input stream with progress monitoring capabilities.
         */
        private InputStream monitorStreamProgress(ProgressListener listener,
                                                  InputStream content) {
            return ProgressInputStream.inputStreamForRequest(content, listener);
        }

        /**
         * Used only for internal testing purposes. Makes a stream unreliable in certain ways for
         * fault testing.
         *
         * @param content Input stream to make unreliable.
         * @return UnreliableFilterInputStream
         */
        private InputStream wrapWithUnreliableStream(InputStream content) {
            return new UnreliableFilterInputStream(content,
                                                   unreliableTestConfig.isFakeIOException())
                    .withBytesReadBeforeException(
                            unreliableTestConfig.getBytesReadBeforeException())
                    .withMaxNumErrors(unreliableTestConfig.getMaxNumErrors())
                    .withResetIntervalBeforeException(
                            unreliableTestConfig.getResetIntervalBeforeException());
        }


        private void afterError(Response<?> response,
                                AmazonClientException e) throws InterruptedException {
            for (RequestHandler2 handler2 : requestHandler2s) {
                handler2.afterError(request, response, e);
                checkInterrupted(response);
            }
        }

        private <T> void afterResponse(Response<T> response) throws InterruptedException {
            for (RequestHandler2 handler2 : requestHandler2s) {
                handler2.afterResponse(request, response);
                checkInterrupted(response);
            }
        }

        private <T> void beforeAttempt(HandlerBeforeAttemptContext context) throws InterruptedException {
            for (RequestHandler2 handler2 : requestHandler2s) {
                handler2.beforeAttempt(context);
                checkInterrupted();
            }
        }

        private <T> void afterAttempt(HandlerAfterAttemptContext context) throws InterruptedException {
            for (RequestHandler2 handler2 : requestHandler2s) {
                handler2.afterAttempt(context);
                checkInterrupted(context.getResponse());
            }
        }

        /**
         * Internal method to execute the HTTP method given.
         */
        private Response<Output> executeHelper() throws InterruptedException {
        /*
         * add the service endpoint to the logs. You can infer service name from service endpoint
         */
            awsRequestMetrics
                    .addPropertyWith(Field.RequestType, requestConfig.getRequestType())
                    .addPropertyWith(Field.ServiceName, request.getServiceName())
                    .addPropertyWith(Field.ServiceEndpoint, request.getEndpoint());
            // Make a copy of the original request params and headers so that we can
            // permute it in this loop and start over with the original every time.
            final Map<String, List<String>> originalParameters = new LinkedHashMap<String, List<String>>(request.getParameters());
            final Map<String, String> originalHeaders = new HashMap<String, String>(request.getHeaders());
            // Always mark the input stream before execution.
            final ExecOneRequestParams execOneParams = new ExecOneRequestParams();
            final InputStream originalContent = request.getContent();
            if (originalContent != null && originalContent.markSupported()
                && !(originalContent instanceof BufferedInputStream)) {
                // Mark only once for non-BufferedInputStream
                final int readLimit = requestConfig.getRequestClientOptions().getReadLimit();
                originalContent.mark(readLimit);
            }
            awsRequestMetrics.startEvent(AwsClientSideMonitoringMetrics.ApiCallLatency);
            while (true) {
                checkInterrupted();
                if (originalContent instanceof BufferedInputStream && originalContent.markSupported()) {
                    // Mark everytime for BufferedInputStream, since the marker could have been invalidated
                    final int readLimit = requestConfig.getRequestClientOptions().getReadLimit();
                    originalContent.mark(readLimit);
                }
                execOneParams.initPerRetry();
                URI redirectedURI = execOneParams.redirectedURI;
                if (redirectedURI != null) {
                /*
                 * [scheme:][//authority][path][?query][#fragment]
                 */
                    String scheme = redirectedURI.getScheme();
                    String beforeAuthority = scheme == null ? "" : scheme + "://";
                    String authority = redirectedURI.getAuthority();
                    String path = redirectedURI.getPath();

                    request.setEndpoint(URI.create(beforeAuthority + authority));
                    request.setResourcePath(SdkHttpUtils.urlEncode(path, true));
                    awsRequestMetrics.addPropertyWith(Field.RedirectLocation,
                                                      redirectedURI.toString());

                }
                if (execOneParams.authRetryParam != null) {
                    request.setEndpoint(execOneParams.authRetryParam.getEndpointForRetry());
                }
                awsRequestMetrics.setCounter(Field.RequestCount, execOneParams.requestCount);
                if (execOneParams.isRetry()) {
                    request.setParameters(originalParameters);
                    request.setHeaders(originalHeaders);
                    request.setContent(originalContent);
                }

                Response<Output> response = null;
                Exception savedException = null;
                boolean thrown = false;
                try {
                    HandlerBeforeAttemptContext beforeAttemptContext = HandlerBeforeAttemptContext.builder()
                            .withRequest(request)
                            .build();

                    beforeAttempt(beforeAttemptContext);
                    response = executeOneRequest(execOneParams);
                    savedException = execOneParams.retriedException;

                    if (response != null) {
                        return response;
                    }
                } catch (IOException ioe) {
                    savedException = ioe;
                    handleRetryableException(execOneParams, ioe);
                } catch (InterruptedException ie) {
                    savedException = ie;
                    thrown = true;
                    throw ie;
                } catch (RuntimeException e) {
                    savedException = e;
                    thrown = true;
                    throw lastReset(captureExceptionMetrics(e));
                } catch (Error e) {
                    thrown = true;
                    throw lastReset(captureExceptionMetrics(e));
                } finally {
                /*
                 * Some response handlers need to manually manage the HTTP connection and will take
                 * care of releasing the connection on their own, but if this response handler
                 * doesn't need the connection left open, we go ahead and release the it to free up
                 * resources. But if we throw, then the caller doesn't get the handle on the response
                 * to close for themselves. In this case, we will close the connection for them as well.
                 */
                    if (!execOneParams.leaveHttpConnectionOpen || thrown) {
                        if (execOneParams.apacheResponse != null) {
                            HttpEntity entity = execOneParams.apacheResponse.getEntity();
                            if (entity != null) {
                                try {
                                    closeQuietly(entity.getContent(), log);
                                } catch (IOException e) {
                                    log.warn("Cannot close the response content.", e);
                                }
                            }
                        }
                    }

                    HandlerAfterAttemptContext afterAttemptContext = HandlerAfterAttemptContext.builder()
                            .withRequest(request)
                            .withResponse(response)
                            .withException(savedException)
                            .build();

                    /*
                     * Exceptions generated here will replace ones rethrown in catch-blocks
                     * above or thrown in the original try-block.
                     */
                    afterAttempt(afterAttemptContext);
                }
            } /* end while (true) */
        }

        private void handleRetryableException(ExecOneRequestParams execOneParams, Exception e) {
            captureExceptionMetrics(e);
            awsRequestMetrics.addProperty(Field.AWSRequestID, null);
            SdkClientException sdkClientException;
            if (!(e instanceof SdkClientException)) {
                sdkClientException = new SdkClientException(
                        "Unable to execute HTTP request: " + e.getMessage(), e);
            } else {
                sdkClientException = (SdkClientException) e;
            }
            boolean willRetry = shouldRetry(execOneParams, sdkClientException);
            if (log.isTraceEnabled()) {
                log.trace(sdkClientException.getMessage() + (willRetry ? " Request will be retried." : ""), e);
            } else if (log.isDebugEnabled()) {
                log.debug(sdkClientException.getMessage() + (willRetry ? " Request will be retried." : ""));
            }
            if (!willRetry) {
                throw lastReset(sdkClientException);
            }
            // Cache the retryable exception
            execOneParams.retriedException = sdkClientException;
        }

        /**
         * Used to perform a last reset on the content input stream (if mark-supported); this is so
         * that, for backward compatibility reason, any "blind" retry (ie without calling reset) by
         * user of this library with the same input stream (such as ByteArrayInputStream) could
         * still succeed.
         *
         * @param t the failure
         * @return the failure as given
         */
        private <T extends Throwable> T lastReset(final T t) {
            try {
                InputStream content = request.getContent();
                if (content != null) {
                    if (content.markSupported()) {
                        content.reset();
                    }
                }
            } catch (Exception ex) {
                log.debug("FYI: failed to reset content inputstream before throwing up", ex);
            }
            return t;
        }

        /**
         * Returns the credentials from the execution if exists. Else returns null.
         */
        private AWSCredentials getCredentialsFromContext() {
            final AWSCredentialsProvider credentialsProvider = executionContext.getCredentialsProvider();

            AWSCredentials credentials = null;
            if (credentialsProvider != null) {
                awsRequestMetrics.startEvent(Field.CredentialsRequestTime);
                try {
                    credentials = credentialsProvider.getCredentials();
                } finally {
                    awsRequestMetrics.endEvent(Field.CredentialsRequestTime);
                }
            }
            return credentials;
        }

        /**
         * Returns the response from executing one httpClientSettings request; or null for retry.
         */
        private Response<Output> executeOneRequest(ExecOneRequestParams execOneParams)
                throws IOException, InterruptedException {

            if (execOneParams.isRetry()) {
                resetRequestInputStream(request, execOneParams.retriedException);
            }
            checkInterrupted();
            if (requestLog.isDebugEnabled()) {
                requestLog.debug((execOneParams.isRetry() ? "Retrying " : "Sending ") + "Request: " + request);
            }
            final AWSCredentials credentials = getCredentialsFromContext();
            final ProgressListener listener = requestConfig.getProgressListener();

            getSendToken();

            if (execOneParams.isRetry()) {
                pauseBeforeRetry(execOneParams, listener);
            }

            updateRetryHeaderInfo(request, execOneParams);
            sdkRequestHeaderProvider.addSdkRequestRetryHeader(request, execOneParams.requestCount);

            // Sign the request if a signer was provided
            execOneParams.newSigner(request, executionContext);
            if (execOneParams.signer != null &&
                (credentials != null || execOneParams.signer instanceof CanHandleNullCredentials)) {
                awsRequestMetrics.startEvent(Field.RequestSigningTime);
                try {
                    if (timeOffset != 0) {
                        // Always use the client level timeOffset if it was
                        // non-zero; Otherwise, we respect the timeOffset in the
                        // request, which could have been externally configured (at
                        // least for the 1st non-retry request).
                        //
                        // For retry due to clock skew, the timeOffset in the
                        // request used for the retry is assumed to have been
                        // adjusted when execution reaches here.
                        request.setTimeOffset(timeOffset);
                    }

                    execOneParams.signer.sign(request, credentials);
                } finally {
                    awsRequestMetrics.endEvent(Field.RequestSigningTime);
                }
            }

            checkInterrupted();
            execOneParams.newApacheRequest(httpRequestFactory, request, httpClientSettings);

            captureConnectionPoolMetrics();

            final HttpClientContext localRequestContext =
                    ApacheUtils.newClientContext(httpClientSettings, ImmutableMapParameter.of
                            (AWSRequestMetrics.SIMPLE_NAME, awsRequestMetrics));

            execOneParams.resetBeforeHttpRequest();
            publishProgress(listener, ProgressEventType.HTTP_REQUEST_STARTED_EVENT);
            awsRequestMetrics.startEvent(Field.HttpRequestTime);
            awsRequestMetrics.setCounter(Field.RetryCapacityConsumed, retryCapacity.consumedCapacity());

            /////////// Send HTTP request ////////////
            executionContext.getClientExecutionTrackerTask().setCurrentHttpRequest(execOneParams.apacheRequest);
            final HttpRequestAbortTaskTracker requestAbortTaskTracker = httpRequestTimer
                    .startTimer(execOneParams.apacheRequest, getRequestTimeout(requestConfig));

            try {
                execOneParams.apacheResponse = httpClient.execute(execOneParams.apacheRequest, localRequestContext);
                if (shouldBufferHttpEntity(responseHandler.needsConnectionLeftOpen(),
                                           executionContext,
                                           execOneParams,
                                           requestAbortTaskTracker)) {
                    execOneParams.apacheResponse
                            .setEntity(new BufferedHttpEntity(
                                    execOneParams.apacheResponse.getEntity()));
                }
            } catch (IOException ioe) {
                // Client execution timeouts take precedence as it's not retryable
                if (executionContext.getClientExecutionTrackerTask().hasTimeoutExpired()) {
                    throw new InterruptedException();
                } else if (requestAbortTaskTracker.httpRequestAborted()) {
                    // Interrupt flag can leak from apache when aborting the request
                    // https://issues.apache.org/jira/browse/HTTPCLIENT-1958, TT0174038332
                    if (ioe instanceof RequestAbortedException) {
                        Thread.interrupted();
                    }
                     throw new HttpRequestTimeoutException(ioe);
                } else {
                    throw ioe;
                }
            } finally {
                requestAbortTaskTracker.cancelTask();
                awsRequestMetrics.endEvent(Field.HttpRequestTime);
            }

            publishProgress(listener, ProgressEventType.HTTP_REQUEST_COMPLETED_EVENT);
            final StatusLine statusLine = execOneParams.apacheResponse.getStatusLine();
            final int statusCode = statusLine == null ? -1 : statusLine.getStatusCode();

            // Always update estimated skew if the wire call is successful.
            clockSkewAdjuster.updateEstimatedSkew(new AdjustmentRequest()
                                                      .clientRequest(request)
                                                      .serviceResponse(execOneParams.apacheResponse));

            if (ApacheUtils.isRequestSuccessful(execOneParams.apacheResponse)) {
                return handleSuccessResponse(execOneParams, localRequestContext, statusCode);
            }

            return handleServiceErrorResponse(execOneParams, localRequestContext, statusCode);
        }

        /**
         * Has signer been explicitly overridden in the configuration?
         */
        private boolean isSignerOverridden() {
            return config != null && config.getSignerOverride() != null;
        }

        /**
         * Handle service error response and check if the response is retryable or not.
         */
        private Response<Output> handleServiceErrorResponse(ExecOneRequestParams execOneParams, HttpClientContext localRequestContext, int statusCode) throws IOException, InterruptedException {
            if (isTemporaryRedirect(execOneParams.apacheResponse)) {
            /*
             * S3 sends 307 Temporary Redirects if you try to delete an EU bucket from the US
             * endpoint. If we get a 307, we'll point the HTTP method to the redirected location,
             * and let the next retry deliver the request to the right location.
             */
                Header[] locationHeaders = execOneParams.apacheResponse.getHeaders("location");
                String redirectedLocation = locationHeaders[0].getValue();
                if (log.isDebugEnabled()) {
                    log.debug("Redirecting to: " + redirectedLocation);
                }
                execOneParams.redirectedURI = URI.create(redirectedLocation);
                awsRequestMetrics.addPropertyWith(Field.StatusCode, statusCode)
                                 .addPropertyWith(Field.AWSRequestID, null);
                return null; // => retry
            }
            execOneParams.leaveHttpConnectionOpen = errorResponseHandler.needsConnectionLeftOpen();
            final SdkBaseException exception = handleErrorResponse(execOneParams.apacheRequest,
                                                                   execOneParams.apacheResponse,
                                                                   localRequestContext);

            ClockSkewAdjustment clockSkewAdjustment =
                    clockSkewAdjuster.getAdjustment(new AdjustmentRequest().exception(exception)
                                                                           .clientRequest(request)
                                                                           .serviceResponse(execOneParams.apacheResponse));

            if (clockSkewAdjustment.shouldAdjustForSkew()) {
                timeOffset = clockSkewAdjustment.inSeconds();
                request.setTimeOffset(timeOffset); // adjust time offset for the retry
                SDKGlobalTime.setGlobalTimeOffset(timeOffset);
            }

            // In an error case, we only want to update the sending rate if we
            // got a throttling exception.
            //
            // The success case (throttlingResponse = false) is handled in
            // handleSuccessResponse, and transient errors don't have an effect.
            if (RetryUtils.isThrottlingException(exception)) {
                tokenBucket.updateClientSendingRate(true);
            }

            // Check whether we should internally retry the auth error
            execOneParams.authRetryParam = null;
            AuthErrorRetryStrategy authRetry = executionContext.getAuthErrorRetryStrategy();
            if (authRetry != null && exception instanceof AmazonServiceException) {
                HttpResponse httpResponse = ApacheUtils.createResponse(request, execOneParams.apacheRequest, execOneParams.apacheResponse, localRequestContext);
                execOneParams.authRetryParam = authRetry
                        .shouldRetryWithAuthParam(request, httpResponse, (AmazonServiceException) exception);
            }
            if (execOneParams.authRetryParam == null && !shouldRetry(execOneParams, exception)) {
                throw exception;
            }
            // Comment out for now. Ref: CR2662349
            // Preserve the cause of retry before retrying
            // awsRequestMetrics.addProperty(RetryCause, ase);
            if (RetryUtils.isThrottlingException(exception)) {
                awsRequestMetrics.incrementCounterWith(Field.ThrottleException)
                        .addProperty(Field.ThrottleException, exception);
            }
            // Cache the retryable exception
            execOneParams.retriedException = exception;

            return null; // => retry
        }

        /**
         * Handle success response.
         */
        private Response<Output> handleSuccessResponse(ExecOneRequestParams execOneParams, HttpClientContext localRequestContext, int statusCode) throws IOException, InterruptedException {
            awsRequestMetrics.addProperty(Field.StatusCode, statusCode);
            /*
             * If we get back any 2xx status code, then we know we should treat the service call as
             * successful.
             */
            execOneParams.leaveHttpConnectionOpen = responseHandler.needsConnectionLeftOpen();
            HttpResponse httpResponse = ApacheUtils.createResponse(request, execOneParams.apacheRequest, execOneParams.apacheResponse, localRequestContext);
            Output response = handleResponse(httpResponse);

            /*
             * If this was a successful retry attempt we'll release the full retry capacity that
             * the attempt originally consumed.  If this was a successful initial request
             * we return a lesser amount.
             */
            if (execOneParams.isRetry() && executionContext.retryCapacityConsumed()) {
                retryCapacity.release(execOneParams.lastConsumedRetryCapacity);
            } else {
                retryCapacity.release();
            }

            tokenBucket.updateClientSendingRate(false);

            return new Response<Output>(response, httpResponse);
        }

        /**
         * Reset the input stream of the request before a retry.
         *
         * @param request Request containing input stream to reset
         * @param retriedException
         * @throws ResetException If Input Stream can't be reset which means the request can't be
         *                        retried
         */
        private void resetRequestInputStream(final Request<?> request, SdkBaseException retriedException)
                throws ResetException {
            InputStream requestInputStream = request.getContent();
            if (requestInputStream != null) {
                if (requestInputStream.markSupported()) {
                    try {
                        requestInputStream.reset();
                    } catch (IOException ex) {
                        ResetException resetException = new ResetException(
                                "The request to the service failed with a retryable reason, but resetting the request input " +
                                "stream has failed. See exception.getExtraInfo or debug-level logging for the original failure " +
                                "that caused this retry.",
                                ex);
                        resetException.setExtraInfo(retriedException.getMessage());
                        throw resetException;
                    }
                }
            }
        }

        /**
         * @return True if the {@link HttpEntity} should be wrapped in a {@link BufferedHttpEntity}
         */
        private boolean shouldBufferHttpEntity(final boolean needsConnectionLeftOpen,
                                               final ExecutionContext execContext,
                                               ExecOneRequestParams execParams,
                                               final HttpRequestAbortTaskTracker requestAbortTaskTracker) {
            return (execContext.getClientExecutionTrackerTask().isEnabled() ||
                    requestAbortTaskTracker.isEnabled())
                   && !needsConnectionLeftOpen && execParams.apacheResponse.getEntity() != null;
        }


        /**
         * Captures the connection pool metrics.
         */
        private void captureConnectionPoolMetrics() {
            if (awsRequestMetrics.isEnabled() &&
                httpClient.getHttpClientConnectionManager() instanceof
                        ConnPoolControl<?>) {
                final PoolStats stats = ((ConnPoolControl<?>) httpClient
                        .getHttpClientConnectionManager()).getTotalStats();

                awsRequestMetrics
                        .withCounter(HttpClientPoolAvailableCount, stats.getAvailable())
                        .withCounter(HttpClientPoolLeasedCount, stats.getLeased())
                        .withCounter(HttpClientPoolPendingCount, stats.getPending());
            }

        }

        /**
         * Capture the metrics for the given throwable.
         */
        private <T extends Throwable> T captureExceptionMetrics(T t) {
            awsRequestMetrics.incrementCounterWith(Field.Exception)
                    .addProperty(Field.Exception, t);
            if (t instanceof AmazonServiceException) {
                AmazonServiceException ase = (AmazonServiceException) t;
                if (RetryUtils.isThrottlingException(ase)) {
                    awsRequestMetrics.incrementCounterWith(Field.ThrottleException)
                            .addProperty(Field.ThrottleException, ase);
                }
            }
            return t;
        }

        /**
         * Create a client side identifier that will be sent with the initial request and each
         * retry.
         */
        private void setSdkTransactionId(Request<?> request) {
            request.addHeader(HEADER_SDK_TRANSACTION_ID,
                              new UUID(random.nextLong(), random.nextLong()).toString());
        }

        /**
         * Sets a User-Agent for the specified request, taking into account any custom data.
         */
        private void setUserAgent(Request<?> request) {
            RequestClientOptions opts = requestConfig.getRequestClientOptions();
            AWSCredentials credentials = request.getHandlerContext(HandlerContextKey.AWS_CREDENTIALS);
            if (opts != null) {
                request.addHeader(HEADER_USER_AGENT, RuntimeHttpUtils
                        .getUserAgent(config, opts.getClientMarker(Marker.USER_AGENT), credentials));
            } else {
                request.addHeader(HEADER_USER_AGENT, RuntimeHttpUtils.getUserAgent(config, null, credentials));
            }
        }

        /**
         * Sets the trace id for the specified request if it doesn't exist in the header and it's present in the
         * environment variables.
         */
        private void setTraceId(Request<?> request) {
            String traceIdHeader = request.getHeaders().get(TRACE_ID_HEADER);
            if (StringUtils.isNullOrEmpty(traceIdHeader)) {
                String traceId = RuntimeHttpUtils.getLambdaEnvironmentTraceId();
                if  (!StringUtils.isNullOrEmpty(traceId)) {
                    request.addHeader(TRACE_ID_HEADER, traceId);
                }
            }
        }

        /**
         * Adds Retry information to the {@link #HEADER_SDK_RETRY_INFO} header. Used for analysis of
         * retry policy.
         *
         * @param request              Request to add header to
         * @param execOneRequestParams Request context containing retry information
         */
        private void updateRetryHeaderInfo(Request<?> request,
                                           ExecOneRequestParams execOneRequestParams) {
            int availableRetryCapacity = retryCapacity.availableCapacity();

            String headerValue = String.format("%s/%s/%s",
                                               execOneRequestParams.requestCount - 1,
                                               execOneRequestParams.lastBackoffDelay,
                                               availableRetryCapacity >= 0 ?
                                                       availableRetryCapacity : "");

            request.addHeader(HEADER_SDK_RETRY_INFO, headerValue);
        }

        /**
         * Returns true if a failed request should be retried.
         *
         * @param params    Params for the individual request being executed.
         * @param exception The client/service exception from the failed request.
         * @return True if the failed request should be retried.
         */
        private boolean shouldRetry(ExecOneRequestParams params, SdkBaseException exception) {
            final int retriesAttempted = params.requestCount - 1;
            final HttpRequestBase method = params.apacheRequest;

            // Never retry on requests containing non-repeatable entity
            if (method instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
                if (entity != null && !entity.isRepeatable()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Entity not repeatable");
                    }
                    return false;
                }
            }

            RetryPolicyContext context = RetryPolicyContext.builder()
                                                           .request(request)
                                                           .originalRequest(requestConfig.getOriginalRequest())
                                                           .exception(exception)
                                                           .retriesAttempted(retriesAttempted)
                                                           .httpStatusCode(params.getStatusCode())
                                                           .build();

            if (!acquireRetryCapacity(context, params)) {
                return false;
            }

            // Finally, pass all the context information to the RetryCondition and let it
            // decide whether it should be retried.
            if (!retryPolicy.shouldRetry(context)) {
                // If the retry policy fails we immediately return consumed capacity to the pool.
                if (executionContext.retryCapacityConsumed()) {
                    retryCapacity.release(THROTTLED_RETRY_COST);
                }
                reportMaxRetriesExceededIfRetryable(context);
                return false;
            }

            return true;
        }

        /**
         * If ADAPTIVE retry mode is enabled, this attempts to acquire a token from the bucket.
         * <p>
         * For other modes, this is a noop.
         */
        private void getSendToken() {
            if (retryMode != RetryMode.ADAPTIVE) {
                return;
            }

            if (!tokenBucket.acquire(1, fastFailRateLimiting())) {
                throw new SdkClientException("Unable to acquire enough send tokens to execute request.");
            }
        }

        private boolean fastFailRateLimiting() {
            return config.getRetryPolicy().isFastFailRateLimiting();
        }

        /**
         * Attempts to acquire retry capacity.
         *
         * @return true if retry capacity can be acquired, false otherwise.
         */
        private boolean acquireRetryCapacity(RetryPolicyContext context, ExecOneRequestParams params) {
            switch (retryMode) {
                case LEGACY:
                    return legacyAcquireRetryCapacity(context, params);
                case ADAPTIVE:
                case STANDARD:
                    return standardAcquireRetryCapacity(context, params);
                default:
                    throw new IllegalStateException("Unsupported retry mode: " + retryMode);
            }
        }

        private boolean standardAcquireRetryCapacity(RetryPolicyContext context, ExecOneRequestParams params) {
            SdkBaseException exception = context.exception();
            if (isTimeoutError(exception)) {
                return doAcquireCapacity(context, TIMEOUT_RETRY_COST, params);
            }

            return doAcquireCapacity(context, THROTTLED_RETRY_COST, params);
        }

        private boolean isTimeoutError(SdkBaseException exception) {
            Throwable cause = exception.getCause();
            return cause instanceof ConnectTimeoutException || cause instanceof SocketTimeoutException;
        }

        private boolean legacyAcquireRetryCapacity(RetryPolicyContext context, ExecOneRequestParams params) {
            // For legacy retry mode, we only attempt to acquire capacity for non-throttling errors
            if (!RetryUtils.isThrottlingException(context.exception())) {
                // Do not use retry capacity for throttling exceptions if the retry mode
                return doAcquireCapacity(context, THROTTLED_RETRY_COST, params);
            } else {
                return true;
            }
        }

        private boolean doAcquireCapacity(RetryPolicyContext context, int retryCost, ExecOneRequestParams params) {
            // See if we have enough available retry capacity to be able to execute
            // this retry attempt.
            if (!retryCapacity.acquire(retryCost)) {
                awsRequestMetrics.incrementCounter(ThrottledRetryCount);
                reportMaxRetriesExceededIfRetryable(context);
                return false;
            }
            params.lastConsumedRetryCapacity = retryCost;
            executionContext.markRetryCapacityConsumed();
            return true;
        }

        private void reportMaxRetriesExceededIfRetryable(RetryPolicyContext context) {
            if (retryPolicy instanceof RetryPolicyAdapter && ((RetryPolicyAdapter) retryPolicy).isRetryable(context)) {
                awsRequestMetrics.addPropertyWith(MaxRetriesExceeded, true);
            }
        }

        /**
         * Handles a successful response from a service call by unmarshalling the results using the
         * specified response handler.
         *
         * @return The contents of the response, unmarshalled using the specified response handler.
         * @throws IOException If any problems were encountered reading the response contents from
         *                     the HTTP method object.
         */
        @SuppressWarnings("deprecation")
        private Output handleResponse(HttpResponse httpResponse) throws IOException,
                                                                        InterruptedException {
            ProgressListener listener = requestConfig.getProgressListener();
            try {
            /*
             * Apply the byte counting stream wrapper if the legacy runtime profiling is enabled.
             */
                CountingInputStream countingInputStream = null;
                InputStream is = httpResponse.getContent();
                if (is != null) {
                    if (System.getProperty(PROFILING_SYSTEM_PROPERTY) != null) {
                        is = countingInputStream = new CountingInputStream(is);
                        httpResponse.setContent(is);
                    }
                    httpResponse.setContent(ProgressInputStream.inputStreamForResponse(is, listener));
                }
                Map<String, String> headers = httpResponse.getHeaders();
                String s = headers.get("Content-Length");
                if (s != null) {
                    try {
                        long contentLength = Long.parseLong(s);
                        publishResponseContentLength(listener, contentLength);
                    } catch (NumberFormatException e) {
                        log.warn("Cannot parse the Content-Length header of the response.");
                    }
                }

                Output awsResponse;
                awsRequestMetrics.startEvent(Field.ResponseProcessingTime);
                publishProgress(listener, ProgressEventType.HTTP_RESPONSE_STARTED_EVENT);
                try {
                    awsResponse = responseHandler
                            .handle(beforeUnmarshalling(httpResponse));
                } finally {
                    awsRequestMetrics.endEvent(Field.ResponseProcessingTime);
                }
                publishProgress(listener, ProgressEventType.HTTP_RESPONSE_COMPLETED_EVENT);

                if (countingInputStream != null) {
                    awsRequestMetrics
                            .setCounter(Field.BytesProcessed, countingInputStream.getByteCount());
                }
                return awsResponse;
            } catch (CRC32MismatchException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (AmazonClientException e) {
                throw e; // simply rethrow rather than further wrapping it
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                String errorMessage =
                        "Unable to unmarshall response (" + e.getMessage() + "). Response Code: "
                        + httpResponse.getStatusCode() + ", Response Text: " +
                        httpResponse.getStatusText();
                throw new SdkClientException(errorMessage, e);
            }
        }

        /**
         * Run {@link RequestHandler2#beforeUnmarshalling(Request, HttpResponse)} callback
         *
         * @param origHttpResponse Original {@link HttpResponse}
         * @return {@link HttpResponse} object to pass to unmarshaller. May have been modified or
         * replaced by the request handlers
         */
        private HttpResponse beforeUnmarshalling(HttpResponse origHttpResponse) {
            HttpResponse toReturn = origHttpResponse;
            for (RequestHandler2 requestHandler : requestHandler2s) {
                toReturn = requestHandler.beforeUnmarshalling(request, toReturn);
            }
            return toReturn;
        }

        /**
         * Responsible for handling an error response, including unmarshalling the error response
         * into the most specific exception type possible, and throwing the exception.
         *
         * @param method The HTTP method containing the actual response content.
         * @throws IOException If any problems are encountering reading the error response.
         */
        private SdkBaseException handleErrorResponse(HttpRequestBase method,
                                                           final org.apache.http.HttpResponse apacheHttpResponse,
                                                           final HttpContext context)
                throws IOException, InterruptedException {
            final StatusLine statusLine = apacheHttpResponse.getStatusLine();
            final int statusCode;
            final String reasonPhrase;
            if (statusLine == null) {
                statusCode = -1;
                reasonPhrase = null;
            } else {
                statusCode = statusLine.getStatusCode();
                reasonPhrase = statusLine.getReasonPhrase();
            }
            HttpResponse response = ApacheUtils.createResponse(request, method, apacheHttpResponse, context);
            SdkBaseException exception;
            try {
                exception = errorResponseHandler.handle(response);
                if (requestLog.isDebugEnabled()) {
                    requestLog.debug("Received error response: " + exception);
                }
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                } else {
                    String errorMessage = "Unable to unmarshall error response (" + e.getMessage() +
                                          "). Response Code: "
                                          + (statusLine == null ? "None" : statusCode) +
                                          ", Response Text: " + reasonPhrase;
                    throw new SdkClientException(errorMessage, e);
                }
            }

            exception.fillInStackTrace();
            return exception;
        }

        /**
         * Pause before the next retry and record metrics around retry behavior.
         */
        private void pauseBeforeRetry(ExecOneRequestParams execOneParams,
                                      final ProgressListener listener) throws InterruptedException {
            publishProgress(listener, ProgressEventType.CLIENT_REQUEST_RETRY_EVENT);
            // Notify the progress listener of the retry
            awsRequestMetrics.startEvent(Field.RetryPauseTime);
            try {
                doPauseBeforeRetry(execOneParams);
            } finally {
                awsRequestMetrics.endEvent(Field.RetryPauseTime);
            }
        }

        /**
         * Sleep for a period of time on failed request to avoid flooding a service with retries.
         */
        private void doPauseBeforeRetry(ExecOneRequestParams execOneParams) throws InterruptedException {
            final int retriesAttempted = execOneParams.requestCount - 2;
            RetryPolicyContext context = RetryPolicyContext.builder()
                    .request(request)
                    .originalRequest(requestConfig.getOriginalRequest())
                    .retriesAttempted(retriesAttempted)
                    .exception(execOneParams.retriedException)
                    .build();
            // don't pause if the retry was not due to a redirection (I.E. when retried exception is null)
            if (context.exception() != null) {
                long delay = retryPolicy.computeDelayBeforeNextRetry(context);
                execOneParams.lastBackoffDelay = delay;

                if (log.isDebugEnabled()) {
                    log.debug("Retriable error detected, " + "will retry in " + delay +
                              "ms, attempt number: " + retriesAttempted);
                }
                Thread.sleep(delay);
            }
        }

        /**
         * Gets the correct request timeout taking into account precedence of the configuration in
         * {@link AmazonWebServiceRequest} versus {@link ClientConfiguration}
         *
         * @param requestConfig Current request configuration
         * @return Request timeout value or 0 if none is set
         */
        private int getRequestTimeout(RequestConfig requestConfig) {
            if (requestConfig.getRequestTimeout() != null) {
                return requestConfig.getRequestTimeout();
            } else {
                return config.getRequestTimeout();
            }
        }

        /**
         * Gets the correct client execution timeout taking into account precedence of the
         * configuration in {@link AmazonWebServiceRequest} versus {@link ClientConfiguration}
         *
         * @param requestConfig Current request configuration
         * @return Client Execution timeout value or 0 if none is set
         */
        private int getClientExecutionTimeout(RequestConfig requestConfig) {
            if (requestConfig.getClientExecutionTimeout() != null) {
                return requestConfig.getClientExecutionTimeout();
            } else {
                return config.getClientExecutionTimeout();
            }
        }

        /**
         * Stateful parameters that are used for executing a single httpClientSettings request.
         */
        private class ExecOneRequestParams {
            int requestCount; // monotonic increasing
            /**
             * Last delay between retries
             */
            long lastBackoffDelay = 0;
            SdkBaseException retriedException; // last retryable exception
            HttpRequestBase apacheRequest;
            org.apache.http.HttpResponse apacheResponse;
            URI redirectedURI;
            AuthRetryParameters authRetryParam;
            int lastConsumedRetryCapacity;
            /*
             * Depending on which response handler we end up choosing to handle the HTTP response, it
             * might require us to leave the underlying HTTP connection open, depending on whether or
             * not it reads the complete HTTP response stream from the HTTP connection, or if delays
             * reading any of the content until after a response is returned to the caller.
             */
            boolean leaveHttpConnectionOpen;
            private Signer signer; // cached
            private URI signerURI;

            boolean isRetry() {
                return requestCount > 1 || redirectedURI != null || authRetryParam != null;
            }

            void initPerRetry() {
                requestCount++;
                apacheRequest = null;
                apacheResponse = null;
                leaveHttpConnectionOpen = false;
            }

            void newSigner(final Request<?> request, final ExecutionContext execContext) {
                final SignerProviderContext.Builder signerProviderContext = SignerProviderContext
                        .builder()
                        .withRequest(request)
                        .withRequestConfig(requestConfig);
                if (authRetryParam != null) {
                    signerURI = authRetryParam.getEndpointForRetry();
                    signer = authRetryParam.getSignerForRetry();
                    // Push the local signer override back to the execution context
                    execContext.setSigner(signer);
                } else if (redirectedURI != null && !redirectedURI.equals(signerURI)) {
                    signerURI = redirectedURI;
                    signer = execContext.getSigner(signerProviderContext
                                                           .withUri(signerURI)
                                                           .withIsRedirect(true)
                                                           .build());

                    if (signer instanceof AWS4Signer) {
                        String regionName = ((AWS4Signer) signer).getRegionName();
                        if (regionName != null) {
                            request.addHandlerContext(HandlerContextKey.SIGNING_REGION, regionName);
                        }
                    }
                } else if (signer == null) {
                    signerURI = request.getEndpoint();
                    signer = execContext
                            .getSigner(signerProviderContext.withUri(signerURI).build());
                }
            }

            /**
             * @throws FakeIOException thrown only during test simulation
             */
            HttpRequestBase newApacheRequest(
                    final HttpRequestFactory<HttpRequestBase> httpRequestFactory,
                    final Request<?> request,
                    final HttpClientSettings options) throws IOException {

                apacheRequest = httpRequestFactory.create(request, options);
                if (redirectedURI != null) {
                    apacheRequest.setURI(redirectedURI);
                }
                return apacheRequest;
            }

            void resetBeforeHttpRequest() {
                retriedException = null;
                authRetryParam = null;
                redirectedURI = null;
            }

            private Integer getStatusCode() {
                if (apacheResponse == null || apacheResponse.getStatusLine() == null) {
                    return null;
                }
                return apacheResponse.getStatusLine().getStatusCode();
            }
        }
    }
}
