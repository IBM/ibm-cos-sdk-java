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

import com.ibm.cloud.objectstorage.AmazonWebServiceClient;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.DelegateTokenProvider;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Fluent builder for {@link AsperaTransferManager}. Use of the builder is preferred over constructors in
 * the AsperaTransferManager class.
 **/
public class AsperaTransferManagerBuilder {
	
	/**AsperConfig to override any default options in the transfer spec**/
	private AsperaConfig asperaConfig = null;
	
	/**token manager interface retrieve the delegate token**/
	private TokenManager tokenManager;
	
	/**token provider interface, the implementation will make the http request with appropriate receiver client id & apiKey**/
	private TokenProvider asperaTokenProvider;

	/**Default Token Manager for token storage & management**/
	private DefaultTokenManager defaultTokenManager;

	/**s3Client to retrieve FaspConnectionInfo per bucket**/
	private AmazonS3 s3Client;

	/**Config to customise the AsperaTransferManager**/
	private AsperaTransferManagerConfig asperaTransferManagerConfig;

	public AsperaTransferManagerBuilder (String apiKey, AmazonS3 s3Client) {

		//Confirm apiKey & s3Client are not null
		if (apiKey == null) throw new SdkClientException("apiKey has not been set for AsperaTransferManager");
		if (s3Client == null) throw new SdkClientException("s3Client has not been set for AsperaTransferManager");

		this.asperaTokenProvider = new DelegateTokenProvider(apiKey);
		//Ascertain the configuration set by the s3Client & apply it to the tokenmanager
		defaultTokenManager = new DefaultTokenManager(asperaTokenProvider);
		if (s3Client instanceof AmazonWebServiceClient){
			AmazonWebServiceClient amazonS3Client = (AmazonWebServiceClient)s3Client;
			defaultTokenManager.setClientConfiguration(amazonS3Client.getClientConfiguration());
		}

		this.s3Client = s3Client;
		this.tokenManager = defaultTokenManager;
		this.asperaTransferManagerConfig = new AsperaTransferManagerConfig();
	}

	public AsperaTransferManager build() {

		AsperaTransferManager transferManager = new AsperaTransferManager(this.s3Client, this.tokenManager, this.asperaConfig, this.asperaTransferManagerConfig);

		return transferManager;
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
	 * dev/test environments.
	 *  
	 * @param tokenManager
	 * @return
	 */
	public AsperaTransferManagerBuilder withTokenManager(TokenManager tokenManager) {
		setTokenManager(tokenManager);
		return this;
	}

	/**
	 * Setter to overwrite the asperaTransferManagerConfig created during initialisation
	 * @param asperaTransferManagerConfig
	 */
	private void setAsperaTransferManagerConfig(AsperaTransferManagerConfig asperaTransferManagerConfig) {
		this.asperaTransferManagerConfig = asperaTransferManagerConfig;
	}

	/**
	 * Allows a user to overwrite the asperaTransferManagerConfig created during initialisation
	 * @param asperaTransferManagerConfig
	 */
	public AsperaTransferManagerBuilder withAsperaTransferManagerConfig(AsperaTransferManagerConfig asperaTransferManagerConfig) {
		setAsperaTransferManagerConfig(asperaTransferManagerConfig);
		return this;
	}

	/**
	 * Setter to overwrite the asperaConfig created during initialisation
	 * 
	 * @param asperaConfig
	 */
	private void setAsperaConfig(AsperaConfig asperaConfig) {
		this.asperaConfig = asperaConfig;
	}

	/**
	 * Allows a user to overwrite the asperaConfig created during initialisation
	 * @param asperaConfig
	 */

	public AsperaTransferManagerBuilder withAsperaConfig(AsperaConfig asperaConfig) {
		setAsperaConfig(asperaConfig);
		return this;
	}
}
