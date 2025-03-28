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

import com.ibm.cloud.objectstorage.annotation.NotThreadSafe;
import com.ibm.cloud.objectstorage.metrics.MetricType;
import com.ibm.cloud.objectstorage.metrics.RequestMetricType;

import java.util.Collections;
import java.util.List;

/**
 * Used as both a base class and a minimal support of AWS SDK request metrics.
 * The base class of supporting AWS SDK request metrics.
 * <p>
 * In contrast to {@link AWSRequestMetricsFullSupport}, which is intended to be
 * a full support of AWS SDK request metrics, this class only provides access to
 * a {@link TimingInfo} instance that only has minimal support for start and end
 * time (ie with no-ops for sub-event measurements) for backward compatibility
 * reason. The other methods related to properties and counters in this class
 * are effectively no-ops.
 * <p>
 * This class is instantiated instead of {@link AWSRequestMetricsFullSupport}
 * when request metric collection is not required during a particular service
 * request/response cycle.
 */
@NotThreadSafe
public class AWSRequestMetrics {
    /**
     *  If the class name is required for logging and metrics we should use this
     *  constant version instead of the expensive function call.
     */
    public static final String SIMPLE_NAME = AWSRequestMetrics.class.getSimpleName();

    /**
     * Predefined AWS SDK metric types general across all AWS clients. Client
     * specific predefined metrics like S3 or DynamoDB are defined in the client
     * specific packages.
     */
    public enum Field implements RequestMetricType {
        AWSErrorCode,
        AWSRequestID,
        /**
         * The specific request subtype, such as PutItemRequest, PutObjectRequest, etc.
         */
        RequestType,
        BytesProcessed,
        /**
         * Total number of milliseconds taken for a request/response including
         * the time taken to execute the request handlers, round trip to AWS,
         * and the time taken to execute the response handlers.
         */
        ClientExecuteTime,
        CredentialsRequestTime,

        Exception,
        /**
         * Used to count and preserve the throttle related exceptions.
         */
        ThrottleException,
        /**
         * Number of milliseconds taken to acquire a connection from the connection pool (or create a new connection), send the
         * request (including reading the user's input stream for streaming uploads), and receive the response status and
         * headers. This does not include the time needed to download the response payload from the AWS service.
         */
        HttpRequestTime,
        RedirectLocation,
        RequestMarshallTime,
        /**
         * Number of milliseconds taken to sign a request.
         */
        RequestSigningTime,
        /**
         * Number of milliseconds taken to execute the response handler for a response from AWS.
         */
        ResponseProcessingTime,
        /**
         * Number of requests to AWS.
         */
        RequestCount,
        /**
         * Number of retries of AWS SDK sending a request to AWS.
         */
        RetryCount, // captured via the RequestCount since (RetryCount = RequestCount - 1)
        /**
         * Snapshot of currently consumed retry capacity.
         */
        RetryCapacityConsumed,
        /**
         * Number of retries that were not attempted due to retry throttling.
         */
        ThrottledRetryCount,
        /**
         * Number of retries of the underlying http client library in sending a
         * request to AWS.
         */
        HttpClientRetryCount,
        /**
         * Number of milliseconds taken to write the request headers and payload to the underlying socket that is connected to
         * AWS. This does not include any time spent acquiring a connection from the connection pool (or creating a new
         * connection).
         */
        HttpClientSendRequestTime,
        /**
         * Number of milliseconds taken to receive the response status and headers from the underlying socket that is connected
         * to AWS. This does not include any time spent reading the response payload.
         */
        HttpClientReceiveResponseTime,

        /**
         * Number of milliseconds taken to read response payload data off of the underlying socket that is connected to AWS. This
         * does not include any time spent processing the response payload data, or time spent reading the response
         * status/headers.
         */
        HttpSocketReadTime,

        /**
         * The number of idle persistent connections.
         * <p>
         * Reference: https://hc.apache
         * .org/httpcomponents-core-ga/httpcore/apidocs/org/apache
         * /http/pool/PoolStats.html
         */
        HttpClientPoolAvailableCount,
        /**
         * The number of persistent connections tracked by the connection
         * manager currently being used to execute requests.
         * <p>
         * Reference: https://hc
         * .apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache
         * /http/pool/PoolStats.html
         */
        HttpClientPoolLeasedCount,
        /**
         * The number of connection requests being blocked awaiting a free
         * connection.
         * <p>
         * Reference: https://hc.apache.org/httpcomponents-core-ga/httpcore
         * /apidocs/org/apache/http/pool/PoolStats.html
         */
        HttpClientPoolPendingCount,
        RetryPauseTime,
        ServiceEndpoint,
        ServiceName,
        StatusCode, // The http status code
        ;
    }

    protected final TimingInfo timingInfo;

    /**
     * This constructor should be used only in the case when AWS SDK metrics
     * collector is disabled, when minimal timing info is supported for backward
     * compatibility reasons.
     *
     * @see AWSRequestMetricsFullSupport
     */
    public AWSRequestMetrics() {
        this.timingInfo = TimingInfo.startTiming();
    }

    protected AWSRequestMetrics(TimingInfo timingInfo) {
        this.timingInfo = timingInfo;
    }

    public final TimingInfo getTimingInfo() {
        return timingInfo;
    }
    /**
     * Returns true if this metrics is enabled; false otherwise.
     * Returns false by default.
     * */
    public boolean isEnabled() {
        return false;
    }

    public void startEvent(String eventName) {}
    public void startEvent(MetricType f) {}
    public void endEvent(String eventName) {}
    public void endEvent(MetricType f) {}

    public void incrementCounter(String event) {}
    public void incrementCounter(MetricType f) {}
    /** Fluent API of {@link #incrementCounter(String)} */
    public final AWSRequestMetrics incrementCounterWith(String event) {
        incrementCounter(event);
        return this;
    }
    /** Fluent API of {@link #incrementCounter(MetricType)} */
    public final AWSRequestMetrics incrementCounterWith(MetricType f) {
        incrementCounter(f);
        return this;
    }

    public void setCounter(String counterName, long count) {}
    public void setCounter(MetricType f, long count) {}
    /** Fluent API of {@link #setCounter(String, long)} */
    public final AWSRequestMetrics withCounter(String counterName, long count) {
        setCounter(counterName, count);
        return this;
    }
    /** Fluent API of {@link #setCounter(MetricType, long)} */
    public final AWSRequestMetrics withCounter(MetricType f, long count) {
        setCounter(f, count);
        return this;
    }

    public void addProperty(String propertyName, Object value) {}
    public void addProperty(MetricType f, Object value) {}
    /** Fluent API of {@link #addProperty(String, Object)} */
    public final AWSRequestMetrics addPropertyWith(String propertyName, Object value) {
        addProperty(propertyName, value);
        return this;
    }
    /** Fluent API of {@link #addProperty(MetricType, Object)} */
    public final AWSRequestMetrics addPropertyWith(MetricType f, Object value) {
        addProperty(f, value);
        return this;
    }

    public void log() {}
    public List<Object> getProperty(String propertyName){ return Collections.emptyList(); }
    public List<Object> getProperty(MetricType f) { return Collections.emptyList(); }
}
