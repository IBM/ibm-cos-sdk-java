/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3.model.lifecycle;

import java.util.List;

/**
 *  Abstract class representing an operator that acts on N number of predicates.
 */
abstract class LifecycleNAryOperator extends LifecycleFilterPredicate {

    private final List<LifecycleFilterPredicate> operands;

    public LifecycleNAryOperator(List<LifecycleFilterPredicate> operands) {
        this.operands = operands;
    }

    public List<LifecycleFilterPredicate> getOperands() {
        return operands;
    }
}
