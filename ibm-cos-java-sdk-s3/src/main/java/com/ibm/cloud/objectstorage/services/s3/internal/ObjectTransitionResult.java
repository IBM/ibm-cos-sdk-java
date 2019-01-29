/*
* Copyright 2018 IBM Corp. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3.internal;

import java.util.Date;

import com.ibm.cloud.objectstorage.services.s3.Headers;

/**
 * Interface for service responses that receive the x-ibm-transition header.
 *
 * @see Headers#TRANSITION
 */
public interface ObjectTransitionResult {

    /**
     * Returns the date when the Object is scheduled to transition to archive, or null if the object is not
     * configured to expire.
     */
    public Date getTransitionDate();

    /**
     * Sets the date when the Object is scheduled to transition to archive.
     *
     * @param transitionDate
     *            The date the object will transition to archive.
     */
    public void setTransitionDate(Date transitionDate);

    /**
     * Set the object's transition storage class.
     * @param transition
     */
    public void setTransition(String transition);

    /**
     * Returns the object's transition storage class.
     */
    public String getTransition();
}
