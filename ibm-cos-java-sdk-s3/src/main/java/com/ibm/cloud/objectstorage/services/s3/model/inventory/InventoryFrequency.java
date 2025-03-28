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
package com.ibm.cloud.objectstorage.services.s3.model.inventory;

/**
 *  The frequency for producing inventory results.
 */
public enum InventoryFrequency {

    Daily("Daily"),

    Weekly("Weekly"),

    ;

    private final String frequency;

    private InventoryFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return frequency;
    }
}
