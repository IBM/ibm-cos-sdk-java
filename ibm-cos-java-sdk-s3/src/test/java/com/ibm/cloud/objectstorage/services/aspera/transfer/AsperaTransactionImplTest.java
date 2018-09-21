package com.ibm.cloud.objectstorage.services.aspera.transfer;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.ibm.aspera.faspmanager2.ITransferListener;

@Ignore
public class AsperaTransactionImplTest {
	
	final String msgQueued = "FASPMGR 2\n"
			+"Type: INIT\n"
			+"Direction: Send\n"
			+"Operation: Transfer\n"
			+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";

	final String msgStats ="FASPMGR 2\n"
				+"Type: STATS\n"
				+"Adaptive: Adaptive\n"
				+"Cipher: aes-128\n"
				+"ClientUser: admin\n"
				+"ClusterNodeId: 0\n"
				+"ClusterNumNodes: 1\n"
				+"CreatePolicy: 0\n"
				+"DSPipelineDepth: 32\n"
				+"DatagramSize: 0\n"
				+"Direction: Send\n"
				+"Encryption: Yes\n";
	
	final String msgError = "FASPMGR 2\n"
				+"Type: ERROR\n"
				+"Code: 5\n"
				+"Description: Server aborted session: Read access denied for user\n"
				+"Loss: 0\n"
				+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
				+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgDone = "FASPMGR 2\n"
				+"Type: DONE\n"
				+"Code: 5\n"
				+"Description: Server aborted session: Read access denied for user\n"
				+"Loss: 0\n"
				+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
				+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	private ITransferListener transferListener;
	final String xferId = "6a31a3b4-3b89-4af4-99ab-1b7dd2d00000";
	
	/**
	 * Test IsDone returns true when the last message received by transferListener
	 * has an ERROR status
	 * 
	 */	
	@Test
	public void testAsperaTransactionIsDoneERROR(){

		transferListener = TransferListener.getInstance(xferId, null);

		AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
		
		transferListener.transferReporter(xferId, msgQueued);
		transferListener.transferReporter(xferId, msgStats);
		transferListener.transferReporter(xferId, msgError);

		assertEquals(asperaTransaction.isDone(), true);
	}

	/**
	 * Test IsDone returns true when the last message received by transferListener
	 * has a DONE status
	 * 
	 */	
	@Test
	public void testAsperaTransactionIsDoneSuccess(){

		transferListener = TransferListener.getInstance(xferId, null);

		AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
		
		transferListener.transferReporter(xferId, msgQueued);
		transferListener.transferReporter(xferId, msgStats);
		transferListener.transferReporter(xferId, msgError);
		transferListener.transferReporter(xferId, msgDone);

		assertEquals(asperaTransaction.isDone(), true);
	}

	/**
	 * Test IsProgress returns true when the last message received by transferListener
	 * has a STATS status
	 * 
	 */	
	@Test
	public void testAsperaTransactionIsProgress(){

		transferListener = TransferListener.getInstance(xferId, null);

		AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
		
		transferListener.transferReporter(xferId, msgQueued);
		transferListener.transferReporter(xferId, msgStats);

		assertEquals(asperaTransaction.progress(), true);
	}

	/**
	 * Test IsQueue returns true when the last message received by transferListener
	 * has a INIT status
	 * 
	 */	
	@Test
	public void testAsperaTransactionIsQueued(){

		transferListener = TransferListener.getInstance(xferId, null);

		AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
		
		transferListener.transferReporter(xferId, msgQueued);

		assertEquals(asperaTransaction.onQueue(), true);
	}
	
    /**
     * Test pause returns true when transaction is paused
     * has a STATS status
     * 
     */ 
    @Test
    public void testAsperaTransactionPause(){

        transferListener = TransferListener.getInstance(xferId, null);

        AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
        
        transferListener.transferReporter(xferId, msgQueued);
        transferListener.transferReporter(xferId, msgStats);

        assertEquals(asperaTransaction.pause(), true);
    }
    
    /**
     * Test pause returns true when transaction is resume
     * has a STATS status
     * 
     */ 
    @Test
    public void testAsperaTransactionResume(){

        transferListener = TransferListener.getInstance(xferId, null);

        AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
        
        transferListener.transferReporter(xferId, msgQueued);
        transferListener.transferReporter(xferId, msgStats);

        assertEquals(asperaTransaction.resume(), true);
    }
    
    /**
     * Test pause returns true when transaction is resume
     * has a STATS status
     * 
     */ 
    @Test
    public void testAsperaTransactionCancel(){

        transferListener = TransferListener.getInstance(xferId, null);

        AsperaTransfer asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, null, null);
        
        transferListener.transferReporter(xferId, msgQueued);
        transferListener.transferReporter(xferId, msgStats);

        assertEquals(asperaTransaction.cancel(), true);
    }

}
