package cs455.scaling.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class Client {
	
	private final String serverHost;
	private final int serverPort, messageRate;
	private final SocketAddress serverAddress;
	private 	  SocketChannel clientSocketChannel;
	private 	  Selector clientSelector;
	static BufferedReader userInputReader = null;
	private Writer writer;
	private static final int buffSize = 64;
	
	public Client(String server, int port, int rate) throws IOException {
		serverHost = server;
		serverPort = port;
		messageRate = rate;
		serverAddress = new InetSocketAddress(InetAddress.getByName(serverHost), serverPort);
		clientSelector = Selector.open();
	}
	
	public void startClient() throws Exception {
		clientSocketChannel = SocketChannel.open();
		clientSocketChannel.configureBlocking(false);
		clientSocketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
		clientSocketChannel.connect(serverAddress);
	
		writer = new Writer(messageRate, clientSocketChannel, clientSelector);
		new Thread(new ClientStats(writer)).start();
		new Thread(writer).start();

		while (true) {
			clientSelector.select();
			Iterator<SelectionKey> selectedKeys = clientSelector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				
				if (! key.isValid()) continue;
				
				if (key.isReadable()) {
					key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
					String msg = processRead(key);
					//System.out.println("Server says: " + msg);
					writer.removeStringFromMap(msg);
					key.interestOps(SelectionKey.OP_WRITE);
				}
				
				else if (key.isWritable()) {
			
				}
				
				else if (key.isConnectable()) {
					connect(key);
				}
				
			}
		}
	}
	
	public static String processRead(SelectionKey key) throws Exception {
		SocketChannel sChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		sChannel.read(buffer);
		buffer.flip();
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
		String msg = charBuffer.toString();
		//System.out.println(msg);
		return msg;
	}
	/*
	public String processRead(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel(); 
		ByteBuffer buffer = ByteBuffer.allocate(buffSize);
		int read = 0;
		try {
			while (buffer.hasRemaining() && read != -1) {
				read = channel.read(buffer); 
			}
		} catch (IOException e) {
			System.out.println(e.getMessage() + " Client processRead()");
			key.cancel();
			channel.close();
		}
		
		if (read == -1) {
			key.cancel();
			channel.close();
		}
		
		buffer.flip();
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
		String msg = charBuffer.toString();
		System.out.println(msg);
		return msg;
	
	}*/
	
	/*
	public static String processRead(SelectionKey key) throws Exception {
		SocketChannel sChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(22);
		sChannel.read(buffer);
		buffer.flip();
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
		String msg = charBuffer.toString();
		return msg;
	}*/
	
	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		System.out.println("Connectable...");
		channel.finishConnect();
		System.out.println("CONNECTED");
		channel.register(clientSelector, SelectionKey.OP_WRITE);
	}
	
	public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			System.out.println("See usage: java bin/ cs455.scaling.client.Client <server-host> <server-port> <message-rage>");
			System.exit(-1);
		}
		userInputReader = new BufferedReader(new InputStreamReader(System.in));
		Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		client.startClient();
	}
	
}
