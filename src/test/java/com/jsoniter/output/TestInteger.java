package com.jsoniter.output;

import junit.framework.TestCase;

import java.math.BigInteger;

public class TestInteger extends TestCase {
    public void testBigInteger() {
        assertEquals("100", JsonStream.serialize(new BigInteger("100")));
    }
}
