/*
 * Copyright 2019-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.ibm.cloud.objectstorage.services.s3.transfer.internal;

import static com.ibm.cloud.objectstorage.services.s3.internal.Constants.MB;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.services.s3.model.S3Object;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectInputStream;
import com.ibm.cloud.objectstorage.util.IOUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class to get a partial objects from s3, write the data to the specified position
 * and return the final position of the file.
 */
@SdkInternalApi
public class DownloadS3ObjectCallable implements Callable<Long> {
    private static final Log LOG = LogFactory.getLog(DownloadS3ObjectCallable.class);
    private static final int BUFFER_SIZE = 2 * MB;

    private final Callable<S3Object> serviceCall;
    private final File destinationFile;
    private final long position;

    public DownloadS3ObjectCallable(Callable<S3Object> serviceCall,
                                    File destinationFile,
                                    long position) {
        this.serviceCall = serviceCall;
        this.destinationFile = destinationFile;
        this.position = position;
    }

    @Override
    public Long call() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(destinationFile, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        channel.position(position);
        S3ObjectInputStream objectContent = null;
        long filePosition;

        try {

            S3Object object = serviceCall.call();

            objectContent = object.getObjectContent();

            final byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            while ((bytesRead = objectContent.read(buffer)) > -1) {
                byteBuffer.limit(bytesRead);

                while (byteBuffer.hasRemaining()) {
                    channel.write(byteBuffer);
                }
                byteBuffer.clear();
            }

            filePosition = channel.position();
        } finally {
            IOUtils.closeQuietly(objectContent, LOG);
            IOUtils.closeQuietly(randomAccessFile, LOG);
            IOUtils.closeQuietly(channel, LOG);
        }
        return filePosition;
    }
}