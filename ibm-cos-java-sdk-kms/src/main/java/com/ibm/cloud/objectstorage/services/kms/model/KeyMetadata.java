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
package com.ibm.cloud.objectstorage.services.kms.model;

import java.io.Serializable;
import javax.annotation.Generated;
import com.ibm.cloud.objectstorage.protocol.StructuredPojo;
import com.ibm.cloud.objectstorage.protocol.ProtocolMarshaller;

/**
 * <p>
 * Contains metadata about a KMS key.
 * </p>
 * <p>
 * This data type is used as a response element for the <a>CreateKey</a>, <a>DescribeKey</a>, and <a>ReplicateKey</a>
 * operations.
 * </p>
 * 
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/KeyMetadata" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class KeyMetadata implements Serializable, Cloneable, StructuredPojo {

    /**
     * <p>
     * The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     * </p>
     */
    private String aWSAccountId;
    /**
     * <p>
     * The globally unique identifier for the KMS key.
     * </p>
     */
    private String keyId;
    /**
     * <p>
     * The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key Management
     * Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General Reference</i>.
     * </p>
     */
    private String arn;
    /**
     * <p>
     * The date and time when the KMS key was created.
     * </p>
     */
    private java.util.Date creationDate;
    /**
     * <p>
     * Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is true,
     * otherwise it is false.
     * </p>
     */
    private Boolean enabled;
    /**
     * <p>
     * The description of the KMS key.
     * </p>
     */
    private String description;
    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     */
    private String keyUsage;
    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private String keyState;
    /**
     * <p>
     * The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is scheduled
     * for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.
     * </p>
     * <p>
     * When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key state is
     * <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     * <code>PendingDeletionWindowInDays</code> field.
     * </p>
     */
    private java.util.Date deletionDate;
    /**
     * <p>
     * The time at which the imported key material expires. When the key material expires, KMS deletes the key material
     * and the KMS key becomes unusable. This value is present only for KMS keys whose <code>Origin</code> is
     * <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is <code>KEY_MATERIAL_EXPIRES</code>, otherwise this
     * value is omitted.
     * </p>
     */
    private java.util.Date validTo;
    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     */
    private String origin;
    /**
     * <p>
     * A unique identifier for the <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>
     * that contains the KMS key. This field is present only when the KMS key is created in a custom key store.
     * </p>
     */
    //IBM unsupported
    //private String customKeyStoreId;
    /**
     * <p>
     * The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a KMS key
     * in an CloudHSM <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>,
     * KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is present only when
     * the KMS key is created in an CloudHSM key store.
     * </p>
     */
    //IBM unsupported
    //private String cloudHsmClusterId;
    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     */
    private String expirationModel;
    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     */
    //IBM unsupported
    //private String keyManager;
    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     */
    //IBM unsupported
    // @Deprecated
    // private String customerMasterKeySpec;
    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     */
    //IBM unsupported
    //private String keySpec;
    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     */
    //IBM unsupported
    //private com.ibm.cloud.objectstorage.internal.SdkInternalList<String> encryptionAlgorithms;
    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     */
    //IBM unsupported
    //private com.ibm.cloud.objectstorage.internal.SdkInternalList<String> signingAlgorithms;
    /**
     * <p>
     * The key agreement algorithm used to derive a shared secret.
     * </p>
     */
    //IBM Unsupported
    //private com.ibm.cloud.objectstorage.internal.SdkInternalList<String> keyAgreementAlgorithms;
    /**
     * <p>
     * Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key. This
     * value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for regional KMS
     * keys.
     * </p>
     * <p>
     * For more information about multi-Region keys, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region keys in
     * KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    //IBM unsupported
    //private Boolean multiRegion;
    /**
     * <p>
     * Lists the primary and replica keys in same multi-Region key. This field is present only when the value of the
     * <code>MultiRegion</code> field is <code>True</code>.
     * </p>
     * <p>
     * For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or <code>REPLICA</code>
     * key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the current KMS
     * key if it is the primary key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the current
     * KMS key if it is a replica key.
     * </p>
     * </li>
     * </ul>
     */
    //IBM unsupported
    //private MultiRegionConfiguration multiRegionConfiguration;
    /**
     * <p>
     * The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins when the
     * last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of the KMS key is
     * <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in a multi-Region key, it
     * is scheduled for deletion, and it still has existing replica keys.
     * </p>
     * <p>
     * When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     * displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     * scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This value
     * displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     * <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     * <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     * </p>
     */
    //IBM unsupported
    //private Integer pendingDeletionWindowInDays;
    /**
     * <p>
     * Information about the external key that is associated with a KMS key in an external key store.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key">External
     * key</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
//    private XksKeyConfigurationType xksKeyConfiguration;

    /**
     * <p>
     * The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     * </p>
     * 
     * @param aWSAccountId
     *        The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     */

    public void setAWSAccountId(String aWSAccountId) {
        this.aWSAccountId = aWSAccountId;
    }

    /**
     * <p>
     * The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     * </p>
     * 
     * @return The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     */

    public String getAWSAccountId() {
        return this.aWSAccountId;
    }

    /**
     * <p>
     * The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     * </p>
     * 
     * @param aWSAccountId
     *        The twelve-digit account ID of the Amazon Web Services account that owns the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withAWSAccountId(String aWSAccountId) {
        setAWSAccountId(aWSAccountId);
        return this;
    }

    /**
     * <p>
     * The globally unique identifier for the KMS key.
     * </p>
     * 
     * @param keyId
     *        The globally unique identifier for the KMS key.
     */

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * <p>
     * The globally unique identifier for the KMS key.
     * </p>
     * 
     * @return The globally unique identifier for the KMS key.
     */

    public String getKeyId() {
        return this.keyId;
    }

    /**
     * <p>
     * The globally unique identifier for the KMS key.
     * </p>
     * 
     * @param keyId
     *        The globally unique identifier for the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key Management
     * Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General Reference</i>.
     * </p>
     * 
     * @param arn
     *        The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     *        href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key
     *        Management Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General
     *        Reference</i>.
     */

    public void setArn(String arn) {
        this.arn = arn;
    }

    /**
     * <p>
     * The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key Management
     * Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General Reference</i>.
     * </p>
     * 
     * @return The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     *         href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key
     *         Management Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General
     *         Reference</i>.
     */

    public String getArn() {
        return this.arn;
    }

    /**
     * <p>
     * The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key Management
     * Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General Reference</i>.
     * </p>
     * 
     * @param arn
     *        The Amazon Resource Name (ARN) of the KMS key. For examples, see <a
     *        href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html#arn-syntax-kms">Key
     *        Management Service (KMS)</a> in the Example ARNs section of the <i>Amazon Web Services General
     *        Reference</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withArn(String arn) {
        setArn(arn);
        return this;
    }

    /**
     * <p>
     * The date and time when the KMS key was created.
     * </p>
     * 
     * @param creationDate
     *        The date and time when the KMS key was created.
     */

    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * <p>
     * The date and time when the KMS key was created.
     * </p>
     * 
     * @return The date and time when the KMS key was created.
     */

    public java.util.Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * <p>
     * The date and time when the KMS key was created.
     * </p>
     * 
     * @param creationDate
     *        The date and time when the KMS key was created.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withCreationDate(java.util.Date creationDate) {
        setCreationDate(creationDate);
        return this;
    }

    /**
     * <p>
     * Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is true,
     * otherwise it is false.
     * </p>
     * 
     * @param enabled
     *        Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is
     *        true, otherwise it is false.
     */

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * <p>
     * Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is true,
     * otherwise it is false.
     * </p>
     * 
     * @return Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value
     *         is true, otherwise it is false.
     */

    public Boolean getEnabled() {
        return this.enabled;
    }

    /**
     * <p>
     * Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is true,
     * otherwise it is false.
     * </p>
     * 
     * @param enabled
     *        Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is
     *        true, otherwise it is false.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    /**
     * <p>
     * Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value is true,
     * otherwise it is false.
     * </p>
     * 
     * @return Specifies whether the KMS key is enabled. When <code>KeyState</code> is <code>Enabled</code> this value
     *         is true, otherwise it is false.
     */

    public Boolean isEnabled() {
        return this.enabled;
    }

    /**
     * <p>
     * The description of the KMS key.
     * </p>
     * 
     * @param description
     *        The description of the KMS key.
     */

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     * The description of the KMS key.
     * </p>
     * 
     * @return The description of the KMS key.
     */

    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * The description of the KMS key.
     * </p>
     * 
     * @param description
     *        The description of the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withDescription(String description) {
        setDescription(description);
        return this;
    }

    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     * 
     * @param keyUsage
     *        The <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     *        cryptographic operations</a> for which you can use the KMS key.
     * @see KeyUsageType
     */

    public void setKeyUsage(String keyUsage) {
        this.keyUsage = keyUsage;
    }

    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     * 
     * @return The <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations"
     *         >cryptographic operations</a> for which you can use the KMS key.
     * @see KeyUsageType
     */

    public String getKeyUsage() {
        return this.keyUsage;
    }

    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     * 
     * @param keyUsage
     *        The <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     *        cryptographic operations</a> for which you can use the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyUsageType
     */

    public KeyMetadata withKeyUsage(String keyUsage) {
        setKeyUsage(keyUsage);
        return this;
    }

    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     * 
     * @param keyUsage
     *        The <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     *        cryptographic operations</a> for which you can use the KMS key.
     * @see KeyUsageType
     */

    public void setKeyUsage(KeyUsageType keyUsage) {
        withKeyUsage(keyUsage);
    }

    /**
     * <p>
     * The <a href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     * cryptographic operations</a> for which you can use the KMS key.
     * </p>
     * 
     * @param keyUsage
     *        The <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#cryptographic-operations">
     *        cryptographic operations</a> for which you can use the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyUsageType
     */

    public KeyMetadata withKeyUsage(KeyUsageType keyUsage) {
        this.keyUsage = keyUsage.toString();
        return this;
    }

    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyState
     *        The current status of the KMS key.</p>
     *        <p>
     *        For more information about how key state affects the use of a KMS key, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *        the <i>Key Management Service Developer Guide</i>.
     * @see KeyState
     */

    public void setKeyState(String keyState) {
        this.keyState = keyState;
    }

    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return The current status of the KMS key.</p>
     *         <p>
     *         For more information about how key state affects the use of a KMS key, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *         the <i>Key Management Service Developer Guide</i>.
     * @see KeyState
     */

    public String getKeyState() {
        return this.keyState;
    }

    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyState
     *        The current status of the KMS key.</p>
     *        <p>
     *        For more information about how key state affects the use of a KMS key, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *        the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyState
     */

    public KeyMetadata withKeyState(String keyState) {
        setKeyState(keyState);
        return this;
    }

    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyState
     *        The current status of the KMS key.</p>
     *        <p>
     *        For more information about how key state affects the use of a KMS key, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *        the <i>Key Management Service Developer Guide</i>.
     * @see KeyState
     */

    public void setKeyState(KeyState keyState) {
        withKeyState(keyState);
    }

    /**
     * <p>
     * The current status of the KMS key.
     * </p>
     * <p>
     * For more information about how key state affects the use of a KMS key, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in the
     * <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyState
     *        The current status of the KMS key.</p>
     *        <p>
     *        For more information about how key state affects the use of a KMS key, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/key-state.html">Key states of KMS keys</a> in
     *        the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyState
     */

    public KeyMetadata withKeyState(KeyState keyState) {
        this.keyState = keyState.toString();
        return this;
    }

    /**
     * <p>
     * The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is scheduled
     * for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.
     * </p>
     * <p>
     * When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key state is
     * <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     * <code>PendingDeletionWindowInDays</code> field.
     * </p>
     * 
     * @param deletionDate
     *        The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is
     *        scheduled for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.</p>
     *        <p>
     *        When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key
     *        state is <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     *        <code>PendingDeletionWindowInDays</code> field.
     */

    public void setDeletionDate(java.util.Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    /**
     * <p>
     * The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is scheduled
     * for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.
     * </p>
     * <p>
     * When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key state is
     * <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     * <code>PendingDeletionWindowInDays</code> field.
     * </p>
     * 
     * @return The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is
     *         scheduled for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.</p>
     *         <p>
     *         When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key
     *         state is <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     *         <code>PendingDeletionWindowInDays</code> field.
     */

    public java.util.Date getDeletionDate() {
        return this.deletionDate;
    }

    /**
     * <p>
     * The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is scheduled
     * for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.
     * </p>
     * <p>
     * When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key state is
     * <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     * <code>PendingDeletionWindowInDays</code> field.
     * </p>
     * 
     * @param deletionDate
     *        The date and time after which KMS deletes this KMS key. This value is present only when the KMS key is
     *        scheduled for deletion, that is, when its <code>KeyState</code> is <code>PendingDeletion</code>.</p>
     *        <p>
     *        When the primary key in a multi-Region key is scheduled for deletion but still has replica keys, its key
     *        state is <code>PendingReplicaDeletion</code> and the length of its waiting period is displayed in the
     *        <code>PendingDeletionWindowInDays</code> field.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withDeletionDate(java.util.Date deletionDate) {
        setDeletionDate(deletionDate);
        return this;
    }

    /**
     * <p>
     * The time at which the imported key material expires. When the key material expires, KMS deletes the key material
     * and the KMS key becomes unusable. This value is present only for KMS keys whose <code>Origin</code> is
     * <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is <code>KEY_MATERIAL_EXPIRES</code>, otherwise this
     * value is omitted.
     * </p>
     * 
     * @param validTo
     *        The time at which the imported key material expires. When the key material expires, KMS deletes the key
     *        material and the KMS key becomes unusable. This value is present only for KMS keys whose
     *        <code>Origin</code> is <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is
     *        <code>KEY_MATERIAL_EXPIRES</code>, otherwise this value is omitted.
     */

    public void setValidTo(java.util.Date validTo) {
        this.validTo = validTo;
    }

    /**
     * <p>
     * The time at which the imported key material expires. When the key material expires, KMS deletes the key material
     * and the KMS key becomes unusable. This value is present only for KMS keys whose <code>Origin</code> is
     * <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is <code>KEY_MATERIAL_EXPIRES</code>, otherwise this
     * value is omitted.
     * </p>
     * 
     * @return The time at which the imported key material expires. When the key material expires, KMS deletes the key
     *         material and the KMS key becomes unusable. This value is present only for KMS keys whose
     *         <code>Origin</code> is <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is
     *         <code>KEY_MATERIAL_EXPIRES</code>, otherwise this value is omitted.
     */

    public java.util.Date getValidTo() {
        return this.validTo;
    }

    /**
     * <p>
     * The time at which the imported key material expires. When the key material expires, KMS deletes the key material
     * and the KMS key becomes unusable. This value is present only for KMS keys whose <code>Origin</code> is
     * <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is <code>KEY_MATERIAL_EXPIRES</code>, otherwise this
     * value is omitted.
     * </p>
     * 
     * @param validTo
     *        The time at which the imported key material expires. When the key material expires, KMS deletes the key
     *        material and the KMS key becomes unusable. This value is present only for KMS keys whose
     *        <code>Origin</code> is <code>EXTERNAL</code> and whose <code>ExpirationModel</code> is
     *        <code>KEY_MATERIAL_EXPIRES</code>, otherwise this value is omitted.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public KeyMetadata withValidTo(java.util.Date validTo) {
        setValidTo(validTo);
        return this;
    }

    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     * 
     * @param origin
     *        The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the
     *        key material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key
     *        doesn't have any key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created
     *        in the CloudHSM cluster associated with a custom key store.
     * @see OriginType
     */

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     * 
     * @return The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the
     *         key material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key
     *         doesn't have any key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created
     *         in the CloudHSM cluster associated with a custom key store.
     * @see OriginType
     */

    public String getOrigin() {
        return this.origin;
    }

    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     * 
     * @param origin
     *        The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the
     *        key material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key
     *        doesn't have any key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created
     *        in the CloudHSM cluster associated with a custom key store.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see OriginType
     */

    public KeyMetadata withOrigin(String origin) {
        setOrigin(origin);
        return this;
    }

    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     * 
     * @param origin
     *        The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the
     *        key material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key
     *        doesn't have any key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created
     *        in the CloudHSM cluster associated with a custom key store.
     * @see OriginType
     */

    public void setOrigin(OriginType origin) {
        withOrigin(origin);
    }

    /**
     * <p>
     * The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the key
     * material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key doesn't have any
     * key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created in the CloudHSM cluster
     * associated with a custom key store.
     * </p>
     * 
     * @param origin
     *        The source of the key material for the KMS key. When this value is <code>AWS_KMS</code>, KMS created the
     *        key material. When this value is <code>EXTERNAL</code>, the key material was imported or the KMS key
     *        doesn't have any key material. When this value is <code>AWS_CLOUDHSM</code>, the key material was created
     *        in the CloudHSM cluster associated with a custom key store.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see OriginType
     */

    public KeyMetadata withOrigin(OriginType origin) {
        this.origin = origin.toString();
        return this;
    }

    /**
     * <p>
     * A unique identifier for the <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>
     * that contains the KMS key. This field is present only when the KMS key is created in a custom key store.
     * </p>
     * 
     * @param customKeyStoreId
     *        A unique identifier for the <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *        store</a> that contains the KMS key. This field is present only when the KMS key is created in a custom
     *        key store.
     */

    //IBM unsupported
    // public void setCustomKeyStoreId(String customKeyStoreId) {
    //     this.customKeyStoreId = customKeyStoreId;
    // }

    /**
     * <p>
     * A unique identifier for the <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>
     * that contains the KMS key. This field is present only when the KMS key is created in a custom key store.
     * </p>
     * 
     * @return A unique identifier for the <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *         store</a> that contains the KMS key. This field is present only when the KMS key is created in a custom
     *         key store.
     */

    //IBM unsupported
    // public String getCustomKeyStoreId() {
    //     return this.customKeyStoreId;
    // }

    /**
     * <p>
     * A unique identifier for the <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>
     * that contains the KMS key. This field is present only when the KMS key is created in a custom key store.
     * </p>
     * 
     * @param customKeyStoreId
     *        A unique identifier for the <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *        store</a> that contains the KMS key. This field is present only when the KMS key is created in a custom
     *        key store.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    //IBM unsupported
    // public KeyMetadata withCustomKeyStoreId(String customKeyStoreId) {
    //     setCustomKeyStoreId(customKeyStoreId);
    //     return this;
    // }

    /**
     * <p>
     * The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a KMS key
     * in an CloudHSM <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>,
     * KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is present only when
     * the KMS key is created in an CloudHSM key store.
     * </p>
     * 
     * @param cloudHsmClusterId
     *        The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a
     *        KMS key in an CloudHSM <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *        store</a>, KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is
     *        present only when the KMS key is created in an CloudHSM key store.
     */
    //IBM unsupported
    // public void setCloudHsmClusterId(String cloudHsmClusterId) {
    //     this.cloudHsmClusterId = cloudHsmClusterId;
    // }

    /**
     * <p>
     * The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a KMS key
     * in an CloudHSM <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>,
     * KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is present only when
     * the KMS key is created in an CloudHSM key store.
     * </p>
     * 
     * @return The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a
     *         KMS key in an CloudHSM <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *         store</a>, KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is
     *         present only when the KMS key is created in an CloudHSM key store.
     */

    //IBM unsupported
    // public String getCloudHsmClusterId() {
    //     return this.cloudHsmClusterId;
    // }

    /**
     * <p>
     * The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a KMS key
     * in an CloudHSM <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key store</a>,
     * KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is present only when
     * the KMS key is created in an CloudHSM key store.
     * </p>
     * 
     * @param cloudHsmClusterId
     *        The cluster ID of the CloudHSM cluster that contains the key material for the KMS key. When you create a
     *        KMS key in an CloudHSM <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/custom-key-store-overview.html">custom key
     *        store</a>, KMS creates the key material for the KMS key in the associated CloudHSM cluster. This field is
     *        present only when the KMS key is created in an CloudHSM key store.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    //IBM unsupported
    // public KeyMetadata withCloudHsmClusterId(String cloudHsmClusterId) {
    //     setCloudHsmClusterId(cloudHsmClusterId);
    //     return this;
    // }

    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     * 
     * @param expirationModel
     *        Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code>
     *        is <code>EXTERNAL</code>, otherwise this value is omitted.
     * @see ExpirationModelType
     */

    public void setExpirationModel(String expirationModel) {
        this.expirationModel = expirationModel;
    }

    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     * 
     * @return Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code>
     *         is <code>EXTERNAL</code>, otherwise this value is omitted.
     * @see ExpirationModelType
     */

    public String getExpirationModel() {
        return this.expirationModel;
    }

    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     * 
     * @param expirationModel
     *        Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code>
     *        is <code>EXTERNAL</code>, otherwise this value is omitted.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see ExpirationModelType
     */

    public KeyMetadata withExpirationModel(String expirationModel) {
        setExpirationModel(expirationModel);
        return this;
    }

    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     * 
     * @param expirationModel
     *        Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code>
     *        is <code>EXTERNAL</code>, otherwise this value is omitted.
     * @see ExpirationModelType
     */

    public void setExpirationModel(ExpirationModelType expirationModel) {
        withExpirationModel(expirationModel);
    }

    /**
     * <p>
     * Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code> is
     * <code>EXTERNAL</code>, otherwise this value is omitted.
     * </p>
     * 
     * @param expirationModel
     *        Specifies whether the KMS key's key material expires. This value is present only when <code>Origin</code>
     *        is <code>EXTERNAL</code>, otherwise this value is omitted.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see ExpirationModelType
     */

    public KeyMetadata withExpirationModel(ExpirationModelType expirationModel) {
        this.expirationModel = expirationModel.toString();
        return this;
    }

    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyManager
     *        The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or
     *        Amazon Web Services managed. For more information about the difference, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the
     *        <i>Key Management Service Developer Guide</i>.
     * @see KeyManagerType
     */

    //IBM unsupported
    // public void setKeyManager(String keyManager) {
    //     this.keyManager = keyManager;
    // }

    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @return The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or
     *         Amazon Web Services managed. For more information about the difference, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the
     *         <i>Key Management Service Developer Guide</i>.
     * @see KeyManagerType
     */

    //IBM unsupported
    // public String getKeyManager() {
    //     return this.keyManager;
    // }

    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyManager
     *        The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or
     *        Amazon Web Services managed. For more information about the difference, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the
     *        <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyManagerType
     */

    //IBM unsupported
    // public KeyMetadata withKeyManager(String keyManager) {
    //     setKeyManager(keyManager);
    //     return this;
    // }

    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyManager
     *        The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or
     *        Amazon Web Services managed. For more information about the difference, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the
     *        <i>Key Management Service Developer Guide</i>.
     * @see KeyManagerType
     */
    //IBM unsupported
    // public void setKeyManager(KeyManagerType keyManager) {
    //     withKeyManager(keyManager);
    // }

    /**
     * <p>
     * The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or Amazon
     * Web Services managed. For more information about the difference, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the <i>Key
     * Management Service Developer Guide</i>.
     * </p>
     * 
     * @param keyManager
     *        The manager of the KMS key. KMS keys in your Amazon Web Services account are either customer managed or
     *        Amazon Web Services managed. For more information about the difference, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#kms_keys">KMS keys</a> in the
     *        <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeyManagerType
     */
    //IBM unsupported
    //  public KeyMetadata withKeyManager(KeyManagerType keyManager) {
    //      this.keyManager = keyManager.toString();
    //      return this;
    //  }

    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     * 
     * @param customerMasterKeySpec
     *        Instead, use the <code>KeySpec</code> field.</p>
     *        <p>
     *        The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend
     *        that you use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports
     *        both fields.
     * @see CustomerMasterKeySpec
     */
    //IBM unsupported
    // @Deprecated
    // public void setCustomerMasterKeySpec(String customerMasterKeySpec) {
    //     this.customerMasterKeySpec = customerMasterKeySpec;
    // }

    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     * 
     * @return Instead, use the <code>KeySpec</code> field.</p>
     *         <p>
     *         The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend
     *         that you use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS
     *         supports both fields.
     * @see CustomerMasterKeySpec
     */
    //IBM unsupported
    // @Deprecated
    // public String getCustomerMasterKeySpec() {
    //     return this.customerMasterKeySpec;
    // }

    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     * 
     * @param customerMasterKeySpec
     *        Instead, use the <code>KeySpec</code> field.</p>
     *        <p>
     *        The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend
     *        that you use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports
     *        both fields.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see CustomerMasterKeySpec
     */
    //IBM unsupported
    // @Deprecated
    // public KeyMetadata withCustomerMasterKeySpec(String customerMasterKeySpec) {
    //     setCustomerMasterKeySpec(customerMasterKeySpec);
    //     return this;
    // }

    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     * 
     * @param customerMasterKeySpec
     *        Instead, use the <code>KeySpec</code> field.</p>
     *        <p>
     *        The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend
     *        that you use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports
     *        both fields.
     * @see CustomerMasterKeySpec
     */
    //IBM unsupported
    // @Deprecated
    // public void setCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
    //     withCustomerMasterKeySpec(customerMasterKeySpec);
    // }

    /**
     * <p>
     * Instead, use the <code>KeySpec</code> field.
     * </p>
     * <p>
     * The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend that you
     * use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports both fields.
     * </p>
     * 
     * @param customerMasterKeySpec
     *        Instead, use the <code>KeySpec</code> field.</p>
     *        <p>
     *        The <code>KeySpec</code> and <code>CustomerMasterKeySpec</code> fields have the same value. We recommend
     *        that you use the <code>KeySpec</code> field in your code. However, to avoid breaking changes, KMS supports
     *        both fields.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see CustomerMasterKeySpec
     */
    //IBM unsupported
    // @Deprecated
    // public KeyMetadata withCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
    //     this.customerMasterKeySpec = customerMasterKeySpec.toString();
    //     return this;
    // }

    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     * 
     * @param keySpec
     *        Describes the type of key material in the KMS key.
     * @see KeySpec
     */

    //IBM unsupported
    // public void setKeySpec(String keySpec) {
    //     this.keySpec = keySpec;
    // }

    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     * 
     * @return Describes the type of key material in the KMS key.
     * @see KeySpec
     */

    //IBM unsupported
    // public String getKeySpec() {
    //     return this.keySpec;
    // }

    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     * 
     * @param keySpec
     *        Describes the type of key material in the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeySpec
     */

    //IBM unsupported
    // public KeyMetadata withKeySpec(String keySpec) {
    //     setKeySpec(keySpec);
    //     return this;
    // }

    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     * 
     * @param keySpec
     *        Describes the type of key material in the KMS key.
     * @see KeySpec
     */
    //IBM unsupported
    // public void setKeySpec(KeySpec keySpec) {
    //     withKeySpec(keySpec);
    // }

    /**
     * <p>
     * Describes the type of key material in the KMS key.
     * </p>
     * 
     * @param keySpec
     *        Describes the type of key material in the KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see KeySpec
     */
    //IBM unsupported
    // public KeyMetadata withKeySpec(KeySpec keySpec) {
    //     this.keySpec = keySpec.toString();
    //     return this;
    // }

    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     * 
     * @return The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption
     *         algorithms within KMS.</p>
     *         <p>
     *         This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * @see EncryptionAlgorithmSpec
     */

    //IBM unsupported
    // public java.util.List<String> getEncryptionAlgorithms() {
    //     if (encryptionAlgorithms == null) {
    //         encryptionAlgorithms = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>();
    //     }
    //     return encryptionAlgorithms;
    // }

    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     * 
     * @param encryptionAlgorithms
     *        The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption
     *        algorithms within KMS.</p>
     *        <p>
     *        This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * @see EncryptionAlgorithmSpec
     */

    //IBM unsupported
    // public void setEncryptionAlgorithms(java.util.Collection<String> encryptionAlgorithms) {
    //     if (encryptionAlgorithms == null) {
    //         this.encryptionAlgorithms = null;
    //         return;
    //     }

    //     this.encryptionAlgorithms = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(encryptionAlgorithms);
    // }

    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     * <p>
     * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
     * {@link #setEncryptionAlgorithms(java.util.Collection)} or {@link #withEncryptionAlgorithms(java.util.Collection)}
     * if you want to override the existing values.
     * </p>
     * 
     * @param encryptionAlgorithms
     *        The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption
     *        algorithms within KMS.</p>
     *        <p>
     *        This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    //IBM unsupported
    // public KeyMetadata withEncryptionAlgorithms(String... encryptionAlgorithms) {
    //     if (this.encryptionAlgorithms == null) {
    //         setEncryptionAlgorithms(new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(encryptionAlgorithms.length));
    //     }
    //     for (String ele : encryptionAlgorithms) {
    //         this.encryptionAlgorithms.add(ele);
    //     }
    //     return this;
    // }

    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     * 
     * @param encryptionAlgorithms
     *        The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption
     *        algorithms within KMS.</p>
     *        <p>
     *        This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    //IBM unsupported
    // public KeyMetadata withEncryptionAlgorithms(java.util.Collection<String> encryptionAlgorithms) {
    //     setEncryptionAlgorithms(encryptionAlgorithms);
    //     return this;
    // }

    /**
     * <p>
     * The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption algorithms
     * within KMS.
     * </p>
     * <p>
     * This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * </p>
     * 
     * @param encryptionAlgorithms
     *        The encryption algorithms that the KMS key supports. You cannot use the KMS key with other encryption
     *        algorithms within KMS.</p>
     *        <p>
     *        This value is present only when the <code>KeyUsage</code> of the KMS key is <code>ENCRYPT_DECRYPT</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */
    //IBM unsupported
    // public KeyMetadata withEncryptionAlgorithms(EncryptionAlgorithmSpec... encryptionAlgorithms) {
    //     com.ibm.cloud.objectstorage.internal.SdkInternalList<String> encryptionAlgorithmsCopy = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(
    //             encryptionAlgorithms.length);
    //     for (EncryptionAlgorithmSpec value : encryptionAlgorithms) {
    //         encryptionAlgorithmsCopy.add(value.toString());
    //     }
    //     if (getEncryptionAlgorithms() == null) {
    //         setEncryptionAlgorithms(encryptionAlgorithmsCopy);
    //     } else {
    //         getEncryptionAlgorithms().addAll(encryptionAlgorithmsCopy);
    //     }
    //     return this;
    // }

    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     * 
     * @return The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing
     *         algorithms within KMS.</p>
     *         <p>
     *         This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * @see SigningAlgorithmSpec
     */

    //IBM unsupported
    // public java.util.List<String> getSigningAlgorithms() {
    //     if (signingAlgorithms == null) {
    //         signingAlgorithms = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>();
    //     }
    //     return signingAlgorithms;
    // }

    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     * 
     * @param signingAlgorithms
     *        The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms
     *        within KMS.</p>
     *        <p>
     *        This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * @see SigningAlgorithmSpec
     */

    //IBM unsupported
    //  public void setSigningAlgorithms(java.util.Collection<String> signingAlgorithms) {
    //     if (signingAlgorithms == null) {
    //         this.signingAlgorithms = null;
    //         return;
    //     }

    //     this.signingAlgorithms = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(signingAlgorithms);
    // }

    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     * <p>
     * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
     * {@link #setSigningAlgorithms(java.util.Collection)} or {@link #withSigningAlgorithms(java.util.Collection)} if
     * you want to override the existing values.
     * </p>
     * 
     * @param signingAlgorithms
     *        The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms
     *        within KMS.</p>
     *        <p>
     *        This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see SigningAlgorithmSpec
     */

    //IBM unsupported
    // public KeyMetadata withSigningAlgorithms(String... signingAlgorithms) {
    //     if (this.signingAlgorithms == null) {
    //         setSigningAlgorithms(new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(signingAlgorithms.length));
    //     }
    //     for (String ele : signingAlgorithms) {
    //         this.signingAlgorithms.add(ele);
    //     }
    //     return this;
    // }

    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     * 
     * @param signingAlgorithms
     *        The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms
     *        within KMS.</p>
     *        <p>
     *        This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see SigningAlgorithmSpec
     */

    //IBM unsupported
    // public KeyMetadata withSigningAlgorithms(java.util.Collection<String> signingAlgorithms) {
    //     setSigningAlgorithms(signingAlgorithms);
    //     return this;
    // }

    /**
     * <p>
     * The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms within
     * KMS.
     * </p>
     * <p>
     * This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * </p>
     * 
     * @param signingAlgorithms
     *        The signing algorithms that the KMS key supports. You cannot use the KMS key with other signing algorithms
     *        within KMS.</p>
     *        <p>
     *        This field appears only when the <code>KeyUsage</code> of the KMS key is <code>SIGN_VERIFY</code>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see SigningAlgorithmSpec
     */
    //IBM unsupported
    // public KeyMetadata withSigningAlgorithms(SigningAlgorithmSpec... signingAlgorithms) {
    //     com.ibm.cloud.objectstorage.internal.SdkInternalList<String> signingAlgorithmsCopy = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(signingAlgorithms.length);
    //     for (SigningAlgorithmSpec value : signingAlgorithms) {
    //         signingAlgorithmsCopy.add(value.toString());
    //     }
    //     if (getSigningAlgorithms() == null) {
    //         setSigningAlgorithms(signingAlgorithmsCopy);
    //     } else {
    //         getSigningAlgorithms().addAll(signingAlgorithmsCopy);
	//	   }
	//     return this;
	// }

    /**
     * <p>
     * The key agreement algorithm used to derive a shared secret.
     * </p>
     * 
     * @return The key agreement algorithm used to derive a shared secret.
     * @see KeyAgreementAlgorithmSpec
     */
// IBM Unsupported
//    public java.util.List<String> getKeyAgreementAlgorithms() {
//        if (keyAgreementAlgorithms == null) {
//            keyAgreementAlgorithms = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>();
//        }
//        return keyAgreementAlgorithms;
//    }

    /**
     * <p>
     * Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key. This
     * value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for regional KMS
     * keys.
     * </p>
     * <p>
     * For more information about multi-Region keys, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region keys in
     * KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param multiRegion
     *        Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key.
     *        This value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for
     *        regional KMS keys.</p>
     *        <p>
     *        For more information about multi-Region keys, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region
     *        keys in KMS</a> in the <i>Key Management Service Developer Guide</i>.
     */

    //IBM unsupported
    // public void setMultiRegion(Boolean multiRegion) {
    //     this.multiRegion = multiRegion;
    // }

    /**
     * <p>
     * Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key. This
     * value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for regional KMS
     * keys.
     * </p>
     * <p>
     * For more information about multi-Region keys, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region keys in
     * KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key.
     *         This value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for
     *         regional KMS keys.</p>
     *         <p>
     *         For more information about multi-Region keys, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region
     *         keys in KMS</a> in the <i>Key Management Service Developer Guide</i>.
     */

    //IBM unsupported
    // public Boolean getMultiRegion() {
    //     return this.multiRegion;
    // }

    /**
     * <p>
     * Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key. This
     * value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for regional KMS
     * keys.
     * </p>
     * <p>
     * For more information about multi-Region keys, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region keys in
     * KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param multiRegion
     *        Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key.
     *        This value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for
     *        regional KMS keys.</p>
     *        <p>
     *        For more information about multi-Region keys, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region
     *        keys in KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    //IBM unsupported
    // public KeyMetadata withMultiRegion(Boolean multiRegion) {
    //     setMultiRegion(multiRegion);
    //     return this;
    // }

    /**
     * <p>
     * Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key. This
     * value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for regional KMS
     * keys.
     * </p>
     * <p>
     * For more information about multi-Region keys, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region keys in
     * KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Indicates whether the KMS key is a multi-Region (<code>True</code>) or regional (<code>False</code>) key.
     *         This value is <code>True</code> for multi-Region primary and replica keys and <code>False</code> for
     *         regional KMS keys.</p>
     *         <p>
     *         For more information about multi-Region keys, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/multi-region-keys-overview.html">Multi-Region
     *         keys in KMS</a> in the <i>Key Management Service Developer Guide</i>.
     */

    //IBM unsupported
    // public Boolean isMultiRegion() {
    //     return this.multiRegion;
    // }

    /**
     * <p>
     * Lists the primary and replica keys in same multi-Region key. This field is present only when the value of the
     * <code>MultiRegion</code> field is <code>True</code>.
     * </p>
     * <p>
     * For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or <code>REPLICA</code>
     * key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the current KMS
     * key if it is the primary key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the current
     * KMS key if it is a replica key.
     * </p>
     * </li>
     * </ul>
     * 
     * @param multiRegionConfiguration
     *        Lists the primary and replica keys in same multi-Region key. This field is present only when the value of
     *        the <code>MultiRegion</code> field is <code>True</code>.</p>
     *        <p>
     *        For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     *        </p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or
     *        <code>REPLICA</code> key.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the
     *        current KMS key if it is the primary key.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the
     *        current KMS key if it is a replica key.
     *        </p>
     *        </li>
     */
    //IBM unsupported
    // public void setMultiRegionConfiguration(MultiRegionConfiguration multiRegionConfiguration) {
    //     this.multiRegionConfiguration = multiRegionConfiguration;
    // }

    /**
     * <p>
     * Lists the primary and replica keys in same multi-Region key. This field is present only when the value of the
     * <code>MultiRegion</code> field is <code>True</code>.
     * </p>
     * <p>
     * For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or <code>REPLICA</code>
     * key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the current KMS
     * key if it is the primary key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the current
     * KMS key if it is a replica key.
     * </p>
     * </li>
     * </ul>
     * 
     * @return Lists the primary and replica keys in same multi-Region key. This field is present only when the value of
     *         the <code>MultiRegion</code> field is <code>True</code>.</p>
     *         <p>
     *         For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     *         </p>
     *         <ul>
     *         <li>
     *         <p>
     *         <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or
     *         <code>REPLICA</code> key.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the
     *         current KMS key if it is the primary key.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the
     *         current KMS key if it is a replica key.
     *         </p>
     *         </li>
     */
    //IBM unsupported
    // public MultiRegionConfiguration getMultiRegionConfiguration() {
    //     return this.multiRegionConfiguration;
    // }

    /**
     * <p>
     * Lists the primary and replica keys in same multi-Region key. This field is present only when the value of the
     * <code>MultiRegion</code> field is <code>True</code>.
     * </p>
     * <p>
     * For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or <code>REPLICA</code>
     * key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the current KMS
     * key if it is the primary key.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the current
     * KMS key if it is a replica key.
     * </p>
     * </li>
     * </ul>
     * 
     * @param multiRegionConfiguration
     *        Lists the primary and replica keys in same multi-Region key. This field is present only when the value of
     *        the <code>MultiRegion</code> field is <code>True</code>.</p>
     *        <p>
     *        For more information about any listed KMS key, use the <a>DescribeKey</a> operation.
     *        </p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>MultiRegionKeyType</code> indicates whether the KMS key is a <code>PRIMARY</code> or
     *        <code>REPLICA</code> key.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>PrimaryKey</code> displays the key ARN and Region of the primary key. This field displays the
     *        current KMS key if it is the primary key.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ReplicaKeys</code> displays the key ARNs and Regions of all replica keys. This field includes the
     *        current KMS key if it is a replica key.
     *        </p>
     *        </li>
     * @return Returns a reference to this object so that method calls can be chained together.
     */
    //IBM unsupported
    // public KeyMetadata withMultiRegionConfiguration(MultiRegionConfiguration multiRegionConfiguration) {
    //     setMultiRegionConfiguration(multiRegionConfiguration);
    //     return this;
    // }

    /**
     * <p>
     * The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins when the
     * last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of the KMS key is
     * <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in a multi-Region key, it
     * is scheduled for deletion, and it still has existing replica keys.
     * </p>
     * <p>
     * When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     * displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     * scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This value
     * displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     * <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     * <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     * </p>
     * 
     * @param pendingDeletionWindowInDays
     *        The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins
     *        when the last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of
     *        the KMS key is <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in
     *        a multi-Region key, it is scheduled for deletion, and it still has existing replica keys.</p>
     *        <p>
     *        When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     *        displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     *        scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This
     *        value displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     *        <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     *        <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     */

    //IBM unsupported
    // public void setPendingDeletionWindowInDays(Integer pendingDeletionWindowInDays) {
    //     this.pendingDeletionWindowInDays = pendingDeletionWindowInDays;
    // }

    /**
     * <p>
     * The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins when the
     * last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of the KMS key is
     * <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in a multi-Region key, it
     * is scheduled for deletion, and it still has existing replica keys.
     * </p>
     * <p>
     * When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     * displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     * scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This value
     * displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     * <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     * <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     * </p>
     * 
     * @return The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins
     *         when the last of its replica keys is deleted. This value is present only when the <code>KeyState</code>
     *         of the KMS key is <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key
     *         in a multi-Region key, it is scheduled for deletion, and it still has existing replica keys.</p>
     *         <p>
     *         When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date
     *         is displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key
     *         is scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted.
     *         This value displays that waiting period. When the last replica key in the multi-Region key is deleted,
     *         the <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code>
     *         to <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     */

    //IBM unsupported
    // public Integer getPendingDeletionWindowInDays() {
    //     return this.pendingDeletionWindowInDays;
    // }

    /**
     * <p>
     * The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins when the
     * last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of the KMS key is
     * <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in a multi-Region key, it
     * is scheduled for deletion, and it still has existing replica keys.
     * </p>
     * <p>
     * When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     * displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     * scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This value
     * displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     * <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     * <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     * </p>
     * 
     * @param pendingDeletionWindowInDays
     *        The waiting period before the primary key in a multi-Region key is deleted. This waiting period begins
     *        when the last of its replica keys is deleted. This value is present only when the <code>KeyState</code> of
     *        the KMS key is <code>PendingReplicaDeletion</code>. That indicates that the KMS key is the primary key in
     *        a multi-Region key, it is scheduled for deletion, and it still has existing replica keys.</p>
     *        <p>
     *        When a single-Region KMS key or a multi-Region replica key is scheduled for deletion, its deletion date is
     *        displayed in the <code>DeletionDate</code> field. However, when the primary key in a multi-Region key is
     *        scheduled for deletion, its waiting period doesn't begin until all of its replica keys are deleted. This
     *        value displays that waiting period. When the last replica key in the multi-Region key is deleted, the
     *        <code>KeyState</code> of the scheduled primary key changes from <code>PendingReplicaDeletion</code> to
     *        <code>PendingDeletion</code> and the deletion date appears in the <code>DeletionDate</code> field.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    //IBM unsupported
    // public KeyMetadata withPendingDeletionWindowInDays(Integer pendingDeletionWindowInDays) {
    //     setPendingDeletionWindowInDays(pendingDeletionWindowInDays);
    //     return this;
	// }

    /**
     * <p>
     * Information about the external key that is associated with a KMS key in an external key store.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key">External
     * key</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param xksKeyConfiguration
     *        Information about the external key that is associated with a KMS key in an external key store.</p>
     *        <p>
     *        For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key"
     *        >External key</a> in the <i>Key Management Service Developer Guide</i>.
     */
// Maybe IBM Unsupported
//    public void setXksKeyConfiguration(XksKeyConfigurationType xksKeyConfiguration) {
//        this.xksKeyConfiguration = xksKeyConfiguration;
//    }

    /**
     * <p>
     * Information about the external key that is associated with a KMS key in an external key store.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key">External
     * key</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Information about the external key that is associated with a KMS key in an external key store.</p>
     *         <p>
     *         For more information, see <a href=
     *         "https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key"
     *         >External key</a> in the <i>Key Management Service Developer Guide</i>.
     */
// Maybe IBM Unsupported
//    public XksKeyConfigurationType getXksKeyConfiguration() {
//        return this.xksKeyConfiguration;
//    }

    /**
     * <p>
     * Information about the external key that is associated with a KMS key in an external key store.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key">External
     * key</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param xksKeyConfiguration
     *        Information about the external key that is associated with a KMS key in an external key store.</p>
     *        <p>
     *        For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/keystore-external.html#concept-external-key"
     *        >External key</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */
// Maybe IBM Unsupported
//    public KeyMetadata withXksKeyConfiguration(XksKeyConfigurationType xksKeyConfiguration) {
//        setXksKeyConfiguration(xksKeyConfiguration);
//        return this;
//    }

    /**
     * Returns a string representation of this object. This is useful for testing and debugging. Sensitive data will be
     * redacted from this string using a placeholder value.
     *
     * @return A string representation of this object.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getAWSAccountId() != null)
            sb.append("AWSAccountId: ").append(getAWSAccountId()).append(",");
        if (getKeyId() != null)
            sb.append("KeyId: ").append(getKeyId()).append(",");
        if (getArn() != null)
            sb.append("Arn: ").append(getArn()).append(",");
        if (getCreationDate() != null)
            sb.append("CreationDate: ").append(getCreationDate()).append(",");
        if (getEnabled() != null)
            sb.append("Enabled: ").append(getEnabled()).append(",");
        if (getDescription() != null)
            sb.append("Description: ").append(getDescription()).append(",");
        if (getKeyUsage() != null)
            sb.append("KeyUsage: ").append(getKeyUsage()).append(",");
        if (getKeyState() != null)
            sb.append("KeyState: ").append(getKeyState()).append(",");
        if (getDeletionDate() != null)
            sb.append("DeletionDate: ").append(getDeletionDate()).append(",");
        if (getValidTo() != null)
            sb.append("ValidTo: ").append(getValidTo()).append(",");
        if (getOrigin() != null)
            sb.append("Origin: ").append(getOrigin()).append(",");
        //IBM unsupported
        // if (getCustomKeyStoreId() != null)
        //     sb.append("CustomKeyStoreId: ").append(getCustomKeyStoreId()).append(",");
        // if (getCloudHsmClusterId() != null)
        //     sb.append("CloudHsmClusterId: ").append(getCloudHsmClusterId()).append(",");
        // if (getExpirationModel() != null)
        //     sb.append("ExpirationModel: ").append(getExpirationModel()).append(",");
        // if (getKeyManager() != null)
        //     sb.append("KeyManager: ").append(getKeyManager()).append(",");
        // if (getCustomerMasterKeySpec() != null)
        //     sb.append("CustomerMasterKeySpec: ").append(getCustomerMasterKeySpec()).append(",");
        // if (getKeySpec() != null)
        //     sb.append("KeySpec: ").append(getKeySpec()).append(",");
        // if (getEncryptionAlgorithms() != null)
        //     sb.append("EncryptionAlgorithms: ").append(getEncryptionAlgorithms()).append(",");
        // if (getSigningAlgorithms() != null)
        //     sb.append("SigningAlgorithms: ").append(getSigningAlgorithms()).append(",");
        //if (getKeyAgreementAlgorithms() != null)
        //    sb.append("KeyAgreementAlgorithms: ").append(getKeyAgreementAlgorithms()).append(",");
        // if (getMultiRegion() != null)
        //     sb.append("MultiRegion: ").append(getMultiRegion()).append(",");
        // if (getMultiRegionConfiguration() != null)
        //     sb.append("MultiRegionConfiguration: ").append(getMultiRegionConfiguration()).append(",");
        // if (getPendingDeletionWindowInDays() != null)
        //     sb.append("PendingDeletionWindowInDays: ").append(getPendingDeletionWindowInDays());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof KeyMetadata == false)
            return false;
        KeyMetadata other = (KeyMetadata) obj;
        if (other.getAWSAccountId() == null ^ this.getAWSAccountId() == null)
            return false;
        if (other.getAWSAccountId() != null && other.getAWSAccountId().equals(this.getAWSAccountId()) == false)
            return false;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
            return false;
        if (other.getArn() == null ^ this.getArn() == null)
            return false;
        if (other.getArn() != null && other.getArn().equals(this.getArn()) == false)
            return false;
        if (other.getCreationDate() == null ^ this.getCreationDate() == null)
            return false;
        if (other.getCreationDate() != null && other.getCreationDate().equals(this.getCreationDate()) == false)
            return false;
        if (other.getEnabled() == null ^ this.getEnabled() == null)
            return false;
        if (other.getEnabled() != null && other.getEnabled().equals(this.getEnabled()) == false)
            return false;
        if (other.getDescription() == null ^ this.getDescription() == null)
            return false;
        if (other.getDescription() != null && other.getDescription().equals(this.getDescription()) == false)
            return false;
        if (other.getKeyUsage() == null ^ this.getKeyUsage() == null)
            return false;
        if (other.getKeyUsage() != null && other.getKeyUsage().equals(this.getKeyUsage()) == false)
            return false;
        if (other.getKeyState() == null ^ this.getKeyState() == null)
            return false;
        if (other.getKeyState() != null && other.getKeyState().equals(this.getKeyState()) == false)
            return false;
        if (other.getDeletionDate() == null ^ this.getDeletionDate() == null)
            return false;
        if (other.getDeletionDate() != null && other.getDeletionDate().equals(this.getDeletionDate()) == false)
            return false;
        if (other.getValidTo() == null ^ this.getValidTo() == null)
            return false;
        if (other.getValidTo() != null && other.getValidTo().equals(this.getValidTo()) == false)
            return false;
        if (other.getOrigin() == null ^ this.getOrigin() == null)
            return false;
        if (other.getOrigin() != null && other.getOrigin().equals(this.getOrigin()) == false)
            return false;
        //IBM unsupported
        // if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null)
        //     return false;
        // if (other.getCustomKeyStoreId() != null && other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId()) == false)
        //     return false;
        // if (other.getCloudHsmClusterId() == null ^ this.getCloudHsmClusterId() == null)
        //     return false;
        // if (other.getCloudHsmClusterId() != null && other.getCloudHsmClusterId().equals(this.getCloudHsmClusterId()) == false)
        //     return false;
        if (other.getExpirationModel() == null ^ this.getExpirationModel() == null)
            return false;
        if (other.getExpirationModel() != null && other.getExpirationModel().equals(this.getExpirationModel()) == false)
            return false;
        //IBM unsupported
        // if (other.getKeyManager() == null ^ this.getKeyManager() == null)
        //     return false;
        // if (other.getKeyManager() != null && other.getKeyManager().equals(this.getKeyManager()) == false)
        //     return false;
        // if (other.getCustomerMasterKeySpec() == null ^ this.getCustomerMasterKeySpec() == null)
        //     return false;
        // if (other.getCustomerMasterKeySpec() != null && other.getCustomerMasterKeySpec().equals(this.getCustomerMasterKeySpec()) == false)
        //     return false;
        // if (other.getKeySpec() == null ^ this.getKeySpec() == null)
        //     return false;
        // if (other.getKeySpec() != null && other.getKeySpec().equals(this.getKeySpec()) == false)
        //     return false;
        // if (other.getEncryptionAlgorithms() == null ^ this.getEncryptionAlgorithms() == null)
        //     return false;
        // if (other.getEncryptionAlgorithms() != null && other.getEncryptionAlgorithms().equals(this.getEncryptionAlgorithms()) == false)
        //     return false;
        // if (other.getSigningAlgorithms() == null ^ this.getSigningAlgorithms() == null)
        //     return false;
        // if (other.getSigningAlgorithms() != null && other.getSigningAlgorithms().equals(this.getSigningAlgorithms()) == false)
        //     return false;
        // if (other.getKeyAgreementAlgorithms() == null ^ this.getKeyAgreementAlgorithms() == null)
        //    return false;
        // if (other.getKeyAgreementAlgorithms() != null && other.getKeyAgreementAlgorithms().equals(this.getKeyAgreementAlgorithms()) == false)
        //    return false;
        // if (other.getMultiRegion() == null ^ this.getMultiRegion() == null)
        //     return false;
        // if (other.getMultiRegion() != null && other.getMultiRegion().equals(this.getMultiRegion()) == false)
        //     return false;
        // if (other.getMultiRegionConfiguration() == null ^ this.getMultiRegionConfiguration() == null)
        //     return false;
        // if (other.getMultiRegionConfiguration() != null && other.getMultiRegionConfiguration().equals(this.getMultiRegionConfiguration()) == false)
        //     return false;
        // if (other.getPendingDeletionWindowInDays() == null ^ this.getPendingDeletionWindowInDays() == null)
        //     return false;
        // if (other.getPendingDeletionWindowInDays() != null && other.getPendingDeletionWindowInDays().equals(this.getPendingDeletionWindowInDays()) == false)
        //     return false;
        // if (other.getMacAlgorithms() == null ^ this.getMacAlgorithms() == null)
        //    return false;
        // if (other.getMacAlgorithms() != null && other.getMacAlgorithms().equals(this.getMacAlgorithms()) == false)
        //    return false;
        // if (other.getXksKeyConfiguration() == null ^ this.getXksKeyConfiguration() == null)
        //    return false;
        // if (other.getXksKeyConfiguration() != null && other.getXksKeyConfiguration().equals(this.getXksKeyConfiguration()) == false)
        //    return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getAWSAccountId() == null) ? 0 : getAWSAccountId().hashCode());
        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getArn() == null) ? 0 : getArn().hashCode());
        hashCode = prime * hashCode + ((getCreationDate() == null) ? 0 : getCreationDate().hashCode());
        hashCode = prime * hashCode + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        hashCode = prime * hashCode + ((getDescription() == null) ? 0 : getDescription().hashCode());
        hashCode = prime * hashCode + ((getKeyUsage() == null) ? 0 : getKeyUsage().hashCode());
        hashCode = prime * hashCode + ((getKeyState() == null) ? 0 : getKeyState().hashCode());
        hashCode = prime * hashCode + ((getDeletionDate() == null) ? 0 : getDeletionDate().hashCode());
        hashCode = prime * hashCode + ((getValidTo() == null) ? 0 : getValidTo().hashCode());
        hashCode = prime * hashCode + ((getOrigin() == null) ? 0 : getOrigin().hashCode());
        //IBM unsupported
        // hashCode = prime * hashCode + ((getCustomKeyStoreId() == null) ? 0 : getCustomKeyStoreId().hashCode());
        // hashCode = prime * hashCode + ((getCloudHsmClusterId() == null) ? 0 : getCloudHsmClusterId().hashCode());
        hashCode = prime * hashCode + ((getExpirationModel() == null) ? 0 : getExpirationModel().hashCode());
        //IBM unsupported
        // hashCode = prime * hashCode + ((getKeyManager() == null) ? 0 : getKeyManager().hashCode());
        // hashCode = prime * hashCode + ((getCustomerMasterKeySpec() == null) ? 0 : getCustomerMasterKeySpec().hashCode());
        // hashCode = prime * hashCode + ((getKeySpec() == null) ? 0 : getKeySpec().hashCode());
        // hashCode = prime * hashCode + ((getEncryptionAlgorithms() == null) ? 0 : getEncryptionAlgorithms().hashCode());
        // hashCode = prime * hashCode + ((getSigningAlgorithms() == null) ? 0 : getSigningAlgorithms().hashCode());
        // hashCode = prime * hashCode + ((getKeyAgreementAlgorithms() == null) ? 0 : getKeyAgreementAlgorithms().hashCode());
        // hashCode = prime * hashCode + ((getMultiRegion() == null) ? 0 : getMultiRegion().hashCode());
        //IBM unsupported
        // hashCode = prime * hashCode + ((getMultiRegionConfiguration() == null) ? 0 : getMultiRegionConfiguration().hashCode());
        // hashCode = prime * hashCode + ((getPendingDeletionWindowInDays() == null) ? 0 : getPendingDeletionWindowInDays().hashCode());
        // hashCode = prime * hashCode + ((getMacAlgorithms() == null) ? 0 : getMacAlgorithms().hashCode());
        // hashCode = prime * hashCode + ((getXksKeyConfiguration() == null) ? 0 : getXksKeyConfiguration().hashCode());
        return hashCode;
    }

    @Override
    public KeyMetadata clone() {
        try {
            return (KeyMetadata) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }

    @com.ibm.cloud.objectstorage.annotation.SdkInternalApi
    @Override
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        com.ibm.cloud.objectstorage.services.kms.model.transform.KeyMetadataMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}
