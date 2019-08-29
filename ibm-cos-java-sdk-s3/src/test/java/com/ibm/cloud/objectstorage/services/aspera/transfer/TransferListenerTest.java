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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.aspera.faspmanager2.ITransferListener;
import com.ibm.aspera.faspmanager2.faspmanager2;

import org.apache.commons.logging.Log;

/**
 * Most of these tests are using the TransferListener() constructor instead of getInstance(). 
 * This is so the ascpCount is always started at zero for testing. 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class, ITransferListener.class })
public class TransferListenerTest {

	@Mock
    protected Log mockLog = mock(Log.class);
	private final int ASCP_COUNT = 10;
	
	final String msgInit = "FASPMGR 2\n"
					+"Type: INIT\n"
					+"Direction: Send\n"
					+"Operation: Transfer\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgInit1 = "FASPMGR 2\n"
			+"Type: INIT\n"
			+"Direction: Send\n"
			+"Operation: Transfer\n"
			+"SessionId: 7d564ec6-7765-49d7-b2a7-9380dec7a69f\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgInit2 = "FASPMGR 2\n"
			+"Type: INIT\n"
			+"Direction: Send\n"
			+"Operation: Transfer\n"
			+"SessionId: f4c34a23-41f5-4fee-bda0-f014a4a2c92b\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";

	final String msgSession ="FASPMGR 2\n"
					+"Type: SESSION\n"
					+"Adaptive: Adaptive\n"
					+"Cipher: aes-128\n"
					+"ClientUser: admin\n"
					+"ClusterNodeId: 0\n"
					+"ClusterNumNodes: 1\n"
					+"CreatePolicy: 0\n"
					+"DSPipelineDepth: 32\n"
					+"DatagramSize: 0\n"
					+"Direction: Send\n"
					+"Encryption: Yes\n";

	final String msgError = "FASPMGR 2\n"
					+"Type: ERROR\n"
					+"Code: 5\n"
					+"Description: Server aborted session: Read access denied for user\n"
					+"Loss: 0\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgDone = "FASPMGR 2\n"
					+"Type: DONE\n"
					+"Direction: Send\n"
					+"Operation: Transfer\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgDone1 = "FASPMGR 2\n"
			+"Type: DONE\n"
			+"Direction: Send\n"
			+"Operation: Transfer\n"
			+"SessionId: 7d564ec6-7765-49d7-b2a7-9380dec7a69f\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgDone2 = "FASPMGR 2\n"
			+"Type: DONE\n"
			+"Direction: Send\n"
			+"Operation: Transfer\n"
			+"SessionId: f4c34a23-41f5-4fee-bda0-f014a4a2c92b\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";

	final String xferId = "6a31a3b4-3b89-4af4-99ab-1b7dd2d00000";
	
	@Before
    public void setup(){
		PowerMockito.mockStatic(ITransferListener.class);

		PowerMockito.suppress(PowerMockito.defaultConstructorIn(ITransferListener.class));
	}

	/**
	 * Test correct status is set after multiple calls to transferReporter 
	 * 
	 */	
	@Test
	public void testTransferReporterUpdatesStatus(){

		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgSession);
		transferListener.transferReporter(xferId, msgError);

		assertEquals("ERROR", transferListener.getStatus(xferId));
	}

	@Test
	public void testErrorLogWhenStatusUpdatedToError() throws Exception{
		
		PowerMockito.mockStatic(faspmanager2.class);
		
		PowerMockito
		.when(
			faspmanager2.stopTransfer(anyString())
		).thenReturn(true);

		TransferListener spyTransferListener = spy(TransferListener.class);
		
		//PowerMockito.whenNew(TransferListener.class).withNoArguments().thenReturn(spyTransferListener);
		
		spyTransferListener.log = mockLog;
		
		spyTransferListener.setStatus("abc123", null, "ERROR", 0);

		verify(mockLog, times(1)).error(Mockito.eq("Status marked as [ERROR] for xferId [abc123]"));
	}
	
	@Test
	public void testAscpCountIsIncremented() {

		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit);
		
		assertEquals(1, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgInit1);
		
		assertEquals(2, transferListener.getAscpCount());
	}
	
	@Test
	public void testAscpCountIsIncrementedOnlyByInit() {
		
		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgSession);
		
		assertEquals(1, transferListener.getAscpCount());
	}

	@Test
	public void testAscpCountIsDecrementedByError() {

		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgSession);
		transferListener.transferReporter(xferId, msgError);
		
		assertEquals(0, transferListener.getAscpCount());
	}

	@Test
	public void testAscpCountIsDecrementedByDone() {

		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit);
		
		assertEquals(1, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgDone);
		
		assertEquals(0, transferListener.getAscpCount());
	}

	@Test
	public void testAscpCountIsDecrementedByDoneAndError() {

		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit);
		
		assertEquals(1, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgDone);
		
		assertEquals(0, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgInit);
		
		assertEquals(1, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgError);
		
		assertEquals(0, transferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgInit);
		
		assertEquals(1, transferListener.getAscpCount());
	}
	
	@Test
	public void testAscpCountMultiSession() {
		TransferListener transferListener = new TransferListener();
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgInit1);
		transferListener.transferReporter(xferId, msgInit2);
				
		assertEquals(3, TransferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgDone);
		
		assertEquals(2, TransferListener.getAscpCount());
		
		transferListener.transferReporter(xferId, msgError);
		
		assertEquals(0, TransferListener.getAscpCount());
		
	}

	/**
	 * This test is actually testing the checkAscpThreshold() which is a private method in {@link AsperaTransferManager}, 
	 * But is link into validating getAscpCount();
	 */
	@Test(expected = AsperaTransferException.class)
	@Ignore("Need to update to pass in proper values to getInstance()")
	public void testAscpCountThresholdThrowsException() {

		TransferListener transferListener = TransferListener.getInstance(null, null);
		for (int n=0; n<=10 ; n++) {
			transferListener.transferReporter(xferId, msgInit);			
		}
		
		checkAscpThreshold();
	}

    /**
     * Check if ascp count has hit limit
     * If it has throw an exception
     */
    private void checkAscpThreshold() {

		if (TransferListener.getInstance(null, null).getAscpCount() >= ASCP_COUNT) {
			throw new AsperaTransferException("ASCP process threshold has been reached");
		}
    }
}
