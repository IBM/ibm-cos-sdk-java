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

/**
 * A {@link LifecycleFilterPredicate} class to represent the
 * prefix identifying one or more objects to which the
 * {@link com.ibm.cloud.objectstorage.services.s3.model.BucketLifecycleConfiguration.Rule} applies.
 */
public final class LifecyclePrefixPredicate extends LifecycleFilterPredicate {

    private final String prefix;

    public LifecyclePrefixPredicate(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the key prefix for which the
     * {@link com.ibm.cloud.objectstorage.services.s3.model.BucketLifecycleConfiguration.Rule} will apply.
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void accept(LifecyclePredicateVisitor lifecyclePredicateVisitor) {
        lifecyclePredicateVisitor.visit(this);
    }
}
