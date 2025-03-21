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

package com.ibm.cloud.objectstorage.retry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RetryModeTest {

    @Test
    public void fromName_mixedSpace_shouldReturnEnum() {
        assertEquals(RetryMode.LEGACY, RetryMode.fromName("legacy"));
        assertEquals(RetryMode.LEGACY, RetryMode.fromName("leGaCy"));
        assertEquals(RetryMode.STANDARD, RetryMode.fromName("standard"));
        assertEquals(RetryMode.STANDARD, RetryMode.fromName("sTandArd"));
        assertEquals(RetryMode.ADAPTIVE, RetryMode.fromName("adaptive"));
        assertEquals(RetryMode.ADAPTIVE, RetryMode.fromName("aDaPtIvE"));
    }

    @Test
    public void fromName_invalidValue_shouldReturnNull() {
        assertNull(RetryMode.fromName("invalid"));
        assertNull(RetryMode.fromName(null));
    }
}
