package com.baton.scheduler;

import java.util.PriorityQueue;

import com.baton.ClientData;
import com.baton.utils.ServerClientConfig;

public class JobExecutor  implements Runnable {
	private PriorityQueue<ClientData> clientQueue = null ;
	private int maxRuns  ;
	private volatile ClientData activeClient = null ;
	private boolean logMsg = false ;
	
	JobExecutor(PriorityQueue<ClientData> queue, int maxRuns) {
		clientQueue = queue ;
		this.maxRuns = maxRuns ;
		logMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
	}
	
	public  ClientData getActiveClient() {
		return activeClient ;
	}
	
	@Override
	public void run() {
		
		while(true) {
			synchronized(clientQueue) {
				if(clientQueue.isEmpty())
					try {
						clientQueue.wait() ;
					} catch (InterruptedException e) {
						
					}
				if(logMsg)
					System.out.println(" Size " + clientQueue.size());
				activeClient = clientQueue.poll() ;
				
			}
			if(activeClient == null)
				continue ; // to handle any unintended interruptions
			try {	
				for(int i = 0 ; i < maxRuns ; i++) {
					System.out.println(activeClient.getClientName() + ", Counter value: " + activeClient.getRunCount());
					activeClient.incrementRunCount(); 
					Thread.sleep(5000L);
				}
				FairScheduleManager.getInstance().addClient(activeClient);
				
			} catch (InterruptedException e) {
				if(logMsg)
					System.out.println(" Received interrupted exception  " +
							clientQueue.size()+ " " + System.currentTimeMillis());
			}
			activeClient = null ;

		}
	}

}
