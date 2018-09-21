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
import com.ibm.cloud.objectstorage.services.s3.transfer.TransferProgress;

public interface AsperaTransaction extends AsperaTransfer{

	public AsperaResult waitForCompletion() throws AsperaTransferException, InterruptedException;

	/**
     * Enumeration of the possible transaction states.
     */
    public static enum AsperaTransactionState {
        /** The transfer is waiting for resources to execute and has not started yet. */
        INIT,
        
        /** The transfer is waiting for resources to execute and has not started yet. */
        NOTIFICATION,
        
        /** The transfer is actively uploading or downloading and hasn't finished yet. */
        STATS,

        /** The transfer is actively uploading or downloading and hasn't finished yet. */
        SESSION,

        /** The transfer completed successfully. */
        DONE,
        
        /** The transfer completed successfully. */
        STOP,
        
        /** The transfer completed */
        ARGSTOP,

        /** The transfer failed. */
        ERROR;    	
    }
    
    /**
     * Adds the specified progress listener to the list of listeners
     * receiving updates about this transfer's progress.
     *
     * @param listener
     *            The progress listener to add.
     */
    public void addProgressListener(ProgressListener listener);

    /**
     * Removes the specified progress listener from the list of progress
     * listeners receiving updates about this transfer's progress.
     *
     * @param listener
     *            The progress listener to remove.
     */
    public void removeProgressListener(ProgressListener listener);

    /**
     * Returns progress information about this transfer.
     *
     * @return The progress information about this transfer.
     */
    public TransferProgress getProgress();

	public ProgressListener getProgressListenerChain();

	public ITransferListener getTransferListener();
}
