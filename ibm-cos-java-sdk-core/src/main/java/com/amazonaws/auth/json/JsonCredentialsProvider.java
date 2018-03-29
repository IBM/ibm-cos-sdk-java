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
package com.amazonaws.auth.json;

import java.util.concurrent.Semaphore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.json.JsonConfigFile;

/**
 * Credentials provider based on IBM json configuration. This provider reads IBMCredentials from
 * the json configuration file.
 *
 * @see JsonConfigFile
 */
public class JsonCredentialsProvider implements AWSCredentialsProvider {

    /**
     * Default refresh interval
     */
    private static final long DEFAULT_REFRESH_INTERVAL_NANOS = 5 * 60 * 1000 * 1000 * 1000L;

    /**
     * Default force reload interval
     */
    private static final long DEFAULT_FORCE_RELOAD_INTERVAL_NANOS =
            2 * DEFAULT_REFRESH_INTERVAL_NANOS;

    /**
     * The credential json file from which this provider loads the security credentials. Lazily
     * loaded by the double-check idiom.
     */
    private volatile JsonConfigFile jsonConfigFile;

    /**
     * When the json file was last refreshed.
     */
    private volatile long lastRefreshed;

    /**
     * Used to have only one thread block on refresh, for applications making at least one call
     * every REFRESH_INTERVAL_NANOS.
     */
    private final Semaphore refreshSemaphore = new Semaphore(1);

    /**
     * Refresh interval. Defaults to {@link #DEFAULT_REFRESH_INTERVAL_NANOS}
     */
    private long refreshIntervalNanos = DEFAULT_REFRESH_INTERVAL_NANOS;

    /**
     * Force reload interval. Defaults to {@link #DEFAULT_FORCE_RELOAD_INTERVAL_NANOS}
     */
    private long refreshForceIntervalNanos = DEFAULT_FORCE_RELOAD_INTERVAL_NANOS;

    /**
     * Creates a new json credentials provider that returns the IBM security credentials.
     * Loading the credential file is deferred until the
     * getCredentials() method is called.
     */
    public JsonCredentialsProvider() {
        this((JsonConfigFile)null);
    }

    /**
     * Creates a new json credentials provider that returns the IBM security credentials.
     *
     * @param jsonConfigFilePath The file path where the json configuration file is located.
     */
    public JsonCredentialsProvider(String jsonConfigFilePath) {
        this(new JsonConfigFile(jsonConfigFilePath));
    }

    /**
     * Creates a new json credentials provider that returns the IBM security credentials.
     *
     * @param jsonConfigFile 	 The json configuration file containing the credentials used by this
     *                           credentials provider or null to defer load to first use.
     */
    public JsonCredentialsProvider(JsonConfigFile jsonConfigFile) {
        this.jsonConfigFile = jsonConfigFile;
        if (this.jsonConfigFile != null) {
            this.lastRefreshed = System.nanoTime();
        }
    }

    @Override
    public AWSCredentials getCredentials() {
        if (jsonConfigFile == null) {
            synchronized (this) {
                if (jsonConfigFile == null) {
                    jsonConfigFile = new JsonConfigFile();
                    lastRefreshed = System.nanoTime();
                }
            }
        }

        // Periodically check if the file on disk has been modified
        // since we last read it.
        //
        // For active applications, only have one thread block.
        // For applications that use this method in bursts, ensure the
        // credentials are never too stale.
        long now = System.nanoTime();
        long age = now - lastRefreshed;
        if (age > refreshForceIntervalNanos) {
            refresh();
        } else if (age > refreshIntervalNanos) {
            if (refreshSemaphore.tryAcquire()) {
                try {
                    refresh();
                } finally {
                    refreshSemaphore.release();
                }
            }
        }

        return jsonConfigFile.getCredentials();
    }

    @Override
    public void refresh() {
        if (jsonConfigFile != null) {
            jsonConfigFile.refresh();
            lastRefreshed = System.nanoTime();
        }
    }

    /**
     * Gets the refresh interval in nanoseconds.
     *
     * @return nanoseconds
     */
    public long getRefreshIntervalNanos() {
        return refreshIntervalNanos;
    }

    /**
     * Sets the refresh interval in nanoseconds.
     *
     * @param refreshIntervalNanos nanoseconds
     */
    public void setRefreshIntervalNanos(long refreshIntervalNanos) {
        this.refreshIntervalNanos = refreshIntervalNanos;
    }

    /**
     * Gets the forced refresh interval in nanoseconds.
     *
     * @return nanoseconds
     */
    public long getRefreshForceIntervalNanos() {
        return refreshForceIntervalNanos;
    }

    /**
     * Sets the forced refresh interval in nanoseconds.
     */
    public void setRefreshForceIntervalNanos(long refreshForceIntervalNanos) {
        this.refreshForceIntervalNanos = refreshForceIntervalNanos;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
