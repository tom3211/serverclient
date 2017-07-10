package com.baton.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.baton.utils.ServerClientConfig;

/**
 * Simple server, receives a requests and starts a thread to serve the request.
 * How to Run
 * Server [portnumber] 
 * @author vajrala
 *
 */
public class Server implements Runnable{

	private int	serverPort   = 9000;
    private ServerSocket serverSocket = null;
     private boolean logMsg = false ;
    
    public Server(int port){
        this.serverPort = port;
        logMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;    
    }

    public void run(){
       
        openServerSocket();
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                if(logMsg) {
                	System.out.println(" Got Connection request " + System.currentTimeMillis());
                }
            } catch (IOException e) { 
               throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread(new Worker (clientSocket)).start();
        }
      
    }


    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }

    public static void main(String[] argv) {
    	int port = 9000 ;
    	if(argv.length == 1)
    		port = Integer.parseInt(argv[0]);
    	Server server = new Server(port) ;
    	Thread serverThread = new Thread(server) ;
    	serverThread.start();
    	try {
			serverThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	
    }
 }
