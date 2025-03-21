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
package com.ibm.cloud.objectstorage.http;

import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.AmazonServiceException.ErrorType;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.internal.http.ErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.JsonErrorMessageParser;
import com.ibm.cloud.objectstorage.protocol.json.JsonContent;
import com.ibm.cloud.objectstorage.transform.EnhancedJsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonErrorUnmarshaller;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContextImpl;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.util.CollectionUtils;
import com.ibm.cloud.objectstorage.util.StringUtils;
import com.fasterxml.jackson.core.JsonFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SdkInternalApi
public class JsonErrorResponseHandler implements HttpResponseHandler<AmazonServiceException> {

    private static final Log LOG = LogFactory.getLog(JsonErrorResponseHandler.class);

    private final List<JsonErrorUnmarshaller> unmarshallers;
    private final ErrorCodeParser errorCodeParser;
    private final JsonErrorMessageParser errorMessageParser;
    private final JsonFactory jsonFactory;

    private static final String QUERY_ERROR_DELIMITER = ";";

    private final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers;
    private final Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeUnmarshallers;

    private boolean hasAwsQueryCompatible;

    public JsonErrorResponseHandler(
            List<JsonErrorUnmarshaller> errorUnmarshallers,
            ErrorCodeParser errorCodeParser,
            JsonErrorMessageParser errorMessageParser,
            JsonFactory jsonFactory) {
        this(errorUnmarshallers, errorCodeParser, false, errorMessageParser, jsonFactory);
    }

    public JsonErrorResponseHandler(
            List<JsonErrorUnmarshaller> errorUnmarshallers,
            ErrorCodeParser errorCodeParser,
            boolean hasAwsQueryCompatible,
            JsonErrorMessageParser errorMessageParser,
            JsonFactory jsonFactory) {
        this.unmarshallers = errorUnmarshallers;
        this.simpleTypeUnmarshallers = null;
        this.customTypeUnmarshallers = null;
        this.errorCodeParser = errorCodeParser;
        this.hasAwsQueryCompatible = hasAwsQueryCompatible;
        this.errorMessageParser = errorMessageParser;
        this.jsonFactory = jsonFactory;
    }

    public JsonErrorResponseHandler(
            List<JsonErrorUnmarshaller> errorUnmarshallers,
            Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers,
            Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeUnmarshallers,
            ErrorCodeParser errorCodeParser,
            JsonErrorMessageParser errorMessageParser,
            JsonFactory jsonFactory) {
        this(errorUnmarshallers, simpleTypeUnmarshallers, customTypeUnmarshallers, errorCodeParser,
                false, errorMessageParser, jsonFactory);
    }

    public JsonErrorResponseHandler(
            List<JsonErrorUnmarshaller> errorUnmarshallers,
            Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers,
            Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeUnmarshallers,
            ErrorCodeParser errorCodeParser,
            boolean hasAwsQueryCompatible,
            JsonErrorMessageParser errorMessageParser,
            JsonFactory jsonFactory) {
        this.unmarshallers = errorUnmarshallers;
        this.simpleTypeUnmarshallers = simpleTypeUnmarshallers;
        this.customTypeUnmarshallers = customTypeUnmarshallers;
        this.errorCodeParser = errorCodeParser;
        this.hasAwsQueryCompatible = hasAwsQueryCompatible;
        this.errorMessageParser = errorMessageParser;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return false;
    }

    @Override
    public AmazonServiceException handle(HttpResponse response) throws Exception {
        JsonContent jsonContent = JsonContent.createJsonContent(response, jsonFactory);

        byte[] rawContent = jsonContent.getRawContent();

        String errorCode = errorCodeParser.parseErrorCode(response, jsonContent);
        AmazonServiceException ase = createException(errorCode, response, jsonContent.getJsonNode(), rawContent);

        // The marshallers instantiate the exception without providing a
        // message. If the Exception included a message member find it and
        // add it here.
        if (ase.getErrorMessage() == null) {
            ase.setErrorMessage(errorMessageParser.parseErrorMessage(response, jsonContent.getJsonNode()));
        }

        ase.setErrorCode(getEffectiveErrorCode(response, errorCode));
        ase.setServiceName(response.getRequest().getServiceName());
        ase.setStatusCode(response.getStatusCode());
        ase.setErrorType(getErrorTypeFromStatusCode(response.getStatusCode()));
        ase.setRawResponse(rawContent);
        String requestId = getRequestIdFromHeaders(response.getHeaders());
        if (requestId != null) {
            ase.setRequestId(requestId);
        }
        ase.setHttpHeaders(response.getHeaders());
        return ase;
    }

    /**
     * Create an AmazonServiceException using the chain of unmarshallers. This method will never
     * return null, it will always return a valid AmazonServiceException
     *
     * @param errorCode
     *            Error code to find an appropriate unmarshaller
     * @param response
     *            The HTTP response
     * @param jsonNode
     *            JsonNode of HTTP response
     * @param rawContent
     *            The raw bytes of the HTTP response content
     * @return AmazonServiceException
     */
    private AmazonServiceException createException(String errorCode, HttpResponse response, JsonNode jsonNode, byte[] rawContent) {
        AmazonServiceException ase = unmarshallException(errorCode, response, jsonNode, rawContent);
        if (ase == null) {
            ase = new AmazonServiceException(
                    "Unable to unmarshall exception response with the unmarshallers provided");
        }
        return ase;
    }

    private AmazonServiceException unmarshallException(String errorCode, HttpResponse response, JsonNode jsonNode, byte[] rawContent) {
        for (JsonErrorUnmarshaller unmarshaller : unmarshallers) {
            if (unmarshaller.matchErrorCode(errorCode)) {
                try {
                    if (unmarshaller instanceof EnhancedJsonErrorUnmarshaller) {
                        EnhancedJsonErrorUnmarshaller enhancedUnmarshaller = (EnhancedJsonErrorUnmarshaller) unmarshaller;
                        return doEnhancedUnmarshall(enhancedUnmarshaller, errorCode, response, rawContent);
                    } else {
                        return doLegacyUnmarshall(unmarshaller, jsonNode);
                    }
                } catch (Exception e) {
                    LOG.debug("Unable to unmarshall exception content", e);
                    return null;
                }
            }
        }
        return null;
    }

    private AmazonServiceException doEnhancedUnmarshall(EnhancedJsonErrorUnmarshaller unmarshaller,
                                                        String errorCode,
                                                        HttpResponse response,
                                                        byte[] rawContent) throws Exception {
        if (rawContent == null) {
            rawContent = new byte[0];
        }

        JsonParser jsonParser = jsonFactory.createParser(rawContent);
        JsonUnmarshallerContext unmarshallerContext = new JsonUnmarshallerContextImpl(
                jsonParser, simpleTypeUnmarshallers, customTypeUnmarshallers, response);
        try {
            return unmarshaller.unmarshallFromContext(unmarshallerContext);
        } catch (JsonParseException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Received response with error code '%s', but response body did not contain " +
                                        "valid JSON. Treating it as an empty object.", errorCode), e);
            }
            // This is to keep consistent with the previous behavior
            JsonParser emptyParser = jsonFactory.createParser("{}");
            unmarshallerContext = new JsonUnmarshallerContextImpl(
                    emptyParser, simpleTypeUnmarshallers, customTypeUnmarshallers, response);
            return unmarshaller.unmarshallFromContext(unmarshallerContext);
        }
    }

    private AmazonServiceException doLegacyUnmarshall(JsonErrorUnmarshaller unmarshaller, JsonNode jsonNode) throws Exception {
        return unmarshaller.unmarshall(jsonNode);
    }

    private ErrorType getErrorTypeFromStatusCode(int statusCode) {
        return statusCode < 500 ? ErrorType.Client : ErrorType.Service;
    }

    private String getRequestIdFromHeaders(Map<String, String> headers) {
        for (Entry<String, String> headerEntry : headers.entrySet()) {
            if (headerEntry.getKey().equalsIgnoreCase(X_AMZN_REQUEST_ID_HEADER)) {
                return headerEntry.getValue();
            }
            if (headerEntry.getKey().equalsIgnoreCase(X_AMZ_REQUEST_ID_ALTERNATIVE_HEADER)) {
                return headerEntry.getValue();
            }
        }
        return null;
    }

    private String getEffectiveErrorCode(HttpResponse response, String errorCode) {
        if (this.hasAwsQueryCompatible) {
            String compatibleErrorCode = queryCompatibleErrorCodeFromResponse(response);
            if (!StringUtils.isNullOrEmpty(compatibleErrorCode)) {
                return compatibleErrorCode;
            }
        }
        return errorCode;
    }

    private String queryCompatibleErrorCodeFromResponse(HttpResponse response) {
        List<String> headerValues = response.getHeaderValues(X_AMZN_QUERY_ERROR);
        if (!CollectionUtils.isNullOrEmpty(headerValues)) {
            String queryHeaderValue = headerValues.get(0);
            if (!StringUtils.isNullOrEmpty(queryHeaderValue)) {
                return parseQueryErrorCodeFromDelimiter(queryHeaderValue);
            }
        }
        return null;
    }

    private String parseQueryErrorCodeFromDelimiter(String queryHeaderValue) {
        int delimiter = queryHeaderValue.indexOf(QUERY_ERROR_DELIMITER);
        if (delimiter > 0) {
            return queryHeaderValue.substring(0, delimiter);
        }
        return null;
    }
}
