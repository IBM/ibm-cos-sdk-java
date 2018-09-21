package com.ibm.cloud.objectstorage.services.aspera.transfer;

/**
 * Transfer Spec Configuration can be specified at client construction time and/or 
 * on method invocation time. If a method level transfer spec configuration is provided 
 * it will take precedence over any such object which was configured at the client 
 * construction level. If neither is provided, then only defaults as returned by 
 * upload_setup will be configured
 *
 */
public class AsperaConfig {
    private long targetRateKbps;
    private long targetRateMbps;
    private long targetRateCapKbps;
    private long targetRateCapMbps;
    private long minRateCapKbps;
    private long minRateCapMbps;
    private long minRateKbps;
    private long minRateMbps;
    private String ratePolicy="fair";
    private boolean lockMinRate;
    private boolean lockTargetRate;
    private boolean lockRatePolicy;
    private int multiSession;
    private String destinationRoot;
    private long multiSessionThreshold;
    private long multiSessionThresholdMb;
    
    /**
     * The desired speed of the transfer, in Kbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
     * @return targetRateKbps
     */
	public long getTargetRateKbps() {
		return targetRateKbps;
	}
	
	/**
	 * Set The desired speed of the transfer, in Kbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
	 * @param targetRateKbps
	 */
	public void setTargetRateKbps(long targetRateKbps) {
		this.targetRateKbps = targetRateKbps;
	}
	
	/**
	 * Set The desired speed of the transfer, in Kbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
	 * @param targetRateKbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withTargetRateKbps(long targetRateKbps) {
		this.targetRateKbps = targetRateKbps;
		return this;
	}
	
	/**
     * The desired speed of the transfer, in Mbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
     * @return targetRateMbps
     */
	public long getTargetRateMbps() {
		return targetRateMbps;
	}

	/**
	 * Set The desired speed of the transfer, in Mbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
	 * @param targetRateMbps
	 */
	public void setTargetRateMbps(long targetRateMbps) {
		this.targetRateMbps = targetRateMbps;
		this.targetRateKbps = targetRateMbps * 1000;
	}
	
	/**
	 * Set The desired speed of the transfer, in Mbps.
     * If there is competing network traffic, FASP may share this bandwidth, depending on the ratePolicy value you set.
     * Default: Server-side target rate default value in the configuration file (aspera.conf).
     * Respects both local- and server-side target rate caps, if set.
     * Must be provided when you set a value for targetRateCapKbps.
     * 
	 * @param targetRateMbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withTargetRateMbps(long targetRateMbps) {
		this.targetRateMbps = targetRateMbps;
		this.targetRateKbps = targetRateMbps * 1000;
		return this;
	}
	
	/**
	 * Upper limit of target rate, in Kbps. Default: no limit.
	 * 
	 * @return targetRateCapKbps
	 */
	public long getTargetRateCapKbps() {
		return targetRateCapKbps;
	}
	
	/**
	 * Set upper limit of target rate, in Kbps. Default: no limit.
	 * 
	 * @param targetRateCapKbps
	 */
	public void setTargetRateCapKbps(long targetRateCapKbps) {
		this.targetRateCapKbps = targetRateCapKbps;
	}
	
	/**
	 * Set upper limit of target rate, in Kbps. Default: no limit.
	 * 
	 * @param targetRateCapKbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withTargetRateCapKbps(long targetRateCapKbps) {
		this.targetRateCapKbps = targetRateCapKbps;
		return this;
	}
		
	/**
	 * Upper limit of target rate, in Mbps. Default: no limit.
	 * 
	 * @return targetRateCapMbps
	 */
	public long getTargetRateCapMbps() {
		return targetRateCapMbps;
	}

	/**
	 * Set upper limit of target rate, in Mbps. Default: no limit.
	 * 
	 * @param targetRateCapMbps
	 */
	public void setTargetRateCapMbps(long targetRateCapMbps) {
		this.targetRateCapMbps = targetRateCapMbps;
		this.targetRateCapKbps = targetRateCapMbps * 1000;
	}
	
	/**
	 * Set upper limit of target rate, in Mbps. Default: no limit.
	 * 
	 * @param targetRateCapMbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withTargetRateCapMbps(long targetRateCapMbps) {
		this.targetRateCapMbps = targetRateCapMbps;
		this.targetRateCapKbps = targetRateCapMbps * 1000;
		return this;
	}

	/**
	 * Lower limit of target rate, in Kbps.
	 * 
	 * @return minRateCapKbps
	 */
	public long getMinRateCapKbps() {
		return minRateCapKbps;
	}
	
	/**
	 * Set lower limit of target rate, in Kbps.
	 * 
	 * @param minRateCapKbps
	 */
	public void setMinRateCapKbps(long minRateCapKbps) {
		this.minRateCapKbps = minRateCapKbps;
	}
	
	/**
	 * Set lower limit of target rate, in Kbps.
	 * 
	 * @param minRateCapKbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withMinRateCapKbps(long minRateCapKbps) {
		this.minRateCapKbps = minRateCapKbps;
		return this;
	}
	
	/**
	 * Lower limit of target rate, in Mbps.
	 * 
	 * @return minRateCapMbps
	 */
	public long getMinRateCapMbps() {
		return minRateCapMbps;
	}

	/**
	 * Set lower limit of target rate, in Mbps.
	 * 
	 * @param minRateCapMbps
	 */
	public void setMinRateCapMbps(long minRateCapMbps) {
		this.minRateCapMbps = minRateCapMbps;
		this.minRateCapKbps = minRateCapMbps * 1000;
	}
	
	/**
	 * Set lower limit of target rate, in Mbps.
	 * 
	 * @param minRateCapMbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withMinRateCapMbps(long minRateCapMbps) {
		this.minRateCapMbps = minRateCapMbps;
		this.minRateCapKbps = minRateCapMbps * 1000;
		return this;
	}

	/**
	 * The minimum transfer rate.
	 * 
	 * @return minRateKbps
	 */
	public long getMinRateKbps() {
		return minRateKbps;
	}
	
	/**
	 * Set The minimum transfer rate.
	 * 
	 * @param minRateKbps
	 */
	public void setMinRateKbps(long minRateKbps) {
		this.minRateKbps = minRateKbps;
	}
	
	/**
	 * Set The minimum transfer rate.
	 * 
	 * @param minRateKbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withMinRateKbps(long minRateKbps) {
		this.minRateKbps = minRateKbps;
		return this;
	}
		
	/**
	 * The minimum transfer rate.
	 * 
	 * @return minRateMbps
	 */
	public long getMinRateMbps() {
		return minRateMbps;
	}

	/**
	 * Set The minimum transfer rate.
	 * 
	 * @param minRateMbps
	 */
	public void setMinRateMbps(long minRateMbps) {
		this.minRateMbps = minRateMbps;
		this.minRateKbps = minRateMbps * 1000;
	}
	
	/**
	 * Set The minimum transfer rate.
	 * 
	 * @param minRateMbps
	 * @return this AsperConfig
	 */
	public AsperaConfig withMinRateMbps(long minRateMbps) {
		this.minRateMbps = minRateMbps;
		this.minRateKbps = minRateMbps * 1000;
		return this;
	}

	/**
	 * The rate policy to use when sharing bandwidth. Allowed values: 
	 * 		high: When sharing bandwidth, transfer at twice the rate of a transfer using a "fair policy.
	 *		fair (default value): Share bandwidth equally with other traffic.
	 * 		low: Use only unused bandwidth.
	 * 		fixed: Transfer at the target rate, regardless of the actual network capacity.
	 * 		 Do not share bandwidth. Aspera recommends that you do not use this setting.
	 * 
	 * @return ratePolicy
	 */
	public String getRatePolicy() {
		return ratePolicy;
	}
	
	/**
	 * The rate policy to use when sharing bandwidth. Allowed values: 
	 * 		high: When sharing bandwidth, transfer at twice the rate of a transfer using a "fair policy.
	 *		fair (default value): Share bandwidth equally with other traffic.
	 * 		low: Use only unused bandwidth.
	 * 		fixed: Transfer at the target rate, regardless of the actual network capacity.
	 * 		 Do not share bandwidth. Aspera recommends that you do not use this setting.
	 * @param ratePolicy
	 */
	public void setRatePolicy(String ratePolicy) {
		this.ratePolicy = ratePolicy;
	}
	
	/**
	 * The rate policy to use when sharing bandwidth. Allowed values: 
	 * 		high: When sharing bandwidth, transfer at twice the rate of a transfer using a "fair policy.
	 *		fair (default value): Share bandwidth equally with other traffic.
	 * 		low: Use only unused bandwidth.
	 * 		fixed: Transfer at the target rate, regardless of the actual network capacity.
	 * 		 Do not share bandwidth. Aspera recommends that you do not use this setting.
	 * @param ratePolicy
	 * @return this AsperConfig
	 */
	public AsperaConfig withRatePolicy(String ratePolicy) {
		this.ratePolicy = ratePolicy;
		return this;
	}
	
	/**
	 * If the minimum transfer rate is locked by the server.
	 * 
	 * @return
	 */
	public boolean isLockMinRate() {
		return lockMinRate;
	}
	
	/**
	 * Set to indicate if the minimum transfer rate is locked by the server.
	 * 
	 * @param lockMinRate
	 */
	public void setLockMinRate(boolean lockMinRate) {
		this.lockMinRate = lockMinRate;
	}
	
	/**
	 * Set to indicate if the minimum transfer rate is locked by the server.
	 * 
	 * @param lockMinRate
	 * @return this AsperConfig
	 */
	public AsperaConfig withLockMinRate(boolean lockMinRate) {
		this.lockMinRate = lockMinRate;
		return this;
	}
	
	/**
	 * If the target transfer rate is locked by the server.
	 * 
	 * @return lockTargetRate
	 */
	public boolean isLockTargetRate() {
		return lockTargetRate;
	}
	
	/**
	 * Set to indicate if the target transfer rate is locked by the server.
	 * 
	 * @param lockTargetRate
	 */
	public void setLockTargetRate(boolean lockTargetRate) {
		this.lockTargetRate = lockTargetRate;
	}
	
	/**
	 * Set to indicate if the target transfer rate is locked by the server.
	 * 
	 * @param lockTargetRate
	 * @return this AsperConfig
	 */
	public AsperaConfig withLockTargetRate(boolean lockTargetRate) {
		this.lockTargetRate = lockTargetRate;
		return this;
	}
	
	/**
	 * If the rate policy is locked by the server.
	 * 
	 * @return lockRatePolicy
	 */
	public boolean isLockRatePolicy() {
		return lockRatePolicy;
	}
	
	/**
	 * Set to indicate if the rate policy is locked by the server.
	 * 
	 * @param lockRatePolicy
	 */
	public void setLockRatePolicy(boolean lockRatePolicy) {
		this.lockRatePolicy = lockRatePolicy;
	}
	
	/**
	 * Set to indicate if the rate policy is locked by the server.
	 * 
	 * @param lockRatePolicy
	 * @return withlockRatePolicy
	 */
	public AsperaConfig withLockRatePolicy(boolean lockRatePolicy) {
		this.lockRatePolicy = lockRatePolicy;
		return this;
	}
	
	/**
	 * The number of simultaneous transfer sessions. Default: 1.
	 * 
	 * @return multiSession
	 */
	public int getMultiSession() {
		return multiSession;
	}
	
	/**
	 * Set the number of simultaneous transfer sessions. Default: 1.
	 * 
	 * @param multiSession
	 */
	public void setMultiSession(int multiSession) {
		this.multiSession = multiSession;
	}

	/**
	 * Set the number of simultaneous transfer sessions. Default: 1.
	 * 
	 * @param multiSession
	 * @return this AsperConfig
	 */
	public AsperaConfig withMultiSession(int multiSession) {
		this.multiSession = multiSession;
		return this;
	}
	
	/**
	 * The file ID of the destination root directory.
	 * 
	 * @return destinationRoot
	 */
	public String getDestinationRoot() {
		return destinationRoot;
	}
	
	/**
	 * Set the file ID of the destination root directory.
	 * 
	 * @param destinationRoot
	 */
	public void setDestinationRoot(String destinationRoot) {
		this.destinationRoot = destinationRoot;
	}
	
	/**
	 * Set the file ID of the destination root directory.
	 * 
	 * @param destinationRoot
	 * @return this AsperConfig
	 */
	public AsperaConfig withDestinationRoot(String destinationRoot) {
		this.destinationRoot = destinationRoot;
		return this;
	}

	/**
	 * return the file size min threshold to allow multiSession to work
	 * 
	 * @return
	 */
	public long getMultiSessionThreshold() {
		return multiSessionThreshold;
	}

	/**
	 * Set the min size file threshold to allow multiSession to work 
	 * 
	 * @param multiSessionThreshold
	 */
	public void setMultiSessionThreshold(long multiSessionThreshold) {
		this.multiSessionThreshold = multiSessionThreshold;
	}

	/**
	 * Set the min size file threshold to allow multiSession to work 
	 * 
	 * @param multiSessionThreshold
	 */
	public AsperaConfig withMultiSessionThreshold(long multiSessionThreshold) {
		this.multiSessionThreshold = multiSessionThreshold;
		return this;
	}

	/**
	 * Return the file size min threshold in MB to allow multiSession to work
	 * 
	 * @return
	 */
	public long getMultiSessionThresholdMb() {
		return multiSessionThresholdMb;
	}

	/**
	 * Set the min size file threshold in MB to allow multiSession to work 
	 * 
	 * @param multiSessionThreshold
	 */
	public void setMultiSessionThresholdMb(long multiSessionThresholdMb) {
		this.multiSessionThresholdMb = multiSessionThresholdMb;
		this.multiSessionThreshold = multiSessionThresholdMb * 1000000;
	}
	
	/**
	 * Set the min size file threshold in MB to allow multiSession to work 
	 * 
	 * @param multiSessionThreshold
	 */
	public AsperaConfig withMultiSessionThresholdMb(long multiSessionThresholdMb) {
		this.multiSessionThresholdMb = multiSessionThresholdMb;
		this.multiSessionThreshold = multiSessionThresholdMb * 1000000;
		return this;
	}
	
}
