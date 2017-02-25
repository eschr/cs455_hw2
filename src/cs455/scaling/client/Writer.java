package cs455.scaling.client;

public class Writer implements Runnable {

	private int messageRate;
	
	public Writer(int messageRate) {
		this.messageRate = messageRate;
	}
	
	@Override
	public void run() {
		System.out.println("Starting message sending at rate: " + messageRate + " messages / sec");
		
	}

}
