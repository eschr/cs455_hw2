/*
 * Author: Eric Schraeder 
 * March 2017
 * CSU CS 455 HW2-PC
 * 
 * ServerChannel Thread runs a Java NIO ServerSocketChannel.
 * 
 * Switches on keys after Selector.select()
 * 		-accepts incoming connections from clients
 * 		-hands off keys for channels that are readable to the ThreadPoolManager
 * 
 * 
 */

package cs455.scaling.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class ServerChannel implements Runnable {
	
	private int portNum;
	private ServerSocketChannel serverSocketChannel;
	private Selector serverSelector;
	private Server mainServer;
	private int connectionCount;
	
	public ServerChannel(int port, Server server) throws IOException {
		mainServer = server;
		portNum = port;
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(portNum));
		serverSocketChannel.configureBlocking(false);
		
		serverSelector = Selector.open();
		
		serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
		
	}
	
	public int getClientCount() { return connectionCount; }
	

	@Override
	public void run() {
		while (true) {
			try {
				serverSelector.select();
			} catch (IOException e1) {
				System.out.println(e1.getMessage() + " in ServerChannel run()");
			}
			Iterator<SelectionKey> keySet = serverSelector.selectedKeys().iterator();
			while (keySet.hasNext()) {
				SelectionKey key = (SelectionKey) keySet.next();
				keySet.remove();
				
				if (! key.isValid()) continue;
				
				if (key.isAcceptable()) {
					try {
						acceptIncomingConnection(key);
					} catch (IOException e) {
						System.out.println(e.getMessage() + " in ServerChannel run()");
					}
				} else if (key.isReadable()) {
					// System.out.println("is readable");
					key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
					mainServer.acceptRead(key);
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
			System.out.println("Accepting incoming connection...");
			connectionCount++;
			
			if (socketChannel != null) {
				socketChannel.configureBlocking(false);
				socketChannel.register(serverSelector, SelectionKey.OP_READ);
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage() + " in ServerChannel acceptIncomingConnection()");
		}
	}
	
	
}
