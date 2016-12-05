package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestInt extends TestCase {
    public void test_unsigned_int() throws IOException {
        Jsoniter iter = Jsoniter.parseString("123");
        assertEquals(123, iter.ReadUnsignedInt());
    }
}
