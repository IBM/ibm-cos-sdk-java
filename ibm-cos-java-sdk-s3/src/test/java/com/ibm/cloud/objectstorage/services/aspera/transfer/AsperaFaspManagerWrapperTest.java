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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.aspera.faspmanager2.faspmanager2;


/**
 * Unit testing of the AsperaFaspManagerWrapper
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class })
public class AsperaFaspManagerWrapperTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Mock
    protected Log mockLog = mock(Log.class);

	@Mock
    private TransferListener mockTransferListener = mock(TransferListener.class);
	
	@Before
    public void setup(){
		PowerMockito.mockStatic(faspmanager2.class);
		PowerMockito.mockStatic(TransferListener.class);
    }

	/**
	 * Check log calls made for start transfer
	 */	
	@Test
	public void testInfoLogCallsMadeForStartTransfer() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.startTransfer(anyString(), isNull(String.class), anyString(), any(TransferListener.class))
			).thenReturn(3L);
		
		when(mockLog.isDebugEnabled()).thenReturn(false);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper(new AsperaTransactionImpl(null, null, null, null, null, null));
		wrapper.log = mockLog;
		
		long rtn = wrapper.startTransfer("xyz123", "configString1");
		assertEquals(3L, rtn);

		verify(mockLog, times(1)).info(Mockito.eq("Starting transfer with xferId [xyz123]"));
	}
	
	@Test
	public void testDebugLogCallsMadeForStartTransfer() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.startTransfer(anyString(), isNull(String.class), anyString(), any(TransferListener.class))
			).thenReturn(3L);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper(new AsperaTransactionImpl(null, null, null, null, null, null));
		wrapper.log = mockLog;
		
		long rtn = wrapper.startTransfer("xyz123", "configString1");
		assertEquals(3L, rtn);

		verify(mockLog, times(1)).debug(Mockito.eq("Transfer Spec for Session with xferId [xyz123]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForStartTransfer() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.startTransfer(anyString(), isNull(String.class), anyString(), any(TransferListener.class))
			).thenReturn(3L);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper(new AsperaTransactionImpl(null, null, null, null, null, null));
		wrapper.log = mockLog;
		
		long rtn = wrapper.startTransfer("xyz123", "configString1");
		assertEquals(3L, rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [startTransfer] with parameters [\"xyz123\", null, transferSpecStr, transferListener]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [startTransfer] returned for xferId [\"xyz123\"] with result: [3]"));
	}
	
	/**
	 * Check log calls made for pause
	 */	
	@Test
	public void testInfoLogCallsMadeForPause() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.modifyTransfer(anyString(), anyInt(), anyLong())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(false);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.pause("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).info(Mockito.eq("Pausing transfer with xferId [xyz123]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForPause() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.modifyTransfer(anyString(), anyInt(), anyLong())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.pause("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [modifyTransfer] with parameters [\"xyz123\", 4, 0]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [modifyTransfer] returned for xferId [\"xyz123\"] with result: [true]"));
	}
	
	/**
	 * Check log calls made for resume
	 */	
	@Test
	public void testInfoLogCallsMadeForResume() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.modifyTransfer(anyString(), anyInt(), anyLong())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(false);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.resume("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).info(Mockito.eq("Resuming transfer with xferId [xyz123]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForResume() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.modifyTransfer(anyString(), anyInt(), anyLong())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.resume("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [modifyTransfer] with parameters [\"xyz123\", 5, 0]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [modifyTransfer] returned for xferId [\"xyz123\"] with result: [true]"));
	}
	
	/**
	 * Check log calls made for cancel
	 */	
	@Test
	public void testInfoLogCallsMadeForCancel() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.stopTransfer(anyString())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(false);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.cancel("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).info(Mockito.eq("Cancel transfer with xferId [xyz123]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForCancel() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.stopTransfer(anyString())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.cancel("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [stopTransfer] with parameters [\"xyz123\", 8, 0]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [stopTransfer] returned for xferId [\"xyz123\"] with result: [true]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForIsRunning() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.isRunning(anyString())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.isRunning("xyz123");
		assertTrue(rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [isRunning] with parameters [\"xyz123\"]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [isRunning] returned for xferId [\"xyz123\"] with result: [true]"));
	}
	
	@Test
	public void testTraceLogCallsMadeForConfigureLogLocation() throws Exception {

		PowerMockito
			.when(
				TransferListener.getInstance(null, null)
			).thenReturn(mockTransferListener);
		
		PowerMockito
			.when(
				faspmanager2.configureLogLocation(anyString())
			).thenReturn(true);
		
		when(mockLog.isDebugEnabled()).thenReturn(true);

		AsperaFaspManagerWrapper wrapper = new AsperaFaspManagerWrapper();
		wrapper.log = mockLog;
		
		boolean rtn = wrapper.configureLogLocation("somePath");
		assertTrue(rtn);

		verify(mockLog, times(1)).trace(Mockito.eq("Calling method [configureLogLocation] with parameters [\"somePath\"]"));
		verify(mockLog, times(1)).trace(Mockito.eq("Method [configureLogLocation] returned for ascpLogPath [\"somePath\"] with result: [true]"));
	}
}
