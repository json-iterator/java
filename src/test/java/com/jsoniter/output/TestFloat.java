package com.jsoniter.output;

import junit.framework.TestCase;

import java.math.BigDecimal;

public class TestFloat extends TestCase {
    public void testBigDecimal() {
        assertEquals("100.1", JsonStream.serialize(new BigDecimal("100.1")));
    }
}
