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
	
	public void addTask(Task t) {
		taskQueue.put(t);
	}
	
	public int getCount() { return threadPool.getCount(); }
	
	
	public static void main(String args[]) throws InterruptedException {
		ThreadPoolManager manager = new ThreadPoolManager(5);
		manager.initialize();
		/*
		for (int i = 0; i < 50000000; i++) {
			manager.addTask(new Task("Hello -- " + i));
		}
		*/
		Thread.sleep(2000);
		
		System.out.println(manager.getCount());
	}
	
	
	
}
