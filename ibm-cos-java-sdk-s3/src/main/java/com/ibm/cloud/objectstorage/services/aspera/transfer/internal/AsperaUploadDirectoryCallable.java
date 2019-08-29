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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaConfig;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaTransaction;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaTransferManager;
import com.ibm.cloud.objectstorage.services.aspera.transfer.TransferSpecs;
import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;

public class AsperaUploadDirectoryCallable implements Callable<AsperaTransaction>{
	private AsperaTransferManager transferManager;
	private String bucket;
	private File localDirectory;
	private String virtualDirectory;
	private AsperaConfig sessionDetails;
	private boolean includeSubdirectories;
	private ProgressListener progressListener;

	public AsperaUploadDirectoryCallable(AsperaTransferManager transferManager, String bucket, File localDirectory, String virtualDirectory, AsperaConfig sessionDetails, boolean includeSubdirectories, ProgressListener progressListener) {
		this.transferManager = transferManager;
		this.bucket = bucket;
		this.localDirectory = localDirectory;
		this.virtualDirectory = virtualDirectory;
		this.sessionDetails = sessionDetails;
		this.includeSubdirectories = includeSubdirectories;
		this.progressListener = progressListener;
	}

	@Override
	public AsperaTransaction call() throws Exception {
		FASPConnectionInfo faspConnectionInfo = transferManager.getFaspConnectionInfo(bucket);
		
		// Get transfer spec
		TransferSpecs transferSpecs = transferManager.getTransferSpec(faspConnectionInfo, localDirectory.getAbsolutePath(), virtualDirectory, "upload");

		//Check if the global setting for mulitsession has been applied
		transferManager.checkMultiSessionAllGlobalConfig(transferSpecs);

		if(sessionDetails != null) {
			transferManager.modifyTransferSpec(sessionDetails, transferSpecs);
		}
		
		if (!includeSubdirectories) {
			transferManager.excludeSubdirectories(localDirectory, transferSpecs);
		}

		ObjectMapper mapper = new ObjectMapper();
		String transferSpecStr = null;
		try {
			transferSpecStr = mapper.writeValueAsString(transferSpecs);

		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AsperaTransaction asperaTransaction = transferManager.processTransfer(transferSpecStr, bucket, virtualDirectory, localDirectory.getAbsolutePath(), progressListener);

		return asperaTransaction;
	}
	
	
}
