package com.ibm.cloud.objectstorage.oauth;

import com.ibm.cloud.objectstorage.oauth.TokenManager;

public class ManagerThreadUtil implements Runnable{

	TokenManager tokenManager;
	
	public ManagerThreadUtil(TokenManager tokenManager) {
		
		this.tokenManager = tokenManager;
	}
	
	@Override
	public void run() {
		tokenManager.getToken();
	}

}
