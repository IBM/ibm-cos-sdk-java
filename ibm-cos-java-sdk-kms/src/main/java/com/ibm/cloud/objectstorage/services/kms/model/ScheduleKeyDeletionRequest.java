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
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/ScheduleKeyDeletion" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.ibm.cloud.objectstorage:ibm-cos-java-sdk-code-generator")
public class ScheduleKeyDeletionRequest extends com.ibm.cloud.objectstorage.AmazonWebServiceRequest implements Serializable, Cloneable {

    /**
     * <p>
     * The unique identifier of the KMS key to delete.
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
     * The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS key.
     * </p>
     * <p>
     * If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of its
     * replica keys is deleted. Otherwise, the waiting period begins immediately.
     * </p>
     * <p>
     * This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not include a
     * value, it defaults to 30. You can use the <a href=
     * "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     * > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the values that
     * principals can specify in the <code>PendingWindowInDays</code> parameter.
     * </p>
     */
    private Integer pendingWindowInDays;

    /**
     * <p>
     * The unique identifier of the KMS key to delete.
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
     *        The unique identifier of the KMS key to delete.</p>
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
     * The unique identifier of the KMS key to delete.
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
     * @return The unique identifier of the KMS key to delete.</p>
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
     * The unique identifier of the KMS key to delete.
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
     *        The unique identifier of the KMS key to delete.</p>
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

    public ScheduleKeyDeletionRequest withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS key.
     * </p>
     * <p>
     * If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of its
     * replica keys is deleted. Otherwise, the waiting period begins immediately.
     * </p>
     * <p>
     * This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not include a
     * value, it defaults to 30. You can use the <a href=
     * "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     * > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the values that
     * principals can specify in the <code>PendingWindowInDays</code> parameter.
     * </p>
     * 
     * @param pendingWindowInDays
     *        The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS
     *        key.</p>
     *        <p>
     *        If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of
     *        its replica keys is deleted. Otherwise, the waiting period begins immediately.
     *        </p>
     *        <p>
     *        This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not
     *        include a value, it defaults to 30. You can use the <a href=
     *        "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     *        > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the
     *        values that principals can specify in the <code>PendingWindowInDays</code> parameter.
     */

    public void setPendingWindowInDays(Integer pendingWindowInDays) {
        this.pendingWindowInDays = pendingWindowInDays;
    }

    /**
     * <p>
     * The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS key.
     * </p>
     * <p>
     * If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of its
     * replica keys is deleted. Otherwise, the waiting period begins immediately.
     * </p>
     * <p>
     * This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not include a
     * value, it defaults to 30. You can use the <a href=
     * "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     * > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the values that
     * principals can specify in the <code>PendingWindowInDays</code> parameter.
     * </p>
     * 
     * @return The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS
     *         key.</p>
     *         <p>
     *         If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last
     *         of its replica keys is deleted. Otherwise, the waiting period begins immediately.
     *         </p>
     *         <p>
     *         This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not
     *         include a value, it defaults to 30. You can use the <a href=
     *         "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     *         > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the
     *         values that principals can specify in the <code>PendingWindowInDays</code> parameter.
     */

    public Integer getPendingWindowInDays() {
        return this.pendingWindowInDays;
    }

    /**
     * <p>
     * The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS key.
     * </p>
     * <p>
     * If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of its
     * replica keys is deleted. Otherwise, the waiting period begins immediately.
     * </p>
     * <p>
     * This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not include a
     * value, it defaults to 30. You can use the <a href=
     * "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     * > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the values that
     * principals can specify in the <code>PendingWindowInDays</code> parameter.
     * </p>
     * 
     * @param pendingWindowInDays
     *        The waiting period, specified in number of days. After the waiting period ends, KMS deletes the KMS
     *        key.</p>
     *        <p>
     *        If the KMS key is a multi-Region primary key with replica keys, the waiting period begins when the last of
     *        its replica keys is deleted. Otherwise, the waiting period begins immediately.
     *        </p>
     *        <p>
     *        This value is optional. If you include a value, it must be between 7 and 30, inclusive. If you do not
     *        include a value, it defaults to 30. You can use the <a href=
     *        "https://docs.aws.amazon.com/kms/latest/developerguide/conditions-kms.html#conditions-kms-schedule-key-deletion-pending-window-in-days"
     *        > <code>kms:ScheduleKeyDeletionPendingWindowInDays</code> </a> condition key to further constrain the
     *        values that principals can specify in the <code>PendingWindowInDays</code> parameter.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public ScheduleKeyDeletionRequest withPendingWindowInDays(Integer pendingWindowInDays) {
        setPendingWindowInDays(pendingWindowInDays);
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
        if (getPendingWindowInDays() != null)
            sb.append("PendingWindowInDays: ").append(getPendingWindowInDays());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof ScheduleKeyDeletionRequest == false)
            return false;
        ScheduleKeyDeletionRequest other = (ScheduleKeyDeletionRequest) obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
            return false;
        if (other.getPendingWindowInDays() == null ^ this.getPendingWindowInDays() == null)
            return false;
        if (other.getPendingWindowInDays() != null && other.getPendingWindowInDays().equals(this.getPendingWindowInDays()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getPendingWindowInDays() == null) ? 0 : getPendingWindowInDays().hashCode());
        return hashCode;
    }

    @Override
    public ScheduleKeyDeletionRequest clone() {
        return (ScheduleKeyDeletionRequest) super.clone();
    }

}
