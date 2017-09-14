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

/**
 * Interface to expose the method required for a class to implement a Token Manager.
 * Essentially a String representation of a valid token is required in the method response.
 * If a user wants to implement their own refresh token method they will have to use this interface also
 */
public interface TokenManager {

	/**
	 * returns the access token string from the token
	 * 
	 * @return String
	 * 			An access token String value
	 */
	public String getToken();
}
