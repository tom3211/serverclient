package com.baton.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.baton.client.Client;
import com.baton.scheduler.FairScheduleManager;
import com.baton.utils.TestUtils;

public class TestServer {
	private Server server =null ;
	private Thread serverThread = null ;
	private int portNumber = 9009 ;
	
	@BeforeSuite
	public void beforeTest() {
		server = new Server(portNumber);
		serverThread = new Thread(server);
		serverThread.start();
	}
	@AfterSuite
	public void afterTest() {
		try {
			server.stop(); 
		} catch(Exception e) {
			// to handle runtime exceptions
		}
	}
	
	@Test
	public void testServerStatus() {
		Assert.assertEquals(server.isStopped(), false);
	}
	// tests both registering unregistering the client
	@Test
	public void testRegisteringClient() {
		Client client = new Client("A");
		client.setPortNumber(portNumber);
		Thread clientThread = new Thread(client);
		clientThread.start();
		TestUtils.sleep(10);
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(),1) ;
		//now disconnect 
		client.stop(); 
		// wait for 15 seconds before proceeding
		TestUtils.sleep(15);
		
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(),0) ;
		
	}
	
	@Test
	public void testDuplicateClients() {
		Client client = new Client("A");
		client.setPortNumber(portNumber);
		Thread clientThread = new Thread(client);
		clientThread.start();
		TestUtils.sleep(10);
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(),1) ;
		//now disconnect 
		Client newClient = new Client("A");
		newClient.setPortNumber(portNumber);
		Thread clientThreadNew = new Thread(newClient);
		clientThreadNew.start();
		TestUtils.sleep(10);
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(),1) ;
		client.stop(); 
		// wait for 15 seconds before proceeding
		TestUtils.sleep(15);
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(),0) ;
		
		
	}
	
	

	
}
