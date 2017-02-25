package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
	
	private final String serverHost;
	private final int serverPort, messageRate;
	private final SocketAddress serverAddress;
	private 	  SocketChannel clientSocketChannel;
	private 	  Selector clientSelector;
	
	public Client(String server, int port, int rate) throws IOException {
		serverHost = server;
		serverPort = port;
		messageRate = rate;
		serverAddress = new InetSocketAddress(InetAddress.getByName(serverHost), serverPort);
		clientSelector = Selector.open();
	}
	
	public void startClient() throws IOException {
		clientSocketChannel = SocketChannel.open();
		clientSocketChannel.configureBlocking(false);
		clientSocketChannel.register(clientSelector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		try {
			clientSocketChannel.connect(serverAddress);
			clientSocketChannel.finishConnect();
		}
		catch (Exception e) { 
			System.out.println(e.getMessage() + "startClient"); 
			System.out.println("TEST TEXT");
		}

		while (true) {
			clientSelector.select();
			Iterator<SelectionKey> selectedKeys = clientSelector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				
				if (! key.isValid()) continue;
				
				if (key.isReadable()) {
	
				}
				
				else if (key.isWritable()) {
					System.out.println("client writable");
				}
				
				else if (key.isConnectable()) {
					connect(key);
				}
				
			}
		}
	}
	
	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		System.out.println("Connectable...");
		channel.finishConnect();
		System.out.println("CONNECTED");
		channel.register(clientSelector, SelectionKey.OP_READ);
		//write("Hello world", channel);
	}
	
	private void write(String message, SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(25);
		buffer.clear();
		buffer.put(message.getBytes());
		buffer.flip();
		//channel.write(buffer);
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
