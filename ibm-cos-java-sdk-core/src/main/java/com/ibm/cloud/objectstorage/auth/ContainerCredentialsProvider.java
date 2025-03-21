/*
 * Copyright 2011-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.auth;

import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.internal.CredentialsEndpointProvider;
import com.ibm.cloud.objectstorage.retry.internal.CredentialsEndpointRetryPolicy;
import com.ibm.cloud.objectstorage.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

/**
 * <p>
 * {@link AWSCredentialsProvider} implementation that loads credentials from a local metadata service.
 * </p>
 * Currently supported containers:
 * <ul>
 *     <li>Amazon Elastic Container Service (ECS)</li>
 *     <li>Amazon Elastic Kubernetes Service (EKS)</li>
 *     <li>AWS Greengrass</li>
 * </ul>
 * <p>
 * The URI path is retrieved from the environment variable "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI" or
 * "AWS_CONTAINER_CREDENTIALS_FULL_URI" in the container's environment. Resolving to use relative or absolute path
 * is the role of {@link EC2ContainerCredentialsProviderWrapper}.
 * </p>
 * <p>
 * <b>Full (absolute) URI configuration</b>
 * <p>
 * For absolute paths, only loopback hosts are allowed when using HTTP, including known endpoints for ECS and EKS.
 * All HTTPS endpoints are allowed.
 * <p>
 * IPv6 addresses are supported when setting the "AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE" environment variable.
 * <p>
 * Optionally, an authorization token can be included in the "Authorization" header of the request.
 * There are two ways of providing the token, in order of priority:
 * <ul>
 *     <li>Setting the "AWS_CONTAINER_AUTHORIZATION_TOKEN" environment variable</li>
 *     <li>Entering the token into a file and providing the path to it using the
 *     "AWS_CONTAINER_AUTHORIZATION_TOKEN_FILE" environment variable. Note that the token content will be
 *     used as-is.</li>
 * </ul>.
 *
 * <p>
 * See <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-client-credentials.html">Migration Guide</a>
 * for more information.
 */
public class ContainerCredentialsProvider implements AWSCredentialsProvider {

    /** Environment variable to get the Amazon ECS credentials resource path. */
    static final String ECS_CONTAINER_CREDENTIALS_PATH = "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI";

    /** Environment variable to get the full URI for a credentials path */
    // IBM unsupported
    // static final String CONTAINER_CREDENTIALS_FULL_URI = "AWS_CONTAINER_CREDENTIALS_FULL_URI";

    static final String AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE = "AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE";

    // static final String CONTAINER_AUTHORIZATION_TOKEN = "AWS_CONTAINER_AUTHORIZATION_TOKEN";
    static final String CONTAINER_AUTHORIZATION_TOKEN_FILE = "AWS_CONTAINER_AUTHORIZATION_TOKEN_FILE";

    private static final String HTTPS = "https";
	// private static final Set<String> ALLOWED_FULL_URI_HOSTS = allowedHosts();

    /** Default endpoint to retrieve the Amazon ECS Credentials. */
    private static final String ECS_CREDENTIALS_ENDPOINT = "http://169.254.170.2";

    private static final String ECS_CONTAINER_HOST = "169.254.170.2";
    private static final String EKS_CONTAINER_HOST = "169.254.170.23";
    private static final String EKS_CONTAINER_HOST_IPV6 = "[fd00:ec2::23]";
    private static final List<String> VALID_LOOP_BACK_IPV4 = Arrays.asList(ECS_CONTAINER_HOST, EKS_CONTAINER_HOST);
    private static final List<String> VALID_LOOP_BACK_IPV6 = Arrays.asList(EKS_CONTAINER_HOST_IPV6);

    private final ContainerCredentialsFetcher credentialsFetcher;

    /**
     * @deprecated use {@link #ContainerCredentialsProvider(CredentialsEndpointProvider)}
     */
    @Deprecated
    public ContainerCredentialsProvider() {
        this(new ECSCredentialsEndpointProvider());
    }

    public ContainerCredentialsProvider(CredentialsEndpointProvider credentialsEndpointProvider) {
        this.credentialsFetcher = new ContainerCredentialsFetcher(credentialsEndpointProvider);
    }

    @Override
    public AWSCredentials getCredentials() {
        return credentialsFetcher.getCredentials();
    }

    @Override
    public void refresh() {
        credentialsFetcher.refresh();
    }

    public Date getCredentialsExpiration() {
        return credentialsFetcher.getCredentialsExpiration();
    }


    static class ECSCredentialsEndpointProvider extends CredentialsEndpointProvider {
        @Override
        public URI getCredentialsEndpoint() {
            String path = System.getenv(ECS_CONTAINER_CREDENTIALS_PATH);
            if (path == null) {
                throw new SdkClientException(
                        "The environment variable " + ECS_CONTAINER_CREDENTIALS_PATH + " is empty");
            }

            return URI.create(ECS_CREDENTIALS_ENDPOINT + path);
        }
        @Override
        public CredentialsEndpointRetryPolicy getRetryPolicy() {
            return ContainerCredentialsRetryPolicy.getInstance();
        }

    }

    /**
     * A URI resolver that uses environment variable {@value CONTAINER_CREDENTIALS_FULL_URI} as the URI
     * for the metadata service.
     * Optionally an authorization token can be provided using the {@value CONTAINER_AUTHORIZATION_TOKEN} environment variable.
     */
    // IBM unsupported
    // static class FullUriCredentialsEndpointProvider extends CredentialsEndpointProvider {

    //     @Override
    //     public URI getCredentialsEndpoint() {
    //         String fullUri = System.getenv(CONTAINER_CREDENTIALS_FULL_URI);
    //         if (fullUri == null || fullUri.length() == 0) {
    //             throw new SdkClientException("The environment variable " + CONTAINER_CREDENTIALS_FULL_URI + " is empty");
    //         }

    //         URI uri = URI.create(fullUri);

    //         if (!ALLOWED_FULL_URI_HOSTS.contains(uri.getHost())) {
    //             throw new SdkClientException("The full URI (" + uri + ") contained withing environment variable " +
    //                 CONTAINER_CREDENTIALS_FULL_URI + " has an invalid host. Host can only be one of [" +
    //                 CollectionUtils.join(ALLOWED_FULL_URI_HOSTS, ", ") + "]");
    //         }

    //         return uri;
    //     }

//         @Override
//         public Map<String, String> getHeaders() {
//            String tokenValue = getTokenValue();
//            if (StringUtils.isNullOrEmpty(tokenValue) ) {
//                return new HashMap<String, String>();
//            }
//            return Collections.singletonMap("Authorization", tokenValue);
//        }

//        private String getTokenValue() {
//         if (System.getenv(CONTAINER_AUTHORIZATION_TOKEN) != null) {
//                return System.getenv(CONTAINER_AUTHORIZATION_TOKEN);
//            } else if (System.getenv(CONTAINER_AUTHORIZATION_TOKEN_FILE) != null) {
//                String tokenFile = System.getenv(CONTAINER_AUTHORIZATION_TOKEN_FILE);
//                return readToken(tokenFile);
//            }
//            return null;
//        }

//        private String readToken(String tokenFile) {
//            try {
//                byte[] bytes = Files.readAllBytes(FileSystems.getDefault().getPath(tokenFile));
//                return new String(bytes, StringUtils.UTF8);
//            } catch (IOException e) {
//                throw new SdkClientException(
//                        String.format("Cannot fetch credentials from container - failed to read %s", tokenFile));
//            }
//        }

//        private boolean isHttps(URI endpoint) {
//            return Objects.equals(HTTPS, endpoint.getScheme());
//        }

        /**
         * Determines if the addresses for a given host are resolved to a loopback address.
         * <p>
         *     This is a best-effort in determining what address a host will be resolved to. DNS caching might be disabled,
         *     or could expire between this check and when the API is invoked.
         * </p>
         * @param host The name or IP address of the host.
         * @return A boolean specifying whether the host is allowed as an endpoint for credentials loading.
         */
//        private boolean isAllowedHost(String host) {
//            try {
//                InetAddress[] addresses = InetAddress.getAllByName(host);
//                boolean allAllowed = true;
//                for (InetAddress address: addresses) {
//                    if (!isLoopbackAddress(address)) {
//                        allAllowed = false;
//                    }
//                }
//
//                return addresses.length > 0 && (allAllowed || isMetadataServiceEndpoint(host));
//
//            } catch (UnknownHostException e) {
//                throw new SdkClientException(String.format("host (%s) could not be resolved to an IP address.", host), e);
//            }
//        }

//        private boolean isLoopbackAddress(InetAddress inetAddress) {
//            return inetAddress.isLoopbackAddress();
//        }

//        private boolean isMetadataServiceEndpoint(String host) {
//            String mode = System.getenv(AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE);
//            if ("IPV6".equalsIgnoreCase(mode)) {
//                return VALID_LOOP_BACK_IPV6.contains(host);
//            }
//            return VALID_LOOP_BACK_IPV4.contains(host);
//        }

//        @Override
//        public CredentialsEndpointRetryPolicy getRetryPolicy() {
//            return ContainerCredentialsRetryPolicy.getInstance();
//        }
//    }

}
