package cs455.scaling.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class ServerChannel implements Runnable {
	
	private int portNum;
	private ServerSocketChannel serverSocketChannel;
	private Selector serverSelector;
	
	public ServerChannel(int port) throws IOException {
		portNum = port;
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(portNum));
		serverSocketChannel.configureBlocking(false);
		serverSelector = Selector.open();
		
		int interestedIn = SelectionKey.OP_ACCEPT; // | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
		
		serverSocketChannel.register(serverSelector, interestedIn);
		
	}
	

	@Override
	public void run() {
		while (true) {
			try {
				serverSelector.select();
			} catch (IOException e) {
				System.out.println(e.getMessage() + " in ServerChannel run()");
			}
			Iterator<SelectionKey> keySet = serverSelector.selectedKeys().iterator();
			while (keySet.hasNext()) {
				SelectionKey key = keySet.next();
				if (key.isAcceptable()) {
					try {
						acceptIncomingConnection(key);
					} catch (IOException e) {
						System.out.println(e.getMessage() + " in ServerChannel run()");
					}
				}
				else if (key.isReadable()) {
					
				}
				else if (key.isWritable()) {
					
				}
			}
		}
		
	}
	
	
	private void acceptIncomingConnection(SelectionKey key) throws IOException {
		ServerSocketChannel serverSoc = (ServerSocketChannel) key.channel();
		try {
			SocketChannel socketChannel = serverSoc.accept();
			System.out.println("Accpting incoming connection...");
			socketChannel.configureBlocking(false);
			socketChannel.register(serverSelector, SelectionKey.OP_READ);
		}
		catch (IOException e) {
			System.out.println(e.getMessage() + " in ServerChannel acceptIncomingConnection()");
		}
	}
	
	
}
