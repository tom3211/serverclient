package com.baton.scheduler;

import java.util.List;
import java.util.PriorityQueue;

import com.baton.ClientData;
import com.baton.utils.ServerClientConfig;

public class JobExecutor  implements Runnable {
	private QueueData queueData = null ;
	private int maxRuns  ;
	private boolean logMsg = false ;
	private long sleepPeriod = 1000L ;
	private int processingId = 0 ;
	JobExecutor(QueueData queueData, int maxRuns) {
		this.queueData = queueData ;
		this.maxRuns = maxRuns ;
		logMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
		sleepPeriod =  ServerClientConfig.getConfiguration().getLong("com.baton.server.jobExectorSleepPeriod", sleepPeriod);
	}


	@Override
	public void run() {
		ClientData clientData = null ;
		while(true) {
			synchronized(queueData) {
				clientData = queueData.getClientDataToProcess() ;
				if(clientData == null)
					try {
						queueData.wait() ;
					} catch (InterruptedException e) {

					}
				if(logMsg)
					System.out.println(" Size " + queueData.getQueueSize());

			}
			if(clientData == null)
				continue ; 
			try {	
				for(int i = 0 ; i < maxRuns ; i++) {
					System.out.println(clientData.getClientName() + ", Counter value: " + clientData.getRunCount());
					clientData.incrementRunCount(); 
					Thread.sleep(1000L);
				}				
			} catch (InterruptedException e) {
				if(logMsg)
					System.out.println(" Received interrupted exception clientName " +
							(clientData != null ? clientData.getClientName() : " null ") + System.currentTimeMillis());
			}
		}
	}



}
