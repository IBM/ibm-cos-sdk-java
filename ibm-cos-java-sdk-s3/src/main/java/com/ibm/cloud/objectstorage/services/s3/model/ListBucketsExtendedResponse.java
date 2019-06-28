/* 
* Copyright 2019 IBM Corp. All Rights Reserved. 
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
import java.util.ArrayList;
import java.util.List;

/**
 * Results of a listing of objects from an S3 bucket.
 */
public class ListBucketsExtendedResponse implements Serializable {
    private static final long serialVersionUID = 2635994786408902260L;

    /**
     * Indicates if this is a complete listing, or if the caller needs to make
     * additional requests to COS to see the full object listing for an S3
     * bucket
     */
    private boolean isTruncated;

    /** The bucket at or after which the listing began */
    private String marker = null;

    /** A list of summary information describing the listed buckets */
    private List<Bucket> buckets = new ArrayList<Bucket>();

    /**
     * Gets whether or not this listing is complete.
     *
     * @return The value <code>true</code> if the bucket listing is <b>not complete</b>.
     *         Returns the value <code>false</code> if otherwise.
     *         When returning <code>true</code>,
     *         additional calls to COS may be needed in order to
     *         obtain more results.
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * For internal use only.  Sets the truncated property for
     * this object listing, indicating if this is a complete listing or not and
     * whether the caller needs to make additional calls to S3 to get more
     * object summaries.
     *
     * @param isTruncated
     *            The value <code>true</code> if the object listing is <b>not complete</b>.
     *            The value <code>false</code> if otherwise.
     */
    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    /**
     * Gets the list of buckets describing the objects stored in the
     * S3 bucket. Listings for large buckets can be
     * truncated for performance reasons.  Always check the
     * {@link ListBucketsExtendedResponse#isTruncated()} method to see if the returned
     * listing is complete or if additional calls are needed to get
     * more results.
     *
     * @return A list of buckets.
     */
    public List<Bucket> getBuckets() {
        return buckets;
    }

    /**
     * For internal use only. Sets the bucket from the XML response.
     */
    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    /**
     * Parameter indicating where in the bucket to begin listing. The
     * list will only include keys that occur lexicographically after the
     * marker. This enables pagination
     * 
     * @return marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * For internal use only. Sets the marker from the XML response.
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

}
