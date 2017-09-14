package com.ibm.oauth;

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
