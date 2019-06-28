package com.ibm.cloud.objectstorage.services.s3.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser;
import com.ibm.cloud.objectstorage.services.s3.model.transform.XmlResponsesSaxParser.ListAllMyBucketsExtendedHandler;

public class ListExtendedBucketHandlerTest {
	
	/**
	 * Test to ensure ListExtendedBucket is mapped correctly from the xml response
	 * @throws Exception 
	 * 
	 */	
    @Test
    public void testListExtendedBucketIsMappedCorrect() throws Exception {
        
        String extendedListingResponse = "<ListAllMyBucketsResult><Owner><ID>cfc52c1b-a6a2-473f-a7ae-045302b1ab49</ID><DisplayName>cfc52c1b-a6a2-473f-a7ae-045302b1ab49</DisplayName>"
                + "</Owner><IsTruncated>true</IsTruncated><MaxKeys>1</MaxKeys><Prefix></Prefix><Marker>foo</Marker><Buckets><Bucket>"
                + "<Name>foojava-extendedlistingtest-201906181446392</Name><CreationDate>2019-06-18T13:46:42.638Z</CreationDate>"
                + "<LocationConstraint>us-standard</LocationConstraint></Bucket></Buckets></ListAllMyBucketsResult>";
        InputStream is = new ByteArrayInputStream( extendedListingResponse.getBytes() );
        
        XmlResponsesSaxParser parser = new XmlResponsesSaxParser();
        ListAllMyBucketsExtendedHandler handler = parser.parseListMyBucketsExtendedResponse(is);
        
        assertEquals(handler.getListBucketsExtendedResponse().getBuckets().get(0).getLocationConstraint(), "us-standard");
        assertEquals(handler.getListBucketsExtendedResponse().getBuckets().get(0).getName(), "foojava-extendedlistingtest-201906181446392");
        assertEquals(handler.getListBucketsExtendedResponse().getMarker(), "foo");
        assertTrue(handler.getListBucketsExtendedResponse().isTruncated());

    }
}
