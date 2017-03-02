package cs455.scaling.server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerStats implements Runnable {
	
	private Server server;
	
	public ServerStats(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			
			System.out.println("[" + timeStamp + "] " + "Current server throughput: " + server.getReadWriteStats() + ", Active client connections: " + server.getActiveConnections());
		}

	}

}
