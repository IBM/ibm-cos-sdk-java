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

import com.ibm.aspera.faspmanager2.ITransferListener;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.event.ProgressListenerChain;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.ibm.cloud.objectstorage.services.s3.transfer.TransferProgress;

/**
 * Class which provides access to the progress & status of a transfer. 
 * This object is returned to the user when any transfer process is started.
 *
 */
public class AsperaTransactionImpl implements AsperaTransaction{
	protected static final InternalLogApi log = InternalLogFactory.getLog(AsperaTransactionImpl.class);
	
	private final String xferid;
	private final TransferListener transferListener;
	private final AsperaFaspManagerWrapper asperaFaspManagerWrapper;
	private final String bucketName;
	private final String key;
	private final String fileName;
	AsperaResultImpl asperaResult;

	private static final String DONE = "DONE";
	private static final String ERROR = "ERROR";
	private static final String STOP = "STOP";
	private static final String ARGSTOP = "ARGSTOP";
	private static final String PROGRESS = "STATS";
	private static final String QUEUED = "INIT";
	
	/** The progress of this transfer. */
    private final TransferProgress transferProgress;
	
	/** Hook for adding/removing more progress listeners. */
    protected final ProgressListenerChain listenerChain;
    

	//TODO also have to add the JNI class to the constructor
	public AsperaTransactionImpl(String xferid, String bucketName, String key, String fileName, TransferProgress transferProgress, ProgressListenerChain listenerChain) {
		this.xferid = xferid;
		this.transferListener = TransferListener.getInstance(xferid, this);
		this.bucketName = bucketName;
		this.key = key;
		this.fileName = fileName;
		this.transferProgress = transferProgress;
		this.listenerChain = listenerChain;
		this.asperaFaspManagerWrapper = new AsperaFaspManagerWrapper();
	}

	@Override 
	public boolean pause() {
		return asperaFaspManagerWrapper.pause(xferid);
	}

	@Override
	public boolean resume() {
		return asperaFaspManagerWrapper.resume(xferid);
	}

    @Override
    public boolean cancel() {
        if (asperaFaspManagerWrapper.cancel(xferid)) {
        	transferListener.removeAllTransactionSessions(xferid);
        	return true;
        } else {
        	return false;
        }
        
    }

	@Override
	public boolean isDone() {
		return (doesStatusMatch(DONE)||doesStatusMatch(ERROR)||doesStatusMatch(STOP)||doesStatusMatch(ARGSTOP));
	} 

	@Override
	public boolean progress() {
		return doesStatusMatch(PROGRESS);
	}

	@Override
	public boolean onQueue() {
		return doesStatusMatch(QUEUED);
	}

	@Override
	public String getBucketName() {
		return bucketName;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public AsperaResult waitForCompletion() throws AsperaTransferException, InterruptedException {
		while (!isDone()  && asperaFaspManagerWrapper.isRunning(xferid)){
			Thread.sleep(500);
		}
		if (doesStatusMatch(DONE)) {
			asperaResult = new AsperaResultImpl(this.bucketName, this.key, this.fileName);
		} else {
			throw new AsperaTransferException("Aspera transfer has completed in Error");
		}

		return asperaResult;
	}

	/**
	 * Check if the status been passed through to check matches the 
	 * current status of the xferId within the transferListener
	 * 
	 * @param status
	 * @return boolean 
	 */
	private boolean doesStatusMatch(String status) {
		return (status.equals(transferListener.getStatus(xferid)) ? true : false);
	}

	@Override
	public void addProgressListener(ProgressListener listener) {
		listenerChain.addProgressListener(listener);		
	}

	@Override
	public void removeProgressListener(ProgressListener listener) {
		listenerChain.removeProgressListener(listener);		
	}

	@Override
	public ProgressListenerChain getProgressListenerChain() {
		return this.listenerChain;		
	}
	
	@Override
	public TransferProgress getProgress() {
		return transferProgress;
	}

	@Override
	public ITransferListener getTransferListener() {
		return this.transferListener;
	}
	
	protected void finalize() {
		this.transferListener.removeTransaction(this.xferid);
	}
}
