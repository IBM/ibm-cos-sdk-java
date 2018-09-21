package com.ibm.cloud.objectstorage.services.aspera.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.services.aspera.transfer.internal.AsperaTransferManagerUtils;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3Client;

/**
 * Unit testing of the AsperaTransferManagerUtilsTest
 * 
 */
public class AsperaTransferManagerUtilsTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	/**
	 * Test path in json is set to [REDACTED] successfully
	 */	
	@Test
	public void testPathRedactedSuccessfully() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"token\" : \"abcd\""
			+ "}";
		String[] jsonPathsToRedact = {
			"token"
		};

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertTrue(rtn.contains("[REDACTED]"));
		assertFalse(rtn.contains("abcd"));
	}
	
	/**
	 * Test path in json is set to [REDACTED] successfully
	 */	
	@Test
	public void testChildPathRedactedSuccessfully() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"token\" : \"abcd\""
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
			"token"
		};

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertTrue(rtn.contains("[REDACTED]"));
		assertFalse(rtn.contains("abcd"));
	}
	
	/**
	 * Test json is unaltered if the path is not found in the json tree
	 */	
	@Test
	public void testChildPathNoChangeIfPathDoesNotExist() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"token\" : \"abcd\""
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
			"fieldDoesNotExist"
		};
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(jsonString);
   		String original = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(original, rtn);
	}
	
	/**
	 * Test json is unaltered if no paths provided
	 */	
	@Test
	public void testChildPathNoChangeIfNoPathsProvided() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"token\" : \"abcd\""
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
		};
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(jsonString);
   		String original = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(original, rtn);
	}
	
	/**
	 * Test json is unaltered if no paths provided
	 */	
	@Test
	public void testChildFullObjectRedacted() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"securityCredentials\" : {"
			+ "      \"token\" : \"abcd\""
			+ "    }"
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
			"securityCredentials"
		};
		
		final String expectedRtn = 
				  "{"
				+ "  \"name\" : \"blah\","
				+ "  \"childNode\" : {"
				+ "    \"field1\" : \"someText\","
				+ "    \"securityCredentials\" : \"[REDACTED]\""
				+ "  }"
				+ "}";
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(expectedRtn);
   		String expectedRtnResult = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(expectedRtnResult, rtn);
	}
	
	@Test
	public void testChildAllArrayItemsRedacted() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNodes\" : ["
			+ "    {"
			+ "      \"childNode1\" : {"
			+ "        \"field1\" : \"someText\","
			+ "        \"securityCredentials\" : {"
			+ "          \"token\" : \"abcd\""
			+ "        }"
			+ "      }"
			+ "    },{"
			+ "      \"childNode2\" : {"
			+ "        \"field1\" : \"someText\","
			+ "        \"securityCredentials\" : {"
			+ "          \"token\" : \"wxyz\""
			+ "        }"
			+ "      }"
			+ "    }"
			+ "  ]"
			+ "}";
		String[] jsonPathsToRedact = {
			"securityCredentials"
		};
		
		final String expectedRtn = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNodes\" : ["
			+ "    {"
			+ "      \"childNode1\" : {"
			+ "        \"field1\" : \"someText\","
			+ "        \"securityCredentials\" : \"[REDACTED]\""
			+ "      }"
			+ "    },{"
			+ "      \"childNode2\" : {"
			+ "        \"field1\" : \"someText\","
			+ "        \"securityCredentials\" : \"[REDACTED]\""
			+ "      }"
			+ "    }"
			+ "  ]"
			+ "}";
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(expectedRtn);
   		String expectedRtnResult = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(expectedRtnResult, rtn);
	}
	
	/**
	 * Test json is unaltered if no paths provided
	 */	
	@Test
	public void testNoExceptionIfNullPathToRedact() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"token\" : \"abcd\""
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
			null
		};
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(jsonString);
   		String original = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(original, rtn);
	}
	
	/**
	 * Test no exception thrown on error condition
	 */	
	@Test
	public void testNoExceptionIfEmptyStringPathToRedact() throws Exception {
		
		final String jsonString = 
			  "{"
			+ "  \"name\" : \"blah\","
			+ "  \"childNode\" : {"
			+ "    \"field1\" : \"someText\","
			+ "    \"token\" : \"abcd\""
			+ "  }"
			+ "}";
		String[] jsonPathsToRedact = {
			""
		};
		// Need to pass string through jackson as the returned string
		// from redacted call has spaces removed etc
		ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootJsonNode = mapper.readTree(jsonString);
   		String original = mapper.writeValueAsString(rootJsonNode);

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals(original, rtn);
	}
	
	/**
	 * Test json is unaltered if no paths provided
	 */	
	@Test
	public void testNoExceptionNullJsonStringProvided() throws Exception {
		
		final String jsonString = null;
		String[] jsonPathsToRedact = {
			"token"
		};

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals("", rtn);
	}
	
	/**
	 * Test json is unaltered if no paths provided
	 */	
	@Test
	public void testNoExceptionEmptyJsonStringProvided() throws Exception {
		
		final String jsonString = "";
		String[] jsonPathsToRedact = {
			"token"
		};

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals("", rtn);
	}
	
	/**
	 * Test json is unaltered if invalid json provided
	 */	
	@Test
	public void testNoExceptionWhenInvalidJsonStringProvided() throws Exception {

		final String jsonString = "{ xyz";
		String[] jsonPathsToRedact = {
			"token"
		};

		String rtn = AsperaTransferManagerUtils.getRedactedJsonString(jsonString, jsonPathsToRedact);
		assertEquals("", rtn);
	}
	
}
