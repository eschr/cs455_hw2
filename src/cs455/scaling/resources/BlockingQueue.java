package cs455.scaling.resources;

import java.util.LinkedList;


public class BlockingQueue {

	private LinkedList<Task> taskList;
	
	public BlockingQueue() {
		taskList = new LinkedList<Task>();
	}
	
	private boolean isEmpty() { return taskList.isEmpty(); }
	
	public void put(Task task) {
		synchronized(this) {
			try {
				taskList.add(task);
				notifyAll();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public Task get() {
		Task t = null;
		synchronized(this) {
			while (isEmpty()) {
				try {
					wait();
				}
				catch (Exception ex) {
					System.out.println(ex.getMessage() + " in BlockingQueue get()");
				}
			}
			t = taskList.remove();
			return t;
		}
	}
	
}
