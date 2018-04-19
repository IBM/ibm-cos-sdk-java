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
package com.amazonaws.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.amazonaws.auth.json.internal.JsonKeyConstants;
import com.ibm.oauth.IBMOAuthCredentials;
import com.ibm.oauth.TokenManager;

/**
 * Simple implementation of IBMOAuthCredentials that reads in API key and
 * resource instance id from json file. The IBM api key is expected to be in the
 * "apikey" property and the resource instance id is expected to be in the
 * "resource_instance_id" property.
 */
public class JsonCredentials implements IBMOAuthCredentials {

	private static final JsonFactory jsonFactory = new JsonFactory();
	private boolean hmacEnabled;
	private boolean iamEnabled;
	private String accessKey;
	private String secretAccessKey;
	private String apiKey;
	private String serviceInstanceId;
	private TokenManager tokenManager;

	/**
	 * Reads the specified file as a Json file and extracts the IBM api key from the
	 * "apikey" field and IBM resource instance id from the "resource#_instance_id"
	 * filed. If the specified file doesn't contain the IBM api key and resource
	 * instance id an IOException will be thrown.
	 *
	 * @param file
	 *            The file from which to read the IBM credentials.
	 *
	 * @throws FileNotFoundException
	 *             If the specified file isn't found.
	 * @throws IOException
	 *             If any problems are encountered reading the IBM credentials from
	 *             the specified file.
	 * @throws IllegalArgumentException
	 *             If the specified json file does not contain the required keys.
	 */
	public JsonCredentials(File file) throws FileNotFoundException, IOException, IllegalArgumentException {
		if (!file.exists()) {
			throw new FileNotFoundException("File doesn't exist:  " + file.getAbsolutePath());
		}

		JsonParser parser = jsonFactory.createParser(file);
		parse(parser);
		parser.close();

		if (!isNullOrEmpty(apiKey) && !isNullOrEmpty(serviceInstanceId))
			iamEnabled = true;
		if (!isNullOrEmpty(accessKey) && !isNullOrEmpty(secretAccessKey))
			hmacEnabled = true;

		if (!iamEnabled && !hmacEnabled) {
			throw new IllegalArgumentException(
					"The specified json doesn't contain the expected properties 'apikey', 'resource_instance_id', 'access_key_id' and 'secret_access_key'.");
		}

		// HMAC takes precedence over IAM
		if (hmacEnabled) {
			this.apiKey = null;
			this.serviceInstanceId = null;
			this.iamEnabled = false;
		}
	}

	/**
	 * Reads the specified input stream as a stream of json object content and
	 * extracts the IBM api key and resource instance id from the object.
	 *
	 * @param inputStream
	 *            The input stream containing the IBM credential properties.
	 *
	 * @throws IOException
	 *             If any problems occur while reading from the input stream.
	 */
	public JsonCredentials(InputStream stream) throws IOException {

		try {
			JsonParser parser = jsonFactory.createParser(stream);
			parse(parser);
			parser.close();
		} finally {
			try {
				stream.close();
			} catch (Exception e) {
			}
		}

		if (!isNullOrEmpty(apiKey) && !isNullOrEmpty(serviceInstanceId))
			iamEnabled = true;
		if (!isNullOrEmpty(accessKey) && !isNullOrEmpty(secretAccessKey))
			hmacEnabled = true;

		if (!iamEnabled && !hmacEnabled) {
			throw new IllegalArgumentException(
					"The specified json doesn't contain the expected properties 'apikey', 'resource_instance_id', 'access_key_id' and 'secret_access_key'.");
		}

		// HMAC takes precedence over IAM
		if (hmacEnabled) {
			this.apiKey = null;
			this.serviceInstanceId = null;
			this.iamEnabled = false;
		} else {
			this.accessKey = null;
			this.secretAccessKey = null;
			this.iamEnabled = true;
		}

	}

	private void parse(JsonParser parser) throws JsonParseException, IOException {

		JsonToken jt = parser.nextToken();
		while (continueRead() && jt != null && jt != JsonToken.END_OBJECT) {
			String token = parser.getCurrentName();

			if (JsonKeyConstants.IBM_HMAC_KEYS.equals(token)) {

				while (parser.nextToken() != JsonToken.END_OBJECT) {
					token = parser.getCurrentName();

					if (JsonKeyConstants.IBM_ACCESS_KEY_ID.equals(token)) {
						parser.nextToken();
						accessKey = parser.getText();
					}

					if (JsonKeyConstants.IBM_SECRET_ACCESS_KEY.equals(token)) {
						parser.nextToken();
						secretAccessKey = parser.getText();
					}
				}

				parser.nextToken();
			}

			if (JsonKeyConstants.IBM_API_KEY.equals(token)) {
				parser.nextToken();
				apiKey = parser.getText();
			}

			if (JsonKeyConstants.IBM_RESOURCE_INSTANCE_ID.equals(token)) {
				parser.nextToken();
				serviceInstanceId = parser.getText();
			}

			jt = parser.nextToken();
		}
	}

	private boolean isNullOrEmpty(String attr) {
		if (attr == null || attr.length() == 0)
			return true;
		else
			return false;
	}

	private boolean continueRead() {
		if (isNullOrEmpty(this.accessKey) || isNullOrEmpty(secretAccessKey) || isNullOrEmpty(apiKey)
				|| isNullOrEmpty(serviceInstanceId))
			return true;
		else
			return false;
	}

	@Override
	public String getAWSAccessKeyId() {
		return accessKey;
	}

	@Override
	public String getAWSSecretKey() {
		return secretAccessKey;
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	@Override
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public boolean isHmacEnabled() {
		return hmacEnabled;
	}

	public boolean isIamEnabled() {
		return iamEnabled;
	}

	/**
	 * allows the token manager to be set outside this class.
	 * JsonFileCredentialsProvider creates a new instance of JsonCredentials each
	 * time getCredentials are called. This created a new instance of TokenManager
	 * also. setter needed to ensure one instance of token manager per s3client
	 */
	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

}