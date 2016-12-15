package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestNull extends TestCase {

    public void test_read_null() throws IOException {
        Jsoniter iter = Jsoniter.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
    }
}
