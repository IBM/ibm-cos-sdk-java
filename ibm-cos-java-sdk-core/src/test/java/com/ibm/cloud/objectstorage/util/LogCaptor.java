/*
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ibm.cloud.objectstorage.util;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * A test utility that allows inspection of log statements
 * during testing.
 * <p>
 * Can either be used stand-alone for example
 * <pre><code>
 *     private LogCaptor logCaptor = new LogCaptor.DefaultLogCaptor(Level.INFO);
 *     // Do stuff that you expect to log things
 *     assertThat(logCaptor.loggedEvents(), is(not(empty())));
 * </code></pre>
 * <p>
 * Or can extend it to make use of @Before / @After test annotations
 * <p>
 * <pre><code>
 *     class MyTestClass extends LogCaptor.LogCaptorTestBase {
 *         {@literal @}Test
 *         public void someTestThatWeExpectToLog() {
 *             // Do stuff that you expect to log things
 *             assertThat(loggedEvents(), is(not(empty())));
 *         }
 *     }
 * </code></pre>
 */

public interface LogCaptor {
    class LoggingEvent {}  // IBM
    class AppenderSkeleton {}  // IBM
    class DefaultLogCaptor extends AppenderSkeleton implements LogCaptor {  // IBM
        public void clear() {}  // IBM
        public List<LoggingEvent> loggedEvents() {  // IBM
            return new ArrayList<LoggingEvent>();  // IBM
        }  // IBM
    }  // IBM

    List<LoggingEvent> loggedEvents();

    void clear();

    class LogCaptorTestBase extends DefaultLogCaptor {
        public LogCaptorTestBase() {
            // IBM super(Level.ALL);
        }

        @Before
        public void setupLogging() {
            // IBM super.setupLogging();
        }

        @After
        public void stopLogging() {
            // IBM super.stopLogging();
        }
    }

    // IBM class DefaultLogCaptor extends AppenderSkeleton implements LogCaptor {
    // IBM
    // IBM     private final List<LoggingEvent> loggedEvents = new ArrayList<LoggingEvent>();
    // IBM     private final Level originalLoggingLevel = Logger.getRootLogger().getLevel();
    // IBM     private final Level levelToCapture;
    // IBM
    // IBM     public DefaultLogCaptor(Level levelToCapture) {
    // IBM         super();
    // IBM         this.levelToCapture = levelToCapture;
    // IBM         setupLogging();
    // IBM     }
    // IBM
    // IBM     @Override
    // IBM     public void finalize() {
    // IBM         super.finalize();
    // IBM         stopLogging();
    // IBM     }
    // IBM
    // IBM     @Override
    // IBM     public List<LoggingEvent> loggedEvents() {
    // IBM         return new ArrayList<LoggingEvent>(loggedEvents);
    // IBM     }
    // IBM
    // IBM     @Override
    // IBM     public void clear() {
    // IBM         loggedEvents.clear();
    // IBM     }
    // IBM
    // IBM     protected void setupLogging() {
    // IBM         loggedEvents.clear();
    // IBM         Logger.getRootLogger().addAppender(this);
    // IBM         Logger.getRootLogger().setLevel(levelToCapture);
    // IBM     }
    // IBM
    // IBM     protected void stopLogging() {
    // IBM         Logger.getRootLogger().removeAppender(this);
    // IBM         Logger.getRootLogger().setLevel(originalLoggingLevel);
    // IBM     }
    // IBM
    // IBM     @Override
    // IBM     protected void append(LoggingEvent loggingEvent) {
    // IBM         loggedEvents.add(loggingEvent);
    // IBM     }
    // IBM
    // IBM     @Override
    // IBM     public boolean requiresLayout() { return false; }
    // IBM
    // IBM     @Override
    // IBM     public void close() { }
    // IBM }
}
