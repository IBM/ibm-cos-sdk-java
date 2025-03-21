/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.s3.model.transform;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.services.s3.internal.XmlWriter;
import com.ibm.cloud.objectstorage.services.s3.model.GlacierJobParameters;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectTagging;
import com.ibm.cloud.objectstorage.services.s3.model.PartETag;
import com.ibm.cloud.objectstorage.services.s3.model.RestoreObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.Tag;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RequestXmlFactory {

    /**
     * Converts the specified list of PartETags to an XML fragment that can be
     * sent to the CompleteMultipartUpload operation of Amazon S3.
     *
     * @param partETags
     *            The list of part ETags containing the data to include in the
     *            new XML fragment.
     *
     * @return A byte array containing the data
     */
    public static byte[] convertToXmlByteArray(List<PartETag> partETags) {
        XmlWriter xml = new XmlWriter();
        xml.start("CompleteMultipartUpload");
        if (partETags != null) {
            List<PartETag> sortedPartETags = new ArrayList<PartETag>(partETags);
            Collections.sort(sortedPartETags, new Comparator<PartETag>() {
                public int compare(PartETag tag1, PartETag tag2) {
                    if (tag1.getPartNumber() < tag2.getPartNumber()) return -1;
                    if (tag1.getPartNumber() > tag2.getPartNumber()) return 1;
                    return 0;
                }
            });

            for (PartETag partEtag : sortedPartETags) {
                xml.start("Part");
                xml.start("PartNumber").value(Integer.toString(partEtag.getPartNumber())).end();
                xml.start("ETag").value(partEtag.getETag()).end();
                xml.end();
            }
        }
        xml.end();

        return xml.getBytes();
    }

    /**
     * Converts the RestoreObjectRequest to an XML fragment that can be sent to
     * the RestoreObject operation of Amazon S3.
     *
     * @param restoreObjectRequest
     *            The container which provides options for restoring an object,
     *            which was transitioned to the Glacier from S3 when it was
     *            expired, into S3 again.
     *
     * @return A byte array containing the data
     *
     * @throws SdkClientException
     */
    public static byte[] convertToXmlByteArray(RestoreObjectRequest restoreObjectRequest) throws SdkClientException {

        XmlWriter xml = new XmlWriter();

        xml.start("RestoreRequest");
        if (restoreObjectRequest.getExpirationInDays() != -1) {
            xml.start("Days").value(Integer.toString(restoreObjectRequest.getExpirationInDays())).end();
        }

        final GlacierJobParameters glacierJobParameters = restoreObjectRequest.getGlacierJobParameters();
        if (glacierJobParameters != null) {
            xml.start("GlacierJobParameters");
            addIfNotNull(xml, "Tier", glacierJobParameters.getTier());
            xml.end();
        }
        xml.end();

        return xml.getBytes();
    }

    private static void addTaggingIfNotNull(XmlWriter xml, ObjectTagging tagSet) {
        if (tagSet == null) {
            return;
        }

        xml.start("Tagging");
        xml.start("TagSet");
        for(Tag tag : tagSet.getTagSet()) {
            xml.start("Tag");
            xml.start("Key").value(tag.getKey()).end();
            xml.start("Value").value(tag.getValue()).end();
            xml.end();
        }
        xml.end();
        xml.end();
    }

    private static void addIfNotNull(XmlWriter xml, String xmlTag, String value) {
        if (value != null) {
            xml.start(xmlTag).value(value).end();
        }
    }

    private static void addIfNotNull(XmlWriter xml, String xmlTag, Object value) {
        if (value != null && value.toString() != null) {
            xml.start(xmlTag).value(value.toString()).end();
        }
    }
}
