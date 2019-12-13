/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.auth;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.cloud.objectstorage.AmazonClientException;

/**
 * Credentials provider implementation that loads credentials from the Amazon
 * EC2 Instance Metadata Service.
 */
public class InstanceProfileCredentialsProvider implements AWSCredentialsProvider {

    private static final Log LOG = LogFactory.getLog(InstanceProfileCredentialsProvider.class);

    /**
     * The wait time, after which the background thread initiates a refresh to
     * load latest credentials if needed.
     */
    private static final int ASYNC_REFRESH_INTERVAL_TIME_MINUTES = 1;

    /**
     * The default InstanceProfileCredentialsProvider that can be shared by
     * multiple CredentialsProvider instance threads to shrink the amount of
     * requests to EC2 metadata service.
     */
    private static final InstanceProfileCredentialsProvider INSTANCE
        = new InstanceProfileCredentialsProvider();

    private final InstanceMetadataServiceCredentialsFetcher credentialsFetcher;

    /**
     * The executor service used for refreshing the credentials in the
     * background.
     */
    private volatile ScheduledExecutorService executor;

    private volatile boolean shouldRefresh = false;

    /**
     * @deprecated for the singleton method {@link #getInstance()}.
     */
    @Deprecated
    public InstanceProfileCredentialsProvider() {
        this(false);
    }

    /**
     * Spins up a new thread to refresh the credentials asynchronously if
     * refreshCredentialsAsync is set to true, otherwise the credentials will be
     * refreshed from the instance metadata service synchronously,
     *
     * @param refreshCredentialsAsync
     *            true if credentials needs to be refreshed asynchronously else
     *            false.
     */
    public InstanceProfileCredentialsProvider(boolean refreshCredentialsAsync) { this(refreshCredentialsAsync, true); }

    /**
     * Spins up a new thread to refresh the credentials asynchronously.
     * @param eagerlyRefreshCredentialsAsync
     *            when set to false will not attempt to refresh credentials asynchronously
     *            until after a call has been made to {@link #getCredentials()} - ensures that
     *            {@link ContainerCredentialsFetcher#getCredentials()} is only hit when this CredentialProvider is actually required
     */
    public static InstanceProfileCredentialsProvider createAsyncRefreshingProvider(final boolean eagerlyRefreshCredentialsAsync) {
        return new InstanceProfileCredentialsProvider(true, eagerlyRefreshCredentialsAsync);
    }


    private InstanceProfileCredentialsProvider(boolean refreshCredentialsAsync, final boolean eagerlyRefreshCredentialsAsync) {
        credentialsFetcher = new InstanceMetadataServiceCredentialsFetcher();
        shouldRefresh = eagerlyRefreshCredentialsAsync;
        if (refreshCredentialsAsync) {
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (shouldRefresh) credentialsFetcher.getCredentials();
                    } catch (AmazonClientException ace) {
                        handleError(ace);
                    } catch (RuntimeException re) {
                        handleError(re);
                    } catch (Error e) {
                        handleError(e);
                    }
                }
            }, 0, ASYNC_REFRESH_INTERVAL_TIME_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * Returns a singleton {@link InstanceProfileCredentialsProvider} that does not refresh credentials
     * asynchronously. Use {@link #InstanceProfileCredentialsProvider(boolean)} for the feature.
     */
    public static InstanceProfileCredentialsProvider getInstance() {
        return INSTANCE;
    }

    private void handleError(Throwable t) {
        refresh();
        LOG.error(t.getMessage(), t);
    }

    @Override
    protected void finalize() throws Throwable {
        if (executor != null) {
            executor.shutdownNow();
        }
    }


    @Override
    public AWSCredentials getCredentials() {
        AWSCredentials creds = credentialsFetcher.getCredentials();
        shouldRefresh = true;
        return creds;
    }

    @Override
    public void refresh() {
        credentialsFetcher.refresh();
    }

}
