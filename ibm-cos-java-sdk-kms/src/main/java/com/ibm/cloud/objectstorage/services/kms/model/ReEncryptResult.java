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

/**
 * 
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ReEncrypt" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class ReEncryptResult extends com.ibm.cloud.objectstorage.AmazonWebServiceResult<com.ibm.cloud.objectstorage.ResponseMetadata> implements Serializable, Cloneable {

    /**
     * <p>
     * The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is Base64-encoded.
     * Otherwise, it is not Base64-encoded.
     * </p>
     */
    private java.nio.ByteBuffer ciphertextBlob;
    /**
     * <p>
     * Unique identifier of the KMS key used to originally encrypt the data.
     * </p>
     */
    private String sourceKeyId;
    /**
     * <p>
     * The Amazon Resource Name (<a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of the KMS
     * key that was used to reencrypt the data.
     * </p>
     */
    private String keyId;
    /**
     * <p>
     * The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * </p>
     */
    private String sourceEncryptionAlgorithm;
    /**
     * <p>
     * The encryption algorithm that was used to reencrypt the data.
     * </p>
     */
    private String destinationEncryptionAlgorithm;

    /**
     * <p>
     * The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is Base64-encoded.
     * Otherwise, it is not Base64-encoded.
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
     *        The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is
     *        Base64-encoded. Otherwise, it is not Base64-encoded.
     */

    public void setCiphertextBlob(java.nio.ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    /**
     * <p>
     * The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is Base64-encoded.
     * Otherwise, it is not Base64-encoded.
     * </p>
     * <p>
     * {@code ByteBuffer}s are stateful. Calling their {@code get} methods changes their {@code position}. We recommend
     * using {@link java.nio.ByteBuffer#asReadOnlyBuffer()} to create a read-only view of the buffer with an independent
     * {@code position}, and calling {@code get} methods on this rather than directly on the returned {@code ByteBuffer}.
     * Doing so will ensure that anyone else using the {@code ByteBuffer} will not be affected by changes to the
     * {@code position}.
     * </p>
     * 
     * @return The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is
     *         Base64-encoded. Otherwise, it is not Base64-encoded.
     */

    public java.nio.ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    /**
     * <p>
     * The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is Base64-encoded.
     * Otherwise, it is not Base64-encoded.
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
     *        The reencrypted data. When you use the HTTP API or the Amazon Web Services CLI, the value is
     *        Base64-encoded. Otherwise, it is not Base64-encoded.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptResult withCiphertextBlob(java.nio.ByteBuffer ciphertextBlob) {
        setCiphertextBlob(ciphertextBlob);
        return this;
    }

    /**
     * <p>
     * Unique identifier of the KMS key used to originally encrypt the data.
     * </p>
     * 
     * @param sourceKeyId
     *        Unique identifier of the KMS key used to originally encrypt the data.
     */

    public void setSourceKeyId(String sourceKeyId) {
        this.sourceKeyId = sourceKeyId;
    }

    /**
     * <p>
     * Unique identifier of the KMS key used to originally encrypt the data.
     * </p>
     * 
     * @return Unique identifier of the KMS key used to originally encrypt the data.
     */

    public String getSourceKeyId() {
        return this.sourceKeyId;
    }

    /**
     * <p>
     * Unique identifier of the KMS key used to originally encrypt the data.
     * </p>
     * 
     * @param sourceKeyId
     *        Unique identifier of the KMS key used to originally encrypt the data.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptResult withSourceKeyId(String sourceKeyId) {
        setSourceKeyId(sourceKeyId);
        return this;
    }

    /**
     * <p>
     * The Amazon Resource Name (<a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of the KMS
     * key that was used to reencrypt the data.
     * </p>
     * 
     * @param keyId
     *        The Amazon Resource Name (<a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of
     *        the KMS key that was used to reencrypt the data.
     */

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * <p>
     * The Amazon Resource Name (<a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of the KMS
     * key that was used to reencrypt the data.
     * </p>
     * 
     * @return The Amazon Resource Name (<a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of
     *         the KMS key that was used to reencrypt the data.
     */

    public String getKeyId() {
        return this.keyId;
    }

    /**
     * <p>
     * The Amazon Resource Name (<a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of the KMS
     * key that was used to reencrypt the data.
     * </p>
     * 
     * @param keyId
     *        The Amazon Resource Name (<a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#key-id-key-ARN">key ARN</a>) of
     *        the KMS key that was used to reencrypt the data.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ReEncryptResult withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * </p>
     *
     * @param sourceEncryptionAlgorithm
     *        The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * @see EncryptionAlgorithmSpec
     */

    public void setSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm;
    }

    /**
     * <p>
     * The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * </p>
     *
     * @return The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * @see EncryptionAlgorithmSpec
     */

    public String getSourceEncryptionAlgorithm() {
        return this.sourceEncryptionAlgorithm;
    }

    /**
     * <p>
     * The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * </p>
     *
     * @param sourceEncryptionAlgorithm
     *        The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    public ReEncryptResult withSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        setSourceEncryptionAlgorithm(sourceEncryptionAlgorithm);
        return this;
    }

    /**
     * <p>
     * The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * </p>
     *
     * @param sourceEncryptionAlgorithm
     *        The encryption algorithm that was used to decrypt the ciphertext before it was reencrypted.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */
    //IBM unsupported
    // public ReEncryptResult withSourceEncryptionAlgorithm(EncryptionAlgorithmSpec sourceEncryptionAlgorithm) {
    //     this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm.toString();
    //     return this;
    // }

    /**
     * <p>
     * The encryption algorithm that was used to reencrypt the data.
     * </p>
     *
     * @param destinationEncryptionAlgorithm
     *        The encryption algorithm that was used to reencrypt the data.
     * @see EncryptionAlgorithmSpec
     */

    public void setDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm;
    }

    /**
     * <p>
     * The encryption algorithm that was used to reencrypt the data.
     * </p>
     *
     * @return The encryption algorithm that was used to reencrypt the data.
     * @see EncryptionAlgorithmSpec
     */

    public String getDestinationEncryptionAlgorithm() {
        return this.destinationEncryptionAlgorithm;
    }

    /**
     * <p>
     * The encryption algorithm that was used to reencrypt the data.
     * </p>
     *
     * @param destinationEncryptionAlgorithm
     *        The encryption algorithm that was used to reencrypt the data.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */

    public ReEncryptResult withDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        setDestinationEncryptionAlgorithm(destinationEncryptionAlgorithm);
        return this;
    }

    /**
     * <p>
     * The encryption algorithm that was used to reencrypt the data.
     * </p>
     *
     * @param destinationEncryptionAlgorithm
     *        The encryption algorithm that was used to reencrypt the data.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see EncryptionAlgorithmSpec
     */
    //IBM unsupported
    // public ReEncryptResult withDestinationEncryptionAlgorithm(EncryptionAlgorithmSpec destinationEncryptionAlgorithm) {
    //     this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm.toString();
    //     return this;
    // }

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
        if (getSourceKeyId() != null)
            sb.append("SourceKeyId: ").append(getSourceKeyId()).append(",");
        if (getKeyId() != null)
            sb.append("KeyId: ").append(getKeyId()).append(",");
        if (getSourceEncryptionAlgorithm() != null)
            sb.append("SourceEncryptionAlgorithm: ").append(getSourceEncryptionAlgorithm()).append(",");
        if (getDestinationEncryptionAlgorithm() != null)
            sb.append("DestinationEncryptionAlgorithm: ").append(getDestinationEncryptionAlgorithm());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof ReEncryptResult == false)
            return false;
        ReEncryptResult other = (ReEncryptResult) obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null)
            return false;
        if (other.getCiphertextBlob() != null && other.getCiphertextBlob().equals(this.getCiphertextBlob()) == false)
            return false;
        if (other.getSourceKeyId() == null ^ this.getSourceKeyId() == null)
            return false;
        if (other.getSourceKeyId() != null && other.getSourceKeyId().equals(this.getSourceKeyId()) == false)
            return false;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
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
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getCiphertextBlob() == null) ? 0 : getCiphertextBlob().hashCode());
        hashCode = prime * hashCode + ((getSourceKeyId() == null) ? 0 : getSourceKeyId().hashCode());
        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getSourceEncryptionAlgorithm() == null) ? 0 : getSourceEncryptionAlgorithm().hashCode());
        hashCode = prime * hashCode + ((getDestinationEncryptionAlgorithm() == null) ? 0 : getDestinationEncryptionAlgorithm().hashCode());
        return hashCode;
    }

    @Override
    public ReEncryptResult clone() {
        try {
            return (ReEncryptResult) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }

}
