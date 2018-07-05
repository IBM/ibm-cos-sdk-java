package com.ibm.cloud.objectstorage.oauth;

import com.ibm.cloud.objectstorage.oauth.Token;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;

public class TokenProviderNull implements TokenProvider{
	
	public TokenProviderNull() {
	}

	@Override
	public Token retrieveToken() {

		return null;
	}

}
