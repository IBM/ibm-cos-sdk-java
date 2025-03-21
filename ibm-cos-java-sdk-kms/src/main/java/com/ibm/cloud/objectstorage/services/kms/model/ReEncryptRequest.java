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
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ReEncrypt" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class ReEncryptRequest extends com.ibm.cloud.objectstorage.AmazonWebServiceRequest implements Serializable, Cloneable {

    /**
     * <p>
     * Ciphertext of the data to reencrypt.
     * </p>
     */
    private java.nio.ByteBuffer ciphertextBlob;
    /**
     * <p>
     * Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that was
     * used to encrypt the ciphertext.
     * </p>
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
    private com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String> sourceEncryptionContext;
    /**
     * <p>
     * Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.
     * </p>
     * <p>
     * Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS key, the
     * <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used a
     * symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric ciphertext
     * blob. However, it is always recommended as a best practice. This practice ensures that you use the KMS key that
     * you intend.
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
    private String sourceKeyId;
    /**
     * <p>
     * A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS key or
     * an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find the
     * <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.
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
    private String destinationKeyId;
    /**
     * <p>
     * Specifies that encryption context to use when the reencrypting the data.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS key.
     * The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     * </p>
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
    private com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String> destinationEncryptionContext;
    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted. The
     * default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption KMS keys.
     * </p>
     * <p>
     * Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm, the
     * decrypt attempt fails.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * </p>
     */
    private String sourceEncryptionAlgorithm;
    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The default
     * value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric encryption KMS
     * keys.
     * </p>
     * <p>
     * This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * </p>
     */
    private String destinationEncryptionAlgorithm;
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
     * Ciphertext of the data to reencrypt.
     * </p>
     * <p>
     * The AWS SDK for Java performs a Base64 encoding on this field before sending this request to the AWS service.
     * Users of the SDK should not perform Base64 encoding on this field.
     * </p>
     * <p>
     * Warning: ByteBuffers returned by the SDK are mutable. Changes to the content or position of the byte buffer will
     * be seen by all objects that have a reference to this object. It is recommended to call ByteBuffer.duplicate() or
     * ByteBuffer.asReadOnlyBuffer() before using or reading from the buffer. This behavior will be changed in a future
     * major version of the SDK.
     * </p>
     *
     * @param ciphertextBlob
     *        Ciphertext of the data to reencrypt.
     */

    public void setCiphertextBlob(java.nio.ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    /**
     * <p>
     * Ciphertext of the data to reencrypt.
     * </p>
     * <p>
     * {@code ByteBuffer}s are stateful. Calling their {@code get} methods changes their {@code position}. We recommend
     * using {@link java.nio.ByteBuffer#asReadOnlyBuffer()} to create a read-only view of the buffer with an independent
     * {@code position}, and calling {@code get} methods on this rather than directly on the returned {@code ByteBuffer}.
     * Doing so will ensure that anyone else using the {@code ByteBuffer} will not be affected by changes to the
     * {@code position}.
     * </p>
     *
     * @return Ciphertext of the data to reencrypt.
     */

    public java.nio.ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    /**
     * <p>
     * Ciphertext of the data to reencrypt.
     * </p>
     * <p>
     * The AWS SDK for Java performs a Base64 encoding on this field before sending this request to the AWS service.
     * Users of the SDK should not perform Base64 encoding on this field.
     * </p>
     * <p>
     * Warning: ByteBuffers returned by the SDK are mutable. Changes to the content or position of the byte buffer will
     * be seen by all objects that have a reference to this object. It is recommended to call ByteBuffer.duplicate() or
     * ByteBuffer.asReadOnlyBuffer() before using or reading from the buffer. This behavior will be changed in a future
     * major version of the SDK.
     * </p>
     *
     * @param ciphertextBlob
     *        Ciphertext of the data to reencrypt.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptRequest withCiphertextBlob(java.nio.ByteBuffer ciphertextBlob) {
        setCiphertextBlob(ciphertextBlob);
        return this;
    }

    /**
     * <p>
     * Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that was
     * used to encrypt the ciphertext.
     * </p>
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
     * @return Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that
     *         was used to encrypt the ciphertext.</p>
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

    public java.util.Map<String, String> getSourceEncryptionContext() {
        if (sourceEncryptionContext == null) {
            sourceEncryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        return sourceEncryptionContext;
    }

    /**
     * <p>
     * Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that was
     * used to encrypt the ciphertext.
     * </p>
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
     * @param sourceEncryptionContext
     *        Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that
     *        was used to encrypt the ciphertext.</p>
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

    public void setSourceEncryptionContext(java.util.Map<String, String> sourceEncryptionContext) {
        this.sourceEncryptionContext = sourceEncryptionContext == null ? null : new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>(
                sourceEncryptionContext);
    }

    /**
     * <p>
     * Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that was
     * used to encrypt the ciphertext.
     * </p>
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
     * @param sourceEncryptionContext
     *        Specifies the encryption context to use to decrypt the ciphertext. Enter the same encryption context that
     *        was used to encrypt the ciphertext.</p>
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

    public ReEncryptRequest withSourceEncryptionContext(java.util.Map<String, String> sourceEncryptionContext) {
        setSourceEncryptionContext(sourceEncryptionContext);
        return this;
    }

    /**
     * Add a single SourceEncryptionContext entry
     *
     * @see ReEncryptRequest#withSourceEncryptionContext
     * @returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptRequest addSourceEncryptionContextEntry(String key, String value) {
        if (null == this.sourceEncryptionContext) {
            this.sourceEncryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        if (this.sourceEncryptionContext.containsKey(key))
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        this.sourceEncryptionContext.put(key, value);
        return this;
    }

    /**
     * Removes all the entries added into SourceEncryptionContext.
     *
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptRequest clearSourceEncryptionContextEntries() {
        this.sourceEncryptionContext = null;
        return this;
    }

    /**
     * <p>
     * Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.
     * </p>
     * <p>
     * Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS key, the
     * <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used a
     * symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric ciphertext
     * blob. However, it is always recommended as a best practice. This practice ensures that you use the KMS key that
     * you intend.
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
     * @param sourceKeyId
     *        Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.</p>
     *        <p>
     *        Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS
     *        key, the <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     *        </p>
     *        <p>
     *        This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used
     *        a symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric
     *        ciphertext blob. However, it is always recommended as a best practice. This practice ensures that you use
     *        the KMS key that you intend.
     *        </p>
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

    public void setSourceKeyId(String sourceKeyId) {
        this.sourceKeyId = sourceKeyId;
    }

    /**
     * <p>
     * Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.
     * </p>
     * <p>
     * Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS key, the
     * <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used a
     * symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric ciphertext
     * blob. However, it is always recommended as a best practice. This practice ensures that you use the KMS key that
     * you intend.
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
     * @return Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.</p>
     *         <p>
     *         Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS
     *         key, the <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     *         </p>
     *         <p>
     *         This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you
     *         used a symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric
     *         ciphertext blob. However, it is always recommended as a best practice. This practice ensures that you use
     *         the KMS key that you intend.
     *         </p>
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

    public String getSourceKeyId() {
        return this.sourceKeyId;
    }

    /**
     * <p>
     * Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.
     * </p>
     * <p>
     * Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS key, the
     * <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used a
     * symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric ciphertext
     * blob. However, it is always recommended as a best practice. This practice ensures that you use the KMS key that
     * you intend.
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
     * @param sourceKeyId
     *        Specifies the KMS key that KMS will use to decrypt the ciphertext before it is re-encrypted.</p>
     *        <p>
     *        Enter a key ID of the KMS key that was used to encrypt the ciphertext. If you identify a different KMS
     *        key, the <code>ReEncrypt</code> operation throws an <code>IncorrectKeyException</code>.
     *        </p>
     *        <p>
     *        This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key. If you used
     *        a symmetric encryption KMS key, KMS can get the KMS key from metadata that it adds to the symmetric
     *        ciphertext blob. However, it is always recommended as a best practice. This practice ensures that you use
     *        the KMS key that you intend.
     *        </p>
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

    public ReEncryptRequest withSourceKeyId(String sourceKeyId) {
        setSourceKeyId(sourceKeyId);
        return this;
    }

    /**
     * <p>
     * A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS key or
     * an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find the
     * <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.
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
     * @param destinationKeyId
     *        A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS
     *        key or an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find
     *        the <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.</p>
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

    public void setDestinationKeyId(String destinationKeyId) {
        this.destinationKeyId = destinationKeyId;
    }

    /**
     * <p>
     * A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS key or
     * an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find the
     * <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.
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
     * @return A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption
     *         KMS key or an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To
     *         find the <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.</p>
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

    public String getDestinationKeyId() {
        return this.destinationKeyId;
    }

    /**
     * <p>
     * A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS key or
     * an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find the
     * <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.
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
     * @param destinationKeyId
     *        A unique identifier for the KMS key that is used to reencrypt the data. Specify a symmetric encryption KMS
     *        key or an asymmetric KMS key with a <code>KeyUsage</code> value of <code>ENCRYPT_DECRYPT</code>. To find
     *        the <code>KeyUsage</code> value of a KMS key, use the <a>DescribeKey</a> operation.</p>
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

    public ReEncryptRequest withDestinationKeyId(String destinationKeyId) {
        setDestinationKeyId(destinationKeyId);
        return this;
    }

    /**
     * <p>
     * Specifies that encryption context to use when the reencrypting the data.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS key.
     * The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     * </p>
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
     * @return Specifies that encryption context to use when the reencrypting the data.</p> <important>
     *         <p>
     *         Do not include confidential or sensitive information in this field. This field may be displayed in
     *         plaintext in CloudTrail logs and other output.
     *         </p>
     *         </important>
     *         <p>
     *         A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS
     *         key. The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     *         </p>
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

    public java.util.Map<String, String> getDestinationEncryptionContext() {
        if (destinationEncryptionContext == null) {
            destinationEncryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        return destinationEncryptionContext;
    }

    /**
     * <p>
     * Specifies that encryption context to use when the reencrypting the data.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS key.
     * The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     * </p>
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
     * @param destinationEncryptionContext
     *        Specifies that encryption context to use when the reencrypting the data.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS
     *        key. The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     *        </p>
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

    public void setDestinationEncryptionContext(java.util.Map<String, String> destinationEncryptionContext) {
        this.destinationEncryptionContext = destinationEncryptionContext == null ? null : new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>(
                destinationEncryptionContext);
    }

    /**
     * <p>
     * Specifies that encryption context to use when the reencrypting the data.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS key.
     * The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     * </p>
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
     * @param destinationEncryptionContext
     *        Specifies that encryption context to use when the reencrypting the data.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        A destination encryption context is valid only when the destination KMS key is a symmetric encryption KMS
     *        key. The standard ciphertext format for asymmetric KMS keys does not include fields for metadata.
     *        </p>
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

    public ReEncryptRequest withDestinationEncryptionContext(java.util.Map<String, String> destinationEncryptionContext) {
        setDestinationEncryptionContext(destinationEncryptionContext);
        return this;
    }

    /**
     * Add a single DestinationEncryptionContext entry
     *
     * @see ReEncryptRequest#withDestinationEncryptionContext
     * @returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptRequest addDestinationEncryptionContextEntry(String key, String value) {
        if (null == this.destinationEncryptionContext) {
            this.destinationEncryptionContext = new com.ibm.cloud.objectstorage.internal.SdkInternalMap<String, String>();
        }
        if (this.destinationEncryptionContext.containsKey(key))
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        this.destinationEncryptionContext.put(key, value);
        return this;
    }

    /**
     * Removes all the entries added into DestinationEncryptionContext.
     *
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptRequest clearDestinationEncryptionContextEntries() {
        this.destinationEncryptionContext = null;
        return this;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted. The
     * default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption KMS keys.
     * </p>
     * <p>
     * Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm, the
     * decrypt attempt fails.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * </p>
     * 
     * @param sourceEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted.
     *        The default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption
     *        KMS keys.</p>
     *        <p>
     *        Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm,
     *        the decrypt attempt fails.
     *        </p>
     *        <p>
     *        This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * @see EncryptionAlgorithmSpec
     */

    public void setSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted. The
     * default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption KMS keys.
     * </p>
     * <p>
     * Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm, the
     * decrypt attempt fails.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * </p>
     * 
     * @return Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted.
     *         The default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption
     *         KMS keys.</p>
     *         <p>
     *         Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm,
     *         the decrypt attempt fails.
     *         </p>
     *         <p>
     *         This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * @see EncryptionAlgorithmSpec
     */

    public String getSourceEncryptionAlgorithm() {
        return this.sourceEncryptionAlgorithm;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted. The
     * default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption KMS keys.
     * </p>
     * <p>
     * Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm, the
     * decrypt attempt fails.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * </p>
     * 
     * @param sourceEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted.
     *        The default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption
     *        KMS keys.</p>
     *        <p>
     *        Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm,
     *        the decrypt attempt fails.
     *        </p>
     *        <p>
     *        This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    public ReEncryptRequest withSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        setSourceEncryptionAlgorithm(sourceEncryptionAlgorithm);
        return this;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted. The
     * default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption KMS keys.
     * </p>
     * <p>
     * Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm, the
     * decrypt attempt fails.
     * </p>
     * <p>
     * This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * </p>
     * 
     * @param sourceEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to decrypt the ciphertext before it is reencrypted.
     *        The default value, <code>SYMMETRIC_DEFAULT</code>, represents the algorithm used for symmetric encryption
     *        KMS keys.</p>
     *        <p>
     *        Specify the same algorithm that was used to encrypt the ciphertext. If you specify a different algorithm,
     *        the decrypt attempt fails.
     *        </p>
     *        <p>
     *        This parameter is required only when the ciphertext was encrypted under an asymmetric KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */
    //IBM unsupported
    // public ReEncryptRequest withSourceEncryptionAlgorithm(EncryptionAlgorithmSpec sourceEncryptionAlgorithm) {
    //     this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm.toString();
    //     return this;
    // }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The default
     * value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric encryption KMS
     * keys.
     * </p>
     * <p>
     * This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * </p>
     * 
     * @param destinationEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The
     *        default value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric
     *        encryption KMS keys.</p>
     *        <p>
     *        This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * @see EncryptionAlgorithmSpec
     */

    public void setDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The default
     * value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric encryption KMS
     * keys.
     * </p>
     * <p>
     * This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * </p>
     * 
     * @return Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The
     *         default value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric
     *         encryption KMS keys.</p>
     *         <p>
     *         This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * @see EncryptionAlgorithmSpec
     */

    public String getDestinationEncryptionAlgorithm() {
        return this.destinationEncryptionAlgorithm;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The default
     * value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric encryption KMS
     * keys.
     * </p>
     * <p>
     * This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * </p>
     * 
     * @param destinationEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The
     *        default value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric
     *        encryption KMS keys.</p>
     *        <p>
     *        This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    public ReEncryptRequest withDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        setDestinationEncryptionAlgorithm(destinationEncryptionAlgorithm);
        return this;
    }

    /**
     * <p>
     * Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The default
     * value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric encryption KMS
     * keys.
     * </p>
     * <p>
     * This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * </p>
     * 
     * @param destinationEncryptionAlgorithm
     *        Specifies the encryption algorithm that KMS will use to reecrypt the data after it has decrypted it. The
     *        default value, <code>SYMMETRIC_DEFAULT</code>, represents the encryption algorithm used for symmetric
     *        encryption KMS keys.</p>
     *        <p>
     *        This parameter is required only when the destination KMS key is an asymmetric KMS key.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */
    //IBM unsupported
    // public ReEncryptRequest withDestinationEncryptionAlgorithm(EncryptionAlgorithmSpec destinationEncryptionAlgorithm) {
    //     this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm.toString();
    //     return this;
    // }

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

    public ReEncryptRequest withGrantTokens(String... grantTokens) {
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

    public ReEncryptRequest withGrantTokens(java.util.Collection<String> grantTokens) {
        setGrantTokens(grantTokens);
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

    public ReEncryptRequest withDryRun(Boolean dryRun) {
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
        if (getCiphertextBlob() != null)
            sb.append("CiphertextBlob: ").append(getCiphertextBlob()).append(",");
        if (getSourceEncryptionContext() != null)
            sb.append("SourceEncryptionContext: ").append(getSourceEncryptionContext()).append(",");
        if (getSourceKeyId() != null)
            sb.append("SourceKeyId: ").append(getSourceKeyId()).append(",");
        if (getDestinationKeyId() != null)
            sb.append("DestinationKeyId: ").append(getDestinationKeyId()).append(",");
        if (getDestinationEncryptionContext() != null)
            sb.append("DestinationEncryptionContext: ").append(getDestinationEncryptionContext()).append(",");
        if (getSourceEncryptionAlgorithm() != null)
            sb.append("SourceEncryptionAlgorithm: ").append(getSourceEncryptionAlgorithm()).append(",");
        if (getDestinationEncryptionAlgorithm() != null)
            sb.append("DestinationEncryptionAlgorithm: ").append(getDestinationEncryptionAlgorithm()).append(",");
        if (getGrantTokens() != null)
            sb.append("GrantTokens: ").append(getGrantTokens()).append(",");
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

        if (obj instanceof ReEncryptRequest == false)
            return false;
        ReEncryptRequest other = (ReEncryptRequest) obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null)
            return false;
        if (other.getCiphertextBlob() != null && other.getCiphertextBlob().equals(this.getCiphertextBlob()) == false)
            return false;
        if (other.getSourceEncryptionContext() == null ^ this.getSourceEncryptionContext() == null)
            return false;
        if (other.getSourceEncryptionContext() != null && other.getSourceEncryptionContext().equals(this.getSourceEncryptionContext()) == false)
            return false;
        if (other.getSourceKeyId() == null ^ this.getSourceKeyId() == null)
            return false;
        if (other.getSourceKeyId() != null && other.getSourceKeyId().equals(this.getSourceKeyId()) == false)
            return false;
        if (other.getDestinationKeyId() == null ^ this.getDestinationKeyId() == null)
            return false;
        if (other.getDestinationKeyId() != null && other.getDestinationKeyId().equals(this.getDestinationKeyId()) == false)
            return false;
        if (other.getDestinationEncryptionContext() == null ^ this.getDestinationEncryptionContext() == null)
            return false;
        if (other.getDestinationEncryptionContext() != null && other.getDestinationEncryptionContext().equals(this.getDestinationEncryptionContext()) == false)
            return false;
        if (other.getSourceEncryptionAlgorithm() == null ^ this.getSourceEncryptionAlgorithm() == null)
            return false;
        if (other.getSourceEncryptionAlgorithm() != null && other.getSourceEncryptionAlgorithm().equals(this.getSourceEncryptionAlgorithm()) == false)
            return false;
        if (other.getDestinationEncryptionAlgorithm() == null ^ this.getDestinationEncryptionAlgorithm() == null)
            return false;
        if (other.getDestinationEncryptionAlgorithm() != null
                && other.getDestinationEncryptionAlgorithm().equals(this.getDestinationEncryptionAlgorithm()) == false)
            return false;
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null)
            return false;
        if (other.getGrantTokens() != null && other.getGrantTokens().equals(this.getGrantTokens()) == false)
            return false;
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

        hashCode = prime * hashCode + ((getCiphertextBlob() == null) ? 0 : getCiphertextBlob().hashCode());
        hashCode = prime * hashCode + ((getSourceEncryptionContext() == null) ? 0 : getSourceEncryptionContext().hashCode());
        hashCode = prime * hashCode + ((getSourceKeyId() == null) ? 0 : getSourceKeyId().hashCode());
        hashCode = prime * hashCode + ((getDestinationKeyId() == null) ? 0 : getDestinationKeyId().hashCode());
        hashCode = prime * hashCode + ((getDestinationEncryptionContext() == null) ? 0 : getDestinationEncryptionContext().hashCode());
        hashCode = prime * hashCode + ((getSourceEncryptionAlgorithm() == null) ? 0 : getSourceEncryptionAlgorithm().hashCode());
        hashCode = prime * hashCode + ((getDestinationEncryptionAlgorithm() == null) ? 0 : getDestinationEncryptionAlgorithm().hashCode());
        hashCode = prime * hashCode + ((getGrantTokens() == null) ? 0 : getGrantTokens().hashCode());
        hashCode = prime * hashCode + ((getDryRun() == null) ? 0 : getDryRun().hashCode());
        return hashCode;
    }

    @Override
    public ReEncryptRequest clone() {
        return (ReEncryptRequest) super.clone();
    }

}
