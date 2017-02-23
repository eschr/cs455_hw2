package cs455.scaling.server;

import java.util.LinkedList;

import cs455.scaling.resources.BlockingQueue;
import cs455.scaling.resources.Task;

public class ThreadPool {
	
	private int workerThreadCount;
	private WorkerThread[] threadPool;
	private final BlockingQueue taskQueue;
	private int count;
	
	public ThreadPool(int size, BlockingQueue queue) {
		workerThreadCount = size; 
		taskQueue = queue;
	}
	
	public synchronized void increment() {
		count++;
	}
	
	public int getCount() { return count; }
	
	public void initializeWorkerThreads() {
		threadPool = new WorkerThread[workerThreadCount];
		for (int i = 0; i < workerThreadCount; i++) {
			threadPool[i] = new WorkerThread(this, taskQueue);
			threadPool[i].setName("Thread: " + i + " -- ");
			threadPool[i].start();
		}
	}
	
	
	
	

}
