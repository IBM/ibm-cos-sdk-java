package com.amazonaws.services.s3.internal;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.transform.Unmarshallers;
import com.amazonaws.transform.Unmarshaller;

public class S3XmlResponseHandlerTest {

	/**
	 * Test the IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK are set in the ObjectLIsting
	 * response object
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testHeadersAddedToObjectListing() throws Exception {
		
		Unmarshaller<ObjectListing, InputStream> unmarshaller = new Unmarshallers.ListObjectsUnmarshaller(false);
		S3XmlResponseHandler xmlResponseHandler = new S3XmlResponseHandler<ObjectListing>(unmarshaller);
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, "True");
		httpResponse.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, "123456");

		InputStream is = new ByteArrayInputStream(getXmlContent().getBytes());;
		httpResponse.setContent(is);

		AmazonWebServiceResponse<ObjectListing> objectListing = xmlResponseHandler.handle(httpResponse);
		
		assertEquals(objectListing.getResult().getIBMSSEKPCrk(), "123456");
		assertEquals(objectListing.getResult().getIBMSSEKPEnabled(), true);
	}
	
	/**
	 * Test the IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK null headers are handled
	 * 
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testNullKPHeadersAreHandled() throws Exception {
		
		Unmarshaller<ObjectListing, InputStream> unmarshaller = new Unmarshallers.ListObjectsUnmarshaller(false);
		S3XmlResponseHandler xmlResponseHandler = new S3XmlResponseHandler<ObjectListing>(unmarshaller);
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_CRK, null);

		InputStream is = new ByteArrayInputStream(getXmlContent().getBytes());;
		httpResponse.setContent(is);

		AmazonWebServiceResponse<ObjectListing> objectListing = xmlResponseHandler.handle(httpResponse);
		
		assertEquals(objectListing.getResult().getIBMSSEKPCrk(), null);
		assertEquals(objectListing.getResult().getIBMSSEKPEnabled(), false);
	}
	
	/**
	 * Test the IBM_SSE_KP_ENABLED & IBM_SSE_KP_CRK empty headers are handled
	 * 
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testEmptyKPHeadersAreHandled() throws Exception {
		
		Unmarshaller<ObjectListing, InputStream> unmarshaller = new Unmarshallers.ListObjectsUnmarshaller(false);
		S3XmlResponseHandler xmlResponseHandler = new S3XmlResponseHandler<ObjectListing>(unmarshaller);
		HttpResponse httpResponse = new HttpResponse(null, null);

		InputStream is = new ByteArrayInputStream(getXmlContent().getBytes());;
		httpResponse.setContent(is);

		AmazonWebServiceResponse<ObjectListing> objectListing = xmlResponseHandler.handle(httpResponse);
		
		assertEquals(objectListing.getResult().getIBMSSEKPCrk(), null);
		assertEquals(objectListing.getResult().getIBMSSEKPEnabled(), false);
	}
	
	/**
	 * Test the IBM_SSE_KP_CRK empty header is handled
	 * 
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testOnlyKPEnabledHeaderIsSet() throws Exception {
		
		Unmarshaller<ObjectListing, InputStream> unmarshaller = new Unmarshallers.ListObjectsUnmarshaller(false);
		S3XmlResponseHandler xmlResponseHandler = new S3XmlResponseHandler<ObjectListing>(unmarshaller);
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_ENABLED, "True");

		InputStream is = new ByteArrayInputStream(getXmlContent().getBytes());;
		httpResponse.setContent(is);

		AmazonWebServiceResponse<ObjectListing> objectListing = xmlResponseHandler.handle(httpResponse);
		
		assertEquals(objectListing.getResult().getIBMSSEKPCrk(), null);
		assertEquals(objectListing.getResult().getIBMSSEKPEnabled(), true);
	}
	
	/**
	 * Test the IBM_SSE_KP_CRK empty header is handled
	 * 
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testOnlyCRKHeaderIsSet() throws Exception {
		
		Unmarshaller<ObjectListing, InputStream> unmarshaller = new Unmarshallers.ListObjectsUnmarshaller(false);
		S3XmlResponseHandler xmlResponseHandler = new S3XmlResponseHandler<ObjectListing>(unmarshaller);
		HttpResponse httpResponse = new HttpResponse(null, null);
		httpResponse.addHeader(Headers.IBM_SSE_KP_CUSTOMER_ROOT_KEY_CRN, "34567");

		InputStream is = new ByteArrayInputStream(getXmlContent().getBytes());;
		httpResponse.setContent(is);

		AmazonWebServiceResponse<ObjectListing> objectListing = xmlResponseHandler.handle(httpResponse);
		
		assertEquals(objectListing.getResult().getIBMSSEKPCrk(), "34567");
		assertEquals(objectListing.getResult().getIBMSSEKPEnabled(), false);
	}
	
	private String getXmlContent() {
		
		final String xml = "<ListBucketResult>"
							+ "<Name>bucket</Name>"
							+ "<Prefix/>"
    							+ "<Marker/>"
    							+ "<MaxKeys>1000</MaxKeys>"
    							+ "<IsTruncated>false</IsTruncated>"
    							+ "<Contents>"
    							+ "<Key>my-image.jpg</Key>"
    					        + "<LastModified>2009-10-12T17:50:30.000Z</LastModified>"
    					        + "<ETag>&quot;fba9dede5f27731c9771645a39863328&quot;</ETag>"
    					        + "<Size>434234</Size>"
    					        + "<StorageClass>STANDARD</StorageClass>"
    					        + "<Owner>"
    					        + "    <ID>75aa57f09aa0c8caeab4f8c24e99d10f8e7faeebf76c078efc7c6caea54ba06a</ID>"
    					        + "    <DisplayName>mtd@amazon.com</DisplayName>"
    					        + "</Owner>"
    					        + "</Contents>"
    							+ "<Contents>"
       							+ "<Key>my-third-image.jpg</Key>"
         							+ "<LastModified>2009-10-12T17:50:30.000Z</LastModified>"
         							+ "<ETag>&quot;1b2cf535f27731c974343645a3985328&quot;</ETag>"
         							+ "<Size>64994</Size>"
         							+ "<StorageClass>STANDARD_IA</StorageClass>"
         							+ "<Owner>"
            							+ "<ID>75aa57f09aa0c8caeab4f8c24e99d10f8e7faeebf76c078efc7c6caea54ba06a</ID>"
            							+ "<DisplayName>mtd@amazon.com</DisplayName>"
        							+ "</Owner>"
    							+ "</Contents>"
							+ "</ListBucketResult>";
		
		return xml;
	}
}
