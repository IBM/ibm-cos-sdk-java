/*
 * Copyright 2010-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3;

import static com.ibm.cloud.objectstorage.event.SDKProgressPublisher.publishProgress;
import static com.ibm.cloud.objectstorage.internal.ResettableInputStream.newResettableInputStream;
import static com.ibm.cloud.objectstorage.services.s3.model.S3DataSource.Utils.cleanupDataSource;
import static com.ibm.cloud.objectstorage.util.LengthCheckInputStream.EXCLUDE_SKIPPED_BYTES;
import static com.ibm.cloud.objectstorage.util.LengthCheckInputStream.INCLUDE_SKIPPED_BYTES;
import static com.ibm.cloud.objectstorage.util.Throwables.failure;
import static com.ibm.cloud.objectstorage.util.ValidationUtils.assertNotNull;
import static com.ibm.cloud.objectstorage.util.ValidationUtils.assertStringNotEmpty;

import com.ibm.cloud.objectstorage.AmazonClientException;
import com.ibm.cloud.objectstorage.AmazonServiceException;
import com.ibm.cloud.objectstorage.AmazonServiceException.ErrorType;
import com.ibm.cloud.objectstorage.AmazonWebServiceClient;
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.AmazonWebServiceResponse;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.HttpMethod;
import com.ibm.cloud.objectstorage.Protocol;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.RequestConfig;
import com.ibm.cloud.objectstorage.ResetException;
import com.ibm.cloud.objectstorage.Response;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.annotation.SdkInternalApi;
import com.ibm.cloud.objectstorage.annotation.SdkTestInternalApi;
import com.ibm.cloud.objectstorage.annotation.ThreadSafe;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.Presigner;
import com.ibm.cloud.objectstorage.auth.Signer;
import com.ibm.cloud.objectstorage.auth.SignerFactory;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder;
import com.ibm.cloud.objectstorage.client.builder.AdvancedConfig.Key;
import com.ibm.cloud.objectstorage.event.ProgressEventType;
import com.ibm.cloud.objectstorage.event.ProgressInputStream;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.handlers.HandlerChainFactory;
import com.ibm.cloud.objectstorage.handlers.HandlerContextKey;
import com.ibm.cloud.objectstorage.handlers.RequestHandler2;
import com.ibm.cloud.objectstorage.http.ExecutionContext;
import com.ibm.cloud.objectstorage.http.HttpMethodName;
import com.ibm.cloud.objectstorage.http.HttpResponseHandler;
import com.ibm.cloud.objectstorage.internal.AmazonWebServiceRequestAdapter;
import com.ibm.cloud.objectstorage.internal.DefaultServiceEndpointBuilder;
import com.ibm.cloud.objectstorage.internal.IdentityEndpointBuilder;
import com.ibm.cloud.objectstorage.internal.ReleasableInputStream;
import com.ibm.cloud.objectstorage.internal.ResettableInputStream;
import com.ibm.cloud.objectstorage.internal.ServiceEndpointBuilder;
import com.ibm.cloud.objectstorage.internal.StaticCredentialsProvider;
import com.ibm.cloud.objectstorage.internal.auth.NoOpSignerProvider;
import com.ibm.cloud.objectstorage.internal.auth.SignerProvider;
import com.ibm.cloud.objectstorage.metrics.AwsSdkMetrics;
import com.ibm.cloud.objectstorage.metrics.RequestMetricCollector;
import com.ibm.cloud.objectstorage.oauth.IBMOAuthCredentials;
import com.ibm.cloud.objectstorage.oauth.IBMOAuthSigner;
import com.ibm.cloud.objectstorage.oauth.OAuthServiceException;
import com.ibm.cloud.objectstorage.regions.RegionUtils;
import com.ibm.cloud.objectstorage.regions.Regions;
import com.ibm.cloud.objectstorage.retry.PredefinedRetryPolicies;
import com.ibm.cloud.objectstorage.retry.RetryPolicy;
import com.ibm.cloud.objectstorage.services.s3.internal.AWSS3V4Signer;
import com.ibm.cloud.objectstorage.services.s3.internal.BucketNameUtils;
import com.ibm.cloud.objectstorage.services.s3.internal.CompleteMultipartUploadRetryCondition;
import com.ibm.cloud.objectstorage.services.s3.internal.Constants;
import com.ibm.cloud.objectstorage.services.s3.internal.DeleteObjectTaggingHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.DeleteObjectsResponse;
import com.ibm.cloud.objectstorage.services.s3.internal.DigestValidationInputStream;
import com.ibm.cloud.objectstorage.services.s3.internal.DualstackEndpointBuilder;
import com.ibm.cloud.objectstorage.services.s3.internal.GetObjectTaggingResponseHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.InitiateMultipartUploadHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.InputSubstream;
import com.ibm.cloud.objectstorage.services.s3.internal.ListPartsHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.MD5DigestCalculatingInputStream;
import com.ibm.cloud.objectstorage.services.s3.internal.Mimetypes;
import com.ibm.cloud.objectstorage.services.s3.internal.MultiFileOutputStream;
import com.ibm.cloud.objectstorage.services.s3.internal.ObjectExpirationHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.ResponseHeaderHandlerChain;
import com.ibm.cloud.objectstorage.services.s3.internal.S3AbortableInputStream;
import com.ibm.cloud.objectstorage.services.s3.internal.S3ErrorResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.S3MetadataResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.S3ObjectResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.S3QueryStringSigner;
import com.ibm.cloud.objectstorage.services.s3.internal.S3RequestEndpointResolver;
import com.ibm.cloud.objectstorage.services.s3.internal.S3RequesterChargedHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.S3Signer;
import com.ibm.cloud.objectstorage.services.s3.internal.S3V4AuthErrorRetryStrategy;
import com.ibm.cloud.objectstorage.services.s3.internal.S3VersionHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.S3XmlResponseHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.ServerSideEncryptionHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.ServiceUtils;
import com.ibm.cloud.objectstorage.services.s3.internal.SetObjectTaggingResponseHeaderHandler;
import com.ibm.cloud.objectstorage.services.s3.internal.SkipMd5CheckStrategy;
import com.ibm.cloud.objectstorage.services.s3.internal.UploadObjectStrategy;
import com.ibm.cloud.objectstorage.services.s3.internal.XmlWriter;
import com.ibm.cloud.objectstorage.services.s3.internal.auth.S3SignerProvider;
import com.ibm.cloud.objectstorage.services.s3.metrics.S3ServiceMetric;
import com.ibm.cloud.objectstorage.services.s3.model.AbortMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.AccessControlList;
import com.ibm.cloud.objectstorage.services.s3.model.AddLegalHoldRequest;
import com.ibm.cloud.objectstorage.services.s3.model.AmazonS3Exception;
import com.ibm.cloud.objectstorage.services.s3.model.Bucket;
import com.ibm.cloud.objectstorage.services.s3.model.BucketCrossOriginConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketLifecycleConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketProtectionConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketReplicationConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketTaggingConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketVersioningConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.BucketWebsiteConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.CannedAccessControlList;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.CopyObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CopyObjectResult;
import com.ibm.cloud.objectstorage.services.s3.model.CopyPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.CopyPartResult;
import com.ibm.cloud.objectstorage.services.s3.model.CreateBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketCrossOriginConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketLifecycleConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketReplicationConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketTaggingConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteBucketWebsiteConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteLegalHoldRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteObjectTaggingRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteObjectTaggingResult;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteObjectsResult;
import com.ibm.cloud.objectstorage.services.s3.model.DeletePublicAccessBlockRequest;
import com.ibm.cloud.objectstorage.services.s3.model.DeletePublicAccessBlockResult;
import com.ibm.cloud.objectstorage.services.s3.model.DeleteVersionRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ExtendObjectRetentionRequest;
import com.ibm.cloud.objectstorage.services.s3.model.FASPConnectionInfo;
import com.ibm.cloud.objectstorage.services.s3.model.GeneratePresignedUrlRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GenericBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketAclRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketCrossOriginConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketFaspConnectionInfoRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketLifecycleConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketProtectionConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketReplicationConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketTaggingConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketVersioningConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetBucketWebsiteConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectAclRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectMetadataRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectTaggingRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectTaggingResult;
import com.ibm.cloud.objectstorage.services.s3.model.GetPublicAccessBlockRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetPublicAccessBlockResult;
import com.ibm.cloud.objectstorage.services.s3.model.GetS3AccountOwnerRequest;
import com.ibm.cloud.objectstorage.services.s3.model.Grant;
import com.ibm.cloud.objectstorage.services.s3.model.Grantee;
import com.ibm.cloud.objectstorage.services.s3.model.GroupGrantee;
import com.ibm.cloud.objectstorage.services.s3.model.HeadBucketRequest;
import com.ibm.cloud.objectstorage.services.s3.model.HeadBucketResult;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.ListBucketsExtendedRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListBucketsExtendedResponse;
import com.ibm.cloud.objectstorage.services.s3.model.ListBucketsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListLegalHoldsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListLegalHoldsResult;
import com.ibm.cloud.objectstorage.services.s3.model.ListMultipartUploadsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListNextBatchOfObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListNextBatchOfVersionsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsV2Request;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsV2Result;
import com.ibm.cloud.objectstorage.services.s3.model.ListPartsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ListVersionsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.MultiFactorAuthentication;
import com.ibm.cloud.objectstorage.services.s3.model.MultiObjectDeleteException;
import com.ibm.cloud.objectstorage.services.s3.model.MultipartUploadListing;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectListing;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectTagging;
import com.ibm.cloud.objectstorage.services.s3.model.Owner;
import com.ibm.cloud.objectstorage.services.s3.model.PartETag;
import com.ibm.cloud.objectstorage.services.s3.model.PartListing;
import com.ibm.cloud.objectstorage.services.s3.model.Permission;
import com.ibm.cloud.objectstorage.services.s3.model.PresignedUrlDownloadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.PresignedUrlDownloadResult;
import com.ibm.cloud.objectstorage.services.s3.model.PresignedUrlUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.PresignedUrlUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.PublicAccessBlockConfiguration;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectResult;
import com.ibm.cloud.objectstorage.services.s3.model.Region;
import com.ibm.cloud.objectstorage.services.s3.model.ResponseHeaderOverrides;
import com.ibm.cloud.objectstorage.services.s3.model.RestoreObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.S3AccelerateUnsupported;
import com.ibm.cloud.objectstorage.services.s3.model.S3DataSource;
import com.ibm.cloud.objectstorage.services.s3.model.S3Object;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectInputStream;
import com.ibm.cloud.objectstorage.services.s3.model.SSEAwsKeyManagementParams;
import com.ibm.cloud.objectstorage.services.s3.model.SSEAwsKeyManagementParamsProvider;
import com.ibm.cloud.objectstorage.services.s3.model.SSECustomerKey;
import com.ibm.cloud.objectstorage.services.s3.model.SSECustomerKeyProvider;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketAclRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketCrossOriginConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketLifecycleConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketProtectionConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketReplicationConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketTaggingConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetBucketWebsiteConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectAclRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectTaggingRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectTaggingResult;
import com.ibm.cloud.objectstorage.services.s3.model.SetPublicAccessBlockRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetPublicAccessBlockResult;
import com.ibm.cloud.objectstorage.services.s3.model.StorageClass;
import com.ibm.cloud.objectstorage.services.s3.model.Tag;
import com.ibm.cloud.objectstorage.services.s3.model.UploadObjectRequest;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartResult;
import com.ibm.cloud.objectstorage.services.s3.model.VersionListing;
import com.ibm.cloud.objectstorage.services.s3.model.transform.AclXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.BucketConfigurationXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.GetPublicAccessBlockStaxUnmarshaller;
import com.ibm.cloud.objectstorage.services.s3.model.transform.HeadBucketResultHandler;
import com.ibm.cloud.objectstorage.services.s3.model.transform.MultiObjectDeleteXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.ObjectTaggingXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.RequestPaymentConfigurationXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.RequestXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.Unmarshallers;
import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser.CompleteMultipartUploadHandler;
import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser.CopyObjectResultHandler;
import com.ibm.cloud.objectstorage.services.s3.model.transform.ObjectLockConfigurationXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.ObjectLockLegalHoldXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.model.transform.ObjectLockRetentionXmlFactory;
import com.ibm.cloud.objectstorage.services.s3.request.S3HandlerContextKeys;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectLockConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectLockConfigurationResult;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectLegalHoldRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectLegalHoldResult;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRetentionRequest;
import com.ibm.cloud.objectstorage.services.s3.model.GetObjectRetentionResult;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectLockConfigurationRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectLockConfigurationResult;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectLegalHoldRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectLegalHoldResult;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectRetentionRequest;
import com.ibm.cloud.objectstorage.services.s3.model.SetObjectRetentionResult;
import com.ibm.cloud.objectstorage.services.s3.waiters.AmazonS3Waiters;
import com.ibm.cloud.objectstorage.transform.Unmarshaller;
import com.ibm.cloud.objectstorage.util.AWSRequestMetrics;
import com.ibm.cloud.objectstorage.util.AWSRequestMetrics.Field;
import com.ibm.cloud.objectstorage.util.AwsHostNameUtils;
import com.ibm.cloud.objectstorage.util.Base16;
import com.ibm.cloud.objectstorage.util.Base64;
import com.ibm.cloud.objectstorage.util.BinaryUtils;
import com.ibm.cloud.objectstorage.util.CredentialUtils;
import com.ibm.cloud.objectstorage.util.DateUtils;
import com.ibm.cloud.objectstorage.util.HostnameValidator;
import com.ibm.cloud.objectstorage.util.IOUtils;
import com.ibm.cloud.objectstorage.util.LengthCheckInputStream;
import com.ibm.cloud.objectstorage.util.Md5Utils;
import com.ibm.cloud.objectstorage.util.RuntimeHttpUtils;
import com.ibm.cloud.objectstorage.util.SdkHttpUtils;
import com.ibm.cloud.objectstorage.util.ServiceClientHolderInputStream;
import com.ibm.cloud.objectstorage.util.StringUtils;
import com.ibm.cloud.objectstorage.util.ValidationUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;

/**
 * <p>
 * Provides the client for accessing the Amazon S3 web service.
 * </p>
 * <p>
 * Amazon S3 provides storage for the Internet,
 * and is designed to make web-scale computing easier for developers.
 * </p>
 * <p>
 * The Amazon S3 Java Client provides a simple interface that can be
 * used to store and retrieve any amount of data, at any time,
 * from anywhere on the web. It gives any developer access to the same
 * highly scalable, reliable, secure, fast, inexpensive infrastructure
 * that Amazon uses to run its own global network of web sites.
 * The service aims to maximize benefits of scale and to pass those
 * benefits on to developers.
 * </p>
 * <p>
 * For more information about Amazon S3, please see
 * <a href="http://aws.amazon.com/s3">
 * http://aws.amazon.com/s3</a>
 * </p>
 */

@ThreadSafe
public class AmazonS3Client extends AmazonWebServiceClient implements AmazonS3 {

    public static final String S3_SERVICE_NAME = "s3";

    private static final String S3_SIGNER = "S3SignerType";
    private static final String S3_V4_SIGNER = "AWSS3V4SignerType";
    private static final String SERVICE_ID = "S3";
    private static final String AWS_PARTITION_KEY = "aws";
    //IBM unsupported
    // private static final String S3_OUTPOSTS_NAME = "s3-outposts";
    // private static final String S3_OBJECT_LAMBDAS_NAME = "s3-object-lambda";
    public static final String OAUTH_SIGNER = "OAuthSignerType";

    protected static final AmazonS3ClientConfigurationFactory configFactory
            = new AmazonS3ClientConfigurationFactory();

    /** Shared logger for client events */
    private static Log log = LogFactory.getLog(AmazonS3Client.class);

    static {
        // Enable S3 specific predefined request metrics.
        AwsSdkMetrics.addAll(Arrays.asList(S3ServiceMetric.values()));

        // Register S3-specific signers.
        SignerFactory.registerSigner(S3_SIGNER, S3Signer.class);
        SignerFactory.registerSigner(S3_V4_SIGNER, AWSS3V4Signer.class);
        SignerFactory.registerSigner(OAUTH_SIGNER, IBMOAuthSigner.class);
    }

    private volatile AmazonS3Waiters waiters;

    /** Provider for Amazon Web Services credentials. */
    protected final AWSCredentialsProvider awsCredentialsProvider;

    /** Responsible for handling error responses from all S3 service calls. */
    protected final S3ErrorResponseHandler errorResponseHandler;

    /** Shared response handler for operations with no response.  */
    private final S3XmlResponseHandler<Void> voidResponseHandler = new S3XmlResponseHandler<Void>(null);

    /** Shared factory for converting configuration objects to XML */
    private static final BucketConfigurationXmlFactory bucketConfigurationXmlFactory = new BucketConfigurationXmlFactory();

    /** Shared factory for converting request payment configuration objects to XML */
    private static final RequestPaymentConfigurationXmlFactory requestPaymentConfigurationXmlFactory = new RequestPaymentConfigurationXmlFactory();
    //IBM unsupported
    // private static final UseArnRegionResolver USE_ARN_REGION_RESOLVER = new UseArnRegionResolver();

    /** S3 specific client configuration options */
    private volatile S3ClientOptions clientOptions = S3ClientOptions.builder().build();

    /**
     * The S3 client region that is set by either (a) calling
     * setRegion/configureRegion OR (b) calling setEndpoint with a
     * region-specific S3 endpoint. This region string will be used for signing
     * requests sent by this client.
     */
    private volatile String clientRegion;
    //IBM unsupported
    //private static RegionalEndpointsOptionResolver REGIONAL_ENDPOINTS_OPTION_RESOLVER = new RegionalEndpointsOptionResolver();

    private static final int BUCKET_REGION_CACHE_SIZE = 300;

    private static final Map<String, String> bucketRegionCache =
                 Collections.synchronizedMap(new LinkedHashMap<String, String>(BUCKET_REGION_CACHE_SIZE, 1.1f, true) {
                     private static final long serialVersionUID = 23453L;

                     @Override
                     protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                         return size() > BUCKET_REGION_CACHE_SIZE;
                     }
                 });

    static Map<String, String> getBucketRegionCache() {
        return bucketRegionCache;
    }

    private final SkipMd5CheckStrategy skipMd5CheckStrategy;

    private final CompleteMultipartUploadRetryCondition
            completeMultipartUploadRetryCondition = new CompleteMultipartUploadRetryCondition();

    /**
     * Constructs a new client to invoke service methods on Amazon S3. A
     * credentials provider chain will be used that searches for credentials in
     * this order:
     * <ul>
     * <li>Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY</li>
     * <li>Java System Properties - aws.accessKeyId and aws.secretKey</li>
     * <li>Credential profiles file at the default location (~/.aws/credentials) shared by all Amazon Web Services SDKs
     * and the Amazon Web Services CLI</li>
     * <li>Instance Profile Credentials - delivered through the Amazon EC2
     * metadata service</li>
     * </ul>
     *
     * <p>
     * If no credentials are found in the chain, this client will attempt to
     * work in an anonymous mode where requests aren't signed. Only a subset of
     * the Amazon S3 API will work with anonymous <i>(i.e. unsigned)</i> requests,
     * but this can prove useful in some situations. For example:
     * <ul>
     * <li>If an Amazon S3 bucket has {@link Permission#Read} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can call
     * {@link #listObjects(String)} to see what objects are stored in a bucket.</li>
     * <li>If an object has {@link Permission#Read} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can call
     * {@link #String, String)} and
     * {@link #getObjectMetadata(String, String)} to pull object content and
     * metadata.</li>
     * <li>If a bucket has {@link Permission#Write} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can upload objects
     * to the bucket.</li>
     * </ul>
     * </p>
     * <p>
     * You can force the client to operate in an anonymous mode, and skip the credentials
     * provider chain, by passing in <code>null</code> for the credentials.
     * </p>
     *
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials)
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials, ClientConfiguration)
     * @sample AmazonS3.CreateClient
     * @deprecated use {@link AmazonS3ClientBuilder#defaultClient()}
     */
    @Deprecated
    public AmazonS3Client() {
        this(new S3CredentialsProviderChain());
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials to
     * access Amazon S3.
     *
     * @param awsCredentials
     *            The Amazon Web Services credentials to use when making requests to Amazon S3
     *            with this client.
     *
     * @see AmazonS3Client#AmazonS3Client()
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials, ClientConfiguration)
     * @deprecated use {@link AmazonS3ClientBuilder#withCredentials(AWSCredentialsProvider)}
     */
    @Deprecated
    public AmazonS3Client(AWSCredentials awsCredentials) {
        this(awsCredentials, configFactory.getConfig());
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials and
     * client configuration to access Amazon S3.
     *
     * @param awsCredentials
     *            The Amazon Web Services credentials to use when making requests to Amazon S3
     *            with this client.
     * @param clientConfiguration
     *            The client configuration options controlling how this client
     *            connects to Amazon S3 (e.g. proxy settings, retry counts, etc).
     *
     * @see AmazonS3Client#AmazonS3Client()
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials)
     * @deprecated use {@link AmazonS3ClientBuilder#withCredentials(AWSCredentialsProvider)} and
     *             {@link AmazonS3ClientBuilder#withClientConfiguration(ClientConfiguration)}
     */
    @Deprecated
    public AmazonS3Client(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration) {
        this(new StaticCredentialsProvider(awsCredentials), clientConfiguration);
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials
     * provider to access Amazon S3.
     *
     * @param credentialsProvider
     *            The Amazon Web Services credentials provider which will provide credentials
     *            to authenticate requests with Amazon Web Services services.
     * @deprecated use {@link AmazonS3ClientBuilder#withCredentials(AWSCredentialsProvider)}
     */
    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider) {
        this(credentialsProvider, configFactory.getConfig());
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials and
     * client configuration to access Amazon S3.
     *
     * @param credentialsProvider
     *            The Amazon Web Services credentials provider which will provide credentials
     *            to authenticate requests with Amazon Web Services services.
     * @param clientConfiguration
     *            The client configuration options controlling how this client
     *            connects to Amazon S3 (e.g. proxy settings, retry counts, etc).
     * @deprecated use {@link AmazonS3ClientBuilder#withCredentials(AWSCredentialsProvider)} and
     *             {@link AmazonS3ClientBuilder#withClientConfiguration(ClientConfiguration)}
     */
    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider,
            ClientConfiguration clientConfiguration) {
        this(credentialsProvider, clientConfiguration, null);
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials,
     * client configuration and request metric collector to access Amazon S3.
     *
     * @param credentialsProvider
     *            The Amazon Web Services credentials provider which will provide credentials
     *            to authenticate requests with Amazon Web Services services.
     * @param clientConfiguration
     *            The client configuration options controlling how this client
     *            connects to Amazon S3 (e.g. proxy settings, retry counts, etc).
     * @param requestMetricCollector request metric collector
     * @deprecated use {@link AmazonS3ClientBuilder#withCredentials(AWSCredentialsProvider)} and
     *             {@link AmazonS3ClientBuilder#withClientConfiguration(ClientConfiguration)} and
     *             {@link AmazonS3ClientBuilder#withMetricsCollector(RequestMetricCollector)}
     */
    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider,
            ClientConfiguration clientConfiguration,
            RequestMetricCollector requestMetricCollector) {
        this(credentialsProvider, clientConfiguration, requestMetricCollector, SkipMd5CheckStrategy.INSTANCE);
    }

    /**
     * Constructs a new Amazon S3 client using the specified Amazon Web Services credentials,
     * client configuration and request metric collector to access Amazon S3.
     *
     * @param credentialsProvider
     *            The Amazon Web Services credentials provider which will provide credentials
     *            to authenticate requests with Amazon Web Services services.
     * @param clientConfiguration
     *            The client configuration options controlling how this client
     *            connects to Amazon S3 (e.g. proxy settings, retry counts, etc).
     * @param requestMetricCollector request metric collector
     */
    @SdkTestInternalApi
    AmazonS3Client(AWSCredentialsProvider credentialsProvider,
            ClientConfiguration clientConfiguration,
            RequestMetricCollector requestMetricCollector,
            SkipMd5CheckStrategy skipMd5CheckStrategy) {
        super(clientConfiguration, requestMetricCollector, true);
        this.awsCredentialsProvider = credentialsProvider;
        this.skipMd5CheckStrategy = skipMd5CheckStrategy;
        this.errorResponseHandler = new S3ErrorResponseHandler(clientConfiguration);
        init();
    }

    /**
     * Constructs a new client using the specified client configuration to
     * access Amazon S3. A credentials provider chain will be used that searches
     * for credentials in this order:
     * <ul>
     * <li>Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY</li>
     * <li>Java System Properties - aws.accessKeyId and aws.secretKey</li>
     * <li>Instance Profile Credentials - delivered through the Amazon EC2
     * metadata service</li>
     * </ul>
     *
     * <p>
     * If no credentials are found in the chain, this client will attempt to
     * work in an anonymous mode where requests aren't signed. Only a subset of
     * the Amazon S3 API will work with anonymous <i>(i.e. unsigned)</i>
     * requests, but this can prove useful in some situations. For example:
     * <ul>
     * <li>If an Amazon S3 bucket has {@link Permission#Read} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can call
     * {@link #listObjects(String)} to see what objects are stored in a bucket.</li>
     * <li>If an object has {@link Permission#Read} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can call
     * {@link #getObject(String, String)} and
     * {@link #getObjectMetadata(String, String)} to pull object content and
     * metadata.</li>
     * <li>If a bucket has {@link Permission#Write} permission for the
     * {@link GroupGrantee#AllUsers} group, anonymous clients can upload objects
     * to the bucket.</li>
     * </ul>
     * </p>
     * <p>
     * You can force the client to operate in an anonymous mode, and skip the
     * credentials provider chain, by passing in <code>null</code> for the
     * credentials.
     * </p>
     *
     * @param clientConfiguration
     *            The client configuration options controlling how this client
     *            connects to Amazon S3 (e.g. proxy settings, retry counts, etc).
     *
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials)
     * @see AmazonS3Client#AmazonS3Client(AWSCredentials, ClientConfiguration)
     * @deprecated use {@link AmazonS3ClientBuilder#withClientConfiguration(ClientConfiguration)}
     */
    @Deprecated
    public AmazonS3Client(ClientConfiguration clientConfiguration) {
        this(new S3CredentialsProviderChain(), clientConfiguration);
    }

    /**
     * Constructs a new client to invoke service methods on S3 using the specified parameters. All
     * service calls made using this new client object are blocking, and will not return until the
     * service call completes.
     *
     * @param s3ClientParams Object providing S3 client parameters.
     * @see AmazonS3ClientBuilder For a fluent way to construct a client.
     */
    @SdkInternalApi
    AmazonS3Client(AmazonS3ClientParams s3ClientParams) {
        super(s3ClientParams.getClientParams());
        this.awsCredentialsProvider = s3ClientParams.getClientParams().getCredentialsProvider();
        this.skipMd5CheckStrategy = SkipMd5CheckStrategy.INSTANCE;
        setS3ClientOptions(s3ClientParams.getS3ClientOptions());
        this.errorResponseHandler = new S3ErrorResponseHandler(
            s3ClientParams.getClientParams().getClientConfiguration());
        init();
    }

    public static AmazonS3ClientBuilder builder() {
        return AmazonS3ClientBuilder.standard();
    }

    private void init() {

        // calling this.setEndpoint(...) will also modify the signer accordingly
        setEndpoint(Constants.S3_HOSTNAME);

        HandlerChainFactory chainFactory = new HandlerChainFactory();
        requestHandler2s.addAll(chainFactory.newRequestHandlerChain(
                "/com/ibm/cloud/objectstorage/services/s3/request.handlers"));
        requestHandler2s.addAll(chainFactory.newRequestHandler2Chain(
                "/com/ibm/cloud/objectstorage/services/s3/request.handler2s"));
        requestHandler2s.addAll(chainFactory.getGlobalHandlers());
    }


    /**
     * @deprecated use {@link AmazonS3ClientBuilder#setEndpointConfiguration(AwsClientBuilder.EndpointConfiguration)}
     */
    @Override
    @Deprecated
    public synchronized void setEndpoint(String endpoint) {
        if (ServiceUtils.isS3AccelerateEndpoint(endpoint)) {
            throw new IllegalStateException("To enable accelerate mode, please use AmazonS3ClientBuilder.withAccelerateModeEnabled(true)");
        } else {
            super.setEndpoint(endpoint);
            /*
             * Extract the region string from the endpoint if it's not known to be a
             * global S3 endpoint.
             */
            if (!ServiceUtils.isS3USStandardEndpoint(endpoint)) {
                clientRegion = AwsHostNameUtils.parseRegionName(this.endpoint.getHost(), S3_SERVICE_NAME);
            }
        }
    }

    /**
     * @deprecated use {@link AmazonS3ClientBuilder#setRegion(String)}
     */
    @Override
    @Deprecated
    public synchronized void setRegion(com.ibm.cloud.objectstorage.regions.Region region) {
        //IBM unsupported
        // if (region.getName().equalsIgnoreCase("us-east-1")) {
        //     if (clientOptions.isRegionalUsEast1EndpointEnabled() || REGIONAL_ENDPOINTS_OPTION_RESOLVER.useRegionalMode()) {
        //         region = RegionUtils.getRegion("us-east-1-regional");
        //     }
        // }

        super.setRegion(region);
        /*
         * We need to preserve the user provided region. This is because the
         * region might be mapped to a global s3 endpoint (e.g. when the client
         * is in accelerate mode), in which case we won't be able to extract the
         * region back from the endpoint during request signing phase.
         */
        clientRegion = region.getName();
    }

    /**
     * <p>
     * Override the default S3 client options for this client. Also set the
     * endpoint to s3-accelerate if such is specified in the S3 client options.
     * </p>
     *
     * @param clientOptions
     *            The S3 client options to use.
     */
    @Override
    public synchronized void setS3ClientOptions(S3ClientOptions clientOptions) {
        checkMutability();
        this.clientOptions = new S3ClientOptions(clientOptions);
    }

    /**
     * S3 uses wildcard certificates so we have to disable strict hostname verification when using
     * SSL.
     */
    @Override
    protected boolean useStrictHostNameVerification() {
        return false;
    }

    @Override
    public VersionListing listNextBatchOfVersions(VersionListing previousVersionListing)
            throws SdkClientException, AmazonServiceException {
        return listNextBatchOfVersions(new ListNextBatchOfVersionsRequest(previousVersionListing));
    }

    @Override
    public VersionListing listNextBatchOfVersions(ListNextBatchOfVersionsRequest listNextBatchOfVersionsRequest) {
        listNextBatchOfVersionsRequest = beforeClientExecution(listNextBatchOfVersionsRequest);
        rejectNull(listNextBatchOfVersionsRequest,
                "The request object parameter must be specified when listing the next batch of versions in a bucket");
        VersionListing previousVersionListing = listNextBatchOfVersionsRequest.getPreviousVersionListing();

        if (!previousVersionListing.isTruncated()) {
            VersionListing emptyListing = new VersionListing();
            emptyListing.setBucketName(previousVersionListing.getBucketName());
            emptyListing.setDelimiter(previousVersionListing.getDelimiter());
            emptyListing.setKeyMarker(previousVersionListing.getNextKeyMarker());
            emptyListing.setVersionIdMarker(previousVersionListing.getNextVersionIdMarker());
            emptyListing.setMaxKeys(previousVersionListing.getMaxKeys());
            emptyListing.setPrefix(previousVersionListing.getPrefix());
            emptyListing.setEncodingType(previousVersionListing.getEncodingType());
            emptyListing.setTruncated(false);

            return emptyListing;
        }

        return listVersions(listNextBatchOfVersionsRequest.toListVersionsRequest());
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix)
            throws SdkClientException, AmazonServiceException {
        return listVersions(new ListVersionsRequest(bucketName, prefix, null, null, null, null));
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix, String keyMarker, String versionIdMarker, String delimiter, Integer maxKeys)
            throws SdkClientException, AmazonServiceException {

        ListVersionsRequest request = new ListVersionsRequest()
            .withBucketName(bucketName)
            .withPrefix(prefix)
            .withDelimiter(delimiter)
            .withKeyMarker(keyMarker)
            .withVersionIdMarker(versionIdMarker)
            .withMaxResults(maxKeys);
        return listVersions(request);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest)
            throws SdkClientException, AmazonServiceException {
        listVersionsRequest = beforeClientExecution(listVersionsRequest);
        rejectNull(listVersionsRequest.getBucketName(), "The bucket name parameter must be specified when listing versions in a bucket");

        /**
         * This flag shows whether we need to url decode S3 key names. This flag is enabled
         * only when the customers don't explicitly call {@link listVersionsRequest#setEncodingType(String)},
         * otherwise, it will be disabled for maintaining backwards compatibility.
         */
        final boolean shouldSDKDecodeResponse = listVersionsRequest.getEncodingType() == null;

        Request<ListVersionsRequest> request = createRequest(listVersionsRequest.getBucketName(), null, listVersionsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjectVersions");
        request.addParameter("versions", null);

        addParameterIfNotNull(request, "prefix", listVersionsRequest.getPrefix());
        addParameterIfNotNull(request, "key-marker", listVersionsRequest.getKeyMarker());
        addParameterIfNotNull(request, "version-id-marker", listVersionsRequest.getVersionIdMarker());
        addParameterIfNotNull(request, "delimiter", listVersionsRequest.getDelimiter());

        if (listVersionsRequest.getMaxResults() != null && listVersionsRequest.getMaxResults() >= 0) request.addParameter("max-keys", listVersionsRequest.getMaxResults().toString());
        request.addParameter("encoding-type", shouldSDKDecodeResponse ? Constants.URL_ENCODING : listVersionsRequest.getEncodingType());

        return invoke(request, new Unmarshallers.VersionListUnmarshaller(shouldSDKDecodeResponse), listVersionsRequest.getBucketName(), null);
    }

    @Override
    public ObjectListing listObjects(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix)
            throws SdkClientException, AmazonServiceException {
        return listObjects(new ListObjectsRequest(bucketName, prefix, null, null, null));
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest)
            throws SdkClientException, AmazonServiceException {
        listObjectsRequest = beforeClientExecution(listObjectsRequest);
        rejectNull(listObjectsRequest.getBucketName(), "The bucket name parameter must be specified when listing objects in a bucket");

        /**
         * This flag shows whether we need to url decode S3 key names. This flag is enabled
         * only when the customers don't explicitly call {@link ListObjectsRequest#setEncodingType(String)},
         * otherwise, it will be disabled for maintaining backwards compatibility.
         */
        final boolean shouldSDKDecodeResponse = listObjectsRequest.getEncodingType() == null;

        Request<ListObjectsRequest> request = createRequest(listObjectsRequest.getBucketName(), null, listObjectsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjects");
        addParameterIfNotNull(request, "prefix", listObjectsRequest.getPrefix());
        addParameterIfNotNull(request, "marker", listObjectsRequest.getMarker());
        addParameterIfNotNull(request, "delimiter", listObjectsRequest.getDelimiter());
        if (listObjectsRequest.getMaxKeys() != null && listObjectsRequest.getMaxKeys().intValue() >= 0) request.addParameter("max-keys", listObjectsRequest.getMaxKeys().toString());
        request.addParameter("encoding-type", shouldSDKDecodeResponse ? Constants.URL_ENCODING : listObjectsRequest.getEncodingType());

        //IBM unsupported
        // populateRequesterPaysHeader(request, listObjectsRequest.isRequesterPays());
        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, listObjectsRequest.getWormMirrorDestination());

        return invoke(request, new Unmarshallers.ListObjectsUnmarshaller(shouldSDKDecodeResponse), listObjectsRequest.getBucketName(), null);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName));
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName, String prefix)
            throws SdkClientException, AmazonServiceException {
        return listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix));
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request)
            throws SdkClientException, AmazonServiceException {
        listObjectsV2Request = beforeClientExecution(listObjectsV2Request);
        rejectNull(listObjectsV2Request.getBucketName(), "The bucket name parameter must be specified when listing objects in a bucket");
        Request<ListObjectsV2Request> request = createRequest(listObjectsV2Request.getBucketName(), null, listObjectsV2Request, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjectsV2");

        /**
         * List type '2' is required to opt-in to listObjectsV2.
         */
        request.addParameter("list-type", "2");

        addParameterIfNotNull(request, "start-after", listObjectsV2Request.getStartAfter());
        addParameterIfNotNull(request, "continuation-token", listObjectsV2Request.getContinuationToken());
        addParameterIfNotNull(request, "delimiter", listObjectsV2Request.getDelimiter());
        addParameterIfNotNull(request, "max-keys", listObjectsV2Request.getMaxKeys());
        addParameterIfNotNull(request, "prefix", listObjectsV2Request.getPrefix());
        addParameterIfNotNull(request, "encoding-type", listObjectsV2Request.getEncodingType());
        request.addParameter("fetch-owner", Boolean.toString(listObjectsV2Request.isFetchOwner()));
        //IBM unsupported
        //populateRequesterPaysHeader(request, listObjectsV2Request.isRequesterPays());

        /**
         * If URL encoding has been requested from S3 we'll automatically decode the response.
         */
        final boolean shouldSDKDecodeResponse = Constants.URL_ENCODING.equals(listObjectsV2Request.getEncodingType());

        return invoke(request, new Unmarshallers.ListObjectsV2Unmarshaller(shouldSDKDecodeResponse), listObjectsV2Request.getBucketName(), null);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ObjectListing previousObjectListing)
            throws SdkClientException, AmazonServiceException {
        return listNextBatchOfObjects(new ListNextBatchOfObjectsRequest(previousObjectListing));
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ListNextBatchOfObjectsRequest listNextBatchOfObjectsRequest)
            throws SdkClientException, AmazonServiceException {
        listNextBatchOfObjectsRequest = beforeClientExecution(listNextBatchOfObjectsRequest);
        rejectNull(listNextBatchOfObjectsRequest,
                "The request object parameter must be specified when listing the next batch of objects in a bucket");
        ObjectListing previousObjectListing = listNextBatchOfObjectsRequest.getPreviousObjectListing();

        if (!previousObjectListing.isTruncated()) {
            ObjectListing emptyListing = new ObjectListing();
            emptyListing.setBucketName(previousObjectListing.getBucketName());
            emptyListing.setDelimiter(previousObjectListing.getDelimiter());
            emptyListing.setMarker(previousObjectListing.getNextMarker());
            emptyListing.setMaxKeys(previousObjectListing.getMaxKeys());
            emptyListing.setPrefix(previousObjectListing.getPrefix());
            emptyListing.setEncodingType(previousObjectListing.getEncodingType());
            emptyListing.setTruncated(false);

            return emptyListing;
        }
        return listObjects(listNextBatchOfObjectsRequest.toListObjectsRequest());
    }

    @Override
    public Owner getS3AccountOwner()
            throws SdkClientException, AmazonServiceException {
        return getS3AccountOwner(new GetS3AccountOwnerRequest());
    }

    @Override
    public Owner getS3AccountOwner(GetS3AccountOwnerRequest getS3AccountOwnerRequest)
            throws SdkClientException, AmazonServiceException {
        getS3AccountOwnerRequest = beforeClientExecution(getS3AccountOwnerRequest);
        rejectNull(getS3AccountOwnerRequest, "The request object parameter getS3AccountOwnerRequest must be specified.");
        Request<GetS3AccountOwnerRequest> request = createRequest(null, null, getS3AccountOwnerRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBuckets");
        return invoke(request, new Unmarshallers.ListBucketsOwnerUnmarshaller(), null, null);
    }

    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest)
            throws SdkClientException, AmazonServiceException {
        listBucketsRequest = beforeClientExecution(listBucketsRequest);
        rejectNull(listBucketsRequest, "The request object parameter listBucketsRequest must be specified.");
        Request<ListBucketsRequest> request = createRequest(null, null, listBucketsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBuckets");

        //Add IBM Service Instance Id to headers
        if ((null != this.awsCredentialsProvider ) && (this.awsCredentialsProvider.getCredentials() instanceof IBMOAuthCredentials)) {
            IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)this.awsCredentialsProvider.getCredentials();
            if (oAuthCreds.getServiceInstanceId() != null) {
                request.addHeader(Headers.IBM_SERVICE_INSTANCE_ID, oAuthCreds.getServiceInstanceId());
            }
        }

        return invoke(request, new Unmarshallers.ListBucketsUnmarshaller(), null, null);
    }

    @Override
    public List<Bucket> listBuckets()
            throws SdkClientException, AmazonServiceException {
        return listBuckets(new ListBucketsRequest());
    }
    //IBM unsupported
    // @Override
    // public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     getBucketLocationRequest = beforeClientExecution(getBucketLocationRequest);
    //     rejectNull(getBucketLocationRequest, "The request parameter must be specified when requesting a bucket's location");
    //     String bucketName = getBucketLocationRequest.getBucketName();
    //     rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's location");

    //     Request<GetBucketLocationRequest> request = createRequest(bucketName, null, getBucketLocationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLocation");
    //     request.addParameter("location", null);

    //     return invoke(request, new Unmarshallers.BucketLocationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public String getBucketLocation(String bucketName)
    //         throws SdkClientException, AmazonServiceException {
    //     return getBucketLocation(new GetBucketLocationRequest(bucketName));
    // }

    @Override
    public ListBucketsExtendedResponse listBucketsExtended() throws SdkClientException, AmazonServiceException {
        return listBucketsExtended(new ListBucketsExtendedRequest());
    }

    @Override
    public ListBucketsExtendedResponse listBucketsExtended(ListBucketsExtendedRequest listBucketsExtendedRequest)
            throws SdkClientException, AmazonServiceException {

        listBucketsExtendedRequest = beforeClientExecution(listBucketsExtendedRequest);
        rejectNull(listBucketsExtendedRequest, "The request object parameter listBucketsExtendedRequest must be specified.");
        Request<ListBucketsExtendedRequest> request = createRequest(null, null, listBucketsExtendedRequest, HttpMethodName.GET);
        request.addParameter("extended", null);

        addParameterIfNotNull(request, "marker", listBucketsExtendedRequest.getMarker());
        addParameterIfNotNull(request, "prefix", listBucketsExtendedRequest.getPrefix());
        addParameterIfNotNull(request, "max-keys", listBucketsExtendedRequest.getMaxKeys());

        //Add IBM Service Instance Id to headers
        if ((null != this.awsCredentialsProvider ) && (this.awsCredentialsProvider.getCredentials() instanceof IBMOAuthCredentials)) {
            IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)this.awsCredentialsProvider.getCredentials();
            if (oAuthCreds.getServiceInstanceId() != null) {
                request.addHeader(Headers.IBM_SERVICE_INSTANCE_ID, oAuthCreds.getServiceInstanceId());
            }
        }

        return invoke(request, new Unmarshallers.ListBucketsExtendedUnmarshaller(), null, null);
    }

    @Override
    public Bucket createBucket(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName));
    }

    @Override
    @Deprecated
    public Bucket createBucket(String bucketName, Region region)
            throws SdkClientException, AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    @Deprecated
    public Bucket createBucket(String bucketName, String region)
            throws SdkClientException, AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest)
            throws SdkClientException, AmazonServiceException {
        createBucketRequest = beforeClientExecution(createBucketRequest);
        rejectNull(createBucketRequest,
                   "The CreateBucketRequest parameter must be specified when creating a bucket");

        String bucketName = createBucketRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when creating a bucket");
        bucketName = bucketName.trim();

        String requestRegion = createBucketRequest.getRegion();
        URI requestEndpoint = getCreateBucketEndpoint(requestRegion);

        BucketNameUtils.validateBucketName(bucketName);

        Request<CreateBucketRequest> request = createRequest(bucketName, null, createBucketRequest, HttpMethodName.PUT, requestEndpoint);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateBucket");
        request = addIAMHeaders(request, createBucketRequest);  // IBM-specific

        if (createBucketRequest.getAccessControlList() != null) {
            addAclHeaders(request, createBucketRequest.getAccessControlList());
        } else if (createBucketRequest.getCannedAcl() != null) {
            request.addHeader(Headers.S3_CANNED_ACL, createBucketRequest.getCannedAcl().toString());
        }

        /*
         * If we're talking to a region-specific endpoint other than the US, we
         * *must* specify a location constraint. Try to derive the region from
         * the endpoint.
         */
        if (getSignerRegion() != null && !getSignerRegion().equals("us-east-1") && StringUtils.isNullOrEmpty(requestRegion)) {
            requestRegion = AwsHostNameUtils.parseRegion(requestEndpoint.getHost(), S3_SERVICE_NAME);
        }

        /*
         * We can only send the CreateBucketConfiguration if we're *not*
         * creating a bucket in the US region.
         */
        if (requestRegion != null && !StringUtils.upperCase(requestRegion).equals(Region.US_Standard.toString())) {
            XmlWriter xml = new XmlWriter();
            xml.start("CreateBucketConfiguration", "xmlns", Constants.XML_NAMESPACE);
            xml.start("LocationConstraint").value(requestRegion).end();
            xml.end();

            request.setContent(new ByteArrayInputStream(xml.getBytes()));
        }

        if (createBucketRequest.getObjectLockEnabledForBucket()) {
            request.addHeader(Headers.OBJECT_LOCK_ENABLED_FOR_BUCKET, "true");
        }
// IBM unsupported
//         if (createBucketRequest.getObjectOwnership() != null) {
//             request.addHeader(Headers.OBJECT_OWNERSHIP, createBucketRequest.getObjectOwnership());
//         }

        invoke(request, voidResponseHandler, bucketName, null);

        return new Bucket(bucketName);
    }

    private URI getCreateBucketEndpoint(String requestRegion) {
        // Route to the default endpoint if they're not trying to specify a different one in the request.
        if(requestRegion == null || requestRegion.equals(clientRegion) || !clientOptions.isForceGlobalBucketAccessEnabled()) {
            return endpoint;
        }

        // If they enabled global bucket access and they're trying to create a bucket in a region different than the default
        // one specified when they created the client, it will probably fail because only us-east-1 (actually the global
        // endpoint) is capable of creating buckets outside of its region. Override the endpoint to which the request
        // is routed so that it will succeed.

        com.ibm.cloud.objectstorage.regions.Region targetRegion = com.ibm.cloud.objectstorage.regions.Region.getRegion(Regions.fromName(requestRegion));
        return new DefaultServiceEndpointBuilder(getEndpointPrefix(),
                                                 clientConfiguration.getProtocol().toString()).withRegion(targetRegion)
                                                                                              .getServiceEndpoint();
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, key));
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key, String versionId)
            throws SdkClientException, AmazonServiceException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, key, versionId));
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        getObjectAclRequest = beforeClientExecution(getObjectAclRequest);
        rejectNull(getObjectAclRequest, "The request parameter must be specified when requesting an object's ACL");
        rejectNull(getObjectAclRequest.getBucketName(), "The bucket name parameter must be specified when requesting an object's ACL");
        rejectNull(getObjectAclRequest.getKey(), "The key parameter must be specified when requesting an object's ACL");

        // IBM-specific
        addHeaderIfNotEmptyForAwsRequest(getObjectAclRequest, Headers.MIRROR_DESTINATION, getObjectAclRequest.getWormMirrorDestination());

        return getAcl(getObjectAclRequest.getBucketName(), getObjectAclRequest.getKey(),
                getObjectAclRequest.getVersionId(), getObjectAclRequest.isRequesterPays(),
                getObjectAclRequest);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, AccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, AccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    /**
     * Same as {@link #setObjectAcl(String, String, String, AccessControlList)}
     * but allows specifying a request metric collector.
     */
    public void setObjectAcl(String bucketName, String key, String versionId,
            AccessControlList acl, RequestMetricCollector requestMetricCollector)
            throws SdkClientException, AmazonServiceException {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl)
                .<SetObjectAclRequest> withRequestMetricCollector(requestMetricCollector));
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, CannedAccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    /**
     * Same as {@link #setObjectAcl(String, String, String, CannedAccessControlList)}
     * but allows specifying a request metric collector.
     */
    public void setObjectAcl(String bucketName, String key, String versionId,
            CannedAccessControlList acl,
            RequestMetricCollector requestMetricCollector) {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl)
                .<SetObjectAclRequest> withRequestMetricCollector(requestMetricCollector));
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest)
            throws SdkClientException, AmazonServiceException {
        setObjectAclRequest = beforeClientExecution(setObjectAclRequest);
        rejectNull(setObjectAclRequest,
                "The request must not be null.");
        rejectNull(setObjectAclRequest.getBucketName(),
                "The bucket name parameter must be specified when setting an object's ACL");
        rejectNull(setObjectAclRequest.getKey(),
                "The key parameter must be specified when setting an object's ACL");

        if (setObjectAclRequest.getAcl() != null && setObjectAclRequest.getCannedAcl() != null) {
            throw new IllegalArgumentException(
                    "Only one of the ACL and CannedACL parameters can be specified, not both.");
        }

        if (setObjectAclRequest.getAcl() != null) {
            setAcl(setObjectAclRequest.getBucketName(),
                   setObjectAclRequest.getKey(),
                   setObjectAclRequest.getVersionId(),
                   setObjectAclRequest.getAcl(),
                   setObjectAclRequest.isRequesterPays(),
                   setObjectAclRequest);

        } else if (setObjectAclRequest.getCannedAcl() != null) {
            setAcl(setObjectAclRequest.getBucketName(),
                   setObjectAclRequest.getKey(),
                   setObjectAclRequest.getVersionId(),
                   setObjectAclRequest.getCannedAcl(),
                   setObjectAclRequest.isRequesterPays(),
                   setObjectAclRequest);

        } else {
            throw new IllegalArgumentException(
                    "At least one of the ACL and CannedACL parameters should be specified");
        }
    }

    /**
     * {@inheritDoc}
     * @see #getBucketAcl(String)
     */
    @Override
    public AccessControlList getBucketAcl(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return getBucketAcl(new GetBucketAclRequest(bucketName));
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest)
        throws SdkClientException, AmazonServiceException {
        getBucketAclRequest = beforeClientExecution(getBucketAclRequest);
        String bucketName = getBucketAclRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's ACL");

        return getAcl(bucketName, null, null, false, getBucketAclRequest);
    }

    @Override
    public void setBucketAcl(String bucketName, AccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setBucketAcl(new SetBucketAclRequest(bucketName, acl));
    }

    /**
     * Same as {@link #setBucketAcl(String, AccessControlList)}
     * but allows specifying a request metric collector.
     */
    public void setBucketAcl(String bucketName, AccessControlList acl,
            RequestMetricCollector requestMetricCollector) {
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName, acl)
            .withRequestMetricCollector(requestMetricCollector);
        setBucketAcl(request);
    }

    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList cannedAcl)
            throws SdkClientException, AmazonServiceException {
        setBucketAcl(new SetBucketAclRequest(bucketName, cannedAcl));
    }

    /**
     * Same as {@link #setBucketAcl(String, CannedAccessControlList)}
     * but allows specifying a request metric collector.
     */
    public void setBucketAcl(String bucketName, CannedAccessControlList cannedAcl,
            RequestMetricCollector requestMetricCollector) throws SdkClientException,
            AmazonServiceException {
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName, cannedAcl)
            .withRequestMetricCollector(requestMetricCollector);
        setBucketAcl(request);
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest)
            throws SdkClientException, AmazonServiceException {
        setBucketAclRequest = beforeClientExecution(setBucketAclRequest);

        String bucketName = setBucketAclRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when setting a bucket's ACL");

        AccessControlList acl = setBucketAclRequest.getAcl();
        CannedAccessControlList cannedAcl = setBucketAclRequest.getCannedAcl();

        if (acl == null && cannedAcl == null) {
            throw new IllegalArgumentException(
                    "The ACL parameter must be specified when setting a bucket's ACL");
        }
        if (acl != null && cannedAcl != null) {
            throw new IllegalArgumentException(
                    "Only one of the acl and cannedAcl parameter can be specified, not both.");
        }

        if (acl != null) {
            setAcl(bucketName, null, null, acl, false, setBucketAclRequest);
        } else {
            setAcl(bucketName, null, null, cannedAcl, false, setBucketAclRequest);
        }
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObjectMetadata(new GetObjectMetadataRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest)
            throws SdkClientException, AmazonServiceException {
        getObjectMetadataRequest = beforeClientExecution(getObjectMetadataRequest);
        rejectNull(getObjectMetadataRequest, "The GetObjectMetadataRequest parameter must be specified when requesting an object's metadata");

        String bucketName = getObjectMetadataRequest.getBucketName();
        String key = getObjectMetadataRequest.getKey();
        String versionId = getObjectMetadataRequest.getVersionId();

        rejectNull(bucketName, "The bucket name parameter must be specified when requesting an object's metadata");
        rejectNull(key, "The key parameter must be specified when requesting an object's metadata");

        Request<GetObjectMetadataRequest> request = createRequest(bucketName, key, getObjectMetadataRequest, HttpMethodName.HEAD);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadObject");

        if (versionId != null) request.addParameter("versionId", versionId);

        populateRequesterPaysHeader(request, getObjectMetadataRequest.isRequesterPays());
        addPartNumberIfNotNull(request, getObjectMetadataRequest.getPartNumber());

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, getObjectMetadataRequest.getWormMirrorDestination());

        populateSSE_C(request, getObjectMetadataRequest.getSSECustomerKey());

        return invoke(request, new S3MetadataResponseHandler(), bucketName, key);
    }

    @Override
    public S3Object getObject(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObject(new GetObjectRequest(bucketName, key));
    }

    @Override
    public boolean doesBucketExist(String bucketName)
            throws SdkClientException, AmazonServiceException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            headBucket(new HeadBucketRequest(bucketName));
            return true;
        } catch (AmazonServiceException ase) {
            // A redirect error or a forbidden error means the bucket exists. So
            // returning true.
            if ((ase.getStatusCode() == Constants.BUCKET_REDIRECT_STATUS_CODE)
                || (ase.getStatusCode() == Constants.BUCKET_ACCESS_FORBIDDEN_STATUS_CODE)) {
                return true;
            }
            if (ase.getStatusCode() == Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                return false;
            }
            throw ase;
        }
    }

    @Override
    public boolean doesBucketExistV2(String bucketName) throws SdkClientException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            getBucketAcl(bucketName);
            return true;
        } catch (AmazonServiceException ase) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((ase.getStatusCode() == Constants.BUCKET_REDIRECT_STATUS_CODE) || "AccessDenied".equals(ase.getErrorCode())) {
                return true;
            }
            if (ase.getStatusCode() == Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                return false;
            }
            throw ase;
        }
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectName)
            throws AmazonServiceException, SdkClientException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            ValidationUtils.assertStringNotEmpty(objectName, "objectName");
            getObjectMetadata(bucketName, objectName);
            return true;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest headBucketRequest)
            throws SdkClientException, AmazonServiceException {
        headBucketRequest = beforeClientExecution(headBucketRequest);
        String bucketName = headBucketRequest.getBucketName();

        rejectNull(bucketName,
                "The bucketName parameter must be specified.");

        Request<HeadBucketRequest> request = createRequest(bucketName, null,
                headBucketRequest, HttpMethodName.HEAD);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadBucket");

        return invoke(request, new HeadBucketResultHandler(), bucketName, null);
    }

    @Override
    public void changeObjectStorageClass(String bucketName, String key, StorageClass newStorageClass)
        throws SdkClientException, AmazonServiceException {
        rejectNull(bucketName,
            "The bucketName parameter must be specified when changing an object's storage class");
        rejectNull(key,
            "The key parameter must be specified when changing an object's storage class");
        rejectNull(newStorageClass,
            "The newStorageClass parameter must be specified when changing an object's storage class");

        copyObject(new CopyObjectRequest(bucketName, key, bucketName, key)
            .withStorageClass(newStorageClass.toString()));
    }

    @Override
    public void setObjectRedirectLocation(String bucketName, String key, String newRedirectLocation)
        throws SdkClientException, AmazonServiceException {
        rejectNull(bucketName,
            "The bucketName parameter must be specified when changing an object's storage class");
        rejectNull(key,
            "The key parameter must be specified when changing an object's storage class");
        rejectNull(newRedirectLocation,
            "The newStorageClass parameter must be specified when changing an object's storage class");

        copyObject(new CopyObjectRequest(bucketName, key, bucketName, key)
            .withRedirectLocation(newRedirectLocation));
    }

    @Override
    public S3Object getObject(GetObjectRequest getObjectRequest)
        throws SdkClientException, AmazonServiceException {
        getObjectRequest = beforeClientExecution(getObjectRequest);
        assertNotNull(getObjectRequest, "GetObjectRequest");
        assertStringNotEmpty(getObjectRequest.getBucketName(), "BucketName");
        assertStringNotEmpty(getObjectRequest.getKey(), "Key");

        Request<GetObjectRequest> request = createRequest(getObjectRequest.getBucketName(), getObjectRequest.getKey(), getObjectRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObject");
        //IBM does not support object streaming
        //request.addHandlerContext(HandlerContextKey.HAS_STREAMING_OUTPUT, Boolean.TRUE);

        if (getObjectRequest.getVersionId() != null) {
            request.addParameter("versionId", getObjectRequest.getVersionId());
        }

        addPartNumberIfNotNull(request, getObjectRequest.getPartNumber());

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, getObjectRequest.getWormMirrorDestination());

        // Range
        long[] range = getObjectRequest.getRange();
        if (range != null) {
            request.addHeader(Headers.RANGE, "bytes=" + Long.toString(range[0]) + "-" + Long.toString(range[1]));
        }

        populateRequesterPaysHeader(request, getObjectRequest.isRequesterPays());

        addResponseHeaderParameters(request, getObjectRequest.getResponseHeaders());

        addDateHeader(request, Headers.GET_OBJECT_IF_MODIFIED_SINCE,
                      getObjectRequest.getModifiedSinceConstraint());
        addDateHeader(request, Headers.GET_OBJECT_IF_UNMODIFIED_SINCE,
                      getObjectRequest.getUnmodifiedSinceConstraint());
        addStringListHeader(request, Headers.GET_OBJECT_IF_MATCH,
                            getObjectRequest.getMatchingETagConstraints());
        addStringListHeader(request, Headers.GET_OBJECT_IF_NONE_MATCH,
                            getObjectRequest.getNonmatchingETagConstraints());

        // Populate the SSE-C parameters to the request header
        populateSSE_C(request, getObjectRequest.getSSECustomerKey());
        final ProgressListener listener = getObjectRequest.getGeneralProgressListener();
        publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);

        try {
            S3Object s3Object = invoke(request, new S3ObjectResponseHandler(),
                                       getObjectRequest.getBucketName(), getObjectRequest.getKey());
            /*
             * TODO: For now, it's easiest to set there here in the client, but
             *       we could push this back into the response handler with a
             *       little more work.
             */
            s3Object.setBucketName(getObjectRequest.getBucketName());
            s3Object.setKey(getObjectRequest.getKey());

            boolean skipClientSideValidation = skipMd5CheckStrategy.skipClientSideValidation(getObjectRequest,
                                                                                             s3Object.getObjectMetadata());
            postProcessS3Object(s3Object, skipClientSideValidation, listener);
            return s3Object;
        } catch (AmazonS3Exception ase) {
            /*
             * If the request failed because one of the specified constraints
             * was not met (ex: matching ETag, modified since date, etc.), then
             * return null, so that users don't have to wrap their code in
             * try/catch blocks and check for this status code if they want to
             * use constraints.
             */
            if (ase.getStatusCode() == 412 || ase.getStatusCode() == 304) {
                publishProgress(listener, ProgressEventType.TRANSFER_CANCELED_EVENT);
                return null;
            }
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw ase;
        }
    }

    /**
     * Post processing the {@link S3Object} downloaded from S3. It includes wrapping the data with wrapper input streams,
     * doing client side validation if possible etc.
     */
    private void postProcessS3Object(final S3Object s3Object, final boolean skipClientSideValidation,
                                         final ProgressListener listener) {
        InputStream is = s3Object.getObjectContent();
        HttpRequestBase httpRequest = s3Object.getObjectContent().getHttpRequest();
        // Hold a reference to this client while the InputStream is still
        // around - otherwise a finalizer in the HttpClient may reset the
        // underlying TCP connection out from under us.
        is = new ServiceClientHolderInputStream(is, this);
        // used trigger a tranfer complete event when the stream is entirely consumed
        ProgressInputStream progressInputStream =
            new ProgressInputStream(is, listener) {
                @Override protected void onEOF() {
                    publishProgress(getListener(), ProgressEventType.TRANSFER_COMPLETED_EVENT);
                }
            };
        is = progressInputStream;

        // The Etag header contains a server-side MD5 of the object. If
        // we're downloading the whole object, by default we wrap the
        // stream in a validator that calculates an MD5 of the downloaded
        // bytes and complains if what we received doesn't match the Etag.
        if (!skipClientSideValidation) {
            byte[] serverSideHash = BinaryUtils.fromHex(s3Object.getObjectMetadata().getETag());
            try {
                // No content length check is performed when the
                // MD5 check is enabled, since a correct MD5 check would
                // imply a correct content length.
                MessageDigest digest = MessageDigest.getInstance("MD5");
                is = new DigestValidationInputStream(is, digest, serverSideHash);
            } catch (NoSuchAlgorithmException e) {
                log.warn("No MD5 digest algorithm available.  Unable to calculate "
                         + "checksum and verify data integrity.", e);
            }
        } else {
            // Ensures the data received from S3 has the same length as the
            // expected content-length

            // Note: The GetObject response many not have a content-length if
            // it was uploaded using chunked encoding, e.g.
            // using WriteGetObjectResponse
            Object contentLength = s3Object.getObjectMetadata().getRawMetadataValue(Headers.CONTENT_LENGTH);
            if (contentLength != null) {
                is = new LengthCheckInputStream(is,
                        s3Object.getObjectMetadata().getContentLength(), // expected length
                        INCLUDE_SKIPPED_BYTES); // bytes received from S3 are all included even if skipped
            }
        }

        S3AbortableInputStream abortableInputStream =
            new S3AbortableInputStream(is, httpRequest, s3Object.getObjectMetadata().getContentLength());
        s3Object.setObjectContent(new S3ObjectInputStream(abortableInputStream, httpRequest, false));
    }

    @Override
    public ObjectMetadata getObject(final GetObjectRequest getObjectRequest, File destinationFile)
            throws SdkClientException, AmazonServiceException {
        rejectNull(destinationFile,
                "The destination file parameter must be specified when downloading an object directly to a file");

        S3Object s3Object = ServiceUtils.retryableDownloadS3ObjectToFile(destinationFile, new ServiceUtils.RetryableS3DownloadTask() {

            @Override
            public S3Object getS3ObjectStream() {
                return getObject(getObjectRequest);
            }

            @Override
            public boolean needIntegrityCheck() {
                return !skipMd5CheckStrategy.skipClientSideValidationPerRequest(getObjectRequest);
            }

        }, ServiceUtils.OVERWRITE_MODE);
        // getObject can return null if constraints were specified but not met
        if (s3Object == null) return null;

        return s3Object.getObjectMetadata();
    }

    @Override
    public String getObjectAsString(String bucketName, String key)
            throws AmazonServiceException, SdkClientException {
        rejectNull(bucketName, "Bucket name must be provided");
        rejectNull(key, "Object key must be provided");

        S3Object object = getObject(bucketName, key);
        try {
            return IOUtils.toString(object.getObjectContent());
        } catch (IOException e) {
            throw new SdkClientException("Error streaming content from S3 during download", e);
        } finally {
            IOUtils.closeQuietly(object, log);
        }
    }

    @Override
    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        getObjectTaggingRequest = beforeClientExecution(getObjectTaggingRequest);
        rejectNull(getObjectTaggingRequest,
                "The request parameter must be specified when getting the object tags");
        String bucketName = assertStringNotEmpty(getObjectTaggingRequest.getBucketName(), "BucketName");
        String key = assertNotNull(getObjectTaggingRequest.getKey(), "Key");

        Request<GetObjectTaggingRequest> request = createRequest(bucketName, key, getObjectTaggingRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectTagging");
        request.addParameter("tagging", null);
        addParameterIfNotNull(request, "versionId", getObjectTaggingRequest.getVersionId());
        //IBM unsupported
        //populateRequesterPaysHeader(request, getObjectTaggingRequest.isRequesterPays());

        ResponseHeaderHandlerChain<GetObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<GetObjectTaggingResult>(
                new Unmarshallers.GetObjectTaggingResponseUnmarshaller(),
                new GetObjectTaggingResponseHeaderHandler()
        );

        return invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) {
        setObjectTaggingRequest = beforeClientExecution(setObjectTaggingRequest);
        rejectNull(setObjectTaggingRequest,
                "The request parameter must be specified setting the object tags");
        String bucketName = assertStringNotEmpty(setObjectTaggingRequest.getBucketName(), "BucketName");
        String key = assertNotNull(setObjectTaggingRequest.getKey(), "Key");
        ObjectTagging tagging = assertNotNull(setObjectTaggingRequest.getTagging(), "ObjectTagging");

        Request<SetObjectTaggingRequest> request = createRequest(bucketName, key, setObjectTaggingRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectTagging");
        request.addParameter("tagging", null);
        addParameterIfNotNull(request, "versionId", setObjectTaggingRequest.getVersionId());
        byte[] content = new ObjectTaggingXmlFactory().convertToXmlByteArray(tagging);
        setContent(request, content, "application/xml", true);
        //IBM unsupported
        //populateRequesterPaysHeader(request, setObjectTaggingRequest.isRequesterPays());

        ResponseHeaderHandlerChain<SetObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<SetObjectTaggingResult>(
                new Unmarshallers.SetObjectTaggingResponseUnmarshaller(),
                new SetObjectTaggingResponseHeaderHandler()
        );

        return invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        deleteObjectTaggingRequest = beforeClientExecution(deleteObjectTaggingRequest);
        rejectNull(deleteObjectTaggingRequest, "The request parameter must be specified when delete the object tags");
        String bucketName = assertStringNotEmpty(deleteObjectTaggingRequest.getBucketName(), "BucketName");
        String key = assertStringNotEmpty(deleteObjectTaggingRequest.getKey(), "Key");

        Request<DeleteObjectTaggingRequest> request = createRequest(bucketName, key, deleteObjectTaggingRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObjectTagging");
        request.addParameter("tagging", null);
        addParameterIfNotNull(request, "versionId", deleteObjectTaggingRequest.getVersionId());

        ResponseHeaderHandlerChain<DeleteObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<DeleteObjectTaggingResult>(
                new Unmarshallers.DeleteObjectTaggingResponseUnmarshaller(),
                new DeleteObjectTaggingHeaderHandler()
        );

        return invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public void deleteBucket(String bucketName)
            throws SdkClientException, AmazonServiceException {
        deleteBucket(new DeleteBucketRequest(bucketName));
    }

    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest)
            throws SdkClientException, AmazonServiceException {
        deleteBucketRequest = beforeClientExecution(deleteBucketRequest);
        rejectNull(deleteBucketRequest,
                "The DeleteBucketRequest parameter must be specified when deleting a bucket");

        String bucketName = deleteBucketRequest.getBucketName();
        rejectNull(bucketName,
                "The bucket name parameter must be specified when deleting a bucket");

        Request<DeleteBucketRequest> request = createRequest(bucketName, null, deleteBucketRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucket");
        invoke(request, voidResponseHandler, bucketName, null);
        bucketRegionCache.remove(bucketName);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file)
            throws SdkClientException, AmazonServiceException {
        return putObject(new PutObjectRequest(bucketName, key, file)
            .withMetadata(new ObjectMetadata()));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws SdkClientException, AmazonServiceException {
        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException, AmazonServiceException {
        putObjectRequest = beforeClientExecution(putObjectRequest);
        rejectNull(putObjectRequest, "The PutObjectRequest parameter must be specified when uploading an object");
        final File file = putObjectRequest.getFile();
        final InputStream isOrig = putObjectRequest.getInputStream();
        InputStream inputStreamObjectLock = null;
        final String bucketName = putObjectRequest.getBucketName();
        final String key = putObjectRequest.getKey();
        final ProgressListener listener = putObjectRequest.getGeneralProgressListener();
        rejectNull(bucketName, "The bucket name parameter must be specified when uploading an object");
        rejectNull(key, "The key parameter must be specified when uploading an object");

        ObjectMetadata metadata = putObjectRequest.getMetadata();
        if (metadata == null)
            metadata = new ObjectMetadata();

        Request<PutObjectRequest> request = createRequest(bucketName, key, putObjectRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObject");
        request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, Boolean.TRUE);
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, Boolean.TRUE);

        // Make backward compatible with buffer size via system property
        final Integer bufsize = Constants.getS3StreamBufferSize();
        if (bufsize != null) {
            AmazonWebServiceRequest awsreq = request.getOriginalRequest();
            // Note awsreq is never null at this point even if the original
            // request was
            awsreq.getRequestClientOptions()
                  .setReadLimit(bufsize.intValue());
        }
        if ( putObjectRequest.getAccessControlList() != null) {
            addAclHeaders(request, putObjectRequest.getAccessControlList());
        } else if ( putObjectRequest.getCannedAcl() != null ) {
            request.addHeader(Headers.S3_CANNED_ACL, putObjectRequest.getCannedAcl().toString());
        }

        if (putObjectRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, putObjectRequest.getStorageClass());
        }

        if (putObjectRequest.getRedirectLocation() != null) {
            request.addHeader(Headers.REDIRECT_LOCATION, putObjectRequest.getRedirectLocation());
        }

        //IBM does not support SSE-KMS
        // Boolean bucketKeyEnabled = putObjectRequest.getBucketKeyEnabled();
        // if (bucketKeyEnabled != null) {
        //     addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_BUCKET_KEY_ENABLED,
        //             String.valueOf(bucketKeyEnabled));
        // }

        addHeaderIfNotNull(request, Headers.S3_TAGGING, urlEncodeTags(putObjectRequest.getTagging()));

        populateRequesterPaysHeader(request, putObjectRequest.isRequesterPays());

        // Populate the SSE-C parameters to the request header
        populateSSE_C(request, putObjectRequest.getSSECustomerKey());

        // Populate the SSE Amazon Web Services KMS parameters to the request header
        populateSSE_KMS(request,
                        putObjectRequest.getSSEAwsKeyManagementParams());

        populateObjectLockHeaders(request, putObjectRequest.getObjectLockMode(), putObjectRequest.getObjectLockRetainUntilDate(),
                                    putObjectRequest.getObjectLockLegalHoldStatus());

        // IBM-specific
        // Populate the object retention parameters to the request header
        if (putObjectRequest.getRetentionExpirationDate() != null) {
            request.addHeader(Headers.RETENTION_EXPIRATION_DATE,
                    DateUtils.formatRFC822Date(putObjectRequest.getRetentionExpirationDate()));
        }

        // IBM-specific
        if (putObjectRequest.getRetentionLegalHoldId() != null) {
            request.addHeader(Headers.RETENTION_LEGAL_HOLD_ID, putObjectRequest.getRetentionLegalHoldId());
        }

        // IBM-specific
        if (putObjectRequest.getRetentionPeriod() != null) {
            request.addHeader(Headers.RETENTION_PERIOD, putObjectRequest.getRetentionPeriod().toString());
        }

        // IBM-specific
        if (putObjectRequest.getObjectLockLegalHoldStatus() != null || putObjectRequest.getObjectLockMode() != null || putObjectRequest.getObjectLockRetainUntilDate() != null) { 
            try {
                if(isOrig != null && metadata.getContentMD5() == null)
                {
                    inputStreamObjectLock = putObjectRequest.getInputStream();
                    int size = inputStreamObjectLock.available();
                    byte[] bytes = new byte[size];
                    inputStreamObjectLock.read(bytes);
                    populateRequestHeaderWithMd5(request, bytes);
                    inputStreamObjectLock = new ByteArrayInputStream(bytes);
                }
            } catch ( Exception e ) {
                throw new SdkClientException("Couldn't compute md5 sum", e);
            }
        }

        // Object Lock Specific check
        if(inputStreamObjectLock != null)
        {
            return uploadObject(inputStreamObjectLock, file, metadata, listener, request, putObjectRequest,
                            skipMd5CheckStrategy.skipServerSideValidation(putObjectRequest),
                            skipMd5CheckStrategy.skipClientSideValidationPerRequest(putObjectRequest),
                            new PutObjectStrategy(bucketName, key),
                            true);
        }
        else
        {
            return uploadObject(isOrig, file, metadata, listener, request, putObjectRequest,
                            skipMd5CheckStrategy.skipServerSideValidation(putObjectRequest),
                            skipMd5CheckStrategy.skipClientSideValidationPerRequest(putObjectRequest),
                            new PutObjectStrategy(bucketName, key),
                            true);
        }
    }

    /**
     * Helper method used by {@link #putObject(PutObjectRequest)} and {@link #upload(PresignedUrlUploadRequest)}.
     */
    private <RequestT, ResponseT> ResponseT uploadObject(final InputStream originalStream,
                                                         final File file,
                                                         final ObjectMetadata metadata,
                                                         final ProgressListener listener,
                                                         final Request<RequestT> request,
                                                         final S3DataSource originalRequest,
                                                         final boolean skipServerSideValidation,
                                                         final boolean skipClientSideValidationPerRequest,
                                                         final UploadObjectStrategy<RequestT, ResponseT> uploadStrategy,
                                                         final boolean setContentTypeIfNotProvided) {

        InputStream input = getInputStream(originalStream, file, metadata, request,
                                           skipServerSideValidation, setContentTypeIfNotProvided);

        final ObjectMetadata returnedMetadata;
        MD5DigestCalculatingInputStream md5DigestStream = null;
        try {
            if (metadata.getContentMD5() == null && !skipClientSideValidationPerRequest) {
                /*
                 * If the user hasn't set the content MD5, then we don't want to buffer the whole
                 * stream in memory just to calculate it. Instead, we can calculate it on the fly
                 * and validate it with the returned ETag from the object upload.
                 */
                input = md5DigestStream = new MD5DigestCalculatingInputStream(input);
            }

            populateRequestMetadata(request, metadata);
            request.setContent(input);
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            try {
                returnedMetadata = uploadStrategy.invokeServiceCall(request);
            } catch (Throwable t) {
                publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
                throw failure(t);
            }
        } finally {
            cleanupDataSource(originalRequest, file, originalStream, input, log);
        }

        String contentMd5 = metadata.getContentMD5();
        if (md5DigestStream != null) {
            contentMd5 = Base64.encodeAsString(md5DigestStream.getMd5Digest());
        }

        final String etag = returnedMetadata.getETag();
        if (contentMd5 != null && !skipMd5CheckStrategy.skipClientSideValidationPerPutResponse(returnedMetadata)) {
            byte[] clientSideHash = BinaryUtils.fromBase64(contentMd5);
            byte[] serverSideHash = BinaryUtils.fromHex(etag);

            if (!Arrays.equals(clientSideHash, serverSideHash)) {
                publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
                throw new SdkClientException(
                    "Unable to verify integrity of data upload. Client calculated content hash (contentMD5: " + contentMd5
                    + " in base 64) didn't match hash (etag: " + etag + " in hex) calculated by Amazon S3.  "
                    + "You may need to delete the data stored in Amazon S3. (metadata.contentMD5: " + metadata.getContentMD5()
                    + ", md5DigestStream: " + md5DigestStream
                    + uploadStrategy.md5ValidationErrorSuffix()
                    + ")");
            }
        }

        publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);

        return uploadStrategy.createResult(returnedMetadata, contentMd5);
    }

    private InputStream getInputStream(final InputStream origStream, final File file, final ObjectMetadata metadata,
                                       final Request<?> request, final boolean skipServerSideValidation,
                                       final boolean setContentTypeIfNotProvided) {
        InputStream input = origStream;

        // If a file is specified for upload, we need to pull some additional
        // information from it to auto-configure a few options
        if (file == null) {
            // When input is a FileInputStream, this wrapping enables
            // unlimited mark-and-reset
            if (input != null)
                input = ReleasableInputStream.wrap(input);
        } else {
            // Always set the content length, even if it's already set
            metadata.setContentLength(file.length());
            final boolean calculateMD5 = metadata.getContentMD5() == null;
            // Only set the content type if it hasn't already been set
            if (metadata.getContentType() == null && setContentTypeIfNotProvided) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(file));
            }

            if (calculateMD5 && !skipServerSideValidation) {
                try {
                    String contentMd5_b64 = Md5Utils.md5AsBase64(file);
                    metadata.setContentMD5(contentMd5_b64);
                } catch (Exception e) {
                    throw new SdkClientException(
                        "Unable to calculate MD5 hash: " + e.getMessage(), e);
                }
            }
            input = newResettableInputStream(file, "Unable to find file to upload");
        }

        if (metadata.getContentType() == null && setContentTypeIfNotProvided) {
            /*
             * Default to the "application/octet-stream" if the user hasn't
             * specified a content type.
             */
            metadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
        }

        if (request.getHeaders().get(Headers.REDIRECT_LOCATION) != null && input == null) {
            input = new ByteArrayInputStream(new byte[0]);
        }

        // Use internal interface to differentiate 0 from unset.
        final Long contentLength = (Long)metadata.getRawMetadataValue(Headers.CONTENT_LENGTH);
        if (contentLength == null) {
                /*
                 * There's nothing we can do except for let the HTTP client buffer
                 * the input stream contents if the caller doesn't tell us how much
                 * data to expect in a stream since we have to explicitly tell
                 * Amazon S3 how much we're sending before we start sending any of
                 * it.
                 */
            log.warn("No content length specified for stream data.  " +
                     "Stream contents will be buffered in memory and could result in " +
                     "out of memory errors.");
        } else {
            final long expectedLength = contentLength.longValue();
            if (expectedLength >= 0) {
                // Performs length check on the underlying data stream.
                // For S3 encryption client, the underlying data stream here
                // refers to the cipher-text data stream (ie not the underlying
                // plain-text data stream which in turn may have been wrapped
                // with it's own length check input stream.)
                LengthCheckInputStream lcis = new LengthCheckInputStream(
                    input,
                    expectedLength, // expected data length to be uploaded
                    EXCLUDE_SKIPPED_BYTES);
                input = lcis;
            }
        }

        return input;
    }

    private static PutObjectResult createPutObjectResult(ObjectMetadata metadata) {
        final PutObjectResult result = new PutObjectResult();
        result.setVersionId(metadata.getVersionId());
        result.setSSEAlgorithm(metadata.getSSEAlgorithm());
        result.setSSECustomerAlgorithm(metadata.getSSECustomerAlgorithm());
        result.setSSECustomerKeyMd5(metadata.getSSECustomerKeyMd5());
        result.setExpirationTime(metadata.getExpirationTime());
        result.setExpirationTimeRuleId(metadata.getExpirationTimeRuleId());
        result.setETag(metadata.getETag());
        result.setMetadata(metadata);
        //IBM Unsupported
        //result.setRequesterCharged(metadata.isRequesterCharged());
        //IBM does not support SSE-KMS
        //result.setBucketKeyEnabled(metadata.getBucketKeyEnabled());
        return result;
    }

    /**
     * Sets the access control headers for the request given.
     */
    private static void addAclHeaders(Request<? extends AmazonWebServiceRequest> request, AccessControlList acl) {
        List<Grant> grants = acl.getGrantsAsList();
        Map<Permission, Collection<Grantee>> grantsByPermission = new HashMap<Permission, Collection<Grantee>>();
        for ( Grant grant : grants ) {
            if ( !grantsByPermission.containsKey(grant.getPermission()) ) {
                grantsByPermission.put(grant.getPermission(), new LinkedList<Grantee>());
            }
            grantsByPermission.get(grant.getPermission()).add(grant.getGrantee());
        }
        for ( Permission permission : Permission.values() ) {
            if ( grantsByPermission.containsKey(permission) ) {
                Collection<Grantee> grantees = grantsByPermission.get(permission);
                boolean seenOne = false;
                StringBuilder granteeString = new StringBuilder();
                for ( Grantee grantee : grantees ) {
                    if ( !seenOne )
                        seenOne = true;
                    else
                        granteeString.append(", ");
                    granteeString.append(grantee.getTypeIdentifier()).append("=").append("\"")
                            .append(grantee.getIdentifier()).append("\"");
                }
                request.addHeader(permission.getHeaderName(), granteeString.toString());
            }
        }
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey,
                                       String destinationBucketName, String destinationKey)
            throws SdkClientException, AmazonServiceException {
        return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey,
                                                destinationBucketName, destinationKey));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)
            throws SdkClientException, AmazonServiceException {
        copyObjectRequest = beforeClientExecution(copyObjectRequest);
        rejectNull(copyObjectRequest.getSourceBucketName(),
                "The source bucket name must be specified when copying an object");
        rejectNull(copyObjectRequest.getSourceKey(),
                "The source object key must be specified when copying an object");
        rejectNull(copyObjectRequest.getDestinationBucketName(),
                "The destination bucket name must be specified when copying an object");
        rejectNull(copyObjectRequest.getDestinationKey(),
                "The destination object key must be specified when copying an object");

        String destinationKey = copyObjectRequest.getDestinationKey();
        String destinationBucketName = copyObjectRequest.getDestinationBucketName();

        Request<CopyObjectRequest> request = createRequest(destinationBucketName, destinationKey, copyObjectRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CopyObject");

        //IBM does not support SSE-KMS
        // Boolean bucketKeyEnabled = copyObjectRequest.getBucketKeyEnabled();
        // if (bucketKeyEnabled != null) {
        //     addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_BUCKET_KEY_ENABLED,
        //             String.valueOf(bucketKeyEnabled));
        // }

        populateRequestWithCopyObjectParameters(request, copyObjectRequest);

        // Populate the SSE Amazon Web Services KMS parameters to the request header
        populateSSE_KMS(request,
                copyObjectRequest.getSSEAwsKeyManagementParams());

        populateObjectLockHeaders(request, copyObjectRequest.getObjectLockMode(), copyObjectRequest.getObjectLockRetainUntilDate(),
                copyObjectRequest.getObjectLockLegalHoldStatus());

        /*
         * We can't send a non-zero length Content-Length header if the user
         * specified it, otherwise it messes up the HTTP connection when the
         * remote server thinks there's more data to pull.
         */
        setZeroContentLength(request);
        CopyObjectResultHandler copyObjectResultHandler = null;
        try {
            @SuppressWarnings("unchecked")
            ResponseHeaderHandlerChain<CopyObjectResultHandler> handler = new ResponseHeaderHandlerChain<CopyObjectResultHandler>(
                    // xml payload unmarshaller
                    new Unmarshallers.CopyObjectUnmarshaller(),
                    // header handlers
                    new ServerSideEncryptionHeaderHandler<CopyObjectResultHandler>(),
                    new S3VersionHeaderHandler<CopyObjectResultHandler>(),
                    new ObjectExpirationHeaderHandler<CopyObjectResultHandler>());
            copyObjectResultHandler = invoke(request, handler, destinationBucketName, destinationKey);
        } catch (AmazonS3Exception ase) {
            /*
             * If the request failed because one of the specified constraints
             * was not met (ex: matching ETag, modified since date, etc.), then
             * return null, so that users don't have to wrap their code in
             * try/catch blocks and check for this status code if they want to
             * use constraints.
             */
            if (ase.getStatusCode() == Constants.FAILED_PRECONDITION_STATUS_CODE) {
               return null;
            }

            throw ase;
        }

        /*
         * CopyObject has two failure modes:
         *  1 - An HTTP error code is returned and the error is processed like any
         *      other error response.
         *  2 - An HTTP 200 OK code is returned, but the response content contains
         *      an XML error response.
         *
         * This makes it very difficult for the client runtime to cleanly detect
         * this case and handle it like any other error response.  We could
         * extend the runtime to have a more flexible/customizable definition of
         * success/error (per request), but it's probably overkill for this
         * one special case.
         */
        if (copyObjectResultHandler.getErrorCode() != null) {
            String errorCode = copyObjectResultHandler.getErrorCode();
            String errorMessage = copyObjectResultHandler.getErrorMessage();
            String requestId = copyObjectResultHandler.getErrorRequestId();
            String hostId = copyObjectResultHandler.getErrorHostId();

            AmazonS3Exception ase = new AmazonS3Exception(errorMessage);
            ase.setErrorCode(errorCode);
            ase.setErrorType(ErrorType.Service);
            ase.setRequestId(requestId);
            ase.setExtendedRequestId(hostId);
            ase.setServiceName(request.getServiceName());
            ase.setStatusCode(200);
            ase.setProxyHost(clientConfiguration.getProxyHost());

            throw ase;
        }

        // TODO: Might be nice to create this in our custom S3VersionHeaderHandler
        CopyObjectResult copyObjectResult = new CopyObjectResult();
        copyObjectResult.setETag(copyObjectResultHandler.getETag());
        copyObjectResult.setLastModifiedDate(copyObjectResultHandler.getLastModified());
        copyObjectResult.setVersionId(copyObjectResultHandler.getVersionId());
        copyObjectResult.setSSEAlgorithm(copyObjectResultHandler.getSSEAlgorithm());
        copyObjectResult.setSSECustomerAlgorithm(copyObjectResultHandler.getSSECustomerAlgorithm());
        copyObjectResult.setSSECustomerKeyMd5(copyObjectResultHandler.getSSECustomerKeyMd5());
        //IBM does not support SSE-KMS
        //copyObjectResult.setBucketKeyEnabled(copyObjectResultHandler.getBucketKeyEnabled());
        copyObjectResult.setExpirationTime(copyObjectResultHandler.getExpirationTime());
        copyObjectResult.setExpirationTimeRuleId(copyObjectResultHandler.getExpirationTimeRuleId());

        return copyObjectResult;
    }

    /**
     * Copies a source object to a part of a multipart upload.
     *
     * To copy an object, the caller's account must have read access to the source object and
     * write access to the destination bucket.
     * </p>
     * <p>For information about maximum and minimum part sizes and other multipart upload specifications,
     * see <a href=\"https://docs.aws.amazon.com/AmazonS3/latest/userguide/qfacts.html\">Multipart upload limits</a>
     * in the <i>Amazon S3 User Guide</i>. </p>
     * <p>
     * If constraints are specified in the <code>CopyPartRequest</code>
     * (e.g.
     * {@link CopyPartRequest#setMatchingETagConstraints(List)})
     * and are not satisfied when Amazon S3 receives the
     * request, this method returns <code>null</code>.
     * This method returns a non-null result under all other
     * circumstances.
     * </p>
     *
     * @param copyPartRequest
     *            The request object containing all the options for copying an
     *            Amazon S3 object.
     *
     * @return A {@link CopyPartResult} object containing the information
     *         returned by Amazon S3 about the newly created object, or <code>null</code> if
     *         constraints were specified that weren't met when Amazon S3 attempted
     *         to copy the object.
     *
     * @throws SdkClientException
     *             If any errors are encountered in the client while making the
     *             request or handling the response.
     * @throws AmazonServiceException
     *             If any errors occurred in Amazon S3 while processing the
     *             request.
     *
     * @see AmazonS3#copyObject(CopyObjectRequest)
     * @see AmazonS3#initiateMultipartUpload(InitiateMultipartUploadRequest)
     */
    @Override
    public CopyPartResult copyPart(CopyPartRequest copyPartRequest) {
        copyPartRequest = beforeClientExecution(copyPartRequest);
        rejectNull(copyPartRequest.getSourceBucketName(),
                "The source bucket name must be specified when copying a part");
        rejectNull(copyPartRequest.getSourceKey(),
                "The source object key must be specified when copying a part");
        rejectNull(copyPartRequest.getDestinationBucketName(),
                "The destination bucket name must be specified when copying a part");
        rejectNull(copyPartRequest.getUploadId(),
                "The upload id must be specified when copying a part");
        rejectNull(copyPartRequest.getDestinationKey(),
                "The destination object key must be specified when copying a part");
        rejectNull(copyPartRequest.getPartNumber(),
                "The part number must be specified when copying a part");

        String destinationKey = copyPartRequest.getDestinationKey();
        String destinationBucketName = copyPartRequest.getDestinationBucketName();

        Request<CopyPartRequest> request = createRequest(destinationBucketName, destinationKey, copyPartRequest,
                HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UploadPartCopy");

        populateRequestWithCopyPartParameters(request, copyPartRequest);

        request.addParameter("uploadId", copyPartRequest.getUploadId());
        request.addParameter("partNumber", Integer.toString(copyPartRequest.getPartNumber()));

        populateRequesterPaysHeader(request, copyPartRequest.isRequesterPays());

        /*
         * We can't send a non-zero length Content-Length header if the user
         * specified it, otherwise it messes up the HTTP connection when the
         * remote server thinks there's more data to pull.
         */
        setZeroContentLength(request);
        CopyObjectResultHandler copyObjectResultHandler = null;
        try {
            @SuppressWarnings("unchecked")
            ResponseHeaderHandlerChain<CopyObjectResultHandler> handler = new ResponseHeaderHandlerChain<CopyObjectResultHandler>(
                    // xml payload unmarshaller
                    new Unmarshallers.CopyObjectUnmarshaller(),
                    // header handlers
                    new ServerSideEncryptionHeaderHandler<CopyObjectResultHandler>(),
                    new S3VersionHeaderHandler<CopyObjectResultHandler>());
            copyObjectResultHandler = invoke(request, handler, destinationBucketName, destinationKey);
        } catch ( AmazonS3Exception ase ) {
            /*
             * If the request failed because one of the specified constraints
             * was not met (ex: matching ETag, modified since date, etc.), then
             * return null, so that users don't have to wrap their code in
             * try/catch blocks and check for this status code if they want to
             * use constraints.
             */
            if ( ase.getStatusCode() == Constants.FAILED_PRECONDITION_STATUS_CODE ) {
                return null;
            }

            throw ase;
        }

        /*
         * CopyPart has two failure modes: 1 - An HTTP error code is returned
         * and the error is processed like any other error response. 2 - An HTTP
         * 200 OK code is returned, but the response content contains an XML
         * error response.
         *
         * This makes it very difficult for the client runtime to cleanly detect
         * this case and handle it like any other error response. We could
         * extend the runtime to have a more flexible/customizable definition of
         * success/error (per request), but it's probably overkill for this one
         * special case.
         */
        if ( copyObjectResultHandler.getErrorCode() != null ) {
            String errorCode = copyObjectResultHandler.getErrorCode();
            String errorMessage = copyObjectResultHandler.getErrorMessage();
            String requestId = copyObjectResultHandler.getErrorRequestId();
            String hostId = copyObjectResultHandler.getErrorHostId();

            AmazonS3Exception ase = new AmazonS3Exception(errorMessage);
            ase.setErrorCode(errorCode);
            ase.setErrorType(ErrorType.Service);
            ase.setRequestId(requestId);
            ase.setExtendedRequestId(hostId);
            ase.setServiceName(request.getServiceName());
            ase.setStatusCode(200);
            ase.setProxyHost(clientConfiguration.getProxyHost());

            throw ase;
        }

        CopyPartResult copyPartResult = new CopyPartResult();
        copyPartResult.setETag(copyObjectResultHandler.getETag());
        copyPartResult.setPartNumber(copyPartRequest.getPartNumber());
        copyPartResult.setLastModifiedDate(copyObjectResultHandler.getLastModified());
        copyPartResult.setVersionId(copyObjectResultHandler.getVersionId());
        copyPartResult.setSSEAlgorithm(copyObjectResultHandler.getSSEAlgorithm());
        copyPartResult.setSSECustomerAlgorithm(copyObjectResultHandler.getSSECustomerAlgorithm());
        copyPartResult.setSSECustomerKeyMd5(copyObjectResultHandler.getSSECustomerKeyMd5());
        //IBM does not support SSE-KMS
        //copyPartResult.setBucketKeyEnabled(copyObjectResultHandler.getBucketKeyEnabled());

        return copyPartResult;
    }

    @Override
    public void deleteObject(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest)
            throws SdkClientException, AmazonServiceException {
        deleteObjectRequest = beforeClientExecution(deleteObjectRequest);
        rejectNull(deleteObjectRequest,
            "The delete object request must be specified when deleting an object");

        rejectNull(deleteObjectRequest.getBucketName(), "The bucket name must be specified when deleting an object");
        rejectNull(deleteObjectRequest.getKey(), "The key must be specified when deleting an object");

        Request<DeleteObjectRequest> request = createRequest(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(), deleteObjectRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObject");

        invoke(request, voidResponseHandler, deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey());
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        deleteObjectsRequest = beforeClientExecution(deleteObjectsRequest);
        Request<DeleteObjectsRequest> request = createRequest(deleteObjectsRequest.getBucketName(), null, deleteObjectsRequest, HttpMethodName.POST);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObjects");
        request.addParameter("delete", null);
        //IBM unsupported
        // if (deleteObjectsRequest.getBypassGovernanceRetention()) {
        //     request.addHeader(Headers.BYPASS_GOVERNANCE_RETENTION, "true");
        // }

        if ( deleteObjectsRequest.getMfa() != null ) {
            populateRequestWithMfaDetails(request, deleteObjectsRequest.getMfa());
        }

        populateRequesterPaysHeader(request, deleteObjectsRequest.isRequesterPays());

        byte[] content = new MultiObjectDeleteXmlFactory().convertToXmlByteArray(deleteObjectsRequest);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        populateRequestHeaderWithMd5(request, content);

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<DeleteObjectsResponse> responseHandler = new ResponseHeaderHandlerChain<DeleteObjectsResponse>(
                new Unmarshallers.DeleteObjectsResultUnmarshaller(),
                new S3RequesterChargedHeaderHandler<DeleteObjectsResponse>());




        DeleteObjectsResponse response = invoke(request, responseHandler, deleteObjectsRequest.getBucketName(), null);

        /*
         * If the result was only partially successful, throw an exception
         */
        if ( !response.getErrors().isEmpty() ) {
            Map<String, String> headers = responseHandler.getResponseHeaders();

            MultiObjectDeleteException ex = new MultiObjectDeleteException(
                    response.getErrors(),
                    response.getDeletedObjects());

            ex.setStatusCode(200);
            ex.setRequestId(headers.get(Headers.REQUEST_ID));
            ex.setExtendedRequestId(headers.get(Headers.EXTENDED_REQUEST_ID));
            ex.setCloudFrontId(headers.get(Headers.CLOUD_FRONT_ID));
            ex.setProxyHost(clientConfiguration.getProxyHost());

            throw ex;
        }
        DeleteObjectsResult result = new DeleteObjectsResult(response.getDeletedObjects(), response.isRequesterCharged());

        return result;
    }

    @Override
    public void deleteVersion(String bucketName, String key, String versionId)
            throws SdkClientException, AmazonServiceException {
        deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest)
            throws SdkClientException, AmazonServiceException {
        deleteVersionRequest = beforeClientExecution(deleteVersionRequest);
        rejectNull(deleteVersionRequest,
            "The delete version request object must be specified when deleting a version");

        String bucketName = deleteVersionRequest.getBucketName();
        String key = deleteVersionRequest.getKey();
        String versionId = deleteVersionRequest.getVersionId();

        rejectNull(bucketName, "The bucket name must be specified when deleting a version");
        rejectNull(key, "The key must be specified when deleting a version");
        rejectNull(versionId, "The version ID must be specified when deleting a version");

        Request<DeleteVersionRequest> request = createRequest(bucketName, key, deleteVersionRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObject");
        if (versionId != null) request.addParameter("versionId", versionId);

        if (deleteVersionRequest.getMfa() != null) {
            populateRequestWithMfaDetails(request, deleteVersionRequest.getMfa());
        }
        //IBM unsupported
        // if (deleteVersionRequest.getBypassGovernanceRetention()) {
        //     request.addHeader(Headers.BYPASS_GOVERNANCE_RETENTION, "true");
        // }

        invoke(request, voidResponseHandler, bucketName, key);
    }

    @Override
    public void setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest)
        throws SdkClientException, AmazonServiceException {
        setBucketVersioningConfigurationRequest = beforeClientExecution(setBucketVersioningConfigurationRequest);
        rejectNull(setBucketVersioningConfigurationRequest,
            "The SetBucketVersioningConfigurationRequest object must be specified when setting versioning configuration");

        String bucketName = setBucketVersioningConfigurationRequest.getBucketName();
        BucketVersioningConfiguration versioningConfiguration = setBucketVersioningConfigurationRequest.getVersioningConfiguration();

        rejectNull(bucketName,
            "The bucket name parameter must be specified when setting versioning configuration");
        rejectNull(versioningConfiguration,
            "The bucket versioning parameter must be specified when setting versioning configuration");
        if (versioningConfiguration.isMfaDeleteEnabled() != null) {
            rejectNull(setBucketVersioningConfigurationRequest.getMfa(),
                "The MFA parameter must be specified when changing MFA Delete status in the versioning configuration");
        }

        Request<SetBucketVersioningConfigurationRequest> request = createRequest(bucketName, null, setBucketVersioningConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketVersioning");
        request.addParameter("versioning", null);

        if (versioningConfiguration.isMfaDeleteEnabled() != null) {
            if (setBucketVersioningConfigurationRequest.getMfa() != null) {
                populateRequestWithMfaDetails(request, setBucketVersioningConfigurationRequest.getMfa());
            }
        }

        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(versioningConfiguration);
        request.setContent(new ByteArrayInputStream(bytes));
        populateRequestHeaderWithMd5(request, bytes);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(bucketName));
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(GetBucketVersioningConfigurationRequest getBucketVersioningConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        getBucketVersioningConfigurationRequest = beforeClientExecution(getBucketVersioningConfigurationRequest);
        rejectNull(getBucketVersioningConfigurationRequest, "The request object parameter getBucketVersioningConfigurationRequest must be specified.");
        String bucketName = getBucketVersioningConfigurationRequest.getBucketName();
        rejectNull(bucketName,
                "The bucket name parameter must be specified when querying versioning configuration");

        Request<GetBucketVersioningConfigurationRequest> request = createRequest(bucketName, null, getBucketVersioningConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketVersioning");
        request.addParameter("versioning", null);

        return invoke(request, new Unmarshallers.BucketVersioningConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return getBucketWebsiteConfiguration(new GetBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(GetBucketWebsiteConfigurationRequest getBucketWebsiteConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        getBucketWebsiteConfigurationRequest = beforeClientExecution(getBucketWebsiteConfigurationRequest);
        rejectNull(getBucketWebsiteConfigurationRequest, "The request object parameter getBucketWebsiteConfigurationRequest must be specified.");
        String bucketName = getBucketWebsiteConfigurationRequest.getBucketName();
        rejectNull(bucketName,
            "The bucket name parameter must be specified when requesting a bucket's website configuration");

        Request<GetBucketWebsiteConfigurationRequest> request = createRequest(bucketName, null, getBucketWebsiteConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");

        try {
            return invoke(request, new Unmarshallers.BucketWebsiteConfigurationUnmarshaller(), bucketName, null);
        } catch (AmazonServiceException ase) {
            if (ase.getStatusCode() == 404) return null;
            throw ase;
        }
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) {
        return getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        getBucketLifecycleConfigurationRequest = beforeClientExecution(getBucketLifecycleConfigurationRequest);
        rejectNull(getBucketLifecycleConfigurationRequest, "The request object pamameter getBucketLifecycleConfigurationRequest must be specified.");
        String bucketName = getBucketLifecycleConfigurationRequest.getBucketName();
        rejectNull(bucketName, "The bucket name must be specifed when retrieving the bucket lifecycle configuration.");

        Request<GetBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, getBucketLifecycleConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLifecycleConfiguration");
        request.addParameter("lifecycle", null);

        try {
            return invoke(request, new Unmarshallers.BucketLifecycleConfigurationUnmarshaller(), bucketName, null);
        } catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
            case 404:
                return null;
            default:
                throw ase;
            }
        }
    }

    @Override
    public void setBucketLifecycleConfiguration(String bucketName, BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        setBucketLifecycleConfiguration(new SetBucketLifecycleConfigurationRequest(bucketName, bucketLifecycleConfiguration));
    }

    @Override
    public void setBucketLifecycleConfiguration(
            SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest) {
        setBucketLifecycleConfigurationRequest = beforeClientExecution(setBucketLifecycleConfigurationRequest);
        rejectNull(setBucketLifecycleConfigurationRequest,
                "The set bucket lifecycle configuration request object must be specified.");

        String bucketName = setBucketLifecycleConfigurationRequest.getBucketName();
        BucketLifecycleConfiguration bucketLifecycleConfiguration = setBucketLifecycleConfigurationRequest.getLifecycleConfiguration();

        rejectNull(bucketName,
                "The bucket name parameter must be specified when setting bucket lifecycle configuration.");
        rejectNull(bucketLifecycleConfiguration,
                "The lifecycle configuration parameter must be specified when setting bucket lifecycle configuration.");

        Request<SetBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, setBucketLifecycleConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketLifecycleConfiguration");
        request.addParameter("lifecycle", null);

        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketLifecycleConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        populateRequestHeaderWithMd5(request, content);
        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(String bucketName) {
        deleteBucketLifecycleConfiguration(new DeleteBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketLifecycleConfiguration(
            DeleteBucketLifecycleConfigurationRequest deleteBucketLifecycleConfigurationRequest) {
        deleteBucketLifecycleConfigurationRequest = beforeClientExecution(deleteBucketLifecycleConfigurationRequest);
        rejectNull(deleteBucketLifecycleConfigurationRequest,
                "The delete bucket lifecycle configuration request object must be specified.");

        String bucketName = deleteBucketLifecycleConfigurationRequest.getBucketName();
        rejectNull(bucketName,
                "The bucket name parameter must be specified when deleting bucket lifecycle configuration.");

        Request<DeleteBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, deleteBucketLifecycleConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketLifecycle");
        request.addParameter("lifecycle", null);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(String bucketName) {
        return getBucketCrossOriginConfiguration(new GetBucketCrossOriginConfigurationRequest(bucketName));
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(GetBucketCrossOriginConfigurationRequest getBucketCrossOriginConfigurationRequest) {
        getBucketCrossOriginConfigurationRequest = beforeClientExecution(getBucketCrossOriginConfigurationRequest);
        rejectNull(getBucketCrossOriginConfigurationRequest, "The request object parameter getBucketCrossOriginConfigurationRequest must be specified.");
        String bucketName = getBucketCrossOriginConfigurationRequest.getBucketName();
        rejectNull(bucketName, "The bucket name must be specified when retrieving the bucket cross origin configuration.");

        Request<GetBucketCrossOriginConfigurationRequest> request = createRequest(bucketName, null, getBucketCrossOriginConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketCors");
        request.addParameter("cors", null);

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, getBucketCrossOriginConfigurationRequest.getWormMirrorDestination());

        try {
            return invoke(request, new Unmarshallers.BucketCrossOriginConfigurationUnmarshaller(), bucketName, null);
        } catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
            case 404:
                return null;
            default:
                throw ase;
            }
        }
    }

    @Override
    public void setBucketCrossOriginConfiguration(String bucketName, BucketCrossOriginConfiguration bucketCrossOriginConfiguration) {
        setBucketCrossOriginConfiguration(new SetBucketCrossOriginConfigurationRequest(bucketName, bucketCrossOriginConfiguration));
    }

    @Override
    public void setBucketCrossOriginConfiguration(
            SetBucketCrossOriginConfigurationRequest setBucketCrossOriginConfigurationRequest) {
        setBucketCrossOriginConfigurationRequest = beforeClientExecution(setBucketCrossOriginConfigurationRequest);
        rejectNull(setBucketCrossOriginConfigurationRequest,
                "The set bucket cross origin configuration request object must be specified.");

        String bucketName = setBucketCrossOriginConfigurationRequest.getBucketName();
        BucketCrossOriginConfiguration bucketCrossOriginConfiguration = setBucketCrossOriginConfigurationRequest.getCrossOriginConfiguration();

        rejectNull(bucketName,
                "The bucket name parameter must be specified when setting bucket cross origin configuration.");
        rejectNull(bucketCrossOriginConfiguration,
                "The cross origin configuration parameter must be specified when setting bucket cross origin configuration.");

        Request<SetBucketCrossOriginConfigurationRequest> request = createRequest(bucketName, null, setBucketCrossOriginConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketCors");
        request.addParameter("cors", null);

        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketCrossOriginConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        populateRequestHeaderWithMd5(request, content);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(String bucketName) {
        deleteBucketCrossOriginConfiguration(new DeleteBucketCrossOriginConfigurationRequest(bucketName));
      }

    @Override
    public void deleteBucketCrossOriginConfiguration(
            DeleteBucketCrossOriginConfigurationRequest deleteBucketCrossOriginConfigurationRequest) {
        deleteBucketCrossOriginConfigurationRequest = beforeClientExecution(deleteBucketCrossOriginConfigurationRequest);
        rejectNull(deleteBucketCrossOriginConfigurationRequest,
                "The delete bucket cross origin configuration request object must be specified.");

        String bucketName = deleteBucketCrossOriginConfigurationRequest.getBucketName();
        rejectNull(bucketName,
                "The bucket name parameter must be specified when deleting bucket cross origin configuration.");

        Request<DeleteBucketCrossOriginConfigurationRequest> request = createRequest(bucketName, null, deleteBucketCrossOriginConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketCors");
        request.addParameter("cors", null);
        invoke(request, voidResponseHandler, bucketName, null);
    }

    /**
     * @exclude
     */
    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(String bucketName) {
        return getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(bucketName));
    }

    /**
     * @exclude
     */
    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(GetBucketTaggingConfigurationRequest getBucketTaggingConfigurationRequest) {
        getBucketTaggingConfigurationRequest = beforeClientExecution(getBucketTaggingConfigurationRequest);
        rejectNull(getBucketTaggingConfigurationRequest, "The request object parameter getBucketTaggingConfigurationRequest must be specifed.");
        String bucketName = getBucketTaggingConfigurationRequest.getBucketName();
        rejectNull(bucketName, "The bucket name must be specified when retrieving the bucket tagging configuration.");

        Request<GetBucketTaggingConfigurationRequest> request = createRequest(bucketName, null, getBucketTaggingConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketTagging");
        request.addParameter("tagging", null);

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, getBucketTaggingConfigurationRequest.getWormMirrorDestination());

        try {
            return invoke(request, new Unmarshallers.BucketTaggingConfigurationUnmarshaller(), bucketName, null);
        } catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
            case 404:
                return null;
            default:
                throw ase;
            }
        }
    }

    /**
     * @exclude
     */
    @Override
    public void setBucketTaggingConfiguration(String bucketName, BucketTaggingConfiguration bucketTaggingConfiguration) {
        setBucketTaggingConfiguration(new SetBucketTaggingConfigurationRequest(bucketName, bucketTaggingConfiguration));
    }

    /**
     * @exclude
     */
    @Override
    public void setBucketTaggingConfiguration(
            SetBucketTaggingConfigurationRequest setBucketTaggingConfigurationRequest) {
        setBucketTaggingConfigurationRequest = beforeClientExecution(setBucketTaggingConfigurationRequest);
        rejectNull(setBucketTaggingConfigurationRequest,
                "The set bucket tagging configuration request object must be specified.");

        String bucketName = setBucketTaggingConfigurationRequest.getBucketName();
        BucketTaggingConfiguration bucketTaggingConfiguration = setBucketTaggingConfigurationRequest.getTaggingConfiguration();

        rejectNull(bucketName,
                "The bucket name parameter must be specified when setting bucket tagging configuration.");
        rejectNull(bucketTaggingConfiguration,
                "The tagging configuration parameter must be specified when setting bucket tagging configuration.");

        Request<SetBucketTaggingConfigurationRequest> request = createRequest(bucketName, null, setBucketTaggingConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketTagging");
        request.addParameter("tagging", null);

        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketTaggingConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        populateRequestHeaderWithMd5(request, content);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    /**
     * @exclude
     */
    @Override
    public void deleteBucketTaggingConfiguration(String bucketName) {
        deleteBucketTaggingConfiguration(new DeleteBucketTaggingConfigurationRequest(bucketName));
    }

    /**
     * @exclude
     */
    @Override
    public void deleteBucketTaggingConfiguration(
            DeleteBucketTaggingConfigurationRequest deleteBucketTaggingConfigurationRequest) {
        deleteBucketTaggingConfigurationRequest = beforeClientExecution(deleteBucketTaggingConfigurationRequest);
        rejectNull(deleteBucketTaggingConfigurationRequest,
                "The delete bucket tagging configuration request object must be specified.");

        String bucketName = deleteBucketTaggingConfigurationRequest.getBucketName();
        rejectNull(bucketName,
                "The bucket name parameter must be specified when deleting bucket tagging configuration.");

        Request<DeleteBucketTaggingConfigurationRequest> request = createRequest(bucketName, null, deleteBucketTaggingConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketTagging");
        request.addParameter("tagging", null);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public void setBucketWebsiteConfiguration(String bucketName, BucketWebsiteConfiguration configuration)
            throws SdkClientException, AmazonServiceException {
        setBucketWebsiteConfiguration(new SetBucketWebsiteConfigurationRequest(bucketName, configuration));
    }

    @Override
    public void setBucketWebsiteConfiguration(SetBucketWebsiteConfigurationRequest setBucketWebsiteConfigurationRequest)
           throws SdkClientException, AmazonServiceException {
        setBucketWebsiteConfigurationRequest = beforeClientExecution(setBucketWebsiteConfigurationRequest);
        String bucketName = setBucketWebsiteConfigurationRequest.getBucketName();
        BucketWebsiteConfiguration configuration = setBucketWebsiteConfigurationRequest.getConfiguration();

        rejectNull(bucketName,
                "The bucket name parameter must be specified when setting a bucket's website configuration");
        rejectNull(configuration,
                "The bucket website configuration parameter must be specified when setting a bucket's website configuration");
        if (configuration.getRedirectAllRequestsTo() == null) {
        rejectNull(configuration.getIndexDocumentSuffix(),
                "The bucket website configuration parameter must specify the index document suffix when setting a bucket's website configuration");
        }

        Request<SetBucketWebsiteConfigurationRequest> request = createRequest(bucketName, null, setBucketWebsiteConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");

        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(configuration);
        request.setContent(new ByteArrayInputStream(bytes));
        populateRequestHeaderWithMd5(request, bytes);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(String bucketName)
            throws SdkClientException, AmazonServiceException {
        deleteBucketWebsiteConfiguration(new DeleteBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketWebsiteConfiguration(DeleteBucketWebsiteConfigurationRequest deleteBucketWebsiteConfigurationRequest)
        throws SdkClientException, AmazonServiceException {
        deleteBucketWebsiteConfigurationRequest = beforeClientExecution(deleteBucketWebsiteConfigurationRequest);
        String bucketName = deleteBucketWebsiteConfigurationRequest.getBucketName();

        rejectNull(bucketName,
            "The bucket name parameter must be specified when deleting a bucket's website configuration");

        Request<DeleteBucketWebsiteConfigurationRequest> request = createRequest(bucketName, null, deleteBucketWebsiteConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");

        invoke(request, voidResponseHandler, bucketName, null);
    }
    //IBM unsupported
    // @Override
    // public void setBucketNotificationConfiguration(String bucketName, BucketNotificationConfiguration bucketNotificationConfiguration)
    //     throws SdkClientException, AmazonServiceException {
    //     setBucketNotificationConfiguration(new SetBucketNotificationConfigurationRequest(bucketName, bucketNotificationConfiguration));
    // }

    // @Override
    // public void setBucketNotificationConfiguration(
    //         SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     setBucketNotificationConfigurationRequest = beforeClientExecution(setBucketNotificationConfigurationRequest);
    //     rejectNull(setBucketNotificationConfigurationRequest,
    //             "The set bucket notification configuration request object must be specified.");

    //     String bucketName = setBucketNotificationConfigurationRequest.getBucketName();
    //     BucketNotificationConfiguration bucketNotificationConfiguration = setBucketNotificationConfigurationRequest.getNotificationConfiguration();
    //     Boolean skipDestinationValidation = setBucketNotificationConfigurationRequest.getSkipDestinationValidation();

    //     rejectNull(bucketName,
    //             "The bucket name parameter must be specified when setting bucket notification configuration.");
    //     rejectNull(bucketNotificationConfiguration,
    //             "The notification configuration parameter must be specified when setting bucket notification configuration.");

    //     Request<SetBucketNotificationConfigurationRequest> request = createRequest(bucketName, null, setBucketNotificationConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketNotificationConfiguration");
    //     request.addParameter("notification", null);
    //     if (skipDestinationValidation != null) {
    //         request.addHeader("x-amz-skip-destination-validation", Boolean.toString(skipDestinationValidation));
    //     }

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(bucketNotificationConfiguration);
    //     request.setContent(new ByteArrayInputStream(bytes));
    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    // @Override
    // public BucketNotificationConfiguration getBucketNotificationConfiguration(String bucketName)
    //         throws SdkClientException, AmazonServiceException {
    //     return getBucketNotificationConfiguration(new GetBucketNotificationConfigurationRequest(bucketName));
    // }

    // @Override
    // public BucketNotificationConfiguration getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     getBucketNotificationConfigurationRequest = beforeClientExecution(getBucketNotificationConfigurationRequest);
    //     rejectNull(getBucketNotificationConfigurationRequest,
    //             "The bucket request parameter must be specified when querying notification configuration");
    //     String bucketName = getBucketNotificationConfigurationRequest.getBucketName();
    //     rejectNull(bucketName,
    //             "The bucket request must specify a bucket name when querying notification configuration");

    //     Request<GetBucketNotificationConfigurationRequest> request = createRequest(bucketName, null, getBucketNotificationConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketNotificationConfiguration");
    //     request.addParameter("notification", null);

    //     return invoke(request, BucketNotificationConfigurationStaxUnmarshaller.getInstance(), bucketName, null);
    // }

    // @Override
    // public BucketLoggingConfiguration getBucketLoggingConfiguration(String bucketName)
    //         throws SdkClientException, AmazonServiceException {
    //     return getBucketLoggingConfiguration(new GetBucketLoggingConfigurationRequest(bucketName));
    // }

    // @Override
    // public BucketLoggingConfiguration getBucketLoggingConfiguration(GetBucketLoggingConfigurationRequest getBucketLoggingConfigurationRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     getBucketLoggingConfigurationRequest = beforeClientExecution(getBucketLoggingConfigurationRequest);
    //     rejectNull(getBucketLoggingConfigurationRequest, "The request object parameter getBucketLoggingConfigurationRequest must be specifed.");
    //     String bucketName = getBucketLoggingConfigurationRequest.getBucketName();
    //     rejectNull(bucketName,
    //             "The bucket name parameter must be specified when requesting a bucket's logging status");

    //     Request<GetBucketLoggingConfigurationRequest> request = createRequest(bucketName, null, getBucketLoggingConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLogging");
    //     request.addParameter("logging", null);

    //     return invoke(request, new Unmarshallers.BucketLoggingConfigurationnmarshaller(), bucketName, null);
    // }

    // @Override
    // public void setBucketLoggingConfiguration(SetBucketLoggingConfigurationRequest setBucketLoggingConfigurationRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     setBucketLoggingConfigurationRequest = beforeClientExecution(setBucketLoggingConfigurationRequest);
    //     rejectNull(setBucketLoggingConfigurationRequest,
    //         "The set bucket logging configuration request object must be specified when enabling server access logging");

    //     String bucketName = setBucketLoggingConfigurationRequest.getBucketName();
    //     BucketLoggingConfiguration loggingConfiguration = setBucketLoggingConfigurationRequest.getLoggingConfiguration();

    //     rejectNull(bucketName,
    //         "The bucket name parameter must be specified when enabling server access logging");
    //     rejectNull(loggingConfiguration,
    //         "The logging configuration parameter must be specified when enabling server access logging");

    //     Request<SetBucketLoggingConfigurationRequest> request = createRequest(bucketName, null, setBucketLoggingConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketLogging");
    //     request.addParameter("logging", null);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(loggingConfiguration);
    //     request.setContent(new ByteArrayInputStream(bytes));
    //     populateRequestHeaderWithMd5(request, bytes);

    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    // @Override
    // public BucketAccelerateConfiguration getBucketAccelerateConfiguration(
    //         String bucketName) throws AmazonServiceException,
    //         SdkClientException {
    //     return getBucketAccelerateConfiguration(new GetBucketAccelerateConfigurationRequest(
    //             bucketName));
    // }

    // @Override
    // public BucketAccelerateConfiguration getBucketAccelerateConfiguration(
    //         GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     getBucketAccelerateConfigurationRequest = beforeClientExecution(getBucketAccelerateConfigurationRequest);
    //     rejectNull(getBucketAccelerateConfigurationRequest, "getBucketAccelerateConfigurationRequest must be specified.");
    //     String bucketName = getBucketAccelerateConfigurationRequest.getBucketName();
    //     rejectNull(bucketName,
    //             "The bucket name parameter must be specified when querying accelerate configuration");

    //     Request<GetBucketAccelerateConfigurationRequest> request = createRequest(bucketName, null, getBucketAccelerateConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAccelerateConfiguration");
    //     request.addParameter("accelerate", null);

    //     return invoke(request, new Unmarshallers.BucketAccelerateConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public void setBucketAccelerateConfiguration(String bucketName,
    //         BucketAccelerateConfiguration accelerateConfiguration)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(
    //             bucketName, accelerateConfiguration));
    // }

    // @Override
    // public void setBucketAccelerateConfiguration(
    //         SetBucketAccelerateConfigurationRequest setBucketAccelerateConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketAccelerateConfigurationRequest = beforeClientExecution(setBucketAccelerateConfigurationRequest);

    //     rejectNull(setBucketAccelerateConfigurationRequest,
    //             "setBucketAccelerateConfigurationRequest must be specified");

    //     String bucketName = setBucketAccelerateConfigurationRequest.getBucketName();
    //     BucketAccelerateConfiguration accelerateConfiguration = setBucketAccelerateConfigurationRequest.getAccelerateConfiguration();

    //     rejectNull(bucketName,
    //         "The bucket name parameter must be specified when setting accelerate configuration.");
    //     rejectNull(accelerateConfiguration,
    //         "The bucket accelerate configuration parameter must be specified.");
    //     rejectNull(accelerateConfiguration.getStatus(),
    //         "The status parameter must be specified when updating bucket accelerate configuration.");

    //     Request<SetBucketAccelerateConfigurationRequest> request = createRequest(
    //             bucketName, null, setBucketAccelerateConfigurationRequest,
    //             HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAccelerateConfiguration");
    //     request.addParameter("accelerate", null);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(accelerateConfiguration);
    //     request.setContent(new ByteArrayInputStream(bytes));

    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    // @Override
    // public BucketPolicy getBucketPolicy(String bucketName)
    //         throws SdkClientException, AmazonServiceException {
    //     return getBucketPolicy(new GetBucketPolicyRequest(bucketName));
    // }

    // @Override
    // public void deleteBucketPolicy(String bucketName)
    //         throws SdkClientException, AmazonServiceException {
    //     deleteBucketPolicy(new DeleteBucketPolicyRequest(bucketName));
    // }

    // @Override
    // public BucketPolicy getBucketPolicy(
    //         GetBucketPolicyRequest getBucketPolicyRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     getBucketPolicyRequest = beforeClientExecution(getBucketPolicyRequest);
    //     rejectNull(getBucketPolicyRequest,
    //         "The request object must be specified when getting a bucket policy");

    //     String bucketName = getBucketPolicyRequest.getBucketName();
    //     rejectNull(bucketName,
    //         "The bucket name must be specified when getting a bucket policy");

    //     Request<GetBucketPolicyRequest> request = createRequest(bucketName, null, getBucketPolicyRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketPolicy");
    //     request.addParameter("policy", null);

    //     BucketPolicy result = new BucketPolicy();
    //     try {
    //         String policyText = invoke(request, new S3StringResponseHandler(), bucketName, null);
    //         result.setPolicyText(policyText);
    //         return result;
    //     } catch (AmazonServiceException ase) {
    //         /*
    //          * If we receive an error response telling us that no policy has
    //          * been set for this bucket, then instead of forcing the user to
    //          * deal with the exception, we'll just return an empty result. Any
    //          * other exceptions will be rethrown for the user to handle.
    //          */
    //         if (ase.getErrorCode().equals("NoSuchBucketPolicy")) return result;
    //         throw ase;
    //     }
    // }


    // @Override
    // public void setBucketPolicy(String bucketName, String policyText)
    //         throws SdkClientException, AmazonServiceException {
    //     setBucketPolicy(new SetBucketPolicyRequest(bucketName, policyText));
    // }

    // @Override
    // public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     setBucketPolicyRequest = beforeClientExecution(setBucketPolicyRequest);
    //     rejectNull(setBucketPolicyRequest,
    //         "The request object must be specified when setting a bucket policy");

    //     String bucketName = setBucketPolicyRequest.getBucketName();
    //     String policyText = setBucketPolicyRequest.getPolicyText();

    //     rejectNull(bucketName,
    //         "The bucket name must be specified when setting a bucket policy");
    //     rejectNull(policyText,
    //         "The policy text must be specified when setting a bucket policy");

    //     Request<SetBucketPolicyRequest> request = createRequest(bucketName, null, setBucketPolicyRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketPolicy");
    //     request.addParameter("policy", null);
    //     byte[] content = ServiceUtils.toByteArray(policyText);
    //     request.setContent(new ByteArrayInputStream(ServiceUtils.toByteArray(policyText)));
    //     populateRequestHeaderWithMd5(request, content);

    //     if (setBucketPolicyRequest.getConfirmRemoveSelfBucketAccess() != null &&
    //         setBucketPolicyRequest.getConfirmRemoveSelfBucketAccess()) {
    //         request.addHeader(Headers.REMOVE_SELF_BUCKET_ACCESS, "true");
    //     }

    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    // @Override
    // public void deleteBucketPolicy(
    //         DeleteBucketPolicyRequest deleteBucketPolicyRequest)
    //         throws SdkClientException, AmazonServiceException {
    //     deleteBucketPolicyRequest = beforeClientExecution(deleteBucketPolicyRequest);
    //     rejectNull(deleteBucketPolicyRequest,
    //         "The request object must be specified when deleting a bucket policy");

    //     String bucketName = deleteBucketPolicyRequest.getBucketName();
    //     rejectNull(bucketName,
    //         "The bucket name must be specified when deleting a bucket policy");

    //     Request<DeleteBucketPolicyRequest> request = createRequest(bucketName, null, deleteBucketPolicyRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketPolicy");
    //     request.addParameter("policy", null);

    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    // @Override
    // public DeleteBucketEncryptionResult deleteBucketEncryption(String bucketName) throws SdkClientException {
    //     return deleteBucketEncryption(new DeleteBucketEncryptionRequest().withBucketName(bucketName));
    // }

    // @Override
    // public DeleteBucketEncryptionResult deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest)
    //     throws SdkClientException {
    //     deleteBucketEncryptionRequest = beforeClientExecution(deleteBucketEncryptionRequest);
    //     rejectNull(deleteBucketEncryptionRequest,
    //                "The request object must be specified when deleting a bucket encryption configuration");

    //     String bucketName = deleteBucketEncryptionRequest.getBucketName();
    //     rejectNull(bucketName,
    //                "The bucket name must be specified when deleting a bucket encryption configuration");

    //     Request<DeleteBucketEncryptionRequest> request =
    //         createRequest(bucketName, null, deleteBucketEncryptionRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketEncryption");
    //     request.addParameter("encryption", null);

    //     return invoke(request, new Unmarshallers.DeleteBucketEncryptionUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketEncryptionResult getBucketEncryption(String bucketName) throws SdkClientException {
    //     return getBucketEncryption(new GetBucketEncryptionRequest().withBucketName(bucketName));
    // }

    // @Override
    // public GetBucketEncryptionResult getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws SdkClientException {
    //     getBucketEncryptionRequest = beforeClientExecution(getBucketEncryptionRequest);
    //     rejectNull(getBucketEncryptionRequest,
    //                "The bucket request parameter must be specified when querying encryption configuration");
    //     String bucketName = getBucketEncryptionRequest.getBucketName();
    //     rejectNull(bucketName,
    //                "The bucket request must specify a bucket name when querying encryption configuration");

    //     Request<GetBucketEncryptionRequest> request = createRequest(bucketName, null, getBucketEncryptionRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketEncryption");
    //     request.addParameter("encryption", null);

    //     return invoke(request, GetBucketEncryptionStaxUnmarshaller.getInstance(), bucketName, null);
    // }

    // @Override
    // public SetBucketEncryptionResult setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest)
    //     throws AmazonServiceException, SdkClientException {
    //     setBucketEncryptionRequest = beforeClientExecution(setBucketEncryptionRequest);
    //     rejectNull(setBucketEncryptionRequest,
    //                "The request object must be specified.");

    //     String bucketName = setBucketEncryptionRequest.getBucketName();
    //     ServerSideEncryptionConfiguration sseConfig = setBucketEncryptionRequest.getServerSideEncryptionConfiguration();
    //     rejectNull(bucketName,
    //                "The bucket name parameter must be specified when setting bucket encryption configuration.");
    //     rejectNull(sseConfig,
    //                "The SSE configuration parameter must be specified when setting bucket encryption configuration.");


    //     Request<SetBucketEncryptionRequest> request = createRequest(bucketName, null, setBucketEncryptionRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketEncryption");
    //     request.addParameter("encryption", null);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(sseConfig);
    //     request.setContent(new ByteArrayInputStream(bytes));
    //    populateRequestHeaderWithMd5(request, bytes);

    //     return invoke(request, new Unmarshallers.SetBucketEncryptionUnmarshaller(), bucketName, null);
    // }

    @Override
    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest setPublicAccessBlockRequest) {
        setPublicAccessBlockRequest = beforeClientExecution(setPublicAccessBlockRequest);
        rejectNull(setPublicAccessBlockRequest, "The request object must be specified.");

        String bucketName = setPublicAccessBlockRequest.getBucketName();
        PublicAccessBlockConfiguration config = setPublicAccessBlockRequest.getPublicAccessBlockConfiguration();
        rejectNull(bucketName,
                   "The bucket name parameter must be specified when setting public block configuration.");
        rejectNull(config,
                   "The PublicAccessBlockConfiguration parameter must be specified when setting public block");


        Request<SetPublicAccessBlockRequest> request = createRequest(bucketName, null, setPublicAccessBlockRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutPublicAccessBlock");
        request.addParameter("publicAccessBlock", null);

        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(config);
        request.setContent(new ByteArrayInputStream(bytes));
        populateRequestHeaderWithMd5(request, bytes);

        return invoke(request, new Unmarshallers.SetPublicAccessBlockUnmarshaller(), bucketName, null);
    }

    @Override
    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        getPublicAccessBlockRequest = beforeClientExecution(getPublicAccessBlockRequest);
        rejectNull(getPublicAccessBlockRequest, "The request object must be specified.");

        String bucketName = getPublicAccessBlockRequest.getBucketName();
        rejectNull(bucketName,
                   "The bucket name parameter must be specified when getting public block configuration.");


        Request<GetPublicAccessBlockRequest> request = createRequest(bucketName, null, getPublicAccessBlockRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetPublicAccessBlock");
        request.addParameter("publicAccessBlock", null);


        return invoke(request, GetPublicAccessBlockStaxUnmarshaller.getInstance(), bucketName, null);
    }

    @Override
    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        deletePublicAccessBlockRequest = beforeClientExecution(deletePublicAccessBlockRequest);
        rejectNull(deletePublicAccessBlockRequest, "The request object must be specified.");

        String bucketName = deletePublicAccessBlockRequest.getBucketName();
        rejectNull(bucketName,
                   "The bucket name parameter must be specified when deleting public block configuration.");


        Request<DeletePublicAccessBlockRequest> request = createRequest(bucketName, null, deletePublicAccessBlockRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeletePublicAccessBlock");
        request.addParameter("publicAccessBlock", null);


        return invoke(request, new Unmarshallers.DeletePublicAccessBlockUnmarshaller(), bucketName, null);
    }
    //IBM unsupported
    // @Override
    // public GetBucketPolicyStatusResult getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
    //     getBucketPolicyStatusRequest = beforeClientExecution(getBucketPolicyStatusRequest);
    //     rejectNull(getBucketPolicyStatusRequest, "The request object must be specified.");

    //     String bucketName = getBucketPolicyStatusRequest.getBucketName();
    //     rejectNull(bucketName,
    //                "The bucket name parameter must be specified when getting bucket policy status");


    //     Request<GetBucketPolicyStatusRequest> request = createRequest(bucketName, null, getBucketPolicyStatusRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketPolicyStatus");
    //     request.addParameter("policyStatus", null);


    //     return invoke(request, GetBucketPolicyStatusStaxUnmarshaller.getInstance(), bucketName, null);
    // }

    // @Override
    // public SelectObjectContentResult selectObjectContent(SelectObjectContentRequest selectRequest) throws AmazonServiceException, SdkClientException {
    //     selectRequest = beforeClientExecution(selectRequest);
    //     rejectNull(selectRequest, "The request parameter must be specified");

    //     rejectNull(selectRequest.getBucketName(), "The bucket name parameter must be specified when selecting object content.");
    //     rejectNull(selectRequest.getKey(), "The key parameter must be specified when selecting object content.");

    //     Request<SelectObjectContentRequest> request = createRequest(selectRequest.getBucketName(), selectRequest.getKey(), selectRequest, HttpMethodName.POST);
    //     request.addParameter("select", null);
    //     request.addParameter("select-type", "2");

    //     populateSSE_C(request, selectRequest.getSSECustomerKey());

    //     setContent(request, RequestXmlFactory.convertToXmlByteArray(selectRequest), ContentType.APPLICATION_XML.toString(), true);

    //     S3Object result = invoke(request, new S3ObjectResponseHandler(), selectRequest.getBucketName(), selectRequest.getKey());

    //     // Hold a reference to this client while the InputStream is still
    //     // around - otherwise a finalizer in the HttpClient may reset the
    //     // underlying TCP connection out from under us.
    //     SdkFilterInputStream resultStream = new ServiceClientHolderInputStream(result.getObjectContent(), this);

    //     return new SelectObjectContentResult().withPayload(new SelectObjectContentEventStream(resultStream));
    // }

    @Override
    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest setObjectLegalHoldRequest) {
        setObjectLegalHoldRequest = beforeClientExecution(setObjectLegalHoldRequest);
        rejectNull(setObjectLegalHoldRequest, "The request parameter must be specified");

        String bucketName = setObjectLegalHoldRequest.getBucketName();
        String key = setObjectLegalHoldRequest.getKey();

        rejectNull(bucketName, "The bucket name parameter must be specified when setting the object legal hold.");
        rejectNull(key, "The key parameter must be specified when setting the object legal hold.");

        Request<SetObjectLegalHoldRequest> request = createRequest(bucketName, key, setObjectLegalHoldRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectLegalHold");
        setContent(request, new ObjectLockLegalHoldXmlFactory().convertToXmlByteArray(setObjectLegalHoldRequest.getLegalHold()),
                ContentType.APPLICATION_XML.toString(), true);
        request.addParameter("legal-hold", null);

        addParameterIfNotNull(request, "versionId", setObjectLegalHoldRequest.getVersionId());
        populateRequesterPaysHeader(request, setObjectLegalHoldRequest.isRequesterPays());

        ResponseHeaderHandlerChain<SetObjectLegalHoldResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectLegalHoldResult>(
                new Unmarshallers.SetObjectLegalHoldResultUnmarshaller(),
                new S3RequesterChargedHeaderHandler<SetObjectLegalHoldResult>());

        return invoke(request, responseHandler, bucketName, key);
    }

    @Override
    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        getObjectLegalHoldRequest = beforeClientExecution(getObjectLegalHoldRequest);
        rejectNull(getObjectLegalHoldRequest, "The request parameter must be specified");

        String bucketName = getObjectLegalHoldRequest.getBucketName();
        String key = getObjectLegalHoldRequest.getKey();
        rejectNull(bucketName, "The bucket name parameter must be specified when getting the object legal hold.");
        rejectNull(key, "The key parameter must be specified when getting the object legal hold.");

        Request<GetObjectLegalHoldRequest> request = createRequest(bucketName, key, getObjectLegalHoldRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectLegalHold");
        request.addParameter("legal-hold", null);
        addParameterIfNotNull(request, "versionId", getObjectLegalHoldRequest.getVersionId());
        populateRequesterPaysHeader(request, getObjectLegalHoldRequest.isRequesterPays());

        return invoke(request, new Unmarshallers.GetObjectLegalHoldResultUnmarshaller(), bucketName, key);
    }

    @Override
    public SetObjectLockConfigurationResult setObjectLockConfiguration(SetObjectLockConfigurationRequest setObjectLockConfigurationRequest) {
        setObjectLockConfigurationRequest = beforeClientExecution(setObjectLockConfigurationRequest);
        rejectNull(setObjectLockConfigurationRequest, "The request parameter must be specified");

        String bucketName = setObjectLockConfigurationRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when setting the object lock configuration");

        Request<SetObjectLockConfigurationRequest> request = createRequest(bucketName, null, setObjectLockConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectLockConfiguration");
        request.addParameter("object-lock", null);

        addHeaderIfNotNull(request, Headers.OBJECT_LOCK_TOKEN, setObjectLockConfigurationRequest.getToken());
        populateRequesterPaysHeader(request, setObjectLockConfigurationRequest.isRequesterPays());

        setContent(request, new ObjectLockConfigurationXmlFactory().convertToXmlByteArray(setObjectLockConfigurationRequest.getObjectLockConfiguration()),
                ContentType.APPLICATION_XML.toString(), true);

        ResponseHeaderHandlerChain<SetObjectLockConfigurationResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectLockConfigurationResult>(
                new Unmarshallers.SetObjectLockConfigurationResultUnmarshaller(),
                new S3RequesterChargedHeaderHandler<SetObjectLockConfigurationResult>());

        return invoke(request, responseHandler, bucketName, null);
    }

    @Override
    public GetObjectLockConfigurationResult getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        getObjectLockConfigurationRequest = beforeClientExecution(getObjectLockConfigurationRequest);
        rejectNull(getObjectLockConfigurationRequest, "The request parameter must be specified");

        String bucketName = getObjectLockConfigurationRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when getting the object lock configuration");

        Request<GetObjectLockConfigurationRequest> request = createRequest(bucketName, null, getObjectLockConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectLockConfiguration");
        request.addParameter("object-lock", null);

        return invoke(request, new Unmarshallers.GetObjectLockConfigurationResultUnmarshaller(), bucketName, null);
    }

    @Override
    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest setObjectRetentionRequest) {
        setObjectRetentionRequest = beforeClientExecution(setObjectRetentionRequest);
        rejectNull(setObjectRetentionRequest, "The request parameter must be specified");

        String bucketName = setObjectRetentionRequest.getBucketName();
        String key = setObjectRetentionRequest.getKey();

        rejectNull(bucketName, "The bucket name parameter must be specified when setting the object retention.");
        rejectNull(key, "The key parameter must be specified when setting the object retention.");

        Request<SetObjectRetentionRequest> request = createRequest(bucketName, key, setObjectRetentionRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectRetention");
        request.addParameter("retention", null);
        if (setObjectRetentionRequest.getBypassGovernanceRetention()) {
            request.addHeader(Headers.BYPASS_GOVERNANCE_RETENTION, "true");
        }
        addParameterIfNotNull(request, "versionId", setObjectRetentionRequest.getVersionId());
        populateRequesterPaysHeader(request, setObjectRetentionRequest.isRequesterPays());

        setContent(request, new ObjectLockRetentionXmlFactory().convertToXmlByteArray(setObjectRetentionRequest.getRetention()),
                ContentType.APPLICATION_XML.toString(), true);

        ResponseHeaderHandlerChain<SetObjectRetentionResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectRetentionResult>(
                new Unmarshallers.SetObjectRetentionResultUnmarshaller(),
                new S3RequesterChargedHeaderHandler<SetObjectRetentionResult>());

        return invoke(request, responseHandler, bucketName, key);
    }

    @Override
    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        getObjectRetentionRequest = beforeClientExecution(getObjectRetentionRequest);
        rejectNull(getObjectRetentionRequest, "The request parameter must be specified");

        String bucketName = getObjectRetentionRequest.getBucketName();
        String key = getObjectRetentionRequest.getKey();

        rejectNull(bucketName, "The bucket name parameter must be specified when getting the object retention.");
        rejectNull(key, "The key parameter must be specified when getting the object retention.");

        Request<GetObjectRetentionRequest> request = createRequest(bucketName, key, getObjectRetentionRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectRetention");
        request.addParameter("retention", null);

        addParameterIfNotNull(request, "versionId", getObjectRetentionRequest.getVersionId());
        // Note: the model has a requester pays member on the request but no requester charged on the result...
        populateRequesterPaysHeader(request, getObjectRetentionRequest.isRequesterPays());

        return invoke(request, new Unmarshallers.GetObjectRetentionResultUnmarshaller(), bucketName, key);
    }

    //IBM unsupported
    // @Override
    // public WriteGetObjectResponseResult writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest) {
    //     writeGetObjectResponseRequest = beforeClientExecution(writeGetObjectResponseRequest);

    //     rejectNull(writeGetObjectResponseRequest, "The request parameter must be specified");

    //     Request<WriteGetObjectResponseRequest> request = createRequest(null, null, writeGetObjectResponseRequest, HttpMethodName.POST);
    //     request.setResourcePath("/WriteGetObjectResponse");
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "WriteGetObjectResponse");
    //     request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, false);
    //     request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, true);

    //     // Never sign the body. This operation is v4-unsigned-body
    //     request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED, true);
    //     request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED, false);

    //     addHeaderIfNotNull(request, Headers.REQUEST_ROUTE, writeGetObjectResponseRequest.getRequestRoute());
    //     addHeaderIfNotNull(request, Headers.REQUEST_TOKEN, writeGetObjectResponseRequest.getRequestToken());
    //     addIntegerHeaderIfNotNull(request, Headers.FWD_STATUS_CODE, writeGetObjectResponseRequest.getStatusCode());
    //     addHeaderIfNotNull(request, Headers.FWD_ERROR_CODE, writeGetObjectResponseRequest.getErrorCode());
    //     addHeaderIfNotNull(request, Headers.FWD_ERROR_MESSAGE, writeGetObjectResponseRequest.getErrorMessage());
    //     addHeaderIfNotNull(request, Headers.FWD_ACCEPT_RANGES, writeGetObjectResponseRequest.getAcceptRanges());
    //     addHeaderIfNotNull(request, Headers.FWD_CACHE_CONTROL, writeGetObjectResponseRequest.getCacheControl());
    //     addHeaderIfNotNull(request, Headers.FWD_CONTENT_DISPOSITION, writeGetObjectResponseRequest.getContentDisposition());
    //     addHeaderIfNotNull(request, Headers.FWD_CONTENT_ENCODING, writeGetObjectResponseRequest.getContentEncoding());
    //     addHeaderIfNotNull(request, Headers.FWD_CONTENT_LANGUAGE, writeGetObjectResponseRequest.getContentLanguage());
    //     addHeaderIfNotNull(request, Headers.FWD_CONTENT_RANGE, writeGetObjectResponseRequest.getContentRange());
    //     addHeaderIfNotNull(request, Headers.FWD_CONTENT_TYPE, writeGetObjectResponseRequest.getContentType());
    //     addHeaderIfNotNull(request, Headers.FWD_DELETE_MARKER, writeGetObjectResponseRequest.getDeleteMarker());
    //     addHeaderIfNotNull(request, Headers.FWD_ETAG, writeGetObjectResponseRequest.getETag());
    //     addDateHeader(request, Headers.FWD_EXPIRES, writeGetObjectResponseRequest.getExpires());
    //     addHeaderIfNotNull(request, Headers.FWD_EXPIRATION, writeGetObjectResponseRequest.getExpiration());
    //     addDateHeader(request, Headers.FWD_LAST_MODIFIED, writeGetObjectResponseRequest.getLastModified());
    //     addIntegerHeaderIfNotNull(request, Headers.FWD_MISSING_META, writeGetObjectResponseRequest.getMissingMeta());
        // addHeaderIfNotNull(request, Headers.FWD_OBJECT_LOCK_MODE, writeGetObjectResponseRequest.getObjectLockMode());
        // addHeaderIfNotNull(request, Headers.FWD_OBJECT_LOCK_LEGAL_HOLD, writeGetObjectResponseRequest.getObjectLockLegalHoldStatus());
        // addDateHeader(request, Headers.FWD_OBJECT_LOCK_RETAIN_UNTIL_DATE, writeGetObjectResponseRequest.getObjectLockRetainUntilDate());
    //     addIntegerHeaderIfNotNull(request, Headers.FWD_PARTS_COUNT, writeGetObjectResponseRequest.getPartsCount());
    //     addHeaderIfNotNull(request, Headers.FWD_REPLICATION_STATUS, writeGetObjectResponseRequest.getReplicationStatus());
    //     addHeaderIfNotNull(request, Headers.FWD_REQUEST_CHARGED, writeGetObjectResponseRequest.getRequestCharged());
    //     addHeaderIfNotNull(request, Headers.FWD_RESTORE, writeGetObjectResponseRequest.getRestore());
    //     addHeaderIfNotNull(request, Headers.FWD_SERVER_SIDE_ENCRYPTION, writeGetObjectResponseRequest.getServerSideEncryption());
    //     addHeaderIfNotNull(request, Headers.FWD_SSE_CUSTOMER_ALGORITHM, writeGetObjectResponseRequest.getSSECustomerAlgorithm());
    //     addHeaderIfNotNull(request, Headers.FWD_SSE_KMS_KEY_ID, writeGetObjectResponseRequest.getSSEKMSKeyId());
    //     addHeaderIfNotNull(request, Headers.FWD_SSE_CUSTOMER_KEY_MD5, writeGetObjectResponseRequest.getSSECustomerKeyMD5());
    //     addHeaderIfNotNull(request, Headers.FWD_STORAGE_CLASS, writeGetObjectResponseRequest.getStorageClass());
    //     addIntegerHeaderIfNotNull(request, Headers.FWD_TAG_COUNT, writeGetObjectResponseRequest.getTagCount());
    //     addHeaderIfNotNull(request, Headers.FWD_VERSION_ID, writeGetObjectResponseRequest.getVersionId());
        // if (writeGetObjectResponseRequest.getBucketKeyEnabled() != null) {
        //     request.addHeader(Headers.FWD_SSE_BUCKET_KEY_ENABLED, writeGetObjectResponseRequest.getBucketKeyEnabled().toString());
        // }

    //     ObjectMetadata metadata = writeGetObjectResponseRequest.getMetadata();
    //     if (metadata == null) {
    //         metadata = new ObjectMetadata();
    //     }

    //     if (writeGetObjectResponseRequest.getContentLength() != null) {
    //         metadata.setContentLength(writeGetObjectResponseRequest.getContentLength());
    //     }

    //     InputStream originalIs = writeGetObjectResponseRequest.getInputStream();
    //     File originalFile = writeGetObjectResponseRequest.getFile();
    //     InputStream requestInputStream = null;
    //     try {
    //         requestInputStream = getInputStream(
    //                 writeGetObjectResponseRequest.getInputStream(),
    //                 writeGetObjectResponseRequest.getFile(),
    //                 metadata,
    //                 request,
    //                 false,
    //                 true
    //         );

    //         request.setContent(requestInputStream);

    //         populateRequestMetadata(request, metadata);

    //         return invoke(request, new Unmarshallers.WriteGetObjectResponseResultUnmarshaller(), null, null);
    //     } finally {
    //         cleanupDataSource(writeGetObjectResponseRequest, originalFile, originalIs, requestInputStream, log);
    //     }
    // }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration)
            throws SdkClientException {
        return generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
            throws SdkClientException {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, method);
        request.setExpiration(expiration);

        return generatePresignedUrl(request);
    }

    @Override
    public URL generatePresignedUrl(GeneratePresignedUrlRequest req) {
        rejectNull(req,
            "The request parameter must be specified when generating a pre-signed URL");
        req.rejectIllegalArguments();

        // Check if credentialProvider is instance of IBMOAuthCredentials
        if (this.awsCredentialsProvider.getCredentials() instanceof IBMOAuthCredentials) {
            IBMOAuthCredentials creds = (IBMOAuthCredentials) this.awsCredentialsProvider.getCredentials();
            if (creds.getApiKey() != null || creds.getTokenManager() != null) {
                throw new AmazonS3Exception("generatePresignedUrl() is not supported with IAM credentials");
            }
        }

        final String bucketName = req.getBucketName();
        final String key = req.getKey();

        if (req.getExpiration() == null) {
            req.setExpiration(
                    new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        }

        HttpMethodName httpMethod = HttpMethodName.valueOf(req.getMethod().toString());

        // If the key starts with a slash character itself, the following method
        // will actually add another slash before the resource path to prevent
        // the HttpClient mistakenly treating the slash as a path delimiter.
        // For presigned request, we need to remember to remove this extra slash
        // before generating the URL.
        Request<GeneratePresignedUrlRequest> request = createRequest(
                bucketName, key, req, httpMethod);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GeneratePresignedUrl");

        addParameterIfNotNull(request, "versionId", req.getVersionId());

        if (req.isZeroByteContent())
            request.setContent(new ByteArrayInputStream(new byte[0]));

        for (Entry<String, String> entry : req.getRequestParameters().entrySet()) {
            request.addParameter(entry.getKey(), entry.getValue());
        }

        addHeaderIfNotNull(request, Headers.CONTENT_TYPE, req.getContentType());
        addHeaderIfNotNull(request, Headers.CONTENT_MD5, req.getContentMd5());

        // SSE-C
        populateSSE_C(request, req.getSSECustomerKey());
        // SSE
        addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION,
                req.getSSEAlgorithm());
        // SSE-KMS
        addHeaderIfNotNull(request,
                Headers.SERVER_SIDE_ENCRYPTION_AWS_KMS_KEYID, req.getKmsCmkId());

        // Custom headers that open up the possibility of supporting unexpected
        // cases.
        Map<String, String> customHeaders = req.getCustomRequestHeaders();
        if (customHeaders != null) {
            for (Map.Entry<String, String> e: customHeaders.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }

        addResponseHeaderParameters(request, req.getResponseHeaders());

        Signer signer = createSigner(request, bucketName, key);

        //IBM unsupported
        // if (request.getHandlerContext(HandlerContextKey.SIGNING_NAME) != null && !isSignerOverridden()) {
        //     String signingName = request.getHandlerContext(HandlerContextKey.SIGNING_NAME);
        //     if (signer instanceof ServiceAwareSigner) {
        //         ((ServiceAwareSigner) signer).setServiceName(signingName);
        //     }
        // }

        if (signer instanceof Presigner) {
            // If we have a signer which knows how to presign requests,
            // delegate directly to it.
            ((Presigner) signer).presignRequest(
                    request,
                    CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), awsCredentialsProvider).getCredentials(),
                    req.getExpiration()
            );
        } else {
            // Otherwise use the default presigning method, which is hardcoded
            // to use QueryStringSigner.
            presignRequest(
                request,
                req.getMethod(),
                bucketName,
                key,
                req.getExpiration(),
                null
            );
        }

        // Remove the leading slash (if any) in the resource-path
        return ServiceUtils.convertRequestToUrl(request, true, false);
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest)
            throws SdkClientException, AmazonServiceException {
        abortMultipartUploadRequest = beforeClientExecution(abortMultipartUploadRequest);
        rejectNull(abortMultipartUploadRequest,
            "The request parameter must be specified when aborting a multipart upload");
        rejectNull(abortMultipartUploadRequest.getBucketName(),
            "The bucket name parameter must be specified when aborting a multipart upload");
        rejectNull(abortMultipartUploadRequest.getKey(),
            "The key parameter must be specified when aborting a multipart upload");
        rejectNull(abortMultipartUploadRequest.getUploadId(),
            "The upload ID parameter must be specified when aborting a multipart upload");

        String bucketName = abortMultipartUploadRequest.getBucketName();
        String key = abortMultipartUploadRequest.getKey();

        Request<AbortMultipartUploadRequest> request = createRequest(bucketName, key, abortMultipartUploadRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "AbortMultipartUpload");
        request.addParameter("uploadId", abortMultipartUploadRequest.getUploadId());
        populateRequesterPaysHeader(request, abortMultipartUploadRequest.isRequesterPays());

        invoke(request, voidResponseHandler, bucketName, key);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest)
            throws SdkClientException, AmazonServiceException {
        completeMultipartUploadRequest = beforeClientExecution(completeMultipartUploadRequest);
        rejectNull(completeMultipartUploadRequest,
            "The request parameter must be specified when completing a multipart upload");

        String bucketName = completeMultipartUploadRequest.getBucketName();
        String key = completeMultipartUploadRequest.getKey();
        String uploadId = completeMultipartUploadRequest.getUploadId();
        rejectNull(bucketName,
            "The bucket name parameter must be specified when completing a multipart upload");
        rejectNull(key,
            "The key parameter must be specified when completing a multipart upload");
        rejectNull(uploadId,
            "The upload ID parameter must be specified when completing a multipart upload");
        rejectNull(completeMultipartUploadRequest.getPartETags(),
            "The part ETags parameter must be specified when completing a multipart upload");

        int retries = 0;
        CompleteMultipartUploadHandler handler;
        do {
            Request<CompleteMultipartUploadRequest> request = createRequest(bucketName, key, completeMultipartUploadRequest, HttpMethodName.POST);
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CompleteMultipartUpload");
            request.addParameter("uploadId", uploadId);

            populateRequesterPaysHeader(request, completeMultipartUploadRequest.isRequesterPays());

            byte[] xml = RequestXmlFactory.convertToXmlByteArray(completeMultipartUploadRequest.getPartETags());
            request.addHeader("Content-Type", "application/xml");
            request.addHeader("Content-Length", String.valueOf(xml.length));

            // IBM-specific
            if (completeMultipartUploadRequest.getRetentionExpirationDate() != null) {
                request.addHeader(Headers.RETENTION_EXPIRATION_DATE, DateUtils.formatRFC822Date(completeMultipartUploadRequest.getRetentionExpirationDate()));
            }
            if (completeMultipartUploadRequest.getRetentionPeriod() != null) {
                request.addHeader(Headers.RETENTION_PERIOD, completeMultipartUploadRequest.getRetentionPeriod().toString());
            }
            addHeaderIfNotNull(request, Headers.RETENTION_LEGAL_HOLD_ID, completeMultipartUploadRequest.getRetentionLegalHoldId());


            request.setContent(new ByteArrayInputStream(xml));

            // IBM-specific for retention?
            // Calculate Content MD5
            try {
                byte[] md5 = Md5Utils.computeMD5Hash(new ByteArrayInputStream(xml));
                String md5Base64 = BinaryUtils.toBase64(md5);
                request.addHeader("Content-MD5", md5Base64);
            } catch ( Exception e ) {
                throw new SdkClientException("Couldn't compute md5 sum", e);
            }

            @SuppressWarnings("unchecked")
            ResponseHeaderHandlerChain<CompleteMultipartUploadHandler> responseHandler = new ResponseHeaderHandlerChain<CompleteMultipartUploadHandler>(
                    // xml payload unmarshaller
                    new Unmarshallers.CompleteMultipartUploadResultUnmarshaller(),
                    // header handlers
                    new ServerSideEncryptionHeaderHandler<CompleteMultipartUploadHandler>(),
                    new ObjectExpirationHeaderHandler<CompleteMultipartUploadHandler>(),
                    new S3VersionHeaderHandler<CompleteMultipartUploadHandler>());
            handler = invoke(request, responseHandler, bucketName, key);
            if (handler.getCompleteMultipartUploadResult() != null) {
                return handler.getCompleteMultipartUploadResult();
            }
        } while (shouldRetryCompleteMultipartUpload(completeMultipartUploadRequest,
                handler.getAmazonS3Exception(), retries++));

        throw handler.getAmazonS3Exception();
    }

    private boolean shouldRetryCompleteMultipartUpload(AmazonWebServiceRequest originalRequest,
                                                       AmazonS3Exception exception,
                                                       int retriesAttempted) {

        final RetryPolicy retryPolicy = clientConfiguration.getRetryPolicy();

        if (retryPolicy == null || retryPolicy.getRetryCondition() == null) {
            return false;
        }

        if (retryPolicy == PredefinedRetryPolicies.NO_RETRY_POLICY) {
            return false;
        }

        return completeMultipartUploadRetryCondition.shouldRetry
                (originalRequest, exception, retriesAttempted);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest initiateMultipartUploadRequest)
            throws SdkClientException, AmazonServiceException {
        initiateMultipartUploadRequest = beforeClientExecution(initiateMultipartUploadRequest);
        rejectNull(initiateMultipartUploadRequest,
            "The request parameter must be specified when initiating a multipart upload");

        rejectNull(initiateMultipartUploadRequest.getBucketName(),
            "The bucket name parameter must be specified when initiating a multipart upload");
        rejectNull(initiateMultipartUploadRequest.getKey(),
            "The key parameter must be specified when initiating a multipart upload");

        Request<InitiateMultipartUploadRequest> request = createRequest(initiateMultipartUploadRequest.getBucketName(), initiateMultipartUploadRequest.getKey(), initiateMultipartUploadRequest, HttpMethodName.POST);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateMultipartUpload");
        request.addParameter("uploads", null);

        if (initiateMultipartUploadRequest.getStorageClass() != null)
            request.addHeader(Headers.STORAGE_CLASS, initiateMultipartUploadRequest.getStorageClass().toString());

        if (initiateMultipartUploadRequest.getRedirectLocation() != null) {
            request.addHeader(Headers.REDIRECT_LOCATION, initiateMultipartUploadRequest.getRedirectLocation());
        }

        if ( initiateMultipartUploadRequest.getAccessControlList() != null ) {
            addAclHeaders(request, initiateMultipartUploadRequest.getAccessControlList());
        } else if ( initiateMultipartUploadRequest.getCannedACL() != null ) {
            request.addHeader(Headers.S3_CANNED_ACL, initiateMultipartUploadRequest.getCannedACL().toString());
        }

        if (initiateMultipartUploadRequest.objectMetadata != null) {
            populateRequestMetadata(request, initiateMultipartUploadRequest.objectMetadata);
        }

        //IBM does not support SSE-KMS
        // Boolean bucketKeyEnabled = initiateMultipartUploadRequest.getBucketKeyEnabled();
        // if (bucketKeyEnabled != null) {
        //     addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_BUCKET_KEY_ENABLED,
        //             String.valueOf(bucketKeyEnabled));
        // }

        populateRequesterPaysHeader(request, initiateMultipartUploadRequest.isRequesterPays());

        // Populate the SSE-C parameters to the request header
        populateSSE_C(request, initiateMultipartUploadRequest.getSSECustomerKey());

        // Populate the SSE Amazon Web Services KMS parameters to the request header
        populateSSE_KMS(request,
                initiateMultipartUploadRequest.getSSEAwsKeyManagementParams());

        addHeaderIfNotNull(request, Headers.S3_TAGGING, urlEncodeTags(initiateMultipartUploadRequest.getTagging()));

        populateObjectLockHeaders(request, initiateMultipartUploadRequest.getObjectLockMode(), initiateMultipartUploadRequest.getObjectLockRetainUntilDate(),
                initiateMultipartUploadRequest.getObjectLockLegalHoldStatus());

        // Be careful that we don't send the object's total size as the content
        // length for the InitiateMultipartUpload request.
        setZeroContentLength(request);
        // Set the request content to be empty (but not null) to force the runtime to pass
        // any query params in the query string and not the request body, to keep S3 happy.
        request.setContent(new ByteArrayInputStream(new byte[0]));

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<InitiateMultipartUploadResult> responseHandler = new ResponseHeaderHandlerChain<InitiateMultipartUploadResult>(
                // xml payload unmarshaller
                new Unmarshallers.InitiateMultipartUploadResultUnmarshaller(),
                // header handlers
                new ServerSideEncryptionHeaderHandler<InitiateMultipartUploadResult>(),
                new S3RequesterChargedHeaderHandler<InitiateMultipartUploadResult>(),
                new InitiateMultipartUploadHeaderHandler());
        return invoke(request, responseHandler,
                initiateMultipartUploadRequest.getBucketName(), initiateMultipartUploadRequest.getKey());
    }

    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest)
            throws SdkClientException, AmazonServiceException {
        listMultipartUploadsRequest = beforeClientExecution(listMultipartUploadsRequest);
        rejectNull(listMultipartUploadsRequest,
            "The request parameter must be specified when listing multipart uploads");

        rejectNull(listMultipartUploadsRequest.getBucketName(),
            "The bucket name parameter must be specified when listing multipart uploads");

        Request<ListMultipartUploadsRequest> request = createRequest(listMultipartUploadsRequest.getBucketName(), null, listMultipartUploadsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListMultipartUploads");
        request.addParameter("uploads", null);

        if (listMultipartUploadsRequest.getKeyMarker() != null) request.addParameter("key-marker", listMultipartUploadsRequest.getKeyMarker());
        if (listMultipartUploadsRequest.getMaxUploads() != null) request.addParameter("max-uploads", listMultipartUploadsRequest.getMaxUploads().toString());
        if (listMultipartUploadsRequest.getUploadIdMarker() != null) request.addParameter("upload-id-marker", listMultipartUploadsRequest.getUploadIdMarker());
        if (listMultipartUploadsRequest.getDelimiter() != null) request.addParameter("delimiter", listMultipartUploadsRequest.getDelimiter());
        if (listMultipartUploadsRequest.getPrefix() != null) request.addParameter("prefix", listMultipartUploadsRequest.getPrefix());
        if (listMultipartUploadsRequest.getEncodingType() != null) request.addParameter("encoding-type", listMultipartUploadsRequest.getEncodingType());

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, listMultipartUploadsRequest.getWormMirrorDestination());

        return invoke(request, new Unmarshallers.ListMultipartUploadsResultUnmarshaller(), listMultipartUploadsRequest.getBucketName(), null);
    }

    @Override
    public PartListing listParts(ListPartsRequest listPartsRequest)
            throws SdkClientException, AmazonServiceException {
        listPartsRequest = beforeClientExecution(listPartsRequest);
        rejectNull(listPartsRequest,
            "The request parameter must be specified when listing parts");

        rejectNull(listPartsRequest.getBucketName(),
            "The bucket name parameter must be specified when listing parts");
        rejectNull(listPartsRequest.getKey(),
            "The key parameter must be specified when listing parts");
        rejectNull(listPartsRequest.getUploadId(),
            "The upload ID parameter must be specified when listing parts");

        Request<ListPartsRequest> request = createRequest(listPartsRequest.getBucketName(), listPartsRequest.getKey(), listPartsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListParts");
        request.addParameter("uploadId", listPartsRequest.getUploadId());

        if (listPartsRequest.getMaxParts() != null) request.addParameter("max-parts", listPartsRequest.getMaxParts().toString());
        if (listPartsRequest.getPartNumberMarker() != null) request.addParameter("part-number-marker", listPartsRequest.getPartNumberMarker().toString());
        if (listPartsRequest.getEncodingType() != null) request.addParameter("encoding-type", listPartsRequest.getEncodingType());

        populateRequesterPaysHeader(request, listPartsRequest.isRequesterPays());

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, listPartsRequest.getWormMirrorDestination());

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<PartListing> responseHandler = new ResponseHeaderHandlerChain<PartListing>(
                // xml payload unmarshaller
                new Unmarshallers.ListPartsResultUnmarshaller(),
                // header handler
                new S3RequesterChargedHeaderHandler<PartListing>(),
                new ListPartsHeaderHandler());
        return invoke(request, responseHandler, listPartsRequest.getBucketName(), listPartsRequest.getKey());
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest)
            throws SdkClientException, AmazonServiceException {
        uploadPartRequest = beforeClientExecution(uploadPartRequest);
        rejectNull(uploadPartRequest,
            "The request parameter must be specified when uploading a part");
        final File fileOrig = uploadPartRequest.getFile();
        final InputStream isOrig = uploadPartRequest.getInputStream();
        final String bucketName = uploadPartRequest.getBucketName();
        final String key        = uploadPartRequest.getKey();
        final String uploadId   = uploadPartRequest.getUploadId();
        final int partNumber    = uploadPartRequest.getPartNumber();
        final long partSize     = uploadPartRequest.getPartSize();
        rejectNull(bucketName,
            "The bucket name parameter must be specified when uploading a part");
        rejectNull(key,
            "The key parameter must be specified when uploading a part");
        rejectNull(uploadId,
            "The upload ID parameter must be specified when uploading a part");
        Request<UploadPartRequest> request = createRequest(bucketName, key, uploadPartRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UploadPart");
        request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, Boolean.TRUE);
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, Boolean.TRUE);

        request.addParameter("uploadId", uploadId);
        request.addParameter("partNumber", Integer.toString(partNumber));

        final ObjectMetadata objectMetadata = uploadPartRequest.getObjectMetadata();
        if (objectMetadata != null)
            populateRequestMetadata(request, objectMetadata);

        addHeaderIfNotNull(request, Headers.CONTENT_MD5, uploadPartRequest.getMd5Digest());
        request.addHeader(Headers.CONTENT_LENGTH, Long.toString(partSize));

        populateRequesterPaysHeader(request, uploadPartRequest.isRequesterPays());

        // Populate the SSE-C parameters to the request header
        populateSSE_C(request, uploadPartRequest.getSSECustomerKey());
        InputStream isCurr = isOrig;
        try {
            if (fileOrig == null) {
                if (isOrig == null) {
                    throw new IllegalArgumentException(
                        "A File or InputStream must be specified when uploading part");
                } else {
                    // When isCurr is a FileInputStream, this wrapping enables
                    // unlimited mark-and-reset
                    isCurr = ReleasableInputStream.wrap(isCurr);
                }
                // Make backward compatible with buffer size via system property
                final Integer bufsize = Constants.getS3StreamBufferSize();
                if (bufsize != null) {
                    AmazonWebServiceRequest awsreq = request.getOriginalRequest();
                    // Note awsreq is never null at this point even if the original
                    // request was
                    awsreq.getRequestClientOptions()
                        .setReadLimit(bufsize.intValue());
                }
            } else {
                try {
                    isCurr = new ResettableInputStream(fileOrig);
                } catch(IOException e) {
                    throw new IllegalArgumentException("Failed to open file "
                            + fileOrig, e);
                }
            }

            //IBM unsupported
            // isCurr = new InputSubstream(
            //         isCurr,
            //         uploadPartRequest.getFileOffset(),
            //         partSize,
            //         uploadPartRequest.isLastPart());
            final boolean closeStream = uploadPartRequest.isCalculateMD5() ? false : uploadPartRequest.isLastPart();
            isCurr = new InputSubstream(
                    isCurr,
                    uploadPartRequest.getFileOffset(),
                    partSize,
                    closeStream);

            // Calculate Content MD5 on part upload if requested.
            if (uploadPartRequest.getMd5Digest() == null && uploadPartRequest.isCalculateMD5() && isCurr.markSupported()) {
            try {
                uploadPartRequest.setMd5Digest(Md5Utils.md5AsBase64(isCurr));
            request.addHeader("Content-MD5", uploadPartRequest.getMd5Digest());
            isCurr.reset();
        } catch (IOException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }
            }

            MD5DigestCalculatingInputStream md5DigestStream = null;
            if (uploadPartRequest.getMd5Digest() == null
                    && !skipMd5CheckStrategy.skipClientSideValidationPerRequest(uploadPartRequest)) {
                /*
                 * If the user hasn't set the content MD5, then we don't want to buffer the whole
                 * stream in memory just to calculate it. Instead, we can calculate it on the fly
                 * and validate it with the returned ETag from the object upload.
                 */
                isCurr = md5DigestStream = new MD5DigestCalculatingInputStream(isCurr);
            }
            final ProgressListener listener = uploadPartRequest.getGeneralProgressListener();
            publishProgress(listener, ProgressEventType.TRANSFER_PART_STARTED_EVENT);
            return doUploadPart(bucketName, key, uploadId, partNumber,
                    partSize, request, isCurr, md5DigestStream, listener);
        } finally {
            cleanupDataSource(uploadPartRequest, fileOrig, isOrig, isCurr, log);
        }
    }

    private UploadPartResult doUploadPart(final String bucketName,
            final String key, final String uploadId, final int partNumber,
            final long partSize, Request<UploadPartRequest> request,
            InputStream inputStream,
            MD5DigestCalculatingInputStream md5DigestStream,
            final ProgressListener listener) {
        try {
            request.setContent(inputStream);
            ObjectMetadata metadata = invoke(request, new S3MetadataResponseHandler(), bucketName, key);
            final String etag = metadata.getETag();

            if (md5DigestStream != null
                    && !skipMd5CheckStrategy.skipClientSideValidationPerUploadPartResponse(metadata)) {
                byte[] clientSideHash = md5DigestStream.getMd5Digest();
                byte[] serverSideHash = BinaryUtils.fromHex(etag);

                if (!Arrays.equals(clientSideHash, serverSideHash)) {
                    final String info = "bucketName: " + bucketName + ", key: "
                            + key + ", uploadId: " + uploadId
                            + ", partNumber: " + partNumber + ", partSize: "
                            + partSize;
                    throw new SdkClientException(
                         "Unable to verify integrity of data upload.  "
                        + "Client calculated content hash (contentMD5: "
                        + Base16.encodeAsString(clientSideHash)
                        + " in hex) didn't match hash (etag: "
                        + etag
                        + " in hex) calculated by Amazon S3.  "
                        + "You may need to delete the data stored in Amazon S3. "
                        + "(" + info + ")");
                }
            }
            publishProgress(listener, ProgressEventType.TRANSFER_PART_COMPLETED_EVENT);
            UploadPartResult result = new UploadPartResult();
            result.setETag(etag);
            result.setPartNumber(partNumber);
            result.setSSEAlgorithm(metadata.getSSEAlgorithm());
            result.setSSECustomerAlgorithm(metadata.getSSECustomerAlgorithm());
            result.setSSECustomerKeyMd5(metadata.getSSECustomerKeyMd5());
            result.setRequesterCharged(metadata.isRequesterCharged());
            //IBM does not support SSE-KMS
            //result.setBucketKeyEnabled(metadata.getBucketKeyEnabled());
            return result;
        } catch (Throwable t) {
            publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
            // Leaving this here in case anyone is depending on it, but it's
            // inconsistent with other methods which only generate one of
            // COMPLETED_EVENT_CODE or FAILED_EVENT_CODE.
            publishProgress(listener, ProgressEventType.TRANSFER_PART_COMPLETED_EVENT);
            throw failure(t);
        }
    }

    @Override
    public S3ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return (S3ResponseMetadata)client.getResponseMetadataForRequest(request);
    }
    //IBM overridden
    // @Override
    // public void restoreObject(RestoreObjectRequest restoreObjectRequest)
    //     throws AmazonServiceException {
    //     restoreObjectV2(restoreObjectRequest);
    // }

    // @Override
    // public RestoreObjectResult restoreObjectV2(RestoreObjectRequest restoreObjectRequest)
    //     throws AmazonServiceException {

    //     restoreObjectRequest = beforeClientExecution(restoreObjectRequest);
    //     String bucketName = restoreObjectRequest.getBucketName();
    //     String key = restoreObjectRequest.getKey();

    //     rejectNull(bucketName, "The bucket name parameter must be specified when restoring a glacier object");
    //     rejectNull(key, "The key parameter must be specified when restoring a glacier object");

    //     if (restoreObjectRequest.getOutputLocation() != null) {
    //         rejectNull(restoreObjectRequest.getType(), "The restore request type must be specified with restores that specify OutputLocation");

    //         if (RestoreRequestType.SELECT.toString().equals(restoreObjectRequest.getType())) {
    //             rejectNull(restoreObjectRequest.getSelectParameters(),
    //                    "The select parameters must be specified when restoring a glacier object with SELECT restore request type");
    //     }
    // }

    //     Request<RestoreObjectRequest> request = createRestoreObjectRequest(restoreObjectRequest);

    //     @SuppressWarnings("unchecked")
    //     ResponseHeaderHandlerChain<RestoreObjectResult> responseHandler = new ResponseHeaderHandlerChain<RestoreObjectResult>(
    //         new Unmarshallers.RestoreObjectResultUnmarshaller(),
    //         new S3RequesterChargedHeaderHandler<RestoreObjectResult>(),
    //         new S3RestoreOutputPathHeaderHandler<RestoreObjectResult>());

    //     return invoke(request, responseHandler, bucketName, key);
    // }

    @Override
    public void restoreObject(RestoreObjectRequest restoreObjectRequest)
        throws AmazonServiceException {
        restoreObjectRequest = beforeClientExecution(restoreObjectRequest);
        String bucketName = restoreObjectRequest.getBucketName();
        String key = restoreObjectRequest.getKey();
        String versionId = restoreObjectRequest.getVersionId();
        int expirationIndays = restoreObjectRequest.getExpirationInDays();

        rejectNull(bucketName, "The bucket name parameter must be specified when restoring a glacier object");
        rejectNull(key, "The key parameter must be specified when restoring a glacier object");

        if (expirationIndays == -1) {
            throw new IllegalArgumentException("The expiration in days parameter must be specified when copying a glacier object");
        }

        Request<RestoreObjectRequest> request = createRequest(bucketName, key, restoreObjectRequest, HttpMethodName.POST);
        request.addParameter("restore", null);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }

        populateRequesterPaysHeader(request, restoreObjectRequest.isRequesterPays());

        byte[] content = RequestXmlFactory.convertToXmlByteArray(restoreObjectRequest);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", md5Base64);
        } catch (Exception e) {
            throw new SdkClientException("Couldn't compute md5 sum", e);
        }

        invoke(request, voidResponseHandler, bucketName, key);
    }

    @Override
    public void restoreObject(String bucketName, String key, int expirationInDays)
            throws AmazonServiceException {
        restoreObject(new RestoreObjectRequest(bucketName, key, expirationInDays));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, String content)
            throws AmazonServiceException, SdkClientException {

        rejectNull(bucketName, "Bucket name must be provided");
        rejectNull(key, "Object key must be provided");
        rejectNull(content, "String content must be provided");

        byte[] contentBytes = content.getBytes(StringUtils.UTF8);

        InputStream is = new ByteArrayInputStream(contentBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        metadata.setContentLength(contentBytes.length);

        return putObject(new PutObjectRequest(bucketName, key, is, metadata));
    }

    /*
     * Private Interface
     */

    /**
     * <p>
     * Asserts that the specified parameter value is not <code>null</code> and if it is,
     * throws an <code>IllegalArgumentException</code> with the specified error message.
     * </p>
     *
     * @param parameterValue
     *            The parameter value being checked.
     * @param errorMessage
     *            The error message to include in the IllegalArgumentException
     *            if the specified parameter is null.
     */
    private void rejectNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null) throw new IllegalArgumentException(errorMessage);
    }

    /**
     * <p>
     * Gets the Amazon S3 {@link AccessControlList} (ACL) for the specified resource.
     * (bucket if only the bucketName parameter is specified, otherwise the object with the
     * specified key in the bucket).
     * </p>
     *
     * @param bucketName
     *            The name of the bucket whose ACL should be returned if the key
     *            parameter is not specified, otherwise the bucket containing
     *            the specified key.
     * @param key
     *            The object key whose ACL should be retrieve. If not specified,
     *            the bucket's ACL is returned.
     * @param versionId
     *            The version ID of the object version whose ACL is being
     *            retrieved.
     * @param originalRequest
     *            The original, user facing request object.
     *
     * @return The S3 ACL for the specified resource.
     */
    private AccessControlList getAcl(String bucketName, String key, String versionId,
            boolean isRequesterPays, AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) originalRequest = new GenericBucketRequest(bucketName);

        Request<AmazonWebServiceRequest> request = createRequest(bucketName, key, originalRequest, HttpMethodName.GET);

        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAcl");
        }

        request.addParameter("acl", null);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        populateRequesterPaysHeader(request, isRequesterPays);

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<AccessControlList> responseHandler = new ResponseHeaderHandlerChain<AccessControlList>(
                new Unmarshallers.AccessControlListUnmarshaller(),
                new S3RequesterChargedHeaderHandler<AccessControlList>());

        return invoke(request, responseHandler, bucketName, key);
    }

    /**
     * Sets the Canned ACL for the specified resource in S3. If only bucketName
     * is specified, the canned ACL will be applied to the bucket, otherwise if
     * bucketName and key are specified, the canned ACL will be applied to the
     * object.
     *
     * @param bucketName
     *            The name of the bucket containing the specified key, or if no
     *            key is listed, the bucket whose ACL will be set.
     * @param key
     *            The optional object key within the specified bucket whose ACL
     *            will be set. If not specified, the bucket ACL will be set.
     * @param versionId
     *            The version ID of the object version whose ACL is being set.
     * @param cannedAcl
     *            The canned ACL to apply to the resource.
     * @param originalRequest
     *            The original, user facing request object.
     */
    private void setAcl(String bucketName, String key, String versionId, CannedAccessControlList cannedAcl, boolean isRequesterPays,
            AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) originalRequest = new GenericBucketRequest(bucketName);

        Request<AmazonWebServiceRequest> request = createRequest(bucketName, key, originalRequest, HttpMethodName.PUT);

        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAcl");
        }

        request.addParameter("acl", null);
        request.addHeader(Headers.S3_CANNED_ACL, cannedAcl.toString());
        if (versionId != null) request.addParameter("versionId", versionId);
        populateRequesterPaysHeader(request, isRequesterPays);

        invoke(request, voidResponseHandler, bucketName, key);
    }

    /**
     * Sets the ACL for the specified resource in S3. If only bucketName is
     * specified, the ACL will be applied to the bucket, otherwise if bucketName
     * and key are specified, the ACL will be applied to the object.
     *
     * @param bucketName
     *            The name of the bucket containing the specified key, or if no
     *            key is listed, the bucket whose ACL will be set.
     * @param key
     *            The optional object key within the specified bucket whose ACL
     *            will be set. If not specified, the bucket ACL will be set.
     * @param versionId
     *            The version ID of the object version whose ACL is being set.
     * @param acl
     *            The ACL to apply to the resource.
     * @param originalRequest
     *            The original, user facing request object.
     */
    private void setAcl(String bucketName, String key, String versionId, AccessControlList acl, boolean isRequesterPays,
            AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) originalRequest = new GenericBucketRequest(bucketName);

        Request<AmazonWebServiceRequest> request = createRequest(bucketName, key, originalRequest, HttpMethodName.PUT);

        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAcl");
        }

        request.addParameter("acl", null);
        if (versionId != null) request.addParameter("versionId", versionId);
        populateRequesterPaysHeader(request, isRequesterPays);

        byte[] aclAsXml = new AclXmlFactory().convertToXmlByteArray(acl);
        request.addHeader("Content-Type", "application/xml");
        request.addHeader("Content-Length", String.valueOf(aclAsXml.length));
        request.setContent(new ByteArrayInputStream(aclAsXml));
        populateRequestHeaderWithMd5(request, aclAsXml);

        invoke(request, voidResponseHandler, bucketName, key);
    }

    /**
     * Returns a "complete" S3 specific signer, taking into the S3 bucket, key,
     * and the current S3 client configuration into account.
     */
    protected Signer createSigner(final Request<?> request,
                                  final String bucketName,
                                  final String key) {
        return createSigner(request, bucketName, key, false);
    }

    /**
     * Returns a "complete" S3 specific signer, taking into the S3 bucket, key,
     * and the current S3 client configuration into account.
     */
    protected Signer createSigner(final Request<?> request,
                                  final String bucketName,
                                  final String key,
                                  final boolean isAdditionalHeadRequestToFindRegion) {

        // Instead of using request.getEndpoint() for this parameter, we use endpoint which is because
        // in accelerate mode, the endpoint in request is regionless. We need the client-wide endpoint
        // to fetch the region information and pick the correct signer.
        URI uri = clientOptions.isAccelerateModeEnabled() ? endpoint : request.getEndpoint();
        //IBM unsupported
        // Signer signer;
        // if (isAccessPointArn(bucketName)) {
        //     Arn resourceArn = Arn.fromString(bucketName);
        //     S3Resource s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
        //     String region = s3Resource.getRegion();
        //     String regionalEndpoint = RegionUtils.getRegion(region).getServiceEndpoint("s3");

        //     signer = getSignerByURI(URI.create(uri.getScheme() + "://" + regionalEndpoint));
        // } else {
        //     signer = getSignerByURI(uri);
        // }
        final Signer signer = getSignerByURI(uri);

        // IBM-specific
        if (this.awsCredentialsProvider.getCredentials() instanceof IBMOAuthCredentials) {
            IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)this.awsCredentialsProvider.getCredentials();
            if (oAuthCreds.getApiKey() != null || oAuthCreds.getTokenManager() != null) {
                return new IBMOAuthSigner(clientConfiguration);
            }
        }

        if (!isSignerOverridden()) {
            if ((signer instanceof AWSS3V4Signer) && bucketRegionShouldBeCached(request)) {

                String region = bucketRegionCache.get(bucketName);
                if (region != null) {
                     // If cache contains the region for the bucket, create an endpoint for the region and
                     // update the request with that endpoint if accelerate mode is not enabled
                     //IBM unsupported
					 //request.addHandlerContext(HandlerContextKey.SIGNING_REGION, region);
                     if (!clientOptions.isAccelerateModeEnabled()) {
                         String serviceEndpoint = RegionUtils.getRegion(region).getServiceEndpoint(S3_SERVICE_NAME);
                         resolveRequestEndpoint(request,
                                                bucketName,
                                                key,
                                                RuntimeHttpUtils.toUri(serviceEndpoint, clientConfiguration),
                                                region);
                     }
                     return updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer) signer, request, region);
                } else if (request.getOriginalRequest() instanceof GeneratePresignedUrlRequest) {
                    //IBM unsupported
                    // String signerRegion = getSignerRegion();
                    // if (signerRegion == null) {
                    //     return createSigV2Signer(request, bucketName, key);
                    // }
                    // return updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer) signer, request, signerRegion);
                    return createSigV2Signer(request, bucketName, key);
                } else if (isAdditionalHeadRequestToFindRegion) {
                    return updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer) signer, request, "us-east-1");
                }
            }

            String regionOverride = getSignerRegionOverride();
            if (regionOverride != null) {
                return updateSigV4SignerWithServiceAndRegion(new AWSS3V4Signer(), request, regionOverride);
            }
        }

        if (signer instanceof S3Signer) {
            // The old S3Signer needs a method and path passed to its
            // constructor; if that's what we should use, getSigner()
            // will return a dummy instance and we need to create a
            // new one with the appropriate values for this request.
            return createSigV2Signer(request, bucketName, key);
        }

        return signer;
    }

    private S3Signer createSigV2Signer(final Request<?> request,
                                            final String bucketName,
                                            final String key) {
        String resourcePath = "/" +
                ((bucketName != null) ? bucketName + "/" : "") +
                ((key != null) ? key : "");
        return new S3Signer(request.getHttpMethod().toString(), resourcePath);
    }

    private AWSS3V4Signer updateSigV4SignerWithServiceAndRegion(final AWSS3V4Signer v4Signer, Request<?> request, String region) {
        String signingNameOverride = request.getHandlerContext(HandlerContextKey.SIGNING_NAME);
        if (signingNameOverride != null) {
            v4Signer.setServiceName(signingNameOverride);
        } else {
            v4Signer.setServiceName(getServiceNameIntern());
        }

        String signingRegionOverride = request.getHandlerContext(HandlerContextKey.SIGNING_REGION);
        if (signingRegionOverride != null) {
            v4Signer.setRegionName(signingRegionOverride);
        } else {
            v4Signer.setRegionName(region);
        }
        return v4Signer;
    }

    /**
     * Return the region string that should be used for signing requests sent by
     * this client. This method can only return null if both of the following
     * are true:
     * (a) the user has never specified a region via setRegion/configureRegion/setSignerRegionOverride
     * (b) the user has specified a client endpoint that is known to be a global S3 endpoint
     */
    private String getSignerRegion() {
        String region = getSignerRegionOverride();
        if (region == null) {
            region = clientRegion;
        }
        return region;
    }

    /**
     * Has signer been explicitly overriden in the configuration?
     */
    private boolean isSignerOverridden() {
        return clientConfiguration != null
          && clientConfiguration.getSignerOverride() != null;
    }

    /**
     * <p>Returns true if the region required for signing could not be computed from the client or the request.</p>
     * <p>
     * This is the case when the standard endpoint is in use and neither an explicit region nor a signer override
     * have been provided by the user.
     * </p>
     */
    private boolean noExplicitRegionProvided(final Request<?> request) {
        return isStandardEndpoint(request.getEndpoint()) &&
                getSignerRegion() == null;
    }

    private boolean isStandardEndpoint(URI endpoint) {
        return endpoint.getHost().endsWith(Constants.S3_HOSTNAME);
    }

    /**
     * Pre-signs the specified request, using a signature query-string
     * parameter.
     *
     * @param request
     *            The request to sign.
     * @param methodName
     *            The HTTP method (GET, PUT, DELETE, HEAD) for the specified
     *            request.
     * @param bucketName
     *            The name of the bucket involved in the request. If the request
     *            is not an operation on a bucket this parameter should be null.
     * @param key
     *            The object key involved in the request. If the request is not
     *            an operation on an object, this parameter should be null.
     * @param expiration
     *            The time at which the signed request is no longer valid, and
     *            will stop working.
     * @param subResource
     *            The optional sub-resource being requested as part of the
     *            request (e.g. "location", "acl", "logging", or "torrent").
     */
    protected <T> void presignRequest(Request<T> request, HttpMethod methodName,
            String bucketName, String key, Date expiration, String subResource) {
        // Run any additional request handlers if present
        beforeRequest(request);

        String resourcePath = "/" +
            ((bucketName != null) ? bucketName + "/" : "") +
            ((key != null) ? SdkHttpUtils.urlEncode(key, true) : "") +
            ((subResource != null) ? "?" + subResource : "");

        // Make sure the resource-path for signing does not contain
        // any consecutive "/"s.
        // Note that we should also follow the same rule to escape
        // consecutive "/"s when generating the presigned URL.
        // See ServiceUtils#convertRequestToUrl(...)
        resourcePath = resourcePath.replaceAll("(?<=/)/", "%2F");

        new S3QueryStringSigner(methodName.toString(), resourcePath, expiration)
            .sign(request, CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), awsCredentialsProvider).getCredentials());

        // The Amazon S3 DevPay token header is a special exception and can be safely moved
        // from the request's headers into the query string to ensure that it travels along
        // with the pre-signed URL when it's sent back to Amazon S3.
        if (request.getHeaders().containsKey(Headers.SECURITY_TOKEN)) {
            String value = request.getHeaders().get(Headers.SECURITY_TOKEN);
            request.addParameter(Headers.SECURITY_TOKEN, value);
            request.getHeaders().remove(Headers.SECURITY_TOKEN);
        }
    }

    private <T> void beforeRequest(Request<T> request) {
        if (requestHandler2s != null) {
            for (RequestHandler2 requestHandler2 : requestHandler2s) {
                requestHandler2.beforeRequest(request);
            }
        }
    }

    /**
     * <p>
     * Populates the specified request object with the appropriate headers from
     * the {@link ObjectMetadata} object.
     * </p>
     *
     * @param request
     *            The request to populate with headers.
     * @param metadata
     *            The metadata containing the header information to include in
     *            the request.
     */
    protected static void populateRequestMetadata(Request<?> request, ObjectMetadata metadata) {
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Entry<String, Object> entry : rawMetadata.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }

        Date httpExpiresDate = metadata.getHttpExpiresDate();
        if (httpExpiresDate != null) {
            request.addHeader(Headers.EXPIRES, DateUtils.formatRFC822Date(httpExpiresDate));
        }

        Map<String, String> userMetadata = metadata.getUserMetadata();
        if (userMetadata != null) {
            for (Entry<String, String> entry : userMetadata.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null) key = key.trim();
                if (value != null) value = value.trim();
                request.addHeader(Headers.S3_USER_METADATA_PREFIX + key, value);
            }
        }
    }

    /**
     * <p>
     * Populate the specified request with {@link Constants#REQUESTER_PAYS} to header {@link Headers#REQUESTER_PAYS_HEADER},
     * if isRequesterPays is true.
     * </p>
     *
     * @param request
     *              The specified request to populate.
     * @param isRequesterPays
     *              The flag whether to populate the header or not.
     */
    protected static void populateRequesterPaysHeader(Request<?> request, boolean isRequesterPays) {
        if (isRequesterPays) {
            request.addHeader(Headers.REQUESTER_PAYS_HEADER, Constants.REQUESTER_PAYS);
        }
    }

    /**
     * <p>
     * Populates the specified request with the specified Multi-Factor
     * Authentication (MFA) details. This includes the MFA header with device serial
     * number and generated token. Since all requests which include the MFA
     * header must be sent over HTTPS, this operation also configures the request object to
     * use HTTPS instead of HTTP.
     * </p>
     *
     * @param request
     *            The request to populate.
     * @param mfa
     *            The Multi-Factor Authentication information.
     */
    private void populateRequestWithMfaDetails(Request<?> request, MultiFactorAuthentication mfa) {
        if (mfa == null) return;

        String endpoint = request.getEndpoint().toString();
        if (endpoint.startsWith("http://")) {
            String httpsEndpoint = endpoint.replace("http://", "https://");
            request.setEndpoint(URI.create(httpsEndpoint));
            log.info("Overriding current endpoint to use HTTPS " +
                    "as required by S3 for requests containing an MFA header");
        }

        request.addHeader(Headers.S3_MFA,
                mfa.getDeviceSerialNumber() + " " + mfa.getToken());
    }

    /**
     * <p>
     * Populates the specified request with the numerous options available in
     * <code>CopyObjectRequest</code>.
     * </p>
     *
     * @param request
     *            The request to populate with headers to represent all the
     *            options expressed in the <code>CopyObjectRequest</code> object.
     * @param copyObjectRequest
     *            The object containing all the options for copying an object in
     *            Amazon S3.
     */
    private void populateRequestWithCopyObjectParameters(Request<? extends AmazonWebServiceRequest> request, CopyObjectRequest copyObjectRequest) {
        String copySourceHeader = assembleCopySourceHeader(copyObjectRequest.getSourceBucketName(),
                                                           copyObjectRequest.getSourceKey(),
                                                           copyObjectRequest.getSourceVersionId());

        request.addHeader("x-amz-copy-source", copySourceHeader);

        addDateHeader(request, Headers.COPY_SOURCE_IF_MODIFIED_SINCE,
                copyObjectRequest.getModifiedSinceConstraint());
        addDateHeader(request, Headers.COPY_SOURCE_IF_UNMODIFIED_SINCE,
                copyObjectRequest.getUnmodifiedSinceConstraint());

        addStringListHeader(request, Headers.COPY_SOURCE_IF_MATCH,
                copyObjectRequest.getMatchingETagConstraints());
        addStringListHeader(request, Headers.COPY_SOURCE_IF_NO_MATCH,
                copyObjectRequest.getNonmatchingETagConstraints());

        if (copyObjectRequest.getAccessControlList() != null) {
            addAclHeaders(request, copyObjectRequest.getAccessControlList());
        } else if (copyObjectRequest.getCannedAccessControlList() != null) {
            request.addHeader(Headers.S3_CANNED_ACL,
                    copyObjectRequest.getCannedAccessControlList().toString());
        }

        if (copyObjectRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, copyObjectRequest.getStorageClass());
        }

        if (copyObjectRequest.getRedirectLocation() != null) {
            request.addHeader(Headers.REDIRECT_LOCATION, copyObjectRequest.getRedirectLocation());
        }

        if (copyObjectRequest.getRetentionDirective() != null) {
            request.addHeader(Headers.RETENTION_DIRECTIVE, copyObjectRequest.getRetentionDirective().toString());
        }

        if (copyObjectRequest.getRetentionExpirationDate() != null) {
            request.addHeader(Headers.RETENTION_EXPIRATION_DATE, DateUtils.formatRFC822Date(copyObjectRequest.getRetentionExpirationDate()));
        }

        if (copyObjectRequest.getRetentionLegalHoldId() != null) {
            request.addHeader(Headers.RETENTION_LEGAL_HOLD_ID, copyObjectRequest.getRetentionLegalHoldId());
        }

        if (copyObjectRequest.getRetentionPeriod() != null) {
            request.addHeader(Headers.RETENTION_PERIOD, copyObjectRequest.getRetentionPeriod().toString());
        }

        populateRequesterPaysHeader(request, copyObjectRequest.isRequesterPays());

        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (copyObjectRequest.getMetadataDirective() != null) {
            request.addHeader(Headers.METADATA_DIRECTIVE, copyObjectRequest.getMetadataDirective());
        } else if (newObjectMetadata != null) {
            request.addHeader(Headers.METADATA_DIRECTIVE, "REPLACE");
        }
        if (newObjectMetadata != null) {
            populateRequestMetadata(request, newObjectMetadata);
        }

        ObjectTagging newObjectTagging = copyObjectRequest.getNewObjectTagging();
        if (newObjectTagging != null) {
            request.addHeader(Headers.TAGGING_DIRECTIVE, "REPLACE");
            request.addHeader(Headers.S3_TAGGING, urlEncodeTags(newObjectTagging));
        }

        // Populate the SSE-C parameters for the destination object
        populateSourceSSE_C(request, copyObjectRequest.getSourceSSECustomerKey());
        populateSSE_C(request, copyObjectRequest.getDestinationSSECustomerKey());
    }

    /**
     * <p>
     * Populates the specified request with the numerous options available in
     * <code>CopyObjectRequest</code>.
     * </p>
     *
     * @param request
     *            The request to populate with headers to represent all the
     *            options expressed in the <code>CopyPartRequest</code> object.
     * @param copyPartRequest
     *            The object containing all the options for copying an object in
     *            Amazon S3.
     */
    private void populateRequestWithCopyPartParameters(Request<?> request, CopyPartRequest copyPartRequest) {
        String copySourceHeader = assembleCopySourceHeader(copyPartRequest.getSourceBucketName(),
                                                           copyPartRequest.getSourceKey(),
                                                           copyPartRequest.getSourceVersionId());
        request.addHeader("x-amz-copy-source", copySourceHeader);

        addDateHeader(request, Headers.COPY_SOURCE_IF_MODIFIED_SINCE,
                copyPartRequest.getModifiedSinceConstraint());
        addDateHeader(request, Headers.COPY_SOURCE_IF_UNMODIFIED_SINCE,
                copyPartRequest.getUnmodifiedSinceConstraint());

        addStringListHeader(request, Headers.COPY_SOURCE_IF_MATCH,
                copyPartRequest.getMatchingETagConstraints());
        addStringListHeader(request, Headers.COPY_SOURCE_IF_NO_MATCH,
                copyPartRequest.getNonmatchingETagConstraints());

        if ( copyPartRequest.getFirstByte() != null && copyPartRequest.getLastByte() != null ) {
            String range = "bytes=" + copyPartRequest.getFirstByte() + "-" + copyPartRequest.getLastByte();
            request.addHeader(Headers.COPY_PART_RANGE, range);
        }

        // Populate the SSE-C parameters for the destination object
        populateSourceSSE_C(request, copyPartRequest.getSourceSSECustomerKey());
        populateSSE_C(request, copyPartRequest.getDestinationSSECustomerKey());
    }


    /**
     * Populates the specified request header with Content-MD5.
     * @param request The request to populate with Content-MD5 header.
     * @param content Content for which MD5Hash is calculated.
     */
    private void populateRequestHeaderWithMd5(Request<?> request, byte[] content) {
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", md5Base64);
        } catch ( Exception e ) {
            throw new SdkClientException("Couldn't compute md5 sum", e);
        }
    }


    /**
     * Assemble copy source header (x-amz-copy-source) from copy source bucket name, object key, and version ID.
     *
     * @param sourceBucketName copy source bucket name, can either be source bucket name or source access point ARN
     * @param sourceObjectKey  copy source object key
     * @param sourceVersionId  copy source version ID, optional.
     * @return copy source header (x-amz-copy-source)
     */
    private String assembleCopySourceHeader(String sourceBucketName, String sourceObjectKey, String sourceVersionId) {
        if (sourceBucketName == null) {
            throw new IllegalArgumentException("Copy source bucket name should not be null");
        }

        if (sourceObjectKey == null) {
            throw new IllegalArgumentException("Copy source object key should not be null");
        }

        String copySourceHeader;

        //IBM does not support ARN
        // if (isArn(sourceBucketName)) {
        //     // The source bucket name appears to be ARN. Parse it as S3 access point ARN and form
        //     // object-via-access-point copy source header.
        //     Arn resourceArn = Arn.fromString(sourceBucketName);
        //     S3Resource s3Resource;
        //     try {
        //         s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
        //     } catch (RuntimeException e) {
        //         throw new IllegalArgumentException("An ARN was passed as a bucket parameter to an S3 operation, "
        //                                            + "however it does not appear to be a valid S3 access point ARN.", e);
        //     }
        //     if (!S3ResourceType.ACCESS_POINT.toString().equals(s3Resource.getType())) {
        //         throw new IllegalArgumentException("An ARN was passed as a bucket parameter to an S3 operation, "
        //                                               + "however it does not appear to be a valid S3 access point ARN.");
        //     }

        //     copySourceHeader = SdkHttpUtils.urlEncode(sourceBucketName + "/object/" + sourceObjectKey, false);
        // } else {
            copySourceHeader = "/" + SdkHttpUtils.urlEncode(sourceBucketName, true)
                    + "/" + SdkHttpUtils.urlEncode(sourceObjectKey, true);
        //}

        if (sourceVersionId != null) {
            copySourceHeader += "?versionId=" + sourceVersionId;
        }

        return copySourceHeader;
    }

    /**
     * <p>
     * Populates the specified request with the numerous attributes available in
     * <code>SSEWithCustomerKeyRequest</code>.
     * </p>
     *
     * @param request
     *            The request to populate with headers to represent all the
     *            options expressed in the
     *            <code>ServerSideEncryptionWithCustomerKeyRequest</code>
     *            object.
     * @param sseKey
     *            The request object for an S3 operation that allows server-side
     *            encryption using customer-provided keys.
     */
    private static void populateSSE_C(Request<?> request, SSECustomerKey sseKey) {
        if (sseKey == null) return;

        addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM,
                sseKey.getAlgorithm());
        addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY,
                sseKey.getKey());
        addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5,
                sseKey.getMd5());
        // Calculate the MD5 hash of the encryption key and fill it in the
        // header, if the user didn't specify it in the metadata
        if (sseKey.getKey() != null
                && sseKey.getMd5() == null) {
            String encryptionKey_b64 = sseKey.getKey();
            byte[] encryptionKey = Base64.decode(encryptionKey_b64);
            request.addHeader(Headers.SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5,
                    Md5Utils.md5AsBase64(encryptionKey));
        }
    }

    private static void populateSourceSSE_C(Request<?> request, SSECustomerKey sseKey) {
        if (sseKey == null) return;

        // Populate the SSE-C parameters for the source object
        addHeaderIfNotNull(request, Headers.COPY_SOURCE_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM,
                sseKey.getAlgorithm());
        addHeaderIfNotNull(request, Headers.COPY_SOURCE_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY,
                sseKey.getKey());
        addHeaderIfNotNull(request, Headers.COPY_SOURCE_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5,
                sseKey.getMd5());
        // Calculate the MD5 hash of the encryption key and fill it in the
        // header, if the user didn't specify it in the metadata
        if (sseKey.getKey() != null
                && sseKey.getMd5() == null) {
            String encryptionKey_b64 = sseKey.getKey();
            byte[] encryptionKey = Base64.decode(encryptionKey_b64);
            request.addHeader(Headers.COPY_SOURCE_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5,
                    Md5Utils.md5AsBase64(encryptionKey));
        }
    }

    private static void populateSSE_KMS(Request<?> request,
            SSEAwsKeyManagementParams sseParams) {

        if (sseParams != null) {
            addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION,
                    sseParams.getEncryption());
            addHeaderIfNotNull(request,
                    Headers.SERVER_SIDE_ENCRYPTION_AWS_KMS_KEYID,
                    sseParams.getAwsKmsKeyId());
            //IBM unsupported
            //addHeaderIfNotNull(request, Headers.SERVER_SIDE_ENCRYPTION_AWS_KMS_CONTEXT, sseParams.getAwsKmsEncryptionContext());
        }
    }

    /**
     * Adds the part number to the specified request, if partNumber is not null.
     *
     * @param request
     *            The request to add the partNumber to.
     * @param partNumber
     *               The part number to be added.
     */
    private void addPartNumberIfNotNull(Request<?> request, Integer partNumber) {
        if (partNumber != null) {
            request.addParameter("partNumber", partNumber.toString());
        }
    }

    /**
     * Adds the specified header to the specified request, if the header value
     * is not null.
     *
     * @param request
     *            The request to add the header to.
     * @param header
     *            The header name.
     * @param value
     *            The header value.
     */
    private static void addHeaderIfNotNull(Request<?> request, String header, String value) {
        if (value != null) {
            request.addHeader(header, value);
        }
    }

    /**
     * Adds the specified header to the specified request, if the header value
     * is not null and is not a trimmed empty string.
     *
     * @param request
     *            The request to add the header to.
     * @param header
     *            The header name.
     * @param value
     *            The header value.
     */
    private static void addHeaderIfNotEmpty(Request<?> request, String header, String value) {
        if (StringUtils.hasValue(value)) {
            request.addHeader(header, value);
        }
    }

    /**
     * Adds the specified header to the specified request, if the header value
     * is not null and is not a trimmed empty string.
     *
     * @param request
     *            The request to add the header to.
     * @param header
     *            The header name.
     * @param value
     *            The header value.
     */
    private static void addHeaderIfNotEmptyForAwsRequest(AmazonWebServiceRequest request, String header, String value) {
        if (StringUtils.hasValue(value)) {
            request.putCustomRequestHeader(header, value);
        }
    }

    private static void addIntegerHeaderIfNotNull(Request<?> request, String header, Integer value) {
        if (value != null) {
            request.addHeader(header, Integer.toString(value));
        }
    }

    /**
     * Adds the specified parameter to the specified request, if the parameter
     * value is not null.
     *
     * @param request
     *            The request to add the parameter to.
     * @param paramName
     *            The parameter name.
     * @param paramValue
     *            The parameter value.
     */
    private static void addParameterIfNotNull(Request<?> request, String paramName, Integer paramValue) {
        if (paramValue != null) {
            addParameterIfNotNull(request, paramName, paramValue.toString());
        }
    }

    /**
     * Adds the specified parameter to the specified request, if the parameter
     * value is not null.
     *
     * @param request
     *            The request to add the parameter to.
     * @param paramName
     *            The parameter name.
     * @param paramValue
     *            The parameter value.
     */
    private static void addParameterIfNotNull(Request<?> request, String paramName, String paramValue) {
        if (paramValue != null) {
            request.addParameter(paramName, paramValue);
        }
    }

    /**
     * <p>
     * Adds the specified date header in RFC 822 date format to the specified
     * request.
     * This method will not add a date header if the specified date value is <code>null</code>.
     * </p>
     *
     * @param request
     *            The request to add the header to.
     * @param header
     *            The header name.
     * @param value
     *            The header value.
     */
    private static void addDateHeader(Request<?> request, String header, Date value) {
        if (value != null) {
            request.addHeader(header, ServiceUtils.formatRfc822Date(value));
        }
    }

    /**
     * <p>
     * Adds the specified string list header, joined together separated with
     * commas, to the specified request.
     * This method will not add a string list header if the specified values
     * are <code>null</code> or empty.
     * </p>
     *
     * @param request
     *            The request to add the header to.
     * @param header
     *            The header name.
     * @param values
     *            The list of strings to join together for the header value.
     */
    private static void addStringListHeader(Request<?> request, String header, List<String> values) {
        if (values != null && !values.isEmpty()) {
            request.addHeader(header, ServiceUtils.join(values));
        }
    }

    /**
     * <p>
     * Adds response headers parameters to the request given, if non-null.
     * </p>
     *
     * @param request
     *            The request to add the response header parameters to.
     * @param responseHeaders
     *            The full set of response headers to add, or null for none.
     */
    private static void addResponseHeaderParameters(Request<?> request, ResponseHeaderOverrides responseHeaders) {
        if ( responseHeaders != null ) {
            if ( responseHeaders.getCacheControl() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL, responseHeaders.getCacheControl());
            }
            if ( responseHeaders.getContentDisposition() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION,
                        responseHeaders.getContentDisposition());
            }
            if ( responseHeaders.getContentEncoding() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING,
                        responseHeaders.getContentEncoding());
            }
            if ( responseHeaders.getContentLanguage() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE,
                        responseHeaders.getContentLanguage());
            }
            if ( responseHeaders.getContentType() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE, responseHeaders.getContentType());
            }
            if ( responseHeaders.getExpires() != null ) {
                request.addParameter(ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES, responseHeaders.getExpires());
            }
        }
    }

    /**
     * Returns the URL to the key in the bucket given, using the client's scheme
     * and endpoint. Returns null if the given bucket and key cannot be
     * converted to a URL.
     */
    public String getResourceUrl(String bucketName, String key) {
        try {
            return getUrl(bucketName, key).toString();
        } catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public URL getUrl(String bucketName, String key) {
        //IBM does not support ARN
        // if (isArn(bucketName)) {
        //     throw new IllegalArgumentException("ARNs are not supported for getUrl in this SDK version. Please use S3Utilities "
        //                                        + "in the AWS SDK for Java 2.x.");
        // }
        Request<?> request = new DefaultRequest<Object>(Constants.S3_SERVICE_DISPLAY_NAME);
        resolveRequestEndpoint(request, bucketName, key, endpoint, null);
        return ServiceUtils.convertRequestToUrl(request, false, false);
    }

    @Override
    public synchronized Region getRegion() {
        String authority = super.endpoint.getAuthority();
        if (Constants.S3_HOSTNAME.equals(authority)) {
            return Region.US_Standard;
        }

        Matcher m = Region.S3_REGIONAL_ENDPOINT_PATTERN.matcher(authority);
        if (m.matches()) {
            return Region.fromValue(m.group(1));
        }

        String signerRegion = getSignerRegion();
        if (signerRegion != null) {
            return Region.fromValue(signerRegion);
        }

        throw new IllegalStateException("Unable to determine region from configured S3 endpoint (" + authority +
                                        ") or signing region.");
    }

    @Override
    public String getRegionName() {
        String authority = super.endpoint.getAuthority();
        if(Constants.S3_HOSTNAME.equals(authority)) {
            return "us-east-1";
        }
        return getRegionNameFromAuthorityOrSigner();
    }

    private String getRegionNameFromAuthorityOrSigner() {
        String authority = super.endpoint.getAuthority();
        Matcher m = Region.S3_REGIONAL_ENDPOINT_PATTERN.matcher(authority);
        if (m.matches()) {
            try {
                return RegionUtils.getRegion(m.group(1)).getName();
            } catch (Exception e) {
                throw new IllegalStateException("No valid region has been specified. Unable to return region name.", e);
            }
        }

        String signerRegion = getSignerRegion();
        if (signerRegion != null) {
            return signerRegion;
        }

        throw new IllegalStateException("Unable to determine region from configured S3 endpoint (" + authority +
                                        ") or signing region.");
    }
    //IBM unsupported
    // private static boolean isRegionFipsEnabled(String regionName) {
    //     return regionName.startsWith("fips-") || regionName.endsWith("-fips");
    // }

    /**
     * Creates and initializes a new request object for the specified S3
     * resource. This method is responsible for determining the right way to
     * address resources. For example, bucket names that are not DNS addressable
     * cannot be addressed in V2, virtual host, style, and instead must use V1,
     * path style. The returned request object has the service name, endpoint
     * and resource path correctly populated. Callers can take the request, add
     * any additional headers or parameters, then sign and execute the request.
     *
     * @param bucketName
     *            An optional parameter indicating the name of the bucket
     *            containing the resource involved in the request.
     * @param key
     *            An optional parameter indicating the key under which the
     *            desired resource is stored in the specified bucket.
     * @param originalRequest
     *            The original request, as created by the user.
     * @param httpMethod
     *            The HTTP method to use when sending the request.
     *
     * @return A new request object, populated with endpoint, resource path, and
     *         service name, ready for callers to populate any additional
     *         headers or parameters, and execute.
     */
    protected <X extends AmazonWebServiceRequest> Request<X> createRequest(String bucketName, String key, X originalRequest, HttpMethodName httpMethod) {
        return createRequest(bucketName, key, originalRequest, httpMethod, endpoint);
    }

    // NOTE: New uses of this method are discouraged and flagged at build time.
    // Be careful not to change its signature.
    protected <X extends AmazonWebServiceRequest> Request<X> createRequest(String bucketName, String key, X originalRequest, HttpMethodName httpMethod, URI endpoint) {
        //IBM unsupported
        //String signingRegion;

        Request<X> request = new DefaultRequest<X>(originalRequest, Constants.S3_SERVICE_DISPLAY_NAME);
        request.setHttpMethod(httpMethod);
        request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED,
                                  clientOptions.isChunkedEncodingDisabled());
        request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED,
                                  clientOptions.isPayloadSigningEnabled());
        request.addHandlerContext(HandlerContextKey.SERVICE_ID, SERVICE_ID);

        //IBM unsupported
        // if (originalRequest instanceof ExpectedBucketOwnerRequest) {
        //     ExpectedBucketOwnerRequest expectedBucketOwnerRequest = (ExpectedBucketOwnerRequest) originalRequest;
        //     addHeaderIfNotNull(request, "x-amz-expected-bucket-owner",
        //                        expectedBucketOwnerRequest.getExpectedBucketOwner());
        // }

        // if (originalRequest instanceof ExpectedSourceBucketOwnerRequest) {
        //     ExpectedSourceBucketOwnerRequest expectedSourceBucketOwnerRequest = (ExpectedSourceBucketOwnerRequest) originalRequest;
        //     addHeaderIfNotNull(request, "x-amz-source-expected-bucket-owner",
        //                        expectedSourceBucketOwnerRequest.getExpectedSourceBucketOwner());
        // }

        //IBM unsupported
        // If the bucketName appears to be an ARN, parse the ARN as an S3 resource and rewrite target resource arguments
        // based on the parsed resource.
        //   if (isAccessPointArn(bucketName)) {
        //     Arn resourceArn = Arn.fromString(bucketName);
        //     S3Resource s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
        //     validateConfiguration(s3Resource);
        //     com.ibm.cloud.objectstorage.regions.Region region = RegionUtils.getRegion(getRegionNameFromAuthorityOrSigner());

        //     validateS3ResourceArn(resourceArn, region);
        //     validateParentResourceIfNeeded((S3AccessPointResource) s3Resource, getRegionName());

        //     endpoint = getEndpointForAccessPoint((S3AccessPointResource) s3Resource, region.getDomain());
        //     signingRegion = s3Resource.getRegion();

        //     request.addHandlerContext(HandlerContextKey.SIGNING_REGION, signingRegion);
        //     resolveAccessPointEndpoint(request, null /* bucketName */, key, endpoint);

        //     if (isOutpostAccessPointArn(bucketName)) {
        //         request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OUTPOSTS_NAME);
        //     } else if (isObjectLambdasArn(bucketName)) {
        //         request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OBJECT_LAMBDAS_NAME);
        //     }
        //     return request;
        // } else if (isObjectLambdasRequest(originalRequest)) {
        //     validateConfigurationForObjectLambdaOperation();

        //     com.ibm.cloud.objectstorage.regions.Region region = RegionUtils.getRegion(getRegionName());
        //     endpoint = getEndpointForObjectLambdas(region.getDomain(), region.getName());

        //     resolveRequestEndpoint(request, null, null, endpoint);

        //     if (originalRequest instanceof WriteGetObjectResponseRequest
        //         && !clientConfiguration.isDisableHostPrefixInjection()) {

        //         WriteGetObjectResponseRequest writeGetObjectResponseRequest = (WriteGetObjectResponseRequest) originalRequest;

        //         rejectNull(writeGetObjectResponseRequest.getRequestRoute(), "requestRoute must not be null");

        //         String requestRoute = writeGetObjectResponseRequest.getRequestRoute() + ".";
        //         URI newEndpoint = UriResourcePathUtils.updateUriHost(request.getEndpoint(), requestRoute);

        //         resolveEndpointIdentity(request, null, null, newEndpoint);
        //     }

        //     request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OBJECT_LAMBDAS_NAME);

        //     return request;
        // } else {
        //     signingRegion = getSigningRegion();
        // }

        // If the underlying AmazonS3Client has enabled accelerate mode and the original
        // request operation is accelerate mode supported, then the request will use the
        // s3-accelerate endpoint to performe the operations.
        if (clientOptions.isAccelerateModeEnabled() && !(originalRequest instanceof S3AccelerateUnsupported)) {
            if (clientOptions.isDualstackEnabled()) {
                endpoint = RuntimeHttpUtils.toUri(Constants.S3_ACCELERATE_DUALSTACK_HOSTNAME, clientConfiguration);
            } else {
                endpoint = RuntimeHttpUtils.toUri(Constants.S3_ACCELERATE_HOSTNAME, clientConfiguration);
            }
        }

        resolveRequestEndpoint(request, bucketName, key, endpoint, null);

        //IBM unsupported
        //request.addHandlerContext(HandlerContextKey.SIGNING_REGION, signingRegion);

        return request;
    }
    // IBM unsupported
    // private void validateParentResourceIfNeeded(S3AccessPointResource s3Resource, String regionName) {
    //     if (s3Resource.getParentS3Resource() == null) {
    //         return;
    //     }

    //     String type = s3Resource.getParentS3Resource().getType();
    //     if (S3ResourceType.fromValue(type) == S3ResourceType.OUTPOST) {
    //         if (clientOptions.isDualstackEnabled()) {
    //             throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 "
    //                                                              + "operation if the S3 client has been configured with dualstack", type));
    //         }

    //         if (isRegionFipsEnabled(regionName)) {
    //             throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3"
    //                                                              + " operation if the S3 client has been configured with a FIPS"
    //                                                              + " enabled region.", type));
    //         }
    //     }
    // }

    //IBM unsupported
    // private URI getEndpointForAccessPoint(S3AccessPointResource s3Resource,
    //                                       String domain) {
    //     URI endpointOverride = isEndpointOverridden() ? getEndpoint() : null;

    //     S3Resource parentS3Resource = s3Resource.getParentS3Resource();
    //     String protocol = clientConfiguration.getProtocol().toString();

    //     if (parentS3Resource instanceof S3OutpostResource) {
    //         S3OutpostResource outpostResource = (S3OutpostResource) parentS3Resource;
    //         return S3OutpostAccessPointBuilder.create()
    //                                           .withEndpointOverride(endpointOverride)
    //                                           .withAccountId(s3Resource.getAccountId())
    //                                           .withOutpostId(outpostResource.getOutpostId())
    //                                           .withRegion(s3Resource.getRegion())
    //                                           .withAccessPointName(s3Resource.getAccessPointName())
    //                                           .withProtocol(protocol)
    //                                           .withDomain(domain)
    //                                           .toURI();
    //     }

    //     com.ibm.cloud.objectstorage.regions.Region clientRegion = RegionUtils.getRegion(getRegionName());
    //     boolean fipsRegionProvided = isRegionFipsEnabled(clientRegion.getName());

    //     if (parentS3Resource != null && S3ResourceType.OBJECT_LAMBDAS.toString().equals(parentS3Resource.getType())) {
    //         return S3ObjectLambdaEndpointBuilder.create()
    //                 .withEndpointOverride(endpointOverride)
    //                 .withAccessPointName(s3Resource.getAccessPointName())
    //                 .withAccountId(s3Resource.getAccountId())
    //                 .withRegion(s3Resource.getRegion())
    //                 .withProtocol(protocol)
    //                 .withDomain(domain)
    //                 .withFipsEnabled(fipsRegionProvided)
    //                 .withDualstackEnabled(clientOptions.isDualstackEnabled())
    //                 .toURI();
    //     }

    //     return S3AccessPointBuilder.create()
    //                                .withEndpointOverride(endpointOverride)
    //                                .withAccessPointName(s3Resource.getAccessPointName())
    //                                .withAccountId(s3Resource.getAccountId())
    //                                .withRegion(s3Resource.getRegion())
    //                                .withProtocol(protocol)
    //                                .withDomain(domain)
    //                                .withDualstackEnabled(clientOptions.isDualstackEnabled())
    //                                .withFipsEnabled(fipsRegionProvided)
    //                                .toURI();
    // }

    // private URI getEndpointForObjectLambdas(String domain,
    //                                         String trimmedRegion) {
    //     if (isEndpointOverridden()) {
    //         return getEndpoint();
    //     }

    //     String protocol = null;
    //     if (clientConfiguration.getProtocol() != null) {
    //         protocol = clientConfiguration.getProtocol().toString();
    //     }

    //     return S3ObjectLambdaOperationEndpointBuilder.create()
    //             .withProtocol(protocol)
    //             .withDomain(domain)
    //             .withRegion(trimmedRegion)
    //             .toURI();
    // }

    // private void validateConfiguration(S3Resource s3Resource) {
    //     String type = s3Resource.getType();

    //     if (!(S3ResourceType.fromValue(type) == S3ResourceType.ACCESS_POINT)) {
    //         throw new IllegalArgumentException("An unsupported ARN was passed as a bucket parameter to an S3 operation");
    //     }

    //     if (clientOptions.isAccelerateModeEnabled()) {
    //         throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 "
    //                                            + "operation if the S3 client has been configured with accelerate mode"
    //                                            + " enabled.", type));
    //     }

    //     if (clientOptions.isPathStyleAccess()) {
    //         throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 "
    //                                            + "operation if the S3 client has been configured with path style "
    //                                            + "addressing enabled.", type));
    //     }
    // }

    // private void validateConfigurationForObjectLambdaOperation() {
    //     if (clientOptions.isDualstackEnabled()) {
    //         throw new IllegalArgumentException("S3 Object Lambda does not support dualstack endpoints");
    //     }

    //     if (clientOptions.isAccelerateModeEnabled()) {
    //         throw new IllegalArgumentException("S3 Object Lambda does not support accelerate endpoints");
    //     }
    // }

    // private void validateS3ResourceArn(Arn resourceArn, com.ibm.cloud.objectstorage.regions.Region clientRegion) {
    //     String clientPartition = (clientRegion == null) ? null : clientRegion.getPartition();

    //     if (isMultiRegionAccessPointArn(resourceArn.toString())) {
    //         throw new IllegalArgumentException("AWS SDK for Java version 1.x does not support passing a multi-region access point "
    //                                           + "Amazon Resource Names (ARNs) as a bucket parameter to an S3 operation. "
    //                                           + "If this functionality is required by your application, please upgrade to "
    //                                           + "AWS SDK for Java version 2.x");
    //     }

    //     if (clientPartition == null || !clientPartition.equals(resourceArn.getPartition())) {
    //         throw new IllegalArgumentException("The partition field of the ARN being passed as a bucket parameter to "
    //                 + "an S3 operation does not match the partition the S3 client has been configured with. Provided "
    //                                           + "partition: '" + resourceArn.getPartition() + "'; client partition: "
    //                                           + "'" + clientPartition + "'.");
    //     }

    //     validateIsTrue(!isRegionFipsEnabled(resourceArn.getRegion()),
    //             "Invalid ARN, FIPS region is not allowed in ARN."
    //             + " Provided arn region: '" + resourceArn.getRegion() + "'." );

    //     if ((!clientOptions.isForceGlobalBucketAccessEnabled() && !useArnRegion())
    //             || isRegionFipsEnabled(clientRegion.getName())) {
    //         validateIsTrue( removeFipsIfNeeded(clientRegion.getName()).equals(resourceArn.getRegion()),
    //                 "The region field of the ARN being passed as a bucket parameter to an "
    //                         + "S3 operation does not match the region the client was configured "
    //                         + "with. Provided region: '" + resourceArn.getRegion() + "'; client "
    //                         + "region: '" + clientRegion.getName() + "'." );
    //     }
    // }

    //IBM unsupported
    // private String removeFipsIfNeeded(String region) {
    //     if (region.startsWith("fips-")) {
    //         return region.replace("fips-", "");
    //     }

    //     if (region.endsWith("-fips")) {
    //         return region.replace("-fips", "");
    //     }
    //     return region;
    // }

    //IBM unsupported
    // private boolean useArnRegion() {

    //     // If useArnRegion is false, it was not set to false by the customer, it was simply not enabled
    //     if (clientOptions.isUseArnRegion()) {
    //         return clientOptions.isUseArnRegion();
    //     }

    //     return USE_ARN_REGION_RESOLVER.useArnRegion();
    // }

    // /**
    //  * Short circuit endpoint logic when working with access points as the endpoint
    //  * is constructed in advance. This resolver will take the existing endpoint and append
    //  * the correct path.
    //  */
    //IBM unsupported
    // private void resolveAccessPointEndpoint(Request<?> request, String bucketName, String key, URI endpoint) {
    //     resolveEndpointIdentity(request, bucketName, key, endpoint);
    // }

    // private void resolveEndpointIdentity(Request<?> request, String bucketName, String key, URI endpoint) {
    //     ServiceEndpointBuilder builder = new IdentityEndpointBuilder(endpoint);
    //     buildEndpointResolver(builder, bucketName, key).resolveRequestEndpoint(request);
    // }

    /**
     * Configure the given request with an endpoint and resource path based on the bucket name and
     * key provided
     */
    private void resolveRequestEndpoint(Request<?> request, String bucketName, String key, URI endpoint, String regionStr) {
        ServiceEndpointBuilder builder = getBuilder(endpoint, regionStr, endpoint.getScheme(), false);
        buildEndpointResolver(builder, bucketName, key).resolveRequestEndpoint(request);
    }

    private S3RequestEndpointResolver buildDefaultEndpointResolver(String protocol, String bucketName, String key) {
        ServiceEndpointBuilder builder = getBuilder(endpoint, null, protocol, true);
        return new S3RequestEndpointResolver(builder, clientOptions.isPathStyleAccess(), bucketName, key);
    }

    private ServiceEndpointBuilder getBuilder(URI endpoint, String regionStr, String protocol, boolean useDefaultBuilder) {
        if(clientOptions.isDualstackEnabled() && !clientOptions.isAccelerateModeEnabled()) {
            com.ibm.cloud.objectstorage.regions.Region awsRegion;
            if (regionStr != null) {
                awsRegion = RegionUtils.getRegion(regionStr);
            } else {
                awsRegion = getRegion().toAWSRegion();
            }
            return new DualstackEndpointBuilder(getServiceNameIntern(), protocol, awsRegion);
        } else {
            if(useDefaultBuilder) {
                return new DefaultServiceEndpointBuilder(getServiceName(), protocol);
            } else {
                return new IdentityEndpointBuilder(endpoint);
            }
        }
    }

    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest presignedUrlDownloadRequest) throws SdkClientException {
        assertNotNull(presignedUrlDownloadRequest.getPresignedUrl(), "Presigned URL");
        final ProgressListener listener = presignedUrlDownloadRequest.getGeneralProgressListener();

        Request<PresignedUrlDownloadRequest> request = createRequestForPresignedUrl(presignedUrlDownloadRequest, HttpMethodName.GET,
                                                                                    presignedUrlDownloadRequest.getPresignedUrl());
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObject");
        //IBM unsupported
        //request.addHandlerContext(HandlerContextKey.HAS_STREAMING_OUTPUT, Boolean.TRUE);

        // set range header if present on request
        long[] range = presignedUrlDownloadRequest.getRange();
        if (range != null) {
            request.addHeader(Headers.RANGE, "bytes=" + Long.toString(range[0]) + "-" + Long.toString(range[1]));
        }

        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);

            S3Object s3Object = client.execute(request,
                                               new S3ObjectResponseHandler(),
                                               errorResponseHandler,
                                               createExecutionContext(AmazonWebServiceRequest.NOOP, new NoOpSignerProvider()),
                                               requestConfigWithSkipAppendUriPath(request))
                                      .getAwsResponse();

            boolean skipClientSideValidation = skipMd5CheckStrategy.skipClientSideValidation(presignedUrlDownloadRequest,
                                                                                             s3Object.getObjectMetadata());
            postProcessS3Object(s3Object, skipClientSideValidation, listener);

            return new PresignedUrlDownloadResult().withS3Object(s3Object);
        } catch (AmazonS3Exception ase) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw ase;
        }
    }

    public void download(final PresignedUrlDownloadRequest presignedUrlDownloadRequest, final File destinationFile)
        throws SdkClientException {
        assertNotNull(destinationFile, "Destination file");

        ServiceUtils.retryableDownloadS3ObjectToFile(destinationFile, new ServiceUtils.RetryableS3DownloadTask() {

            @Override
            public S3Object getS3ObjectStream() {
                return download(presignedUrlDownloadRequest).getS3Object();
            }

            @Override
            public boolean needIntegrityCheck() {
                return !skipMd5CheckStrategy.skipClientSideValidationPerRequest(presignedUrlDownloadRequest);
            }

        }, ServiceUtils.OVERWRITE_MODE);
    }

    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest presignedUrlUploadRequest) {
        presignedUrlUploadRequest = beforeClientExecution(presignedUrlUploadRequest);
        rejectNull(presignedUrlUploadRequest, "The PresignedUrlUploadRequest object cannot be null");
        rejectNull(presignedUrlUploadRequest.getPresignedUrl(), "Presigned URL");

        final File file = presignedUrlUploadRequest.getFile();
        final InputStream isOrig = presignedUrlUploadRequest.getInputStream();
        final ProgressListener listener = presignedUrlUploadRequest.getGeneralProgressListener();

        ObjectMetadata metadata = presignedUrlUploadRequest.getMetadata();
        if (metadata == null)
            metadata = new ObjectMetadata();

        Request<PresignedUrlUploadRequest> request = createRequestForPresignedUrl(presignedUrlUploadRequest,
                                                                                  presignedUrlUploadRequest.getHttpMethodName(),
                                                                                  presignedUrlUploadRequest.getPresignedUrl());
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObject");

        // Make backward compatible with buffer size via system property
        final Integer bufsize = Constants.getS3StreamBufferSize();
        if (bufsize != null) {
            AmazonWebServiceRequest awsreq = request.getOriginalRequest();
            // Note awsreq is never null at this point even if the original
            // request was
            awsreq.getRequestClientOptions()
                  .setReadLimit(bufsize.intValue());
        }

        return uploadObject(isOrig, file, metadata, listener, request, presignedUrlUploadRequest,
                            // server side validation is skipped so that SDK doesn't send Content-MD5 header.
                            // As this is signed header, only users know if this header is signed in the presigned url or not.
                            // To enable server side validation, users should set the md5 value through ObjectMetadata
                            true,
                            skipMd5CheckStrategy.skipClientSideValidationPerRequest(presignedUrlUploadRequest),
                            new PresignedUrlUploadStrategy(presignedUrlUploadRequest.getPresignedUrl()),
                            !isSigV2PresignedUrl(presignedUrlUploadRequest.getPresignedUrl()));
    }

    /**
     * Request config used by the APIs that execute presigned urls. Without this config, SDK appends slash ("/") at the end
     * of uri while making the request which will fail for presigned urls.
     *
     * Used in {@link #download(PresignedUrlDownloadRequest)} and {@link #upload(PresignedUrlUploadRequest)} operations.
     */
    private RequestConfig requestConfigWithSkipAppendUriPath(Request request) {
        RequestConfig config = new AmazonWebServiceRequestAdapter(request.getOriginalRequest());
        config.getRequestClientOptions().setSkipAppendUriPath(true);
        return config;
    }

    /**
     * Method to create {@link Request} object. This method is intended to be used only by operations that use S3
     * presigned URL.
     *
     * This method has special behavior that is not used by normal APIs (that don't use presigned url) like:
     * 1) Setting empty Content-Type header when url is signed with SigV2 signer
     * 2) Ignoring signer, credentials when constructing the request object
     *
     */
    private <X extends AmazonWebServiceRequest> Request<X> createRequestForPresignedUrl(X originalRequest,
                                                                                        HttpMethodName httpMethod,
                                                                                        URL endpoint) {
        Request<X> request = new DefaultRequest<X>(originalRequest, Constants.S3_SERVICE_DISPLAY_NAME);
        request.setHttpMethod(httpMethod);

        try {
            request.setEndpoint(endpoint.toURI());
        } catch (URISyntaxException e) {
            throw new SdkClientException(e);
        }

        if (originalRequest.getCustomRequestHeaders() != null) {
            for (Map.Entry<String, String> entry : originalRequest.getCustomRequestHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // Content-Type is signed in SigV2 signer
        // If no Content-Type is present, SDK puts a default type in Apache http layer, which will make the call fail with
        // Signature error. So set it to empty string for sigv2 presigned urls when user don't provide a Content-Type value
        if (request.getHeaders().get(Headers.CONTENT_TYPE) == null && isSigV2PresignedUrl(endpoint)) {
            request.addHeader(Headers.CONTENT_TYPE, "");
        }

        request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED,
                                  Boolean.valueOf(clientOptions.isChunkedEncodingDisabled()));
        request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED,
                                  Boolean.valueOf(clientOptions.isPayloadSigningEnabled()));
        request.addHandlerContext(HandlerContextKey.SERVICE_ID, SERVICE_ID);

        return request;
    }

    /**
     * SigV2 presigned url has "AWSAccessKeyId" in the params. Also doing "X-Amz-Algorithm" check to ensure
     * "AWSAccessKeyId=" is not present in the bucket or key name
     */
    private boolean isSigV2PresignedUrl(URL presignedUrl) {
        String url = presignedUrl.toString();
        return url.contains("AWSAccessKeyId=") && !presignedUrl.toString().contains("X-Amz-Algorithm=AWS4-HMAC-SHA256");
    }

    private S3RequestEndpointResolver buildEndpointResolver(ServiceEndpointBuilder serviceEndpointBuilder, String bucketName, String key) {
        return new S3RequestEndpointResolver(serviceEndpointBuilder, clientOptions.isPathStyleAccess(), bucketName, key);
    }

    @Override
    protected final SignerProvider createSignerProvider(Signer signer) {
        return new S3SignerProvider(this, signer);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request,
                                                            Unmarshaller<X, InputStream> unmarshaller,
                                                            String bucketName,
                                                            String key) {
        return invoke(request, new S3XmlResponseHandler<X>(unmarshaller), bucketName, key);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request,
                                                            HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler,
                                                            String bucket, String key) {
        return invoke(request, responseHandler, bucket, key, false);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request,
                                                            HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler,
                                                            String bucket, String key, boolean isAdditionalHeadRequestToFindRegion) {


        AmazonWebServiceRequest originalRequest = request.getOriginalRequest();
        checkHttps(originalRequest);
        S3SignerProvider signerProvider = new S3SignerProvider(this, getSigner());
        ExecutionContext executionContext = createExecutionContext(originalRequest, signerProvider);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        // Binds the request metrics to the current request.
        request.setAWSRequestMetrics(awsRequestMetrics);
        // Having the ClientExecuteTime defined here is not ideal (for the
        // timing measurement should start as close to the top of the call
        // stack of the service client method as possible)
        // but definitely a safe compromise for S3 at least for now.
        // We can incrementally make it more elaborate should the need arise
        // for individual method.
        awsRequestMetrics.startEvent(Field.ClientExecuteTime);
        Response<X> response = null;
        try {
            request.setTimeOffset(timeOffset);
            /*
             * The string we sign needs to include the exact headers that we
             * send with the request, but the client runtime layer adds the
             * Content-Type header before the request is sent if one isn't set,
             * so we have to set something here otherwise the request will fail.
             */
            if (!request.getHeaders().containsKey(Headers.CONTENT_TYPE)) {
                request.addHeader(Headers.CONTENT_TYPE,
                    "application/octet-stream");
            }

            // Update the bucketRegionCache if we can't find region for the request
            if (!isAdditionalHeadRequestToFindRegion && shouldPerformHeadRequestToFindRegion(request, bucket)) {
                fetchRegionFromCache(bucket);
            }

            Signer signer = createSigner(request, bucket, key, isAdditionalHeadRequestToFindRegion);
            signerProvider.setSigner(signer);

            // Retry V4 auth errors if signer is explicitly overridden and
            // signer is not a SigV4 signer.
            if (isSignerOverridden() && !(signer instanceof AWSS3V4Signer)) {
                executionContext.setAuthErrorRetryStrategy(
                        new S3V4AuthErrorRetryStrategy(buildDefaultEndpointResolver(getProtocol(request), bucket, key)));
            }

            executionContext.setCredentialsProvider(CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), awsCredentialsProvider));
            validateRequestBeforeTransmit(request);
            response = client.execute(request, responseHandler, errorResponseHandler, executionContext);
            return response.getAwsResponse();
        } catch (ResetException ex) {
            ex.setExtraInfo("If the request involves an input stream, the maximum stream buffer size can be configured via request.getRequestClientOptions().setReadLimit(int)");
            throw ex;
        } catch (AmazonS3Exception ase) {
            /**
             * This is to handle the edge case: when the bucket is deleted and recreated in a different region,
             * the cache still has the old region info.
             * If region is not specified, the first request to this newly created bucket will fail because it used
             * the outdated region present in cache. Here we update the cache with correct region. The subsequent
             * requests will succeed.
             * The recommended practice for any request is to provide region info always.
             */
            if (ase.getStatusCode() == 301) {
                if (ase.getAdditionalDetails() != null) {
                    String region = ase.getAdditionalDetails().get(Headers.S3_BUCKET_REGION);
                    bucketRegionCache.put(bucket, region);
                    ase.setErrorMessage("The bucket is in this region: " + region +
                                        ". Please use this region to retry the request");
                }
            }
            throw ase;
        } catch (OAuthServiceException ose) {
            /**
             * Wrap OAuthServiceException as AmazonS3Exception and re-throw for backwards compatability.
             */
            AmazonS3Exception ase = new AmazonS3Exception(ose.getErrorMessage());
            ase.setStatusCode(ose.getStatusCode());
            ase.setServiceName("IAM");
            ase.setStackTrace(ose.getStackTrace());
            throw ase;
        } finally {
            endClientExecution(awsRequestMetrics, request, response);
        }
    }

    private void validateRequestBeforeTransmit(Request<?> request) {
        boolean implicitCrossRegionForbidden = areImplicitGlobalClientsDisabled();
        boolean explicitCrossRegionEnabled = clientOptions.isForceGlobalBucketAccessEnabled();

        // The region must be set if implicit cross region clients are not allowed
        if (noExplicitRegionProvided(request) && implicitCrossRegionForbidden && !explicitCrossRegionEnabled) {
            String error = String.format("While the %s system property is enabled, Amazon S3 clients cannot be used without " +
                                         "first configuring a region or explicitly enabling global bucket access discovery " +
                                         "in the S3 client builder.",
                                         SDKGlobalConfiguration.DISABLE_S3_IMPLICIT_GLOBAL_CLIENTS_SYSTEM_PROPERTY);
            throw new IllegalStateException(error);
        }
    }

    /**
     * Returns true in the event that the {@link SDKGlobalConfiguration#DISABLE_S3_IMPLICIT_GLOBAL_CLIENTS_SYSTEM_PROPERTY}
     * is non-null and not "false".
     *
     * If this system property is set, S3 clients may not act in a cross-region manner unless cross-region behavior is
     * explicitly enabled, using options like {@link AmazonS3ClientBuilder#enableForceGlobalBucketAccess()}.
     */
    private boolean areImplicitGlobalClientsDisabled() {
        String setting = System.getProperty(SDKGlobalConfiguration.DISABLE_S3_IMPLICIT_GLOBAL_CLIENTS_SYSTEM_PROPERTY);
        return setting != null && !setting.equals("false");
    }

    private boolean shouldPerformHeadRequestToFindRegion(Request<?> request, String bucket) {
        return bucket != null &&
               //IBM unsupported
               //!isAccessPointArn(bucket) &&
               !(request.getOriginalRequest() instanceof CreateBucketRequest) &&
               bucketRegionShouldBeCached(request);
    }

    //IBM unsupported
    // private boolean isAccessPointArn(String s) {
    //     return s != null
    //             && s.startsWith("arn:")
    //             && (isS3AccessPointArn(s) || isOutpostAccessPointArn(s) || isObjectLambdasArn(s));
    // }

    // private boolean isS3AccessPointArn(String s) {
    //     return s.contains(":accesspoint");
    // }

    // private boolean isOutpostAccessPointArn(String s) {
    //     return s.contains(":s3-outposts");
    // }

    // private boolean isObjectLambdasArn(String s) {
    //     return s.contains(":s3-object-lambda");
    // }

    // private boolean isObjectLambdasRequest(AmazonWebServiceRequest request) {
    //     return request instanceof WriteGetObjectResponseRequest;
    // }

    // private boolean isMultiRegionAccessPointArn(String s) {
    //     return s.contains(":global");
    // }

    //IBM unsupported
    // private boolean isArn(String s) {
    //     return s != null && s.startsWith("arn:");
    // }

    private boolean bucketRegionShouldBeCached(Request<?> request) {
        return clientOptions.isForceGlobalBucketAccessEnabled() || noExplicitRegionProvided(request);
    }
    //IBM unsupported
    // @Override
    // public void enableRequesterPays(String bucketName) {
    //     RequestPaymentConfiguration configuration = new RequestPaymentConfiguration(
    //             Payer.Requester);

    //     setRequestPaymentConfiguration(new SetRequestPaymentConfigurationRequest(
    //             bucketName, configuration));
    // }

    // @Override
    // public void disableRequesterPays(String bucketName) {
    //     RequestPaymentConfiguration configuration = new RequestPaymentConfiguration(
    //             Payer.BucketOwner);

    //     setRequestPaymentConfiguration(new SetRequestPaymentConfigurationRequest(
    //             bucketName, configuration));
    // }

    // @Override
    // public boolean isRequesterPaysEnabled(String bucketName) {
    //     RequestPaymentConfiguration configuration = getBucketRequestPayment(new GetRequestPaymentConfigurationRequest(
    //             bucketName));
    //     return (configuration.getPayer() == Payer.Requester);
    // }

    /**
     * Sets the request payment configuration for a given Amazon S3 bucket.
     * This operation can be done only by the owner of the Amazon S3 bucket.
     * <p>
     * When the request payment configuration for a Amazon S3 bucket is set to
     * <code>Requester</code>, the requester instead of the bucket owner pays
     * the cost of the request and the data download from the bucket. The bucket
     * owner always pays the cost of storing data.
     */
    //IBM unsupported
    // @Override
    // public void setRequestPaymentConfiguration(
    //         SetRequestPaymentConfigurationRequest setRequestPaymentConfigurationRequest) {

    //     String bucketName = setRequestPaymentConfigurationRequest
    //             .getBucketName();
    //     RequestPaymentConfiguration configuration = setRequestPaymentConfigurationRequest
    //             .getConfiguration();

    //     rejectNull(bucketName,
    //             "The bucket name parameter must be specified while setting the Requester Pays.");

    //     rejectNull(
    //             configuration,
    //             "The request payment configuration parameter must be specified when setting the Requester Pays.");

    //     Request<SetRequestPaymentConfigurationRequest> request = createRequest(
    //             bucketName, null, setRequestPaymentConfigurationRequest,
    //             HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketRequestPayment");
    //     request.addParameter("requestPayment", null);
    //     request.addHeader("Content-Type", "application/xml");

    //     byte[] bytes = requestPaymentConfigurationXmlFactory
    //             .convertToXmlByteArray(configuration);
    //     request.setContent(new ByteArrayInputStream(bytes));
    //     populateRequestHeaderWithMd5(request, bytes);

    //     invoke(request, voidResponseHandler, bucketName, null);
    // }

    /**
     * Retrieves the request payment configuration for a given Amazon S3 bucket.
     * <p>
     * When the request payment configuration for a Amazon S3 bucket is
     * <code>Requester</code>, the requester instead of the bucket owner pays
     * the cost of the request and the data download from the bucket. The bucket
     * owner always pays the cost of storing data.
     */
    // IBM unsupported
    // private RequestPaymentConfiguration getBucketRequestPayment(
    //         GetRequestPaymentConfigurationRequest getRequestPaymentConfigurationRequest) {

    //     String bucketName = getRequestPaymentConfigurationRequest
    //             .getBucketName();

    //     rejectNull(
    //             bucketName,
    //             "The bucket name parameter must be specified while getting the Request Payment Configuration.");

    //     Request<GetRequestPaymentConfigurationRequest> request = createRequest(
    //             bucketName, null, getRequestPaymentConfigurationRequest,
    //             HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketRequestPayment");
    //     request.addParameter("requestPayment", null);
    //     request.addHeader("Content-Type", "application/xml");

    //     return invoke(request,
    //             new Unmarshallers.RequestPaymentConfigurationUnmarshaller(),
    //             bucketName, null);
    // }

    private void setZeroContentLength(Request<?> req) {
        // https://github.com/aws/aws-sdk-java/pull/215
        // http://aws.amazon.com/articles/1109#14
        req.addHeader(Headers.CONTENT_LENGTH, String.valueOf(0));
    }

    /**
     * Throws {@link IllegalArgumentException} if SSE customer key is in use
     * without https.
     */
    private void checkHttps(AmazonWebServiceRequest req) {
        if (req instanceof SSECustomerKeyProvider) {
            SSECustomerKeyProvider p = (SSECustomerKeyProvider) req;
            if (p.getSSECustomerKey() != null)
                assertHttps();
        } else if (req instanceof CopyObjectRequest) {
            CopyObjectRequest cor = (CopyObjectRequest) req;
            if (cor.getSourceSSECustomerKey() != null
            ||  cor.getDestinationSSECustomerKey() != null) {
                assertHttps();
            }
        } else if (req instanceof CopyPartRequest) {
            CopyPartRequest cpr = (CopyPartRequest) req;
            if (cpr.getSourceSSECustomerKey() != null
            ||  cpr.getDestinationSSECustomerKey() != null) {
                assertHttps();
            }
        }

        if (req instanceof SSEAwsKeyManagementParamsProvider) {
            SSEAwsKeyManagementParamsProvider p = (SSEAwsKeyManagementParamsProvider) req;
            if (p.getSSEAwsKeyManagementParams() != null)
                assertHttps();
        }
    }

    private void assertHttps() {
        URI endpoint = this.endpoint;
        String scheme = endpoint == null ? null : endpoint.getScheme();
        if (!Protocol.HTTPS.toString().equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException(
                    "HTTPS must be used when sending customer encryption keys (SSE-C) to S3, in order to protect your encryption keys.");
        }
    }

    /**
     * For testing
     */
    synchronized URI getEndpoint() {
        return endpoint;
    }

    private static String getProtocol(Request<?> request) {
        if (request == null || request.getEndpoint() == null) {
            return null;
        }
        return request.getEndpoint().getScheme();
    }

    /**
     * Creates and returns a multi-part upload initiation request from the given upload-object
     * request.
     */
    protected final InitiateMultipartUploadRequest newInitiateMultipartUploadRequest(
            UploadObjectRequest req) {
        return new InitiateMultipartUploadRequest(
                req.getBucketName(), req.getKey(), req.getMetadata())
            .withRedirectLocation(req.getRedirectLocation())
            .withSSEAwsKeyManagementParams(req.getSSEAwsKeyManagementParams())
            .withSSECustomerKey(req.getSSECustomerKey())
            .withStorageClass(req.getStorageClass())
            .withAccessControlList(req.getAccessControlList())
            .withCannedACL(req.getCannedAcl())
            .withGeneralProgressListener(req.getGeneralProgressListener())
            .withRequestMetricCollector(req.getRequestMetricCollector())
            ;
    }

    /**
     * Used for performance testing purposes only.
     */
    private void putLocalObject(final UploadObjectRequest reqIn,
            OutputStream os) throws IOException {
        UploadObjectRequest req = reqIn.clone();

        final File fileOrig = req.getFile();
        final InputStream isOrig = req.getInputStream();

        if (isOrig == null) {
            if (fileOrig == null)
                throw new IllegalArgumentException("Either a file lor input stream must be specified");
            req.setInputStream(new FileInputStream(fileOrig));
            req.setFile(null);
        }

        try {
            IOUtils.copy(req.getInputStream(), os);
        } finally {
            cleanupDataSource(req, fileOrig, isOrig,
                    req.getInputStream(), log);
            IOUtils.closeQuietly(os, log);
        }
        return;
    }

    /**
     * Used for performance testing purposes only.  Hence package private.
     * This method is subject to removal anytime without notice.
     */
    CompleteMultipartUploadResult uploadObject(final UploadObjectRequest req)
            throws IOException, InterruptedException, ExecutionException {
        // Set up the pipeline for concurrent encrypt and upload
        // Set up a thread pool for this pipeline
        ExecutorService es = req.getExecutorService();
        final boolean defaultExecutorService = es == null;
        if (es == null)
            es = Executors.newFixedThreadPool(clientConfiguration.getMaxConnections());
        UploadObjectObserver observer = req.getUploadObjectObserver();
        if (observer == null)
            observer = new UploadObjectObserver();
        // initialize the observer
        observer.init(req, this, this, es);
        // Initiate upload
        observer.onUploadInitiation(req);
        final List<PartETag> partETags = new ArrayList<PartETag>();
        MultiFileOutputStream mfos = req.getMultiFileOutputStream();
        if (mfos == null)
            mfos = new MultiFileOutputStream();
        try {
            // initialize the multi-file output stream
            mfos.init(observer, req.getPartSize(), req.getDiskLimit());
            // Kicks off the encryption-upload pipeline;
            // Note mfos is automatically closed upon method completion.
            putLocalObject(req, mfos);
            // block till all part have been uploaded
            for (Future<UploadPartResult> future: observer.getFutures()) {
                UploadPartResult partResult = future.get();
                partETags.add(new PartETag(partResult.getPartNumber(), partResult.getETag()));
            }
        } finally {
            if (defaultExecutorService)
                es.shutdownNow();   // shut down the locally created thread pool
            mfos.cleanup();       // delete left-over temp files
        }
        // Complete upload
        return observer.onCompletion(partETags);
    }

    @Override
    public void setBucketReplicationConfiguration(String bucketName,
            BucketReplicationConfiguration configuration)
            throws AmazonServiceException, SdkClientException {
        setBucketReplicationConfiguration(new SetBucketReplicationConfigurationRequest(
                bucketName, configuration));
    }

    @Override
    public void setBucketReplicationConfiguration(
            SetBucketReplicationConfigurationRequest setBucketReplicationConfigurationRequest)
            throws AmazonServiceException, SdkClientException {
        setBucketReplicationConfigurationRequest = beforeClientExecution(setBucketReplicationConfigurationRequest);
        rejectNull(setBucketReplicationConfigurationRequest,
                "The set bucket replication configuration request object must be specified.");

        final String bucketName = setBucketReplicationConfigurationRequest
                .getBucketName();

        final BucketReplicationConfiguration bucketReplicationConfiguration = setBucketReplicationConfigurationRequest
                .getReplicationConfiguration();

        rejectNull(
                bucketName,
                "The bucket name parameter must be specified when setting replication configuration.");
        rejectNull(
                bucketReplicationConfiguration,
                "The replication configuration parameter must be specified when setting replication configuration.");

        Request<SetBucketReplicationConfigurationRequest> request = createRequest(
                bucketName, null, setBucketReplicationConfigurationRequest,
                HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketReplication");
        request.addParameter("replication", null);

        final byte[] bytes = bucketConfigurationXmlFactory
                .convertToXmlByteArray(bucketReplicationConfiguration);

        addHeaderIfNotNull(request, Headers.OBJECT_LOCK_TOKEN, setBucketReplicationConfigurationRequest.getToken());
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));

        populateRequestHeaderWithMd5(request, bytes);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(
            String bucketName) throws AmazonServiceException,
            SdkClientException {
      return getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(bucketName));
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(
                GetBucketReplicationConfigurationRequest getBucketReplicationConfigurationRequest)
                    throws AmazonServiceException, SdkClientException {
        getBucketReplicationConfigurationRequest = beforeClientExecution(getBucketReplicationConfigurationRequest);
        rejectNull(
                getBucketReplicationConfigurationRequest,
                "The bucket request parameter must be specified when retrieving replication configuration");
        String bucketName = getBucketReplicationConfigurationRequest.getBucketName();
        rejectNull(
                bucketName,
                "The bucket request must specify a bucket name when retrieving replication configuration");

        Request<GetBucketReplicationConfigurationRequest> request = createRequest(bucketName, null,
                          getBucketReplicationConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketReplication");
        request.addParameter("replication", null);

        return invoke(request,
                new Unmarshallers.BucketReplicationConfigurationUnmarshaller(),
                bucketName, null);
    }

    @Override
    public void deleteBucketReplicationConfiguration(String bucketName)
            throws AmazonServiceException, SdkClientException {
        deleteBucketReplicationConfiguration(new
                DeleteBucketReplicationConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketReplicationConfiguration
            (DeleteBucketReplicationConfigurationRequest
                     deleteBucketReplicationConfigurationRequest)
            throws AmazonServiceException, SdkClientException {
        deleteBucketReplicationConfigurationRequest = beforeClientExecution(deleteBucketReplicationConfigurationRequest);
        final String bucketName = deleteBucketReplicationConfigurationRequest.getBucketName();
        rejectNull(
                bucketName,
                "The bucket name parameter must be specified when deleting replication configuration");

        Request<DeleteBucketReplicationConfigurationRequest> request = createRequest(bucketName, null,
                deleteBucketReplicationConfigurationRequest, HttpMethodName
                        .DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketReplication");
        request.addParameter("replication", null);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    // @Override
    // public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return deleteBucketMetricsConfiguration(new DeleteBucketMetricsConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(
    //         DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     deleteBucketMetricsConfigurationRequest = beforeClientExecution(deleteBucketMetricsConfigurationRequest);
    //     rejectNull(deleteBucketMetricsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(deleteBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(deleteBucketMetricsConfigurationRequest.getId(), "Metrics Id");

    //     Request<DeleteBucketMetricsConfigurationRequest> request =
    //             createRequest(bucketName, null, deleteBucketMetricsConfigurationRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketMetricsConfiguration");
    //     request.addParameter("metrics", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.DeleteBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return getBucketMetricsConfiguration(new GetBucketMetricsConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(
    //         GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     getBucketMetricsConfigurationRequest = beforeClientExecution(getBucketMetricsConfigurationRequest);
    //     rejectNull(getBucketMetricsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(getBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(getBucketMetricsConfigurationRequest.getId(), "Metrics Id");

    //     Request<GetBucketMetricsConfigurationRequest> request =
    //             createRequest(bucketName, null, getBucketMetricsConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketMetricsConfiguration");
    //     request.addParameter("metrics", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.GetBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    // }

    // public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(
    //         String bucketName, MetricsConfiguration metricsConfiguration)
    //         throws AmazonServiceException, SdkClientException {
    //     return setBucketMetricsConfiguration(new SetBucketMetricsConfigurationRequest(bucketName, metricsConfiguration));
    // }

    // @Override
    // public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(
    //         SetBucketMetricsConfigurationRequest setBucketMetricsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketMetricsConfigurationRequest = beforeClientExecution(setBucketMetricsConfigurationRequest);
    //     new SetBucketMetricsConfigurationRequest();
    //     rejectNull(setBucketMetricsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(setBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
    //     final MetricsConfiguration metricsConfiguration = assertNotNull(
    //             setBucketMetricsConfigurationRequest.getMetricsConfiguration(), "Metrics Configuration");
    //     final String id = assertNotNull(metricsConfiguration.getId(), "Metrics Id");

    //     Request<SetBucketMetricsConfigurationRequest> request =
    //             createRequest(bucketName, null, setBucketMetricsConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketMetricsConfiguration");
    //     request.addParameter("metrics", null);
    //     request.addParameter("id", id);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(metricsConfiguration);
    //     request.addHeader("Content-Length", String.valueOf(bytes.length));
    //     request.addHeader("Content-Type", "application/xml");
    //     request.setContent(new ByteArrayInputStream(bytes));

    //     return invoke(request, new Unmarshallers.SetBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public ListBucketMetricsConfigurationsResult listBucketMetricsConfigurations(
    //         ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     listBucketMetricsConfigurationsRequest = beforeClientExecution(listBucketMetricsConfigurationsRequest);
    //     rejectNull(listBucketMetricsConfigurationsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(listBucketMetricsConfigurationsRequest.getBucketName(), "BucketName");

    //     Request<ListBucketMetricsConfigurationsRequest> request =
    //             createRequest(bucketName, null, listBucketMetricsConfigurationsRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketMetricsConfigurations");
    //     request.addParameter("metrics", null);
    //     addParameterIfNotNull(request, "continuation-token", listBucketMetricsConfigurationsRequest.getContinuationToken());

    //     return invoke(request, new Unmarshallers.ListBucketMetricsConfigurationsUnmarshaller(), bucketName, null);
    // }

    //IBM unsupported
    // @Override
    // public DeleteBucketOwnershipControlsResult deleteBucketOwnershipControls(
    //     DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest)
    //     throws AmazonServiceException, SdkClientException {
    //     deleteBucketOwnershipControlsRequest = beforeClientExecution(deleteBucketOwnershipControlsRequest);
    //     rejectNull(deleteBucketOwnershipControlsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(deleteBucketOwnershipControlsRequest.getBucketName(), "BucketName");

    //     Request<DeleteBucketOwnershipControlsRequest> request =
    //         createRequest(bucketName, null, deleteBucketOwnershipControlsRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketOwnershipControls");
    //     request.addParameter("ownershipControls", null);

    //     return invoke(request, new Unmarshallers.DeleteBucketOwnershipControlsUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketOwnershipControlsResult getBucketOwnershipControls(
    //     GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest)
    //     throws AmazonServiceException, SdkClientException {
    //     getBucketOwnershipControlsRequest = beforeClientExecution(getBucketOwnershipControlsRequest);
    //     rejectNull(getBucketOwnershipControlsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(getBucketOwnershipControlsRequest.getBucketName(), "BucketName");

    //     Request<GetBucketOwnershipControlsRequest> request =
    //         createRequest(bucketName, null, getBucketOwnershipControlsRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketOwnershipControls");
    //     request.addParameter("ownershipControls", null);

    //     return invoke(request, new Unmarshallers.GetBucketOwnershipControlsUnmarshaller(), bucketName, null);
    // }

    // public SetBucketOwnershipControlsResult setBucketOwnershipControls(
    //     String bucketName, OwnershipControls ownershipControls)
    //     throws AmazonServiceException, SdkClientException {
    //     return setBucketOwnershipControls(new SetBucketOwnershipControlsRequest(bucketName, ownershipControls));
    // }

    // @Override
    // public SetBucketOwnershipControlsResult setBucketOwnershipControls(
    //     SetBucketOwnershipControlsRequest setBucketOwnershipControlsRequest)
    //     throws AmazonServiceException, SdkClientException {
    //     setBucketOwnershipControlsRequest = beforeClientExecution(setBucketOwnershipControlsRequest);
    //     rejectNull(setBucketOwnershipControlsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(setBucketOwnershipControlsRequest.getBucketName(), "BucketName");
    //     final OwnershipControls ownershipControls = assertNotNull(
    //         setBucketOwnershipControlsRequest.getOwnershipControls(), "OwnershipControls");

    //     Request<SetBucketOwnershipControlsRequest> request =
    //         createRequest(bucketName, null, setBucketOwnershipControlsRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketOwnershipControls");
    //     request.addParameter("ownershipControls", null);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(ownershipControls);
    //     setContent(request, bytes, "application/xml", true);

    //     return invoke(request, new Unmarshallers.SetBucketOwnershipControlsUnmarshaller(), bucketName, null);
    // }

    //@Override
    // public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return deleteBucketAnalyticsConfiguration(new DeleteBucketAnalyticsConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(
    //         DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     deleteBucketAnalyticsConfigurationRequest = beforeClientExecution(deleteBucketAnalyticsConfigurationRequest);
    //     rejectNull(deleteBucketAnalyticsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             deleteBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(
    //             deleteBucketAnalyticsConfigurationRequest.getId(), "Analytics Id");

    //     Request<DeleteBucketAnalyticsConfigurationRequest> request =
    //             createRequest(bucketName, null, deleteBucketAnalyticsConfigurationRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketAnalyticsConfiguration");
    //     request.addParameter("analytics", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.DeleteBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return getBucketAnalyticsConfiguration(new GetBucketAnalyticsConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(
    //         GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     getBucketAnalyticsConfigurationRequest = beforeClientExecution(getBucketAnalyticsConfigurationRequest);

    //     rejectNull(getBucketAnalyticsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             getBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(
    //             getBucketAnalyticsConfigurationRequest.getId(), "Analytics Id");

    //     Request<GetBucketAnalyticsConfigurationRequest> request =
    //             createRequest(bucketName, null, getBucketAnalyticsConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAnalyticsConfiguration");
    //     request.addParameter("analytics", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.GetBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(
    //         String bucketName, AnalyticsConfiguration analyticsConfiguration)
    //         throws AmazonServiceException, SdkClientException {
    //     return setBucketAnalyticsConfiguration(
    //             new SetBucketAnalyticsConfigurationRequest(bucketName, analyticsConfiguration));
    // }

    // @Override
    // public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(
    //         SetBucketAnalyticsConfigurationRequest setBucketAnalyticsConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketAnalyticsConfigurationRequest = beforeClientExecution(setBucketAnalyticsConfigurationRequest);
    //     rejectNull(setBucketAnalyticsConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             setBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
    //     final AnalyticsConfiguration analyticsConfiguration = assertNotNull(
    //             setBucketAnalyticsConfigurationRequest.getAnalyticsConfiguration(), "Analytics Configuration");
    //     final String id = assertNotNull(analyticsConfiguration.getId(), "Analytics Id");

    //     Request<SetBucketAnalyticsConfigurationRequest> request =
    //             createRequest(bucketName, null, setBucketAnalyticsConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAnalyticsConfiguration");
    //     request.addParameter("analytics", null);
    //     request.addParameter("id", id);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(analyticsConfiguration);
    //     request.addHeader("Content-Length", String.valueOf(bytes.length));
    //     request.addHeader("Content-Type", "application/xml");
    //     request.setContent(new ByteArrayInputStream(bytes));

    //     return invoke(request, new Unmarshallers.SetBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public ListBucketAnalyticsConfigurationsResult listBucketAnalyticsConfigurations(
    //         ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     listBucketAnalyticsConfigurationsRequest = beforeClientExecution(listBucketAnalyticsConfigurationsRequest);
    //     rejectNull(listBucketAnalyticsConfigurationsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             listBucketAnalyticsConfigurationsRequest.getBucketName(), "BucketName");

    //     Request<ListBucketAnalyticsConfigurationsRequest> request =
    //             createRequest(bucketName, null, listBucketAnalyticsConfigurationsRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketAnalyticsConfigurations");
    //     request.addParameter("analytics", null);
    //     addParameterIfNotNull(request, "continuation-token", listBucketAnalyticsConfigurationsRequest.getContinuationToken());

    //     return invoke(request, new Unmarshallers.ListBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    // }

    //IBM unsupported
    // @Override
    // public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return deleteBucketIntelligentTieringConfiguration(new DeleteBucketIntelligentTieringConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(
    //         DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     deleteBucketIntelligentTieringConfigurationRequest = beforeClientExecution(deleteBucketIntelligentTieringConfigurationRequest);
    //     rejectNull(deleteBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             deleteBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(
    //             deleteBucketIntelligentTieringConfigurationRequest.getId(), "IntelligentTiering Id");

    //     Request<DeleteBucketIntelligentTieringConfigurationRequest> request =
    //             createRequest(bucketName, null, deleteBucketIntelligentTieringConfigurationRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketIntelligentTieringConfiguration");
    //     request.addParameter("intelligent-tiering", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.DeleteBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return getBucketIntelligentTieringConfiguration(new GetBucketIntelligentTieringConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(
    //         GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     getBucketIntelligentTieringConfigurationRequest = beforeClientExecution(getBucketIntelligentTieringConfigurationRequest);

    //     rejectNull(getBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             getBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
    //     final String id = assertStringNotEmpty(
    //             getBucketIntelligentTieringConfigurationRequest.getId(), "IntelligentTiering Id");

    //     Request<GetBucketIntelligentTieringConfigurationRequest> request =
    //             createRequest(bucketName, null, getBucketIntelligentTieringConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketIntelligentTieringConfiguration");
    //     request.addParameter("intelligent-tiering", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.GetBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(
    //         String bucketName, IntelligentTieringConfiguration intelligentTieringConfiguration)
    //         throws AmazonServiceException, SdkClientException {
    //     return setBucketIntelligentTieringConfiguration(
    //             new SetBucketIntelligentTieringConfigurationRequest(bucketName, intelligentTieringConfiguration));
    // }

    // @Override
    // public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(
    //         SetBucketIntelligentTieringConfigurationRequest setBucketIntelligentTieringConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketIntelligentTieringConfigurationRequest = beforeClientExecution(setBucketIntelligentTieringConfigurationRequest);
    //     rejectNull(setBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             setBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
    //     final IntelligentTieringConfiguration intelligentTieringConfiguration = assertNotNull(
    //             setBucketIntelligentTieringConfigurationRequest.getIntelligentTierinConfiguration(),
    //             "Intelligent Tiering Configuration");
    //     final String id = assertNotNull(intelligentTieringConfiguration.getId(), "Intelligent Tiering Id");

    //     Request<SetBucketIntelligentTieringConfigurationRequest> request =
    //             createRequest(bucketName, null, setBucketIntelligentTieringConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketIntelligentTieringConfiguration");
    //     request.addParameter("intelligent-tiering", null);
    //     request.addParameter("id", id);

    //     byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(intelligentTieringConfiguration);
    //     request.addHeader("Content-Length", String.valueOf(bytes.length));
    //     request.addHeader("Content-Type", "application/xml");
    //     request.setContent(new ByteArrayInputStream(bytes));

    //     return invoke(request, new Unmarshallers.SetBucketIntelligentTieringConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public ListBucketIntelligentTieringConfigurationsResult listBucketIntelligentTieringConfigurations(
    //         ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     listBucketIntelligentTieringConfigurationsRequest = beforeClientExecution(listBucketIntelligentTieringConfigurationsRequest);
    //     rejectNull(listBucketIntelligentTieringConfigurationsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(
    //             listBucketIntelligentTieringConfigurationsRequest.getBucketName(), "BucketName");

    //     Request<ListBucketIntelligentTieringConfigurationsRequest> request =
    //             createRequest(bucketName, null, listBucketIntelligentTieringConfigurationsRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketIntelligentTieringConfigurations");
    //     request.addParameter("intelligent-tiering", null);
    //     addParameterIfNotNull(request, "continuation-token", listBucketIntelligentTieringConfigurationsRequest.getContinuationToken());

    //     return invoke(request, new Unmarshallers.ListBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    // }

    //IBM unsupported
    //@Override
    // public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return deleteBucketInventoryConfiguration(
    //             new DeleteBucketInventoryConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(
    //         DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     deleteBucketInventoryConfigurationRequest = beforeClientExecution(deleteBucketInventoryConfigurationRequest);
    //     rejectNull(deleteBucketInventoryConfigurationRequest, "The request cannot be null");
    //     String bucketName = assertStringNotEmpty(deleteBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
    //     String id = assertStringNotEmpty(deleteBucketInventoryConfigurationRequest.getId(), "Inventory id");

    //     Request<DeleteBucketInventoryConfigurationRequest> request = createRequest(bucketName, null, deleteBucketInventoryConfigurationRequest, HttpMethodName.DELETE);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketInventoryConfiguration");
    //     request.addParameter("inventory", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.DeleteBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(
    //         String bucketName, String id) throws AmazonServiceException, SdkClientException {
    //     return getBucketInventoryConfiguration(
    //             new GetBucketInventoryConfigurationRequest(bucketName, id));
    // }

    // @Override
    // public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(
    //         GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     getBucketInventoryConfigurationRequest = beforeClientExecution(getBucketInventoryConfigurationRequest);
    //     rejectNull(getBucketInventoryConfigurationRequest, "The request cannot be null");
    //     String bucketName = assertStringNotEmpty(getBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
    //     String id = assertStringNotEmpty(getBucketInventoryConfigurationRequest.getId(), "Inventory id");

    //     Request<GetBucketInventoryConfigurationRequest> request = createRequest(bucketName, null, getBucketInventoryConfigurationRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketInventoryConfiguration");
    //     request.addParameter("inventory", null);
    //     request.addParameter("id", id);

    //     return invoke(request, new Unmarshallers.GetBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(
    //         String bucketName, InventoryConfiguration inventoryConfiguration)
    //         throws AmazonServiceException, SdkClientException {
    //     return setBucketInventoryConfiguration(
    //             new SetBucketInventoryConfigurationRequest(bucketName, inventoryConfiguration));
    // }

    // @Override
    // public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(
    //         SetBucketInventoryConfigurationRequest setBucketInventoryConfigurationRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     setBucketInventoryConfigurationRequest = beforeClientExecution(setBucketInventoryConfigurationRequest);
    //     rejectNull(setBucketInventoryConfigurationRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(setBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
    //     final InventoryConfiguration inventoryConfiguration = assertNotNull(setBucketInventoryConfigurationRequest.getInventoryConfiguration(),
    //             "InventoryConfiguration");
    //     final String id = assertNotNull(inventoryConfiguration.getId(), "Inventory id");

    //     Request<SetBucketInventoryConfigurationRequest> request = createRequest(bucketName, null, setBucketInventoryConfigurationRequest, HttpMethodName.PUT);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketInventoryConfiguration");
    //     request.addParameter("inventory", null);
    //     request.addParameter("id", id);

    //     final byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(inventoryConfiguration);
    //     request.addHeader("Content-Length", String.valueOf(bytes.length));
    //     request.addHeader("Content-Type", "application/xml");
    //     request.setContent(new ByteArrayInputStream(bytes));

    //     return invoke(request, new Unmarshallers.SetBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    // }

    // @Override
    // public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest)
    //         throws AmazonServiceException, SdkClientException {
    //     listBucketInventoryConfigurationsRequest = beforeClientExecution(listBucketInventoryConfigurationsRequest);
    //     rejectNull(listBucketInventoryConfigurationsRequest, "The request cannot be null");
    //     final String bucketName = assertStringNotEmpty(listBucketInventoryConfigurationsRequest.getBucketName(), "BucketName");

    //     Request<ListBucketInventoryConfigurationsRequest> request = createRequest(bucketName, null, listBucketInventoryConfigurationsRequest, HttpMethodName.GET);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketInventoryConfigurations");
    //     request.addParameter("inventory", null);
    //     addParameterIfNotNull(request, "continuation-token", listBucketInventoryConfigurationsRequest.getContinuationToken());

    //     return invoke(request, new Unmarshallers.ListBucketInventoryConfigurationsUnmarshaller(), bucketName, null);
    // }

    /**
     * Specifically made package access for testing.
     * Used for internal consumption of Amazon Web Services SDK.
     *
     * Tries to determine the service endpoint for the bucket name.
     * Returns the endpoint configured in the client if the region cannot be determined.
     */
    URI resolveServiceEndpointFromBucketName(String bucketName) {

        if (getSignerRegion() != null || isSignerOverridden()) return endpoint;

        final String regionStr = fetchRegionFromCache(bucketName);
        return resolveServiceEndpointFromRegion(regionStr);
    }

    private URI resolveServiceEndpointFromRegion(String regionName) {
        final com.ibm.cloud.objectstorage.regions.Region region = RegionUtils.getRegion(regionName);

        if (region == null) {
            log.warn("Region information for "
                    + regionName
                    + " is not available. Please upgrade to latest version of AWS Java SDK");
        }

        return region != null
                ? RuntimeHttpUtils.toUri(region.getServiceEndpoint(S3_SERVICE_NAME), clientConfiguration)
                : endpoint;
    }

    /**
     * Fetches the region of the bucket from the cache maintained. If the cache
     * doesn't have an entry, fetches the region from Amazon S3 and updates the
     * cache.
     */
    private String fetchRegionFromCache(String bucketName) {
        String bucketRegion = bucketRegionCache.get(bucketName);
        if (bucketRegion == null) {
            if (log.isDebugEnabled()) {
                log.debug("Bucket region cache doesn't have an entry for " + bucketName
                        + ". Trying to get bucket region from Amazon S3.");
            }

            bucketRegion = getBucketRegionViaHeadRequest(bucketName);

            if (bucketRegion != null) {
                bucketRegionCache.put(bucketName, bucketRegion);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Region for " + bucketName + " is " + bucketRegion);
        }
        return bucketRegion;
    }

    /**
     * Retrieves the region of the bucket by making a HeadBucket request to us-west-1 region.
     *
     * Currently S3 doesn't return region in a HEAD Bucket request if the bucket
     * owner has enabled bucket to accept only SigV4 requests via bucket
     * policies.
     */
    private String getBucketRegionViaHeadRequest(String bucketName) {
        String bucketRegion = null;

        try {
            Request<HeadBucketRequest> request = createRequest(bucketName, null,
                    new HeadBucketRequest(bucketName), HttpMethodName.HEAD);
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadBucket");

            HeadBucketResult result = invoke(request, new HeadBucketResultHandler(), bucketName, null, true);
            bucketRegion = result.getBucketRegion();
        } catch (AmazonS3Exception exception) {
            if (exception.getAdditionalDetails() != null) {
                bucketRegion = exception.getAdditionalDetails().get(
                    Headers.S3_BUCKET_REGION);
            }
        }

        if (bucketRegion == null && log.isDebugEnabled()) {
            log.debug("Not able to derive region of the " + bucketName + " from the HEAD Bucket requests.");
        }

        return bucketRegion;
    }

    public AmazonS3Waiters waiters(){
        if (waiters == null) {
            synchronized (this) {
                if (waiters == null) {
                    waiters = new AmazonS3Waiters(this);
                }
            }
        }
        return waiters;
    }

    private String urlEncodeTags(ObjectTagging tagging) {
        if (tagging == null || tagging.getTagSet() == null) return null;

        StringBuilder sb = new StringBuilder();

        Iterator<Tag> tagIter = tagging.getTagSet().iterator();
        while (tagIter.hasNext()) {
            Tag tag = tagIter.next();
            sb.append(SdkHttpUtils.urlEncode(tag.getKey(), false)).append('=').append(SdkHttpUtils.urlEncode(tag.getValue(), false));
            if (tagIter.hasNext()) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

    private void setContent(Request<?> request, byte[] content, String contentType, boolean setMd5) {
        request.setContent(new ByteArrayInputStream(content));
        request.addHeader("Content-Length", Integer.toString(content.length));
        request.addHeader("Content-Type", contentType);
        if (setMd5) {
            try {
                byte[] md5 = Md5Utils.computeMD5Hash(content);
                String md5Base64 = BinaryUtils.toBase64(md5);
                request.addHeader("Content-MD5", md5Base64);
            } catch ( Exception e ) {
                throw new AmazonClientException("Couldn't compute md5 sum", e);
            }
        }
    }
    //IBM unsupported
    // private Request<RestoreObjectRequest> createRestoreObjectRequest(RestoreObjectRequest restoreObjectRequest) {
    //     String bucketName = restoreObjectRequest.getBucketName();
    //     String key = restoreObjectRequest.getKey();
    //     String versionId = restoreObjectRequest.getVersionId();

    //     Request<RestoreObjectRequest> request = createRequest(
    //         bucketName, key, restoreObjectRequest, HttpMethodName.POST);
    //     request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "RestoreObject");
    //     request.addParameter("restore", null);
    //     if (versionId != null) {
    //         request.addParameter("versionId", versionId);
    //     }

    //     populateRequesterPaysHeader(request, restoreObjectRequest.isRequesterPays());
    //     byte[] content = RequestXmlFactory.convertToXmlByteArray(restoreObjectRequest);
    //     setContent(request, content, "application/xml", true);
    //     return request;
    // }
    private static void populateObjectLockHeaders(Request<?> request, String mode, Date retainUntil, String status) {
        addHeaderIfNotNull(request, Headers.OBJECT_LOCK_MODE, mode);
        if (retainUntil != null) {
            request.addHeader(Headers.OBJECT_LOCK_RETAIN_UNTIL_DATE, ServiceUtils.formatIso8601Date(retainUntil));
        }
        addHeaderIfNotNull(request, Headers.OBJECT_LOCK_LEGAL_HOLD_STATUS, status);
    }

    /**
     * Add IAM specific headers based on the credentials set & any optional
     * parameters added to the CreateBucketRequest object
     *
     * @param request
     * @param createBucketRequest
     * @return Request<CreateBucketRequest>
     */
    protected Request<CreateBucketRequest> addIAMHeaders(Request<CreateBucketRequest> request, CreateBucketRequest createBucketRequest){

        if ((null != this.awsCredentialsProvider ) && (this.awsCredentialsProvider.getCredentials() instanceof IBMOAuthCredentials)) {
            if (null != createBucketRequest.getServiceInstanceId()) {
                request.addHeader(Headers.IBM_SERVICE_INSTANCE_ID, createBucketRequest.getServiceInstanceId());
                if (null != createBucketRequest.getEncryptionType()) {
                    request.addHeader(Headers.IBM_SSE_KP_ENCRYPTION_ALGORITHM, createBucketRequest.getEncryptionType().getKmsEncryptionAlgorithm());
                    request.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, createBucketRequest.getEncryptionType().getIBMSSEKMSCustomerRootKeyCrn());
                }
            } else {
                IBMOAuthCredentials oAuthCreds = (IBMOAuthCredentials)this.awsCredentialsProvider.getCredentials();
                if (oAuthCreds.getServiceInstanceId() != null) {
                    request.addHeader(Headers.IBM_SERVICE_INSTANCE_ID, oAuthCreds.getServiceInstanceId());
                    if (null != createBucketRequest.getEncryptionType()) {
                        request.addHeader(Headers.IBM_SSE_KP_ENCRYPTION_ALGORITHM, createBucketRequest.getEncryptionType().getKmsEncryptionAlgorithm());
                        request.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, createBucketRequest.getEncryptionType().getIBMSSEKMSCustomerRootKeyCrn());
                    }
                }
            }
        }

        return request;
    }

    @Override
    public void setBucketProtectionConfiguration(SetBucketProtectionConfigurationRequest setBucketProtectionConfigurationRequest) throws SdkClientException, AmazonServiceException {
        setBucketProtectionConfigurationRequest = beforeClientExecution(setBucketProtectionConfigurationRequest);
        rejectNull(setBucketProtectionConfigurationRequest, "The set bucket protection configuration request object must be specified.");

        String bucketName = setBucketProtectionConfigurationRequest.getBucketName();
        BucketProtectionConfiguration bucketProtectionConfiguration = setBucketProtectionConfigurationRequest.getProtectionConfiguration();

        rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket protection configuration.");
        rejectNull(bucketProtectionConfiguration, "The protection configuration parameter must be specified when setting bucket protection configuration.");

        Request<SetBucketProtectionConfigurationRequest> request = createRequest(bucketName, null, setBucketProtectionConfigurationRequest, HttpMethodName.PUT);
        request.addParameter("protection", null);

        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketProtectionConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", md5Base64);
        } catch ( Exception e ) {
            throw new SdkClientException("Couldn't compute md5 sum", e);
        }

        invoke(request, voidResponseHandler, bucketName, null);
    }

    @Override
    public void setBucketProtection(String bucketName, BucketProtectionConfiguration protectionConfiguration) throws SdkClientException, AmazonServiceException {
        setBucketProtectionConfiguration(new SetBucketProtectionConfigurationRequest()
            .withBucketName(bucketName)
            .withProtectionConfiguration(protectionConfiguration));
    }

    @Override
    public BucketProtectionConfiguration getBucketProtection(String bucketName) throws SdkClientException, AmazonServiceException {
        return getBucketProtectionConfiguration(new GetBucketProtectionConfigurationRequest(bucketName));
    }

    @Override
    public BucketProtectionConfiguration getBucketProtectionConfiguration(GetBucketProtectionConfigurationRequest getBucketProtectionRequest)
            throws SdkClientException, AmazonServiceException {
        getBucketProtectionRequest = beforeClientExecution(getBucketProtectionRequest);
        rejectNull(getBucketProtectionRequest, "The request object parameter GetBucketProtectionConfigurationRequest must be specified.");
        String bucketName = getBucketProtectionRequest.getBucketName();
        rejectNull(bucketName, "The bucket name must be specified when retrieving the bucket protection configuration.");

        Request<GetBucketProtectionConfigurationRequest> request = createRequest(bucketName, null, getBucketProtectionRequest, HttpMethodName.GET);
        request.addParameter("protection", null);

        try {
            return invoke(request, new Unmarshallers.BucketProtectionConfigurationUnmarshaller(), bucketName, null);
        } catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
            case 404:
                return null;
            default:
                throw ase;
            }
        }
    }

    @Override
    public void addLegalHold(String bucketName, String key, String legalHoldId) throws SdkClientException, AmazonServiceException {
        addLegalHold(new AddLegalHoldRequest(bucketName, key, legalHoldId));
    }

    @Override
    public void addLegalHold(AddLegalHoldRequest addLegalHoldRequest) throws SdkClientException, AmazonServiceException {
        addLegalHoldRequest = beforeClientExecution(addLegalHoldRequest);
        rejectNull(addLegalHoldRequest, "The add legal hold configuration request object must be specified.");

        String bucketName = addLegalHoldRequest.getBucketName();
        String key = addLegalHoldRequest.getKey();
        String legalHoldId = addLegalHoldRequest.getLegalHoldId();

        rejectNull(bucketName,  "The bucket name must be specified when adding a legal hold id.");
        rejectNull(key, "The bucket name must be specified when adding a legal hold id.");
        rejectNull(legalHoldId, "The legal hold id must be specified when adding a legal hold id.");

        Request<AddLegalHoldRequest> request = createRequest(bucketName, key, addLegalHoldRequest, HttpMethodName.POST);
        request.addParameter("legalHold", null);
        request.addParameter("add", legalHoldId);

        byte[] content = addLegalHoldRequest.getLegalHoldId().getBytes();
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", md5Base64);
        } catch ( Exception e ) {
            throw new SdkClientException("Couldn't compute md5 sum", e);
        }

        invoke(request, voidResponseHandler, bucketName, key);
    }

    @Override
    public ListLegalHoldsResult listLegalHolds(String bucketName, String key) throws SdkClientException, AmazonServiceException {
        return listLegalHolds(new ListLegalHoldsRequest(bucketName, key));
    }

    @Override
    public ListLegalHoldsResult listLegalHolds(ListLegalHoldsRequest listLegalHoldsRequest)
            throws SdkClientException, AmazonServiceException {
        listLegalHoldsRequest = beforeClientExecution(listLegalHoldsRequest);
        rejectNull(listLegalHoldsRequest, "The request object parameter ListLegalHoldsRequest must be specified.");
        String bucketName = listLegalHoldsRequest.getBucketName();
        rejectNull(bucketName, "The bucket name must be specified when retrieving the list of legal holds.");
        String key = listLegalHoldsRequest.getKey();
        rejectNull(key, "The object key must be specified when retrieving the list of legal holds.");

        Request<ListLegalHoldsRequest> request = createRequest(bucketName, key, listLegalHoldsRequest, HttpMethodName.GET);
        request.addParameter("legalHold", null);

        // IBM-specific
        addHeaderIfNotEmpty(request, Headers.MIRROR_DESTINATION, listLegalHoldsRequest.getWormMirrorDestination());

        try {
            return invoke(request, new Unmarshallers.ListLegalHoldsRequestUnmarshaller(), bucketName, key);
        } catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
            case 404:
                return null;
            default:
                throw ase;
            }
        }
    }

    @Override
    public void deleteLegalHold(String bucketName, String key, String legalHoldId) throws SdkClientException, AmazonServiceException {
        deleteLegalHold(new DeleteLegalHoldRequest(bucketName, key, legalHoldId));
    }

    @Override
    public void deleteLegalHold(DeleteLegalHoldRequest deleteLegalHoldRequest) throws SdkClientException, AmazonServiceException {
        deleteLegalHoldRequest = beforeClientExecution(deleteLegalHoldRequest);
        rejectNull(deleteLegalHoldRequest, "The delete legal hold configuration request object must be specified.");

        String bucketName = deleteLegalHoldRequest.getBucketName();
        String key = deleteLegalHoldRequest.getKey();
        String legalHoldId = deleteLegalHoldRequest.getLegalHoldId();

        rejectNull(bucketName, "The bucket name must be specified when deleting a legal hold id.");
        rejectNull(key, "The bucket name must be specified when deleting a legal hold id.");
        rejectNull(legalHoldId, "The legal hold id must be specified when deleting a legal hold id.");

        Request<DeleteLegalHoldRequest> request = createRequest(bucketName, key, deleteLegalHoldRequest, HttpMethodName.POST);
        request.addParameter("legalHold", null);
        request.addParameter("remove", legalHoldId);

        byte[] content = "".getBytes();
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "text/plain");
        request.setContent(new ByteArrayInputStream(content));

        invoke(request, voidResponseHandler, bucketName, key);
    }

    @Override
    public void extendObjectRetention(String bucketName, String key, Long additionalRetentionPeriod,
            Long extendRetentionFromCurrentTime, Date newRetentionExpirationDate, Long newRetentionPeriod)
            throws SdkClientException, AmazonServiceException {
        extendObjectRetention(new ExtendObjectRetentionRequest()
        .withBucketName(bucketName)
        .withKey(key)
        .withAdditionalRetentionPeriod(additionalRetentionPeriod)
        .withExtendRetentionFromCurrentTime(extendRetentionFromCurrentTime)
        .withNewRetentionExpirationDate(newRetentionExpirationDate)
        .withNewRetentionPeriod(newRetentionPeriod));
    }

    @Override
    public void extendObjectRetention(ExtendObjectRetentionRequest extendObjectRetentionRequest)
            throws SdkClientException, AmazonServiceException {
        extendObjectRetentionRequest = beforeClientExecution(extendObjectRetentionRequest);
        rejectNull(extendObjectRetentionRequest, "The extend object retention request object must be specified.");

        String bucketName = extendObjectRetentionRequest.getBucketName();
        String key = extendObjectRetentionRequest.getKey();
        Long additionalRetentionPeriod = extendObjectRetentionRequest.getAdditionalRetentionPeriod();
        Long extendRetentionFromCurrentTime = extendObjectRetentionRequest.getExtendRetentionFromCurrentTime();
        Date newRetentionExpirationDate = extendObjectRetentionRequest.getNewRetentionExpirationDate();
        Long newRetentionPeriod = extendObjectRetentionRequest.getNewRetentionPeriod();

        rejectNull(bucketName, "The bucket name must be specified when adding a legal hold id.");
        rejectNull(key, "The bucket name must be specified when adding a legal hold id.");

        // Only one of the extend retention headers can be populated
        String argumentExceptionMessage = "Only one of additionalRetentionPeriod, extendRetentionFromCurrentTime, newRetentionExpirationDate"
                + " or newRetentionPeriod can be specified.";
        if (additionalRetentionPeriod != null &&
                (extendRetentionFromCurrentTime != null || newRetentionExpirationDate != null
                || newRetentionPeriod != null)) {
            throw new IllegalArgumentException(argumentExceptionMessage);
        }

        if(extendRetentionFromCurrentTime != null &&
                (newRetentionExpirationDate != null || newRetentionPeriod != null)) {
            throw new IllegalArgumentException(argumentExceptionMessage);
        }

        if(newRetentionExpirationDate != null && newRetentionPeriod != null) {
            throw new IllegalArgumentException(argumentExceptionMessage);
        }

        Request<ExtendObjectRetentionRequest> request = createRequest(bucketName, key, extendObjectRetentionRequest, HttpMethodName.POST);
        request.addParameter("extendRetention", null);
        if(additionalRetentionPeriod != null) {
            request.addHeader("additional-retention-period", extendObjectRetentionRequest.getAdditionalRetentionPeriod().toString());
        }
        if(extendRetentionFromCurrentTime != null) {
            request.addHeader("extend-retention-from-current-time", extendRetentionFromCurrentTime.toString());
        }
        if(newRetentionExpirationDate != null) {
            request.addHeader("new-retention-expiration-date", DateUtils.formatRFC822Date(newRetentionExpirationDate));
        }
        if(newRetentionPeriod != null) {
            request.addHeader("new-retention-period", newRetentionPeriod.toString());
        }

        byte[] content = new byte[0];
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "text/plain");
        request.setContent(new ByteArrayInputStream(content));
        invoke(request, voidResponseHandler, bucketName, key);
    }

    public FASPConnectionInfo getBucketFaspConnectionInfo(String bucketName) throws SdkClientException, AmazonServiceException {
        return getBucketFaspConnectionInfo(new GetBucketFaspConnectionInfoRequest(bucketName));
    }

    @Override
    public FASPConnectionInfo getBucketFaspConnectionInfo(GetBucketFaspConnectionInfoRequest getBucketFaspConnectionInfoRequest)
            throws SdkClientException, AmazonServiceException {
        getBucketFaspConnectionInfoRequest = beforeClientExecution(getBucketFaspConnectionInfoRequest);
        String bucketName = getBucketFaspConnectionInfoRequest.getBucketName();
        rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's FASP Connection Info");

        Request<GetBucketFaspConnectionInfoRequest> request = createRequest(bucketName, null, getBucketFaspConnectionInfoRequest, HttpMethodName.GET);
        request.addParameter("faspConnectionInfo", null);

        populateRequesterPaysHeader(request, false);

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<FASPConnectionInfo> responseHandler = new ResponseHeaderHandlerChain<FASPConnectionInfo>(
                new Unmarshallers.FASPConnectionInfoUnmarshaller(),
                new S3RequesterChargedHeaderHandler<FASPConnectionInfo>());

        return invoke(request, responseHandler, bucketName, null);
    }

    /**
     * Upload strategy to use in {@link #putObject(PutObjectRequest)} API
     */
    private class PutObjectStrategy implements UploadObjectStrategy<PutObjectRequest, PutObjectResult> {
        private final String bucketName;
        private final String key;

        private PutObjectStrategy(String bucketName, String key) {
            this.bucketName = bucketName;
            this.key = key;
        }

        @Override
        public ObjectMetadata invokeServiceCall(Request<PutObjectRequest> request) {
            return invoke(request, new S3MetadataResponseHandler(), bucketName, key);
        }

        @Override
        public PutObjectResult createResult(ObjectMetadata metadata, String contentMd5) {
            PutObjectResult result = createPutObjectResult(metadata);
            result.setContentMd5(contentMd5);
            return result;
        }

        @Override
        public String md5ValidationErrorSuffix() {
            return ", bucketName: " + bucketName + ", key: " + key;
        }
    }

    /**
     * Upload strategy to use in {@link #upload(PresignedUrlUploadRequest)} API
     */
    private class PresignedUrlUploadStrategy implements UploadObjectStrategy<PresignedUrlUploadRequest, PresignedUrlUploadResult> {
        private final URL url;

        private PresignedUrlUploadStrategy(URL url) {
            this.url = url;
        }

        @Override
        public ObjectMetadata invokeServiceCall(Request<PresignedUrlUploadRequest> request) {
            return client.execute(request,
                                  new S3MetadataResponseHandler(),
                                  errorResponseHandler,
                                  createExecutionContext(AmazonWebServiceRequest.NOOP, new NoOpSignerProvider()),
                                  requestConfigWithSkipAppendUriPath(request))
                         .getAwsResponse();
        }

        @Override
        public PresignedUrlUploadResult createResult(ObjectMetadata metadata, String contentMd5) {
            return createPresignedUrlUploadResult(metadata, contentMd5);
        }

        @Override
        public String md5ValidationErrorSuffix() {
            return ", object presigned url: " + url;
        }
    }

    private PresignedUrlUploadResult createPresignedUrlUploadResult(ObjectMetadata metadata, String contentMd5) {
        final PresignedUrlUploadResult result = new PresignedUrlUploadResult();
        result.setMetadata(metadata);
        result.setContentMd5(contentMd5);
        return result;
    }

    private void validateIsTrue(boolean condition, String error, Object... params) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(error, params));
        }
    }

}
