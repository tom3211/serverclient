package com.baton;

import com.baton.utils.ClientDataGenerator;

public class ClientData implements Comparable<ClientData>{
	private Integer clientWeight ;
	private String clientName ;
	private int runCount ;

	
	public ClientData(String name) {
		clientName = name ;
		
	}
	
	public ClientData clone() {
		ClientData clone = new ClientData(this.clientName);
		clone.setRunCount(this.runCount);
	//	clone.clientWeight = this.clientWeight ;
		return clone ;
	}
	public void resetClientWeight() {
		clientWeight = ClientDataGenerator.getInstance().getNextWeight() ;
	}
	
	public void incrementRunCount() {
		runCount++ ;
	}
	
	
	public Integer getClientWeight() {
		return clientWeight;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public int hashCode() {
		return clientName.hashCode() ;
	}
	public boolean equals(Object o) {
		if (o == null)
			throw new RuntimeException("Object is null" );
		if(!( o instanceof ClientData)) {
			throw new RuntimeException("Invalid object type " + o.getClass().getName());
		}
		ClientData obj2 = (ClientData) o ;
		return this.clientName.equals(obj2.clientName);
	}
	@Override
	public int compareTo(ClientData o) {
		if (o == null)
			throw new RuntimeException("Object is null" );
		ClientData obj2 = (ClientData) o ;
		return (int) (this.clientWeight - obj2.clientWeight);
	}
	
	public static void main(String[] argv) {
		ClientData cd1 = new ClientData("A");
		cd1.resetClientWeight();
		
		ClientData cd2 = new ClientData("A");
		cd2.resetClientWeight();
		
		System.out.println("Cd1 compare to " + cd1.compareTo(cd2));
		
	}

}
