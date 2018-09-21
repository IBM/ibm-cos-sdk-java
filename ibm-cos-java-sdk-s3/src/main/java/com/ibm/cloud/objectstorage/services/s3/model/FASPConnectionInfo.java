package com.ibm.cloud.objectstorage.services.s3.model;

import java.io.Serializable;

import com.ibm.cloud.objectstorage.services.s3.internal.S3RequesterChargedResult;

/**
 * <p>
 * Represents an Aspera FASP Connection Information. This will contain 
 * Access Key Id & secret along with ATSEnpoint
 * </p>
 * 
 **/
public class FASPConnectionInfo implements Serializable, S3RequesterChargedResult{

	private static final long serialVersionUID = -368314477468312227L;

	private String accessKeyId;
	private String accessKeySecret;
	private String atsEndpoint;

    /**
     * Indicate if the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
    private boolean isRequesterCharged;

	/**
	 * 
	 * @return Aspera Access Key ID
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * 
	 * @return Aspera Access Key ID
	 */
	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	/**
	 * 
	 * @return Aspera ATS Endpoint
	 */
	public String getAtsEndpoint() {
		return atsEndpoint;
	}

	public void setAtsEndpoint(String atsEndpoint) {
		this.atsEndpoint = atsEndpoint;
	}

    @Override
    public boolean isRequesterCharged() {
        return isRequesterCharged;
    }

    @Override
    public void setRequesterCharged(boolean isRequesterCharged) {
        this.isRequesterCharged = isRequesterCharged;
    }
}
