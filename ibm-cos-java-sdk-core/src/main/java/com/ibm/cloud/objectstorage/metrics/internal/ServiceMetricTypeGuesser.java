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
package com.ibm.cloud.objectstorage.metrics.internal;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.metrics.AwsSdkMetrics;
import com.ibm.cloud.objectstorage.metrics.ServiceMetricType;
import com.ibm.cloud.objectstorage.metrics.SimpleThroughputMetricType;
import com.ibm.cloud.objectstorage.metrics.ThroughputMetricType;

/**
 * An internal helper factory for generating service specific {@link ServiceMetricType}
 * without causing compile time dependency on the service specific artifacts.
 * 
 * There exists a S3ServiceMetricTest.java unit test in the S3 client library
 * that ensures this class behaves consistently with the service metric enum
 * defined in the S3 client library.
 */
public enum ServiceMetricTypeGuesser {
    ;
    /**
     * Returned the best-guessed throughput metric type for the given request,
     * or null if there is none or if metric is disabled.
     */
    public static ThroughputMetricType guessThroughputMetricType(
            final Request<?> req,
            final String metricNameSuffix,
            final String byteCountMetricNameSuffix)
    {
        if (!AwsSdkMetrics.isMetricsEnabled())
            return null;    // metric disabled
        Object orig = req.getOriginalRequestObject();
        if (orig.getClass().getName().startsWith("com.ibm.cloud.objectstorage.services.s3")) {
            return new SimpleThroughputMetricType(
                "S3" + metricNameSuffix,
                req.getServiceName(),
                "S3" + byteCountMetricNameSuffix);
        }
        return null;
    }
}
