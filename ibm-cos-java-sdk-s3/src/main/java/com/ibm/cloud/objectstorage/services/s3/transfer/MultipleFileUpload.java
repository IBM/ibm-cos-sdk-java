/*
 * Copyright 2012-2024 Amazon Technologies, Inc.
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
package com.ibm.cloud.objectstorage.services.s3.transfer;

import java.util.Collection;


/**
 * Multiple file upload of an entire virtual directory.
 */
public interface  MultipleFileUpload extends Transfer {

    /**
     * Returns the key prefix of the virtual directory being uploaded.
     */
    public String getKeyPrefix();

    /**
     * Returns the name of the bucket to which files are uploaded.
     */
    public String getBucketName();

    /**
     * Returns a collection of sub transfers associated with the multi file upload.
     */
    public Collection<? extends Upload> getSubTransfers();

}
