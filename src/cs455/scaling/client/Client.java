package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Client {
	
	private final String serverHost;
	private final int serverPort, messageRate;
	private final SocketAddress socketAddress;
	private 	  SocketChannel socketChannel;
	private 	  Selector clientSelector;
	
	public Client(String server, int port, int rate) throws IOException {
		serverHost = server;
		serverPort = port;
		messageRate = rate;
		socketAddress = new InetSocketAddress(InetAddress.getByName(serverHost), serverPort);
		clientSelector = Selector.open();
	}
	
	public void startClient() throws IOException {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(socketAddress);
		
		while (true) {
			
		}
	}
	
	
	
	public static void main(String args[]) throws NumberFormatException, IOException {
		if (args.length != 3) {
			System.out.println("See usage: java bin/ cs455.scaling.client.Client <server-host> <server-port> <message-rage>");
			System.exit(-1);
		}
		
		Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		client.startClient();
	}
	
}
