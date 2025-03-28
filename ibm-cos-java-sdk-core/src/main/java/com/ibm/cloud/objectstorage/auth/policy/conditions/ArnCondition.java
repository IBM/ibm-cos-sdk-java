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
package com.ibm.cloud.objectstorage.auth.policy.conditions;

import java.util.Arrays;

import com.ibm.cloud.objectstorage.auth.policy.Condition;

/**
 * AWS access control policy condition that allows an access control statement
 * to be conditionally applied based on the comparison of an Amazon Resource
 * Name (ARN).
 * <p>
 * An Amazon Resource Name (ARN) takes the following format:
 * <b>arn:aws:&lt;vendor>:&lt;region>:&lt;namespace>:&lt;relative-id></b>
 * <p>
 * <ul>
 * <li>vendor identifies the AWS product (e.g., sns)</li>
 * <li>region is the AWS Region the resource resides in (e.g., us-east-1), if
 * any
 * <li>namespace is the AWS account ID with no hyphens (e.g., 123456789012)
 * <li>relative-id is the service specific portion that identifies the specific
 * resource
 * </ul>
 * <p>
 * For example, an Amazon SQS queue might be addressed with the following ARN:
 * <b>arn:aws:sqs:us-east-1:987654321000:MyQueue</b>
 * <p>
 * <p>
 * Currently the only valid condition key to use in an ARN condition is
 * {@link ConditionFactory#SOURCE_ARN_CONDITION_KEY}, which indicates the
 * source resource that is modifying another resource, for example, an SNS topic
 * is the source ARN when publishing messages from the topic to an SQS queue.
 */
public class ArnCondition extends Condition {

    /**
     * <p>
     * Enumeration of the supported ways an ARN comparison can be evaluated.
     * </p>
     * <p>
     * For additional information about ARN condition operators, see <a href=
     * "https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition_operators.html#Conditions_ARN"
     * >Amazon Resource Name (ARN) Condition Operators</a> in the <i>AWS
     * Identity and Access Management User Guide</i>.
     * </p>
     */
    public static enum ArnComparisonType {
        /** Case-sensitive matching of the ARN. Each of the six colon-delimited
         * components of the ARN is checked separately and each can include a
         * multi-character match wildcard (*) or a single-character match
         * wildcard (?). Behaves identically to {@link #ArnLike}.
         */
        ArnEquals,

        /** Case-sensitive matching of the ARN. Each of the six colon-delimited
         * components of the ARN is checked separately and each can include a
         * multi-character match wildcard (*) or a single-character match
         * wildcard (?). Behaves identically to {@link #ArnEquals}.
         */
        ArnLike,

        /** Negated form of {@link #ArnEquals} */
        ArnNotEquals,

        /** Negated form of {@link #ArnLike} */
        ArnNotLike;
    };

    /**
     * Constructs a new access control policy condition that compares ARNs
     * (Amazon Resource Names).
     *
     * @param type
     *            The type of comparison to perform.
     * @param key
     *            The access policy condition key specifying where to get the
     *            first ARN for the comparison (ex:
     *            {@link ConditionFactory#SOURCE_ARN_CONDITION_KEY}).
     * @param value
     *            The second ARN to compare against. When using
     *            {@link ArnComparisonType#ArnLike} or
     *            {@link ArnComparisonType#ArnNotLike} this may contain the
     *            multi-character wildcard (*) or the single-character wildcard
     *            (?).
     */
    public ArnCondition(ArnComparisonType type, String key, String value) {
        super.type = type.toString();
        super.conditionKey = key;
        super.values = Arrays.asList(new String[] {value});
    }

}
