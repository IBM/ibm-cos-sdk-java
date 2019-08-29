/*
 * Copyright 2018 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.aspera.transfer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AsperaConfigTest {
	
	@Test
	public void testConfigMBConvertsToKB() {
		AsperaConfig asperaConfig = new AsperaConfig();
		
		asperaConfig.setTargetRateMbps(1000);
		asperaConfig.setTargetRateCapMbps(500);
		asperaConfig.setMinRateCapMbps(300);
		asperaConfig.setMinRateMbps(400);
		asperaConfig.setMultiSessionThresholdMb(60);
		
		assertEquals(1000000, asperaConfig.getTargetRateKbps());
		assertEquals(500000, asperaConfig.getTargetRateCapKbps());
		assertEquals(300000, asperaConfig.getMinRateCapKbps());
		assertEquals(400000, asperaConfig.getMinRateKbps());
		assertEquals(60000000, asperaConfig.getMultiSessionThreshold());
	}
	
	@Test
	public void testConfigConstructorMBConvertsToKB() {
		AsperaConfig asperaConfig = new AsperaConfig()
				.withTargetRateMbps(1000)
				.withTargetRateCapMbps(500)
				.withMinRateCapMbps(300)
				.withMinRateMbps(400)
				.withMultiSessionThresholdMb(60);
		
		assertEquals(1000000, asperaConfig.getTargetRateKbps());
		assertEquals(500000, asperaConfig.getTargetRateCapKbps());
		assertEquals(300000, asperaConfig.getMinRateCapKbps());
		assertEquals(400000, asperaConfig.getMinRateKbps());
		assertEquals(60000000, asperaConfig.getMultiSessionThreshold());
	}
    
    @Test
    public void testConfigKBConvertsToMB() {
        AsperaConfig asperaConfig = new AsperaConfig();
        
        asperaConfig.setTargetRateKbps(1000000);
        asperaConfig.setTargetRateCapKbps(500000);
        asperaConfig.setMinRateCapKbps(300000);
        asperaConfig.setMinRateKbps(400000);
        
        assertEquals(1000, asperaConfig.getTargetRateMbps());
        assertEquals(500, asperaConfig.getTargetRateCapMbps());
        assertEquals(300, asperaConfig.getMinRateCapMbps());
        assertEquals(400, asperaConfig.getMinRateMbps());
    }
    
    @Test
    public void testConfigConstructorKBConvertsToMB() {
        AsperaConfig asperaConfig = new AsperaConfig()
                .withTargetRateKbps(1000000)
                .withTargetRateCapKbps(500000)
                .withMinRateCapKbps(300000)
                .withMinRateKbps(400000);
        
        assertEquals(1000, asperaConfig.getTargetRateMbps());
        assertEquals(500, asperaConfig.getTargetRateCapMbps());
        assertEquals(300, asperaConfig.getMinRateCapMbps());
        assertEquals(400, asperaConfig.getMinRateMbps());
    }
}
