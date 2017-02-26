package cs455.scaling.server;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.SelectionKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.resources.BlockingQueue;
import cs455.scaling.resources.Task;

public class WorkerThread extends Thread {
	
	private final ThreadPool threadPool;
	private static int workDone = 0;
	private static final  int READ = 1;
	private static final int HASH = 2;
	private static final int WRITE = 3;
	private final BlockingQueue workQueue;
	
	public WorkerThread(ThreadPool pool, BlockingQueue queue) {
		threadPool = pool;
		this.workQueue = queue;
	}
	
	public void run() {
		while (true) {
			try {
				processTask(workQueue.get());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void incrementWork() { workDone++; }
	
	private void processTask(Task task) throws NoSuchAlgorithmException, IOException {
		incrementWork();
		int type = task.getType();
		if (type == READ) {
			SelectionKey key = task.getKey();
		    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
			int val = task.readAndSendBackEcho();
			threadPool.increment(val);
			key.interestOps (key.interestOps(  ) | SelectionKey.OP_READ);
            // Cycle the selector so this key is active again
            key.selector().wakeup(  );
		}
		else if (type == HASH) {
			System.out.println(this.getName() + SHA1FromBytes(task.getBytes()) + " WORK: " + workDone);
		}
		else {
			//Do write actions
		}
		
		/*
		if (workDone % 100 == 0) 
			System.out.println(this.getName() + SHA1FromBytes(task.getBytes()) + " WORK: " + workDone);
		else {
			SHA1FromBytes(task.getBytes());
		}*/
	}
	
	private String SHA1FromBytes(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash = digest.digest(bytes);
		 
		BigInteger hashBigInt = new BigInteger(1, hash);
		return hashBigInt.toString(16);
	}
}
