package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestNull extends TestCase {

    public void test_null_string() throws IOException {
        Jsoniter iter = Jsoniter.parseString("null".replace('\'', '"'));
        assertEquals("", iter.readSlice().toString());
    }

    public void test_read_null() throws IOException {
        Jsoniter iter = Jsoniter.parseString("null".replace('\'', '"'));
        assertTrue(iter.readNull());
    }
}
