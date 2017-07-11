package com.baton;

public class ClientData implements Comparable<ClientData>{
	private String clientName ;
	private int runCount =1 ;

	
	public ClientData(String name) {
		clientName = name ;
		
	}
	
	public ClientData clone() {
		ClientData clone = new ClientData(this.clientName);
		clone.setRunCount(this.runCount);
		return clone ;
	}
	
	
	public void incrementRunCount() {
		runCount++ ;
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
		return this.clientName.compareTo(obj2.clientName) ;
	}


}
