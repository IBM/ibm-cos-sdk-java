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
package com.ibm.cloud.objectstorage.services.s3.model;

import com.ibm.cloud.objectstorage.regions.RegionUtils;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Specifies constants that define Amazon S3 Regions.
 * <p>
 * Amazon S3 Regions allow the user to choose the geographical region where Amazon S3
 * will store the buckets the user creates. Choose a Amazon S3 Region to optimize
 * latency, minimize costs, or address regulatory requirements.
 * </p>
 * <p>
 * Objects stored in a Amazon S3 Region never leave that region unless explicitly
 * transferred to another region.
 * </p>
 * <p>
 * In Amazon S3, all the regions provides
 * read-after-write consistency for PUTS of new objects in Amazon
 * S3 buckets and eventual consistency for overwrite PUTS and DELETES.
 * </p>
 * <p>
 * See <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration.html">Migration Guide</a>
 * for more information.
 */
public enum Region {

    /**
     * The US Standard Amazon S3 Region. This region is equivalent to 'us-east-1', see
     * <a href="https://aws.amazon.com/s3/faqs/">Amazon Simple Storage Service (S3) FAQs</a> for more information.
     * <p>
     * This is the default Amazon S3 Region. All requests sent to <code>s3.amazonaws.com</code> go
     * to this region unless a location constraint is specified when creating a bucket.
     */
    US_Standard((String[]) null),

    /**
     * The US-East-2 (Ohio) Region. This region
     * uses Amazon S3 servers located in Ohio.
     * <p>
     * When using buckets in this region, set the client
     * endpoint to <code>s3.us-east-2.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    US_East_2("us-east-2"),

    /**
     * The US-West (Northern California) Amazon S3 Region. This region uses Amazon S3
     * servers located in Northern California.
     * <p>
     * When using buckets in this region, set the client
     * endpoint to <code>s3-us-west-1.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first
     * hour of creating a bucket in this region.
     * </p>
     */
    US_West("us-west-1"),

    /**
     * The US-West-2 (Oregon) Region. This region uses Amazon S3 servers located
     * in Oregon.
     * <p>
     * When using buckets in this region, set the client
     * endpoint to <code>s3-us-west-2.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    US_West_2("us-west-2"),

    /**
     * The US GovCloud Region. This region uses Amazon S3 servers located in the Northwestern
     * region of the United States.
     */
    US_GovCloud("us-gov-west-1"),

    /**
     * The US GovCloud (East) Region.
     */
    US_Gov_East_1("us-gov-east-1", "AWS GovCloud (US-East)"),

    /**
     * The EU (Ireland) Amazon S3 Region. This region uses Amazon S3 servers located
     * in Ireland.
     */
    EU_Ireland("eu-west-1","EU"),

    /**
     * The EU (London) Amazon S3 Region. This region uses Amazon S3 servers located
     * in London.
     */
    EU_London("eu-west-2"),

    /**
     * The EU (Paris) Amazon S3 Region. This region uses Amazon S3 servers located
     * in Paris.
     */
    EU_Paris("eu-west-3"),

    /**
     * The EU (Frankfurt) Amazon S3 Region. This region uses Amazon S3 servers
     * located in Frankfurt.
     * <p>
     *
     * <pre>
     * AmazonS3Client s3 = new AmazonS3Client();
     * s3.setRegion(RegionUtils.getRegion("eu-central-1"));
     * </pre>
     *
     * </p>
     *
     * @see AmazonS3Client#setRegion(com.ibm.cloud.objectstorage.regions.Region)
     */
    EU_Frankfurt("eu-central-1"),

    /**
     * The EU (Zurich) Amazon S3 Region. This region uses Amazon S3 servers
     * located in Zurich.
     * <p>
     *
     * <pre>
     * AmazonS3Client s3 = new AmazonS3Client();
     * s3.setRegion(RegionUtils.getRegion("eu-central-2"));
     * </pre>
     *
     * </p>
     *
     * @see AmazonS3Client#setRegion(com.ibm.cloud.objectstorage.regions.Region)
     */
    EU_Zurich("eu-central-2"),

    /**
     * The EU (Stockholm) Amazon S3 Region. This region uses Amazon S3 servers
     * located in Stockholm.
     * <p>
     *
     * <pre>
     * AmazonS3Client s3 = new AmazonS3Client();
     * s3.setRegion(RegionUtils.getRegion("eu-north-1"));
     * </pre>
     *
     * </p>
     *
     * @see AmazonS3Client#setRegion(com.ibm.cloud.objectstorage.regions.Region)
     */
    EU_North_1("eu-north-1"),

    /**
     * The EU (Milan) Amazon S3 Region. This region uses Amazon S3 servers
     * located in Milan.
     * <p>
     *
     * <pre>
     * AmazonS3Client s3 = new AmazonS3Client();
     * s3.setRegion(RegionUtils.getRegion("eu-south-1"));
     * </pre>
     *
     * </p>
     *
     * @see AmazonS3Client#setRegion(com.ibm.cloud.objectstorage.regions.Region)
     */
    EU_South_1("eu-south-1"),

    /**
     * The EU (Spain) Amazon S3 Region. This region uses Amazon S3 servers
     * located in Spain.
     * <p>
     *
     * <pre>
     * AmazonS3Client s3 = new AmazonS3Client();
     * s3.setRegion(RegionUtils.getRegion("eu-south-2"));
     * </pre>
     *
     * </p>
     *
     * @see AmazonS3Client#setRegion(com.ibm.cloud.objectstorage.regions.Region)
     */
    EU_South_2("eu-south-2"),

    /**
     * The Asia Pacific (Hong Kong) Region. This region uses Amazon S3 servers located
     * in Hong Kong.
     * <p>
     * When using buckets in this region, set the client
     * endpoint to <code>s3.ap-east-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_HongKong("ap-east-1"),

    /**
     * The Asia Pacific (Singapore) Region. This region uses Amazon S3 servers located
     * in Singapore.
     * <p>
     * When using buckets in this region, set the client
     * endpoint to <code>s3-ap-southeast-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Singapore("ap-southeast-1"),

    /**
     * The Asia Pacific (Sydney) Region. This region uses Amazon S3 servers
     * located in Sydney, Australia.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-ap-southeast-2.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    AP_Sydney("ap-southeast-2"),

    /**
     * The Asia Pacific (Jakarta) Region. This region uses Amazon S3 servers
     * located in Jakarta, Indonesia.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-ap-southeast-3.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    AP_Jakarta("ap-southeast-3"),

    /**
     * The Asia Pacific (Melbourne) Region. This region uses Amazon S3 servers
     * located in Melbourne, Australia.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-ap-southeast-4.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    AP_Melbourne("ap-southeast-4"),

    /**
     * The Asia Pacific (Tokyo) Region. This region uses Amazon S3 servers
     * located in Tokyo.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-ap-northeast-1.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Tokyo("ap-northeast-1"),

    /**
     * The Asia Pacific (Seoul) Region. This region uses Amazon S3 servers
     * located in Seoul.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3.ap-northeast-2.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Seoul("ap-northeast-2"),

    /**
     * The Asia Pacific (Osaka) Region. This region uses Amazon S3 servers
     * located in Osaka.
     */
    AP_Osaka("ap-northeast-3"),

    /**
     * The Asia Pacific (Mumbai) Region. This region uses Amazon S3 servers
     * located in Mumbai.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3.ap-south-1.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Mumbai("ap-south-1"),

    /**
     * The Asia Pacific (Hyderabad) Region. This region uses Amazon S3 servers
     * located in Hyderabad.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3.ap-south-2.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Hyderabad("ap-south-2"),

    /**
     * The South America (Sao Paulo) Region. This region uses Amazon S3 servers
     * located in Sao Paulo.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-sa-east-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    SA_SaoPaulo("sa-east-1"),

    /**
     * The Canada (Central) Region. This region uses Amazon S3 servers
     * located in Canada.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3.ca-central-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    CA_Central("ca-central-1"),

    /**
     * The China (Beijing) Region. This region uses Amazon S3 servers
     * located in Beijing.
     * <p>
     * When using buckets in this region, you must set the client endpoint to
     * <code>s3.cn-north-1.amazonaws.com.cn</code>.
     * </p>
     */
    CN_Beijing("cn-north-1"),

    /**
     * The China (Ningxia) Region. This region uses Amazon S3 servers
     * located in Ningxia.
     * <p>
     * When using buckets in this region, you must set the client endpoint to
     * <code>s3.cn-northwest-1.amazonaws.com.cn</code>.
     * </p>
     */
    CN_Northwest_1("cn-northwest-1"),

    /**
     * The Middle East (Bahrain) Region. This region uses Amazon S3 servers
     * located in Bahrain.
     */
    ME_Bahrain("me-south-1"),

    /**
     * The Middle East (UAE) Region. This region uses Amazon S3 servers
     * located in UAE.
     */
    ME_UAE("me-central-1"),

    /**
     * The Israel (Tel Aviv) Region. This region uses Amazon S3 servers
     * located in Tel Aviv, Israel.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3-il-central-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    IL_TelAviv("il-central-1"),

    /**
     * The Canada West (Calgary) Region. This region uses Amazon S3 servers located in Canada West.
     * <p>
     * When using buckets in this region, set the client endpoint to
     * <code>s3.ca-west-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    CA_Calgary("ca-west-1"),

    /**
     * The Africa South (Cape Town) Region. This region uses Amazon S3 servers
     * located in Cape Town.
     */
    AF_CapeTown("af-south-1"),

    /**
     * The US ISO East Region. This region uses Amazon S3 servers
     * located in Virginia.
     * <p>
     * When using buckets in this region, you must set the client endpoint to
     * <code>s3.us-iso-east-1.c2s.ic.gov</code>.
     * </p>
     */
    US_ISO_EAST_1("us-iso-east-1"),

    /**
     * The US ISOB East (Ohio) Region. This region uses Amazon S3 servers
     * located in Ohio.
     * <p>
     * When using buckets in this region, you must set the client endpoint to
     * <code>s3.us-isob-east-1.sc2s.sgov.gov</code>.
     * </p>
     */
    US_ISOB_EAST_1("us-isob-east-1"),

    /**
     * The US ISO West Region. This region uses Amazon S3 servers
     * located in Colorado.
     * <p>
     * When using buckets in this region, you must set the client endpoint to
     * <code>s3.us-iso-west-1.c2s.ic.gov</code>.
     * </p>
     */
    US_ISO_WEST_1("us-iso-west-1")
    ;

   /**
    * Used to extract the S3 regional id from an S3 end point.
    * Note this pattern will not match the S3 US standard endpoint by intent.
    * Exampless:
    * <pre>
    * s3-eu-west-1.amazonaws.com
    * s3.cn-north-1.amazonaws.com.cn
    * </pre>
    */
    public static final Pattern S3_REGIONAL_ENDPOINT_PATTERN =
            Pattern.compile("s3[-.]([^.]+)\\.amazonaws\\.com(\\.[^.]*)?");

    /** The list of ID's representing each region. */
    private final List<String> regionIds;

    /**
     * Constructs a new region with the specified region ID's.
     *
     * @param regionIds
     *            The list of ID's representing the S3 region.
     */
    private Region(String... regionIds) {
        this.regionIds = regionIds != null ? Arrays.asList(regionIds) : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getFirstRegionId0();
    }

    /**
     * Returns the first region id or null for {@link #US_Standard}.
     */
    public String getFirstRegionId() {
        return getFirstRegionId0();
    }

    private String getFirstRegionId0() {
        return this.regionIds == null || regionIds.size() == 0
             ? null : this.regionIds.get(0);
    }

    /**
     * Returns the Amazon S3 Region enumeration value representing the specified Amazon
     * S3 Region ID string. If specified string doesn't map to a known Amazon S3
     * Region, then an <code>IllegalArgumentException</code> is thrown.
     *
     * @param s3RegionId
     *            The Amazon S3 region ID string.
     *
     * @return The Amazon S3 Region enumeration value representing the specified Amazon
     *         S3 Region ID.
     *
     * @throws IllegalArgumentException
     *             If the specified value does not map to one of the known
     *             Amazon S3 regions.
     */
    public static Region fromValue(final String s3RegionId) throws IllegalArgumentException
    {
        if (s3RegionId == null || s3RegionId.equals("US") || s3RegionId.equals("us-east-1")) {
            return Region.US_Standard;
        }
        for (Region region : Region.values()) {
            List<String> regionIds = region.regionIds;
            if (regionIds != null && regionIds.contains(s3RegionId))
                return region;
        }

        throw new IllegalArgumentException(
                "Cannot create enum from " + s3RegionId + " value!");
    }

    /**
     * Returns the respective Amazon Web Services region.
     */
    public com.ibm.cloud.objectstorage.regions.Region toAWSRegion() {
        String s3regionId = getFirstRegionId();
        if ( s3regionId == null ) { // US Standard
            // TODO This is a bit of a hack but customers are relying on this returning us-east-1 rather then
            // aws-global. For now we'll keep the legacy behavior and consider changing it in the next major version
            // bump. See TT0073140598
            return RegionUtils.getRegion("us-east-1");
        } else {
            return RegionUtils.getRegion(s3regionId);
        }
    }
}
