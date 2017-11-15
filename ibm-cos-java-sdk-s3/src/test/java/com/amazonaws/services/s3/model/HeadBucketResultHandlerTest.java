package com.amazonaws.services.s3.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.transform.HeadBucketResultHandler;

public class HeadBucketResultHandlerTest {
	
	/**
	 * Test the IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK are set in the HeadResult object
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testHeadResultIsSetCorrect() throws Exception {
		
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, "True");
		httpResponse.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, "123456");
		
		HeadBucketResultHandler handler = new HeadBucketResultHandler();
		AmazonWebServiceResponse<HeadBucketResult> result = handler.handle(httpResponse);
		
		assertEquals(result.getResult().getIBMSSEKPCrk(), "123456");
		assertEquals(result.getResult().getIBMSSEKPEnabled(), true);
	}
	
	/**
	 * Test null IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK headers are handled
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testNullHeadersAreHandled() throws Exception {
		
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, null);
		
		HeadBucketResultHandler handler = new HeadBucketResultHandler();
		AmazonWebServiceResponse<HeadBucketResult> result = handler.handle(httpResponse);
		
		assertEquals(result.getResult().getIBMSSEKPCrk(), null);
		assertEquals(result.getResult().getIBMSSEKPEnabled(), false);
	}
	
	/**
	 * Test empty IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK headers are handled
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testEmptyHeadersAreHandled() throws Exception {
		
		HttpResponse httpResponse = new HttpResponse(null, null);
		
		HeadBucketResultHandler handler = new HeadBucketResultHandler();
		AmazonWebServiceResponse<HeadBucketResult> result = handler.handle(httpResponse);
		
		assertEquals(result.getResult().getIBMSSEKPCrk(), null);
		assertEquals(result.getResult().getIBMSSEKPEnabled(), false);
	}

	/**
	 * Test empty IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK headers are handled
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testOnlyKPEnabledHeaderIsSet() throws Exception {
		
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, "True");
		
		HeadBucketResultHandler handler = new HeadBucketResultHandler();
		AmazonWebServiceResponse<HeadBucketResult> result = handler.handle(httpResponse);
		
		assertEquals(result.getResult().getIBMSSEKPCrk(), null);
		assertEquals(result.getResult().getIBMSSEKPEnabled(), true);
	}
	
	/**
	 * Test empty IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK headers are handled
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testOnlyCRKHeaderIsSet() throws Exception {
		
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, "12345");
		
		HeadBucketResultHandler handler = new HeadBucketResultHandler();
		AmazonWebServiceResponse<HeadBucketResult> result = handler.handle(httpResponse);
		
		assertEquals(result.getResult().getIBMSSEKPCrk(), "12345");
		assertEquals(result.getResult().getIBMSSEKPEnabled(), false);
	}
}
