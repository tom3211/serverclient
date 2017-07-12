package com.baton.linkedlist;

import java.util.HashMap;
import java.util.Map;

import com.baton.ClientData;
import com.baton.utils.ServerClientConfig;

public class QueueData {
	private QueueNode rootNode = null ;
	private QueueNode currentProcessingNode = null ;
	private QueueNode nextProcessingNode = null ;
	private Map<String,QueueNode> queueMap = new HashMap<String,QueueNode>() ;
	private boolean logMsg = false ;
	public QueueData() {
		logMsg = ServerClientConfig.getConfiguration().getBoolean("com.baton.server.logMsg", false) ;
		
	}
	
	public int getQueueSize() {
		return queueMap.size() ;
	}
	public boolean isClientAlreadyExists(String clientName) {
		return queueMap.containsKey(clientName) ;
	}
	public ClientData getClientData(String clientName) {
		if(queueMap.containsKey(clientName))
			return queueMap.get(clientName).getValue() ;
		return null ;
	}
	public boolean addClient(String clientName) {
		if(queueMap.containsKey(clientName)) {
			return false ;
		}
		ClientData cd = new ClientData(clientName);
		QueueNode newNode = new QueueNode(cd);
		if(rootNode == null) {
			rootNode = newNode ;
			nextProcessingNode = rootNode ;
		}else 
			QueueNode.addNodeToEnd(rootNode, newNode);
		queueMap.put(clientName, newNode) ;
		return true;
	}
	
	public boolean isNodeCurrentlyProcessed(String clientName) {
		if(queueMap.containsKey(clientName) == false) {
			return false ;
		}
		QueueNode node = queueMap.get(clientName) ;
		return (node == currentProcessingNode);
	}
	public boolean removeClient(String clientName) {
		if(queueMap.containsKey(clientName) == false) {
			return false ;
		}
		QueueNode nodeToRemove = queueMap.get(clientName) ;
		if(nodeToRemove.getPrevNode() == null) { //it means it is root node
			rootNode = nodeToRemove.getNextNode() ;
			if(rootNode != null)
			rootNode.setPrevNode(null);
		} else {
			if(nodeToRemove.getPrevNode() != null)
				nodeToRemove.getPrevNode().setNextNode(nodeToRemove.getNextNode());
			if(nodeToRemove.getNextNode() != null)
				nodeToRemove.getNextNode().setPrevNode(nodeToRemove.getPrevNode());
		}
		if(nodeToRemove == currentProcessingNode) {		
			nextProcessingNode = (currentProcessingNode.getNextNode() != null ? currentProcessingNode.getNextNode() : rootNode);
			currentProcessingNode = null ;
		}
		if(nextProcessingNode == nodeToRemove) {
			if(nextProcessingNode.getNextNode() == null)
				nextProcessingNode = rootNode ;
			else
				nextProcessingNode = nextProcessingNode.getNextNode() ;
		}
		queueMap.remove(clientName);
		if(logMsg)
			QueueNode.print(rootNode);
		return true ;
	}
	
	public ClientData getClientDataToProcess() {
		// it means either it is first time or queue is empty
		ClientData cd = null ;
		if(currentProcessingNode != null) {
			currentProcessingNode = (currentProcessingNode.getNextNode() != null ? currentProcessingNode.getNextNode() : rootNode);
			if(currentProcessingNode == null) {
				return null ;
			}
			return currentProcessingNode.getValue() ;
		}
		if(nextProcessingNode == null) {
			nextProcessingNode = rootNode ;
		} 
		if(nextProcessingNode != null) {
			currentProcessingNode = nextProcessingNode ;
			nextProcessingNode = null ;
			cd = currentProcessingNode.getValue() ;
		}
		
		return cd ;
	}
}
