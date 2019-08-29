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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;

public class AsperaKeyCacheTest {

	AsperaKeyCache lruCache;
	FASPConnectionInfo faspConnectionInfo1 = getFaspConnectionInfo("1");
	FASPConnectionInfo faspConnectionInfo2 = getFaspConnectionInfo("2");
	FASPConnectionInfo faspConnectionInfo3 = getFaspConnectionInfo("3");
	FASPConnectionInfo faspConnectionInfo4 = getFaspConnectionInfo("4");

	/**
	 * Test the LRU cache is updated as expected
	 * 
	 */	
	@Test
	public void testAccessOrderChangesWhenKeyIsAccessed() {

		lruCache = new AsperaKeyCache(1000);

		lruCache.put(Integer.toString(1), faspConnectionInfo1);
	    lruCache.put(Integer.toString(2), faspConnectionInfo2);
	    lruCache.put(Integer.toString(3), faspConnectionInfo3);
	    lruCache.get("1");
	    lruCache.put(Integer.toString(4), faspConnectionInfo4);
		int count = 0;

	    for (Iterator<Entry<String, FASPConnectionInfo>> it = lruCache.entrySet().iterator(); it.hasNext();){
	    	Entry<String, FASPConnectionInfo> entry = it.next();
	        count ++;
	        
	        if (count == 3) {
	        	assertEquals("1", entry.getValue().getAccessKeyId());
	        }
	      }
	}

	/**
	 * Test the LRU cache is updated as expected and when the max
	 * size is exceeded the least used record is removed
	 * 
	 */	
	@Test
	public void testExceededCacheRemovesLastUsedKey() {

		lruCache = new AsperaKeyCache(1000);
		
		for (int i=0; i<=1001;i++) {
			lruCache.put(Integer.toString(i),getFaspConnectionInfo(Integer.toString(i)));
		}

		assertFalse("Cache still contains the 1st record", lruCache.containsKey("1"));
		assertTrue("Cache should still contain the 2nd record", lruCache.containsKey("2"));
		assertTrue("Cache should still contain the last record", lruCache.containsKey("1001"));
	}

	/**
	 * Test the LRU cache is updated as expected and when the max
	 * size is reached the least used record is removed
	 * 
	 */	
	@Test
	public void testMaxedCacheKeepsEarliestEntry() {

		lruCache = new AsperaKeyCache(1000);
		
		for (int i=0; i<=1000;i++) {
			lruCache.put(Integer.toString(i),getFaspConnectionInfo(Integer.toString(i)));
		}

		assertTrue("Cache should still contain the 1st record", lruCache.containsKey("1"));
		assertTrue("Cache should still contain the last record", lruCache.containsKey("1000"));
		assertFalse("Cache does not contain over 1000 records", lruCache.containsKey("1001"));
	}

	/**
	 * Test the LRU cache does not exceed its max set size of 1000 records
	 * 
	 */	
	@Test
	public void testMaxSizeIs1000Records() {

		lruCache = new AsperaKeyCache(1000);
		
		for (int i=0; i<=2000;i++) {
			lruCache.put(Integer.toString(i),getFaspConnectionInfo(Integer.toString(i)));
		}

		assertTrue(lruCache.size() == 1000);
	}

	/**
	 * Test the LRU cache does not exceed its max set size of 1000 records
	 * 
	 */	
	@Test
	public void testMaxSizeCanBeSet() {

		lruCache = new AsperaKeyCache(10);
		
		for (int i=0; i<=2000;i++) {
			lruCache.put(Integer.toString(i),getFaspConnectionInfo(Integer.toString(i)));
		}

		assertTrue(lruCache.size() == 10);
	}

	/**
	 * Test the LRU cache is updated as expected
	 * 
	 */	
	@Test
	public void testBucketsAndKeysAreStored() {

		lruCache = new AsperaKeyCache(1000);

		lruCache.put("images", faspConnectionInfo1);
	    lruCache.put("music", faspConnectionInfo2);
	    lruCache.put("films", faspConnectionInfo3);
	    lruCache.get("images");
	    lruCache.put("soundbites", faspConnectionInfo4);
		int count = 0;

	    for (Iterator<Entry<String, FASPConnectionInfo>> it = lruCache.entrySet().iterator(); it.hasNext();){
	    	Entry<String, FASPConnectionInfo> entry = it.next();
	        count ++;
	        
	        if (count == 3) {
	        	assertEquals("1", entry.getValue().getAccessKeyId());
	        }
	      }
	}
	
	/*
	 * create & return a FASPConnectionIfo object
	 */
	private FASPConnectionInfo getFaspConnectionInfo(String id) {

		FASPConnectionInfo faspConnectionInfo = new FASPConnectionInfo();
		faspConnectionInfo.setAccessKeyId(id);
		faspConnectionInfo.setAccessKeySecret("Secret"+id);
		faspConnectionInfo.setAtsEndpoint("http://1.1.1."+id);
		
		return faspConnectionInfo;
	}
}
