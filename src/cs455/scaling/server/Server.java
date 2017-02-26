package cs455.scaling.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import cs455.scaling.resources.Task;

public class Server {
	
	private int portNum, workerThreadCount;
	private ThreadPoolManager threadPoolManager;
	
	public Server(int port, int poolSize) {
		portNum = port;
		workerThreadCount = poolSize;
	}
	
	public void initiate() throws IOException {
		threadPoolManager = new ThreadPoolManager(workerThreadCount);
		threadPoolManager.initialize();
		try {
			ServerChannel serverChannel = new ServerChannel(portNum, this);
			new Thread(serverChannel).start();
		}
		catch (IOException e) {
			System.out.println(e.getMessage() + " in Server initiate()");
		}
	}
	
	public int getResults() {
		return threadPoolManager.getCount();
	}

	
	public static void main(String args[]) throws IOException {
		if (args.length != 2) {
			System.out.println("See usage: java bin/ cs455.scaling.server.Server <portnum> <thread-pool-size>");
			System.exit(-1);
		}
		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.initiate();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            System.out.println("In shutdown hook");
	            System.out.println(server.getResults());
	        }
	    }, "Shutdown-thread"));
	}
	
	public void acceptRead(SelectionKey key) {
		threadPoolManager.addTask(new Task(key, 1));
	}
}
