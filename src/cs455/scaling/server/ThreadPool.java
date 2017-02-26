package cs455.scaling.server;

import java.util.LinkedList;

import cs455.scaling.resources.BlockingQueue;
import cs455.scaling.resources.Task;

public class ThreadPool {
	
	private int workerThreadCount;
	private LinkedList<WorkerThread> idleWorkers;
	private final BlockingQueue taskQueue;
	private int count;
	
	public ThreadPool(int size, BlockingQueue queue) {
		workerThreadCount = size; 
		taskQueue = queue;
		idleWorkers = new LinkedList<WorkerThread>();
	}
	
	public synchronized void increment(int sum) {
		count += sum;
	}
	
	public synchronized int getCount() { return count; }
	
	public void initializeWorkerThreads() {
		for (int i = 0; i < workerThreadCount; i++) {
			WorkerThread worker = new WorkerThread(this, taskQueue);
			worker.setName("Worker_" + i);
			worker.start();
			idleWorkers.add(worker);
		}
	}
	
	public WorkerThread getIdleWorker() {
		WorkerThread worker = null;
		synchronized (idleWorkers) {
			if (idleWorkers.size() > 0) {
				worker = idleWorkers.remove();
			}
		}
		
		return worker;
	}
	
	public void addWorkerBack(WorkerThread worker) {
		synchronized (idleWorkers) {
			idleWorkers.add(worker);
		}
	}
	

}
