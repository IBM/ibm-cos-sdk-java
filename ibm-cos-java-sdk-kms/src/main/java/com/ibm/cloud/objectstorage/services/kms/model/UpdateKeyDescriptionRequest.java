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
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/UpdateKeyDescription" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class UpdateKeyDescriptionRequest extends com.ibm.cloud.objectstorage.AmazonWebServiceRequest implements Serializable, Cloneable {

    /**
     * <p>
     * Updates the description of the specified KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key.
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
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     * </p>
     */
    private String keyId;
    /**
     * <p>
     * New description for the KMS key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     */
    private String description;

    /**
     * <p>
     * Updates the description of the specified KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key.
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
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     * </p>
     * 
     * @param keyId
     *        Updates the description of the specified KMS key.</p>
     *        <p>
     *        Specify the key ID or key ARN of the KMS key.
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
     *        </ul>
     *        <p>
     *        To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     */

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * <p>
     * Updates the description of the specified KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key.
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
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     * </p>
     * 
     * @return Updates the description of the specified KMS key.</p>
     *         <p>
     *         Specify the key ID or key ARN of the KMS key.
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
     *         </ul>
     *         <p>
     *         To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     */

    public String getKeyId() {
        return this.keyId;
    }

    /**
     * <p>
     * Updates the description of the specified KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key.
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
     * </ul>
     * <p>
     * To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     * </p>
     * 
     * @param keyId
     *        Updates the description of the specified KMS key.</p>
     *        <p>
     *        Specify the key ID or key ARN of the KMS key.
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
     *        </ul>
     *        <p>
     *        To get the key ID and key ARN for a KMS key, use <a>ListKeys</a> or <a>DescribeKey</a>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public UpdateKeyDescriptionRequest withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * New description for the KMS key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * 
     * @param description
     *        New description for the KMS key.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     */

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     * New description for the KMS key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * 
     * @return New description for the KMS key.</p> <important>
     *         <p>
     *         Do not include confidential or sensitive information in this field. This field may be displayed in
     *         plaintext in CloudTrail logs and other output.
     *         </p>
     */

    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * New description for the KMS key.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * 
     * @param description
     *        New description for the KMS key.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public UpdateKeyDescriptionRequest withDescription(String description) {
        setDescription(description);
        return this;
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
        if (getDescription() != null)
            sb.append("Description: ").append(getDescription());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof UpdateKeyDescriptionRequest == false)
            return false;
        UpdateKeyDescriptionRequest other = (UpdateKeyDescriptionRequest) obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
            return false;
        if (other.getDescription() == null ^ this.getDescription() == null)
            return false;
        if (other.getDescription() != null && other.getDescription().equals(this.getDescription()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getDescription() == null) ? 0 : getDescription().hashCode());
        return hashCode;
    }

    @Override
    public UpdateKeyDescriptionRequest clone() {
        return (UpdateKeyDescriptionRequest) super.clone();
    }

}
