package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.math.BigDecimal;

public class TestBigDecimal extends TestCase {
    public void test() throws IOException {
        Jsoniter iter = Jsoniter.parse("1.23");
        assertEquals(new BigDecimal("1.23"), iter.readBigDecimal());
    }
}
