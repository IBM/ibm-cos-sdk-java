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

package com.ibm.cloud.objectstorage.waiters;

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * Callbacks are executed synchronously. That is the same thread the waiter
 * completes on and it's not submitted back to the executor.
 */
public abstract class WaiterHandler<Input extends AmazonWebServiceRequest> {

    public abstract void onWaitSuccess(Input request);

    public abstract void onWaitFailure(Exception e);
}

