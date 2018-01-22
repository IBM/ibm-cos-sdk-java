package com.ibm.cloud.objectstorage.oauth;

import com.ibm.cloud.objectstorage.oauth.TokenManager;

public class TokenManagerUtil implements TokenManager{

	@Override
	public String getToken() {
		
		return "TokenManagerAccessToken";
	}

}
