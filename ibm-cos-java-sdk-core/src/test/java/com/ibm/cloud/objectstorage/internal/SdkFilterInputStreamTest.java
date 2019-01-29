package com.ibm.cloud.objectstorage.internal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.junit.Test;

public class SdkFilterInputStreamTest {

	/**
	 * Check that the boolean aborted flag is set to True
	 */
	@SuppressWarnings("resource")
	@Test
	public void testInputStreamIsAborted(){
	
		InputStream in = mock(InputStream.class);
		SdkFilterInputStream sdkFilterInputStream = new SdkFilterInputStream(in);
		
		sdkFilterInputStream.abort();
		
		assertTrue(sdkFilterInputStream.isAborted());
	}

	/**
	 * Verify that when the inputstream is an instance of SdkFilterInputStream that
	 * the stream abort() method is called
	 */
	@Test
	public void testInputStreamAbortedIsCalled(){
	
		SdkFilterInputStream in = mock(SdkFilterInputStream.class);
		SdkFilterInputStream sdkFilterInputStream = spy(new SdkFilterInputStream(in));
		
		sdkFilterInputStream.abort();
		
		verify(in, times(1)).abort();
	}
}
