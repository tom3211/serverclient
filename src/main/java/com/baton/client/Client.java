package com.baton.client;


import java.io.*;
import java.net.*;
import org.apache.commons.lang.StringUtils;
import com.baton.utils.ServerClientConfig;

public class Client  implements Runnable{
	private String clientName  ;
	private String hostName ;
	private int portNumber  ;
	private boolean logClientMsg ;
	private long defaultSleepPeriod ;
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
	public void run() {
		Socket clientSocket =  null ;
		String serverResponse;

		try  {
			if(logClientMsg)
				System.out.println(" Adding client " + System.currentTimeMillis()) ;
			clientSocket = new Socket(hostName, portNumber);
				InputStream input  = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(input)) ;

			while (true) {
				output.write((clientName + "\n").getBytes());
				output.flush();
				serverResponse = inputReader.readLine()  ;
					
				if(serverResponse != null) {
					if("DUPLICATE".equals(serverResponse)) {
						System.out.println(" Duplicate Client ..:" +   clientName + "  Please start with Unique Client Name") ;
						throw new RuntimeException("Duplicate Client");
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
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Couldn't get I/O for the connection to " +
					hostName + " portNum " + portNumber);
			throw new RuntimeException() ;
		} catch (InterruptedException e) {
			System.out.println("InterruptedException " + hostName + " portNum " + portNumber);
			throw new RuntimeException() ;
		} finally {
			if(clientSocket != null)
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	public static void main(String[] args)  {
		if(args.length < 1) {
			System.out.println("No Client Name ");
			throw new RuntimeException("No client name") ;
		}
		String clientName = args[0] ;
		Client client = new Client(clientName);
		Thread clientThread = new Thread(client);
		clientThread.start();
		try {
			clientThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}