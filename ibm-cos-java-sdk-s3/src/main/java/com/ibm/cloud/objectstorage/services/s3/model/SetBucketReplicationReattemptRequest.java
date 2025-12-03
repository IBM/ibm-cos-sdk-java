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

package com.ibm.cloud.objectstorage.services.s3.model;

import java.io.Serializable;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.util.json.Jackson;

public class SetBucketReplicationReattemptRequest extends
        AmazonWebServiceRequest implements Serializable
        {

    /**
     * The name of Amazon S3 bucket to which the replication reattempt is
     * set.
     */
    private String bucketName;

    /**
     * Creates a new SetBucketReplicationReattemptRequest.
     */
    public SetBucketReplicationReattemptRequest() { }

    /**
     * Creates a new SetBucketReplicationReattemptRequest.
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            reattempt is set.
     */
    public SetBucketReplicationReattemptRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Returns the name of Amazon S3 bucket.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of Amazon S3 bucket for replication reattempt.
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            reattempt is set.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of Amazon S3 bucket for replication reattempt. Returns
     * the updated object.
     *
     * @param bucketName
     *            The name of Amazon S3 bucket to which the replication
     *            reattempt is set.
     * @return The updated {@link SetBucketReplicationReattemptRequest}
     *         object.
     */
    public SetBucketReplicationReattemptRequest withBucketName(
            String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
