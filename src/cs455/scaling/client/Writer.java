package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Writer implements Runnable {

	private int messageRate;
	private SocketChannel channel;
	private String message = "START";
	private Random randomGen; 
	private Selector clientSelector;
	private int total;
	//private final HashMap<String, Integer> messagesHashList;
	private final LinkedList<String> messagesHashList;
	private static final int BUFFER_SIZE = 8192;
	private int sentCount = 0;
	private int receivedCount = 0;
	
	public Writer(int messageRate, SocketChannel clientChannel, Selector selector) {
		this.messageRate = messageRate;
		channel = clientChannel;
		randomGen = new Random();
		clientSelector = selector;
		messagesHashList = new LinkedList<String>();
	}
	
	public int getSentCountAndReset() {
		int sent = sentCount;
		sentCount = 0;
		return sent;
	}
	
	public int getRecievedCountAndReset() {
		int received = receivedCount;
		receivedCount = 0;
		return received;
	}
	
	@Override
	public void run() {
		System.out.println("Starting message sending at rate: " + messageRate + " messages / sec");
		while (true) {
			try {
				//int next = randomGen.nextInt(1000);
				//total += next;
				//String msg = "Hello" + "<===>" +  next;
				//System.out.println("Sending: " + msg);
				byte[] nextMessage = new byte[BUFFER_SIZE];
				randomGen.nextBytes(nextMessage);
				try {
					addMessageToMap(nextMessage);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.sleep(1000 / messageRate);
				try {
					sentCount++;
					writeMessage(nextMessage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				//System.out.println("Total - " + total);
			}
			
			
		}
	}
	
	private void writeMessage(byte[] message) throws IOException {
	    ByteBuffer buffer = ByteBuffer.wrap(message);
	    channel.write(buffer);
	    channel.keyFor(clientSelector).interestOps(SelectionKey.OP_READ);
	}
	
	private void addMessageToMap(byte[] message) throws NoSuchAlgorithmException {
		String hash = SHA1FromBytes(message);
		//System.out.println("Sending: " + hash);
		synchronized (messagesHashList) {
			messagesHashList.add(hash);
			/*if (messagesHashList.containsKey(hash)) {
				messagesHashList.put(hash, messagesHashList.get(hash) + 1);
			} else {
				messagesHashList.put(hash, 1);
			}*/
		}
		
	}
	
	public void removeStringFromMap(String msg) throws NoSuchAlgorithmException {
		receivedCount++;
		removeHashString(msg.trim());
	}
	
	private void removeHashString(String hash) {
		boolean found = false;
		synchronized(messagesHashList) {
			for (String each : messagesHashList) {
				if (each.equals(hash)) found = true;
			}
			
			if (found) {
				messagesHashList.remove(hash);
				//System.out.println("Removed " + hash + " from list, size: " + messagesHashList.size());
			}
			else System.out.println("--------------HASH: " + hash + " not found in the map----------");
			/*if (! messagesHashList.containsKey(hash)) {
				System.out.println("--------------HASH: " + hash + " not found in the map----------");
				return;
			}
			if (messagesHashList.get(hash) == 1) {
				messagesHashList.remove(hash);
				System.out.println("HASH removed successfully!  *************");
			}
			else messagesHashList.put(hash, messagesHashList.get(hash) - 1);*/
		}
	}
	
	private String SHA1FromBytes(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash = digest.digest(bytes);
		 
		BigInteger hashBigInt = new BigInteger(1, hash);
		return hashBigInt.toString(16);
	}
	
	public synchronized void setMessage(String msg) {
		message = msg;
	}
	
	public synchronized void setByteMessage(byte[] message) {
		
	}

	public synchronized String getMessage() {
		return message;
	}

}
