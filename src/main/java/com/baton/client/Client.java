package com.baton.client;


import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import com.baton.utils.ServerClientConfig;

public class Client  implements Runnable{
	private String clientName  ;
	private String hostName ;
	private int portNumber  ;
	private boolean logClientMsg ;
	private long defaultSleepPeriod ;
	private volatile boolean isStopped = false ;
	public Client(String clientName) {
		this.clientName = clientName ;
		hostName = ServerClientConfig.getConfiguration().getString("com.baton.serverName", null) ;
		if(StringUtils.isBlank(hostName))
			hostName = "localhost";
		portNumber =  ServerClientConfig.getConfiguration().getInt("com.baton.serverName", 9000) ;
		logClientMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.client.logMsg", false) ;
		defaultSleepPeriod = ServerClientConfig.getConfiguration().getLong("com.baton.client.sleepPeriod", 1000L) ;
		System.out.println("logClientMsg " + logClientMsg);
	}
	public void setPortNumber(int portNum) {
		this.portNumber = portNum ;
	}
	public int getPortNumber() {
		return portNumber ;
	}
	public void run() {
		Socket clientSocket =  null ;
		String serverResponse;
		InputStream input  = null ;
		OutputStream output = null ;
		try  {
			if(logClientMsg)
				System.out.println(" Adding client " + System.currentTimeMillis()) ;
			clientSocket = new Socket(hostName, portNumber);
			input  = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(input)) ;
			
			while (isStopped() == false) {
				output.write((clientName + "\n").getBytes());
				output.flush();
				serverResponse = inputReader.readLine()  ;

				if(serverResponse != null) {
					if("DUPLICATE".equals(serverResponse)) {
						System.out.println(" Duplicate Client ..:" +   clientName + "  Please start with Unique Client Name") ;
						break ;
					}
					if(logClientMsg)
						System.out.println( serverResponse);
				}
				//System.out.println( serverResponse);
				Thread.sleep(defaultSleepPeriod);
				if(logClientMsg)
					System.out.println(" Message from Client " + clientName);
			}
		} catch (UnknownHostException e) {
			System.out.println("Don't know about host " + hostName + " portNum " + portNumber);
			this.isStopped = true ;

		} catch (IOException e) {
			System.out.println("Couldn't get I/O for the connection to " +
					hostName + " portNum " + portNumber);
			this.isStopped = true ;
		} catch (InterruptedException e) {
			System.out.println("InterruptedException " + hostName + " portNum " + portNumber);
			this.isStopped = true ;
		} finally {
			if(clientSocket != null || input != null || output != null)
				try {
					if(input != null)
						input.close() ;
					if(output != null)
						output.close() ;
					if(clientSocket != null)
						clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	public boolean isStopped() {
		return this.isStopped ;
	}
	public void stop() {
		this.isStopped = true ;
	}
	public static void main(String[] args)  {
		if(args.length < 1) {
			System.out.println("No Client Name ");
			throw new RuntimeException("No client name") ;
		}
		String clientName = args[0] ;
		Client client = new Client(clientName);
		Thread thread  = new Thread(client) ;
		thread.start(); 
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}