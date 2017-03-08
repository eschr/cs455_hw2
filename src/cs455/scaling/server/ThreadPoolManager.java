
/*
 * Author: Eric Schraeder 
 * March 2017
 * CSU CS 455 HW2-PC
 * 
 * ThreadPoolManager creates ThreadPool and adds new jobs to Task Queue
 * 
 */

package cs455.scaling.server;

import java.util.LinkedList;

import cs455.scaling.resources.BlockingQueue;
import cs455.scaling.resources.Task;

public class ThreadPoolManager {
	
	private ThreadPool threadPool;
	private int workerThreadCount;
	private final BlockingQueue taskQueue;
	
	public ThreadPoolManager(int poolSize) {
		workerThreadCount = poolSize;
		taskQueue = new BlockingQueue();
	}
	
	public void initialize() {
		threadPool = new ThreadPool(workerThreadCount, taskQueue);
		threadPool.initializeWorkerThreads();
	}
	
	// Add new task to the BlockingQueue
	public void addTask(Task t) {
		taskQueue.put(t);
	}
	
	public int getReadCount() { return threadPool.getReadCount(); }
	public int getWriteCount() { return threadPool.getWriteCount(); }
	
	
	/*public static void main(String args[]) throws InterruptedException {
		ThreadPoolManager manager = new ThreadPoolManager(5);
		manager.initialize();
		
		for (int i = 0; i < 500; i++) {
			manager.addTask(new Task("Hello -- " + i, 2));
		}
		
		Thread.sleep(2000);
		

	}*/
	
	
	
}
