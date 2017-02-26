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
				System.out.print("THREAD: " + this.getName());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void incrementWork() { workDone++; }
	
	private void addTaskBacktoQueue(Task t) { 
		workQueue.put(t);
	}
	
	private void processTask(Task task) throws NoSuchAlgorithmException, IOException {
		incrementWork();
		int type = task.getType();
		SelectionKey key = task.getKey();
		if (type == READ) {
			task.readFromBuffer();
			task.setType(HASH);
			addTaskBacktoQueue(task);
		}
		else if (type == HASH) {
			String hash = SHA1FromBytes(task.getBytes());
			System.out.println(hash);
			task.setHash(hash);
			task.setType(WRITE);
			addTaskBacktoQueue(task);
		}
		else {
			task.writeHashBackToClient();
			if (key.isValid()) {
				key.interestOps(SelectionKey.OP_READ);
				key.selector().wakeup();
			}
		}
		
	}
	
	private String SHA1FromBytes(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash = digest.digest(bytes);
		 
		BigInteger hashBigInt = new BigInteger(1, hash);
		return hashBigInt.toString(16);
	}
}
