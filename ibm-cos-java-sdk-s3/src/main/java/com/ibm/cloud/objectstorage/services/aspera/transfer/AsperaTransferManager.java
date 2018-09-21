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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.http.apache.client.impl.ApacheConnectionManagerFactory.TrustingX509TrustManager;
import com.ibm.cloud.objectstorage.http.conn.ssl.SdkTLSSocketFactory;
import com.ibm.cloud.objectstorage.log.InternalLogApi;
import com.ibm.cloud.objectstorage.log.InternalLogFactory;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.protocol.json.SdkJsonGenerator.JsonGenerationException;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaDownloadCallable;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaDownloadDirectoryCallable;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaTransferManagerUtils;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaUploadCallable;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaUploadDirectoryCallable;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;
import com.ibm.cloud.objectstorage.services.s3.transfer.TransferProgress;
import com.ibm.cloud.objectstorage.services.s3.transfer.internal.S3ProgressListenerChain;
import com.ibm.cloud.objectstorage.services.s3.transfer.internal.TransferManagerUtils;
import com.ibm.cloud.objectstorage.services.s3.transfer.internal.TransferProgressUpdatingListener;
import com.ibm.cloud.objectstorage.util.StringUtils;

/**
 * Aspera transfer Manager class to handle multiple functions to the Aspera
 * service using the Aspera SDK as the underlying library to handle the
 * transfer. This Class also instantiates the token provider
 * {@link AsperaDelegateTokenProvider} to retrieve a delegate token for the
 * Aspera Service
 * 
 */

public class AsperaTransferManager {

	protected static final InternalLogApi log = InternalLogFactory.getLog(AsperaTransferManager.class);
	
	/**AsperConfig to override any default options in the transfer spec**/
	private AsperaConfig asperaConfig;

	/**token manager interface retrieve the delegate token**/
	private TokenManager tokenManager;
	
	/**s3Client to retrieve FaspConnectionInfo per bucket**/
	private AmazonS3 s3Client;

	/**Cache to store FaspConnectionInfo**/
	private AsperaKeyCache akCache;
	private ExecutorService executorService;

	/**Wrapper to allow logging for fasp manager calls**/
	private AsperaFaspManagerWrapper asperaFaspManagerWrapper;

	/**Config to customise the AsperaTransferManager**/
	private AsperaTransferManagerConfig asperaTransferManagerConfig;

	protected AsperaTransferManager(AmazonS3 s3Client, TokenManager tokenManager, AsperaConfig asperaConfig, 
			AsperaTransferManagerConfig asperaTransferManagerConfig) {

		if (s3Client == null)
			throw new SdkClientException("s3Client has not been set for AsperaTransferManager");

		if (asperaTransferManagerConfig == null)
			throw new SdkClientException("asperaTransferManagerConfig has not been set for AsperaTransferManager");

		this.tokenManager = tokenManager;
		this.akCache = new AsperaKeyCache(asperaTransferManagerConfig.getMaxFaspCacheSize());
		this.s3Client = s3Client;
		this.executorService = AsperaTransferManagerUtils.createDefaultExecutorService();
		this.asperaTransferManagerConfig = asperaTransferManagerConfig;
		this.asperaFaspManagerWrapper = new AsperaFaspManagerWrapper();
		this.asperaConfig = asperaConfig;
		//Set log location if configured
		if (null != asperaTransferManagerConfig.getAscpLogPath()) 
			asperaFaspManagerWrapper.configureLogLocation(asperaTransferManagerConfig.getAscpLogPath());
	}

	/**
	 * Uploads a file via Aspera FASP
	 * 
	 * @param bucket
	 * 			  - The bucket to upload the object to 
	 * @param localFileName
	 *            - File path & name of the file to upload
	 * @param remoteFileName
	 *            - The remote file name to save the object as on cloud storage
	 */
	public Future<AsperaTransaction> upload(String bucket, File localFileName, String remoteFileName) {
		
		return upload(bucket, localFileName, remoteFileName, asperaConfig, null);
	}

	public Future<AsperaTransaction> upload(String bucket, File localFileName, String remoteFileName, AsperaConfig sessionDetails, ProgressListener progressListener) {
		log.trace("AsperaTransferManager.upload >> Starting Upload " + System.nanoTime());
		checkAscpThreshold();

		// Destination bucket and source path must be specified
		if (bucket == null || bucket.isEmpty())
			throw new SdkClientException("Bucket name has not been specified for upload");
		if (localFileName == null || !localFileName.exists())
			throw new SdkClientException("localFileName has not been specified for upload");
		if (remoteFileName == null || remoteFileName.isEmpty())
			throw new SdkClientException("remoteFileName has not been specified for upload");

		// Submit upload to thread pool
		AsperaUploadCallable uploadCallable =  new AsperaUploadCallable(this, bucket, localFileName, remoteFileName, sessionDetails, progressListener);
		Future<AsperaTransaction> asperaTransaction = executorService.submit(uploadCallable);

		log.trace("AsperaTransferManager.upload << Ending Upload " + System.nanoTime());
		
		// Return AsperaTransaction
		return asperaTransaction;

	}
	
	/**
	 * Downloads a file via Aspera FASP
	 * 
	 * @param bucket
	 * 			  - The bucket to download the object from 
	 * @param localFileName
	 *            - File path & name to save the downloaed file to
	 * @param remoteFileName
	 *            - The remote file name to download
	 */
	public Future<AsperaTransaction> download(String bucket, File localFileName, String remoteFileName) {
		return download(bucket, localFileName, remoteFileName, asperaConfig, null);
	}

	public Future<AsperaTransaction> download(String bucket, File localFileName, String remoteFileName, AsperaConfig sessionDetails, ProgressListener listenerChain) {
		log.trace("AsperaTransferManager.download >> Starting Download " + System.nanoTime());
		
		checkAscpThreshold();

		// Destination bucket and source path must be specified
		if (bucket == null || bucket.isEmpty())
			throw new SdkClientException("Bucket name has not been specified for download");
		if (localFileName == null || !localFileName.exists())
			throw new SdkClientException("localFile does not exist");
		if (remoteFileName == null || remoteFileName.isEmpty())
			throw new SdkClientException("remoteFileName has not been specified for download");
		// Submit upload to thread pool
		AsperaDownloadCallable downloadCallable =  new AsperaDownloadCallable(this, bucket, localFileName, remoteFileName, sessionDetails, listenerChain);
		Future<AsperaTransaction> asperaTransaction = executorService.submit(downloadCallable);
		
		log.trace("AsperaTransferManager.download << Ending Download " + System.nanoTime());
		return asperaTransaction;

	}

	public Future<AsperaTransaction> downloadDirectory(String bucketName, String virtualDirectoryKeyPrefix, File directory) {
		return downloadDirectory(bucketName, virtualDirectoryKeyPrefix, directory, null, null);
	}

	public Future<AsperaTransaction> downloadDirectory(String bucketName, String virtualDirectoryKeyPrefix, File directory,
			AsperaConfig sessionDetails, ProgressListener progressListener) {
		
		log.trace("AsperaTransferManager.downloadDirectory >> Starting Download " + System.nanoTime());
		checkAscpThreshold();

		if (bucketName == null || bucketName.isEmpty())
			throw new SdkClientException("Bucket name has not been specified for upload");
		if (directory == null || !directory.exists())
			throw new SdkClientException("localFileName has not been specified for upload");
		if (virtualDirectoryKeyPrefix == null || virtualDirectoryKeyPrefix.isEmpty())
			throw new SdkClientException("remoteFileName has not been specified for upload");
		
		// Submit upload to thread pool
		AsperaDownloadDirectoryCallable downloadDirectoryCallable =  new AsperaDownloadDirectoryCallable(this, bucketName, directory, virtualDirectoryKeyPrefix, sessionDetails, progressListener);
		Future<AsperaTransaction> asperaTransaction = executorService.submit(downloadDirectoryCallable);
		
		log.trace("AsperaTransferManager.downloadDirectory << Ending Download " + System.nanoTime());
		// Return AsperaTransaction
		return asperaTransaction;
	}

	/**
	 * Subdirectories are included in the upload by default, to exclude ensure you pass through 'false' for
	 * includeSubdirectories param
	 * 
	 * @param bucketName
	 * @param virtualDirectoryKeyPrefix
	 * @param directory
	 * @param includeSubdirectories
	 * @return
	 */
	public Future<AsperaTransaction> uploadDirectory(String bucketName, String virtualDirectoryKeyPrefix, File directory,
			boolean includeSubdirectories) {
		return uploadDirectory(bucketName, virtualDirectoryKeyPrefix, directory, includeSubdirectories, asperaConfig, null);
	}

	/**
	 * Subdirectories are included in the upload by default, to exclude ensure you pass through 'false' for
	 * includeSubdirectories param
	 * 
	 * @param bucketName
	 * @param virtualDirectoryKeyPrefix
	 * @param directory
	 * @param includeSubdirectories
	 * @param sessionDetails
	 * @return
	 */
	public Future<AsperaTransaction> uploadDirectory(String bucketName, String virtualDirectoryKeyPrefix, File directory,
			boolean includeSubdirectories, AsperaConfig sessionDetails, ProgressListener progressListener) {
		log.trace("AsperaTransferManager.uploadDirectory >> Starting Upload " + System.nanoTime());
		checkAscpThreshold();

		// Destination bucket and source path must be specified
		if (bucketName == null || bucketName.isEmpty())
			throw new SdkClientException("Bucket name has not been specified for upload");
		if (directory == null || !directory.exists())
			throw new SdkClientException("localFileName has not been specified for upload");
		if (virtualDirectoryKeyPrefix == null || virtualDirectoryKeyPrefix.isEmpty())
			throw new SdkClientException("remoteFileName has not been specified for upload");
		
		// Submit upload to thread pool
		AsperaUploadDirectoryCallable uploadDirectoryCallable =  new AsperaUploadDirectoryCallable(this, bucketName, directory, virtualDirectoryKeyPrefix, sessionDetails, includeSubdirectories, progressListener);
		Future<AsperaTransaction> asperaTransaction = executorService.submit(uploadDirectoryCallable);
		
		log.trace("AsperaTransferManager.uploadDirectory << Ending Upload " + System.nanoTime());
		// Return AsperaTransaction
		return asperaTransaction;

		
	}
	
	/**
	 * Process the transfer spec to call the underlying Aspera libraries to begin the transfer
	 * 
	 * @param transferSpecStr
	 * @param bucketName
	 * @param key
	 * @param fileName
	 * @return
	 */
	public AsperaTransaction processTransfer(String transferSpecStr, String bucketName, String key, String fileName, ProgressListener progressListener) {
		// Generate session id
		String xferId = UUID.randomUUID().toString();
	
		AsperaTransactionImpl asperaTransaction = null;

		try {
			TransferProgress transferProgress = new TransferProgress();

	        S3ProgressListenerChain listenerChain = new S3ProgressListenerChain(
	                new TransferProgressUpdatingListener(transferProgress),
	                progressListener);
	        
	        log.trace("AsperaTransferManager.processTransfer >> creating AsperaTransactionImpl " + System.nanoTime());
	        asperaTransaction = new AsperaTransactionImpl(xferId, bucketName, key, fileName, transferProgress, listenerChain);
	        log.trace("AsperaTransferManager.processTransfer << creating AsperaTransactionImpl " + System.nanoTime());
	        asperaFaspManagerWrapper.setAsperaTransaction(asperaTransaction);
	        
	        log.trace("AsperaTransferManager.processTransfer >> start transfer " + System.nanoTime());
			asperaFaspManagerWrapper.startTransfer(xferId, transferSpecStr);
			log.trace("AsperaTransferManager.processTransfer >> end transfer " + System.nanoTime());
	
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return asperaTransaction;
	}
	
	/**
	 * Check the LRU cache to see if the Aspera Key has already been retrieved for
	 * this bucket. If it has, return it, else call onto the s3Client to get the
	 * FASPConnectionInfo for the bucket name
	 * 
	 * @param bucketName
	 * @return
	 */
	public FASPConnectionInfo getFaspConnectionInfo(String bucketName) {
		log.trace("AsperaTransferManager.getFaspConnectionInfo >> start " + System.nanoTime());
		FASPConnectionInfo faspConnectionInfo = akCache.get(bucketName);

		if (null == faspConnectionInfo) {

			log.trace("AsperaTransferManager.getFaspConnectionInfo >> retrieve from COS " + System.nanoTime());
			faspConnectionInfo = s3Client.getBucketFaspConnectionInfo(bucketName);
			log.trace("AsperaTransferManager.getFaspConnectionInfo << retrieve from COS " + System.nanoTime());

			if (null == faspConnectionInfo) {
				throw new SdkClientException("Failed to retrieve faspConnectionInfo for bucket: " + bucketName);
			}

			akCache.put(bucketName, faspConnectionInfo);
		}

		log.trace("AsperaTransferManager.getFaspConnectionInfo << end " + System.nanoTime());
		return faspConnectionInfo;
	}

	//TODO possibly move to separate class
	public TransferSpecs getTransferSpec(FASPConnectionInfo faspConnectionInfo, String localFileName, String remoteFileName, String direction)
			throws SdkClientException, AmazonServiceException {
		log.trace("AsperaTransferManager.getTransferSpec >> start " + System.nanoTime());
		TransferSpecs transferSpecs = null;
		
		URL ats_url = null;
		try {
			ats_url = new URL(faspConnectionInfo.getAtsEndpoint());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		URI ats_uri = null;
		try {
			ats_uri = ats_url.toURI();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.trace("AsperaTransferManager.getTransferSpec >> retrieve delegate token " + System.nanoTime());
		Token token = new Token().withDelegate_token(tokenManager.getToken());
		log.trace("AsperaTransferManager.getTransferSpec << retrieve delegate token " + System.nanoTime());
		
		log.trace("AsperaTransferManager.getTransferSpec >> prepare transfer request " + System.nanoTime());
		StorageCredentials storageCredentials = new StorageCredentials().withType("token")
				.withToken(token);
		Node node = new Node().withStorage_credentials(storageCredentials);
		Aspera aspera = new Aspera().withNode(node);
		Tags tags = new Tags().withAspera(aspera);
		List<Path> paths = new ArrayList<Path>();
		Path path = new Path().withSource(localFileName)
				.withDestination(remoteFileName);
		paths.add(path);
		List<TransferRequest> transferRequests = new ArrayList<TransferRequest>();
		TransferRequest transferRequest = new TransferRequest().withPaths(paths).withDestination_root("")
				.withTags(tags).withRemote_host(ats_uri.getHost());
		transferRequests.add(transferRequest);
		AsperaTransferSpecRequest transferSpecRequest = new AsperaTransferSpecRequest()
				.withTransfer_requests(transferRequests);

		ObjectMapper mapper = new ObjectMapper();

		String jsonTransferRequest = null;
		try {
			jsonTransferRequest = mapper.writeValueAsString(transferSpecRequest);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.trace("AsperaTransferManager.getTransferSpec << prepare transfer request " + System.nanoTime());

		// Post transfer spec request
		// TODO need to manage retries.
		try {
			log.trace("AsperaTransferManager.getTransferSpec >> prepare post request " + System.nanoTime());
			SSLContext sslContext;
			/*
			 * If SSL cert checking for endpoints has been explicitly disabled, register a
			 * new scheme for HTTPS that won't cause self-signed certs to error out.
			 */
			if (SDKGlobalConfiguration.isCertCheckingDisabled()) {
				if (log.isWarnEnabled()) {
					log.warn("SSL Certificate checking for endpoints has been " + "explicitly disabled.");
				}
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { new TrustingX509TrustManager() }, null);
			} else {
				sslContext = SSLContexts.createDefault();
			}

			SSLConnectionSocketFactory sslsf = new SdkTLSSocketFactory(sslContext, new DefaultHostnameVerifier());

			HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();

			HttpPost post = new HttpPost(faspConnectionInfo.getAtsEndpoint() + "/files/" + direction + "_setup");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String authStr = faspConnectionInfo.getAccessKeyId() + ":" + faspConnectionInfo.getAccessKeySecret();
			post.setHeader("Authorization", "Basic " + DatatypeConverter.printBase64Binary(authStr.getBytes("UTF-8")));
			post.setHeader("Accept", "application/json");
			post.setHeader("X-Aspera-Storage-Credentials",
					"{\"type\": \"token\", \"token\" : {\"delegated_refresh_token\":\"" + tokenManager.getToken() + "\"}}");

			StringEntity transferParams = new StringEntity(jsonTransferRequest);
			transferParams.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

			post.setEntity(transferParams);
			log.trace("AsperaTransferManager.getTransferSpec << prepare post request " + System.nanoTime());

			log.trace("AsperaTransferManager.getTransferSpec >> post request " + System.nanoTime());
			final HttpResponse response = client.execute(post);
			log.trace("AsperaTransferManager.getTransferSpec << post request " + System.nanoTime());

			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("Response code= " + response.getStatusLine().getStatusCode() + ", Reason= "
						+ response.getStatusLine().getReasonPhrase() + ".Throwing AsperaTransferException");
				AsperaTransferException exception = new AsperaTransferException("Failed to get Aspera Transfer Spec");
				exception.setStatusCode(response.getStatusLine().getStatusCode());
				exception.setStatusMessage(response.getStatusLine().getReasonPhrase());
				throw exception;
			}

			final HttpEntity entity = response.getEntity();
			final String resultStr = EntityUtils.toString(entity);
			
			log.trace("AsperaTransferManager.getTransferSpec >> mapping transfer spec " + System.nanoTime());
			transferSpecs = mapper.readValue(resultStr, TransferSpecs.class);
			log.trace("AsperaTransferManager.getTransferSpec << mapping transfer spec " + System.nanoTime());

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		log.trace("AsperaTransferManager.getTransferSpec << end " + System.nanoTime());
		return transferSpecs;
	}
	
	/**
	 * Modify the retrieved TransferSpec and apply an '-all' suffix to
	 * the subdomain on the remote)host field
	 * 
	 * @param sessionDetails
	 * @param transferSpecs
	 */
	public void checkMultiSessionAllGlobalConfig(TransferSpecs transferSpecs) {
		if(asperaTransferManagerConfig.isMultiSession()){
			for(TransferSpec transferSpec : transferSpecs.transfer_specs) {
					//If multisession defined as global use 'all' suffix, else check if a number has been specified
					transferSpec.setRemote_host(updateRemoteHost(transferSpec.getRemote_host()));
			}
		}
	}

	/**
	 * Modify the retrieved TransferSpec with the customised AsperaConfig object
	 * created by the user
	 * 
	 * @param sessionDetails
	 * @param transferSpecs
	 */
	public void modifyTransferSpec(AsperaConfig sessionDetails, TransferSpecs transferSpecs) {
		for(TransferSpec transferSpec : transferSpecs.transfer_specs) {
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getTargetRateKbps())))transferSpec.setTarget_rate_kbps(sessionDetails.getTargetRateKbps());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getTargetRateCapKbps())))transferSpec.setTarget_rate_cap_kbps(sessionDetails.getTargetRateCapKbps());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getMinRateCapKbps())))transferSpec.setMin_rate_cap_kbps(sessionDetails.getMinRateCapKbps());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getMinRateKbps())))transferSpec.setMin_rate_kbps(sessionDetails.getMinRateKbps());
			if (!StringUtils.isNullOrEmpty(sessionDetails.getRatePolicy()))transferSpec.setRate_policy(sessionDetails.getRatePolicy());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.isLockMinRate())))transferSpec.setLock_min_rate(sessionDetails.isLockMinRate());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.isLockTargetRate())))transferSpec.setLock_target_rate(sessionDetails.isLockTargetRate());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.isLockRatePolicy())))transferSpec.setLock_rate_policy(sessionDetails.isLockRatePolicy());
			if (!StringUtils.isNullOrEmpty(sessionDetails.getDestinationRoot()))transferSpec.setDestination_root(sessionDetails.getDestinationRoot());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getMultiSession())))transferSpec.setMulti_session(sessionDetails.getMultiSession());
			if (!StringUtils.isNullOrEmpty(String.valueOf(sessionDetails.getMultiSessionThreshold())))transferSpec.setMulti_session_threshold(sessionDetails.getMultiSessionThreshold());
		}
	}

	/**
	 * List all files within the folder to exclude all subdirectories & modify the transfer spec to 
	 * pass onto Aspera SDK
	 * 
	 * @param directory
	 * @param transferSpecs
	 */
	public void excludeSubdirectories(File directory, TransferSpecs transferSpecs) {

		if ( directory == null || !directory.exists() || !directory.isDirectory() ) {
            throw new IllegalArgumentException("Must provide a directory to upload");
        }

		List<File> files = new LinkedList<File>();
        listFiles(directory, files);

        for(TransferSpec transferSpec : transferSpecs.transfer_specs) {

        	Vector<NodePath> paths = new Vector<NodePath>();
            for (File f : files) {
            	NodePath path = new NodePath();
            	path.setDestination(f.getName());
            	path.setSource(f.getAbsolutePath());
            	paths.add(path);
            }
            transferSpec.setPaths(paths);
		}

	}

    /**
     * Lists files in the directory given and adds them to the result list
     * passed in, optionally adding subdirectories recursively.
     */
    private void listFiles(File dir, List<File> results) {
        File[] found = dir.listFiles();
        if ( found != null ) {
            for ( File f : found ) {
                if (f.isDirectory()) {
                	//do nothing
                } else {
                    results.add(f);
                }
            }
        }
    }

	/**
	 * Update the remoteHost parameter with the suffix "-all" within the subdomain name
	 * 
	 * @param remoteHost
	 */
	private String updateRemoteHost(String remoteHost){

		String [] splitStr = remoteHost.split("\\.");
		remoteHost = new StringBuilder(remoteHost).insert(splitStr[0].length(), "-all").toString();
		return remoteHost;
	}
	
	/**
	 * Setter to overwrite the TokenManager created during initialisation
	 * 	
	 * @param tokenManager
	 */
	private void setTokenManager(TokenManager tokenManager){
		this.tokenManager = tokenManager;
	}

	/**
	 * Allows a user to overwrite the TokenManager with their own. This will mainly be utilised for
	 * dev/test environments. This method is to be used within {@link AsperaTransferManagerBuilder}
	 *  
	 * @param tokenManager
	 * @return
	 */
	protected AsperaTransferManager withTokenManager(TokenManager tokenManager) {
		setTokenManager(tokenManager);
		return this;
	}

	/**
	 * Setter to overwrite the default config on the AsperaTransferManager
	 * 
	 * @param asperaTransferManagerConfig
	 */
	private void setAsperaTransferManagerConfig(AsperaTransferManagerConfig asperaTransferManagerConfig) {
		this.asperaTransferManagerConfig = asperaTransferManagerConfig;
	}

	/**
	 * Allows a user to overwrite the AsperaTransferManager with their own customised properties. 
	 * This method is to be used within {@link AsperaTransferManagerBuilder}
	 *  
	 * @param asperaTransferManagerConfig
	 * @return
	 */
	protected AsperaTransferManager withAsperaTransferManagerConfig(AsperaTransferManagerConfig asperaTransferManagerConfig) {
		setAsperaTransferManagerConfig(asperaTransferManagerConfig);
		return this;
	}
	
	/**
     * Shutdown without interrupting the threads involved, so that, for example,
     * any upload in progress can complete without throwing
     * {@link AbortedException}.
     */
    public void shutdownThreadPools() {
            executorService.shutdown();
    }
	
	/**
     * Releasing all resources created by <code>TransferManager</code> before it
     * is being garbage collected.
     */
    @Override
    protected void finalize() throws Throwable {
        shutdownThreadPools();
    }

    /**
     * Check if ascp count has hit limit
     * If it has throw an exception
     */
    protected void checkAscpThreshold() {

		if (TransferListener.getAscpCount() >= asperaTransferManagerConfig.getAscpMaxConcurrent()) {
			log.error("ASCP process threshold has been reached, there are currently " + TransferListener.getAscpCount() + " processes running");
			throw new AsperaTransferException("ASCP process threshold has been reached");
		}
    }
}
