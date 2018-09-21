package com.ibm.cloud.objectstorage.services.aspera.transfer.internal;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AsperaTransferManagerUtils {
	
	/**
     * Returns a new thread pool configured with the default settings.
     *
     * @return A new thread pool configured with the default settings.
     */
    public static ThreadPoolExecutor createDefaultExecutorService() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("aspera-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor)Executors.newFixedThreadPool(10, threadFactory);
    }
    
    /**
     * Returns a String of the json passed in, but with any jsonPathsToRedact
     * marked as [REDACTED]<br />
     * On Error will return an empty string
     * @param jsonString The original json string to be redacted
     * @param jsonPathsToRedact The json path values which are to be redacted
     * @return The jsonString passed in, but with specific values marked as [REDACTED]
     */
    public static String getRedactedJsonString(final String jsonString, final String... jsonPathsToRedact) {
    	String rtn = "";
    	if (jsonString != null && jsonString != "") {
    		rtn = jsonString;
    		try {
    			ObjectMapper mapper = new ObjectMapper();
    			JsonNode rootJsonNode = mapper.readTree(jsonString);
    			List<JsonNode> nodesToRedact;
    			ObjectNode writableNodeToRedact;
    			for (String jsonPathToRedact : jsonPathsToRedact) {
    				if (jsonPathToRedact != null && jsonPathToRedact != "") {
    					nodesToRedact = rootJsonNode.findParents(jsonPathToRedact);
    					for (JsonNode jsonNodeToRedact : nodesToRedact) {

    						writableNodeToRedact = (ObjectNode)jsonNodeToRedact;
    						if (writableNodeToRedact != null && !writableNodeToRedact.isMissingNode()) {
    							writableNodeToRedact.put(jsonPathToRedact, "[REDACTED]");
    						}
    					}
   					}
    			}
    			rtn = mapper.writeValueAsString(rootJsonNode);
    		} catch (IOException e) {
    			rtn = "";
    		}
    	}
    	return rtn;
    }
}
