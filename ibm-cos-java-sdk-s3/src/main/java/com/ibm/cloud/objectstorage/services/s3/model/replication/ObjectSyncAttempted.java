/* 
* Copyright 2025 IBM Corp. All Rights Reserved. 
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
package com.ibm.cloud.objectstorage.services.s3.model.replication;

import java.util.Date;

/**
 * Base class to represent the root predicate in
 * {@link ContentList} class.

 */

public class ObjectSyncAttempted {

   /**
     * Name of object that has failed sync.
     */
    private String key;
   /**
    * UUID of object version that has failed sync."
    */
    private String versionId;

   /**
    * >The type of sync that has failed. This shall be either <code> content </code> , <code> metadata </code> or <code> objectLock </code>.</p>
    */
    private String syncType;

   /**
    * Timestamp of last sync attempt from primary sync queue.
    */
    private Date firstSyncAttempted;

   /**
    * Timestamp of most recent sync attempt from failure sync queue.
    */
    private Date lastSyncAttempted;

   /**
    * Descriptive message regarding the cause of the most recent sync attempt failure.
    */
    private String syncFailureCause;


    /**
     * Returns the key of Amazon S3 bucket.
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key

     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @param key

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withKey(
            String key) {
        setKey(key);
        return this;
    }

    /**
     * Returns the key of Amazon S3 bucket.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     *
     * @param versionId

     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * @param key

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withVersionId(
            String versionId) {
        setVersionId(versionId);
        return this;
    }

    /**
     * Returns the key of Amazon S3 bucket.
     */
    public String getSyncType() {
        return syncType;
    }

    /**
     *
     * @param syncType

     */
    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    /**
     * @param syncType

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withSyncType(
            String syncType) {
        setKey(syncType);
        return this;
    }
  
    public Date getFirstSyncAttempted() {
        return firstSyncAttempted;
    }

    /**
     *
     * @param firstSyncAttempted

     */
    public void setFirstSyncAttempted(Date firstSyncAttempted) {
        this.firstSyncAttempted = firstSyncAttempted;
    }

    /**
     * @param firstSyncAttempted

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withFirstSyncAttempted(
            Date firstSyncAttempted) {
        setFirstSyncAttempted(firstSyncAttempted);
        return this;
    }

    public Date getLastSyncAttempted() {
        return lastSyncAttempted;
    }

    /**
     *
     * @param lastSyncAttempted

     */
    public void setLastSyncAttempted(Date lastSyncAttempted) {
        this.lastSyncAttempted = lastSyncAttempted;
    }

    /**
     * @param lastSyncAttempted

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withLastSyncAttempted(
            Date lastSyncAttempted) {
        setLastSyncAttempted(lastSyncAttempted);
        return this;
    }

      public String getSyncFailureCause() {
        return syncFailureCause;
    }

    /**
     *
     * @param syncFailureCause

     */
    public void setSyncFailureCause(String syncFailureCause) {
        this.syncFailureCause = syncFailureCause;
    }

    /**
     * @param syncFailureCause

     * @return The updated {@link ObjectSyncAttempted}
     *         object.
     */
    public ObjectSyncAttempted withSyncFailureCause(
            String syncFailureCause) {
        setSyncFailureCause(syncFailureCause);
        return this;
    }


}
