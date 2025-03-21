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

package com.ibm.cloud.objectstorage.handlers;

import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.Response;

/**
 * Contextual data for the RequestHandler2 afterAttempt callback
 *
 * Under certain conditions (redirects), both response and exception may be null
 */
public final class HandlerAfterAttemptContext {

    /**
     * The request being attempted
     */
    private final Request<?> request;

    /**
     * The response returned by the request; null if the request was not successful
     */
    private final Response<?> response;

    /**
     * Exception generated while processing the request; possibly null
     */
    private final Exception exception;

    /**
     * Default constructor for the HandlerAfterAttemptContext class
     */
    private HandlerAfterAttemptContext(Request<?> request, Response<?> response, Exception exception) {
        this.request = request;
        this.response = response;
        this.exception = exception;
    }

    /**
     * @return the request being attempted
     */
    public Request<?> getRequest() {
        return request;
    }

    /**
     * @return the response to the request or null if the attempt failed
     */
    public Response<?> getResponse() {
        return response;
    }

    /**
     * @return the exception that was generated while processing the request, or null if the attempt succeeded
     */
    public Exception getException() {
        return exception;
    }


    /**
     * @return a new builder for a HandlerAfterAttemptContext instance
     */
    public static HandlerAfterAttemptContextBuilder builder() {
        return new HandlerAfterAttemptContextBuilder();
    }

    /**
     * Builder class for HandlerAfterAttemptContext
     */
    public static class HandlerAfterAttemptContextBuilder {

        /**
         * The request being attempted
         */
        private Request<?> request;

        /**
         * The response returned by the request; null if the request was not successful
         */
        private Response<?> response;

        /**
         * Exception generated while processing the request; possibly null
         */
        private Exception exception;

        /**
         * Default constructor
         */
        private HandlerAfterAttemptContextBuilder() {}

        /**
         * Fluent set for what the request should be on the eventual HandlerAfterAttemptContext instance
         * @param request the request that was attempted
         * @return the modified builder
         */
        public HandlerAfterAttemptContextBuilder withRequest(Request<?> request) {
            this.request = request;

            return this;
        }

        /**
         * Fluent set for what the response should be on the eventual HandlerAfterAttemptContext instance
         * @param response response from the request attempt, or null if there was an error
         * @return the modified builder
         */
        public HandlerAfterAttemptContextBuilder withResponse(Response<?> response) {
            this.response = response;

            return this;
        }

        /**
         * Fluent set for what the exception should be on the eventual HandlerAfterAttemptContext instance
         * @param exception exception generated by the request attempt, or null if the attempt was succcessful
         * @return the modified builder
         */
        public HandlerAfterAttemptContextBuilder withException(Exception exception) {
            this.exception = exception;

            return this;
        }

        /**
         * @return a new HandlerAfterAttemptContext object
         */
        public HandlerAfterAttemptContext build() {
            return new HandlerAfterAttemptContext(request, response, exception);
        }
    }
}
