package com.jsoniter;

import junit.framework.TestCase;

public class IterImplForStreamingTest extends TestCase {

	public void testReadMaxDouble() throws Exception {
		String maxDouble = "1.7976931348623157e+308";
		JsonIterator iter = JsonIterator.parse("1.7976931348623157e+308");
		String number = IterImplForStreaming.readNumber(iter);
		assertEquals(maxDouble, number);
	}
}