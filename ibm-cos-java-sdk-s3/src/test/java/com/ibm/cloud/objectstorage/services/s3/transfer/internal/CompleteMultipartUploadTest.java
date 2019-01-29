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

import static org.mockito.Mockito.*;
import org.junit.Test;

public class CompleteMultipartUploadTest {
	
	/**
	 * Tests that the Exception is thrown within CompleteMultipartUpload.Call() & the subsequent call
	 * to monitor.reportFailure() is made to update the isDone status of the transfer
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testReportFailureIsCalled() throws Exception{

		UploadMonitor monitor = mock(UploadMonitor.class);
		CompleteMultipartUpload completeMultipartCopy = new CompleteMultipartUpload(null, null, null, null, null, null, monitor);
		try{
			completeMultipartCopy.call();
		} catch (Exception e){
			
		}
		verify(monitor, times(1)).uploadFailure();
	}

}
