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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.aspera.faspmanager2.faspmanager2;

/**
 * Utility class to support extracting and loading native Aspera
 * assets (ascp, dynamic libraries, configuration) from the cos-aspera dependency
 */
public final class AsperaLibraryLoader {
	protected static Log log = LogFactory.getLog(AsperaLibraryLoader.class);

	private static final List<String> MAC_DYNAMIC_LIBS = Collections.unmodifiableList(
			Arrays.asList("libfaspmanager2.jnilib", "_faspmanager2.so"));
	private static final List<String> UNIX_DYNAMIC_LIBS = Collections.unmodifiableList(
			Arrays.asList("libfaspmanager2.so"));
	private static final List<String> WINDOWS_DYNAMIC_LIBS = Collections.unmodifiableList(
			Arrays.asList("faspmanager2.dll"));
	private static final String SEPARATOR = System.getProperty("file.separator");
	private static final File EXTRACT_LOCATION_ROOT =
			new File(System.getProperty("user.home") +
					SEPARATOR +
					".aspera" +
					SEPARATOR +
					"cos-aspera");

	/**
	 * Prepares and loads the Aspera library in preparation for its use. May conditionally
	 * extract Aspera library jar contents to a location on the local filesystem, determined
	 * by a combination of the User's home directory and library version. Loads the underlying
	 * dynamic library for use by the Aspera library Java bindings
	 */
	public static String load() {
		JarFile jar = null;
		String location = null;
		log.warn("Using Aspera through the COS SDK is deprecated. Refer to the project readme: https://github.com/IBM/ibm-cos-sdk-java");
		try {
			jar = createJar();
			String version = jarVersion(jar);
			location = EXTRACT_LOCATION_ROOT + SEPARATOR + version;
			File extractedLocation = new File(location);
			if (!extractedLocation.exists()) {
				extractJar(jar, extractedLocation);
			}
			loadLibrary(extractedLocation, osLibs());
		} catch (Exception e) {
			throw new AsperaLibraryLoadException("Unable to load Aspera Library", e);
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					log.warn("Unable to close Aspera jar file after loading", e);
				}
			}
		}

		return location;
	}

	/**
	 * Creates an instance of JarFile which references the Aspera library
	 * @return Aspera Library jar file
	 * @throws IOException if unable to create a JarFile reference
	 * @throws URISyntaxException if the location to the Aspera library is invalid
	 */
	public static JarFile createJar() throws IOException, URISyntaxException {
		URL location = faspmanager2.class.getProtectionDomain().getCodeSource().getLocation();
		return new JarFile(new File(location.toURI()));
	}

	/**
	 * Determines the version associated with this jar
	 * @param jar Source jar to query version information from
	 * @return The source jar's version
	 * @throws IOException if unable to successfully query source jar
	 */
	public static String jarVersion(JarFile jar) throws IOException {
		String version = jar.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);

		if (version == null) {
			version = String.format("%d", System.currentTimeMillis());
		}
		return version;
	}

	/**
	 * Creates a 1:1 copy of all contents from a jar file to the specified location on the local filesystem
	 * @param jar The source jar to extract contents from
	 * @param extractedLocation The target location for extracted jar contents
	 * @throws IOException if jar extraction fails IO
	 */
	public static void extractJar(JarFile jar, File extractedLocation) throws IOException {
		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			File destPath = new File(extractedLocation, SEPARATOR + entry.getName());

			// is Directory
			if (entry.isDirectory()) {
				log.debug("Creating directory: " + destPath);
				destPath.mkdirs();
				continue;
			}

			// is File
			log.debug("Creating parent directories for file: " + destPath);
			destPath.getParentFile().mkdirs();
			extractFile(jar, entry, destPath);
		}
	}

	/**
	 * Extracts a jar entry from a jar file to a target location on the local file system
	 * @param jar The jar in which the desired file resides
	 * @param entry The desired entry (file) to extract from the jar
	 * @param destPath The target location to extract the jar entry to
	 * @throws IOException if any IO failure occurs during file extraction
	 */
	public static void extractFile(JarFile jar, JarEntry entry, File destPath) throws IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = jar.getInputStream(entry);
			out = new FileOutputStream(destPath);
			byte[] buf = new byte[1024];
	        for (int i = in.read(buf); i != -1; i = in.read(buf))
	        {
	           out.write(buf, 0, i);
	        }
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}

		//Ensure persmissions are set correct on ascp, has to be done after the File has been created
		if (entry.getName().equals("ascp")) {
			destPath.setExecutable(true);
			destPath.setWritable(true);
		}
	}


	/**
	 * Determine which os the jvm is running on
	 * @return 	the os specific list of native files to load
	 */
	public static List<String> osLibs() {
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("win") >= 0){
			return WINDOWS_DYNAMIC_LIBS;
		} else if (OS.indexOf("mac") >= 0) {
			return MAC_DYNAMIC_LIBS;
		} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) {
			return UNIX_DYNAMIC_LIBS;
		} else {
			throw new AsperaLibraryLoadException("OS is not supported for Aspera");
		}
	}

	/**
	 * Loads a dynamic library into the JVM from a list of candidates
	 * @param extractedPath The base directory where candidate dynamic libraries reside
	 * @param candidates A list of candidate dynamic library names for various platforms
	 * @return 	true if one of the candidate libraries was loaded successfully, else false
	 */
	public static void loadLibrary(File extractedPath, List<String> candidates) {
		for (String lib : candidates) {
			File libPath = new File(extractedPath, lib);
			String absPath = libPath.getAbsolutePath();
			log.debug("Attempting to load dynamic library: " + absPath);
			try {
				System.load(absPath);
				log.info("Loaded dynamic library: " + absPath);
				return;
			} catch (UnsatisfiedLinkError e) {
				log.debug("Unable to load dynamic library: " + absPath, e);
			}
		}
		throw new RuntimeException("Failed to load Aspera dynamic library from candidates " + candidates + " at location: " + extractedPath);
	}

	/**
	 * An exception that represents a failed attempt to load the Aspera library
	 */
	public static class AsperaLibraryLoadException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
	     * Creates a new AsperaLibraryLoadException with the specified message.
	     *
	     * @param message
	     *            An error message describing why this exception was thrown.
	     */
		public AsperaLibraryLoadException(String message) {
	        super(message);
	    }

		/**
	     * Creates a new AsperaLibraryLoadException with the root cause.
	     *
	     * @param cause
	     *          The underlying cause of this exception.
	     */
	    public AsperaLibraryLoadException(Throwable cause) {
	        super(cause);
	    }

	    /**
	     * Creates a new AsperaLibraryLoadException with the specified message, and root
	     * cause.
	     *
	     * @param message
	     *            An error message describing why this exception was thrown.
	     * @param cause
	     *            The underlying cause of this exception.
	     */
	    public AsperaLibraryLoadException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
}
