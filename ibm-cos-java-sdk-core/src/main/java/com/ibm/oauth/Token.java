/* 
* Copyright 2017 IBM Corp. All Rights Reserved. 
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
package com.ibm.oauth;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Pojo representation of the json IAM token
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
	
	private String access_token;
	private String refresh_token;
	private String token_type;
	private String expires_in;
	private String expiration;
	private String uaa_token;
	private String ims_token;

	/**
	 * Returns the UAA Token.
	 */
	public String getUaa_token() {
		return uaa_token;
	}

	/**
	 * Sets the UAA token.
	 */
	public void setUaa_token(String uaa_token) {
		this.uaa_token = uaa_token;
	}

	/**
	 * Returns the IMS token.
	 */
	public String getIms_token() {
		return ims_token;
	}

	/**
	 * Sets the IMS token.
	 */
	public void setIms_token(String ims_token) {
		this.ims_token = ims_token;
	}

	/**
	 * Returns the access token.
	 */
	public String getAccess_token() {
		return access_token;
	}
	
	/**
	 * Sets the access token.
	 * @param access_token
     *            The access token
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	/**
	 * Returns the refresh token.
	 */
	public String getRefresh_token() {
		return refresh_token;
	}
	
	/**
	 * Sets the refresh token.
	 * @param refresh_token
     *            The refresh token.
	 */
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	/**
	 * Returns the token type.
	 */
	public String getToken_type() {
		return token_type;
	}
	
	/**
	 * Sets the token type.
	 * @param token type
     *            The token type.
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	
	/**
	 * Returns the number of seconds remaining before the token expires.
	 */
	public String getExpires_in() {
		return expires_in;
	}
	
	/**
	 * Sets the expiry timestamp.
	 * @param expires_in
     *            Number of seconds after which the token will expire.
	 */
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	
	/**
	 * Returns the timestamp when the token will expire.
	 */
	public String getExpiration() {
		return expiration;
	}
	
	/**
	 * Sets the expiry timestamp.
	 * @param expiration
     *            Timestamp when the token will expire.
	 */
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

}
