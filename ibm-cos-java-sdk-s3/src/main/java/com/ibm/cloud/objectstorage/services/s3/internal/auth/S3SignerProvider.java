/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
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
package com.ibm.cloud.objectstorage.services.s3.internal.auth;

import com.ibm.cloud.objectstorage.AmazonWebServiceClient;
import com.ibm.cloud.objectstorage.auth.RegionAwareSigner;
//IBM unsupported
//import com.ibm.cloud.objectstorage.auth.ServiceAwareSigner;
import com.ibm.cloud.objectstorage.auth.Signer;
import com.ibm.cloud.objectstorage.internal.auth.SignerProviderContext;
import com.ibm.cloud.objectstorage.internal.auth.SignerProvider;
//IBM unsupported
//import com.ibm.cloud.objectstorage.regions.EndpointToRegion;
import com.ibm.cloud.objectstorage.services.s3.internal.ServiceUtils;
import com.ibm.cloud.objectstorage.util.AwsHostNameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;

public class S3SignerProvider extends SignerProvider {

    private static Log log = LogFactory.getLog(S3SignerProvider.class);

    private final AmazonWebServiceClient awsClient;
    private Signer signer;

    public S3SignerProvider(final AmazonWebServiceClient awsClient,
                            final Signer defaultSigner) {
        this.awsClient = awsClient;
        this.signer = defaultSigner;
    }

    @Override
    public Signer getSigner(SignerProviderContext signerProviderContext) {
        URI uri = signerProviderContext.getUri();

        // Return the default signer if no URI is passed, the client is configured with region override,
        // or if we are hitting an accelerate endpoint.
        if (uri == null || ServiceUtils.isS3AccelerateEndpoint(uri.getHost()) || isSignerRegionOverrideSet()) {
            return signer;
        }

        if (signer instanceof RegionAwareSigner && !isAccessPointUri(uri)) {
            // Parse region name from the host component of the URL and
            // assign it to the signer
            RegionAwareSigner regionSigner = (RegionAwareSigner) signer;

            try {
                //IBM specific
                //regionSigner.setRegionName(EndpointToRegion.guessRegionNameForEndpoint(uri.getHost(), "s3"));
                regionSigner.setRegionName(AwsHostNameUtils.parseRegionName(uri.getHost(), "s3"));

            } catch (RuntimeException e) {
                log.warn("Failed to parse the endpoint " + uri +
                         ", and skip re-assigning the signer region", e);
            }
        }

//IBM unsupported
//        Request<?> request = signerProviderContext.getRequest();
//        if (!isSignerOverridden() &&
//            request != null &&
//            request.getHandlerContext(HandlerContextKey.SIGNING_NAME) != null) {
//            // S3 signer is created per-request, so no need to create a new one
//            String signingName = request.getHandlerContext(HandlerContextKey.SIGNING_NAME);
//            if (signer instanceof ServiceAwareSigner) {
//                ((ServiceAwareSigner)(signer)).setServiceName(signingName);
//            }
//        }

        return signer;
    }

    private boolean isAccessPointUri(URI uri) {
        String str = uri.toASCIIString();
        return str.contains(".s3-accesspoint.");
        //IBM unsupported
        //|| str.contains(".s3-outposts.") || str.contains(".s3-object-lambda.");
    }

    private boolean isSignerRegionOverrideSet() {
        return awsClient != null && awsClient.getSignerRegionOverride() != null;
    }

    private boolean isSignerOverridden() {
        return awsClient.getSignerOverride() != null;
    }

    /**
     * This is necessary for S3 since we update the signer during request processing dependent on
     * which region the bucket exists in.
     */
    public void setSigner(Signer signer) {
        this.signer = signer;
    }

}
