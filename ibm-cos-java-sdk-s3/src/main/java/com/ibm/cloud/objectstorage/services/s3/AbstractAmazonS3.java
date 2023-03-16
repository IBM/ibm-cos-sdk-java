/*
 * Copyright 2015-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.ibm.cloud.objectstorage.AmazonServiceException;
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
import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;
import com.ibm.cloud.objectstorage.HttpMethod;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.regions.Region;
import com.ibm.cloud.objectstorage.services.s3.model.*;
import com.ibm.cloud.objectstorage.services.s3.waiters.AmazonS3Waiters;

/**
 * An Abstract class that users needs to extend instead of {@link AmazonS3}
 * interface. This abstract class is provided so we don't break any customers
 * when we introduce new methods in {@link AmazonS3} interface.
 */
public abstract class AbstractAmazonS3 implements AmazonS3 {

    @Override
    public void setEndpoint(String endpoint) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void setRegion(Region region) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setS3ClientOptions(S3ClientOptions clientOptions) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void changeObjectStorageClass(String bucketName, String key,
            StorageClass newStorageClass) throws SdkClientException,
            AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setObjectRedirectLocation(String bucketName, String key,
            String newRedirectLocation) throws SdkClientException,
            AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

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
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName)
            throws SdkClientException, AmazonServiceException {
        ListObjectsV2Request request = new ListObjectsV2Request();
        request.setBucketName(bucketName);
        return listObjectsV2(request);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName, String prefix)
            throws SdkClientException, AmazonServiceException {
        ListObjectsV2Request request = new ListObjectsV2Request();
        request.setBucketName(bucketName);
        request.setPrefix(prefix);
        return listObjectsV2(request);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public ObjectListing listNextBatchOfObjects(
            ObjectListing previousObjectListing) throws SdkClientException,
            AmazonServiceException {
        return listNextBatchOfObjects(new ListNextBatchOfObjectsRequest(previousObjectListing));
    }

    @Override
    public ObjectListing listNextBatchOfObjects(
            ListNextBatchOfObjectsRequest listNextBatchOfObjectsRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix)
            throws SdkClientException, AmazonServiceException {
        return listVersions(new ListVersionsRequest(bucketName, prefix, null, null, null, null));
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix,
            String keyMarker, String versionIdMarker, String delimiter,
            Integer maxResults) throws SdkClientException,
            AmazonServiceException {

        ListVersionsRequest request = new ListVersionsRequest()
            .withBucketName(bucketName)
            .withPrefix(prefix)
            .withDelimiter(delimiter)
            .withKeyMarker(keyMarker)
            .withVersionIdMarker(versionIdMarker)
            .withMaxResults(maxResults);
        return listVersions(request);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public VersionListing listNextBatchOfVersions(
            VersionListing previousVersionListing)
            throws SdkClientException, AmazonServiceException {
        return listNextBatchOfVersions(new ListNextBatchOfVersionsRequest(previousVersionListing));
    }

    @Override
    public VersionListing listNextBatchOfVersions(
            ListNextBatchOfVersionsRequest listNextBatchOfVersionsRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public Owner getS3AccountOwner() throws SdkClientException,
            AmazonServiceException {
        return getS3AccountOwner(new GetS3AccountOwnerRequest());
    }

    @Override
    public Owner getS3AccountOwner(GetS3AccountOwnerRequest getS3AccountOwnerRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public boolean doesBucketExist(String bucketName)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public boolean doesBucketExistV2(String bucketName)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest headBucketRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public List<Bucket> listBuckets() throws SdkClientException,
            AmazonServiceException {
        return listBuckets(new ListBucketsRequest());
    }

    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public Bucket createBucket(String bucketName) throws SdkClientException,
            AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName));
    }

    @Override
    public Bucket createBucket(String bucketName,
            com.ibm.cloud.objectstorage.services.s3.model.Region region)
            throws SdkClientException, AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    public Bucket createBucket(String bucketName, String region)
            throws SdkClientException, AmazonServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, key));
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key,
            String versionId) throws SdkClientException,
            AmazonServiceException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, key, versionId));
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclRequest getObjectAclRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setObjectAcl(String bucketName, String key,
            AccessControlList acl) throws SdkClientException,
            AmazonServiceException {
        setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key,
            CannedAccessControlList acl) throws SdkClientException,
            AmazonServiceException {
        setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId,
            AccessControlList acl) throws SdkClientException,
            AmazonServiceException {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId,
            CannedAccessControlList acl) throws SdkClientException,
            AmazonServiceException {
        setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public AccessControlList getBucketAcl(String bucketName)
            throws SdkClientException, AmazonServiceException {
        return getBucketAcl(new GetBucketAclRequest(bucketName));
    }

    @Override
    public AccessControlList getBucketAcl(
            GetBucketAclRequest getBucketAclRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void setBucketAcl(String bucketName, AccessControlList acl)
            throws SdkClientException, AmazonServiceException {
        setBucketAcl(new SetBucketAclRequest(bucketName, acl));
    }

    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList cannedAcl)
            throws SdkClientException, AmazonServiceException {
        setBucketAcl(new SetBucketAclRequest(bucketName, cannedAcl));
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObjectMetadata(new GetObjectMetadataRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObjectMetadata(
            GetObjectMetadataRequest getObjectMetadataRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public S3Object getObject(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        return getObject(new GetObjectRequest(bucketName, key));
    }

    @Override
    public S3Object getObject(GetObjectRequest getObjectRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest,
            File destinationFile) throws SdkClientException,
            AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public String getObjectAsString(String bucketName, String key)
            throws AmazonServiceException, SdkClientException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest objectTaggingRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteBucket(String bucketName) throws SdkClientException,
            AmazonServiceException {
        deleteBucket(new DeleteBucketRequest(bucketName));
    }

    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file)
            throws SdkClientException, AmazonServiceException {
        return putObject(new PutObjectRequest(bucketName, key, file)
            .withMetadata(new ObjectMetadata()));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key,
            InputStream input, ObjectMetadata metadata)
            throws SdkClientException, AmazonServiceException {
        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, String content)
            throws AmazonServiceException, SdkClientException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName,
            String sourceKey, String destinationBucketName,
            String destinationKey) throws SdkClientException,
            AmazonServiceException {
        return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey,
                destinationBucketName, destinationKey));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public CopyPartResult copyPart(CopyPartRequest copyPartRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteObject(String bucketName, String key)
            throws SdkClientException, AmazonServiceException {
        deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public DeleteObjectsResult deleteObjects(
            DeleteObjectsRequest deleteObjectsRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteVersion(String bucketName, String key, String versionId)
            throws SdkClientException, AmazonServiceException {
        deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(
            String bucketName) throws SdkClientException,
            AmazonServiceException {
        return getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(bucketName));
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(
            GetBucketVersioningConfigurationRequest getBucketVersioningConfigurationRequest) throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setBucketVersioningConfiguration(
            SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(
            String bucketName) {
        return getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setBucketLifecycleConfiguration(String bucketName,
            BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        setBucketLifecycleConfiguration(new SetBucketLifecycleConfigurationRequest(bucketName, bucketLifecycleConfiguration));
    }

    @Override
    public void setBucketLifecycleConfiguration(
            SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteBucketLifecycleConfiguration(String bucketName) {
        deleteBucketLifecycleConfiguration(new DeleteBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketLifecycleConfiguration(
            DeleteBucketLifecycleConfigurationRequest deleteBucketLifecycleConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(
            String bucketName) {
        return getBucketCrossOriginConfiguration(new GetBucketCrossOriginConfigurationRequest(bucketName));
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(
            GetBucketCrossOriginConfigurationRequest getBucketCrossOriginConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void setBucketCrossOriginConfiguration(String bucketName,
            BucketCrossOriginConfiguration bucketCrossOriginConfiguration) {
        setBucketCrossOriginConfiguration(new SetBucketCrossOriginConfigurationRequest(bucketName, bucketCrossOriginConfiguration));
    }

    @Override
    public void setBucketCrossOriginConfiguration(
            SetBucketCrossOriginConfigurationRequest setBucketCrossOriginConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteBucketCrossOriginConfiguration(String bucketName) {
        deleteBucketCrossOriginConfiguration(new DeleteBucketCrossOriginConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(
            DeleteBucketCrossOriginConfigurationRequest deleteBucketCrossOriginConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    /**
     * @exclude
     */
    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(
            String bucketName) {
        return getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(bucketName));
    }

    /**
     * @exclude
     */
    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(
            GetBucketTaggingConfigurationRequest getBucketTaggingConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    /**
     * @exclude
     */
    @Override
    public void setBucketTaggingConfiguration(String bucketName,
            BucketTaggingConfiguration bucketTaggingConfiguration) {
        setBucketTaggingConfiguration(new SetBucketTaggingConfigurationRequest(bucketName, bucketTaggingConfiguration));
    }

    /**
     * @exclude
     */
    @Override
    public void setBucketTaggingConfiguration(
            SetBucketTaggingConfigurationRequest setBucketTaggingConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

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
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(
            String bucketName) throws SdkClientException,
            AmazonServiceException {
        return getBucketWebsiteConfiguration(new GetBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(
            GetBucketWebsiteConfigurationRequest getBucketWebsiteConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void setBucketWebsiteConfiguration(String bucketName,
            BucketWebsiteConfiguration configuration)
            throws SdkClientException, AmazonServiceException {
        setBucketWebsiteConfiguration(new SetBucketWebsiteConfigurationRequest(bucketName, configuration));
    }

    @Override
    public void setBucketWebsiteConfiguration(
            SetBucketWebsiteConfigurationRequest setBucketWebsiteConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public void deleteBucketWebsiteConfiguration(String bucketName)
            throws SdkClientException, AmazonServiceException {
        deleteBucketWebsiteConfiguration(new DeleteBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketWebsiteConfiguration(
            DeleteBucketWebsiteConfigurationRequest deleteBucketWebsiteConfigurationRequest)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key,
            Date expiration) throws SdkClientException {
        return generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key,
            Date expiration, HttpMethod method) throws SdkClientException {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, method);
        request.setExpiration(expiration);

        return generatePresignedUrl(request);
    }

    @Override
    public URL generatePresignedUrl(
            GeneratePresignedUrlRequest generatePresignedUrlRequest)
            throws SdkClientException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public PartListing listParts(ListPartsRequest request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest request)
            throws SdkClientException, AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public MultipartUploadListing listMultipartUploads(
            ListMultipartUploadsRequest request) throws SdkClientException,
            AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public S3ResponseMetadata getCachedResponseMetadata(
            AmazonWebServiceRequest request) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void restoreObject(String bucketName, String key,
            int expirationInDays) throws AmazonServiceException {
        restoreObject(new RestoreObjectRequest(bucketName, key, expirationInDays));
    }


    @Override
    public void restoreObject(RestoreObjectRequest request)
            throws AmazonServiceException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

    }

//IBM unsupported
//    @Override
//    public void setRequestPaymentConfiguration(SetRequestPaymentConfigurationRequest request) {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
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
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");

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
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void deleteBucketReplicationConfiguration(String bucketName)
            throws AmazonServiceException, SdkClientException {
        deleteBucketReplicationConfiguration(new
                DeleteBucketReplicationConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketReplicationConfiguration(DeleteBucketReplicationConfigurationRequest request)
            throws AmazonServiceException, SdkClientException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectName)
            throws AmazonServiceException, SdkClientException {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

//IBM unsupported
//    @Override
//    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(
//            String bucketName, String id) throws AmazonServiceException, SdkClientException {
//        return deleteBucketIntelligentTieringConfiguration(new DeleteBucketIntelligentTieringConfigurationRequest(bucketName, id));
//    }
//
//    @Override
//    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(
//            DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest)
//            throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(
//            String bucketName, String id) throws AmazonServiceException, SdkClientException {
//        return getBucketIntelligentTieringConfiguration(new GetBucketIntelligentTieringConfigurationRequest(bucketName, id));
//    }
//
//    @Override
//    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(
//            GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest)
//            throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(
//            String bucketName, IntelligentTieringConfiguration intelligentTieringConfiguration)
//            throws AmazonServiceException, SdkClientException {
//        return setBucketIntelligentTieringConfiguration(
//                new SetBucketIntelligentTieringConfigurationRequest(bucketName, intelligentTieringConfiguration));
//    }
//
//    @Override
//    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(
//            SetBucketIntelligentTieringConfigurationRequest setBucketIntelligentTieringConfigurationRequest)
//            throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public ListBucketIntelligentTieringConfigurationsResult listBucketIntelligentTieringConfigurations(
//            ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest)
//            throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }

    @Override
    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest request) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest request) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest request) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

//IBM unsupported
//	  @Override
//    public WriteGetObjectResponseResult writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest) {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }

    @Override
    public GetObjectLockConfigurationResult getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
    throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public SetObjectLockConfigurationResult setObjectLockConfiguration(SetObjectLockConfigurationRequest setObjectLockConfigurationRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest setObjectLegalHoldRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest setObjectRetentionRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest presignedUrlDownloadRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void download(PresignedUrlDownloadRequest presignedUrlDownloadRequest, File destinationFile) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest presignedUrlUploadRequest) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public com.ibm.cloud.objectstorage.services.s3.model.Region getRegion() {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public String getRegionName() {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public URL getUrl(String bucketName, String key) {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

    @Override
    public AmazonS3Waiters waiters() {
        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
    }

//IBM unsupported
//    @Override
//    public DeleteBucketOwnershipControlsResult deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest)
//        throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public GetBucketOwnershipControlsResult getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest)
//        throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public SetBucketOwnershipControlsResult setBucketOwnershipControls(String bucketName, OwnershipControls ownershipControls)
//        throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
//
//    @Override
//    public SetBucketOwnershipControlsResult setBucketOwnershipControls(SetBucketOwnershipControlsRequest setBucketOwnershipControlsRequest)
//        throws AmazonServiceException, SdkClientException {
//        throw new UnsupportedOperationException("Extend AbstractAmazonS3 to provide an implementation");
//    }
}
