/*
 * Copyright 2011-2024 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.s3.internal;

import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.services.s3.Headers;


/**
 * Base request handler for responses that include a server-side encryption
 * header
 */
public class ServerSideEncryptionHeaderHandler <T extends ServerSideEncryptionResult> implements HeaderHandler<T>{

    /* (non-Javadoc)
     * @see com.ibm.cloud.objectstorage.services.s3.internal.HeaderHandler#handle(java.lang.Object, com.ibm.cloud.objectstorage.http.HttpResponse)
     */
    @Override
    public void handle(T result, HttpResponse response) {
        result.setSSEAlgorithm(response.getHeaders().get(Headers.SERVER_SIDE_ENCRYPTION));
        result.setSSECustomerAlgorithm(response.getHeaders().get(Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM));
        result.setSSECustomerKeyMd5(response.getHeaders().get(Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5));
//IBM does not support SSE-KMS
//        String bucketKeyEnabled = response.getHeaders().get(Headers.SERVER_SIDE_ENCRYPTION_BUCKET_KEY_ENABLED);
//        if (bucketKeyEnabled != null) {
//            result.setBucketKeyEnabled("true".equals(bucketKeyEnabled));
//        }
    }
}
