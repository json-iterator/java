package com.jsoniter.output;

import junit.framework.TestCase;

import java.math.BigDecimal;

public class TestFloat extends TestCase {
    public void testBigDecimal() {
        assertEquals("100.1", JsonStream.serialize(new BigDecimal("100.1")));
    }
    public void test_infinity() {
        assertEquals("\"Infinity\"", JsonStream.serialize(Double.POSITIVE_INFINITY));
        assertEquals("\"Infinity\"", JsonStream.serialize(Float.POSITIVE_INFINITY));
        assertEquals("\"-Infinity\"", JsonStream.serialize(Double.NEGATIVE_INFINITY));
        assertEquals("\"-Infinity\"", JsonStream.serialize(Float.NEGATIVE_INFINITY));
    }
}
