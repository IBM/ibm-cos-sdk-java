package com.ibm.oauth;

public class TokenProviderUtil implements TokenProvider{
	
	public TokenProviderUtil() {
	}

	@Override
	public Token retrieveToken() {
		
		Token token = new Token();
		token.setAccess_token("ProviderAccessToken");
		
		final long expiration = (System.currentTimeMillis() / 1000L) + 3600;
		
		token = new Token();
		token.setAccess_token("ProviderAccessToken");
		token.setRefresh_token("ProviderRefreshToken");
		token.setToken_type("Bearer");
		token.setExpires_in("3600");
		token.setExpiration(String.valueOf(expiration));
		
		return token;
	}

}
