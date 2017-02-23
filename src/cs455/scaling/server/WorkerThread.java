package cs455.scaling.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.resources.BlockingQueue;
import cs455.scaling.resources.Task;

public class WorkerThread extends Thread {
	
	private final ThreadPool threadPool;
	private final BlockingQueue workQueue;
	private int workDone = 0;
	
	public WorkerThread(ThreadPool pool, BlockingQueue queue) {
		threadPool = pool;
		workQueue = queue;
	}
	
	public void run() {
		while (true) {
			try {
				processTask(workQueue.get());
				threadPool.increment();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void processTask(Task task) throws NoSuchAlgorithmException {
		System.out.println(this.getName() + SHA1FromBytes(task.getBytes()));
	}
	
	private String SHA1FromBytes(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash = digest.digest(bytes);
		 
		BigInteger hashBigInt = new BigInteger(1, hash);
		return hashBigInt.toString(16);
	}
}
