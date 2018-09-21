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

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;

/**
 * LRU Cache implementation for Aspera Keys. Keys are stored against the bucket
 * name and are removed from LinkedHashMap based on least recently used.
 * The Cache is configured to hold a maximum of 1000 records. 
 * 
 */
public class AsperaKeyCache extends LinkedHashMap<String, FASPConnectionInfo> {

	private static final long serialVersionUID = 5474075814087652410L;

	private int capacity;

	public AsperaKeyCache(int capacity) {
		super(capacity, 1.0F, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<String, FASPConnectionInfo> eldest) {
		return this.size() > capacity;
	}
}
