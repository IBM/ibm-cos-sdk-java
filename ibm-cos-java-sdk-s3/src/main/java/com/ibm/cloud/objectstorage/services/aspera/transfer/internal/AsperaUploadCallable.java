package com.ibm.cloud.objectstorage.services.aspera.transfer.internal;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaConfig;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaTransaction;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaTransferManager;
import com.ibm.cloud.objectstorage.services.aspera.transfer.TransferSpecs;
import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;

public class AsperaUploadCallable implements Callable<AsperaTransaction>{
	private AsperaTransferManager transferManager;
	private CountDownLatch latch;
	private String bucket;
	private File localFileName;
	private String remoteFileName;
	private AsperaConfig sessionDetails;
	private ProgressListener progressListener;

	public AsperaUploadCallable(AsperaTransferManager transferManager, String bucket, File localFileName, String remoteFileName, AsperaConfig sessionDetails, ProgressListener progressListener) {
		this.transferManager = transferManager;
		this.bucket = bucket;
		this.localFileName = localFileName;
		this.remoteFileName = remoteFileName;
		this.sessionDetails = sessionDetails;
		this.progressListener = progressListener;
	}

	@Override
	public AsperaTransaction call() throws Exception {
		
		FASPConnectionInfo faspConnectionInfo = transferManager.getFaspConnectionInfo(bucket);
		
		// Get transfer spec
		TransferSpecs transferSpecs = transferManager.getTransferSpec(faspConnectionInfo, localFileName.getAbsolutePath(), remoteFileName, "upload");

		//Check if the global setting for mulitsession has been applied
		transferManager.checkMultiSessionAllGlobalConfig(transferSpecs);

		if(sessionDetails != null)
			transferManager.modifyTransferSpec(sessionDetails, transferSpecs);
		
		ObjectMapper mapper = new ObjectMapper();
		String transferSpecStr = null;
		try {
			transferSpecStr = mapper.writeValueAsString(transferSpecs);

		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AsperaTransaction asperaTransaction = transferManager.processTransfer(transferSpecStr, bucket, remoteFileName, localFileName.getAbsolutePath(), progressListener);

		return asperaTransaction;
	}
	
	
}
