/*
 * Copyright 2013-2024 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.cloud.objectstorage.regions;

import com.ibm.cloud.objectstorage.AmazonClientException;
import org.apache.commons.logging.LogFactory;

import com.ibm.cloud.objectstorage.util.EC2MetadataUtils;

/**
 * Enumeration of region names
 *
 * <p>
 * See <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration.html">Migration Guide</a>
 * for more information.
 */
public enum Regions {

    GovCloud("us-gov-west-1", "AWS GovCloud (US)"),
    US_GOV_EAST_1("us-gov-east-1", "AWS GovCloud (US-East)"),
    US_EAST_1("us-east-1", "US East (N. Virginia)"),
    US_EAST_2("us-east-2", "US East (Ohio)"),
    US_WEST_1("us-west-1", "US West (N. California)"),
    US_WEST_2("us-west-2", "US West (Oregon)"),
    EU_WEST_1("eu-west-1", "EU (Ireland)"),
    EU_WEST_2("eu-west-2", "EU (London)"),
    EU_WEST_3("eu-west-3", "EU (Paris)"),
    EU_CENTRAL_1("eu-central-1", "EU (Frankfurt)"),
    EU_CENTRAL_2("eu-central-2", "EU (Zurich)"),
    EU_NORTH_1("eu-north-1", "EU (Stockholm)"),
    EU_SOUTH_1("eu-south-1", "EU (Milan)"),
    EU_SOUTH_2("eu-south-2", "EU (Spain)"),
    AP_EAST_1("ap-east-1", "Asia Pacific (Hong Kong)"),
    AP_SOUTH_1("ap-south-1", "Asia Pacific (Mumbai)"),
    AP_SOUTH_2("ap-south-2", "Asia Pacific (Hyderabad)"),
    AP_SOUTHEAST_1("ap-southeast-1", "Asia Pacific (Singapore)"),
    AP_SOUTHEAST_2("ap-southeast-2", "Asia Pacific (Sydney)"),
    AP_SOUTHEAST_3("ap-southeast-3", "Asia Pacific (Jakarta)"),
    AP_SOUTHEAST_4("ap-southeast-4", "Asia Pacific (Melbourne)"),
    AP_NORTHEAST_1("ap-northeast-1", "Asia Pacific (Tokyo)"),
    AP_NORTHEAST_2("ap-northeast-2", "Asia Pacific (Seoul)"),
    AP_NORTHEAST_3("ap-northeast-3", "Asia Pacific (Osaka)"),

    SA_EAST_1("sa-east-1", "South America (Sao Paulo)"),
    CN_NORTH_1("cn-north-1", "China (Beijing)"),
    CN_NORTHWEST_1("cn-northwest-1", "China (Ningxia)"),
    CA_CENTRAL_1("ca-central-1", "Canada (Central)"),
    CA_WEST_1("ca-west-1", "Canada West (Calgary)"),
    ME_CENTRAL_1("me-central-1", "Middle East (UAE)"),
    ME_SOUTH_1("me-south-1", "Middle East (Bahrain)"),
    AF_SOUTH_1("af-south-1", "Africa (Cape Town)"),
    US_ISO_EAST_1("us-iso-east-1", "US ISO East"),
    US_ISOB_EAST_1("us-isob-east-1", "US ISOB East (Ohio)"),
    US_ISO_WEST_1("us-iso-west-1", "US ISO West"),
    IL_CENTRAL_1("il-central-1", "Israel (Tel Aviv)")
    ;

    /**
     * The default region that new customers in the US are encouraged to use
     * when using AWS services for the first time.
     */
    public static final Regions DEFAULT_REGION = US_WEST_2;

    private final String name;
    private final String description;

    private Regions(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * The name of this region, used in the regions.xml file to identify it.
     */
    public String getName() {
        return name;
    }

    /**
     * Descriptive readable name for this region.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a region enum corresponding to the given region name.
     *
     * @param regionName
     *            The name of the region. Ex.: eu-west-1
     * @return Region enum representing the given region name.
     * @deprecated This method requires the SDK version to be updated to support new regions. Use
     * {@link RegionUtils#getRegion(String)}, which does not have this limitation.
     */
    @Deprecated
    public static Regions fromName(String regionName) {
        for (Regions region : Regions.values()) {
            if (region.getName().equals(regionName)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Cannot create enum from " + regionName + " value! "
                                           + "Use com.ibm.cloud.objectstorage.regions.RegionUtils.getRegion to access regions that "
                                           + "aren't known to this SDK version.");
    }

    /**
     * Returns a Region object representing the region the application is
     * running in, when running in EC2. If this method is called from a non-EC2
     * environment, it will return null.
     */
    public static Region getCurrentRegion() {
        try {
            final String region = EC2MetadataUtils.getEC2InstanceRegion();
            if (region != null)
                return RegionUtils.getRegion(region);
        } catch (AmazonClientException e) {
            LogFactory.getLog(Regions.class).debug(
                "Ignoring failure to retrieve the region: " + e.getMessage());
        }
        return null;
    }
}
