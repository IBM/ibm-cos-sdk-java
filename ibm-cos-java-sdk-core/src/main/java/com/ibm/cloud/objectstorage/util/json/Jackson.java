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
package com.ibm.cloud.objectstorage.util.json;

import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public enum Jackson {
    ;
    private static final InternalLogApi log = InternalLogFactory.getLog(Jackson.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    private static final ObjectWriter writer = objectMapper.writer();
    private static final ObjectWriter prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();

    private static final TypeReference<HashMap<String, String>>
        STRING_MAP_TYPEREFERENCE = new TypeReference<HashMap<String, String>>() {};

    public static String toJsonPrettyString(Object value) {
        try {
            return prettyWriter.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String toJsonString(Object value) {
        try {
            return writer.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the deserialized object from the given json string and target
     * class; or null if the given json string is null.
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new SdkClientException("Unable to parse Json String.", e);
        }
    }

    /**
     * Returns a map of strings from the given json string; or null if the given json string is null.
     */
    public static Map<String, String> stringMapFromJsonString(String json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, STRING_MAP_TYPEREFERENCE);
        } catch (IOException e) {
            throw new SdkClientException("Unable to parse Json String.", e);
        }
    }

    /**
     * Returns the deserialized object from the given json string and target
     * class; or null if the given json string is null. Clears the JSON location in the event of an error
     */
    public static <T> T fromSensitiveJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.debug("Failed to parse JSON string.", e);
            throw new SdkClientException("Unable to parse Json string. See debug-level logs for the exact error " +
                                         "details, which may include sensitive information.");
        }
    }

    public static JsonNode jsonNodeOf(String json) {
        return fromJsonString(json, JsonNode.class);
    }

    public static JsonGenerator jsonGeneratorOf(Writer writer) throws IOException {
        return new JsonFactory().createGenerator(writer);
    }

    public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectWriter getWriter() {
        return writer;
    }

    public static ObjectWriter getPrettywriter() {
        return prettyWriter;
    }
}
