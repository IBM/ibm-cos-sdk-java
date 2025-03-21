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
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/DescribeKey" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class DescribeKeyResult extends com.ibm.cloud.objectstorage.AmazonWebServiceResult<com.ibm.cloud.objectstorage.ResponseMetadata> implements Serializable, Cloneable {

    /**
     * <p>
     * Metadata associated with the key.
     * </p>
     */
    private KeyMetadata keyMetadata;

    /**
     * <p>
     * Metadata associated with the key.
     * </p>
     *
     * @param keyMetadata
     *        Metadata associated with the key.
     */

    public void setKeyMetadata(KeyMetadata keyMetadata) {
        this.keyMetadata = keyMetadata;
    }

    /**
     * <p>
     * Metadata associated with the key.
     * </p>
     *
     * @return Metadata associated with the key.
     */

    public KeyMetadata getKeyMetadata() {
        return this.keyMetadata;
    }

    /**
     * <p>
     * Metadata associated with the key.
     * </p>
     *
     * @param keyMetadata
     *        Metadata associated with the key.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public DescribeKeyResult withKeyMetadata(KeyMetadata keyMetadata) {
        setKeyMetadata(keyMetadata);
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
        if (getKeyMetadata() != null)
            sb.append("KeyMetadata: ").append(getKeyMetadata());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof DescribeKeyResult == false)
            return false;
        DescribeKeyResult other = (DescribeKeyResult) obj;
        if (other.getKeyMetadata() == null ^ this.getKeyMetadata() == null)
            return false;
        if (other.getKeyMetadata() != null && other.getKeyMetadata().equals(this.getKeyMetadata()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getKeyMetadata() == null) ? 0 : getKeyMetadata().hashCode());
        return hashCode;
    }

    @Override
    public DescribeKeyResult clone() {
        try {
            return (DescribeKeyResult) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() " + "even though we're Cloneable!", e);
        }
    }

}
