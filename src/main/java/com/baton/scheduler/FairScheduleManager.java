package com.baton.scheduler;


import com.baton.ClientData;
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
	private JobExecutor jobExecutor = null ;
	private Thread schedulerThread = null ;
	private static final FairScheduleManager instance = new FairScheduleManager() ;
	private boolean logQueueMsg = false ;
	private QueueData queueData = null ;
	private FairScheduleManager() {
		logQueueMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
		queueData = new QueueData() ; 
		jobExecutor = new JobExecutor(queueData, 4);
		schedulerThread = new Thread(jobExecutor);
		
		// make the thread daemon 
		schedulerThread.setDaemon(true);
		schedulerThread.start(); 
		
	}

	public static FairScheduleManager getInstance() {
		return instance ;
	}
	public boolean isClientJobRunning(String clientName) {
		return queueData.isNodeCurrentlyProcessed(clientName);
	}
	// this method should not called frequently. This is just added for
	// unit tests.
	public ClientData getClientData(String clientName) {
		ClientData cloneData = null ;
		ClientData queueClient = null ;
		synchronized(queueData) {
			queueClient = queueData.getClientData(clientName);
			if(queueClient != null) {
				cloneData = queueClient.clone() ;
			}
		}
		return cloneData ;
	}
	public int getSize() {
		return queueData.getQueueSize() ;
	}
	public boolean isAlreadyRegisterd(String clientName) {
		synchronized(queueData) {
			return queueData.isClientAlreadyExists(clientName) ;
		}
	}
	public  boolean addClient(String clientName) {
		if(logQueueMsg)
			System.out.println(" request to add client " + clientName + " " + System.currentTimeMillis());
		boolean added = false ;
		synchronized(queueData) {
			if(queueData.isClientAlreadyExists(clientName)  == false) {
				queueData.addClient(clientName);
				added = true ;	
				queueData.notify(); 
			}
		}
		return added ;
	}
	public  boolean removeClient(String clientName) {
		if(logQueueMsg)
			System.out.println("Received removeClient name " + clientName + 
					" size:" + queueData.getQueueSize() + " " + System.currentTimeMillis());
		boolean removedFlag = false ;
		boolean sendInterrupt = false ;
		synchronized(queueData) {
			sendInterrupt = queueData.isNodeCurrentlyProcessed(clientName);
			removedFlag = queueData.removeClient(clientName) ;
			if(sendInterrupt)
				schedulerThread.interrupt(); 
			
		}
		return removedFlag ;
	}
	
	public static void main(String[] argv) {
		FairScheduleManager.getInstance().addClient("A");
		FairScheduleManager.getInstance().addClient("B");
		FairScheduleManager.getInstance().addClient("C");
		while(true) {
			System.out.println(" size " + FairScheduleManager.getInstance().getSize() );
			try {
				Thread.currentThread().sleep(10000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
