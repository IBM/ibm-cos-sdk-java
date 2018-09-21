package com.ibm.cloud.objectstorage.services.aspera.transfer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.aspera.faspmanager2.ITransferListener;
import com.ibm.aspera.faspmanager2.faspmanager2;
import com.ibm.cloud.objectstorage.event.ProgressEvent;
import com.ibm.cloud.objectstorage.event.ProgressListener;
import com.ibm.cloud.objectstorage.event.SyncProgressListener;
import com.ibm.cloud.objectstorage.services.s3.transfer.TransferProgress;
import com.ibm.cloud.objectstorage.services.s3.transfer.internal.S3ProgressListenerChain;
import com.ibm.cloud.objectstorage.services.s3.transfer.internal.TransferProgressUpdatingListener;

import org.apache.commons.logging.Log;
import org.hamcrest.core.IsEqual;

/**
 * 
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ faspmanager2.class, TransferListener.class, ITransferListener.class, AsperaLibraryLoader.class  })
public class AsperaTransactionProgressTest {

	@Mock
    protected Log mockLog = mock(Log.class);
	
	final String msgInit = "FASPMGR 2\n"
					+"Type: INIT\n"
					+"Direction: Send\n"
					+"Operation: Transfer\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";

	final String msgSession ="FASPMGR 2\n"
					+"Type: SESSION\n"
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
	
	final String msgNotification = "FASPMGR 2\n" + 
			"Type: NOTIFICATION\n" + 
			"Adaptive: Adaptive\n" + 
			"MinRate: 0\n" + 
			"PreTransferBytes: 1048576\n" + 
			"PreTransferFiles: 1\n" + 
			"Rate: 700000\n" + 
			"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n" + 
			"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";

	final String msgStats1 ="FASPMGR 2\n"
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
				+"Encryption: Yes\n"
				+"FileBytes: 262144\n"
				+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
				+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";;
	
	final String msgStats2 ="FASPMGR 2\n"
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
			+"Encryption: Yes\n"
			+"FileBytes: 524288\n"
			+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";;
	
	final String msgStats3 ="FASPMGR 2\n"
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
			+"Encryption: Yes\n"
			+"FileBytes: 786432\n"
			+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
			+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";;

	final String msgError = "FASPMGR 2\n"
					+"Type: ERROR\n"
					+"Code: 5\n"
					+"Description: Server aborted session: Read access denied for user\n"
					+"Loss: 0\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n";
	
	final String msgDone = "FASPMGR 2\n"
					+"Type: DONE\n"
					+"Direction: Send\n"
					+"FileBytes: 1048576\n"
					+"Operation: Transfer\n"
					+"SessionId: f8fdea60-e416-4a17-a488-9abe093290a9\n"
					+"UserStr: 6a31a3b4-3b89-4af4-99ab-1b7dd2d00000\n"
					+"TransferBytes: 4096\n"
					+"TransfersAttempted: 1\n";

	final String xferId = "6a31a3b4-3b89-4af4-99ab-1b7dd2d00000";
	
	@Before
    public void setup(){
		PowerMockito.mockStatic(faspmanager2.class);
		PowerMockito.mockStatic(AsperaLibraryLoader.class);
		PowerMockito.mockStatic(ITransferListener.class);

		PowerMockito.suppress(PowerMockito.defaultConstructorIn(ITransferListener.class));
	}

	/**
	 * Test correct bytes transferred is reported after multiple calls to transferReporter 
	 * 
	 */	
	@Test
	public void testTransferProgressUpdates(){

		TransferListener transferListener = new TransferListener();
		
		TransferProgress transferProgress = new TransferProgress();

        S3ProgressListenerChain listenerChain = new S3ProgressListenerChain(
                new TransferProgressUpdatingListener(transferProgress),
                null);
		AsperaTransaction asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, transferProgress, listenerChain);
		
		
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgNotification);
		transferListener.transferReporter(xferId, msgSession);
		
		transferListener.transferReporter(xferId, msgStats1);
		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 262144l);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 25.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgStats2);
		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 524288l);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 50.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgStats3);
		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 786432l);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 75.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgDone);

		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 1048576);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 100.0d, 0.0);
	}


	/**
	 * Test correct bytes transferred is reported after error calls to transferReporter 
	 * 
	 */	
	@Test
	public void testTransferProgressError(){

		TransferListener transferListener = new TransferListener();
		
		TransferProgress transferProgress = new TransferProgress();

        S3ProgressListenerChain listenerChain = new S3ProgressListenerChain(
                new TransferProgressUpdatingListener(transferProgress),
                null);
		AsperaTransaction asperaTransaction = new AsperaTransactionImpl(xferId, null, null, null, transferProgress, listenerChain);
		
		
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgNotification);
		transferListener.transferReporter(xferId, msgSession);
		
		transferListener.transferReporter(xferId, msgStats1);
		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 262144l);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 25.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgError);

		assertEquals(asperaTransaction.getProgress().getBytesTransferred(), 262144l);
		assertEquals(asperaTransaction.getProgress().getPercentTransferred(), 25.0d, 0.0);
	}
	
	/**
	 * Test correct bytes transferred is reported after multiple calls to transferReporter 
	 * 
	 */	
	@Test
	public void testCustomTransferProgressUpdates(){

		TransferListener transferListener = new TransferListener();
		
		TransferProgress transferProgress = new TransferProgress();
		
		CustomProgress customProgress = new CustomProgress();
		customProgress.setTotalBytesToTransfer(1048576);
		CustomProgressUpdatingListener customProgressUpdatingListener = new CustomProgressUpdatingListener(customProgress);

        S3ProgressListenerChain listenerChain = new S3ProgressListenerChain(
                new TransferProgressUpdatingListener(transferProgress),
                customProgressUpdatingListener);
        
		new AsperaTransactionImpl(xferId, null, null, null, transferProgress, listenerChain);
		
		
		transferListener.transferReporter(xferId, msgInit);
		transferListener.transferReporter(xferId, msgNotification);
		transferListener.transferReporter(xferId, msgSession);
		
		transferListener.transferReporter(xferId, msgStats1);
		assertEquals(customProgress.getBytesTransferred(), 262144l);
		assertEquals(customProgress.getPercentTransferred(), 25.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgStats2);
		assertEquals(customProgress.getBytesTransferred(), 524288l);
		assertEquals(customProgress.getPercentTransferred(), 50.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgStats3);
		assertEquals(customProgress.getBytesTransferred(), 786432l);
		assertEquals(customProgress.getPercentTransferred(), 75.0d, 0.0);
		
		transferListener.transferReporter(xferId, msgDone);

		assertEquals(customProgress.getBytesTransferred(), 1048576);
		assertEquals(customProgress.getPercentTransferred(), 100.0d, 0.0);
	}
}

class CustomProgress {
	private volatile long bytesTransferred = 0;
    private volatile long totalBytesToTransfer = -1;
    
    /**
     * Returns the number of bytes completed in the associated transfer.
     *
     * @return The number of bytes completed in the associated transfer.
     */
    public long getBytesTransferred() {
        return bytesTransferred;
    }

    /**
     * Returns the total size in bytes of the associated transfer, or -1
     * if the total size isn't known.
     *
     * @return The total size in bytes of the associated transfer.
     * 		   Returns or -1 if the total size of the associated
     * 		   transfer isn't known yet.
     */
    public long getTotalBytesToTransfer() {
        return totalBytesToTransfer;
    }
    
    /**
     * Returns a percentage of the number of bytes transferred out of the total
     * number of bytes to transfer.
     *
     * @return A percentage of the number of bytes transferred out of the total
     *         number of bytes to transfer; or -1.0 if the total length is not known.
     */
    public synchronized double getPercentTransferred() {
        if (getBytesTransferred() < 0) return 0;

        return totalBytesToTransfer < 0
             ? -1.0 
             : ((double)bytesTransferred / (double)totalBytesToTransfer) * (double)100;
    }

    public synchronized void updateProgress(long bytes) {
        this.bytesTransferred += bytes;
        if (totalBytesToTransfer > -1
        &&  this.bytesTransferred > this.totalBytesToTransfer) {
            this.bytesTransferred = this.totalBytesToTransfer;
        }
    }

    public void setTotalBytesToTransfer(long totalBytesToTransfer) {
        this.totalBytesToTransfer = totalBytesToTransfer;
    }
}

class CustomProgressUpdatingListener extends SyncProgressListener {
    private final CustomProgress customProgress;

    public CustomProgressUpdatingListener(CustomProgress customProgress) {
        this.customProgress = customProgress;
    }

    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytesTransferred();
        if (bytes == 0)
            return; // only interested in non-zero bytes
        customProgress.updateProgress(bytes);
    }
}