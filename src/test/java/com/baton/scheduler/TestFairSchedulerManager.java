package com.baton.scheduler;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.baton.ClientData;

public class TestFairSchedulerManager {
	private String[] clientNames = {"A", "B", "C" } ;
	private long intervalBetweenEachClient = -1L ;
	@BeforeMethod
	public void beforeEachMethod() {
		// add clients for each
		for(int i = 0 ; i < clientNames.length ; i++) {
			ClientData clientData = new ClientData(clientNames[i]);
			FairScheduleManager.getInstance().addClient(clientData);
			if(intervalBetweenEachClient > 0L) {
				try {
					Thread.sleep(intervalBetweenEachClient);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@AfterMethod
	public void afterEachMethod() {
		for(int i = 0 ; i < clientNames.length ; i++) {
			FairScheduleManager.getInstance().removeClient(clientNames[i]);
		}
		// sleep for 4 seconds ;
		try {
			Thread.sleep(4000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// since client job by default sleeps for 1 second, we should be able to pass this test
	// test the first client
	@Test
	public void testRunningClient() {
		// first test the A and then B
		ClientData clientData = new ClientData(clientNames[0]);
		boolean isRunning = FairScheduleManager.getInstance().isClientJobRunning(clientData);
		Assert.assertEquals(isRunning, true);
	}
	// try adding duplicate clients
	@Test
	public void testDuplicateClients() {
		// first test the A and then B
		ClientData clientData = new ClientData(clientNames[0]);
		boolean addedFlag = FairScheduleManager.getInstance().addClient(clientData);
		Assert.assertEquals(addedFlag, false);
		// size should be 2
		Assert.assertEquals(FairScheduleManager.getInstance().getSize(), 2);
		
		// ensure that b/c are not running
		clientData = new ClientData(clientNames[2]);
		addedFlag = FairScheduleManager.getInstance().addClient(clientData) ;
		Assert.assertEquals(addedFlag, false);
	}
	
	@Test
	public void testRunCount() {
		// first test the A and then B
		ClientData clientData = new ClientData(clientNames[0]);
		clientData = FairScheduleManager.getInstance().getClientData(clientData);
		Assert.assertNotNull(clientData);
		try {
			Thread.sleep(6000L );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ClientData clientNewData = FairScheduleManager.getInstance().getClientData(clientData);
		Assert.assertNotNull(clientNewData);
		boolean runCountGreater = clientNewData.getRunCount() > clientData.getRunCount() ? true : false ;
		Assert.assertEquals(runCountGreater, true);
	}
	
}
