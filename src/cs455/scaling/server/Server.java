package cs455.scaling.server;

import java.io.IOException;

public class Server {
	
	private int portNum, workerThreadCount;
	private ThreadPoolManager threadPoolManager;
	
	public Server(int port, int poolSize) {
		portNum = port;
		workerThreadCount = poolSize;
	}
	
	public void initiate() throws IOException {
		threadPoolManager = new ThreadPoolManager(workerThreadCount);
		try {
			ServerChannel serverChannel = new ServerChannel(portNum);
			new Thread(serverChannel).start();
		}
		catch (IOException e) {
			System.out.println(e.getMessage() + " in Server initiate()");
		}
	}
	
	public static void main(String args[]) throws IOException {
		if (args.length != 2) {
			System.out.println("See usage: java bin/ cs455.scaling.server.Server <portnum> <thread-pool-size>");
			System.exit(-1);
		}
		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.initiate();
	}
}
