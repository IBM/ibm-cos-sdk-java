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

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonServiceException;

/**
 * Class which provides access to the progress & status of a transfer. 
 * This object is returned to the user when any transfer process is started.
 *
 */
public interface AsperaTransfer {

    /**
     * The name of the bucket where the object is being transfered from/to.
     *
     * @return The name of the bucket where the object is being transfered from/to.
     */
    public String getBucketName();

    /**
     * The key under which this object is stored in Amazon S3.
     *
     * @return The key under which this object is stored in Amazon S3.
     */
    public String getKey();

	/**
	 * Pause the AsperaTransaction transfer
	 * 
	 */
	public boolean pause();

	/**
	 * Resume the AsperaTransaction transfer
	 * 
	 */
	public boolean resume();

    /**
     * Cancel the AsperaTransaction transfer
     * 
     */
    public boolean cancel();

	/**
     * Returns whether or not the transfer is finished (i.e. completed successfully or failed).
     *
     * @return Returns <code>true</code> if this transfer is finished (i.e. completed successfully
     *         or failed).  Returns <code>false</code> if otherwise.
     */
	public boolean isDone();

	/**
     * Returns whether or not the transfer is in a state of progress
     *
     * @return Returns <code>true</code> if this transfer is progressing.
     * Returns <code>false</code> if otherwise.
     */
	public boolean progress();

	/**
     * Returns whether or not the transfer is queueing.
     *
     * @return Returns <code>true</code> if this transfer is queueing.
     * Returns <code>false</code> if otherwise.
     */
	public boolean onQueue();

}
