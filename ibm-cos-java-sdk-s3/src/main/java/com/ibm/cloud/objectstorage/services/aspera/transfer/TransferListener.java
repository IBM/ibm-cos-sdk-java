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

import static com.ibm.cloud.objectstorage.event.SDKProgressPublisher.publishRequestBytesTransferred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ibm.aspera.faspmanager2.ITransferListener;
import com.ibm.cloud.objectstorage.services.aspera.transfer.AsperaTransaction.AsperaTransactionState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Singleton Listener for the progress of all Aspera transfer. Each status message is 
 * parsed for the status and updated in the internal map, using xferId as the key. 
 *
 */

public class TransferListener extends ITransferListener
{
	//Map to hold last timestamp of a transaction calling into transferReporter
	final Map<String, Long> transactionCallbackTime;
	//
	final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	final Map<String, String> status;
	final Map<String, List<String>> transactionSessions;
	final Map<String, Long> bytesTransferred;
	final Map<String, Long> totalPreTransferBytes;
	final Map<String, Map<String, Long>> sessionsBytesTransferred;
	private static final Map<String, List<AsperaTransaction>> transactions = new ConcurrentHashMap<String, List<AsperaTransaction>>();
	private static TransferListener instance = null;
	protected Log log = LogFactory.getLog(AsperaFaspManagerWrapper.class);
	private static int ascpCount;

	protected TransferListener()
	{
		super();
		status = new ConcurrentHashMap<String, String>();
		transactionSessions = new ConcurrentHashMap<String, List<String>>();
		bytesTransferred = new ConcurrentHashMap<String, Long>();
		totalPreTransferBytes = new ConcurrentHashMap<String, Long>();
		sessionsBytesTransferred = new ConcurrentHashMap<String, Map<String, Long>>();
		transactionCallbackTime = new ConcurrentHashMap<String, Long>();
		startScheduler();

		ascpCount = 0;
	}

	/**
	 * Returns TransferListener instance and associates an AsperaTransaction with a Transfer ID.
	 * On change of transfer status or bytes transferred the TransferLsitener will fire a progress
	 * change event to all progress listeners attached to the AsperaTransaction.
	 * 
	 * @param xferId
	 * @param transaction
	 * @return
	 */
	public static TransferListener getInstance(String xferId, AsperaTransaction transaction) {
		if(instance == null) {
			instance = new TransferListener();
		}

		if(transactions.get(xferId) != null) {
			transactions.get(xferId).add(transaction);
		} else {
			List<AsperaTransaction> transferTransactions = new ArrayList<AsperaTransaction>();
			transferTransactions.add(transaction);
			transactions.put(xferId, transferTransactions);
		}

		return instance;
	}

	/**
	 * 
	 * @param xferId
	 * @param msg
	 * 
	 * Set the status for the xferid 
	 */
	public synchronized void transferReporter(String xferId, String msg)
	{
		transactionCallbackTime.put(xferId, System.currentTimeMillis());
		// Parse transfer stats from ascp callback message
		log.debug("TransferListener >>  transferReporter: msg= " + msg);
		log.trace("TransferListener.transferReporter >> " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());
		HashMap<String, String> msgStats = new HashMap<String, String>();
		String[] keyValPairs = msg.split("\n");
		for(String keyVal : keyValPairs) {
			String[] arr = keyVal.split(":");
			if(arr.length == 2)
				msgStats.put(arr[0].trim(), arr[1].trim());
		}

		// Resolve transfer status
		String type = msgStats.get("Type");		
		AsperaTransactionState transState = AsperaTransactionState.valueOf(type);

		// Get Session Id
		String sessionId = msgStats.get("SessionId");

		if(msgStats.get("PreTransferBytes") != null
				&& transState == AsperaTransactionState.NOTIFICATION) {
			Long oldPreTransferBytes = totalPreTransferBytes.get(xferId);
			Long newPreTransferByteTotal = 0l;
			long preTransferBytes = 0;
			try {
				preTransferBytes = Long.parseLong(msgStats.get("PreTransferBytes"));
			} catch (NumberFormatException e) {
				preTransferBytes = 0;
			}

			if(oldPreTransferBytes == null) {
				totalPreTransferBytes.put(xferId, preTransferBytes);
				newPreTransferByteTotal = preTransferBytes;
			} else {
				newPreTransferByteTotal = oldPreTransferBytes + preTransferBytes;
				totalPreTransferBytes.put(xferId, newPreTransferByteTotal);
			}

			setTotalBytesToTransfer(xferId, newPreTransferByteTotal);
		}


		long bytes=0;
		if(msgStats.get("FileBytes") != null 
				&& (transState == AsperaTransactionState.STATS
				|| transState == AsperaTransactionState.STOP
				|| transState == AsperaTransactionState.DONE)) {

			try {
				bytes = Long.parseLong(msgStats.get("FileBytes"));
			} catch (NumberFormatException e) {
				bytes = 0;
			}

			Map<String, Long> sessionBytesTransferred = sessionsBytesTransferred.get(xferId);
			if(sessionBytesTransferred == null) {
				sessionBytesTransferred = new ConcurrentHashMap<String, Long>();
				sessionBytesTransferred.put(sessionId, 0l);
				sessionsBytesTransferred.put(xferId, sessionBytesTransferred);
			}

		}

		setStatus(xferId, sessionId, type, bytes);

		log.trace("TransferListener.transferReporter << " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());
	}

	private void setTotalBytesToTransfer(String xferId, Long preTransferBytes) {
		for(AsperaTransaction transaction : transactions.get(xferId)) {
			if(transaction.getProgress() != null)
				transaction.getProgress().setTotalBytesToTransfer(preTransferBytes);
		}
	}

	public String getStatus(String xferId) {
		synchronized(this) {
			return status.get(xferId);
		}
	}

	public synchronized void setStatus(String xferId, String sessionId, String status, long bytes) {
		log.trace("TransferListener.setStatus >> " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());

		//Increment or Decrement the ascpCount
		if(status.equals("INIT") && isNewSession(xferId, sessionId)) {
			ascpCount++;
		} else if (status.equals("DONE") || status.equals("STOP") || status.equals("ARGSTOP")){
			// Remove session from current session list
			removeTransactionSession(xferId, sessionId);
			removeTransactionFromAudit(xferId);
		} else if (status.equals("ERROR")) {
			log.error("Status marked as [ERROR] for xferId [" + xferId + "]");

			// Reduce the ascp count by the number of sessions in transaction
			ascpCount -= numberOfSessionsInTransaction(xferId);
			removeAllTransactionSessions(xferId);
			removeTransactionProgressData(xferId);
			removeTransactionFromAudit(xferId);
		}

		if(status.equals("ARGSTOP")) {
			removeTransactionFromAudit(xferId);
			removeAllTransactionSessions(xferId);
		}

		if(bytes > 0)
			updateProgress(xferId, sessionId, status, bytes);
		// Only set status to DONE if all bytes transferred.
		if(status.equals("DONE") || status.equals("STOP")) {
			if(totalPreTransferBytes.get(xferId) == null 
					|| ((bytesTransferred.get(xferId) != null && bytesTransferred.get(xferId) >= totalPreTransferBytes.get(xferId)))) {
				this.status.put(xferId, status);
				removeTransactionProgressData(xferId);
				removeTransactionFromAudit(xferId);
				log.info("Status marked as [" + status + "] for xferId [" + xferId + "]");
			}
		} else {
			this.status.put(xferId, status);
		}

		log.trace("TransferListener.setStatus << " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());
	}

	private void removeTransactionProgressData(String xferId) {
		TransferListener.transactions.remove(xferId);		
	}

	private void removeTransactionFromAudit(String xferId) {
		transactionCallbackTime.remove(xferId);		
	}

	/**
	 * return the current ascp count
	 * @return
	 */
	public static int getAscpCount() {
		return ascpCount;
	}

	private synchronized void updateProgress(String xferId, String sessionId, String status, long bytes) {
		log.trace("TransferListener.updateProgress >> " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());

		Long totalBytesTransferred = bytesTransferred.get(xferId);
		if(totalBytesTransferred == null)
			totalBytesTransferred = new Long(0);

		Map<String, Long> sessionBytesTransferred = sessionsBytesTransferred.get(xferId);
		Long currentBytesTransferredForSession = sessionBytesTransferred.get(sessionId);
		if(currentBytesTransferredForSession == null) {
			currentBytesTransferredForSession = 0l;
			sessionBytesTransferred.put(sessionId, currentBytesTransferredForSession);
		}

		long chunkBytes = bytes - currentBytesTransferredForSession;
		sessionBytesTransferred.put(sessionId, bytes);
		this.bytesTransferred.put(xferId, totalBytesTransferred + chunkBytes);
		AsperaTransactionState transState;
		try {
			transState = AsperaTransactionState.valueOf(status);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			transState = null;
		}

		switch (transState) {
		case STATS:
			fireProgressEvent(xferId, chunkBytes);
			break;
		case DONE:
			fireProgressEvent(xferId, chunkBytes);
			break;
		case STOP:
			fireProgressEvent(xferId, chunkBytes);
			break;
		default:
			break;
		}

		log.trace("TransferListener.updateProgress << " + System.nanoTime() + ": " + new Exception().getStackTrace()[1].getClassName());
	}

	protected void fireProgressEvent(String xferid, long bytesTransferred) {
		log.trace("TransferListener.fireProgressEvent >> " + System.nanoTime());
		if(transactions.get(xferid) == null)
			return;
		for(AsperaTransaction transaction : transactions.get(xferid)) {
			publishRequestBytesTransferred(transaction.getProgressListenerChain(), bytesTransferred);
		}
		log.trace("TransferListener.fireProgressEvent << " + System.nanoTime());
	}

	public void removeTransaction(String xferid) {
		log.debug("TransferListener >> removeTransaction: " + xferid);
		this.status.remove(xferid);
		this.bytesTransferred.remove(xferid);
		this.totalPreTransferBytes.remove(xferid);
		this.sessionsBytesTransferred.remove(xferid);
	}

	/**
	 * Return true if new session for transaction
	 * 
	 * @param xferId
	 * @param sessionId
	 * @return boolean
	 */
	private boolean isNewSession(String xferId, String sessionId) {
		List<String> currentSessions = transactionSessions.get(xferId);

		if(currentSessions == null) {
			List<String> sessions = new ArrayList<String>();
			sessions.add(sessionId);
			transactionSessions.put(xferId, sessions);
			return true;
		} else if (!currentSessions.contains(sessionId)) {
			currentSessions.add(sessionId);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Removes the specified transaction session
	 * 
	 * @param xferId
	 * @param sessionId
	 */
	private void removeTransactionSession(String xferId, String sessionId) {
		List<String> sessions = transactionSessions.get(xferId);

		if(sessions != null){
			final boolean removal = sessions.remove(sessionId);
			if (removal) {
				ascpCount--;
			}
		}
	}

	/**
	 * Removes all sessions for their specified transaction
	 * 
	 * @param xferId
	 * @param sessionId
	 */
	public void removeAllTransactionSessions(String xferId) {
		List<String> sessions = transactionSessions.get(xferId);

		if(sessions != null)
			sessions.clear();
	}

	/**
	 * Returns the number of active sessions for transaction.
	 * 
	 * @param xferId
	 * @return int
	 */
	private int numberOfSessionsInTransaction(String xferId) {
		int sessionCount = 0;
		List<String> sessions = transactionSessions.get(xferId);

		if(sessions != null)
			sessionCount = sessions.size();

		return sessionCount;
	}

	/**
	 * Start the scheduler to monitor the transactions timestamps within transactionAuditTime
	 */
	@SuppressWarnings("unchecked")
	private void startScheduler(){

		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			@SuppressWarnings("rawtypes")
			public void run()  {

				Iterator<Entry<String, Long>> it = transactionCallbackTime.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
					if ((System.currentTimeMillis() - (Long)pair.getValue()) > 5000 ){
						final String xferId = (String)pair.getKey();
						it.remove();
						status.put(xferId, "ERROR");
						ascpCount -= numberOfSessionsInTransaction(xferId);
						removeAllTransactionSessions(xferId);
						removeTransactionProgressData(xferId);
						log.error("Status marked as [ERROR] for xferId [" + xferId + "] after not reporting for over 5 seconds");
					}
				}
			}
		},5,5,TimeUnit.SECONDS);
	}

	/**
     * Shutdown without interrupting the threads involved
     */
    public void shutdownThreadPools() {
    	scheduledExecutorService.shutdown();
    }
	
	/**
     * Releasing all resources created by <code>TransferListener</code> before it
     * is being garbage collected.
     */
    @Override
    protected void finalize() {
        shutdownThreadPools();
    }
}
