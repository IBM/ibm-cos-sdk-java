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

import com.ibm.cloud.objectstorage.metrics.ServiceMetricType;

/**
 * Predefined AWS SDK non-request specific metric types general across all AWS
 * clients. Client specific predefined metrics like S3 or DynamoDB are defined
 * in the client specific packages.
 */
public enum AWSServiceMetrics implements ServiceMetricType {
    /**
     * Time taken to get a connection by the http client library.
     */
    HttpClientGetConnectionTime("HttpClient"),
    ;

    private final String serviceName;
    private AWSServiceMetrics(String serviceName) {
        this.serviceName = serviceName;
    }
    @Override public String getServiceName() { return serviceName; }
}
