/*
 * Copyright 2014-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.File;

import com.ibm.cloud.objectstorage.services.s3.model.AbortMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.CopyPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CopyPartResult;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectResult;
import com.ibm.cloud.objectstorage.services.s3.model.S3Object;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartResult;

/**
 * A Service Provider Interface that allows direct access to the underlying
 * non-encrypting S3 client of an S3 encryption client instance.
 */
public interface S3DirectSpi {
    PutObjectResult putObject(PutObjectRequest req);

    S3Object getObject(GetObjectRequest req);

    ObjectMetadata getObject(GetObjectRequest req, File dest);

    CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest req);

    InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest req);

    UploadPartResult uploadPart(UploadPartRequest req);

    CopyPartResult copyPart(CopyPartRequest req);

    void abortMultipartUpload(AbortMultipartUploadRequest req);
}
