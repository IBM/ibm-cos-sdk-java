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

import com.ibm.aspera.faspmanager2.faspmanager2;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaTransferManagerUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Wrapper Class which provides logging around the faspmanager2 calls
 */
public class AsperaFaspManagerWrapper {
	private AsperaTransaction asperaTransaction;

    protected Log log = LogFactory.getLog(AsperaFaspManagerWrapper.class);
   
    /**
     * Load in native libraries once
     */
    static {
    	String location = AsperaLibraryLoader.load();
    	faspmanager2.configureAsperaLocation(location);
    	faspmanager2.configureLogLocation(location);
    }
    
    public AsperaFaspManagerWrapper() {}
    
    public AsperaFaspManagerWrapper(AsperaTransaction asperaTransaction) {
    	this.asperaTransaction = asperaTransaction;
    }
	
	public long startTransfer(final String xferId, final String transferSpecStr) {
		log.info("Starting transfer with xferId [" + xferId + "]");
    
		if (log.isDebugEnabled()) {
			log.debug("Transfer Spec for Session with xferId [" + xferId + "]");
			log.debug(AsperaTransferManagerUtils.getRedactedJsonString(
					transferSpecStr, 
					"token"));
		}
		
		log.trace("Calling method [startTransfer] with parameters [\"" + xferId + "\", null, transferSpecStr, transferListener]");
		long rtn = faspmanager2.startTransfer(xferId, null, transferSpecStr, asperaTransaction.getTransferListener());
		log.trace("Method [startTransfer] returned for xferId [\"" + xferId + "\"] with result: [" + rtn + "]");
		return rtn;
	}

	public boolean pause(final String xferId) {
		log.info("Pausing transfer with xferId [" + xferId + "]");
		log.trace("Calling method [modifyTransfer] with parameters [\"" + xferId + "\", 4, 0]");
		boolean rtn = faspmanager2.modifyTransfer(xferId, 4, 0);
		log.trace("Method [modifyTransfer] returned for xferId [\"" + xferId + "\"] with result: [" + rtn + "]");
		return rtn;
	}

	public boolean resume(final String xferId) {
		log.info("Resuming transfer with xferId [" + xferId + "]");
		log.trace("Calling method [modifyTransfer] with parameters [\"" + xferId + "\", 5, 0]");
		boolean rtn = faspmanager2.modifyTransfer(xferId, 5, 0);
		log.trace("Method [modifyTransfer] returned for xferId [\"" + xferId + "\"] with result: [" + rtn + "]");
		return rtn;
	}

    public boolean cancel(final String xferId) {
        log.info("Cancel transfer with xferId [" + xferId + "]");
		log.trace("Calling method [stopTransfer] with parameters [\"" + xferId + "\", 8, 0]");
        boolean rtn = faspmanager2.stopTransfer(xferId);
		log.trace("Method [stopTransfer] returned for xferId [\"" + xferId + "\"] with result: [" + rtn + "]");
		return rtn;
    }

	public boolean isRunning(final String xferId) {
		log.trace("Calling method [isRunning] with parameters [\"" + xferId + "\"]");
		boolean rtn = faspmanager2.isRunning(xferId);
		log.trace("Method [isRunning] returned for xferId [\"" + xferId + "\"] with result: [" + rtn + "]");
		return rtn;
	}

	public boolean configureLogLocation(final String ascpLogPath) {
		log.trace("Calling method [configureLogLocation] with parameters [\"" + ascpLogPath + "\"]");
		boolean rtn =  faspmanager2.configureLogLocation(ascpLogPath);
		log.trace("Method [configureLogLocation] returned for ascpLogPath [\"" + ascpLogPath + "\"] with result: [" + rtn + "]");
		return rtn;
	}

	public void setAsperaTransaction(AsperaTransaction asperaTransaction) {
		this.asperaTransaction = asperaTransaction;
	}

}
