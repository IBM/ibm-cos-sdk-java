package com.ibm.cloud.objectstorage.oauth;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.oauth.DefaultTokenManager;
import com.ibm.cloud.objectstorage.oauth.Token;
import com.ibm.cloud.objectstorage.oauth.TokenProvider;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultTokenManagerTest {

	private Token token;
	private DefaultTokenManager defaultTokenManager;
	private long expiration = (System.currentTimeMillis() / 1000L) + 3600;
	private final String accessToken = "1eyJraWQiOiIyMDE3MDQwMS0wMDowMDowMCIsImFsZyI6IlJTMjU2In0.eyJpZCI6IklCTWlkLTEyMDAwMEoyQVkiLCJpYW1faWQiOiJJQk1pZC0xMjAwMDBKMkFZIiwicmVhbG1pZCI6IklCTWlkIiwiaWRlbnRpZmllciI6IjEyMDAwMEoyQVkiLCJnaXZlbl9uYW1lIjoiV2lsbGlhbSIsImZhbWlseV9uYW1lIjoiSHViZXIiLCJuYW1lIjoiV2lsbGlhbSBIdWJlciIsImVtYWlsIjoid2JodWJlckB1cy5pYm0uY29tIiwic3ViIjoid2JodWJlckBVcy5pYm0uY29tIiwiYWNjb3VudCI6eyJic3MiOiI1YjY0Nzc5YzkwODI3Njg3ODYzOWRjNDc5YjRmZmI4ZSJ9LCJpYXQiOjE0OTY2NTk1OTEsImV4cCI6MTQ5NjY2MzE5MSwiaXNzIjoiaHR0cHM6Ly9pYW0uc3RhZ2UxLm5nLmJsdWVtaXgubmV0L29pZGMvdG9rZW4iLCJncmFudF90eXBlIjoidXJuOmlibTpwYXJhbXM6b2F1dGg6Z3JhbnQtdHlwZTphcGlrZXkiLCJzY29wZSI6Im9wZW5pZCIsImNsaWVudF9pZCI6ImJ4In0.QZDRk9OYzzAE11SngPGIEriyP0ZJ3AG6EwxxQjlilKekiniimUZ3aSrxNqfkvn99MBmUg9RWmjzeuKl8HQD_0Y4xtJ0yHX9hVg61skBTWUDl1jD4kvMUC1C8ZzYuviJ5R9fBtcmDwZlv26VLlPBxs86MOuz7fNNVHOHxifxaGbxtM8VdJDtGprz4HO4uV1esMe27YQfrA72Hv2UnazgTUAOpjgrAXdrxcL9ekvnuMRYn-P2dSamUyZdv0E7TwokRfF0gXLBnnb89L4chT1DKrleQ8V1S1uOxpwbzBQbNldabEQ0ORebfAJbwWKtJyUfvI41rDz42F_r5leSDqiVdmQ1";
	private final String refreshToken = "1cx4fd6vs9FQUdzK8_5xvDBBbsDsGg08cPg-jplWaLgDutoqpwjrApdfAeXutVIk1KmC0SZOf0dNXvl4r9l2_R0thiUTmJSx5r003iwUpfX75154Fdy92Qk16HDG_MHhEKnk_zjs9ytgAcMKxLfOR-4X9TZv3YcBQ7dkHtx67l6XgMBl91g-ACczcduOqZs9yUk_4R7vyJTDvb37Eqm0EKi62PlE1j6r8vaYV2MN6Ouu7TdfEo8jpKEtbPnOQ50CiFvvVMv8cAhbrSaDm0FqSxmG3U3L_nO7IsTyS_v_Gpv5etwlaFqUiWZcz74q9ZsthmbYc-qU8pRUaPYtb6ixyFwNFE0tgjIHScPG-T5_O2DhRwMiVkX1rDUiW9LeCHkEBa7f1vplgLrhZkJlb41U1IRt8uG7QSEytD-PJ24KjZb0WH2RaxIG7xEl0XavrOeXsuO21l4fLZIF91qTRypl4Fz-SxLzj-bU9M3SVG50_kVL7f1GEX9it0OUp0ZMrwWxlLdNbocEITKSfKrLOOuDy75SjeXqq8QknH0DMTOmM-4LYrJfXexGxJiUDQDKiAe8Uk0eJV5m4cQeFx4JJuPIpAa8MiPJC3PMHrK0ERmymFQ=";

	@Before
	public void setup() {

		defaultTokenManager = new DefaultTokenManager("myApiKey");
		token = new Token();
		token.setAccess_token(accessToken);
		token.setRefresh_token(refreshToken);
		token.setToken_type("Bearer");
		token.setExpires_in("3600");
		token.setExpiration(String.valueOf(expiration));

	}

	@Test
	public void shouldStoreAndRetrieveToken() {

		defaultTokenManager.setTokenCache(token);

		String retrievedToken = defaultTokenManager.getToken();

		assertEquals(retrievedToken, accessToken);

	}

	@Test
	public void shouldFindTokenInCache() {

		defaultTokenManager.setTokenCache(token);

		assertTrue(defaultTokenManager.checkCache());
	}

	@Test
	public void shouldRefreshExpiringTokenAsynchronously() throws Exception {

		long expiry = (System.currentTimeMillis() / 1000L) + 14;
		token = new Token();
		token.setAccess_token(accessToken);
		token.setRefresh_token(refreshToken);
		token.setToken_type("Bearer");
		token.setExpires_in("3600");
		token.setExpiration(String.valueOf(expiry));

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);

		defaultTokenManager.setTokenCache(token);

		assertTrue(defaultTokenManager.isTokenExpiring(token));

	}

	@Test
	public void shouldRefreshExpiredTokenSynchronously() {

		long expiry = (System.currentTimeMillis() / 1000L) - 1000;
		token = new Token();
		token.setAccess_token(accessToken);
		token.setRefresh_token(refreshToken);
		token.setToken_type("Bearer");
		token.setExpires_in("3600");
		token.setExpiration(String.valueOf(expiry));

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenReturn(new Token());

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);

		defaultTokenManager.setTokenCache(token);

		assertTrue(defaultTokenManager.hasTokenExpired(token));
	}

	@Test
	public void shouldAcceptTokenProvider() {

		TokenProvider tokenProvider = new TokenProviderUtil();
		defaultTokenManager = new DefaultTokenManager(tokenProvider);

		assertEquals(defaultTokenManager.getToken(), ("ProviderAccessToken"));

	}

	@Test(expected = Exception.class)
	public void shouldBubbleExceptionUpThroughSDK() {

		TokenProvider tokenProvider = null;
		defaultTokenManager = new DefaultTokenManager(tokenProvider);

		defaultTokenManager.getToken().equals("ProviderAccessToken");
	}

	@Test
	public void shouldOnlyCallIAMOnceInSingleThreadForInitialToken() {

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenReturn(token);

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);
		defaultTokenManager.getToken();
		defaultTokenManager.getToken();

		assertEquals(1, Mockito.mockingDetails(tokenProviderMock).getInvocations().size());

	}

	/**
	 * Test set to ignore due to the assert invocation not been accurate with
	 * multiple threads.
	 */
	@Test
	@Ignore
	public void shouldOnlyCallIAMOnceInMutliThreadForInitialToken() {

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenReturn(token);

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);

		Thread t1 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t2 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t3 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t4 = new Thread(new ManagerThreadUtil(defaultTokenManager));

		t1.start();
		t2.start();
		t3.start();
		t4.start();

		assertEquals(1, Mockito.mockingDetails(tokenProviderMock).getInvocations().size());

	}

	@Test
	public void shouldOnlyRefreshExpiredTokenOnceInSingleThread() {

		long expiry = (System.currentTimeMillis() / 1000L) - 100;
		Token expiredToken = new Token();
		expiredToken.setAccess_token(accessToken);
		expiredToken.setRefresh_token(refreshToken);
		expiredToken.setToken_type("Bearer");
		expiredToken.setExpires_in("3600");
		expiredToken.setExpiration(String.valueOf(expiry));

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenReturn(token);

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);
		defaultTokenManager.setTokenCache(expiredToken);

		defaultTokenManager.getToken();
		defaultTokenManager.getToken();

		assertEquals(1, Mockito.mockingDetails(tokenProviderMock).getInvocations().size());
	}

	/**
	 * Test set to ignore due to the assert invocation not been accurate with
	 * multiple threads.
	 */
	@Test
	@Ignore
	public void shouldOnlyRefreshExpiredTokenOnceInMultiThread() {

		long expiry = (System.currentTimeMillis() / 1000L) - 1000;
		Token expiredToken = new Token();
		expiredToken.setAccess_token(accessToken);
		expiredToken.setRefresh_token(refreshToken);
		expiredToken.setToken_type("Bearer");
		expiredToken.setExpires_in("3600");
		expiredToken.setExpiration(String.valueOf(expiry));

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenReturn(token);

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);
		defaultTokenManager.setTokenCache(expiredToken);

		Thread t1 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t2 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t3 = new Thread(new ManagerThreadUtil(defaultTokenManager));
		Thread t4 = new Thread(new ManagerThreadUtil(defaultTokenManager));

		t1.start();
		t2.start();
		t3.start();
		t4.start();

		assertEquals(1, Mockito.mockingDetails(tokenProviderMock).getInvocations().size());
	}

	/**
	 * Mock TokenProvider to throw OAuthServiceException on token retrieval.
	 * Check that TokenManager retries up to the max number of retry attempts.
	 */
	@Test
	public void shouldRetryTokenRetrievalNoMoreThanMax() {
		OAuthServiceException exception = new OAuthServiceException(
				"Token retrival from IAM service failed with refresh token");
		exception.setStatusCode(429);
		exception.setStatusMessage("Too many requests");

		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		when(tokenProviderMock.retrieveToken()).thenThrow(exception);

		defaultTokenManager = new DefaultTokenManager(tokenProviderMock);

		try {
			defaultTokenManager.getToken();
			fail("Should have thrown an OAuthServiceException");
		} catch (OAuthServiceException expected) {
			assertEquals(SDKGlobalConfiguration.IAM_MAX_RETRY, Mockito.mockingDetails(tokenProviderMock).getInvocations().size());
		}
	}
	
	/**
	 * Mock TokenProvider to return expiring token.
	 * Token manager should recognize that token is expiring and attempt to refresh.
	 */
	@Test
	public void shouldRefreshToken() {
		long expiry = (System.currentTimeMillis() / 1000L);
		Token expiringToken = new Token();
		expiringToken.setAccess_token(accessToken);
		expiringToken.setRefresh_token(refreshToken);
		expiringToken.setToken_type("Bearer");
		expiringToken.setExpires_in("3600");
		expiringToken.setExpiration(String.valueOf(expiry));
		
		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		DefaultTokenManager defaultTokenManager = spy(new DefaultTokenManager(tokenProviderMock));
		when(defaultTokenManager.getProvider()).thenReturn(tokenProviderMock);
		when(tokenProviderMock.retrieveToken()).thenReturn(expiringToken);
		Mockito.doNothing().when(defaultTokenManager).submitRefreshTask();
		
		defaultTokenManager.getToken();

		verify(defaultTokenManager, times(1)).submitRefreshTask();
	}
	
	/**
	 * Mock TokenProvider to return non expiring token.
	 * Token manager should not attempt to refresh.
	 */
	@Test
	public void shouldNotRefreshToken() {
		int extraTime = (int) (3600 * SDKGlobalConfiguration.IAM_REFRESH_OFFSET);
		long expiry = (System.currentTimeMillis() / 1000L) + (extraTime * 3);
		Token token = new Token();
		token.setAccess_token(accessToken);
		token.setRefresh_token(refreshToken);
		token.setToken_type("Bearer");
		token.setExpires_in("3600");
		token.setExpiration(String.valueOf(expiry));
		
		TokenProvider tokenProviderMock = mock(TokenProvider.class);
		DefaultTokenManager defaultTokenManager = spy(new DefaultTokenManager(tokenProviderMock));
		when(defaultTokenManager.getProvider()).thenReturn(tokenProviderMock);
		when(tokenProviderMock.retrieveToken()).thenReturn(token);
		Mockito.doNothing().when(defaultTokenManager).submitRefreshTask();
		
		defaultTokenManager.getToken();
		verify(defaultTokenManager, times(0)).submitRefreshTask();
	}
	
	/**
	 * TokenProvider to return null.
	 * Token manager should throw an OAuthServiceException instead 
	 * of a null pointer - CSAFE-38356.
	 */
	@Test(expected = OAuthServiceException.class)
	public void shouldHandleNullTokenWithCorrectExcpetion() {
		
		DefaultTokenManager defaultTokenManager = new DefaultTokenManager(new TokenProviderNull());
		
		defaultTokenManager.getToken();
	}
}
