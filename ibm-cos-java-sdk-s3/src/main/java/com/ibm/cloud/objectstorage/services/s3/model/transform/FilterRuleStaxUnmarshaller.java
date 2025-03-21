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
package com.ibm.cloud.objectstorage.services.s3.model.transform;

import javax.xml.stream.events.XMLEvent;

import com.ibm.cloud.objectstorage.services.s3.model.FilterRule;
import com.ibm.cloud.objectstorage.transform.StaxUnmarshallerContext;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.transform.SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller;

class FilterRuleStaxUnmarshaller implements Unmarshaller<FilterRule, StaxUnmarshallerContext> {

    private static final FilterRuleStaxUnmarshaller instance = new FilterRuleStaxUnmarshaller();

    public static FilterRuleStaxUnmarshaller getInstance() {
        return instance;
    }

    private FilterRuleStaxUnmarshaller() {
    }

    @Override
    public FilterRule unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;

        if (context.isStartOfDocument()) {
            targetDepth += 1;
        }

        FilterRule filter = new FilterRule();

        while (true) {
            XMLEvent xmlEvent = context.nextEvent();
            if (xmlEvent.isEndDocument()) {
                return filter;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("Name", targetDepth)) {
                    filter.setName(StringStaxUnmarshaller.getInstance().unmarshall(context));
                } else if (context.testExpression("Value", targetDepth)) {
                    filter.setValue(StringStaxUnmarshaller.getInstance().unmarshall(context));
                }
            } else if (xmlEvent.isEndElement()) {
                if (context.getCurrentDepth() < originalDepth) {
                    return filter;
                }
            }
        }

    }
}
