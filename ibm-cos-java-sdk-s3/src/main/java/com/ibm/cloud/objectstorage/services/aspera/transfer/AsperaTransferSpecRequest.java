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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;

/**
 * Request object to fetch FASP Upload/Download Transfer Spec.
 *
 * @see AmazonS3#getFaspTransferSpec(GetBucketFaspTransferSpecRequest)
 */
public class AsperaTransferSpecRequest  {
	private List<TransferRequest> transfer_requests;

	public List<TransferRequest> getTransfer_requests() {
		return transfer_requests;
	}

	public void setTransfer_requests(List<TransferRequest> transfer_requests) {
		this.transfer_requests = transfer_requests;
	}
	
	public AsperaTransferSpecRequest withTransfer_requests(List<TransferRequest> transfer_requests) {
		this.transfer_requests = transfer_requests;
		return this;
	}
}

@JsonTypeName("transfer_request")
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT,use= JsonTypeInfo.Id.NAME)
class TransferRequest {
	private List<Path> paths;
	private String destination_root;
	private String remote_host;
	private Tags tags;
	
	public List<Path> getPaths() {
		return paths;
	}
	
	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}
	
	public TransferRequest withPaths(List<Path> paths) {
		this.paths = paths;
		return this;
	}
	
	public String getDestination_root() {
		return destination_root;
	}
	
	public void setDestination_root(String destination_root) {
		this.destination_root = destination_root;
	}
	
	public TransferRequest withDestination_root(String destination_root) {
		this.destination_root = destination_root;
		return this;
	}
	
	public Tags getTags() {
		return tags;
	}
	
	public void setTags(Tags tags) {
		this.tags = tags;
	}
	
	public TransferRequest withTags(Tags tags) {
		this.tags = tags;
		return this;
	}

	public String getRemote_host() {
		return remote_host;
	}

	public void setRemote_host(String remote_host) {
		this.remote_host = remote_host;
	}
	
	public TransferRequest withRemote_host(String remote_host) {
		this.remote_host = remote_host;
		return this;
	}
}

class Path {
	private String source;
	private String destination;
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Path withSource(String source) {
		this.source = source;
		return this;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public Path withDestination(String destination) {
		this.destination = destination;
		return this;
	}
}

class Tags {
	private Aspera aspera;

	public Aspera getAspera() {
		return aspera;
	}

	public void setAspera(Aspera aspera) {
		this.aspera = aspera;
	}
	
	public Tags withAspera(Aspera aspera) {
		this.aspera = aspera;
		return this;
	}
}

class Aspera {
	private Node node;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public Aspera withNode(Node node) {
		this.node = node;
		return this;
	}
}

class Node {
	private StorageCredentials storage_credentials;

	public StorageCredentials getStorage_credentials() {
		return storage_credentials;
	}

	public void setStorage_credentials(StorageCredentials storage_credentials) {
		this.storage_credentials = storage_credentials;
	}
	
	public Node withStorage_credentials(StorageCredentials storage_credentials) {
		this.storage_credentials = storage_credentials;
		return this;
	}
}

class StorageCredentials {
	private String type;
	private Token token;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public StorageCredentials withType(String type) {
		this.type = type;
		return this;
	}
	
	public Token getToken() {
		return token;
	}
	
	public void setToken(Token token) {
		this.token = token;
	}
	
	public StorageCredentials withToken(Token token) {
		this.token = token;
		return this;
	}
}

class Token {
	private String  delegated_refresh_token;

	public String getDelegated_refresh_token() {
		return delegated_refresh_token;
	}

	public void setDelegated_refresh_token(String delegated_refresh_token) {
		this.delegated_refresh_token = delegated_refresh_token;
	}

	public Token withDelegate_token(String token) {
		this.delegated_refresh_token = token;
		return this;
	}
}