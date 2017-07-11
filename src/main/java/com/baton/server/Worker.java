package com.baton.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.baton.ClientData;
import com.baton.scheduler.FairScheduleManager;
import com.baton.utils.ServerClientConfig;

/**
 * Responsible for 
 * a) registering client with Scheduler 
 * b) Unregister when the client is stopped
 * Checks:
 * Before adding the client check for duplicate client names
 * 
 * Logic (following logic is executed periodically depending on the sleep period)
 * 	Read line from input stream
 * 		if the client is not registered and line is not null
 * 			check for duplicate client
 * 				if yes send the error msg and exit 
 *          register the client
 *       if the line is not null and client  registered
 *       	write to client // to keep the connection open
 *       sleep for N milliseconds
 * @author vajrala
 *
 */
public class Worker implements Runnable{

	private Socket clientSocket = null;
	private String clientName   = null;
	private boolean logMsg = false ;
	private boolean clientAdded = false;
	private long defaultSleepPeriod = 1000L ;
	private InputStream input  =  null ;
	private OutputStream output  = null ;
	public Worker(Socket clientSocket) {
		this.clientSocket = clientSocket;
		logMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logWorkerMsg", false) ;
		defaultSleepPeriod = ServerClientConfig.getConfiguration().getLong("com.baton.server.workerSleepPeriod", defaultSleepPeriod) ;
	}

	public void run() {
		BufferedReader inputReader = null ;
		long sleepTime = 10L ;
		try {
			input  = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
			inputReader = new BufferedReader(new InputStreamReader(input)) ;

			while(true) {
				String line = inputReader.readLine() ;
				if(line != null && clientAdded == false) {
					clientName = line ;
					sleepTime = this.defaultSleepPeriod ;
					if(registerWithScheduler(clientName)  == false ) {
						if(logMsg)
							System.out.println(" Unable to register client close ") ;
						break ;
					}
					clientAdded = true ;

				} else if (clientAdded == false && line == null) {
					if(logMsg)
						System.out.println(" Still not received clientName ") ;
				} else {
					output.write((clientName + "\n").getBytes());
					output.flush();
				}

				Thread.sleep(sleepTime);
				if(logMsg)
					System.out.println(" ClientData " +inputReader.readLine()) ;
			}
		} catch (IOException e) {
			//report exception somewhere.
			if(logMsg) {
				e.printStackTrace();
				System.out.println(" IOException  " + clientName);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			if(logMsg) {
				e.printStackTrace();
				System.out.println(" InterruptedException  " + clientName);
			}
		} finally {
			if(clientSocket != null)
				try {
					if(input !=null) {
						input.close();
					}
					if(output !=null) {
						output.close();
					}
					clientSocket.close();
				} catch (IOException e) {
					if(logMsg) {
						System.out.println(" Exception in closing the clientSocket  " + clientName);
						e.printStackTrace();

					}
				} 
			if(clientAdded)
				FairScheduleManager.getInstance().removeClient(clientName);
		}

	}

	private boolean registerWithScheduler(String clientName) throws IOException {
		boolean success = false ;
		if(isClientAlreadyRegistred(clientName)) {
			output.write(("DUPLICATE" + "\n").getBytes());
			output.flush();
		} else {
			if(logMsg)
				System.out.println(" registerWithScheduler " + clientName);
			FairScheduleManager.getInstance().addClient(clientName);
			success = true ;
		}
		return success ;
	}

	private boolean isClientAlreadyRegistred(String clientName) {
		return FairScheduleManager.getInstance().isAlreadyRegisterd(clientName) ;
	}
}