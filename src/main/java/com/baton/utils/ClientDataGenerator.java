package com.baton.utils;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * Utility class to generate client attributes 
 * a) Generate wt, used for sorting the clients
 * b) Generate unique clientName (This not being used)
 * @author vajrala
 *
 */
public final class  ClientDataGenerator {
	private static final char[] namePrefixs = {
			'A','B','C','D','E','F','G','H',
			'I','J','K','L','M','N','O','P',
			'Q','R','S','T','U','V','W','X',
			'Y','Z'
	};
	private final static ClientDataGenerator instance = new ClientDataGenerator() ;
	private AtomicInteger clientNameId = new AtomicInteger() ;
	private AtomicInteger clientWeight = new AtomicInteger() ;
	private ClientDataGenerator() {
		
	}
	
	public int getNextWeight() {
		return clientWeight.incrementAndGet() ;
	}
	
	public String generateClintName() {
		int id = clientNameId.getAndIncrement() ;
		int count = id / 26 ;
		int suffix = id % 26 ;
		StringBuilder sb = new StringBuilder() ;
		for(int i = 0 ; i < count ; i++) {
			sb.append(namePrefixs[0]);
		}
		sb.append(namePrefixs[suffix]);
		return sb.toString() ;
	}
	public static ClientDataGenerator getInstance() {
		return instance ;
	}
	
	public static void main(String[] argv) {
		for(int i = 0 ;i < 2 ;i++) {
			System.out.println(" Client Name " + ClientDataGenerator.getInstance().generateClintName());
		}
	}
}
