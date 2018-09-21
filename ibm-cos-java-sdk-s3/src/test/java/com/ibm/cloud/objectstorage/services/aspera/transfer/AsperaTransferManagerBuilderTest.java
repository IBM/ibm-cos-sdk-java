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
