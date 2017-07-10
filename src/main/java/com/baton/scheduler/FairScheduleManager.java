package com.baton.scheduler;


import java.util.PriorityQueue;

import com.baton.ClientData;
import com.baton.utils.ClientDataGenerator;
import com.baton.utils.ServerClientConfig;

/**
 * manages the client queue. Uses PriorityQueue to manage the queue. 
 * Client has weight associated with it and any new client added is ensured it is added at the end.
 * When a client gets chance to run, JobExecutor gets(removes) the client with the lowest weight and it is
 * added back after finishes processing it. 
 * If the client is removed while JobExecutor is processing it
 *  (this happens when the client is stopped. Worker thread receives the exception while trying to write to client and it 
 *   calls the removeMethod)
 *  a) Interrupt is sent to JobExectuor only if it is currently processing the same client
 *  
 *  
 * 
 *  It is a singleton and provides the methods to
 * a) addClient
 * 	Adds the client to the queue. It ensures that the weight assigned to the client is highest and it is added 
 *  to the end.
 * b) removeClient
 * 
 * @author vajrala
 *
 */
public final class  FairScheduleManager {
	private PriorityQueue<ClientData> clientQueue  ;
	private JobExecutor jobExecutor = null ;
	private Thread schedulerThread = null ;
	private static final FairScheduleManager instance = new FairScheduleManager() ;
	private boolean logQueueMsg = false ;
	
	private FairScheduleManager() {
		logQueueMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
		clientQueue = new PriorityQueue<ClientData>() ;
		jobExecutor = new JobExecutor(clientQueue, 4);
		schedulerThread = new Thread(jobExecutor);
		schedulerThread.start(); 
	}

	public static FairScheduleManager getInstance() {
		return instance ;
	}
	public boolean isAlreadyRegisterd(ClientData data) {
		synchronized(clientQueue) {
			return clientQueue.contains(data);
		}
		
	}
	public  void addClient(ClientData data) {
		if(logQueueMsg)
			System.out.println(" request to add client " + data.getClientName() + " " + System.currentTimeMillis());
		synchronized(clientQueue) {
			data.resetClientWeight();
			if(logQueueMsg)
				System.out.println(" Done Adding client " + data.getClientName() + " wt " + data.getClientWeight()) ;
			clientQueue.add(data);
			clientQueue.notify() ;
		}
	}
	public  void removeClient(String clientName) {
		if(logQueueMsg)
			System.out.println("Received removeClient name " + clientName + 
					" size:" + clientQueue.size() + " " + System.currentTimeMillis());
	
		synchronized(clientQueue) {
			ClientData activeClient = jobExecutor.getActiveClient() ;
			if(activeClient.getClientName().equals(clientName)) {
				if(logQueueMsg)
					System.out.println(" Size " + clientQueue.size() + " " + System.currentTimeMillis());
			
				schedulerThread.interrupt();
			} 
			// may not be needed to remove if it is the one i.e. getting processed.
			ClientData cd = new ClientData(clientName);
			clientQueue.remove(cd);
		
		}

	}
	
	public static void main(String[] argv) {
		FairScheduleManager fs = new FairScheduleManager() ;
		for(int i = 0 ; i < 4 ; i++) {
			ClientData cd = new ClientData(ClientDataGenerator.getInstance().generateClintName()) ;
			fs.addClient(cd);
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	//	ClientData activeClient = fs.scheduler.getActiveClient() ;
		//ffs.removeClient(activeClient.getClientName());
		while(true) {
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
