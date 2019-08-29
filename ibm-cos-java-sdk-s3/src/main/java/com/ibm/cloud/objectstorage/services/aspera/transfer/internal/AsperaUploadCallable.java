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

		if(sessionDetails != null) {
			transferManager.modifyTransferSpec(sessionDetails, transferSpecs);
		}
		
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
