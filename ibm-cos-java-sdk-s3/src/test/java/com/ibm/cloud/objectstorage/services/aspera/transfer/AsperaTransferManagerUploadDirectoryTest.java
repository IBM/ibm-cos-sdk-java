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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Ignore;
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
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Unit testing of the AsperaTransferManager
 * 
 * NOTE: Due to the number of tests there are separate junit test
 * classes for each of the method types e.g. upload/download etc
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class, AsperaTransferManager.class, AsperaLibraryLoader.class })
public class AsperaTransferManagerUploadDirectoryTest {

	private final String TEST_BUCKET_NAME = "test-bucket-1";
	private final String TEST_REMOTE_FILENAME = "testRemoteFile1";
	private final String TEST_VIRTUAL_DIRECTORY_KEY_PREFIX = "testVirtualDirectoryKeyPrefix";
	
    @Mock
    private AmazonS3 mockS3Client = mock(AmazonS3.class);
    
    @Mock
    private TokenManager mockTokenManager = mock(TokenManager.class);

    @Mock
    private AsperaConfig mockAsperaConfig = mock(AsperaConfig.class);

    @Mock
    private AsperaTransferManagerConfig mockAsperaTransferManagerConfig = mock(AsperaTransferManagerConfig.class);

    @Mock
    private File mockDirectoryToUpload = mock(File.class);

	@Mock
    private TransferListener mockTransferListener = mock(TransferListener.class);
	
	private AsperaTransferManager spyAtm;
	
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
	 */
	@Test
	public void testExceptionThrownWhenNullBucketName() {
		expectedEx.expect(SdkClientException.class);
		
		spyAtm = spy(new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig));
		
		Mockito
			.doNothing()
			.when(spyAtm).checkAscpThreshold();

		//public Future<AsperaTransaction> uploadDirectory(String bucketName, String virtualDirectoryKeyPrefix, File directory,
				//boolean includeSubdirectories) {
	    spyAtm.uploadDirectory(null, TEST_VIRTUAL_DIRECTORY_KEY_PREFIX, mockDirectoryToUpload, true);
	}
	
	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 */	
	@Test
	public void testExceptionThrownWhenEmptyBuckName() throws Exception {
		
		expectedEx.expect(SdkClientException.class);
	    
		PowerMockito
			.when(
				TransferListener.getInstance(anyString(), any(AsperaTransaction.class))
			).thenReturn(mockTransferListener);
		
		spyAtm = spy(new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig));
		
		Mockito
			.doNothing()
			.when(spyAtm).checkAscpThreshold();
		
	    spyAtm.uploadDirectory("", TEST_VIRTUAL_DIRECTORY_KEY_PREFIX, mockDirectoryToUpload, true);
	}
	
	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 */
	@Test
	public void testExceptionThrownWhenNullUploadDirectory() {
		expectedEx.expect(SdkClientException.class);
		
		PowerMockito
		.when(
			TransferListener.getInstance(anyString(), any(AsperaTransaction.class))
		).thenReturn(mockTransferListener);
	
		spyAtm = spy(new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig));
	
		Mockito
			.doNothing()
			.when(spyAtm).checkAscpThreshold();

	    spyAtm.uploadDirectory(TEST_BUCKET_NAME, TEST_VIRTUAL_DIRECTORY_KEY_PREFIX, null, true);
	}
	
	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 */
	@Test
	public void testExceptionThrownWhenFileToUploadDoesNotExist() {
		expectedEx.expect(SdkClientException.class);
	    
	    when(mockDirectoryToUpload.exists()).thenReturn(false);
	    
	    PowerMockito
		.when(
			TransferListener.getInstance(anyString(), any(AsperaTransaction.class))
		).thenReturn(mockTransferListener);
	
		spyAtm = spy(new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig));
	
		Mockito
			.doNothing()
			.when(spyAtm).checkAscpThreshold();

	    spyAtm.uploadDirectory(TEST_BUCKET_NAME, TEST_VIRTUAL_DIRECTORY_KEY_PREFIX, mockDirectoryToUpload, true);
	}
}
