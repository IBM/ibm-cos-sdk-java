/*
 * Copyright 2010-2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
/**
 * Synchronous client for accessing Amazon S3.
 *
 * <h1>Appendix: Amazon S3 client-side encryption meta information</h1>
 * 
 * <h2>Introduction</h2>
 * <p>
 * This appendix summarizes the current crypto related meta information
 * associated with an S3 object encrypted using SDK client-side encryption. In
 * SDK's terminology, these meta information are classified as "user metatdata".
 * In general, the meta information is just a map of key/value pairs. Physically
 * they are persisted either as metadata of the encrypted S3 object, or as a
 * separate S3 object called an "instruction file" (which is just a JSON file in
 * plaintext.)  For all references to the Cipher Algorithm Names, Modes, and Padding,
 * such as <code>"AES/GCM/NoPadding"</code>, please refer to Oracle's <a
 * href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html"
 * >Java&trade; Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for more details.
 * 
 * <h2>S3 metadata vs instruction file</h2>
 * <p>
 * Note when a key/value pair is stored as "user metadata" as part of the S3
 * metadata, the SDK always adds an additional prefix of <b>x-amz-meta-</b> to
 * the key name. In contrast, the SDK doesn't add any prefix when the key names
 * are stored in an instruction file. (The reason has to do with how the http
 * headers are handled.)
 * <p>
 * For example, <b>x-amz-key</b> would get stored as <b>x-amz-meta-x-amz-key</b>
 * in S3 metadata. In contrast, <b>x-amz-key</b> would simply be stored as is
 * (ie without the prefix) in an instruction file. All the keys described below
 * are considered "user metadata" by the SDK.
 * 
 * <h2>S3 1st gen crypto meta information</h2>
 * <p>
 * For non-KMS client side encryption, this crypto meta information is used
 * to support the "Encryption Only" crypto mode (which involves the use of
 * <code>AES/CBC/PKCS5Padding</code> for content encryption, and
 * <code>AES/ECB</code> for encrypting the one-time randomly generated data
 * key). For KMS client-side encryption, the 2nd gen crypto meta information is used.
 * <h3>Scope</h3>
 * This metadata can only be generated when encrypting using the V1 encryption client,
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}, but can be interpreted (decrypted) by
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClientV2}.
 * The format is also known as v1 metadata, but because there are now v1 and v2 clients,
 * the term v1 metadata is confusing.
 * <p>
 * <b>Encryption:</b> {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}, crypto mode EncryptionOnly<br>
 * <b>Decryption:</b> {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}, crypto mode EncryptionOnly or
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClientV2}, crypto mode AuthenticationOnly
 * <p>
 * <h3>Metadata</h3>
 * <table border=1>
 * <tr aligned="left">
 * <th>key</th>
 * <th>description</th>
 * </tr>
 * <tr>
 * <td width="200"><b>x-amz-key</b></td>
 * <td>Content encrypting key (cek) in encrypted form, base64 encoded. The cek
 * is randomly generated per S3 object, and is always an AES 256-bit key. The
 * corresponding cipher is always "AES/CBC/PKCS5Padding".</td>
 * </tr>
 * <tr>
 * <td><b>x-amz-iv</b></td>
 * <td>Randomly generated IV (per S3 object), base64 encoded</td>
 * </tr>
 * <tr>
 * <td><b>x-amz-matdesc </b></td>
 * <td>Customer provided material description in JSON (UTF8) format. Used to
 * identify the client-side master key (ie used to encrypt/wrap the generated
 * content encrypting key).</td>
 * </tr>
 * <tr>
 * <td><b>x-amz-unencrypted-content-length</b></td>
 * <td>Unencrypted content length (optional but should be specified whenever
 * possible).</td>
 * </tr>
 * </table>
 * <p>
 * <h2>S3 2nd gen crypto meta information</h2>
 * <p>
 * This crypto meta information is used to support both authenticated
 * encryption (which involves the use of <code>AES/GCM/NoPadding</code> for
 * content encryption, and key wrapping for the one-time randomly generated data
 * key), and KMS client-side encryption (which can either be encryption-only or
 * authenticated encryption).
 * <h3>Scope</h3>
 * 2nd gen metadata can be generated/encrypted by both the V1 and V2 encryption clients.
 * The format is also known as v2 metadata, but this is confusing since the V1 client also generates
 * 2nd gen metadata in the AuthenticatedEncryption and StrictAuthenticatedEncryption modes.
 * <p>
 * <b>Encryption:</b> {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}/
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}, crypto modes AuthenticatedEncryption and
 * StrictAuthenticatedEncryption<br>
 * <b>Decryption:</b> {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClient}, all crypto modes, or
 * {@link com.ibm.cloud.objectstorage.services.s3.AmazonS3EncryptionClientV2}, crypto modes AuthenticatedEncryption and
 * StrictAuthenticatedEncryption.
 * <p>
 * <b>Note:</b>
 * While the V1 client can both generate and interpret 2nd gen metadata, it cannot decrypt any object encrypted
 * using the V2 client, because the values of <b>x-amz-wrap-alg</b> differ.
 * <p>
 * <h3>Metadata used the same way by V1 and V2 clients</h3>
 * <table border=1>
 * <tr aligned="left">
 * <th>key</th>
 * <th>description</th>
 * </tr>
 * <tr>
 * <td width="200"><b>x-amz-key-v2</b></td>
 * <td>CEK in key wrapped form. This is necessary so that the S3 encryption
 * client that doesn't recognize the v2 format will not mistakenly decrypt S3
 * object encrypted in v2 format.</td>
 * </tr>
 * <tr>
 * <td><b>x-amz-iv</b></td>
 * <td>Randomly generated IV (per S3 object), base64 encoded. (Same as v1.)</td>
 * </tr>
 * <td><b>x-amz-unencrypted-content-length</b></td>
 * <td>Unencrypted content length. (optional but should be specified whenever
 * possible. Same as v1.)</td>
 * </tr>
 * <tr>
 * <td><b>x-amz-tag-len</b></td>
 * <td>Tag length (in bits) when AEAD is in use.
 * <ul>
 * <li>Only applicable if AEAD is in use. This meta information is absent
 * otherwise, or if KMS is in use.</li>
 * <li>Supported value: <code>"128"</code></li>
 * </ul>
 * </td>
 * </tr>
 * </table>
 * <h3>Metadata using V1 client</h3>
 * <table border=1>
 * <tr aligned="left">
 * <th>key</th>
 * <th>description</th>
 * </tr>
 * <td><b>x-amz-matdesc</b></td>
 * <td>Customer provided material description in JSON format. (Same as v1). Used
 * to identify the client-side master key.
 * <ul>
 * <li>For KMS client side encryption, the KMS Customer Master Key ID is stored
 * as part of the material description, <b>x-amz-matdesc</b>, under the key-name
 * <code>"kms_cmk_id"</code>.</li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td><b>x-amz-wrap-alg</b></td>
 * <td>Key wrapping algorithm used.
 * <ul>
 * <li>Supported values:
 * <code>"AESWrap", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "kms"</code></li>
 * <li>No standard key wrapping is used if this meta information is absent</li>
 * <li>Always set to <b>"kms"</b> if KMS is used for client-side encryption</li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td><b>x-amz-cek-alg</b></td>
 * <td>Content encryption algorithm used.
 * <ul>
 * <li>Supported values:
 * <code>"AES/GCM/NoPadding", "AES/CBC/PKCS5Padding"</code></li>
 * <li>Default to <code>"AES/CBC/PKCS5Padding"</code> if this key is absent.</li>
 * </ul>
 * </td>
 * </tr>
 * </table>
 * <h3>Metadata using V2 client</h3>
 * <table border=1>
 * <tr aligned="left">
 * <th>key</th>
 * <th>description</th>
 * </tr>
 * <tr>
 * <td><b>x-amz-matdesc</b></td>
 * <td>Customer provided material description in JSON format. (Same as v1). For KMS client side encryption,
 * the cek algorithm is stored as part of the material description under the key-name <code>aws:x-amz-cek-alg</code>.
 * </td>
 * </tr>
 * <tr>
 * <td><b>x-amz-wrap-alg</b></td>
 * <td>Key wrapping algorithm used.
 * <ul>
 * <li>Supported values:
 * <code>"AES/GCM/NoPadding" (symmetric default), "RSA-OAEP-SHA1", "RSA-OAEP-SHA1" (asymmetric default), "kms"</code>
 * </li>
 * <li>No standard key wrapping is used if this meta information is absent</li>
 * <li>Always set to <b>"kms"</b> if KMS is used for client-side encryption</li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td><b>x-amz-cek-alg</b></td>
 * <td>Content encryption algorithm used. Supported values: <code>"AES/GCM/NoPadding"</code>
 * </td>
 * </tr>
 * </table>
 * <p>
 * <h3>KMS Integration for client-side encryption</h3>
 * <ol>
 * <li>All client-side KMS protected S3 objects are stored in v2 crypto meta information
 * format. However, the key wrapping algorithm, <b>x-amz-wrap-alg</b> is always
 * set to <b>"kms"</b>.</li>
 * <li>For V1 clients, the KMS Customer Master Key ID is currently stored as part of the
 * material description, <b>x-amz-matdesc</b>, under the key-name
 * <b>"kms_cmk_id"</b>.</li>
 * <li>For V2 clients, the KMS Customer Master Key ID is <b>not</b> stored as part of the
 * material description and must be supplied by the decrypting client.</li>
 * </ol>
 * <p>
 * <h4>Sample S3 crypto meta information for KMS client-side encryption</h4>
 * <p>
 * <h5>V1 client, CryptoMode: EncryptionOnly</h5>
 * 
 * <pre>
 * x-amz-meta-x-amz-key-v2: 
 * CiAJ2GqwpJRnt4izujwoalC2YdNRNSMEslG5rw3pEI+J8hKnAQEBAwB4CdhqsKSUZ7eIs7o8KGpQtmHTUTUjBLJRua8N6RCPifIAAAB+MHwGCSqGSIb3DQEHBqBvMG0CAQAwaAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAyD0rzN3KHViWixDVcCARCAOwhZ6xA8rob3Z0mNx3uKFieHKVyIC/PKlchALvG1oVLnf86mcZJNpTXtwMkKkDocVj3Z1zGjZTufFsOH
 * x-amz-iv: xqOHh+M6y0UogwG5SHJM3Q==
 * x-amz-unencrypted-content-length: 32768
 * x-amz-cek-alg: AES/CBC/PKCS5Padding
 * x-amz-wrap-alg: kms
 * x-amz-matdesc: {"kms_cmk_id":"d3fd2273-4ca0-4da5-b1c2-c89742ec6a26"}
 * </pre>
 * 
 * <h5>V1 client, CryptoMode: AuthenticatedEncryption/StrictAuthenticatedEncryption</h5>
 * 
 * <pre>
 * x-amz-key-v2: CiAJ2GqwpJRnt4izujwoalC2YdNRNSMEslG5rw3pEI+J8hKnAQEBAwB4CdhqsKSUZ7eIs7o8KGpQtmHTUTUjBLJRua8N6RCPifIAAAB+MHwGCSqGSIb3DQEHBqBvMG0CAQAwaAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAytFVyW4I6QmwcPK1ECARCAO1VzoM6KxA9DHyNMm/BEbbjle2vLA7IY7f9OkUoSqrdxgKxpNID9QaL/7928m1Djtld0bJpHefjm89C
 * x-amz-iv: VZBksyij6DhrUMye
 * x-amz-unencrypted-content-length: 32768
 * x-amz-cek-alg: AES/GCM/NoPadding
 * x-amz-wrap-alg: kms
 * x-amz-matdesc: {"kms_cmk_id":"d3fd2273-4ca0-4da5-b1c2-c89742ec6a26"}
 * x-amz-tag-len: 128
 * </pre>
 *
 * <h5>V2 client, CryptoMode: AuthenticatedEncryption/StrictAuthenticatedEncryption</h5>
 *
 * <pre>
 * x-amz-key-v2: CiAJ2GqwpJRnt4izujwoalC2YdNRNSMEslG5rw3pEI+J8hKnAQEBAwB4CdhqsKSUZ7eIs7o8KGpQtmHTUTUjBLJRua8N6RCPifIAAAB+MHwGCSqGSIb3DQEHBqBvMG0CAQAwaAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAytFVyW4I6QmwcPK1ECARCAO1VzoM6KxA9DHyNMm/BEbbjle2vLA7IY7f9OkUoSqrdxgKxpNID9QaL/7928m1Djtld0bJpHefjm89C
 * x-amz-iv: VZBksyij6DhrUMye
 * x-amz-unencrypted-content-length: 32768
 * x-amz-cek-alg: AES/GCM/NoPadding
 * x-amz-wrap-alg: kms
 * x-amz-matdesc: {"aws:x-amz-cek-alg":"AES/GCM/NoPadding"}
 * x-amz-tag-len: 128
 * </pre>
 *
 * <h2>S3 metadata of an instruction file</h2>
 * <p>
 * The following key is always included as part of the S3 metadata of the
 * instruction file (which is just an S3 object). Since the SDK treats this key
 * as "user metadata", the key name is always prefixed with <b>x-amz-meta-</b>
 * when physically stored.
 * <p>
 * <table border=1>
 * <tr aligned="left">
 * <th>key</th>
 * <th>description</th>
 * </tr>
 * <tr>
 * <td width="200"><b>x-amz-crypto-instr-file</b></td>
 * <td>the presence of this key is used to indicate this S3 object is an
 * instruction file (of an associated client-side encrypted S3 object).</td>
 * </tr>
 * </table>
 */
package com.ibm.cloud.objectstorage.services.s3;
