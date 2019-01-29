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
package com.ibm.cloud.objectstorage.services.s3.transfer.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.UUID;

import org.junit.Test;

import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.util.StringUtils;

public class DownloadPartCallableTest {

	/**
	 * Ensure Multi-part downloads through TransferManager now 
	 * use a shorter temporary file name to prevent file system 
	 * limitations with long file names. The file name should be in UUID format. 
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testFileNameConformsToUUIDFormat() throws Exception{
		
		AmazonS3 s3Client = mock(AmazonS3.class);
		ObjectMetadata metadata = mock(ObjectMetadata.class);
		GetObjectRequest request = new GetObjectRequest("bucket", "key");
		request.setPartNumber(2);
		String TEMP_FILE_MIDDLE_NAME = ".part.";
		String fileName = "/tmp/myNewFile";
		File myFile = new File(fileName);
	
		String expectedFileName = UUID.nameUUIDFromBytes(myFile.getName().getBytes(StringUtils.UTF8)).toString() +  
									TEMP_FILE_MIDDLE_NAME + request.getPartNumber().toString();
		
		DownloadPartCallable downloadCallable = new DownloadPartCallable(s3Client, request, myFile);
		doReturn(metadata).when(s3Client).getObject(any(GetObjectRequest.class), any(File.class));
		
		File downloadCallableFile = downloadCallable.call();

		assertEquals(expectedFileName.substring(0, 23), downloadCallableFile.getName().substring(0, 23));
		
	}
}
