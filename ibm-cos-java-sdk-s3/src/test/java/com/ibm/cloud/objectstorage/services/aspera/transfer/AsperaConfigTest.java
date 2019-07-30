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
