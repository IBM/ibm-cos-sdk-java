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
package com.ibm.cloud.objectstorage.arn;

import com.ibm.cloud.objectstorage.util.ValidationUtils;

/**
 * The ARNs generated and recognized by this code are the ARNs described here:
 *
 * <a href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">
 *     https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html</a>
 * <p>
 * The primary supported ARN format is: {@code arn:<partition>:<service>:<region>:<account>:<resource>}
 * <p>
 * To parse an ARN from a string use Arn.fromString(). To convert an ARN to it's
 * string representation use Arn.toString(). For instance, for a string s, containing a well-formed ARN the
 * following should always be true:
 * <pre>
 * ARN theArn = Arn.fromString(s);
 * s.equals(theArn.toString());
 * </pre>
 *
 * <h3>Resources</h3>
 * {@link #getResourceAsString()} returns everything after the account section of the ARN
 * as a single string.
 * <p>
 * You can also retrieve the resource with the different resource parts using {@link #getResource()}.
 * This method returns a {@link ArnResource} which has access to {@link ArnResource#getResourceType()},
 * {@link ArnResource#getResource()} and {@link ArnResource#getQualifier()}.
 * <p>
 * The following ARN formats are supported when parsing resources:
 * <pre>{@code
 * arn:<partition>:<service>:<region>:<account>:resource
 * arn:<partition>:<service>:<region>:<account>:<resourcetype>/resource
 * arn:<partition>:<service>:<region>:<account>:<resourcetype>/resource:qualifier
 * arn:<partition>:<service>:<region>:<account>:<resourcetype>:resource
 * arn:<partition>:<service>:<region>:<account>:<resourcetype>:resource:qualifier
 * }</pre>
 * Some services use parent- and subresources which follow the format
 * 'sub-resource-type/parent-resource/sub-resource'. In this case, the whole expression 'parent-resource/sub-resource'
 * will be returned as the resource-id. An ARN using this pattern would be
 * {@code arn:<partition>:<service>:<region>:<account>:<resourcetype>/parent-resource/sub-resource} which has
 * a resource type and a resource but no qualifier.
 * <p>
 * <b>Note:</b><br/>
 * Parsing permutations of any ARN formats not covered in tests or described as supported in documentation are at the
 * user's own risk, as these use cases may change in the future.
 */
public class Arn {

    private final String partition;
    private final String service;
    private final String region;
    private final String accountId;
    private final String resource;
    private final ArnResource arnResource;

    private Arn(Builder builder) {
        this.partition = ValidationUtils.assertStringNotEmpty(builder.partition, "partition");
        this.service = ValidationUtils.assertStringNotEmpty(builder.service, "service");
        this.region = builder.region;
        this.accountId = builder.accountId;
        this.resource = ValidationUtils.assertStringNotEmpty(builder.resource, "resource");
        this.arnResource = ArnResource.fromString(resource);
    }

    public String getPartition() {
        return partition;
    }

    public String getService() {
        return service;
    }

    public String getRegion() {
        return region;
    }

    public String getAccountId() {
        return accountId;
    }

    public ArnResource getResource() {
        return arnResource;
    }

    public String getResourceAsString() {
        return resource;
    }

    public Builder toBuilder() {
        return builder().withPartition(getPartition())
                        .withService(getService())
                        .withAccountId(getAccountId())
                        .withRegion(getRegion())
                        .withResource(getResourceAsString());
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses a given string into an {@link Arn}. The resource is accessible entirely as a
     * string through {@link #getResourceAsString()}. Where correctly formatted, a parsed
     * resource containing resource type, resource and qualifier is available through
     * {@link #getResource()}.
     *
     * @param arn - A string containing an ARN.
     * @return {@link Arn} - A modeled ARN.
     */
    public static Arn fromString(String arn) {
        int arnColonIndex = arn.indexOf(':');
        if (arnColonIndex < 0 || !"arn".equals(arn.substring(0, arnColonIndex))) {
            throw new IllegalArgumentException("Malformed ARN - doesn't start with 'arn:'");
        }

        int partitionColonIndex = arn.indexOf(':', arnColonIndex + 1);
        if (partitionColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS partition specified");
        }
        String partition = arn.substring(arnColonIndex + 1, partitionColonIndex);

        int serviceColonIndex = arn.indexOf(':', partitionColonIndex + 1);
        if (serviceColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no service specified");
        }
        String service = arn.substring(partitionColonIndex + 1, serviceColonIndex);

        int regionColonIndex = arn.indexOf(':', serviceColonIndex + 1);
        if (regionColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS region partition specified");
        }
        String region = arn.substring(serviceColonIndex + 1, regionColonIndex);

        int accountColonIndex = arn.indexOf(':', regionColonIndex + 1);
        if (accountColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS account specified");
        }
        String accountId = arn.substring(regionColonIndex + 1, accountColonIndex);

        String resource = arn.substring(accountColonIndex + 1);
        if (resource.isEmpty()) {
            throw new IllegalArgumentException("Malformed ARN - no resource specified");
        }

        return Arn.builder()
                  .withPartition(partition)
                  .withService(service)
                  .withRegion(region)
                  .withAccountId(accountId)
                  .withResource(resource)
                  .build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("arn:");
        sb.append(this.partition);
        sb.append(":");
        sb.append(this.service);
        sb.append(":");
        sb.append(region);
        sb.append(":");
        sb.append(this.accountId);
        sb.append(":");
        sb.append(this.resource);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Arn arn = (Arn) o;

        if (!partition.equals(arn.partition)) {
            return false;
        }
        if (!service.equals(arn.service)) {
            return false;
        }
        if (region != null ? !region.equals(arn.region) : arn.region != null) {
            return false;
        }
        if (accountId != null ? !accountId.equals(arn.accountId) : arn.accountId != null) {
            return false;
        }
        return resource.equals(arn.resource);
    }

    @Override
    public int hashCode() {
        int result = partition.hashCode();
        result = 31 * result + service.hashCode();
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + resource.hashCode();
        return result;
    }

    public static final class Builder {
        private String partition;
        private String service;
        private String region;
        private String accountId;
        private String resource;

        private Builder() {}

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public Builder withPartition(String partition) {
            setPartition(partition);
            return this;
        }

        public void setService(String service) {
            this.service = service;
        }

        public Builder withService(String service) {
            setService(service);
            return this;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Builder withRegion(String region) {
            setRegion(region);
            return this;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public Builder withAccountId(String accountId) {
            setAccountId(accountId);
            return this;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public Builder withResource(String resource) {
            setResource(resource);
            return this;
        }

        public Arn build() {
            return new Arn(this);
        }
    }
}
