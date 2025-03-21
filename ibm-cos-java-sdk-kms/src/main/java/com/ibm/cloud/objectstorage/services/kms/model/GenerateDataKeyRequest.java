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

import com.ibm.cloud.objectstorage.AmazonWebServiceRequest;

/**
 * 
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/GenerateDataKey" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class GenerateDataKeyRequest extends com.ibm.cloud.objectstorage.AmazonWebServiceRequest implements Serializable, Cloneable {

    /**
     * <p>
     * Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric KMS key
     * or a KMS key in a custom key store. To get the type and origin of your KMS key, use the <a>DescribeKey</a>
     * operation.
     * </p>
     * <p>
     * To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix it with
     * <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must use the key ARN
     * or alias ARN.
     * </p>
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>
     * <p>
     * Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias name: <code>alias/ExampleAlias</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     * </p>
     * </li>
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias name and
     * alias ARN, use <a>ListAliases</a>.
     * </p>
     */
    private String keyId;
    /**
     * <p>
     * Specifies the encryption context that will be used when encrypting the data key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     * authenticated data. When you use an encryption context to encrypt data, you must specify the same (an exact
     * case-sensitive match) encryption context to decrypt the data. An encryption context is supported only on
     * operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys, an encryption
     * context is optional, but it is strongly recommended.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String> encryptionContext;
    /**
     * <p>
     * Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data key (64
     * bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the <code>KeySpec</code>
     * parameter.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     */
    private Integer numberOfBytes;
    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     */
    private String keySpec;
    /**
     * <p>
     * A list of grant tokens.
     * </p>
     * <p>
     * Use a grant token when your permission to call this operation comes from a new grant that has not yet achieved
     * <i>eventual consistency</i>. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using a grant
     * token</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private com.ibm.cloud.objectstorage.internal.SdkInternalList<String> grantTokens;
    /**
     * <p>
     * A signed <a
     * href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc">attestation
     * document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use with the enclave's
     * public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     * </p>
     * <p>
     * This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     * parameter, use the <a
     * href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web Services
     * Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     * </p>
     * <p>
     * When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data key
     * under the public key in the attestation document, and returns the resulting ciphertext in the
     * <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the private
     * key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the data key
     * encrypted under the KMS key specified by the <code>KeyId</code> parameter. The <code>Plaintext</code> field in
     * the response is null or empty.
     * </p>
     * <p>
     * For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web Services
     * Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    // IBM Unsupported
    // private RecipientInfo recipient;
    /**
     * <p>
     * Checks if your request will succeed. <code>DryRun</code> is an optional parameter.
     * </p>
     * <p>
     * To learn more about how to use this parameter, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     * calls</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private Boolean dryRun;

    /**
     * <p>
     * Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric KMS key
     * or a KMS key in a custom key store. To get the type and origin of your KMS key, use the <a>DescribeKey</a>
     * operation.
     * </p>
     * <p>
     * To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix it with
     * <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must use the key ARN
     * or alias ARN.
     * </p>
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>
     * <p>
     * Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias name: <code>alias/ExampleAlias</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     * </p>
     * </li>
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias name and
     * alias ARN, use <a>ListAliases</a>.
     * </p>
     * 
     * @param keyId
     *        Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric
     *        KMS key or a KMS key in a custom key store. To get the type and origin of your KMS key, use the
     *        <a>DescribeKey</a> operation.</p>
     *        <p>
     *        To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix
     *        it with <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must
     *        use the key ARN or alias ARN.
     *        </p>
     *        <p>
     *        For example:
     *        </p>
     *        <ul>
     *        <li>
     *        <p>
     *        Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Alias name: <code>alias/ExampleAlias</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     *        </p>
     *        </li>
     *        </ul>
     *        <p>
     *        To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias
     *        name and alias ARN, use <a>ListAliases</a>.
     */

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * <p>
     * Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric KMS key
     * or a KMS key in a custom key store. To get the type and origin of your KMS key, use the <a>DescribeKey</a>
     * operation.
     * </p>
     * <p>
     * To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix it with
     * <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must use the key ARN
     * or alias ARN.
     * </p>
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>
     * <p>
     * Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias name: <code>alias/ExampleAlias</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     * </p>
     * </li>
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias name and
     * alias ARN, use <a>ListAliases</a>.
     * </p>
     * 
     * @return Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric
     *         KMS key or a KMS key in a custom key store. To get the type and origin of your KMS key, use the
     *         <a>DescribeKey</a> operation.</p>
     *         <p>
     *         To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix
     *         it with <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must
     *         use the key ARN or alias ARN.
     *         </p>
     *         <p>
     *         For example:
     *         </p>
     *         <ul>
     *         <li>
     *         <p>
     *         Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         Alias name: <code>alias/ExampleAlias</code>
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     *         </p>
     *         </li>
     *         </ul>
     *         <p>
     *         To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias
     *         name and alias ARN, use <a>ListAliases</a>.
     */

    public String getKeyId() {
        return this.keyId;
    }

    /**
     * <p>
     * Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric KMS key
     * or a KMS key in a custom key store. To get the type and origin of your KMS key, use the <a>DescribeKey</a>
     * operation.
     * </p>
     * <p>
     * To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix it with
     * <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must use the key ARN
     * or alias ARN.
     * </p>
     * <p>
     * For example:
     * </p>
     * <ul>
     * <li>
     * <p>
     * Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias name: <code>alias/ExampleAlias</code>
     * </p>
     * </li>
     * <li>
     * <p>
     * Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     * </p>
     * </li>
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias name and
     * alias ARN, use <a>ListAliases</a>.
     * </p>
     * 
     * @param keyId
     *        Specifies the symmetric encryption KMS key that encrypts the data key. You cannot specify an asymmetric
     *        KMS key or a KMS key in a custom key store. To get the type and origin of your KMS key, use the
     *        <a>DescribeKey</a> operation.</p>
     *        <p>
     *        To specify a KMS key, use its key ID, key ARN, alias name, or alias ARN. When using an alias name, prefix
     *        it with <code>"alias/"</code>. To specify a KMS key in a different Amazon Web Services account, you must
     *        use the key ARN or alias ARN.
     *        </p>
     *        <p>
     *        For example:
     *        </p>
     *        <ul>
     *        <li>
     *        <p>
     *        Key ID: <code>1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Key ARN: <code>arn:aws:kms:us-east-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Alias name: <code>alias/ExampleAlias</code>
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        Alias ARN: <code>arn:aws:kms:us-east-2:111122223333:alias/ExampleAlias</code>
     *        </p>
     *        </li>
     *        </ul>
     *        <p>
     *        To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>. To get the alias
     *        name and alias ARN, use <a>ListAliases</a>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * Specifies the encryption context that will be used when encrypting the data key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     * authenticated data. When you use an encryption context to encrypt data, you must specify the same (an exact
     * case-sensitive match) encryption context to decrypt the data. An encryption context is supported only on
     * operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys, an encryption
     * context is optional, but it is strongly recommended.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Specifies the encryption context that will be used when encrypting the data key.</p> <important>
     *         <p>
     *         Do not include confidential or sensitive information in this field. This field may be displayed in
     *         plaintext in CloudTrail logs and other output.
     *         </p>
     *         </important>
     *         <p>
     *         An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     *         authenticated data. When you use an encryption context to encrypt data, you must specify the same (an
     *         exact case-sensitive match) encryption context to decrypt the data. An encryption context is supported
     *         only on operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys,
     *         an encryption context is optional, but it is strongly recommended.
     *         </p>
     *         <p>
     *         For more information, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *         context</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public java.util.Map<String, String> getEncryptionContext() {
        if (encryptionContext == null) {
            encryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        return encryptionContext;
    }

    /**
     * <p>
     * Specifies the encryption context that will be used when encrypting the data key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     * authenticated data. When you use an encryption context to encrypt data, you must specify the same (an exact
     * case-sensitive match) encryption context to decrypt the data. An encryption context is supported only on
     * operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys, an encryption
     * context is optional, but it is strongly recommended.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param encryptionContext
     *        Specifies the encryption context that will be used when encrypting the data key.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     *        authenticated data. When you use an encryption context to encrypt data, you must specify the same (an
     *        exact case-sensitive match) encryption context to decrypt the data. An encryption context is supported
     *        only on operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys,
     *        an encryption context is optional, but it is strongly recommended.
     *        </p>
     *        <p>
     *        For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *        context</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public void setEncryptionContext(java.util.Map<String, String> encryptionContext) {
        this.encryptionContext = encryptionContext == null ? null : new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>(encryptionContext);
    }

    /**
     * <p>
     * Specifies the encryption context that will be used when encrypting the data key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     * authenticated data. When you use an encryption context to encrypt data, you must specify the same (an exact
     * case-sensitive match) encryption context to decrypt the data. An encryption context is supported only on
     * operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys, an encryption
     * context is optional, but it is strongly recommended.
     * </p>
     * <p>
     * For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param encryptionContext
     *        Specifies the encryption context that will be used when encrypting the data key.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        An <i>encryption context</i> is a collection of non-secret key-value pairs that represent additional
     *        authenticated data. When you use an encryption context to encrypt data, you must specify the same (an
     *        exact case-sensitive match) encryption context to decrypt the data. An encryption context is supported
     *        only on operations with symmetric encryption KMS keys. On operations with symmetric encryption KMS keys,
     *        an encryption context is optional, but it is strongly recommended.
     *        </p>
     *        <p>
     *        For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *        context</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withEncryptionContext(java.util.Map<String, String> encryptionContext) {
        setEncryptionContext(encryptionContext);
        return this;
    }

    /**
     * Add a single EncryptionContext entry
     *
     * @see GenerateDataKeyRequest#withEncryptionContext
     * @returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest addEncryptionContextEntry(String key, String value) {
        if (null == this.encryptionContext) {
            this.encryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        if (this.encryptionContext.containsKey(key))
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        this.encryptionContext.put(key, value);
        return this;
    }

    /**
     * Removes all the entries added into EncryptionContext.
     *
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest clearEncryptionContextEntries() {
        this.encryptionContext = null;
        return this;
    }

    /**
     * <p>
     * Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data key (64
     * bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the <code>KeySpec</code>
     * parameter.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param numberOfBytes
     *        Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data
     *        key (64 bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the
     *        <code>KeySpec</code> parameter.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     */

    public void setNumberOfBytes(Integer numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    /**
     * <p>
     * Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data key (64
     * bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the <code>KeySpec</code>
     * parameter.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @return Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data
     *         key (64 bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the
     *         <code>KeySpec</code> parameter.</p>
     *         <p>
     *         You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *         both) in every <code>GenerateDataKey</code> request.
     */

    public Integer getNumberOfBytes() {
        return this.numberOfBytes;
    }

    /**
     * <p>
     * Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data key (64
     * bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the <code>KeySpec</code>
     * parameter.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param numberOfBytes
     *        Specifies the length of the data key in bytes. For example, use the value 64 to generate a 512-bit data
     *        key (64 bytes is 512 bits). For 128-bit (16-byte) and 256-bit (32-byte) data keys, use the
     *        <code>KeySpec</code> parameter.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withNumberOfBytes(Integer numberOfBytes) {
        setNumberOfBytes(numberOfBytes);
        return this;
    }

    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param keySpec
     *        Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     *        <code>AES_256</code> to generate a 256-bit symmetric key.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     * @see DataKeySpec
     */

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @return Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     *         <code>AES_256</code> to generate a 256-bit symmetric key.</p>
     *         <p>
     *         You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *         both) in every <code>GenerateDataKey</code> request.
     * @see DataKeySpec
     */

    public String getKeySpec() {
        return this.keySpec;
    }

    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param keySpec
     *        Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     *        <code>AES_256</code> to generate a 256-bit symmetric key.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see DataKeySpec
     */

    public GenerateDataKeyRequest withKeySpec(String keySpec) {
        setKeySpec(keySpec);
        return this;
    }

    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param keySpec
     *        Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     *        <code>AES_256</code> to generate a 256-bit symmetric key.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     * @see DataKeySpec
     */

    public void setKeySpec(DataKeySpec keySpec) {
        withKeySpec(keySpec);
    }

    /**
     * <p>
     * Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     * <code>AES_256</code> to generate a 256-bit symmetric key.
     * </p>
     * <p>
     * You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not both) in
     * every <code>GenerateDataKey</code> request.
     * </p>
     *
     * @param keySpec
     *        Specifies the length of the data key. Use <code>AES_128</code> to generate a 128-bit symmetric key, or
     *        <code>AES_256</code> to generate a 256-bit symmetric key.</p>
     *        <p>
     *        You must specify either the <code>KeySpec</code> or the <code>NumberOfBytes</code> parameter (but not
     *        both) in every <code>GenerateDataKey</code> request.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see DataKeySpec
     */

    public GenerateDataKeyRequest withKeySpec(DataKeySpec keySpec) {
        this.keySpec = keySpec.toString();
        return this;
    }

    /**
     * <p>
     * A list of grant tokens.
     * </p>
     * <p>
     * Use a grant token when your permission to call this operation comes from a new grant that has not yet achieved
     * <i>eventual consistency</i>. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using a grant
     * token</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return A list of grant tokens.</p>
     *         <p>
     *         Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *         achieved <i>eventual consistency</i>. For more information, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *         <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *         a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public java.util.List<String> getGrantTokens() {
        if (grantTokens == null) {
            grantTokens = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>();
        }
        return grantTokens;
    }

    /**
     * <p>
     * A list of grant tokens.
     * </p>
     * <p>
     * Use a grant token when your permission to call this operation comes from a new grant that has not yet achieved
     * <i>eventual consistency</i>. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using a grant
     * token</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param grantTokens
     *        A list of grant tokens.</p>
     *        <p>
     *        Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *        achieved <i>eventual consistency</i>. For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *        <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *        a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public void setGrantTokens(java.util.Collection<String> grantTokens) {
        if (grantTokens == null) {
            this.grantTokens = null;
            return;
        }

        this.grantTokens = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(grantTokens);
    }

    /**
     * <p>
     * A list of grant tokens.
     * </p>
     * <p>
     * Use a grant token when your permission to call this operation comes from a new grant that has not yet achieved
     * <i>eventual consistency</i>. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using a grant
     * token</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
     * {@link #setGrantTokens(java.util.Collection)} or {@link #withGrantTokens(java.util.Collection)} if you want to
     * override the existing values.
     * </p>
     * 
     * @param grantTokens
     *        A list of grant tokens.</p>
     *        <p>
     *        Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *        achieved <i>eventual consistency</i>. For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *        <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *        a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withGrantTokens(String... grantTokens) {
        if (this.grantTokens == null) {
            setGrantTokens(new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    /**
     * <p>
     * A list of grant tokens.
     * </p>
     * <p>
     * Use a grant token when your permission to call this operation comes from a new grant that has not yet achieved
     * <i>eventual consistency</i>. For more information, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using a grant
     * token</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param grantTokens
     *        A list of grant tokens.</p>
     *        <p>
     *        Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *        achieved <i>eventual consistency</i>. For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *        <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *        a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withGrantTokens(java.util.Collection<String> grantTokens) {
        setGrantTokens(grantTokens);
        return this;
    }

    /**
     * <p>
     * A signed <a
     * href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc">attestation
     * document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use with the enclave's
     * public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     * </p>
     * <p>
     * This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     * parameter, use the <a
     * href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web Services
     * Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     * </p>
     * <p>
     * When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data key
     * under the public key in the attestation document, and returns the resulting ciphertext in the
     * <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the private
     * key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the data key
     * encrypted under the KMS key specified by the <code>KeyId</code> parameter. The <code>Plaintext</code> field in
     * the response is null or empty.
     * </p>
     * <p>
     * For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web Services
     * Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param recipient
     *        A signed <a
     *        href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc"
     *        >attestation document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use
     *        with the enclave's public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     *        </p>
     *        <p>
     *        This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     *        parameter, use the <a
     *        href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web
     *        Services Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     *        </p>
     *        <p>
     *        When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data
     *        key under the public key in the attestation document, and returns the resulting ciphertext in the
     *        <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the
     *        private key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the
     *        data key encrypted under the KMS key specified by the <code>KeyId</code> parameter. The
     *        <code>Plaintext</code> field in the response is null or empty.
     *        </p>
     *        <p>
     *        For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web
     *        Services Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     */
    // IBM Unsupported
    // public void setRecipient(RecipientInfo recipient) {
    //     this.recipient = recipient;
    // }

    /**
     * <p>
     * A signed <a
     * href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc">attestation
     * document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use with the enclave's
     * public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     * </p>
     * <p>
     * This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     * parameter, use the <a
     * href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web Services
     * Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     * </p>
     * <p>
     * When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data key
     * under the public key in the attestation document, and returns the resulting ciphertext in the
     * <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the private
     * key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the data key
     * encrypted under the KMS key specified by the <code>KeyId</code> parameter. The <code>Plaintext</code> field in
     * the response is null or empty.
     * </p>
     * <p>
     * For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web Services
     * Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return A signed <a
     *         href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc"
     *         >attestation document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use
     *         with the enclave's public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     *         </p>
     *         <p>
     *         This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include
     *         this parameter, use the <a
     *         href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web
     *         Services Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     *         </p>
     *         <p>
     *         When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data
     *         key under the public key in the attestation document, and returns the resulting ciphertext in the
     *         <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the
     *         private key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the
     *         data key encrypted under the KMS key specified by the <code>KeyId</code> parameter. The
     *         <code>Plaintext</code> field in the response is null or empty.
     *         </p>
     *         <p>
     *         For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web
     *         Services Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     */

    // IBM Unsupported
    // public RecipientInfo getRecipient() {
    //     return this.recipient;
    // }

    /**
     * <p>
     * A signed <a
     * href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc">attestation
     * document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use with the enclave's
     * public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     * </p>
     * <p>
     * This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     * parameter, use the <a
     * href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web Services
     * Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     * </p>
     * <p>
     * When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data key
     * under the public key in the attestation document, and returns the resulting ciphertext in the
     * <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the private
     * key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the data key
     * encrypted under the KMS key specified by the <code>KeyId</code> parameter. The <code>Plaintext</code> field in
     * the response is null or empty.
     * </p>
     * <p>
     * For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web Services
     * Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param recipient
     *        A signed <a
     *        href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/nitro-enclave-how.html#term-attestdoc"
     *        >attestation document</a> from an Amazon Web Services Nitro enclave and the encryption algorithm to use
     *        with the enclave's public key. The only valid encryption algorithm is <code>RSAES_OAEP_SHA_256</code>.
     *        </p>
     *        <p>
     *        This parameter only supports attestation documents for Amazon Web Services Nitro Enclaves. To include this
     *        parameter, use the <a
     *        href="https://docs.aws.amazon.com/enclaves/latest/user/developing-applications.html#sdk">Amazon Web
     *        Services Nitro Enclaves SDK</a> or any Amazon Web Services SDK.
     *        </p>
     *        <p>
     *        When you use this parameter, instead of returning the plaintext data key, KMS encrypts the plaintext data
     *        key under the public key in the attestation document, and returns the resulting ciphertext in the
     *        <code>CiphertextForRecipient</code> field in the response. This ciphertext can be decrypted only with the
     *        private key in the enclave. The <code>CiphertextBlob</code> field in the response contains a copy of the
     *        data key encrypted under the KMS key specified by the <code>KeyId</code> parameter. The
     *        <code>Plaintext</code> field in the response is null or empty.
     *        </p>
     *        <p>
     *        For information about the interaction between KMS and Amazon Web Services Nitro Enclaves, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/services-nitro-enclaves.html">How Amazon Web
     *        Services Nitro Enclaves uses KMS</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */
    // IBM Unsupported
    // public GenerateDataKeyRequest withRecipient(RecipientInfo recipient) {
    //     setRecipient(recipient);
    //     return this;
	//}
    /**
     * <p>
     * Checks if your request will succeed. <code>DryRun</code> is an optional parameter.
     * </p>
     * <p>
     * To learn more about how to use this parameter, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     * calls</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param dryRun
     *        Checks if your request will succeed. <code>DryRun</code> is an optional parameter. </p>
     *        <p>
     *        To learn more about how to use this parameter, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     *        calls</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * <p>
     * Checks if your request will succeed. <code>DryRun</code> is an optional parameter.
     * </p>
     * <p>
     * To learn more about how to use this parameter, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     * calls</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Checks if your request will succeed. <code>DryRun</code> is an optional parameter. </p>
     *         <p>
     *         To learn more about how to use this parameter, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     *         calls</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public Boolean getDryRun() {
        return this.dryRun;
    }

    /**
     * <p>
     * Checks if your request will succeed. <code>DryRun</code> is an optional parameter.
     * </p>
     * <p>
     * To learn more about how to use this parameter, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     * calls</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param dryRun
     *        Checks if your request will succeed. <code>DryRun</code> is an optional parameter. </p>
     *        <p>
     *        To learn more about how to use this parameter, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     *        calls</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public GenerateDataKeyRequest withDryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * <p>
     * Checks if your request will succeed. <code>DryRun</code> is an optional parameter.
     * </p>
     * <p>
     * To learn more about how to use this parameter, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     * calls</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return Checks if your request will succeed. <code>DryRun</code> is an optional parameter. </p>
     *         <p>
     *         To learn more about how to use this parameter, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/programming-dryrun.html">Testing your KMS API
     *         calls</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public Boolean isDryRun() {
        return this.dryRun;
    }

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
        if (getKeyId() != null)
            sb.append("KeyId: ").append(getKeyId()).append(",");
        if (getEncryptionContext() != null)
            sb.append("EncryptionContext: ").append(getEncryptionContext()).append(",");
        if (getNumberOfBytes() != null)
            sb.append("NumberOfBytes: ").append(getNumberOfBytes()).append(",");
        if (getKeySpec() != null)
            sb.append("KeySpec: ").append(getKeySpec()).append(",");
        if (getGrantTokens() != null)
            sb.append("GrantTokens: ").append(getGrantTokens()).append(",");
        // IBM Unsupported    
        // if (getRecipient() != null)
        //     sb.append("Recipient: ").append(getRecipient());
        if (getDryRun() != null)
            sb.append("DryRun: ").append(getDryRun());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof GenerateDataKeyRequest == false)
            return false;
        GenerateDataKeyRequest other = (GenerateDataKeyRequest) obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
            return false;
        if (other.getEncryptionContext() == null ^ this.getEncryptionContext() == null)
            return false;
        if (other.getEncryptionContext() != null && other.getEncryptionContext().equals(this.getEncryptionContext()) == false)
            return false;
        if (other.getNumberOfBytes() == null ^ this.getNumberOfBytes() == null)
            return false;
        if (other.getNumberOfBytes() != null && other.getNumberOfBytes().equals(this.getNumberOfBytes()) == false)
            return false;
        if (other.getKeySpec() == null ^ this.getKeySpec() == null)
            return false;
        if (other.getKeySpec() != null && other.getKeySpec().equals(this.getKeySpec()) == false)
            return false;
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null)
            return false;
        if (other.getGrantTokens() != null && other.getGrantTokens().equals(this.getGrantTokens()) == false)
            return false;
        // IBM Unsupported
        // if (other.getRecipient() == null ^ this.getRecipient() == null)
        //     return false;
        // if (other.getRecipient() != null && other.getRecipient().equals(this.getRecipient()) == false)
        //     return false;
        if (other.getDryRun() == null ^ this.getDryRun() == null)
            return false;
        if (other.getDryRun() != null && other.getDryRun().equals(this.getDryRun()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getEncryptionContext() == null) ? 0 : getEncryptionContext().hashCode());
        hashCode = prime * hashCode + ((getNumberOfBytes() == null) ? 0 : getNumberOfBytes().hashCode());
        hashCode = prime * hashCode + ((getKeySpec() == null) ? 0 : getKeySpec().hashCode());
        hashCode = prime * hashCode + ((getGrantTokens() == null) ? 0 : getGrantTokens().hashCode());
        // IBM Unsupported
        // hashCode = prime * hashCode + ((getRecipient() == null) ? 0 : getRecipient().hashCode());
        hashCode = prime * hashCode + ((getDryRun() == null) ? 0 : getDryRun().hashCode());
        return hashCode;
    }

    @Override
    public GenerateDataKeyRequest clone() {
        return (GenerateDataKeyRequest) super.clone();
    }

}
