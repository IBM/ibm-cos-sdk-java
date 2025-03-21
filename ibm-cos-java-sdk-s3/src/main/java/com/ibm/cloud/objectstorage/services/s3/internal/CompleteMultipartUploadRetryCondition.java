/*
 * Copyright 2015-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3.internal;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.internal.SdkPredicate;
import com.ibm.cloud.objectstorage.retry.RetryPolicy;
import com.ibm.cloud.objectstorage.services.s3.model.AmazonS3Exception;
import com.ibm.cloud.objectstorage.util.ValidationUtils;

public class CompleteMultipartUploadRetryCondition implements RetryPolicy.RetryCondition {

    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final SdkPredicate<AmazonS3Exception>
            completeMultipartRetryablePredicate;

    private final int maxCompleteMultipartUploadRetries;

    public CompleteMultipartUploadRetryCondition() {
        this(new CompleteMultipartUploadRetryablePredicate(), MAX_RETRY_ATTEMPTS);
    }

    /**
     * For testing purposes.
     */
    @SdkTestInternalApi
    CompleteMultipartUploadRetryCondition(SdkPredicate<AmazonS3Exception>
                                                  predicate, int maxRetryAttempts) {
        ValidationUtils.assertNotNull(predicate, "sdk predicate");
        this.completeMultipartRetryablePredicate = predicate;
        this.maxCompleteMultipartUploadRetries = maxRetryAttempts;
    }

    @Override
    public boolean shouldRetry(AmazonWebServiceRequest originalRequest,
                               AmazonClientException exception, int retriesAttempted) {

        if (exception instanceof AmazonS3Exception) {
            return completeMultipartRetryablePredicate.test((AmazonS3Exception) exception)
                    && retriesAttempted < maxCompleteMultipartUploadRetries;
        }
        return false;
    }
}
