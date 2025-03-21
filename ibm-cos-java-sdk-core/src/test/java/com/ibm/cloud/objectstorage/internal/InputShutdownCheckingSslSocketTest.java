/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.ibm.cloud.objectstorage.internal;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InputShutdownCheckingSslSocketTest {

    @Test(expected = IOException.class)
    public void outputStreamChecksInputShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        when(mockSocket.isInputShutdown()).thenReturn(true);
        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();
        os.write(1);
    }

    @Test
    public void outputStreamWritesNormallyWhenInputNotShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.isInputShutdown()).thenReturn(false);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();
        os.write(1);
        verify(mockOutputStream).write(1);
    }

    @Test(expected = IOException.class)
    public void writeByteArrayThrowsIOExceptionWhenInputIsShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        when(mockSocket.isInputShutdown()).thenReturn(true);
        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();
        os.write(new byte[10]);
    }

    @Test
    public void writeByteArraySucceedsWhenInputNotShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.isInputShutdown()).thenReturn(false);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();

        byte[] data = new byte[10];
        os.write(data);
        verify(mockOutputStream).write(data);
    }

    @Test(expected = IOException.class)
    public void writeByteArrayWithOffsetThrowsIOExceptionWhenInputIsShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        when(mockSocket.isInputShutdown()).thenReturn(true);

        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();

        os.write(new byte[10], 0, 10);
    }

    @Test
    public void writeByteArrayWithOffsetSucceedsWhenInputNotShutdown() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.isInputShutdown()).thenReturn(false);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();

        byte[] data = new byte[10];
        os.write(data, 0, 10);
        verify(mockOutputStream).write(data, 0, 10);
    }

    @Test
    public void checkInputShutdownThrowsIOExceptionWithSuppressed() throws IOException {
        SdkSSLSocket mockSocket = mock(SdkSSLSocket.class);
        when(mockSocket.isInputShutdown()).thenReturn(false);
        when(mockSocket.getInputStream()).thenThrow(new IOException("InputStream exception"));

        InputShutdownCheckingSslSocket socket = new InputShutdownCheckingSslSocket(mockSocket);
        OutputStream os = socket.getOutputStream();

        try{
            os.write(1);
            fail("");
        }catch (Exception e){
            assertThat(e, instanceOf(IOException.class));
            assertThat(e.getMessage(), containsString("Remote end is closed."));
        }
    }
}
