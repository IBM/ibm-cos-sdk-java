package com.ibm.cloud.objectstorage.services.aspera.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.aspera.faspmanager2.faspmanager2;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;

/**
 * Unit testing of the AsperaTransferManager
 * 
 * NOTE: Due to the number of tests there are separate junit test
 * classes for each of the method types e.g. upload/download etc
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class, AsperaTransferManager.class, AsperaLibraryLoader.class })
public class AsperaTransferManagerTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Mock
	private AmazonS3 mockS3Client = mock(AmazonS3.class);

	@Mock
	private TokenManager mockTokenManager = mock(TokenManager.class);

	@Mock
	private AsperaConfig mockAsperaConfig = mock(AsperaConfig.class);

	@Mock
	private AsperaTransferManagerConfig mockAsperaTransferManagerConfig = mock(AsperaTransferManagerConfig.class);

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
	public void testExceptionThrownWhenNullS3Client() {
		expectedEx.expect(SdkClientException.class);

		new AsperaTransferManager(null, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig);
	}

	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 * 
	 */	
	@Test
	public void testExceptionNotThrownWhenNullS3TokenManager() {
		new AsperaTransferManager(mockS3Client, null, mockAsperaConfig, mockAsperaTransferManagerConfig);
	}

	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 * 
	 */	
	@Test
	public void testExceptionNotThrownWhenNullAsperaConfig() {
		new AsperaTransferManager(mockS3Client, mockTokenManager, null, mockAsperaTransferManagerConfig);
	}

	/**
	 * Test AsperaTransferManager throws SdkClientException with appropriate message
	 * 
	 */	
	@Test
	public void testExceptionThrownWhenNullAsperaTransferManagerConfig() {
		expectedEx.expect(SdkClientException.class);

		new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, null);
	}

	/**
	 * Test AsperaTransferManager passing in AsperaTransferManagerConfig with the
	 * MultiSession flag set to true. This should update the remote_host within the transferspec 
	 * 
	 */	
	@Test
	public void testRemoteHostUpdatedWhenAsperaTransferManagerConfigMultiSessionTrue() {

		AsperaTransferManagerConfig asperaTransferManagerConfig = new AsperaTransferManagerConfig();
		asperaTransferManagerConfig.setMultiSession(true);
		TransferSpec transferSpec = new TransferSpec();
		transferSpec.setRemote_host("mysubDomain.domain.realm");

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, asperaTransferManagerConfig);
		atm.checkMultiSessionAllGlobalConfig(transferSpecs);

		TransferSpec modifiedTransferSpec = transferSpecs.getTransfer_specs().get(0);
		assertEquals("mysubDomain-all.domain.realm", modifiedTransferSpec.getRemote_host());
	}

	/**
	 * Test AsperaTransferManager passing in AsperaTransferManagerConfig with the
	 * MultiSession flag set to false. This should not update the remote_host within the transferspec 
	 * 
	 */	
	@Test
	public void testRemoteHostUpdatedWhenAsperaTransferManagerConfigMultiSessionFalse() {

		PowerMockito
		.when(
				AsperaLibraryLoader.load()
				).thenReturn("/tmp");

		AsperaTransferManagerConfig asperaTransferManagerConfig = new AsperaTransferManagerConfig();
		asperaTransferManagerConfig.setMultiSession(false);
		TransferSpec transferSpec = new TransferSpec();
		transferSpec.setRemote_host("mysubDomain.domain.realm");

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, asperaTransferManagerConfig);
		atm.checkMultiSessionAllGlobalConfig(transferSpecs);

		TransferSpec modifiedTransferSpec = transferSpecs.getTransfer_specs().get(0);
		assertEquals("mysubDomain.domain.realm", modifiedTransferSpec.getRemote_host());
	}

	/**
	 * Test Asperaconfig setting is applied at runtime  
	 * 
	 */	
	@Test
	public void testRemoteHostUpdatedWhenAsperaTransferConfigMultiSession3RunTime() {

		AsperaConfig asperaConfig = new AsperaConfig();
		asperaConfig.setMultiSession(3);
		TransferSpec transferSpec = new TransferSpec();

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig);
		atm.modifyTransferSpec(asperaConfig, transferSpecs);

		TransferSpec modifiedTransferSpec = transferSpecs.getTransfer_specs().get(0);
		assertEquals(3, modifiedTransferSpec.getMulti_session());
	}

	/**
	 * Test MultiSessionThreshold Asperaconfig setting is applied at runtime  
	 * 
	 */	
	@Test
	public void testMultiSessionThresholdUpdatedWhenAsperaTransferConfigRunTime() {

		AsperaConfig asperaConfig = new AsperaConfig();
		asperaConfig.setMultiSessionThreshold(70000);
		TransferSpec transferSpec = new TransferSpec();

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig);
		atm.modifyTransferSpec(asperaConfig, transferSpecs);

		TransferSpec modifiedTransferSpec = transferSpecs.getTransfer_specs().get(0);
		assertEquals(70000, modifiedTransferSpec.getMulti_session_threshold());
	}

	/**
	 * Test multi_session is not passed through to Aspera, when the value is not set. 
	 * The multi_session should not be set to zero
	 * @throws JsonProcessingException 
	 * 
	 */	
	@Test
	public void testMultiSessionFieldIsNotSetToZeroIfDefault() throws JsonProcessingException {

		AsperaConfig asperaConfig = new AsperaConfig();
		TransferSpec transferSpec = new TransferSpec();

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig);
		atm.modifyTransferSpec(asperaConfig, transferSpecs);

		ObjectMapper mapper = new ObjectMapper();
		String transferSpecStr = mapper.writeValueAsString(transferSpecs);
		System.out.println(transferSpecStr);
		assertFalse(transferSpecStr.contains("multi_session"));
	}

	/**
	 * Test multi_session is not passed through to Aspera, when the value is not set. 
	 * The multi_session should not be set to zero
	 * @throws JsonProcessingException 
	 * 
	 */	
	@Ignore
	@Test
	public void testMultiSessionFieldIsSetToZeroIfSpecified() throws JsonProcessingException {

		AsperaConfig asperaConfig = new AsperaConfig();
		asperaConfig.setMultiSession(0);
		TransferSpec transferSpec = new TransferSpec();

		TransferSpecs transferSpecs = new TransferSpecs();
		List<TransferSpec> list = new ArrayList<TransferSpec>();
		list.add(transferSpec);
		transferSpecs.setTransfer_specs(list);

		AsperaTransferManager atm = new AsperaTransferManager(mockS3Client, mockTokenManager, mockAsperaConfig, mockAsperaTransferManagerConfig);
		atm.modifyTransferSpec(asperaConfig, transferSpecs);

		ObjectMapper mapper = new ObjectMapper();
		String transferSpecStr = mapper.writeValueAsString(transferSpecs);
		System.out.println(transferSpecStr);
		assertTrue(transferSpecStr.contains("multi_session"));
	}
}
