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

import com.amazonaws.SdkClientException;

/**
 * Provides an extension of the SdkClientException
 * for errors reported by the IAM Service processing a request. 
 */
public class OAuthServiceException extends SdkClientException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The error message as returned by the service.
     */
    private String errorMessage;

    /**
     * Constructs a new OAuthServiceException with the specified message.
     *
     * @param errorMessage
     *            An error message describing what went wrong.
     */
	public OAuthServiceException(String errorMessage) {
        super((String)null);
        this.errorMessage = errorMessage;
	}
	
	 /**
     * Constructs a new OAuthServiceException with the specified message and
     * exception indicating the root cause.
     *
     * @param errorMessage
     *            An error message describing what went wrong.
     * @param cause
     *            The root exception that caused this exception to be thrown.
     */
    public OAuthServiceException(String errorMessage, Exception cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }
    

	/**
     * @return the human-readable error message provided by the service
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the human-readable error message provided by the service.
     *
     * NOTE: errorMessage by default is set to the same as the message value
     * passed to the constructor of OAuthServiceException.
     *
     */
    public void setErrorMessage(String value) {
        errorMessage = value;
    }
}
