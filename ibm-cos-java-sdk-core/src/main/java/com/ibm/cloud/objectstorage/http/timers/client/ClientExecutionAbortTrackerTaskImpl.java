/*
 * Copyright 2015-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.http.timers.client;

import java.util.concurrent.ScheduledFuture;

import org.apache.http.client.methods.HttpRequestBase;

import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.util.ValidationUtils;

/**
 * Keeps track of the scheduled {@link ClientExecutionAbortTask} and the associated {@link Future}
 */
@SdkInternalApi
public class ClientExecutionAbortTrackerTaskImpl implements ClientExecutionAbortTrackerTask {

    private final ClientExecutionAbortTask task;
    private final ScheduledFuture<?> future;

    public ClientExecutionAbortTrackerTaskImpl(final ClientExecutionAbortTask task, final ScheduledFuture<?> future) {
        this.task = ValidationUtils.assertNotNull(task, "task");
        this.future = ValidationUtils.assertNotNull(future, "future");
    }

    @Override
    public void setCurrentHttpRequest(HttpRequestBase newRequest) {
        task.setCurrentHttpRequest(newRequest);
    }

    @Override
    public boolean hasTimeoutExpired() {
        return task.hasClientExecutionAborted();
    }

    @Override
    public boolean isEnabled() {
        return task.isEnabled();
    }

    @Override
    public void cancelTask() {
        // Best-effort attempt to ensure task is canceled even if it's running as we don't want the Thread to be
        // interrupted in the caller's code
        future.cancel(false);

        // Ensure that if the future hasn't executed its timeout logic already, it won't do so.
        task.cancel();
    }
}