/*
 * Copyright 2016-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.protocol.json;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.internal.http.CompositeErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.ErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.IonErrorCodeParser;
import com.ibm.cloud.objectstorage.internal.http.JsonErrorCodeParser;
import com.ibm.cloud.objectstorage.transform.JsonUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.BigIntegerIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.BooleanIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.ByteBufferIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.ByteIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.DateIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.DoubleIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.FloatIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.IntegerIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.LongIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.ShortIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeIonUnmarshallers.StringIonUnmarshaller;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.util.ImmutableMapParameter;
import com.fasterxml.jackson.core.JsonFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonBinaryWriterBuilder;
import com.amazon.ion.system.IonSystemBuilder;
import com.amazon.ion.system.IonTextWriterBuilder;
import com.amazon.ion.system.IonWriterBuilder;

@SdkInternalApi
class SdkStructuredIonFactory extends SdkStructuredJsonFactoryImpl {
    private static final IonSystem ION_SYSTEM = IonSystemBuilder.standard().build();
    private static final JsonFactory JSON_FACTORY = new IonFactory(ION_SYSTEM);
    private static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> UNMARSHALLERS =
            new ImmutableMapParameter.Builder<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>>()
            .put(BigDecimal.class, BigDecimalIonUnmarshaller.getInstance())
            .put(BigInteger.class, BigIntegerIonUnmarshaller.getInstance())
            .put(Boolean.class, BooleanIonUnmarshaller.getInstance())
            .put(ByteBuffer.class, ByteBufferIonUnmarshaller.getInstance())
            .put(Byte.class, ByteIonUnmarshaller.getInstance())
            .put(Date.class, DateIonUnmarshaller.getInstance())
            .put(Double.class, DoubleIonUnmarshaller.getInstance())
            .put(Float.class, FloatIonUnmarshaller.getInstance())
            .put(Integer.class, IntegerIonUnmarshaller.getInstance())
            .put(Long.class, LongIonUnmarshaller.getInstance())
            .put(Short.class, ShortIonUnmarshaller.getInstance())
            .put(String.class, StringIonUnmarshaller.getInstance())
            .build();

    private static final IonBinaryWriterBuilder BINARY_WRITER_BUILDER = IonBinaryWriterBuilder.standard().immutable();
    private static final IonTextWriterBuilder TEXT_WRITER_BUILDER = IonTextWriterBuilder.standard().immutable();

    public static final SdkStructuredIonFactory SDK_ION_BINARY_FACTORY = new SdkStructuredIonFactory(BINARY_WRITER_BUILDER);
    public static final SdkStructuredIonFactory SDK_ION_TEXT_FACTORY = new SdkStructuredIonFactory(TEXT_WRITER_BUILDER);

    private final IonWriterBuilder builder;

    private SdkStructuredIonFactory(IonWriterBuilder builder) {
        super(JSON_FACTORY, UNMARSHALLERS,
              Collections.<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>>emptyMap());
        this.builder = builder;
    }

    @Override
    protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory, String contentType) {
        return SdkIonGenerator.create(builder, contentType);
    }

    @Override
    protected ErrorCodeParser getErrorCodeParser(String customErrorCodeFieldName) {
        return new CompositeErrorCodeParser(
                new IonErrorCodeParser(ION_SYSTEM),
                new JsonErrorCodeParser(customErrorCodeFieldName));
    }
}
