package cs455.scaling.client;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientStats implements Runnable {
	
	private Writer writer;

	
	public ClientStats(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			System.out.println("[" + timeStamp + "] " + "Total sent count: " + writer.getSentCountAndReset() + ", Total received count: " + 
					writer.getRecievedCountAndReset());

		}
		
	}

}
