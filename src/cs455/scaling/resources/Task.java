package cs455.scaling.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cs455.scaling.server.WorkerThread;

public class Task {
	
	private SelectionKey key;
	private byte[] bytes;
	private String test;
	private static final  int READ = 1;
	private static final int HASH = 2;
	private static final int WRITE = 3;
	private volatile static boolean taskComplete = false;
	private int val;
	
	private final int taskType;
	
	private ByteBuffer buffer = ByteBuffer.allocate(100);
	
	public Task(SelectionKey key, int type) {
		this.key = key;
		taskType = type;
		val = 0;
	}
	
	public SelectionKey getKey() { return key; }
	public int getType() { return taskType; }
	
	public Task(String something, int type) {
		test = something;
		bytes = test.getBytes();
		taskType = type;
	}
	
	public int getVal() { return val; }
	
	public boolean getTaskComplete() { return taskComplete; }
	
	/*public String processRead(SelectionKey key) throws Exception {
		SocketChannel sChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int bytesCount = sChannel.read(buffer);
		if (bytesCount > 0) {
			buffer.flip();
			String message = new String(buffer.array());
			String[] split = message.split("//s+");
			System.out.println("INSIDE PROCESS:");
			System.out.println(message + "aaaa");
			return new String(buffer.array());
		}
		return "NoMessage";
	}
	
	public void readFromChannel() throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		
		buffer.clear();
		
		int read = 0;
		try {
			read = channel.read(buffer);
		}
		catch (IOException e) {
			System.out.println(e.getMessage() +  " in Task readFromChannel()");
			key.cancel();
			channel.close();
			return;
		}
		
		if (read == -1) {
			key.channel().close();
			key.cancel();
		}
		
		System.out.println(toString(buffer));
		
	}*/
	
	private String toString(ByteBuffer bb) {
		final byte[] bytes = new byte[bb.remaining()];
		
		bb.duplicate().get(bytes);
		
		return new String(bytes);
	}
	
	public byte[] getBytes() { return bytes; }

	public int readAndSendBackEcho() throws IOException {
		SocketChannel sChannel = (SocketChannel) key.channel();
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	    int bytesCount = sChannel.read(buffer);
	    if (bytesCount == -1) {
	    	System.out.println("NEGATIVE ONE!!");
	    	key.cancel();
	    	sChannel.close();
	    	return 0;
	    }
	    if (bytesCount > 0) {
	      buffer.flip();
	      String message = new String(buffer.array());
	      System.out.println(message);
	      String[] split = message.split("<===>");
	      if (split.length == 2) 
	    	  return Integer.parseInt(split[1].trim());
	    }
	    return 0;
	}
}
