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

public class AsperaResultImpl implements AsperaResult {

	private final String bucketName;
	private final String key;
	private final String fileName;

	public AsperaResultImpl(String bucketName, String key, String fileName){
		this.bucketName = bucketName;
		this.key = key;
		this.fileName = fileName;
	}

	@Override
	public String getBucketName() {
		return this.bucketName;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}
}
