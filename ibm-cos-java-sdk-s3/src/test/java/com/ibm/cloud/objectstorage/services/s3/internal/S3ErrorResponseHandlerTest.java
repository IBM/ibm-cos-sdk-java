package com.ibm.cloud.objectstorage.services.s3.internal;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.ibm.cloud.objectstorage.DefaultRequest;
import com.ibm.cloud.objectstorage.Request;
import com.ibm.cloud.objectstorage.http.HttpMethodName;
import com.ibm.cloud.objectstorage.http.HttpResponse;
import com.ibm.cloud.objectstorage.services.s3.Headers;
import com.ibm.cloud.objectstorage.services.s3.model.AmazonS3Exception;

public class S3ErrorResponseHandlerTest {

	@Test
	public void testRegionHeaderIsAddedToExcpetion() throws XMLStreamException {

		String region = "myDummyRegion";
		String strResponse = "<Error><Code>PermanentRedirect</Code><Message>The bucket is in this region: null. Please use this region to retry the request</Message></Error>";
		ByteArrayInputStream content = new ByteArrayInputStream(strResponse.getBytes(Charset.forName("UTF-8")));
		
		S3ErrorResponseHandler errorHandler = new S3ErrorResponseHandler();
		Request request = new DefaultRequest("default");
		request.setHttpMethod(HttpMethodName.GET);
		HttpResponse response = new HttpResponse(request, null);
		
		response.setContent(content);
		response.addHeader(Headers.S3_BUCKET_REGION, region);
		
		AmazonS3Exception exception = (AmazonS3Exception)errorHandler.handle(response);
		
		assertTrue(exception.getAdditionalDetails().get(Headers.S3_BUCKET_REGION).equals(region));
		
	}

}
