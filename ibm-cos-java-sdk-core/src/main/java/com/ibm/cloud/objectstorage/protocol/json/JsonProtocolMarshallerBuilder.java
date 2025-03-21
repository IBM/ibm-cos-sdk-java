/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.protocol.MarshallLocation;
import com.ibm.cloud.objectstorage.protocol.MarshallingType;
import com.ibm.cloud.objectstorage.protocol.OperationInfo;
import com.ibm.cloud.objectstorage.protocol.ProtocolMarshaller;
import com.ibm.cloud.objectstorage.protocol.ProtocolRequestMarshaller;
import com.ibm.cloud.objectstorage.protocol.json.internal.EmptyBodyJsonMarshaller;
import com.ibm.cloud.objectstorage.protocol.json.internal.JsonProtocolMarshaller;
import com.ibm.cloud.objectstorage.protocol.json.internal.MarshallerRegistry;
import com.ibm.cloud.objectstorage.protocol.json.internal.SimpleTypeJsonMarshallers;

/**
 * Builder to create an appropriate implementation of {@link ProtocolMarshaller} for JSON based services.
 *
 * @param <T> Type of the original request object.
 */
@SdkProtectedApi
public class JsonProtocolMarshallerBuilder<T> {

    private StructuredJsonGenerator jsonGenerator;
    private String contentType;
    private OperationInfo operationInfo;
    private T originalRequest;
    private MarshallerRegistry.Builder marshallerRegistry;
    private EmptyBodyJsonMarshaller emptyBodyMarshaller;

    public static <T> JsonProtocolMarshallerBuilder<T> standard() {
        return new JsonProtocolMarshallerBuilder<T>();
    }

    public JsonProtocolMarshallerBuilder<T> jsonGenerator(StructuredJsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
        return this;
    }

    public JsonProtocolMarshallerBuilder<T> contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public JsonProtocolMarshallerBuilder<T> operationInfo(OperationInfo operationInfo) {
        this.operationInfo = operationInfo;
        return this;
    }

    public JsonProtocolMarshallerBuilder<T> originalRequest(T originalRequest) {
        this.originalRequest = originalRequest;
        return this;
    }

    /**
     * Has been used to direct whether an explicit JSON null should be sent as the body when the payload member is null,
     * but now does nothing. Deprecated in favor of directly supplying a marshaller for empty bodies.
     *
     * @see #emptyBodyMarshaller(EmptyBodyJsonMarshaller)
     */
    @Deprecated
    public JsonProtocolMarshallerBuilder<T> sendExplicitNullForPayload(boolean sendExplicitNullForPayload) {
        return this;
    }

    /**
     * Sets the marshaller to use when a request contains an explicit member but that member is null.
     *
     * @param emptyBodyMarshaller An empty body marshaller
     * @return This builder for method chaining
     */
    public JsonProtocolMarshallerBuilder<T> emptyBodyMarshaller(EmptyBodyJsonMarshaller emptyBodyMarshaller) {
        this.emptyBodyMarshaller = emptyBodyMarshaller;
        return this;
    }

    /**
     * Registers an override for the marshaller registry.
     *
     * @param marshallLocation Location to override marshaller for.
     * @param marshallingType  Type to override marshaller for.
     * @param marshaller       Marshaller to use for the given location and type.
     * @param <MarshallT>      Type of thing being marshalled.
     * @return This builder for method chaining.
     */
    public <MarshallT> JsonProtocolMarshallerBuilder<T> marshallerOverride(MarshallLocation marshallLocation,
                                                                           MarshallingType<MarshallT> marshallingType,
                                                                           StructuredJsonMarshaller<MarshallT> marshaller) {
        if (marshallerRegistry == null) {
            this.marshallerRegistry = MarshallerRegistry.builder();
        }
        marshallerRegistry.addMarshaller(marshallLocation, marshallingType, SimpleTypeJsonMarshallers.adapt(marshaller));
        return this;
    }

    public ProtocolRequestMarshaller<T> build() {
        return new JsonProtocolMarshaller<T>(jsonGenerator,
                                             contentType,
                                             operationInfo,
                                             originalRequest,
                                             marshallerRegistry,
                                             emptyBodyMarshaller);
    }

}
