/*
 *
 * Copyright (c) 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package com.ibm.cloud.objectstorage.protocol.json;

import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext.UnmarshallerType;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.BigDecimalJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.BigIntegerJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.BooleanJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.ByteBufferJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.ByteJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.CharacterJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.DateJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.DoubleJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.FloatJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.IntegerJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.JsonValueStringUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.LongJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.ShortJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.util.ImmutableMapParameter;
import com.fasterxml.jackson.core.JsonFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

/**
 * Creates generators and protocol handlers for plain text JSON wire format.
 */
@SdkProtectedApi
public class SdkStructuredPlainJsonFactory {

    /**
     * Recommended to share JsonFactory instances per http://wiki.fasterxml
     * .com/JacksonBestPracticesPerformance
     */
    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    @SdkTestInternalApi
    public static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> JSON_SCALAR_UNMARSHALLERS =
            new ImmutableMapParameter.Builder<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>>()
            .put(String.class, StringJsonUnmarshaller.getInstance())
            .put(Double.class, DoubleJsonUnmarshaller.getInstance())
            .put(Integer.class, IntegerJsonUnmarshaller.getInstance())
            .put(BigInteger.class, BigIntegerJsonUnmarshaller.getInstance())
            .put(BigDecimal.class, BigDecimalJsonUnmarshaller.getInstance())
            .put(Boolean.class, BooleanJsonUnmarshaller.getInstance())
            .put(Float.class, FloatJsonUnmarshaller.getInstance())
            .put(Long.class, LongJsonUnmarshaller.getInstance())
            .put(Byte.class, ByteJsonUnmarshaller.getInstance())
            .put(Date.class, DateJsonUnmarshaller.getInstance())
            .put(ByteBuffer.class, ByteBufferJsonUnmarshaller.getInstance())
            .put(Character.class, CharacterJsonUnmarshaller.getInstance())
            .put(Short.class, ShortJsonUnmarshaller.getInstance()).build();

    @SdkTestInternalApi
    public static final Map<UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> JSON_CUSTOM_TYPE_UNMARSHALLERS =
            new ImmutableMapParameter.Builder<UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>>()
                    .put(UnmarshallerType.JSON_VALUE, JsonValueStringUnmarshaller.getInstance())
                    .build();

    public static final SdkStructuredJsonFactory SDK_JSON_FACTORY = new SdkStructuredJsonFactoryImpl(
            JSON_FACTORY, JSON_SCALAR_UNMARSHALLERS, JSON_CUSTOM_TYPE_UNMARSHALLERS) {
        @Override
        protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory,
                                                       String contentType) {
            return new SdkJsonGenerator(jsonFactory, contentType);
        }
    };

}
