package com.jsoniter;

import com.jsoniter.Jsoniter;
import junit.framework.TestCase;

import java.io.IOException;

public class TestNull extends TestCase {

    public void test_null_string() throws IOException {
        Jsoniter iter = Jsoniter.parse("null".replace('\'', '"'));
        assertEquals(null, iter.readSlice());
    }

    public void test_read_null() throws IOException {
        Jsoniter iter = Jsoniter.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
    }
}
