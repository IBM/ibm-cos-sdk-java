/* 
* Copyright 2017 IBM Corp. All Rights Reserved. 
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
package com.ibm.cloud.objectstorage.services.s3.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;

public class MD5DigestCalculatingInputStreamTest {

	@Test
	public void testMD5CloneSupportedTrue() throws Exception {

		InputStream in  = mock(InputStream.class);
		doReturn(true).when(in).markSupported();;
		
		MD5DigestCalculatingInputStream md5Digest = new MD5DigestCalculatingInputStream(in);
		assertTrue(md5Digest.markSupported());
	}

	@Test
	public void testMD5CloneSupportedFalse() throws Exception {

		InputStream in  = mock(InputStream.class);
		doReturn(false).when(in).markSupported();;
		
		MD5DigestCalculatingInputStream md5Digest = new MD5DigestCalculatingInputStream(in);
		assertFalse(md5Digest.markSupported());
	}
}
