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
package com.ibm.cloud.objectstorage.services.s3;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.client.AwsSyncClientParams;

/**
 * Parameter object for {@link AmazonS3Client}. Simple wrapper to include {@link S3ClientOptions}.
 */
@SdkInternalApi
abstract class AmazonS3ClientParams {

    public abstract AwsSyncClientParams getClientParams();

    public abstract S3ClientOptions getS3ClientOptions();
}
