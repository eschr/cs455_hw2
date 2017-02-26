package cs455.scaling.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

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
	private String message, hashValue;
	
	private final int buffSize = 1024;
	
	private int taskType;
	
	private ByteBuffer buffer = ByteBuffer.allocate(100);
	
	public Task(SelectionKey key, int type) {
		this.key = key;
		taskType = type;
		val = 0;
		bytes = "000".getBytes();
	}
	
	public SelectionKey getKey() { return key; }
	public int getType() { return taskType; }
	
	public void setHash(String hash) { hashValue = hash; }
	public void setType(int type) { taskType = type; }
	
	public Task(String something, int type) {
		test = something;
		bytes = test.getBytes();
		taskType = type;
	}
	
	public int getVal() { return val; }
	
	public boolean getTaskComplete() { return taskComplete; }
	
	
	private String toString(ByteBuffer bb) {
		final byte[] bytes = new byte[bb.remaining()];
		
		bb.duplicate().get(bytes);
		
		return new String(bytes);
	}
	
	public byte[] getBytes() { return bytes; }

	public void readFromBuffer() throws IOException {
		SocketChannel sChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int bytesCount = sChannel.read(buffer);
		if (bytesCount == -1) {
			System.out.println("NEGATIVE ONE!!");
			key.cancel();
			sChannel.close();
			return;
		}
		if (bytesCount > 0) {
			buffer.flip();
			String message = new String(buffer.array());
			this.message = message;
			this.bytes = this.message.getBytes();
			System.out.println(this.message);
		}

		/*SocketChannel channel = (SocketChannel) key.channel(); 
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
			System.out.println("Negative one exception");
			key.cancel();
			channel.close();
		}
		
		buffer.flip();
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
		String msg = charBuffer.toString();
		System.out.println(msg);
		this.message = msg;
		
		if (key.isValid())
			key.interestOps(SelectionKey.OP_WRITE);*/
	}
	
	public void writeHashBackToClient() throws IOException {
		if (key.isValid()) {
			SocketChannel sChannel = (SocketChannel) key.channel();
			ByteBuffer buffer = ByteBuffer.wrap(hashValue.getBytes());
			sChannel.write(buffer);
		}
	}

}











