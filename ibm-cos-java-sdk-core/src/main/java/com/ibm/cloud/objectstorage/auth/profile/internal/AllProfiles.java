/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.auth.profile.internal;

import com.ibm.cloud.objectstorage.annotation.Immutable;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;

import java.util.Collections;
import java.util.Map;

/**
 * Simple wrapper around a map of profiles.
 */
@Immutable
@SdkInternalApi
public class AllProfiles {

    private final Map<String, BasicProfile> profiles;

    public AllProfiles(Map<String, BasicProfile> profiles) {
        this.profiles = profiles;
    }

    public Map<String, BasicProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    public BasicProfile getProfile(String profileName) {
        BasicProfile profile = profiles.get(profileName);
        if (profile != null) {
            return profile;
        }

        return profiles.get("profile " + profileName);
    }
}
