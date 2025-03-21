/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.regions;

import static com.ibm.cloud.objectstorage.SDKGlobalConfiguration.REGIONS_FILE_OVERRIDE_SYSTEM_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RegionUtils class.
 */
public class RegionUtilsTest {

    @Before
    @After
    public void clearProperties() {
        System.clearProperty(REGIONS_FILE_OVERRIDE_SYSTEM_PROPERTY);
        RegionUtils.initialize();
    }

    /**
     * Tests that region file override could be properly loaded, and the
     * endpoint verification is also disabled so that invalid (not owned by AWS)
     * endpoints don't trigger RuntimeException.
     */
    @Test
    public void testRegionFileOverride() {
        String fakeRegionFilePath = RegionUtilsTest.class.getResource("fake-regions.xml").getPath();
        System.setProperty(REGIONS_FILE_OVERRIDE_SYSTEM_PROPERTY, fakeRegionFilePath);

        RegionUtils.initialize();
        assertEquals(2, RegionUtils.getRegions().size());

        assertEquals("hostname.com", RegionUtils.getRegion("us-east-1").getDomain());
        assertEquals("fake.hostname.com", RegionUtils.getRegion("us-east-1").getServiceEndpoint("cloudformation"));

        assertEquals("amazonaws.com", RegionUtils.getRegion("us-west-1").getDomain());
    }

    @Test
    public void testGetRegionUrlEncoding() {
        RegionUtils.initialize();

        assertEquals("http%3A%2F%2Fmy-host.com%2F%3F", RegionUtils.getRegion("http://my-host.com/?").getName());
    }

    @Test
    public void testNullRegionName() {
        RegionUtils.initialize();

        assertNull(RegionUtils.getRegion(null));
    }
}
