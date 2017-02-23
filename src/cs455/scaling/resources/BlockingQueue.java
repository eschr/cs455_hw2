package cs455.scaling.resources;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue {

	private LinkedList<Task> taskList;
	private final Lock lock;
	private final Condition isFull;
	private final Condition isEmpty;
	
	public BlockingQueue() {
		taskList = new LinkedList<Task>();
		lock = new ReentrantLock();
		isFull = lock.newCondition();
		isEmpty = lock.newCondition();
	}
	
	private boolean isEmpty() { return taskList.isEmpty(); }
	
	public void put(Task task) {
		lock.lock();
		try {
			taskList.add(task);
			isEmpty.signalAll();
		}
		finally {
			lock.unlock();
		}
	}
	
	public Task get() {
		Task t = null;
		lock.lock();
		try {
			while (isEmpty()) {
				try {
					isEmpty.await();
				}
				catch (Exception ex) {
					System.out.println(ex.getMessage() + " in BlockingQueue get()");
				}
			}
			
			t = taskList.remove();
			
		} 
		finally {
			lock.unlock();
		}
		return t;
	}
	
}
