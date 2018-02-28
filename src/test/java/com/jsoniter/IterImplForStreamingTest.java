package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class IterImplForStreamingTest extends TestCase {
	

	public void testReadMaxDouble() throws Exception {
		String maxDouble = "1.7976931348623157e+308";
		JsonIterator iter = JsonIterator.parse("1.7976931348623157e+308");
		String number = IterImplForStreaming.readNumber(iter);
		assertEquals(maxDouble, number);
	}

	public void testReadNumber() throws IOException {
		// contract: readNumber should be able to read numbers of arbitrary length
		String number1 = "111111111111111111111111111111111";
		JsonIterator iter = JsonIterator.parse("111111111111111111111111111111111");
		String readNumber = IterImplForStreaming.readNumber(iter);
		assertEquals(number1, readNumber);

		String number2 = "1234E-1234";
		iter = JsonIterator.parse("1234E-1234");
		readNumber = IterImplForStreaming.readNumber(iter);
		assertEquals(number2, readNumber);

		String number3 = "55555554433223493783918368292.848389383477436373269";
		iter = JsonIterator.parse("55555554433223493783918368292.848389383477436373269 hello I am a string");
		readNumber = IterImplForStreaming.readNumber(iter);
		assertEquals(number3, readNumber);
	}
}