package com.baton.utils;

public class TestUtils {

	public static void sleep(long seconds) {
		try {
			Thread.sleep( seconds * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
