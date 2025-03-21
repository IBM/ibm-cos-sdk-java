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
 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/kms-2014-11-01/CreateGrant" target="_top">AWS API
 *      Documentation</a>
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class CreateGrantRequest extends com.ibm.cloud.objectstorage.AmazonWebServiceRequest implements Serializable, Cloneable {

    /**
     * <p>
     * Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services account,
     * you must use the key ARN.
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
     * The identity that gets the permissions specified in the grant.
     * </p>
     * <p>
     * To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal. Valid
     * principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed role users.
     * For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     */
    private String granteePrincipal;
    /**
     * <p>
     * The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant.
     * </p>
     * <p>
     * To specify the principal, use the <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name (ARN)</a>
     * of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM users, IAM roles,
     * federated users, and assumed role users. For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * <p>
     * The grant determines the retiring principal. Other principals might have permission to retire the grant or revoke
     * the grant. For details, see <a>RevokeGrant</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and revoking
     * grants</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private String retiringPrincipal;
    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     */
    private com.ibm.cloud.objectstorage.internal.SdkInternalList<String> operations;
    /**
     * <p>
     * Specifies a grant constraint.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant constraints,
     * which allow the permissions in the grant only when the encryption context in the request matches (
     * <code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the encryption context
     * specified in the constraint.
     * </p>
     * <p>
     * The encryption context grant constraints are supported only on <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     * operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations on
     * symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     * <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a grant
     * constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants created with
     * the <code>CreateGrant</code> permission have an equally strict or stricter encryption context constraint.
     * </p>
     * <p>
     * You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS keys or
     * HMAC KMS keys. Operations with these keys don't support an encryption context.
     * </p>
     * <p>
     * Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     * constraint cannot exceed 384 characters. For information about grant constraints, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     * grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about encryption
     * context, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i> <i>Key Management Service Developer Guide</i> </i>.
     * </p>
     */
    private GrantConstraints constraints;
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
     * A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     * retrying this request.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     * <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended duplicates
     * when you retry the <code>CreateGrant</code> request.
     * </p>
     * <p>
     * When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if the
     * grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note that the
     * returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     * <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     * </p>
     */
    private String name;
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
     * Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services account,
     * you must use the key ARN.
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
     *        Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.</p>
     *        <p>
     *        Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services
     *        account, you must use the key ARN.
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
     * Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services account,
     * you must use the key ARN.
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
     * @return Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.</p>
     *         <p>
     *         Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services
     *         account, you must use the key ARN.
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
     * Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.
     * </p>
     * <p>
     * Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services account,
     * you must use the key ARN.
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
     *        Identifies the KMS key for the grant. The grant gives principals permission to use this KMS key.</p>
     *        <p>
     *        Specify the key ID or key ARN of the KMS key. To specify a KMS key in a different Amazon Web Services
     *        account, you must use the key ARN.
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

    public CreateGrantRequest withKeyId(String keyId) {
        setKeyId(keyId);
        return this;
    }

    /**
     * <p>
     * The identity that gets the permissions specified in the grant.
     * </p>
     * <p>
     * To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal. Valid
     * principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed role users.
     * For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * 
     * @param granteePrincipal
     *        The identity that gets the permissions specified in the grant.</p>
     *        <p>
     *        To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal.
     *        Valid principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed
     *        role users. For help with the ARN syntax for a principal, see <a
     *        href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *        ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     */

    public void setGranteePrincipal(String granteePrincipal) {
        this.granteePrincipal = granteePrincipal;
    }

    /**
     * <p>
     * The identity that gets the permissions specified in the grant.
     * </p>
     * <p>
     * To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal. Valid
     * principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed role users.
     * For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * 
     * @return The identity that gets the permissions specified in the grant.</p>
     *         <p>
     *         To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal.
     *         Valid principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed
     *         role users. For help with the ARN syntax for a principal, see <a
     *         href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *         ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     */

    public String getGranteePrincipal() {
        return this.granteePrincipal;
    }

    /**
     * <p>
     * The identity that gets the permissions specified in the grant.
     * </p>
     * <p>
     * To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal. Valid
     * principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed role users.
     * For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * 
     * @param granteePrincipal
     *        The identity that gets the permissions specified in the grant.</p>
     *        <p>
     *        To specify the grantee principal, use the Amazon Resource Name (ARN) of an Amazon Web Services principal.
     *        Valid principals include Amazon Web Services accounts, IAM users, IAM roles, federated users, and assumed
     *        role users. For help with the ARN syntax for a principal, see <a
     *        href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *        ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withGranteePrincipal(String granteePrincipal) {
        setGranteePrincipal(granteePrincipal);
        return this;
    }

    /**
     * <p>
     * The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant.
     * </p>
     * <p>
     * To specify the principal, use the <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name (ARN)</a>
     * of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM users, IAM roles,
     * federated users, and assumed role users. For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * <p>
     * The grant determines the retiring principal. Other principals might have permission to retire the grant or revoke
     * the grant. For details, see <a>RevokeGrant</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and revoking
     * grants</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     *
     * @param retiringPrincipal
     *        The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant. </p>
     *        <p>
     *        To specify the principal, use the <a
     *        href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name
     *        (ARN)</a> of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM
     *        users, IAM roles, federated users, and assumed role users. For help with the ARN syntax for a principal,
     *        see <a
     *        href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *        ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     *        </p>
     *        <p>
     *        The grant determines the retiring principal. Other principals might have permission to retire the grant or
     *        revoke the grant. For details, see <a>RevokeGrant</a> and <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and
     *        revoking grants</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public void setRetiringPrincipal(String retiringPrincipal) {
        this.retiringPrincipal = retiringPrincipal;
    }

    /**
     * <p>
     * The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant.
     * </p>
     * <p>
     * To specify the principal, use the <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name (ARN)</a>
     * of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM users, IAM roles,
     * federated users, and assumed role users. For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * <p>
     * The grant determines the retiring principal. Other principals might have permission to retire the grant or revoke
     * the grant. For details, see <a>RevokeGrant</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and revoking
     * grants</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     *
     * @return The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant. </p>
     *         <p>
     *         To specify the principal, use the <a
     *         href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name
     *         (ARN)</a> of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM
     *         users, IAM roles, federated users, and assumed role users. For help with the ARN syntax for a principal,
     *         see <a
     *         href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *         ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     *         </p>
     *         <p>
     *         The grant determines the retiring principal. Other principals might have permission to retire the grant
     *         or revoke the grant. For details, see <a>RevokeGrant</a> and <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and
     *         revoking grants</a> in the <i>Key Management Service Developer Guide</i>.
     */

    public String getRetiringPrincipal() {
        return this.retiringPrincipal;
    }

    /**
     * <p>
     * The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant.
     * </p>
     * <p>
     * To specify the principal, use the <a
     * href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name (ARN)</a>
     * of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM users, IAM roles,
     * federated users, and assumed role users. For help with the ARN syntax for a principal, see <a
     * href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM ARNs</a>
     * in the <i> <i>Identity and Access Management User Guide</i> </i>.
     * </p>
     * <p>
     * The grant determines the retiring principal. Other principals might have permission to retire the grant or revoke
     * the grant. For details, see <a>RevokeGrant</a> and <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and revoking
     * grants</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     *
     * @param retiringPrincipal
     *        The principal that has permission to use the <a>RetireGrant</a> operation to retire the grant. </p>
     *        <p>
     *        To specify the principal, use the <a
     *        href="https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html">Amazon Resource Name
     *        (ARN)</a> of an Amazon Web Services principal. Valid principals include Amazon Web Services accounts, IAM
     *        users, IAM roles, federated users, and assumed role users. For help with the ARN syntax for a principal,
     *        see <a
     *        href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns">IAM
     *        ARNs</a> in the <i> <i>Identity and Access Management User Guide</i> </i>.
     *        </p>
     *        <p>
     *        The grant determines the retiring principal. Other principals might have permission to retire the grant or
     *        revoke the grant. For details, see <a>RevokeGrant</a> and <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#grant-delete">Retiring and
     *        revoking grants</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withRetiringPrincipal(String retiringPrincipal) {
        setRetiringPrincipal(retiringPrincipal);
        return this;
    }

    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @return A list of operations that the grant permits. </p>
     *         <p>
     *         This list must include only operations that are permitted in a grant. Also, the operation must be
     *         supported on the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that
     *         allows the <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the
     *         <a>GenerateDataKey</a> operation. If you try, KMS returns a <code>ValidationError</code> exception. For
     *         details, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     *         operations</a> in the <i>Key Management Service Developer Guide</i>.
     * @see GrantOperation
     */

    public java.util.List<String> getOperations() {
        if (operations == null) {
            operations = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>();
        }
        return operations;
    }

    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param operations
     *        A list of operations that the grant permits. </p>
     *        <p>
     *        This list must include only operations that are permitted in a grant. Also, the operation must be
     *        supported on the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that
     *        allows the <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the
     *        <a>GenerateDataKey</a> operation. If you try, KMS returns a <code>ValidationError</code> exception. For
     *        details, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     *        operations</a> in the <i>Key Management Service Developer Guide</i>.
     * @see GrantOperation
     */

    public void setOperations(java.util.Collection<String> operations) {
        if (operations == null) {
            this.operations = null;
            return;
        }

        this.operations = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(operations);
    }

    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * <p>
     * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
     * {@link #setOperations(java.util.Collection)} or {@link #withOperations(java.util.Collection)} if you want to
     * override the existing values.
     * </p>
     *
     * @param operations
     *        A list of operations that the grant permits. </p>
     *        <p>
     *        This list must include only operations that are permitted in a grant. Also, the operation must be
     *        supported on the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that
     *        allows the <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the
     *        <a>GenerateDataKey</a> operation. If you try, KMS returns a <code>ValidationError</code> exception. For
     *        details, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     *        operations</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see GrantOperation
     */

    public CreateGrantRequest withOperations(String... operations) {
        if (this.operations == null) {
            setOperations(new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(operations.length));
        }
        for (String ele : operations) {
            this.operations.add(ele);
        }
        return this;
    }

    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param operations
     *        A list of operations that the grant permits. </p>
     *        <p>
     *        This list must include only operations that are permitted in a grant. Also, the operation must be
     *        supported on the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that
     *        allows the <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the
     *        <a>GenerateDataKey</a> operation. If you try, KMS returns a <code>ValidationError</code> exception. For
     *        details, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     *        operations</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see GrantOperation
     */

    public CreateGrantRequest withOperations(java.util.Collection<String> operations) {
        setOperations(operations);
        return this;
    }

    /**
     * <p>
     * A list of operations that the grant permits.
     * </p>
     * <p>
     * This list must include only operations that are permitted in a grant. Also, the operation must be supported on
     * the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that allows the
     * <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the <a>GenerateDataKey</a> operation. If
     * you try, KMS returns a <code>ValidationError</code> exception. For details, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     * operations</a> in the <i>Key Management Service Developer Guide</i>.
     * </p>
     * 
     * @param operations
     *        A list of operations that the grant permits. </p>
     *        <p>
     *        This list must include only operations that are permitted in a grant. Also, the operation must be
     *        supported on the KMS key. For example, you cannot create a grant for a symmetric encryption KMS key that
     *        allows the <a>Sign</a> operation, or a grant for an asymmetric KMS key that allows the
     *        <a>GenerateDataKey</a> operation. If you try, KMS returns a <code>ValidationError</code> exception. For
     *        details, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">Grant
     *        operations</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see GrantOperation
     */

    public CreateGrantRequest withOperations(GrantOperation... operations) {
        com.ibm.cloud.objectstorage.internal.SdkInternalList<String> operationsCopy = new com.ibm.cloud.objectstorage.internal.SdkInternalList<String>(operations.length);
        for (GrantOperation value : operations) {
            operationsCopy.add(value.toString());
        }
        if (getOperations() == null) {
            setOperations(operationsCopy);
        } else {
            getOperations().addAll(operationsCopy);
        }
        return this;
    }

    /**
     * <p>
     * Specifies a grant constraint.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant constraints,
     * which allow the permissions in the grant only when the encryption context in the request matches (
     * <code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the encryption context
     * specified in the constraint.
     * </p>
     * <p>
     * The encryption context grant constraints are supported only on <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     * operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations on
     * symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     * <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a grant
     * constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants created with
     * the <code>CreateGrant</code> permission have an equally strict or stricter encryption context constraint.
     * </p>
     * <p>
     * You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS keys or
     * HMAC KMS keys. Operations with these keys don't support an encryption context.
     * </p>
     * <p>
     * Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     * constraint cannot exceed 384 characters. For information about grant constraints, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     * grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about encryption
     * context, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i> <i>Key Management Service Developer Guide</i> </i>.
     * </p>
     * 
     * @param constraints
     *        Specifies a grant constraint.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant
     *        constraints, which allow the permissions in the grant only when the encryption context in the request
     *        matches (<code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the
     *        encryption context specified in the constraint.
     *        </p>
     *        <p>
     *        The encryption context grant constraints are supported only on <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     *        operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations
     *        on symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     *        <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a
     *        grant constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants
     *        created with the <code>CreateGrant</code> permission have an equally strict or stricter encryption context
     *        constraint.
     *        </p>
     *        <p>
     *        You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS
     *        keys or HMAC KMS keys. Operations with these keys don't support an encryption context.
     *        </p>
     *        <p>
     *        Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     *        constraint cannot exceed 384 characters. For information about grant constraints, see <a href=
     *        "https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     *        grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about
     *        encryption context, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *        context</a> in the <i> <i>Key Management Service Developer Guide</i> </i>.
     */

    public void setConstraints(GrantConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * <p>
     * Specifies a grant constraint.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant constraints,
     * which allow the permissions in the grant only when the encryption context in the request matches (
     * <code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the encryption context
     * specified in the constraint.
     * </p>
     * <p>
     * The encryption context grant constraints are supported only on <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     * operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations on
     * symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     * <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a grant
     * constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants created with
     * the <code>CreateGrant</code> permission have an equally strict or stricter encryption context constraint.
     * </p>
     * <p>
     * You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS keys or
     * HMAC KMS keys. Operations with these keys don't support an encryption context.
     * </p>
     * <p>
     * Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     * constraint cannot exceed 384 characters. For information about grant constraints, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     * grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about encryption
     * context, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i> <i>Key Management Service Developer Guide</i> </i>.
     * </p>
     * 
     * @return Specifies a grant constraint.</p> <important>
     *         <p>
     *         Do not include confidential or sensitive information in this field. This field may be displayed in
     *         plaintext in CloudTrail logs and other output.
     *         </p>
     *         </important>
     *         <p>
     *         KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant
     *         constraints, which allow the permissions in the grant only when the encryption context in the request
     *         matches (<code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the
     *         encryption context specified in the constraint.
     *         </p>
     *         <p>
     *         The encryption context grant constraints are supported only on <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     *         operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations
     *         on symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     *         <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a
     *         grant constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants
     *         created with the <code>CreateGrant</code> permission have an equally strict or stricter encryption
     *         context constraint.
     *         </p>
     *         <p>
     *         You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS
     *         keys or HMAC KMS keys. Operations with these keys don't support an encryption context.
     *         </p>
     *         <p>
     *         Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     *         constraint cannot exceed 384 characters. For information about grant constraints, see <a href=
     *         "https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints"
     *         >Using grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information
     *         about encryption context, see <a
     *         href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *         context</a> in the <i> <i>Key Management Service Developer Guide</i> </i>.
     */

    public GrantConstraints getConstraints() {
        return this.constraints;
    }

    /**
     * <p>
     * Specifies a grant constraint.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant constraints,
     * which allow the permissions in the grant only when the encryption context in the request matches (
     * <code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the encryption context
     * specified in the constraint.
     * </p>
     * <p>
     * The encryption context grant constraints are supported only on <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     * operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations on
     * symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     * <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a grant
     * constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants created with
     * the <code>CreateGrant</code> permission have an equally strict or stricter encryption context constraint.
     * </p>
     * <p>
     * You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS keys or
     * HMAC KMS keys. Operations with these keys don't support an encryption context.
     * </p>
     * <p>
     * Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     * constraint cannot exceed 384 characters. For information about grant constraints, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     * grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about encryption
     * context, see <a
     * href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption context</a>
     * in the <i> <i>Key Management Service Developer Guide</i> </i>.
     * </p>
     * 
     * @param constraints
     *        Specifies a grant constraint.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        KMS supports the <code>EncryptionContextEquals</code> and <code>EncryptionContextSubset</code> grant
     *        constraints, which allow the permissions in the grant only when the encryption context in the request
     *        matches (<code>EncryptionContextEquals</code>) or includes (<code>EncryptionContextSubset</code>) the
     *        encryption context specified in the constraint.
     *        </p>
     *        <p>
     *        The encryption context grant constraints are supported only on <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#terms-grant-operations">grant
     *        operations</a> that include an <code>EncryptionContext</code> parameter, such as cryptographic operations
     *        on symmetric encryption KMS keys. Grants with grant constraints can include the <a>DescribeKey</a> and
     *        <a>RetireGrant</a> operations, but the constraint doesn't apply to these operations. If a grant with a
     *        grant constraint includes the <code>CreateGrant</code> operation, the constraint requires that any grants
     *        created with the <code>CreateGrant</code> permission have an equally strict or stricter encryption context
     *        constraint.
     *        </p>
     *        <p>
     *        You cannot use an encryption context grant constraint for cryptographic operations with asymmetric KMS
     *        keys or HMAC KMS keys. Operations with these keys don't support an encryption context.
     *        </p>
     *        <p>
     *        Each constraint value can include up to 8 encryption context pairs. The encryption context value in each
     *        constraint cannot exceed 384 characters. For information about grant constraints, see <a href=
     *        "https://docs.aws.amazon.com/kms/latest/developerguide/create-grant-overview.html#grant-constraints">Using
     *        grant constraints</a> in the <i>Key Management Service Developer Guide</i>. For more information about
     *        encryption context, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#encrypt_context">Encryption
     *        context</a> in the <i> <i>Key Management Service Developer Guide</i> </i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withConstraints(GrantConstraints constraints) {
        setConstraints(constraints);
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
     * @return A list of grant tokens. </p>
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
     *        A list of grant tokens. </p>
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
     *        A list of grant tokens. </p>
     *        <p>
     *        Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *        achieved <i>eventual consistency</i>. For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *        <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *        a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withGrantTokens(String... grantTokens) {
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
     *        A list of grant tokens. </p>
     *        <p>
     *        Use a grant token when your permission to call this operation comes from a new grant that has not yet
     *        achieved <i>eventual consistency</i>. For more information, see <a
     *        href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html#grant_token">Grant token</a> and
     *        <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grant-manage.html#using-grant-token">Using
     *        a grant token</a> in the <i>Key Management Service Developer Guide</i>.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withGrantTokens(java.util.Collection<String> grantTokens) {
        setGrantTokens(grantTokens);
        return this;
    }

    /**
     * <p>
     * A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     * retrying this request.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     * <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended duplicates
     * when you retry the <code>CreateGrant</code> request.
     * </p>
     * <p>
     * When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if the
     * grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note that the
     * returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     * <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     * </p>
     * 
     * @param name
     *        A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     *        retrying this request.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     *        <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended
     *        duplicates when you retry the <code>CreateGrant</code> request.
     *        </p>
     *        <p>
     *        When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if
     *        the grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note
     *        that the returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     *        <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     * retrying this request.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     * <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended duplicates
     * when you retry the <code>CreateGrant</code> request.
     * </p>
     * <p>
     * When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if the
     * grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note that the
     * returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     * <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     * </p>
     * 
     * @return A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     *         retrying this request.</p> <important>
     *         <p>
     *         Do not include confidential or sensitive information in this field. This field may be displayed in
     *         plaintext in CloudTrail logs and other output.
     *         </p>
     *         </important>
     *         <p>
     *         When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     *         <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended
     *         duplicates when you retry the <code>CreateGrant</code> request.
     *         </p>
     *         <p>
     *         When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters;
     *         if the grant already exists, the original <code>GrantId</code> is returned without creating a new grant.
     *         Note that the returned grant token is unique with every <code>CreateGrant</code> request, even when a
     *         duplicate <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used
     *         interchangeably.
     */

    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     * retrying this request.
     * </p>
     * <important>
     * <p>
     * Do not include confidential or sensitive information in this field. This field may be displayed in plaintext in
     * CloudTrail logs and other output.
     * </p>
     * </important>
     * <p>
     * When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     * <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended duplicates
     * when you retry the <code>CreateGrant</code> request.
     * </p>
     * <p>
     * When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if the
     * grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note that the
     * returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     * <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     * </p>
     * 
     * @param name
     *        A friendly name for the grant. Use this value to prevent the unintended creation of duplicate grants when
     *        retrying this request.</p> <important>
     *        <p>
     *        Do not include confidential or sensitive information in this field. This field may be displayed in
     *        plaintext in CloudTrail logs and other output.
     *        </p>
     *        </important>
     *        <p>
     *        When this value is absent, all <code>CreateGrant</code> requests result in a new grant with a unique
     *        <code>GrantId</code> even if all the supplied parameters are identical. This can result in unintended
     *        duplicates when you retry the <code>CreateGrant</code> request.
     *        </p>
     *        <p>
     *        When this value is present, you can retry a <code>CreateGrant</code> request with identical parameters; if
     *        the grant already exists, the original <code>GrantId</code> is returned without creating a new grant. Note
     *        that the returned grant token is unique with every <code>CreateGrant</code> request, even when a duplicate
     *        <code>GrantId</code> is returned. All grant tokens for the same grant ID can be used interchangeably.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public CreateGrantRequest withName(String name) {
        setName(name);
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

    public CreateGrantRequest withDryRun(Boolean dryRun) {
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
        if (getGranteePrincipal() != null)
            sb.append("GranteePrincipal: ").append(getGranteePrincipal()).append(",");
        if (getRetiringPrincipal() != null)
            sb.append("RetiringPrincipal: ").append(getRetiringPrincipal()).append(",");
        if (getOperations() != null)
            sb.append("Operations: ").append(getOperations()).append(",");
        if (getConstraints() != null)
            sb.append("Constraints: ").append(getConstraints()).append(",");
        if (getGrantTokens() != null)
            sb.append("GrantTokens: ").append(getGrantTokens()).append(",");
        if (getName() != null)
            sb.append("Name: ").append(getName()).append(",");
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

        if (obj instanceof CreateGrantRequest == false)
            return false;
        CreateGrantRequest other = (CreateGrantRequest) obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null)
            return false;
        if (other.getKeyId() != null && other.getKeyId().equals(this.getKeyId()) == false)
            return false;
        if (other.getGranteePrincipal() == null ^ this.getGranteePrincipal() == null)
            return false;
        if (other.getGranteePrincipal() != null && other.getGranteePrincipal().equals(this.getGranteePrincipal()) == false)
            return false;
        if (other.getRetiringPrincipal() == null ^ this.getRetiringPrincipal() == null)
            return false;
        if (other.getRetiringPrincipal() != null && other.getRetiringPrincipal().equals(this.getRetiringPrincipal()) == false)
            return false;
        if (other.getOperations() == null ^ this.getOperations() == null)
            return false;
        if (other.getOperations() != null && other.getOperations().equals(this.getOperations()) == false)
            return false;
        if (other.getConstraints() == null ^ this.getConstraints() == null)
            return false;
        if (other.getConstraints() != null && other.getConstraints().equals(this.getConstraints()) == false)
            return false;
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null)
            return false;
        if (other.getGrantTokens() != null && other.getGrantTokens().equals(this.getGrantTokens()) == false)
            return false;
        if (other.getName() == null ^ this.getName() == null)
            return false;
        if (other.getName() != null && other.getName().equals(this.getName()) == false)
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

        hashCode = prime * hashCode + ((getKeyId() == null) ? 0 : getKeyId().hashCode());
        hashCode = prime * hashCode + ((getGranteePrincipal() == null) ? 0 : getGranteePrincipal().hashCode());
        hashCode = prime * hashCode + ((getRetiringPrincipal() == null) ? 0 : getRetiringPrincipal().hashCode());
        hashCode = prime * hashCode + ((getOperations() == null) ? 0 : getOperations().hashCode());
        hashCode = prime * hashCode + ((getConstraints() == null) ? 0 : getConstraints().hashCode());
        hashCode = prime * hashCode + ((getGrantTokens() == null) ? 0 : getGrantTokens().hashCode());
        hashCode = prime * hashCode + ((getName() == null) ? 0 : getName().hashCode());
        hashCode = prime * hashCode + ((getDryRun() == null) ? 0 : getDryRun().hashCode());
        return hashCode;
    }

    @Override
    public CreateGrantRequest clone() {
        return (CreateGrantRequest) super.clone();
    }

}
