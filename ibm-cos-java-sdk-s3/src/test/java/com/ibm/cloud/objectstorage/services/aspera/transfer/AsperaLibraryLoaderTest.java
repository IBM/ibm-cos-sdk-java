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
