package cs455.scaling.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Task {
	
	private SelectionKey key;
	private byte[] bytes;
	
	private ByteBuffer buffer = ByteBuffer.allocate(100);
	
	public Task(SelectionKey key) {
		this.key = key;
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
		
	}
	
	private String toString(ByteBuffer bb) {
		final byte[] bytes = new byte[bb.remaining()];
		
		bb.duplicate().get(bytes);
		
		return new String(bytes);
	}
	
	public byte[] getBytes() { return bytes; }
}
