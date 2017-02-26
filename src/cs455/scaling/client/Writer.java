package cs455.scaling.client;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class Writer implements Runnable {

	private int messageRate;
	private SocketChannel channel;
	private String message = "START";
	private Random randomGen; 
	private Selector clientSelector;
	private int total;
	
	public Writer(int messageRate, SocketChannel clientChannel, Selector selector) {
		this.messageRate = messageRate;
		channel = clientChannel;
		randomGen = new Random();
		clientSelector = selector;
	}
	
	@Override
	public void run() {
		System.out.println("Starting message sending at rate: " + messageRate + " messages / sec");
		while (true) {
			try {
				Thread.sleep(100);
				int next = randomGen.nextInt(1000);
				total += next;
				setMessage("Hello" + "<===>" +  next);
				channel.keyFor(clientSelector).interestOps(SelectionKey.OP_WRITE);
				clientSelector.wakeup();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				System.out.println("Total - " + total);
			}
			
			
		}
	}
	
	public synchronized void setMessage(String msg) {
		message = msg;
	}

	public synchronized String getMessage() {
		return message;
	}

}
