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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.services.s3.Headers;

/**
 * Header handler to pull the TRANSITION header out of the response.
 */
public class ObjectTransitionHeaderHandler<T extends ObjectTransitionResult>
        implements HeaderHandler<T> {

    /*
     *  transition="ARCHIVE", date="Fri, 23 Dec 2012 00:00:00 GMT"
     */

    private static final Pattern datePattern =
        Pattern.compile("date=\"(.*?)\"");
    private static final Pattern transitionPattern =
        Pattern.compile("transition=\"(.*?)\"");

    private static final Log log =
        LogFactory.getLog(ObjectTransitionHeaderHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ibm.cloud.objectstorage.services.s3.internal.HeaderHandler#handle(java.lang.Object,
     * com.ibm.cloud.objectstorage.http.HttpResponse)
     */
    @Override
    public void handle(T result, HttpResponse response) {
        String transitionHeader = response.getHeaders().get(Headers.IBM_TRANSITION);
        if (transitionHeader != null) {
            result.setTransitionDate(parseDate(transitionHeader));
            String transition = parseTransition(transitionHeader);
            if (transition != null) {
                result.setTransition(transition);
            }
        }
    }

    private Date parseDate(String restoreHeader) {
        Matcher matcher = datePattern.matcher(restoreHeader);
        if ( matcher.find() ) {
            String date = matcher.group(1);
            try {
                return ServiceUtils.parseRfc822Date(date);
            } catch (Exception exception) {
                log.warn("Error parsing date from x-ibm-transition "
                         + "header.",
                         exception);
            }
        }

        return null;
    }

    private String parseTransition(String restoreHeader) {
        Matcher matcher = transitionPattern.matcher(restoreHeader);
        if (matcher.find()) {
            String transition = matcher.group(1);
            return transition;
        }
        return null;
    }

}

