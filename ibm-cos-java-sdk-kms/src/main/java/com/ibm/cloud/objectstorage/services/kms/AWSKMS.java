/*
 * Copyright 2019-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.ibm.cloud.objectstorage.services.kms;

import javax.annotation.Generated;

import com.ibm.cloud.objectstorage.*;
import com.ibm.cloud.objectstorage.regions.*;

import com.ibm.cloud.objectstorage.services.kms.model.*;

/**
 * Interface for accessing KMS.
 * <p>
 * <b>Note:</b> Do not directly implement this interface, new methods are added to it regularly. Extend from
 * {@link com.ibm.cloud.objectstorage.services.kms.AbstractAWSKMS} instead.
 * </p>
 * <p>
 * <fullname>Key Management Service</fullname>
 * <p>
 * Key Management Service (KMS) is an encryption and key management web service. This guide describes the KMS operations
 * that you can call programmatically. For general information about KMS, see the <a
 * href="https://docs.aws.amazon.com/kms/latest/developerguide/"> <i>Key Management Service Developer Guide</i> </a>.
 * </p>
 * <note>
 * <p>
 * KMS has replaced the term <i>customer master key (CMK)</i> with <i>KMS key</i> and <i>KMS key</i>. The concept has
 * not changed. To prevent breaking changes, KMS is keeping some variations of this term.
 * </p>
 * <p>
 * Amazon Web Services provides SDKs that consist of libraries and sample code for various programming languages and
 * platforms (Java, Ruby, .Net, macOS, Android, etc.). The SDKs provide a convenient way to create programmatic access
 * to KMS and other Amazon Web Services services. For example, the SDKs take care of tasks such as signing requests (see
 * below), managing errors, and retrying requests automatically. For more information about the Amazon Web Services
 * SDKs, including how to download and install them, see <a href="http://aws.amazon.com/tools/">Tools for Amazon Web
 * Services</a>.
 * </p>
 * </note>
 * <p>
 * We recommend that you use the Amazon Web Services SDKs to make programmatic API calls to KMS.
 * </p>
 * <p>
 * If you need to use FIPS 140-2 validated cryptographic modules when communicating with Amazon Web Services, use the
 * FIPS endpoint in your preferred Amazon Web Services Region. For more information about the available FIPS endpoints,
 * see <a href="https://docs.aws.amazon.com/general/latest/gr/kms.html#kms_region">Service endpoints</a> in the Key
 * Management Service topic of the <i>Amazon Web Services General Reference</i>.
 * </p>
 * <p>
 * All KMS API calls must be signed and be transmitted using Transport Layer Security (TLS). KMS recommends you always
 * use the latest supported TLS version. Clients must also support cipher suites with Perfect Forward Secrecy (PFS) such
 * as Ephemeral Diffie-Hellman (DHE) or Elliptic Curve Ephemeral Diffie-Hellman (ECDHE). Most modern systems such as
 * Java 7 and later support these modes.
 * </p>
 * <p>
 * <b>Signing Requests</b>
 * </p>
 * <p>
 * Requests must be signed using an access key ID and a secret access key. We strongly recommend that you do not use
 * your Amazon Web Services account root access key ID and secret access key for everyday work. You can use the access
 * key ID and secret access key for an IAM user or you can use the Security Token Service (STS) to generate temporary
 * security credentials and use those to sign requests.
 * </p>
 * <p>
 * All KMS requests must be signed with <a
 * href="https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html">Signature Version 4</a>.
 * </p>
 * <p>
 * <b>Logging API Requests</b>
 * </p>
 * <p>
 * KMS supports CloudTrail, a service that logs Amazon Web Services API calls and related events for your Amazon Web
 * Services account and delivers them to an Amazon S3 bucket that you specify. By using the information collected by
 * CloudTrail, you can determine what requests were made to KMS, who made the request, when it was made, and so on. To
 * learn more about CloudTrail, including how to turn it on and find your log files, see the <a
 * href="https://docs.aws.amazon.com/awscloudtrail/latest/userguide/">CloudTrail User Guide</a>.
 * </p>
 * <p>
 * <b>Additional Resources</b>
 * </p>
 * <p>
 * For more information about credentials and request signing, see the following:
 * </p>
 * <ul>
 * <li>
 * <p>
 * <a href="https://docs.aws.amazon.com/general/latest/gr/aws-security-credentials.html">Amazon Web Services Security
 * Credentials</a> - This topic provides general information about the types of credentials used to access Amazon Web
 * Services.
 * </p>
 * </li>
 * <li>
 * <p>
 * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp.html">Temporary Security
 * Credentials</a> - This section of the <i>IAM User Guide</i> describes how to create and use temporary security
 * credentials.
 * </p>
 * </li>
 * <li>
 * <p>
 * <a href="https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html">Signature Version 4 Signing
 * Process</a> - This set of topics walks you through the process of signing a request using an access key ID and a
 * secret access key.
 * </p>
 * </li>
 * </ul>
 * <p>
 * <b>Commonly Used API Operations</b>
 * </p>
 * <p>
 * Of the API operations discussed in this guide, the following will prove the most useful for most applications. You
 * will likely perform operations other than these, such as creating keys and assigning policies, by using the console.
 * </p>
 * <ul>
 * <li>
 * <p>
 * <a>Encrypt</a>
 * </p>
 * </li>
 * <li>
 * <p>
 * <a>Decrypt</a>
 * </p>
 * </li>
 * <li>
 * <p>
 * <a>GenerateDataKey</a>
 * </p>
 * </li>
 * <li>
 * <p>
 * <a>GenerateDataKeyWithoutPlaintext</a>
 * </p>
 * </li>
 * </ul>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public interface AWSKMS {

    /**
     * The region metadata service name for computing region endpoints. You can use this value to retrieve metadata
     * (such as supported regions) of the service.
     *
     * @see RegionUtils#getRegionsForService(String)
     */
    String ENDPOINT_PREFIX = "kms";

    /**
     * Overrides the default endpoint for this client ("https://kms.us-east-1.amazonaws.com/"). Callers can use this
     * method to control which AWS region they want to work with.
     * <p>
     * Callers can pass in just the endpoint (ex: "kms.us-east-1.amazonaws.com/") or a full URL, including the protocol
     * (ex: "https://kms.us-east-1.amazonaws.com/"). If the protocol is not specified here, the default protocol from
     * this client's {@link ClientConfiguration} will be used, which by default is HTTPS.
     * <p>
     * For more information on using AWS regions with the AWS SDK for Java, and a complete list of all available
     * endpoints for all AWS services, see: <a
     * href="http://developer.amazonwebservices.com/connect/entry.jspa?externalID=3912">
     * http://developer.amazonwebservices.com/connect/entry.jspa?externalID=3912</a>
     * <p>
     * <b>This method is not threadsafe. An endpoint should be configured when the client is created and before any
     * service requests are made. Changing it afterwards creates inevitable race conditions for any service requests in
     * transit or retrying.</b>
     *
     * @param endpoint
     *        The endpoint (ex: "kms.us-east-1.amazonaws.com/") or a full URL, including the protocol (ex:
     *        "https://kms.us-east-1.amazonaws.com/") of the region specific AWS endpoint this client will communicate
     *        with.
     * @deprecated use {@link AwsClientBuilder#setEndpointConfiguration(AwsClientBuilder.EndpointConfiguration)} for
     *             example:
     *             {@code builder.setEndpointConfiguration(new EndpointConfiguration(endpoint, signingRegion));}
     */
    @Deprecated
    void setEndpoint(String endpoint);

    /**
     * An alternative to {@link AWSKMS#setEndpoint(String)}, sets the regional endpoint for this client's service calls.
     * Callers can use this method to control which AWS region they want to work with.
     * <p>
     * By default, all service endpoints in all regions use the https protocol. To use http instead, specify it in the
     * {@link ClientConfiguration} supplied at construction.
     * <p>
     * <b>This method is not threadsafe. A region should be configured when the client is created and before any service
     * requests are made. Changing it afterwards creates inevitable race conditions for any service requests in transit
     * or retrying.</b>
     *
     * @param region
     *        The region this client will communicate with. See {@link Region#getRegion(com.ibm.cloud.objectstorage.regions.Regions)}
     *        for accessing a given region. Must not be null and must be a region where the service is available.
     *
     * @see Region#getRegion(com.ibm.cloud.objectstorage.regions.Regions)
     * @see Region#createClient(Class, com.ibm.cloud.objectstorage.auth.AWSCredentialsProvider, ClientConfiguration)
     * @see Region#isServiceSupported(String)
     * @deprecated use {@link AwsClientBuilder#setRegion(String)}
     */
    @Deprecated
    void setRegion(Region region);

    /**
     * <p>
     * Cancels the deletion of a KMS key. When this operation succeeds, the key state of the KMS key is
     * <code>Disabled</code>. To enable the KMS key, use <a>EnableKey</a>.
     * </p>
     * <p>
     * For more information about scheduling and canceling deletion of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/deleting-keys.html">Deleting KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * The KMS key that you use for this operation must be in a compatible key state. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * <b>Cross-account use</b>: No. You cannot perform this operation on a KMS key in a different Amazon Web Services
     * account.
     * </p>
     * <p>
     * <b>Required permissions</b>: <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/kms-api-permissions-reference.html"
     * >kms:CancelKeyDeletion</a> (key policy)
     * </p>
     * <p>
     * <b>Related operations</b>: <a>ScheduleKeyDeletion</a>
     * </p>
     * <p>
     * <b>Eventual consistency</b>: The KMS API follows an eventual consistency model. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-eventual-consistency.html">KMS eventual
     * consistency</a>.
     * </p>
     * 
     * @param cancelKeyDeletionRequest
     * @return Result of the CancelKeyDeletion operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN, or an ARN in a key policy, is not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. You can retry the request.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         This exceptions means one of the following:
     *         </p>
     *         <ul>
     *         <li>
     *         <p>
     *         The key state of the KMS key is not compatible with the operation.
     *         </p>
     *         <p>
     *         To find the key state, use the <a>DescribeKey</a> operation. For more information about which key states
     *         are compatible with each KMS operation, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *         the <i> <i>Key Management Service Developer Guide</i> </i>.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         For cryptographic operations on KMS keys in custom key stores, this exception represents a general
     *         failure with many possible causes. To identify the cause, see the error message that accompanies the
     *         exception.
     *         </p>
     *         </li>
     * @sample AWSKMS.CancelKeyDeletion
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CancelKeyDeletion" target="_top">AWS API
     *      Documentation</a>
     */
    CancelKeyDeletionResult cancelKeyDeletion(CancelKeyDeletionRequest cancelKeyDeletionRequest);

    /**
     * <p>
     * Connects or reconnects a <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>
     * to its backing key store. For an CloudHSM key store, <code>ConnectCustomKeyStore</code> connects the key store to
     * its associated CloudHSM cluster. For an external key store, <code>ConnectCustomKeyStore</code> connects the key
     * store to the external key store proxy that communicates with your external key manager.
     * </p>
     * <p>
     * The custom key store must be connected before you can create KMS keys in the key store or use the KMS keys it
     * contains. You can disconnect and reconnect a custom key store at any time.
     * </p>
     * <p>
     * The connection process for a custom key store can take an extended amount of time to complete. This operation
     * starts the connection process, but it does not wait for it to complete. When it succeeds, this operation quickly
     * returns an HTTP 200 response and a JSON object with no properties. However, this response does not indicate that
     * the custom key store is connected. To get the connection state of the custom key store, use the
     * <a>DescribeCustomKeyStores</a> operation.
     * </p>
     * 
     * @param createAliasRequest
     * @return Result of the CreateAlias operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws AlreadyExistsException
     *         The request was rejected because it attempted to create a resource that already exists.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidAliasNameException
     *         The request was rejected because the specified alias name is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.CreateAlias
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CreateAlias" target="_top">AWS API
     *      Documentation</a>
     */
    CreateAliasResult createAlias(CreateAliasRequest createAliasRequest);

    /**
     * <p>
     * <b>External key store</b>
     * </p>
     * <p>
     * When you connect an external key store that uses public endpoint connectivity, KMS tests its ability to
     * communicate with your external key manager by sending a request via the external key store proxy.
     * </p>
     * 
     * @param createGrantRequest
     * @return Result of the CreateGrant operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.CreateGrant
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CreateGrant" target="_top">AWS API
     *      Documentation</a>
     */
    CreateGrantResult createGrant(CreateGrantRequest createGrantRequest);

    /**
     * <p>
     * <b>Required permissions</b>: <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/kms-api-permissions-reference.html"
     * >kms:ConnectCustomKeyStore</a> (IAM policy)
     * </p>
     * <p>
     * <b>Related operations</b>
     * </p>
     * <ul>
     * <li>
     * <p>
     * <a>CreateCustomKeyStore</a>
     * </p>
     * </li>
     * <li>
     * <p>
     * <a>DeleteCustomKeyStore</a>
     * </p>
     * </li>
     * </ul>
     * 
     * @param createKeyRequest
     * @return Result of the CreateKey operation returned by the service.
     * @throws MalformedPolicyDocumentException
     *         The request was rejected because the specified policy is not syntactically or semantically correct.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws TagException
     *         The request was rejected because one or more tags are not valid.
     * @sample AWSKMS.CreateKey
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CreateKey" target="_top">AWS API
     *      Documentation</a>
     */
    CreateKeyResult createKey(CreateKeyRequest createKeyRequest);

    /**
     * Simplified method form for invoking the CreateKey operation.
     *
     * @see #createKey(CreateKeyRequest)
     */
    CreateKeyResult createKey();

    /**
     * <p>
     * Decrypts ciphertext. Ciphertext is plaintext that has been previously encrypted by using any of the following
     * functions:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <a>DescribeCustomKeyStores</a>
     * </p>
     * </li>
     * <li>
     * <p>
     * <a>DisconnectCustomKeyStore</a>
     * </p>
     * </li>
     * <li>
     * <p>
     * <a>UpdateCustomKeyStore</a>
     * </p>
     * </li>
     * </ul>
     * <p>
     * <b>Eventual consistency</b>: The KMS API follows an eventual consistency model. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-eventual-consistency.html">KMS eventual
     * consistency</a>.
     * </p>
     * 
     * @param decryptRequest
     * @return Result of the Decrypt operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws InvalidCiphertextException
     *         The request was rejected because the specified ciphertext has been corrupted or is otherwise invalid.
     * @throws KeyUnavailableException
     *         The request was rejected because the specified CMK was not available. The request can be retried.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.Decrypt
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/Decrypt" target="_top">AWS API
     *      Documentation</a>
     */
    DecryptResult decrypt(DecryptRequest decryptRequest);

    /**
     * <p>
     * Deletes the specified alias. To map an alias to a different key, call <a>UpdateAlias</a>.
     * </p>
     * 
     * @param deleteAliasRequest
     * @return Result of the DeleteAlias operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws CloudHsmClusterInvalidConfigurationException
     *         The request was rejected because the associated CloudHSM cluster did not meet the configuration
     *         requirements for an CloudHSM key store.</p>
     *         <ul>
     *         <li>
     *         <p>
     *         The CloudHSM cluster must be configured with private subnets in at least two different Availability Zones
     *         in the Region.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         The <a href="https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg.html">security group for
     *         the cluster</a> (cloudhsm-cluster-<i>&lt;cluster-id&gt;</i>-sg) must include inbound rules and outbound
     *         rules that allow TCP traffic on ports 2223-2225. The <b>Source</b> in the inbound rules and the
     *         <b>Destination</b> in the outbound rules must match the security group ID. These rules are set by default
     *         when you create the CloudHSM cluster. Do not delete or change them. To get information about a particular
     *         security group, use the <a
     *         href="https://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DescribeSecurityGroups.html"
     *         >DescribeSecurityGroups</a> operation.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         The CloudHSM cluster must contain at least as many HSMs as the operation requires. To add HSMs, use the
     *         CloudHSM <a
     *         href="https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_CreateHsm.html">CreateHsm</a>
     *         operation.
     *         </p>
     *         <p>
     *         For the <a>CreateCustomKeyStore</a>, <a>UpdateCustomKeyStore</a>, and <a>CreateKey</a> operations, the
     *         CloudHSM cluster must have at least two active HSMs, each in a different Availability Zone. For the
     *         <a>ConnectCustomKeyStore</a> operation, the CloudHSM must contain at least one active HSM.
     *         </p>
     *         </li>
     *         </ul>
     *         <p>
     *         For information about the requirements for an CloudHSM cluster that is associated with an CloudHSM key
     *         store, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/create-keystore.html#before-keystore"
     *         >Assemble the Prerequisites</a> in the <i>Key Management Service Developer Guide</i>. For information
     *         about creating a private subnet for an CloudHSM cluster, see <a
     *         href="https://docs.aws.amazon.com/cloudhsm/latest/userguide/create-subnets.html">Create a Private
     *         Subnet</a> in the <i>CloudHSM User Guide</i>. For information about cluster security groups, see <a
     *         href="https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg.html">Configure a Default
     *         Security Group</a> in the <i> <i>CloudHSM User Guide</i> </i>.
     * @sample AWSKMS.ConnectCustomKeyStore
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ConnectCustomKeyStore" target="_top">AWS API
     *      Documentation</a>
     */
    DeleteAliasResult deleteAlias(DeleteAliasRequest deleteAliasRequest);

    /**
     * <p>
     * Creates a friendly name for a KMS key.
     * </p>
     * <note>
     * <p>
     * Adding, deleting, or updating an alias can allow or deny permission to the KMS key. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/abac.html">ABAC for KMS</a> in the <i>Key Management
     * Service Developer Guide</i>.
     * </p>
     * </note>
     * <p>
     * You can use an alias to identify a KMS key in the KMS console, in the <a>DescribeKey</a> operation and in <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">cryptographic
     * operations</a>, such as <a>Encrypt</a> and <a>GenerateDataKey</a>. You can also change the KMS key that's
     * associated with the alias (<a>UpdateAlias</a>) or delete the alias (<a>DeleteAlias</a>) at any time. These
     * operations don't affect the underlying KMS key.
     * </p>
     * <p>
     * You can associate the alias with any customer managed key in the same Amazon Web Services Region. Each alias is
     * associated with only one KMS key at a time, but a KMS key can have multiple aliases. A valid KMS key is required.
     * You can't create an alias without a KMS key.
     * </p>
     * 
     * @param deleteImportedKeyMaterialRequest
     * @return Result of the DeleteImportedKeyMaterial operation returned by the service.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.DeleteImportedKeyMaterial
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/DeleteImportedKeyMaterial" target="_top">AWS
     *      API Documentation</a>
     */
    DeleteImportedKeyMaterialResult deleteImportedKeyMaterial(DeleteImportedKeyMaterialRequest deleteImportedKeyMaterialRequest);

    /**
     * <p>
     * <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/kms-api-permissions-reference.html">kms:CreateAlias
     * </a> on the KMS key (key policy).
     * </p>
     * 
     * @param describeKeyRequest
     * @return Result of the DescribeKey operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @sample AWSKMS.DescribeKey
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/DescribeKey" target="_top">AWS API
     *      Documentation</a>
     */
    DescribeKeyResult describeKey(DescribeKeyRequest describeKeyRequest);

    /**
     * <p>
     * <a>UpdateAlias</a>
     * </p>
     * </li>
     * </ul>
     * <p>
     * <b>Eventual consistency</b>: The KMS API follows an eventual consistency model. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-eventual-consistency.html">KMS eventual
     * consistency</a>.
     * </p>
     * 
     * @param createAliasRequest
     * @return Result of the CreateAlias operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. You can retry the request.
     * @throws AlreadyExistsException
     *         The request was rejected because it attempted to create a resource that already exists.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidAliasNameException
     *         The request was rejected because the specified alias name is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws LimitExceededException
     *         The request was rejected because a quota was exceeded. For more information, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Quotas</a> in the <i>Key
     *         Management Service Developer Guide</i>.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         This exceptions means one of the following:
     *         </p>
     *         <ul>
     *         <li>
     *         <p>
     *         The key state of the KMS key is not compatible with the operation.
     *         </p>
     *         <p>
     *         To find the key state, use the <a>DescribeKey</a> operation. For more information about which key states
     *         are compatible with each KMS operation, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *         the <i> <i>Key Management Service Developer Guide</i> </i>.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         For cryptographic operations on KMS keys in custom key stores, this exception represents a general
     *         failure with many possible causes. To identify the cause, see the error message that accompanies the
     *         exception.
     *         </p>
     *         </li>
     * @sample AWSKMS.CreateAlias
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CreateAlias" target="_top">AWS API
     *      Documentation</a>
     */
    DisableKeyResult disableKey(DisableKeyRequest disableKeyRequest);

    /**
     * <p>
     * Disables rotation of the specified key.
     * </p>
     * 
     * @param disableKeyRotationRequest
     * @return Result of the DisableKeyRotation operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @sample AWSKMS.DisableKeyRotation
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/DisableKeyRotation" target="_top">AWS API
     *      Documentation</a>
     */
    DisableKeyRotationResult disableKeyRotation(DisableKeyRotationRequest disableKeyRotationRequest);

    /**
     * <p>
     * Marks a key as enabled, thereby permitting its use.
     * </p>
     * 
     * @param enableKeyRequest
     * @return Result of the EnableKey operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.EnableKey
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/EnableKey" target="_top">AWS API
     *      Documentation</a>
     */
    EnableKeyResult enableKey(EnableKeyRequest enableKeyRequest);

    /**
     * <p>
     * Enables rotation of the specified customer master key.
     * </p>
     * 
     * @param enableKeyRotationRequest
     * @return Result of the EnableKeyRotation operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @sample AWSKMS.EnableKeyRotation
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/EnableKeyRotation" target="_top">AWS API
     *      Documentation</a>
     */
    EnableKeyRotationResult enableKeyRotation(EnableKeyRotationRequest enableKeyRotationRequest);

    /**
     * <p>
     * Encrypts plaintext into ciphertext by using a customer master key. The <code>Encrypt</code> function has two
     * primary use cases:
     * </p>
     * <ul>
     * <li>
     * <p>
     * You can encrypt up to 4 KB of arbitrary data such as an RSA key, a database password, or other sensitive customer
     * information.
     * </p>
     * </li>
     * <li>
     * <p>
     * If you are moving encrypted data from one region to another, you can use this API to encrypt in the new region
     * the plaintext data key that was used to encrypt the data in the original region. This provides you with an
     * encrypted copy of the data key that can be decrypted in the new region and used there to decrypt the encrypted
     * data.
     * </p>
     * </li>
     * </ul>
     * <p>
     * Unless you are moving encrypted data from one region to another, you don't use this function to encrypt a
     * generated data key within a region. You retrieve data keys already encrypted by calling the
     * <a>GenerateDataKey</a> or <a>GenerateDataKeyWithoutPlaintext</a> function. Data keys don't need to be encrypted
     * again by calling <code>Encrypt</code>.
     * </p>
     * <p>
     * If you want to encrypt data locally in your application, you can use the <code>GenerateDataKey</code> function to
     * return a plaintext data encryption key and a copy of the key encrypted under the customer master key (CMK) of
     * your choosing.
     * </p>
     * 
     * @param encryptRequest
     * @return Result of the Encrypt operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws KeyUnavailableException
     *         The request was rejected because the specified CMK was not available. The request can be retried.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidKeyUsageException
     *         The request was rejected because the specified <code>KeySpec</code> value is not valid.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.Encrypt
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/Encrypt" target="_top">AWS API
     *      Documentation</a>
     */
    EncryptResult encrypt(EncryptRequest encryptRequest);

    /**
     * <p>
     * Returns a data encryption key that you can use in your application to encrypt data locally.
     * </p>
     * <p>
     * You must specify the customer master key (CMK) under which to generate the data key. You must also specify the
     * length of the data key using either the <code>KeySpec</code> or <code>NumberOfBytes</code> field. You must
     * specify one field or the other, but not both. For common key lengths (128-bit and 256-bit symmetric keys), we
     * recommend that you use <code>KeySpec</code>.
     * </p>
     * <p>
     * This operation returns a plaintext copy of the data key in the <code>Plaintext</code> field of the response, and
     * an encrypted copy of the data key in the <code>CiphertextBlob</code> field. The data key is encrypted under the
     * CMK specified in the <code>KeyId</code> field of the request.
     * </p>
     * <p>
     * We recommend that you use the following pattern to encrypt data locally in your application:
     * </p>
     * <ol>
     * <li>
     * <p>
     * Use this operation (<code>GenerateDataKey</code>) to retrieve a data encryption key.
     * </p>
     * </li>
     * <li>
     * <p>
     * Use the plaintext data encryption key (returned in the <code>Plaintext</code> field of the response) to encrypt
     * data locally, then erase the plaintext data key from memory.
     * </p>
     * </li>
     * <li>
     * <p>
     * Store the encrypted data key (returned in the <code>CiphertextBlob</code> field of the response) alongside the
     * locally encrypted data.
     * </p>
     * </li>
     * </ol>
     * <p>
     * To decrypt data locally:
     * </p>
     * <ol>
     * <li>
     * <p>
     * Use the <a>Decrypt</a> operation to decrypt the encrypted data key into a plaintext copy of the data key.
     * </p>
     * </li>
     * <li>
     * <p>
     * Use the plaintext data key to decrypt data locally, then erase the plaintext data key from memory.
     * </p>
     * </li>
     * </ol>
     * <p>
     * To return only an encrypted copy of the data key, use <a>GenerateDataKeyWithoutPlaintext</a>. To return a random
     * byte string that is cryptographically secure, use <a>GenerateRandom</a>.
     * </p>
     * <p>
     * If you use the optional <code>EncryptionContext</code> field, you must store at least enough information to be
     * able to reconstruct the full encryption context when you later send the ciphertext to the <a>Decrypt</a>
     * operation. It is a good practice to choose an encryption context that you can reconstruct on the fly to better
     * secure the ciphertext. For more information, see <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/encryption-context.html">Encryption Context</a> in the
     * <i>AWS Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param generateDataKeyRequest
     * @return Result of the GenerateDataKey operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws KeyUnavailableException
     *         The request was rejected because the specified CMK was not available. The request can be retried.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidKeyUsageException
     *         The request was rejected because the specified <code>KeySpec</code> value is not valid.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.GenerateDataKey
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GenerateDataKey" target="_top">AWS API
     *      Documentation</a>
     */
    GenerateDataKeyResult generateDataKey(GenerateDataKeyRequest generateDataKeyRequest);
//    IBM Unsupported
//    GenerateDataKeyPairResult generateDataKeyPair(GenerateDataKeyPairRequest request);

    /**
     * <p>
     * Returns a data encryption key encrypted under a customer master key (CMK). This operation is identical to
     * <a>GenerateDataKey</a> but returns only the encrypted copy of the data key.
     * </p>
     * <p>
     * This operation is useful in a system that has multiple components with different degrees of trust. For example,
     * consider a system that stores encrypted data in containers. Each container stores the encrypted data and an
     * encrypted copy of the data key. One component of the system, called the <i>control plane</i>, creates new
     * containers. When it creates a new container, it uses this operation (<code>GenerateDataKeyWithoutPlaintext</code>
     * ) to get an encrypted data key and then stores it in the container. Later, a different component of the system,
     * called the <i>data plane</i>, puts encrypted data into the containers. To do this, it passes the encrypted data
     * key to the <a>Decrypt</a> operation, then uses the returned plaintext data key to encrypt data, and finally
     * stores the encrypted data in the container. In this system, the control plane never sees the plaintext data key.
     * </p>
     * 
     * @param generateDataKeyWithoutPlaintextRequest
     * @return Result of the GenerateDataKeyWithoutPlaintext operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws KeyUnavailableException
     *         The request was rejected because the specified CMK was not available. The request can be retried.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidKeyUsageException
     *         The request was rejected because the specified <code>KeySpec</code> value is not valid.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.GenerateDataKeyWithoutPlaintext
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GenerateDataKeyWithoutPlaintext"
     *      target="_top">AWS API Documentation</a>
     */
    GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintext(GenerateDataKeyWithoutPlaintextRequest generateDataKeyWithoutPlaintextRequest);

    /**
     * <p>
     * Returns a random byte string that is cryptographically secure.
     * </p>
     * <p>
     * For more information about entropy and random number generation, see the <a
     * href="https://d0.awsstatic.com/whitepapers/KMS-Cryptographic-Details.pdf">AWS Key Management Service
     * Cryptographic Details</a> whitepaper.
     * </p>
     * 
     * @param generateRandomRequest
     * @return Result of the GenerateRandom operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @sample AWSKMS.GenerateRandom
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GenerateRandom" target="_top">AWS API
     *      Documentation</a>
     */
    GenerateRandomResult generateRandom(GenerateRandomRequest generateRandomRequest);

    /**
     * Simplified method form for invoking the GenerateRandom operation.
     *
     * @see #generateRandom(GenerateRandomRequest)
     */
    GenerateRandomResult generateRandom();

    /**
     * <p>
     * Retrieves a policy attached to the specified key.
     * </p>
     * 
     * @param getKeyPolicyRequest
     * @return Result of the GetKeyPolicy operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.GetKeyPolicy
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GetKeyPolicy" target="_top">AWS API
     *      Documentation</a>
     */
    GetKeyPolicyResult getKeyPolicy(GetKeyPolicyRequest getKeyPolicyRequest);

    /**
     * <p>
     * Retrieves a Boolean value that indicates whether key rotation is enabled for the specified key.
     * </p>
     * 
     * @param getKeyRotationStatusRequest
     * @return Result of the GetKeyRotationStatus operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @sample AWSKMS.GetKeyRotationStatus
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GetKeyRotationStatus" target="_top">AWS API
     *      Documentation</a>
     */
    GetKeyRotationStatusResult getKeyRotationStatus(GetKeyRotationStatusRequest getKeyRotationStatusRequest);

    /**
     * <p>
     * Returns the items you need in order to import key material into AWS KMS from your existing key management
     * infrastructure. For more information about importing key material into AWS KMS, see <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/importing-keys.html">Importing Key Material</a> in the
     * <i>AWS Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * You must specify the key ID of the customer master key (CMK) into which you will import key material. This CMK's
     * <code>Origin</code> must be <code>EXTERNAL</code>. You must also specify the wrapping algorithm and type of
     * wrapping key (public key) that you will use to encrypt the key material.
     * </p>
     * <p>
     * This operation returns a public key and an import token. Use the public key to encrypt the key material. Store
     * the import token to send with a subsequent <a>ImportKeyMaterial</a> request. The public key and import token from
     * the same response must be used together. These items are valid for 24 hours, after which they cannot be used for
     * a subsequent <a>ImportKeyMaterial</a> request. To retrieve new ones, send another
     * <code>GetParametersForImport</code> request.
     * </p>
     * 
     * @param getParametersForImportRequest
     * @return Result of the GetParametersForImport operation returned by the service.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.GetParametersForImport
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GetParametersForImport" target="_top">AWS API
     *      Documentation</a>
     */
    GetParametersForImportResult getParametersForImport(GetParametersForImportRequest getParametersForImportRequest);

    /**
     * <p>
     * Imports key material into an AWS KMS customer master key (CMK) from your existing key management infrastructure.
     * For more information about importing key material into AWS KMS, see <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/importing-keys.html">Importing Key Material</a> in the
     * <i>AWS Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * You must specify the key ID of the CMK to import the key material into. This CMK's <code>Origin</code> must be
     * <code>EXTERNAL</code>. You must also send an import token and the encrypted key material. Send the import token
     * that you received in the same <a>GetParametersForImport</a> response that contained the public key that you used
     * to encrypt the key material. You must also specify whether the key material expires and if so, when. When the key
     * material expires, AWS KMS deletes the key material and the CMK becomes unusable. To use the CMK again, you can
     * reimport the same key material. If you set an expiration date, you can change it only by reimporting the same key
     * material and specifying a new expiration date.
     * </p>
     * <p>
     * When this operation is successful, the specified CMK's key state changes to <code>Enabled</code>, and you can use
     * the CMK.
     * </p>
     * <p>
     * After you successfully import key material into a CMK, you can reimport the same key material into that CMK, but
     * you cannot import different key material.
     * </p>
     * 
     * @param importKeyMaterialRequest
     * @return Result of the ImportKeyMaterial operation returned by the service.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws InvalidCiphertextException
     *         The request was rejected because the specified ciphertext has been corrupted or is otherwise invalid.
     * @throws IncorrectKeyMaterialException
     *         The request was rejected because the provided key material is invalid or is not the same key material
     *         that was previously imported into this customer master key (CMK).
     * @throws ExpiredImportTokenException
     *         The request was rejected because the provided import token is expired. Use <a>GetParametersForImport</a>
     *         to retrieve a new import token and public key, use the new public key to encrypt the key material, and
     *         then try the request again.
     * @throws InvalidImportTokenException
     *         The request was rejected because the provided import token is invalid or is associated with a different
     *         customer master key (CMK).
     * @sample AWSKMS.ImportKeyMaterial
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ImportKeyMaterial" target="_top">AWS API
     *      Documentation</a>
     */
    ImportKeyMaterialResult importKeyMaterial(ImportKeyMaterialRequest importKeyMaterialRequest);

    /**
     * <p>
     * Lists all of the key aliases in the account.
     * </p>
     * 
     * @param listAliasesRequest
     * @return Result of the ListAliases operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidMarkerException
     *         The request was rejected because the marker that specifies where pagination should next begin is not
     *         valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @sample AWSKMS.ListAliases
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListAliases" target="_top">AWS API
     *      Documentation</a>
     */
    ListAliasesResult listAliases(ListAliasesRequest listAliasesRequest);

    /**
     * Simplified method form for invoking the ListAliases operation.
     *
     * @see #listAliases(ListAliasesRequest)
     */
    ListAliasesResult listAliases();

    /**
     * <p>
     * List the grants for a specified key.
     * </p>
     * 
     * @param listGrantsRequest
     * @return Result of the ListGrants operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidMarkerException
     *         The request was rejected because the marker that specifies where pagination should next begin is not
     *         valid.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.ListGrants
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListGrants" target="_top">AWS API
     *      Documentation</a>
     */
    ListGrantsResult listGrants(ListGrantsRequest listGrantsRequest);

    /**
     * <p>
     * Retrieves a list of policies attached to a key.
     * </p>
     * 
     * @param listKeyPoliciesRequest
     * @return Result of the ListKeyPolicies operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.ListKeyPolicies
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListKeyPolicies" target="_top">AWS API
     *      Documentation</a>
     */
    ListKeyPoliciesResult listKeyPolicies(ListKeyPoliciesRequest listKeyPoliciesRequest);

    /**
     * <p>
     * Lists the customer master keys.
     * </p>
     * 
     * @param listKeysRequest
     * @return Result of the ListKeys operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws InvalidMarkerException
     *         The request was rejected because the marker that specifies where pagination should next begin is not
     *         valid.
     * @sample AWSKMS.ListKeys
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListKeys" target="_top">AWS API
     *      Documentation</a>
     */
    ListKeysResult listKeys(ListKeysRequest listKeysRequest);

    /**
     * Simplified method form for invoking the ListKeys operation.
     *
     * @see #listKeys(ListKeysRequest)
     */
    ListKeysResult listKeys();

    /**
     * <p>
     * Returns a list of all tags for the specified customer master key (CMK).
     * </p>
     * 
     * @param listResourceTagsRequest
     * @return Result of the ListResourceTags operation returned by the service.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws InvalidMarkerException
     *         The request was rejected because the marker that specifies where pagination should next begin is not
     *         valid.
     * @sample AWSKMS.ListResourceTags
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListResourceTags" target="_top">AWS API
     *      Documentation</a>
     */
    ListResourceTagsResult listResourceTags(ListResourceTagsRequest listResourceTagsRequest);

    /**
     * <p>
     * Returns a list of all grants for which the grant's <code>RetiringPrincipal</code> matches the one specified.
     * </p>
     * <p>
     * A typical use is to list all grants that you are able to retire. To retire a grant, use <a>RetireGrant</a>.
     * </p>
     * 
     * @param listRetirableGrantsRequest
     * @return Result of the ListRetirableGrants operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidMarkerException
     *         The request was rejected because the marker that specifies where pagination should next begin is not
     *         valid.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @sample AWSKMS.ListRetirableGrants
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ListRetirableGrants" target="_top">AWS API
     *      Documentation</a>
     */
    ListRetirableGrantsResult listRetirableGrants(ListRetirableGrantsRequest listRetirableGrantsRequest);

    /**
     * <p>
     * Attaches a key policy to the specified customer master key (CMK).
     * </p>
     * <p>
     * For more information about key policies, see <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html">Key Policies</a> in the <i>AWS Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @param putKeyPolicyRequest
     * @return Result of the PutKeyPolicy operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws MalformedPolicyDocumentException
     *         The request was rejected because the specified policy is not syntactically or semantically correct.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws UnsupportedOperationException
     *         The request was rejected because a specified parameter is not supported or a specified resource is not
     *         valid for this operation.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.PutKeyPolicy
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/PutKeyPolicy" target="_top">AWS API
     *      Documentation</a>
     */
    PutKeyPolicyResult putKeyPolicy(PutKeyPolicyRequest putKeyPolicyRequest);

    /**
     * <p>
     * Encrypts data on the server side with a new customer master key (CMK) without exposing the plaintext of the data
     * on the client side. The data is first decrypted and then reencrypted. You can also use this operation to change
     * the encryption context of a ciphertext.
     * </p>
     * <p>
     * Unlike other operations, <code>ReEncrypt</code> is authorized twice, once as <code>ReEncryptFrom</code> on the
     * source CMK and once as <code>ReEncryptTo</code> on the destination CMK. We recommend that you include the
     * <code>"kms:ReEncrypt*"</code> permission in your <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html">key policies</a> to permit
     * reencryption from or to the CMK. This permission is automatically included in the key policy when you create a
     * CMK through the console, but you must include it manually when you create a CMK programmatically or when you set
     * a key policy with the <a>PutKeyPolicy</a> operation.
     * </p>
     * 
     * @param reEncryptRequest
     * @return Result of the ReEncrypt operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DisabledException
     *         The request was rejected because the specified CMK is not enabled.
     * @throws InvalidCiphertextException
     *         The request was rejected because the specified ciphertext has been corrupted or is otherwise invalid.
     * @throws KeyUnavailableException
     *         The request was rejected because the specified CMK was not available. The request can be retried.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidKeyUsageException
     *         The request was rejected because the specified <code>KeySpec</code> value is not valid.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.ReEncrypt
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ReEncrypt" target="_top">AWS API
     *      Documentation</a>
     */
    ReEncryptResult reEncrypt(ReEncryptRequest reEncryptRequest);
    /**
     * <p>
     * Retires a grant. To clean up, you can retire a grant when you're done using it. You should revoke a grant when
     * you intend to actively deny operations that depend on it. The following are permitted to call this API:
     * </p>
     * <ul>
     * <li>
     * <p>
     * The AWS account (root user) under which the grant was created
     * </p>
     * </li>
     * <li>
     * <p>
     * The <code>RetiringPrincipal</code>, if present in the grant
     * </p>
     * </li>
     * <li>
     * <p>
     * The <code>GranteePrincipal</code>, if <code>RetireGrant</code> is an operation specified in the grant
     * </p>
     * </li>
     * </ul>
     * <p>
     * You must identify the grant to retire by its grant token or by a combination of the grant ID and the Amazon
     * Resource Name (ARN) of the customer master key (CMK). A grant token is a unique variable-length base64-encoded
     * string. A grant ID is a 64 character unique identifier of a grant. The <a>CreateGrant</a> operation returns both.
     * </p>
     * 
     * @param retireGrantRequest
     * @return Result of the RetireGrant operation returned by the service.
     * @throws InvalidGrantTokenException
     *         The request was rejected because the specified grant token is not valid.
     * @throws InvalidGrantIdException
     *         The request was rejected because the specified <code>GrantId</code> is not valid.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.RetireGrant
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/RetireGrant" target="_top">AWS API
     *      Documentation</a>
     */
    RetireGrantResult retireGrant(RetireGrantRequest retireGrantRequest);

    /**
     * Simplified method form for invoking the RetireGrant operation.
     *
     * @see #retireGrant(RetireGrantRequest)
     */
    RetireGrantResult retireGrant();

    /**
     * <p>
     * Revokes a grant. You can revoke a grant to actively deny operations that depend on it.
     * </p>
     * 
     * @param revokeGrantRequest
     * @return Result of the RevokeGrant operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws InvalidGrantIdException
     *         The request was rejected because the specified <code>GrantId</code> is not valid.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.RevokeGrant
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/RevokeGrant" target="_top">AWS API
     *      Documentation</a>
     */
    RevokeGrantResult revokeGrant(RevokeGrantRequest revokeGrantRequest);

    /**
     * <p>
     * Schedules the deletion of a customer master key (CMK). You may provide a waiting period, specified in days,
     * before deletion occurs. If you do not provide a waiting period, the default period of 30 days is used. When this
     * operation is successful, the state of the CMK changes to <code>PendingDeletion</code>. Before the waiting period
     * ends, you can use <a>CancelKeyDeletion</a> to cancel the deletion of the CMK. After the waiting period ends, AWS
     * KMS deletes the CMK and all AWS KMS data associated with it, including all aliases that refer to it.
     * </p>
     * <important>
     * <p>
     * Deleting a CMK is a destructive and potentially dangerous operation. When a CMK is deleted, all data that was
     * encrypted under the CMK is rendered unrecoverable. To restrict the use of a CMK without deleting it, use
     * <a>DisableKey</a>.
     * </p>
     * </important>
     * <p>
     * For more information about scheduling a CMK for deletion, see <a
     * href="http://docs.aws.amazon.com/kms/latest/developerguide/deleting-keys.html">Deleting Customer Master Keys</a>
     * in the <i>AWS Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param scheduleKeyDeletionRequest
     * @return Result of the ScheduleKeyDeletion operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.ScheduleKeyDeletion
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ScheduleKeyDeletion" target="_top">AWS API
     *      Documentation</a>
     */
    ScheduleKeyDeletionResult scheduleKeyDeletion(ScheduleKeyDeletionRequest scheduleKeyDeletionRequest);

//    IBM Unsupported
//    SignResult sign(SignRequest request);

    /**
     * <p>
     * Adds or overwrites one or more tags for the specified customer master key (CMK).
     * </p>
     * <p>
     * Each tag consists of a tag key and a tag value. Tag keys and tag values are both required, but tag values can be
     * empty (null) strings.
     * </p>
     * <p>
     * You cannot use the same tag key more than once per CMK. For example, consider a CMK with one tag whose tag key is
     * <code>Purpose</code> and tag value is <code>Test</code>. If you send a <code>TagResource</code> request for this
     * CMK with a tag key of <code>Purpose</code> and a tag value of <code>Prod</code>, it does not create a second tag.
     * Instead, the original tag is overwritten with the new tag value.
     * </p>
     * 
     * @param tagResourceRequest
     * @return Result of the TagResource operation returned by the service.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws LimitExceededException
     *         The request was rejected because a limit was exceeded. For more information, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/limits.html">Limits</a> in the <i>AWS Key
     *         Management Service Developer Guide</i>.
     * @throws TagException
     *         The request was rejected because one or more tags are not valid.
     * @sample AWSKMS.TagResource
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/TagResource" target="_top">AWS API
     *      Documentation</a>
     */
    TagResourceResult tagResource(TagResourceRequest tagResourceRequest);

    /**
     * <p>
     * Removes the specified tag or tags from the specified customer master key (CMK).
     * </p>
     * <p>
     * To remove a tag, you specify the tag key for each tag to remove. You do not specify the tag value. To overwrite
     * the tag value for an existing tag, use <a>TagResource</a>.
     * </p>
     * 
     * @param untagResourceRequest
     * @return Result of the UntagResource operation returned by the service.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @throws TagException
     *         The request was rejected because one or more tags are not valid.
     * @sample AWSKMS.UntagResource
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/UntagResource" target="_top">AWS API
     *      Documentation</a>
     */
    UntagResourceResult untagResource(UntagResourceRequest untagResourceRequest);

    /**
     * <p>
     * Updates an alias to map it to a different key.
     * </p>
     * <p>
     * An alias is not a property of a key. Therefore, an alias can be mapped to and unmapped from an existing key
     * without changing the properties of the key.
     * </p>
     * <p>
     * An alias name can contain only alphanumeric characters, forward slashes (/), underscores (_), and dashes (-). An
     * alias must start with the word "alias" followed by a forward slash (alias/). An alias that begins with "aws"
     * after the forward slash (alias/aws...) is reserved by Amazon Web Services (AWS).
     * </p>
     * <p>
     * The alias and the key it is mapped to must be in the same AWS account and the same region.
     * </p>
     * 
     * @param updateAliasRequest
     * @return Result of the UpdateAlias operation returned by the service.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.UpdateAlias
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/UpdateAlias" target="_top">AWS API
     *      Documentation</a>
     */
    UpdateAliasResult updateAlias(UpdateAliasRequest updateAliasRequest);

//    IBM Unsupported
//    UpdateCustomKeyStoreResult updateCustomKeyStore(UpdateCustomKeyStoreRequest request);

    /**
     * <p>
     * Updates the description of a customer master key (CMK).
     * </p>
     * 
     * @param updateKeyDescriptionRequest
     * @return Result of the UpdateKeyDescription operation returned by the service.
     * @throws NotFoundException
     *         The request was rejected because the specified entity or resource could not be found.
     * @throws InvalidArnException
     *         The request was rejected because a specified ARN was not valid.
     * @throws DependencyTimeoutException
     *         The system timed out while trying to fulfill the request. The request can be retried.
     * @throws KMSInternalException
     *         The request was rejected because an internal exception occurred. The request can be retried.
     * @throws KMSInvalidStateException
     *         The request was rejected because the state of the specified resource is not valid for this request.</p>
     *         <p>
     *         For more information about how key state affects the use of a CMK, see <a
     *         href="http://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">How Key State Affects Use of a
     *         Customer Master Key</a> in the <i>AWS Key Management Service Developer Guide</i>.
     * @sample AWSKMS.UpdateKeyDescription
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/UpdateKeyDescription" target="_top">AWS API
     *      Documentation</a>
     */
    UpdateKeyDescriptionResult updateKeyDescription(UpdateKeyDescriptionRequest updateKeyDescriptionRequest);

    /**
     * Shuts down this client object, releasing any resources that might be held open. This is an optional method, and
     * callers are not expected to call it, but can if they want to explicitly release any open resources. Once a client
     * has been shutdown, it should not be used to make any more requests.
     */
    void shutdown();

//    IBM Unsupported
//    UpdatePrimaryRegionResult updatePrimaryRegion(UpdatePrimaryRegionRequest request);
//    VerifyResult verify(VerifyRequest request);
//    VerifyMacResult verifyMac(VerifyMacRequest request);

    /**
     * Returns additional metadata for a previously executed successful request, typically used for debugging issues
     * where a service isn't acting as expected. This data isn't considered part of the result data returned by an
     * operation, so it's available through this separate, diagnostic interface.
     * <p>
     * Response metadata is only cached for a limited period of time, so if you need to access this extra diagnostic
     * information for an executed request, you should use this method to retrieve it as soon as possible after
     * executing a request.
     *
     * @param request
     *        The originally executed request.
     *
     * @return The response metadata for the specified request, or null if none is available.
     */
    ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request);

}
