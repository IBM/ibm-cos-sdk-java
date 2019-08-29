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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.aspera.faspmanager2.faspmanager2;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class, AsperaTransferManager.class, AsperaLibraryLoader.class })
public class AsperaTransferManagerBuilderTest {

    @Mock
    private AmazonS3 mockS3Client = mock(AmazonS3.class);

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
    public void setup(){
		PowerMockito.mockStatic(faspmanager2.class);
		PowerMockito.mockStatic(TransferListener.class);
		PowerMockito.mockStatic(AsperaLibraryLoader.class);
    }
	
	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 * 
	 */	
	@Test
	public void testExceptionThrownWhenNullS3Client() {
		expectedEx.expect(SdkClientException.class);

	    new AsperaTransferManagerBuilder("apiKey", null).build();
	}

	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 * 
	 */	
	@Test
	public void testExceptionThrownWhenNullS3ApiKey() {
		expectedEx.expect(SdkClientException.class);
	    
	    new AsperaTransferManagerBuilder(null, mockS3Client).build();
	}
	
	/**
	 * Test AsperaTransferManagerBuilder successfully builds an AsperaTransferManager
	 * when the appropriate parameters are provided 
	 * 
	 */	
	@Test
	public void testAsperTransferManagerIsCreated() {
	    
	    AsperaTransferManager transferManager = new AsperaTransferManagerBuilder("apiKey", mockS3Client).build();
	    
	    assertNotNull(transferManager);
	}
}
