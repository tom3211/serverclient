package com.baton.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestClientDataGenerator {

	@Test
	public void testWeightIncrement() {
		int wt1 = ClientDataGenerator.getInstance().getNextWeight() ;
		int wt2 = ClientDataGenerator.getInstance().getNextWeight() ;
		boolean valid = (wt2 > wt1 ? true : false);
		Assert.assertEquals(valid, true);
	}
}
