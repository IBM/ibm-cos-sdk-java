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
package com.ibm.cloud.objectstorage.services.s3.model.transform;

import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.services.s3.Headers;
import com.ibm.cloud.objectstorage.services.s3.internal.AbstractS3ResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.model.HeadBucketResult;
import com.ibm.cloud.objectstorage.util.StringUtils;

public class HeadBucketResultHandler extends AbstractS3ResponseHandler<HeadBucketResult> {

    @Override
    public AmazonWebServiceResponse<HeadBucketResult> handle(HttpResponse response)
            throws Exception {
        final AmazonWebServiceResponse<HeadBucketResult> awsResponse = new AmazonWebServiceResponse<HeadBucketResult>();
        final HeadBucketResult result = new HeadBucketResult();
        result.setBucketRegion(response.getHeaders().get(Headers.S3_BUCKET_REGION));

        if (!StringUtils.isNullOrEmpty(response.getHeaders().get(Headers.IBM_SSE_KP_ENABLED))){
            result.setIBMSSEKPEnabled(Boolean.parseBoolean(response.getHeaders().get(Headers.IBM_SSE_KP_ENABLED)));
        }
        if (!StringUtils.isNullOrEmpty(response.getHeaders().get(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN))){
            result.setIBMSSEKPCrk(response.getHeaders().get(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN));
        }
        awsResponse.setResult(result);

        return awsResponse;
    }
}
