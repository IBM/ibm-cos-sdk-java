package com.ibm.cloud.objectstorage.services.s3.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser;
import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser.FASPConnectionInfoHandler;

public class FASPConnectionInfohandlerTest {
	
	/**
	 * Test to ensure FASPConnectionInfo is mapped correctly from the xml response
	 * @throws Exception 
	 * 
	 */	
	@Test
	public void testFASPConnectionInfoIsMappedCorrect() throws Exception {
		
		String AsperaResponse = "<FASPConnectionInfo><AccessKey><Id>123456789</Id><Secret>myAsperaSecret</Secret></AccessKey><ATSEndpoint>ats_endpoint</ATSEndpoint></FASPConnectionInfo>";
		InputStream is = new ByteArrayInputStream( AsperaResponse.getBytes() );
		
		XmlResponsesSaxParser pasrser = new XmlResponsesSaxParser();
		FASPConnectionInfoHandler handler = pasrser.parseFASPConnectionInfoResponse(is);
		
		assertEquals(handler.getFASPConnectionInfo().getAccessKeyId(), "123456789");
		assertEquals(handler.getFASPConnectionInfo().getAccessKeySecret(), "myAsperaSecret");
		assertEquals(handler.getFASPConnectionInfo().getAtsEndpoint(), "ats_endpoint");
	}
}
