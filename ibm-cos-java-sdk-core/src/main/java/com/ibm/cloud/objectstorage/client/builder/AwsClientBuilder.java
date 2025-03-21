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
package com.ibm.cloud.objectstorage.client.builder;

import com.ibm.cloud.objectstorage.AmazonWebServiceClient;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.ClientConfigurationFactory;
import com.ibm.cloud.objectstorage.PredefinedClientConfigurations;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.NotThreadSafe;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.SdkProtectedApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.DefaultAWSCredentialsProviderChain;
import com.ibm.cloud.objectstorage.client.AwsAsyncClientParams;
import com.ibm.cloud.objectstorage.client.AwsSyncClientParams;
import com.ibm.cloud.objectstorage.monitoring.MonitoringListener;
import com.ibm.cloud.objectstorage.handlers.RequestHandler2;
import com.ibm.cloud.objectstorage.metrics.RequestMetricCollector;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenProvider;
import com.ibm.cloud.objectstorage.oauth.IBMOAuthCredentials;
import com.ibm.cloud.objectstorage.regions.AwsRegionProvider;
import com.ibm.cloud.objectstorage.regions.DefaultAwsRegionProviderChain;
import com.ibm.cloud.objectstorage.regions.Region;
import com.ibm.cloud.objectstorage.regions.RegionUtils;
import com.ibm.cloud.objectstorage.regions.Regions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Base class for all service specific client builders.
 *
 * @param <Subclass> Concrete builder type, used for better fluent methods.
 * @param <TypeToBuild>  Type that this builder builds.
 */
@NotThreadSafe
@SdkProtectedApi
public abstract class AwsClientBuilder<Subclass extends AwsClientBuilder, TypeToBuild> {

    /**
     * Default Region Provider chain. Used only when the builder is not explicitly configured with a
     * region.
     */
    private static final AwsRegionProvider DEFAULT_REGION_PROVIDER = new DefaultAwsRegionProviderChain();

    /**
     * Different services may have custom client configuration factories to vend defaults tailored
     * for that service. If no explicit client configuration is provided to the builder the default
     * factory for the service is used.
     */
    private final ClientConfigurationFactory clientConfigFactory;

    /**
     * {@link AwsRegionProvider} to use when no explicit region or endpointConfiguration is configured.
     * This is currently not exposed for customization by customers.
     */
    private final AwsRegionProvider regionProvider;

    //IBM unsupported
    //private final AdvancedConfig.Builder advancedConfig = AdvancedConfig.builder();

    private AWSCredentialsProvider credentials;
    private ClientConfiguration clientConfig;
    private RequestMetricCollector metricsCollector;
    private Region region;
    private List<RequestHandler2> requestHandlers;
    private EndpointConfiguration endpointConfiguration;
    //IBM unsupported
    // private CsmConfigurationProvider csmConfig;
    private MonitoringListener monitoringListener;
    private String iamEndpoint;
    private double iamTokenRefreshOffset;
    private int iamMaxRetry;

    protected AwsClientBuilder(ClientConfigurationFactory clientConfigFactory) {
        this(clientConfigFactory, DEFAULT_REGION_PROVIDER);
    }

    @SdkTestInternalApi
    protected AwsClientBuilder(ClientConfigurationFactory clientConfigFactory,
                               AwsRegionProvider regionProvider) {
        this.clientConfigFactory = clientConfigFactory;
        this.regionProvider = regionProvider;
    }

    /**
     * Gets the AWSCredentialsProvider currently configured in the builder.
     */
    public final AWSCredentialsProvider getCredentials() {
        return this.credentials;
    }

    /**
     * Sets the AWSCredentialsProvider used by the client. If not specified the default is {@link
     * DefaultAWSCredentialsProviderChain}.
     *
     * @param credentialsProvider New AWSCredentialsProvider to use.
     */
    public final void setCredentials(AWSCredentialsProvider credentialsProvider) {
        this.credentials = credentialsProvider;
        if (null != this.iamEndpoint){
            if ((this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
                ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
                    ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamEndpoint(iamEndpoint);
                    if (((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).getProvider() instanceof DefaultTokenProvider){
                        ((DefaultTokenProvider)((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).getProvider()).setIamEndpoint(iamEndpoint);
                    }
            }
        }

        if (this.iamTokenRefreshOffset > 0){
            if ((this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
                ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
                    ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamRefreshOffset(iamTokenRefreshOffset);
            }
        }

        if (this.iamMaxRetry > 0){
            if ((this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
                    ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
                    ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamMaxRetry(iamMaxRetry);;
            }
        }
    }

    /**
     * Sets the AWSCredentialsProvider used by the client. If not specified the default is {@link
     * DefaultAWSCredentialsProviderChain}.
     *
     * @param credentialsProvider New AWSCredentialsProvider to use.
     * @return This object for method chaining.
     */
    public final Subclass withCredentials(AWSCredentialsProvider credentialsProvider) {
        setCredentials(credentialsProvider);
        return getSubclass();
    }

    /**
     * Sets the IAM endpoint to use for token retrieval by the DefaultTokenManager
     * and the DefaultTokenProvider. This should only be over written
     * for a dev or staging environment
     *
     * @param iamEndpoint, http endpoint for token retrieval
     * @return This object for method chaining.
     */
    public Subclass withIAMEndpoint(String iamEndpoint) {
        this.iamEndpoint = iamEndpoint;

        if ((this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
                ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
                    ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamEndpoint(iamEndpoint);
                    if (((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).getProvider() instanceof DefaultTokenProvider){
                        ((DefaultTokenProvider)((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).getProvider()).setIamEndpoint(iamEndpoint);
                        ((DefaultTokenProvider)((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).getProvider()).retrieveToken();
                    }
        }
        return getSubclass();
    }

    /**
     * Sets the time offset used for IAM token refresh by the DefaultTokenManager.
     * This should only be over written for a dev or staging environment
     *
     * @param offset, percentage of token life before expiration that token should be refreshed.
     * @return This object for method chaining.
     */
    public Subclass withIAMTokenRefresh(double offset) {
        this.iamTokenRefreshOffset = offset;

        if ((offset > 0) &&
                (this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
                ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
                ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamRefreshOffset(iamTokenRefreshOffset);
        }
        return getSubclass();
    }

    /**
     * Sets the maximum number of attempts for retrieving/refreshing IAM token by the DefaultTokenManager.
     * This should only be over written for a dev or staging environment
     *
     * @param offset, offset in seconds from token expiry time.
     * @return This object for method chaining.
     */
    public Subclass withIAMMaxRetry(int retryCount) {
        this.iamMaxRetry = retryCount;

        if ((retryCount > 0) &&
            (this.credentials.getCredentials() instanceof IBMOAuthCredentials) &&
            ((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager() instanceof DefaultTokenManager){
            ((DefaultTokenManager)((IBMOAuthCredentials)this.credentials.getCredentials()).getTokenManager()).setIamMaxRetry(iamMaxRetry);
        }
        return getSubclass();
    }

    /**
     * If the builder isn't explicitly configured with credentials we use the {@link
     * DefaultAWSCredentialsProviderChain}.
     */
    private AWSCredentialsProvider resolveCredentials() {
        return (credentials == null) ? DefaultAWSCredentialsProviderChain.getInstance() : credentials;
    }

    /**
     * Gets the ClientConfiguration currently configured in the builder
     */
    public final ClientConfiguration getClientConfiguration() {
        return this.clientConfig;
    }

    /**
     * Sets the ClientConfiguration to be used by the client. If not specified the default is
     * typically {@link PredefinedClientConfigurations#defaultConfig} but may differ per service.
     *
     * @param config Custom configuration to use
     */
    public final void setClientConfiguration(ClientConfiguration config) {
        this.clientConfig = config;
    }

    /**
     * Sets the ClientConfiguration to be used by the client. If not specified the default is
     * typically {@link PredefinedClientConfigurations#defaultConfig} but may differ per service.
     *
     * @param config Custom configuration to use
     * @return This object for method chaining.
     */
    public final Subclass withClientConfiguration(ClientConfiguration config) {
        setClientConfiguration(config);
        return getSubclass();
    }

    /**
     * If not explicit client configuration is provided we consult the {@link
     * ClientConfigurationFactory} of the service. If an explicit configuration is provided we use
     * ClientConfiguration's copy constructor to avoid mutation.
     */
    private ClientConfiguration resolveClientConfiguration() {
        return (clientConfig == null) ? clientConfigFactory.getConfig() :
                new ClientConfiguration(clientConfig);
    }

    /**
     * Gets the {@link RequestMetricCollector} in use by the builder.
     */
    public final RequestMetricCollector getMetricsCollector() {
        return this.metricsCollector;
    }

    /**
     * Sets a custom RequestMetricCollector to use for the client.
     *
     * @param metrics Custom RequestMetricCollector to use.
     */
    public final void setMetricsCollector(RequestMetricCollector metrics) {
        this.metricsCollector = metrics;
    }

    /**
     * Sets a custom RequestMetricCollector to use for the client.
     *
     * @param metrics Custom RequestMetricCollector to use.
     * @return This object for method chaining.
     */
    public final Subclass withMetricsCollector(RequestMetricCollector metrics) {
        setMetricsCollector(metrics);
        return getSubclass();
    }

    /**
     * Gets the region in use by the builder.
     */
    public final String getRegion() {
        return region == null ? null : region.getName();
    }

    /**
     * Sets the region to be used by the client. This will be used to determine both the
     * service endpoint (eg: https://sns.us-west-1.amazonaws.com) and signing region (eg: us-west-1)
     * for requests. If neither region or endpoint configuration {@link #setEndpointConfiguration(EndpointConfiguration)}
     * are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * @param region Region to use
     */
    public final void setRegion(String region) {
        withRegion(region);
    }

    /**
     * Sets the region to be used by the client. This will be used to determine both the
     * service endpoint (eg: https://sns.us-west-1.amazonaws.com) and signing region (eg: us-west-1)
     * for requests. If neither region or endpoint configuration {@link #setEndpointConfiguration(EndpointConfiguration)}
     * are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * <p> For regions not explicitly in the {@link Regions} enum use the {@link
     * #withRegion(String)} overload.</p>
     *
     * @param region Region to use
     * @return This object for method chaining.
     */
    public final Subclass withRegion(Regions region) {
        return withRegion(region.getName());
    }

    /**
     * Sets the region to be used by the client. This will be used to determine both the
     * service endpoint (eg: https://sns.us-west-1.amazonaws.com) and signing region (eg: us-west-1)
     * for requests. If neither region or endpoint configuration {@link #setEndpointConfiguration(EndpointConfiguration)}
     * are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * @param region Region to use
     * @return This object for method chaining.
     */
    public final Subclass withRegion(String region) {
        return withRegion(getRegionObject(region));
    }

    /**
     * Lookups the {@link Region} object for the given string region name.
     *
     * @param regionStr Region name.
     * @return Region object.
     * @throws SdkClientException If region cannot be found in the metadata.
     */
    private Region getRegionObject(String regionStr) {
        Region regionObj = RegionUtils.getRegion(regionStr);
        if (regionObj == null) {
            throw new SdkClientException(String.format("Could not find region information for '%s' in SDK metadata.",
                                                             regionStr));
        }
        return regionObj;
    }

    /**
     * Sets the region to be used by the client. This will be used to determine both the
     * service endpoint (eg: https://sns.us-west-1.amazonaws.com) and signing region (eg: us-west-1)
     * for requests. If neither region or endpoint configuration {@link #setEndpointConfiguration(EndpointConfiguration)}
     * are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * @param region Region to use, this will be used to determine both service endpoint
     *               and the signing region
     * @return This object for method chaining.
     */
    private Subclass withRegion(Region region) {
        this.region = region;
        return getSubclass();
    }

    /**
     * Gets the service endpointConfiguration in use by the builder
     */
    public final EndpointConfiguration getEndpoint() {
        return endpointConfiguration;
    }

    /**
     * Sets the endpoint configuration (service endpoint & signing region) to be used for requests. If neither region {@link #setRegion(String)}
     * or endpoint configuration are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * <p><b>Only use this if using a non-standard service endpoint - the recommended approach for configuring a client is to use {@link #setRegion(String)}</b>
     *
     * @param endpointConfiguration The endpointConfiguration to use
     */
    public final void setEndpointConfiguration(EndpointConfiguration endpointConfiguration) {
        withEndpointConfiguration(endpointConfiguration);
    }

    /**
     * Sets the endpoint configuration (service endpoint & signing region) to be used for requests. If neither region {@link #withRegion(String)}
     * or endpoint configuration are explicitly provided in the builder the {@link #DEFAULT_REGION_PROVIDER} is consulted.
     *
     * <p><b>Only use this if using a non-standard service endpoint - the recommended approach for configuring a client is to use {@link #withRegion(String)}</b>
     *
     * @param endpointConfiguration The endpointConfiguration to use
     * @return This object for method chaining.
     */
    public final Subclass withEndpointConfiguration(EndpointConfiguration endpointConfiguration) {
        this.endpointConfiguration = endpointConfiguration;
        return getSubclass();
    }

    /**
     * Gets the list of request handlers in use by the builder.
     */
    public final List<RequestHandler2> getRequestHandlers() {
        return this.requestHandlers == null ? null :
                Collections.unmodifiableList(this.requestHandlers);
    }

    /**
     * Sets the request handlers to use in the client.
     *
     * @param handlers Request handlers to use for client.
     */
    public final void setRequestHandlers(RequestHandler2... handlers) {
        this.requestHandlers = Arrays.asList(handlers);
    }

    /**
     * Sets the request handlers to use in the client.
     *
     * @param handlers Request handlers to use for client.
     * @return This object for method chaining.
     */
    public final Subclass withRequestHandlers(RequestHandler2... handlers) {
        setRequestHandlers(handlers);
        return getSubclass();
    }

    //IBM unsupported
    /**
     * Gets the {@link MonitoringListener} in use by the builder.
     */
    // public final MonitoringListener getMonitoringListener() {
    //     return this.monitoringListener;
    // }

    /**
     * Sets a custom MonitoringListener to use for the client.
     *
     * @param monitoringListener Custom Monitoring Listener to use.
     */
    // public final void setMonitoringListener(MonitoringListener monitoringListener) {
    //     this.monitoringListener = monitoringListener;
    // }

    /**
     * Sets a custom MonitoringListener to use for the client.
     *
     * @param monitoringListener Custom MonitoringListener to use.
     * @return This object for method chaining.
     */
    // public final Subclass withMonitoringListener(MonitoringListener monitoringListener) {
    //     setMonitoringListener(monitoringListener);
    //     return getSubclass();
    // }

    /**
     * Request handlers are copied to a new list to avoid mutation, if no request handlers are
     * provided to the builder we supply an empty list.
     */
    private List<RequestHandler2> resolveRequestHandlers() {
        return (requestHandlers == null) ? new ArrayList<RequestHandler2>() :
                new ArrayList<RequestHandler2>(requestHandlers);
    }

    //IBM unsupported
    // public CsmConfigurationProvider getClientSideMonitoringConfigurationProvider() {
    //     return csmConfig;
    // }

    // public void setClientSideMonitoringConfigurationProvider(CsmConfigurationProvider csmConfig) {
    //     this.csmConfig = csmConfig;
    // }

    // public Subclass withClientSideMonitoringConfigurationProvider(
    //         CsmConfigurationProvider csmConfig) {
    //     setClientSideMonitoringConfigurationProvider(csmConfig);
    //     return getSubclass();
    // }

    // private CsmConfigurationProvider resolveClientSideMonitoringConfig() {
    //     return csmConfig == null ? DefaultCsmConfigurationProviderChain.getInstance() : csmConfig;
    // }

    /**
     * Get the current value of an advanced config option.
     * @param key Key of value to get.
     * @param <T> Type of value to get.
     * @return Value if set, otherwise null.
     */
    //IBM unsupported
    // protected final <T> T getAdvancedConfig(AdvancedConfig.Key<T> key) {
    //     return advancedConfig.get(key);
    // }

    /**
     * Sets the value of an advanced config option.
     * @param key Key of value to set.
     * @param value The new value.
     * @param <T> Type of value.
     */
    //IBM unsupported
    // protected final <T> void putAdvancedConfig(AdvancedConfig.Key<T> key, T value) {
    //     advancedConfig.put(key, value);
    // }

    /**
     * Region and endpoint logic is tightly coupled to the client class right now so it's easier to
     * set them after client creation and let the normal logic kick in. Ideally this should resolve
     * the endpoint and signer information here and just pass that information as is to the client.
     *
     * @param clientInterface Client to configure
     */
    @SdkInternalApi
    final TypeToBuild configureMutableProperties(TypeToBuild clientInterface) {
        AmazonWebServiceClient client = (AmazonWebServiceClient) clientInterface;
        setRegion(client);
        client.makeImmutable();
        return clientInterface;
    }

    /**
     * Builds a client with the configure properties.
     *
     * @return Client instance to make API calls with.
     */
    public abstract TypeToBuild build();

    /**
     * @return An instance of AwsSyncClientParams that has all params to be used in the sync client
     * constructor.
     */
    protected final AwsSyncClientParams getSyncClientParams() {
        return new SyncBuilderParams();
    }

    //IBM unsupported
    // protected final AdvancedConfig getAdvancedConfig() {
    //     return advancedConfig.build();
    // }

    private void setRegion(AmazonWebServiceClient client) {
        if (region != null && endpointConfiguration != null) {
            throw new IllegalStateException("Only one of Region or EndpointConfiguration may be set.");
        }
        if (endpointConfiguration != null) {
            client.setEndpoint(endpointConfiguration.getServiceEndpoint());
            client.setSignerRegionOverride(endpointConfiguration.getSigningRegion());
        } else if (region != null) {
            client.setRegion(region);
        } else {
            final String region = determineRegionFromRegionProvider();
            if (region != null) {
                client.setRegion(getRegionObject(region));
            } else {
                throw new SdkClientException(
                        "Unable to find a region via the region provider chain. " +
                        "Must provide an explicit region in the builder or setup environment to supply a region.");
            }
        }
    }

    /**
     * Attempt to determine the region from the configured region provider. This will return null in the event that the
     * region provider could not determine the region automatically.
     */
    private String determineRegionFromRegionProvider() {
        try {
            return regionProvider.getRegion();
        }
        catch (SdkClientException e) {
            // The AwsRegionProviderChain that is used by default throws an exception instead of returning null when
            // the region is not defined. For that reason, we have to support both throwing an exception and returning
            // null as the region not being defined.
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected final Subclass getSubclass() {
        return (Subclass) this;
    }

    /**
     * Presents a view of the builder to be used in a client constructor.
     */
    protected class SyncBuilderParams extends AwsAsyncClientParams {


        private final ClientConfiguration _clientConfig;
        private final AWSCredentialsProvider _credentials;
        private final RequestMetricCollector _metricsCollector;
        private final List<RequestHandler2> _requestHandlers;
        //IBM unsupported
        //private final CsmConfigurationProvider _csmConfig;
        private final MonitoringListener _monitoringListener;
        //IBM unsupported
        //private final AdvancedConfig _advancedConfig;

        protected SyncBuilderParams() {
            this._clientConfig = resolveClientConfiguration();
            this._credentials = resolveCredentials();
            this._metricsCollector = metricsCollector;
            this._requestHandlers = resolveRequestHandlers();
            //IBM unsupported
            //this._csmConfig = resolveClientSideMonitoringConfig();
            this._monitoringListener = monitoringListener;
            //IBM unsupported
            //this._advancedConfig = advancedConfig.build();
        }

        @Override
        public AWSCredentialsProvider getCredentialsProvider() {
            return this._credentials;
        }

        @Override
        public ClientConfiguration getClientConfiguration() {
            return this._clientConfig;
        }

        @Override
        public RequestMetricCollector getRequestMetricCollector() {
            return this._metricsCollector;
        }

        @Override
        public List<RequestHandler2> getRequestHandlers() {
            return this._requestHandlers;
        }

        //IBM unsupported
        // @Override
        // public CsmConfigurationProvider getClientSideMonitoringConfigurationProvider() {
        //     return this._csmConfig;
        // }

        @Override
        public MonitoringListener getMonitoringListener() {
            return this._monitoringListener;
        }

        //IBM unsupported
        // @Override
        // public AdvancedConfig getAdvancedConfig() {
        //     return _advancedConfig;
        // }

        @Override
        public ExecutorService getExecutor() {
            throw new UnsupportedOperationException("ExecutorService is not used for sync client.");
        }

    }

    /**
     * A container for configuration required to submit requests to a service (service endpoint and signing region)
     */
    public static final class EndpointConfiguration {
        private final String serviceEndpoint;
        private final String signingRegion;

        /**
         * @param serviceEndpoint the service endpoint either with or without the protocol (e.g. https://sns.us-west-1.amazonaws.com or sns.us-west-1.amazonaws.com)
         * @param signingRegion the region to use for SigV4 signing of requests (e.g. us-west-1)
         */
        public EndpointConfiguration(String serviceEndpoint, String signingRegion) {
            this.serviceEndpoint = serviceEndpoint;
            this.signingRegion = signingRegion;
        }

        public String getServiceEndpoint() {
            return serviceEndpoint;
        }

        public String getSigningRegion() {
            return signingRegion;
        }
    }

}
