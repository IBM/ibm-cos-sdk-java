/*
 * Copyright 2010-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.ResponseMetadata;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.http.HttpResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.Headers;
import com.ibm.cloud.objectstorage.services.s3.S3ResponseMetadata;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.util.DateUtils;
import com.ibm.cloud.objectstorage.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract HTTP response handler for Amazon S3 responses. Provides common
 * utilities that other specialized S3 response handlers need to share such as
 * pulling common response metadata (ex: request IDs) out of headers.
 *
 * @param <T>
 *            The output type resulting from handling a response.
 */
public abstract class AbstractS3ResponseHandler<T>
        implements HttpResponseHandler<AmazonWebServiceResponse<T>> {

    /** Shared logger */
    private static final Log log = LogFactory.getLog(S3MetadataResponseHandler.class);

    /** The set of response headers that aren't part of the object's metadata */
    private static final Set<String> ignoredHeaders;

    static {
        ignoredHeaders = new HashSet<String>();
        ignoredHeaders.add(Headers.DATE);
        ignoredHeaders.add(Headers.SERVER);
        ignoredHeaders.add(Headers.REQUEST_ID);
        ignoredHeaders.add(Headers.EXTENDED_REQUEST_ID);
        ignoredHeaders.add(Headers.CLOUD_FRONT_ID);
        ignoredHeaders.add(Headers.CONNECTION);
    }

    /**
     * The majority of S3 response handlers read the complete response while
     * handling it, and don't need to manually manage the underlying HTTP
     * connection.
     *
     * @see com.ibm.cloud.objectstorage.http.HttpResponseHandler#needsConnectionLeftOpen()
     */
    public boolean needsConnectionLeftOpen() {
        return false;
    }

    /**
     * Parses the S3 response metadata (ex: Amazon Web Services request ID) from the specified
     * response, and returns a AmazonWebServiceResponse<T> object ready for the
     * result to be plugged in.
     *
     * @param response
     *            The response containing the response metadata to pull out.
     *
     * @return A new, populated AmazonWebServiceResponse<T> object, ready for
     *         the result to be plugged in.
     */
    protected AmazonWebServiceResponse<T> parseResponseMetadata(HttpResponse response) {
        AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
        String awsRequestId = response.getHeaders().get(Headers.REQUEST_ID);
        String hostId = response.getHeaders().get(Headers.EXTENDED_REQUEST_ID);
        String cloudFrontId = response.getHeaders().get(Headers.CLOUD_FRONT_ID);
        String IbmProtectionManagementState = response.getHeaders().get(Headers.IBM_PROTECTION_MANAGEMENT_STATE);

        Map<String, String> metadataMap = new HashMap<String, String>();
        metadataMap.put(ResponseMetadata.AWS_REQUEST_ID, awsRequestId);
        metadataMap.put(S3ResponseMetadata.HOST_ID, hostId);
        metadataMap.put(S3ResponseMetadata.CLOUD_FRONT_ID, cloudFrontId);
        metadataMap.put(ResponseMetadata.IBM_PROTECTION_MANAGEMENT_STATE, IbmProtectionManagementState);
        awsResponse.setResponseMetadata(new S3ResponseMetadata(metadataMap));

        return awsResponse;
    }

    /**
     * Populates the specified S3ObjectMetadata object with all object metadata
     * pulled from the headers in the specified response.
     *
     * @param response
     *            The HTTP response containing the object metadata within the
     *            headers.
     * @param metadata
     *            The metadata object to populate from the response's headers.
     */
    protected void populateObjectMetadata(HttpResponse response, ObjectMetadata metadata) {
        for (Entry<String, String> header : response.getHeaders().entrySet()) {
            String key = header.getKey();
            if (StringUtils.beginsWithIgnoreCase(key, Headers.S3_USER_METADATA_PREFIX)) {
                key = key.substring(Headers.S3_USER_METADATA_PREFIX.length());
                metadata.addUserMetadata(key, header.getValue());
            } else if (ignoredHeaders.contains(key)) {
                // ignore...
            } else if (key.equalsIgnoreCase(Headers.LAST_MODIFIED)) {
                try {
                    metadata.setHeader(key, ServiceUtils.parseRfc822Date(header.getValue()));
                } catch (Exception pe) {
                    log.warn("Unable to parse last modified date: " + header.getValue(), pe);
                }
            } else if (key.equalsIgnoreCase(Headers.CONTENT_LENGTH)) {
                try {
                    metadata.setHeader(key, Long.parseLong(header.getValue()));
                } catch (NumberFormatException nfe) {
                    throw new SdkClientException(
                            "Unable to parse content length. Header 'Content-Length' has corrupted data" + nfe.getMessage(), nfe);
                }
            } else if (key.equalsIgnoreCase(Headers.ETAG)) {
                metadata.setHeader(key, ServiceUtils.removeQuotes(header.getValue()));
            } else if (key.equalsIgnoreCase(Headers.EXPIRES)) {
                // Set the raw header then try to parse it as a date
                metadata.setHeader(Headers.EXPIRES, header.getValue());
                try {
                    metadata.setHttpExpiresDate(DateUtils.parseRFC822Date(header.getValue()));
                } catch (Exception pe) {
                    log.warn("Unable to parse http expiration date: " + header.getValue(), pe);
                }
            } else if (key.equalsIgnoreCase(Headers.EXPIRATION)) {
                new ObjectExpirationHeaderHandler<ObjectMetadata>().handle(metadata, response);
                metadata.setHeader(key, header.getValue());
            } else if (key.equalsIgnoreCase(Headers.RESTORE)) {
                new ObjectRestoreHeaderHandler<ObjectRestoreResult>().handle(metadata, response);
            } else if (key.equalsIgnoreCase(Headers.IBM_TRANSITION)) {
                new ObjectTransitionHeaderHandler<ObjectTransitionResult>().handle(metadata, response);
                metadata.setHeader(key, header.getValue());
            } else if (key.equalsIgnoreCase(Headers.REQUESTER_CHARGED_HEADER)) {
                new S3RequesterChargedHeaderHandler<S3RequesterChargedResult>().handle(metadata, response);
            } 
//IBM does not support SSE-KMS            
//            else if (key.equalsIgnoreCase(Headers.SERVER_SIDE_ENCRYPTION_BUCKET_KEY_ENABLED)) {
//                    metadata.setBucketKeyEnabled("true".equals(header.getValue()));
//                } 
            else if (key.equalsIgnoreCase(Headers.S3_PARTS_COUNT)) {
                try {
                    metadata.setHeader(key, Integer.parseInt(header.getValue()));
                } catch (NumberFormatException nfe) {
                    throw new SdkClientException(
                            "Unable to parse part count. Header x-amz-mp-parts-count has corrupted data" + nfe.getMessage(), nfe);
                }
            } else if (key.equalsIgnoreCase(Headers.RETENTION_EXPIRATION_DATE)) {
                try {
                    metadata.setRetentionExpirationDate(ServiceUtils.parseRfc822Date(header.getValue()));
                } catch (Exception pe) {
                    log.warn("Unable to parse retention expiration date: " + header.getValue(), pe);
                }
            } else if (key.equalsIgnoreCase(Headers.RETENTION_LEGAL_HOLD_COUNT)) {
                try {
                    metadata.setRetentionLegalHoldCount(Integer.parseInt(header.getValue()));
                } catch (NumberFormatException nfe) {
                    log.warn("Unable to parse legal hold count: " + header.getValue(), nfe);
                }
            } else if (key.equalsIgnoreCase(Headers.RETENTION_PERIOD)) {
                try {
                    metadata.setRetentionPeriod(Long.parseLong(header.getValue()));
                } catch (NumberFormatException nfe) {
                    log.warn("Unable to parse retention period: " + header.getValue(), nfe);
                }
            } else {
                metadata.setHeader(key, header.getValue());
            }
        }
    }

}
