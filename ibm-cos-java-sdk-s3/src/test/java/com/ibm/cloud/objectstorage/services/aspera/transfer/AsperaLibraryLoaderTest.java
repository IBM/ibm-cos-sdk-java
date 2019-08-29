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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.jar.JarFile;

import org.junit.Test;


public class AsperaLibraryLoaderTest {
	
	@Test 
	public void testJarIsCreatedSuccessfully() throws IOException, URISyntaxException {
		
		JarFile jar = AsperaLibraryLoader.createJar();
		
		assertNotNull(jar);
	}

	@Test
	public void testOSLibIsReturned() {

		String OS = System.getProperty("os.name").toLowerCase();
		List<String> osNativeLibraries = AsperaLibraryLoader.osLibs();

		assertNotNull(osNativeLibraries);
		
		if (OS.indexOf("win") >= 0){
			assertTrue(osNativeLibraries.contains("faspmanager2.dll"));
		} else if (OS.indexOf("mac") >= 0) {
			assertTrue(osNativeLibraries.contains("libfaspmanager2.jnilib"));
		} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) {
			assertTrue(osNativeLibraries.contains("libfaspmanager2.so"));
		}
	}
}
