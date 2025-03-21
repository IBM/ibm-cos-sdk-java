/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.DefaultAWSCredentialsProviderChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Amazon Web Services credentials provider chain for Amazon S3 that looks for credentials in
 * the {@link DefaultAWSCredentialsProviderChain}. If the {@link DefaultAWSCredentialsProviderChain}
 * returns null, S3 falls back to anonymous access.
 */
class S3CredentialsProviderChain extends DefaultAWSCredentialsProviderChain {

    private static Log LOG = LogFactory.getLog(S3CredentialsProviderChain.class);

    @Override
    public AWSCredentials getCredentials() {
        try {
            return super.getCredentials();
        } catch (AmazonClientException ace) {

        }

        LOG.debug("No credentials available; falling back to anonymous access");
        return null;
    }
}
