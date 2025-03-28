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
package com.ibm.cloud.objectstorage.http;

/**
 * Responsible for handling an HTTP response and returning an object of type T.
 * For example, a typical response handler might accept a response, and
 * translate it into a concrete typed object.
 *
 * @param <T>
 *            The output of this response handler.
 */
public interface HttpResponseHandler<T> {

    /**
     * Header identifying the request. Generated by the service and returned in the HTTP response.
     */
    String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";

    /**
     * An extended request ID used primarily by S3 related services.
     */
    String X_AMZN_EXTENDED_REQUEST_ID_HEADER = "x-amz-id-2";

    /**
     * Alternative header identifying the request. Generated by S3 related services and with 503 error responses.
     */
    String X_AMZ_REQUEST_ID_ALTERNATIVE_HEADER = "x-amz-request-id";

    String X_AMZN_QUERY_ERROR = "x-amzn-query-error";

    /**
     * Accepts an HTTP response object, and returns an object of type T.
     * Individual implementations may choose to handle the response however they
     * need to, and return any type that they need to.
     *
     * @param response
     *            The HTTP response to handle, as received from an AWS service.
     *
     * @return An object of type T, as defined by individual implementations.
     *
     * @throws Exception
     *             If any problems are encountered handling the response.
     */
    T handle(HttpResponse response) throws Exception;

    /**
     * Indicates if this response handler requires that the underlying HTTP
     * connection <b>not</b> be closed automatically after the response is
     * handled.
     * <p>
     * For example, if the object returned by this response handler manually
     * manages the stream of data from the HTTP connection, and doesn't read all
     * the data from the connection in the {@link #handle(HttpResponse)} method,
     * this method can be used to prevent the underlying connection from being
     * prematurely closed.
     * <p>
     * Response handlers should use this option very carefully, since it means
     * that resource cleanup is no longer handled automatically, and if
     * neglected, can result in the client runtime running out of resources for
     * new HTTP connections.
     *
     * @return True if this response handler requires that the underlying HTTP
     *         connection be left open, and not automatically closed, otherwise
     *         false.
     */
    boolean needsConnectionLeftOpen();

}
