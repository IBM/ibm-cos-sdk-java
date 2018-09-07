/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.ibm.cloud.objectstorage.services.s3.model;

/**
 * <p>
 * Specifies constants that define Amazon S3 storage classes. The standard storage class
 * is the default storage class.
 * </p>
 * <p>
 * Amazon S3 offers multiple storage classes for different customers' needs. The
 * <code>STANDARD</code> storage class is the default storage class, and means that
 * redundant copies of data will be stored in different locations.
 * </p>
 * <p>
 */
public enum StorageClass {

    /**
     * The Amazon Glacier storage class.
     * This storage class means your object's data is stored in Amazon Glacier,
     * and Amazon S3 stores a reference to the data in the Amazon S3 bucket.
     */
    Glacier("GLACIER"),
    
    ;

    /**
     * Returns the Amazon S3 {@link StorageClass} enumeration value representing the
     * specified Amazon S3 <code>StorageClass</code> ID string.
     * If the specified string doesn't map to a known Amazon S3 storage class,
     * an <code>IllegalArgumentException</code> is thrown.
     *
     * @param s3StorageClassString
     *            The Amazon S3 storage class ID string.
     *
     * @return The Amazon S3 <code>StorageClass</code> enumeration value representing the
     *         specified Amazon S3 storage class ID.
     *
     * @throws IllegalArgumentException
     *             If the specified value does not map to one of the known
     *             Amazon S3 storage classes.
     */
    public static StorageClass fromValue(String s3StorageClassString) throws IllegalArgumentException {
        for (StorageClass storageClass : StorageClass.values()) {
            if (storageClass.toString().equals(s3StorageClassString)) return storageClass;
        }

        throw new IllegalArgumentException(
                "Cannot create enum from " + s3StorageClassString + " value!");
    }

    private final String storageClassId;

    private StorageClass(String id) {
        this.storageClassId = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return storageClassId;
    }

}
