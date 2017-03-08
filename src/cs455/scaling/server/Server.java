/*
 * Author: Eric Schraeder 
 * March 2017
 * CSU CS 455 HW2-PC
 * 
 * Implementation of scalable server using Java NIO library.  
 * 
 * Server.java starts the NIO ServerSocketChannel and Thread Pool Manager
 * 
 */

package cs455.scaling.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import cs455.scaling.resources.Task;

public class Server {
	
	private int portNum, workerThreadCount;
	private ThreadPoolManager threadPoolManager;
	private ServerChannel serverChannel;
	
	public Server(int port, int poolSize) {
		portNum = port;
		workerThreadCount = poolSize;
	}
	
	public void initiate() throws IOException {
		threadPoolManager = new ThreadPoolManager(workerThreadCount);
		threadPoolManager.initialize();
		try {
			serverChannel = new ServerChannel(portNum, this);
			new Thread(serverChannel).start();
		}
		catch (IOException e) {
			System.out.println(e.getMessage() + " in Server initiate()");
		}
		
		new Thread(new ServerStats(this)).start();
	}
	
	
	// Accessed by ServerStats Thread to print out Server throughput every 5 seconds 
	public String getReadWriteStats() {
		int reads = threadPoolManager.getReadCount() / 5;
		int writes = threadPoolManager.getWriteCount() / 5;
		return reads + ":" + writes + "/s";
	}
	
	public int getActiveConnections() {
		return serverChannel.getClientCount();
	}
	
	public static void main(String args[]) throws IOException {
		if (args.length != 2) {
			System.out.println("See usage: java bin/ cs455.scaling.server.Server <portnum> <thread-pool-size>");
			System.exit(-1);
		}
		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.initiate();
	}
	
	public void acceptRead(SelectionKey key) {
		threadPoolManager.addTask(new Task(key, 1));
	}
}
