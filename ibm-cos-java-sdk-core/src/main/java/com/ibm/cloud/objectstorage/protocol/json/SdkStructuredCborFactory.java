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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.BigDecimalCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.BigIntegerCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.BooleanCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.ByteBufferCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.ByteCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.DateCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.DoubleCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.FloatCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.IntegerCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.LongCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.ShortCborUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeCborUnmarshallers.StringCborUnmarshaller;
import com.ibm.cloud.objectstorage.util.ImmutableMapParameter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Creates generators and protocol handlers for CBOR wire format.
 */
@SdkInternalApi
class SdkStructuredCborFactory {

    private static final JsonFactory CBOR_FACTORY = new CBORFactory();

    /**
     * cbor unmarshallers for scalar types.
     */
    private static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> CBOR_SCALAR_UNMARSHALLERS =
            new ImmutableMapParameter.Builder<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>>()
            .put(String.class, StringCborUnmarshaller.getInstance())
            .put(Double.class, DoubleCborUnmarshaller.getInstance())
            .put(Integer.class, IntegerCborUnmarshaller.getInstance())
            .put(BigInteger.class, BigIntegerCborUnmarshaller.getInstance())
            .put(BigDecimal.class, BigDecimalCborUnmarshaller.getInstance())
            .put(Boolean.class, BooleanCborUnmarshaller.getInstance())
            .put(Float.class, FloatCborUnmarshaller.getInstance())
            .put(Long.class, LongCborUnmarshaller.getInstance())
            .put(Byte.class, ByteCborUnmarshaller.getInstance())
            .put(Date.class, DateCborUnmarshaller.getInstance())
            .put(ByteBuffer.class, ByteBufferCborUnmarshaller.getInstance())
            .put(Short.class, ShortCborUnmarshaller.getInstance()).build();

    public static final SdkStructuredJsonFactory SDK_CBOR_FACTORY = new SdkStructuredJsonFactoryImpl(
            CBOR_FACTORY, CBOR_SCALAR_UNMARSHALLERS,
            Collections.<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>>emptyMap()) {
        @Override
        protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory,
                                                       String contentType) {
            return new SdkCborGenerator(jsonFactory, contentType);
        }
    };

}
