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

/**
 * Transfer Manager level configuration - meant for configuration 
 * that drives how the transfer manager implementation works
 *
 */
public class AsperaTransferManagerConfig {
	
	/**default number of ascp processes allowed to run at one time**/
	private int ascpMaxConcurrent = 10;

	/**optional parameter to set the log location of ascp Logs**/
	private String ascpLogPath;

	/**default size of the FASPConnectionInfo cache size**/
	private int maxFaspCacheSize = 1000;
	
	/**default setting of the multisession 'all' option **/
	private boolean multiSession = false;

	/**
	 * return the number of ascp processes that are allowed to run at 
	 * any one time. Default 10
	 * 
	 * @return
	 */
	public int getAscpMaxConcurrent() {
		return ascpMaxConcurrent;
	}

	/**
	 * Overwrite the default number of ascp processes. Default 10
	 * 
	 * @param ascpMaxConcurrent
	 */
	public void setAscpMaxConcurrent(int ascpMaxConcurrent) {
		this.ascpMaxConcurrent = ascpMaxConcurrent;
	}

	/**
	 * Overwrite the default number of ascp processes. Default 10
	 * 
	 * @param ascpMaxConcurrent
	 */
	public AsperaTransferManagerConfig withAscpMaxConcurrent(int ascpMaxConcurrent) {
		setAscpMaxConcurrent(ascpMaxConcurrent);
		return this;
	}

	/**
	 * return the updated log path for the ascp processes
	 *  
	 * @return
	 */
	public String getAscpLogPath() {
		return ascpLogPath;
	}

	/**
	 * Set the log path for ascp processes to write to
	 * 
	 * @param ascpLogPath
	 */
	public void setAscpLogPath(String ascpLogPath) {
		this.ascpLogPath = ascpLogPath;
	}

	/**
	 * Set the log path for ascp processes to write to
	 * 
	 * @param ascpLogPath
	 */
	public AsperaTransferManagerConfig withAscpLogPath(String ascpLogPath) {
		setAscpLogPath(ascpLogPath);
		return this;
	}

	/**
	 * return the size of the FaspConnectionInfo cache. This info is stored per bucket.
	 * Default size is 1000
	 * 
	 * @return
	 */
	public int getMaxFaspCacheSize() {
		return maxFaspCacheSize;
	}

	/**
	 * Overwrite the FaspConnectionInfo cache. This info is stored per bucket.
	 * Default size is 1000
	 * 
	 * @return
	 */
	public void setMaxFaspCacheSize(int maxFaspCacheSize) {
		this.maxFaspCacheSize = maxFaspCacheSize;
	}


	/**
	 * Overwrite the FaspConnectionInfo cache. This info is stored per bucket.
	 * Default size is 1000
	 * 
	 * @return
	 */
	public AsperaTransferManagerConfig withMaxFaspCacheSize(int maxFaspCacheSize) {
		setMaxFaspCacheSize(maxFaspCacheSize);
		return this;
	}

	/**
	 * Check if Aspera Transfer should use all available connections to the Aspera Service.
	 * If set the remote_host field on the transfer spec is updated with a suffix '-all'
	 * on the subdomain and will direct the underlying Aspera SDK to use multiple sessions
	 * for a transfer. Default is false
	 * @return
	 */
	public boolean isMultiSession() {
		return multiSession;
	}

	/**
	 * Overwrite the default option for multi session using the '-all' suffix on the subdomain
	 * of the remote_host field in the transfer spec
	 *  
	 * @param multiSession
	 */
	public void setMultiSession(boolean multiSession) {
		this.multiSession = multiSession;
	}

	/**
	 * Overwrite the default option for multi session using the '-all' suffix on the subdomain
	 * of the remote_host field in the transfer spec
	 *  
	 * @param multiSession
	 */
	public AsperaTransferManagerConfig withMultiSession(boolean multiSession) {
		setMultiSession(multiSession);
		return this;
	}
}
