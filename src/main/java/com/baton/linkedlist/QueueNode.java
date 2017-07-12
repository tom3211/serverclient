package com.baton.linkedlist;

import com.baton.ClientData;

public class QueueNode {
	private ClientData value ;
	private QueueNode nextNode ;
	private QueueNode prevNode ;

	public QueueNode(ClientData value) {
		this.value = value ;
	}

	public ClientData getValue() {
		return value;
	}
	public QueueNode getNextNode() {
		return nextNode;
	}

	public QueueNode getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(QueueNode node) {
		this.prevNode = node ;
	}
	public void setNextNode(QueueNode node) {
		this.nextNode = node ;
	}
	public static void addNodeToEnd(QueueNode root, QueueNode newNode) {
		QueueNode lastNode = root ;
		while(lastNode.nextNode != null) {
			lastNode = lastNode.nextNode ;
		}
		newNode.prevNode = lastNode ;
		lastNode.nextNode = newNode ;
	}
	public static void print(QueueNode rootNode) {
		while(rootNode != null) {
			System.out.println(" Value " + rootNode.value.getClientName() +  
					" Next " + (rootNode.getNextNode() != null ? rootNode.getNextNode().getValue().getClientName() : " NULL ") +
					" Prev " + (rootNode.getPrevNode() != null ? rootNode.getPrevNode().getValue().getClientName() : " NULL ")
					
					);
			rootNode = rootNode.nextNode ;
		}
	}
	public static void main(String[] argv) {
		ClientData cd = new ClientData("A");
		QueueNode rootNode = new QueueNode(cd);
		cd = new ClientData("B");
		QueueNode childNode =  new QueueNode(cd);
		QueueNode.addNodeToEnd(rootNode, childNode);
		
		cd = new ClientData("C");
		childNode =  new QueueNode(cd);
		QueueNode.addNodeToEnd(rootNode, childNode);
		
		//
		cd = new ClientData("D");
		childNode =  new QueueNode(cd);
		QueueNode.addNodeToEnd(rootNode, childNode);
		childNode = rootNode ;
		while(childNode != null) {
			System.out.println(" Value " + childNode.getValue().getClientName() +
					" next " + (childNode.nextNode != null ? childNode.nextNode.getValue().getClientName() : " NULL") +
					" prev " + (childNode.prevNode != null ? childNode.prevNode.getValue().getClientName() : " NULL")
					
					);
			childNode = childNode.nextNode ;
		}
		
	}
}
