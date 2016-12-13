package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestFloat extends TestCase {

    public void test_float() throws IOException {
        Jsoniter iter = Jsoniter.parse("1.1");
        assertEquals(1.1f, iter.readFloat());
    }

    public void test_double() throws IOException {
        Jsoniter iter = Jsoniter.parse("1.1,");
        assertEquals(1.1, iter.readDouble());
    }

    public void test_double_long() throws IOException {
        Jsoniter iter = Jsoniter.parse("1234567,");
        assertEquals(1234567.0, iter.readDouble());
        assertEquals(7, iter.head);
    }

    public void test_negative() throws IOException {
        Jsoniter iter = Jsoniter.parse("-1234567,");
        assertEquals(-1234567.0, iter.readDouble());
        assertEquals(8, iter.head);
    }
}
