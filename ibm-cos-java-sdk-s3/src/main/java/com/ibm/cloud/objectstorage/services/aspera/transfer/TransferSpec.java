/*
* Copyright 2018 IBM Corp. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
*/
package com.ibm.cloud.objectstorage.services.aspera.transfer;

import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("transfer_spec")
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT,use= JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)

public class TransferSpec {
	private String cipher;
	private String destination_root;
	private String direction;
	private String token;
	private long fasp_port;
	private boolean http_fallback;
	private boolean lock_min_rate;
	private Vector<NodePath> paths;
	private String rate_policy;
	private String rate_policy_allowed;
	private String source_root;
	private String sshfp;
	private long ssh_port;
	private long min_rate_cap_kbps;
	private long min_rate_kbps;
	private long target_rate_cap_kbps;
	private long target_rate_kbps;
	private Tags tags;
	private String remote_host;
	private String remote_user;
	private boolean lock_target_rate;
	private boolean lock_rate_policy;

	@JsonInclude(Include.NON_DEFAULT)
	private int multi_session;
	@JsonInclude(Include.NON_DEFAULT)
	private long multi_session_threshold;

	public String getRemote_user() {
		return remote_user;
	}

	public void setRemote_user(String remote_user) {
		this.remote_user = remote_user;
	}

	public String getRemote_host() {
		return remote_host;
	}

	public void setRemote_host(String remote_host) {
		this.remote_host = remote_host;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public String getDestination_root() {
		return destination_root;
	}

	public void setDestination_root(String destination_root) {
		this.destination_root = destination_root;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public long getFasp_port() {
		return fasp_port;
	}

	public void setFasp_port(long fasp_port) {
		this.fasp_port = fasp_port;
	}

	public boolean isHttp_fallback() {
		return http_fallback;
	}

	public void setHttp_fallback(boolean http_fallback) {
		this.http_fallback = http_fallback;
	}

	public boolean isLock_min_rate() {
		return lock_min_rate;
	}

	public void setLock_min_rate(boolean lock_min_rate) {
		this.lock_min_rate = lock_min_rate;
	}

	public Vector<NodePath> getPaths() {
		return paths;
	}

	public void setPaths(Vector<NodePath> paths) {
		this.paths = paths;
	}

	public String getRate_policy() {
		return rate_policy;
	}

	public void setRate_policy(String rate_policy) {
		this.rate_policy = rate_policy;
	}

	public String getRate_policy_allowed() {
		return rate_policy_allowed;
	}

	public void setRate_policy_allowed(String rate_policy_allowed) {
		this.rate_policy_allowed = rate_policy_allowed;
	}

	public String getSource_root() {
		return source_root;
	}

	public void setSource_root(String source_root) {
		this.source_root = source_root;
	}

	public String getSshfp() {
		return sshfp;
	}

	public void setSshfp(String sshfp) {
		this.sshfp = sshfp;
	}

	public long getSsh_port() {
		return ssh_port;
	}

	public void setSsh_port(long ssh_port) {
		this.ssh_port = ssh_port;
	}

	public long getMin_rate_cap_kbps() {
		return min_rate_cap_kbps;
	}

	public void setMin_rate_cap_kbps(long min_rate_cap_kbps) {
		this.min_rate_cap_kbps = min_rate_cap_kbps;
	}

	public long getMin_rate_kbps() {
		return min_rate_kbps;
	}

	public void setMin_rate_kbps(long min_rate_kbps) {
		this.min_rate_kbps = min_rate_kbps;
	}

	public long getTarget_rate_cap_kbps() {
		return target_rate_cap_kbps;
	}

	public void setTarget_rate_cap_kbps(long target_rate_cap_kbps) {
		this.target_rate_cap_kbps = target_rate_cap_kbps;
	}

	public long getTarget_rate_kbps() {
		return target_rate_kbps;
	}

	public void setTarget_rate_kbps(long target_rate_kbps) {
		this.target_rate_kbps = target_rate_kbps;
	}

	public Tags getTags() {
		return tags;
	}

	public void setTags(Tags tags) {
		this.tags = tags;
	}

	public boolean isLock_target_rate() {
		return lock_target_rate;
	}

	public void setLock_target_rate(boolean lock_target_rate) {
		this.lock_target_rate = lock_target_rate;
	}

	public boolean isLock_rate_policy() {
		return lock_rate_policy;
	}

	public void setLock_rate_policy(boolean lock_rate_policy) {
		this.lock_rate_policy = lock_rate_policy;
	}

	public int getMulti_session() {
		return multi_session;
	}

	public void setMulti_session(int multi_session) {
		this.multi_session = multi_session;
	}

	public long getMulti_session_threshold() {
		return multi_session_threshold;
	}

	public void setMulti_session_threshold(long multi_session_threshold) {
		this.multi_session_threshold = multi_session_threshold;
	}
}

