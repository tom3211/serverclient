package com.baton.queue;


import java.util.ArrayDeque;
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
	private ArrayDeque<ClientData> clientQueue  ;
	private JobExecutor jobExecutor = null ;
	private Thread schedulerThread = null ;
	private static final FairScheduleManager instance = new FairScheduleManager() ;
	private boolean logQueueMsg = false ;
	
	private FairScheduleManager() {
		logQueueMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
		clientQueue = new ArrayDeque<ClientData>() ;
		jobExecutor = new JobExecutor(clientQueue, 4);
		schedulerThread = new Thread(jobExecutor);
		// make the thread daemon 
		schedulerThread.setDaemon(true);
		schedulerThread.start(); 
	}

	public static FairScheduleManager getInstance() {
		return instance ;
	}
	public boolean isClientJobRunning(String clientName) {
		ClientData activeClient = jobExecutor.getActiveClient() ;
		if(activeClient != null && activeClient.getClientName().equals(clientName)) {
			return true ;
		}
		return false ;
	}
	// this method should not called frequently. This is just added for
	// unit tests.
	public ClientData getClientData(String clientName) {
		ClientData temp = new ClientData(clientName);
		ClientData cloneData = null ;
		ClientData clientData = null ;
		synchronized(clientQueue) {
			if(jobExecutor.getActiveClient() != null &&  jobExecutor.getActiveClient().equals(temp)) {
				clientData = jobExecutor.getActiveClient() ;
			} else {
				for(ClientData cd :clientQueue) {
					if(cd.equals(temp)) {
						if(this.logQueueMsg)
							System.out.println(" found " + clientName);
						clientData = cd ;
						break ;
					}
				}
			}
			if(clientData != null) {
				cloneData = clientData.clone() ;
			}
		}
		return cloneData ;
	}
	public int getSize() {
		int count = 0 ;
		synchronized(clientQueue) {
			count = clientQueue.size()  ;
			if(jobExecutor.getActiveClient()  != null)
				count += 1 ;
		}
		return count;
	}
	public boolean isAlreadyRegisterd(String clientName) {
		ClientData clientData = new ClientData(clientName);
		synchronized(clientQueue) {
			ClientData activeClient = jobExecutor.getActiveClient() ;
			if(activeClient != null && activeClient.equals(clientData) ||
					clientQueue.contains(clientData)) {
				return true ;
			}
		}
		return false ;
		
	}
	public  boolean addClient(String clientName) {
		return addClient(new ClientData(clientName));
	}
		
	public  boolean addClient(ClientData clientData) {
		if(logQueueMsg)
			System.out.println(" request to add client " +  clientData.getClientName() + " " + System.currentTimeMillis());
		boolean added = false ;
		synchronized(clientQueue) {
			ClientData activeClient = jobExecutor.getActiveClient() ;
			if(activeClient != null && activeClient.equals(clientData) ||
					clientQueue.contains(clientData)) {
				System.out.println("Not adding client as it is already exists " + clientData.getClientName());
				return added;
			}	
			if(logQueueMsg)
				System.out.println(" Done Adding client " + clientData.getClientName()) ;
			clientQueue.add(clientData);
			clientQueue.notify() ;
			added = true ;	
		}
		return added ;
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
				return ;
			} 
			// may not be needed to remove if it is the one i.e. getting processed.
			ClientData cd = new ClientData(clientName);
			clientQueue.remove(cd);
		
		}

	}
	
	public static void main(String[] argv) {
		FairScheduleManager fs = new FairScheduleManager() ;
		for(int i = 0 ; i < 4 ; i++) {
			fs.addClient(new ClientData(ClientDataGenerator.getInstance().generateClintName()));
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
