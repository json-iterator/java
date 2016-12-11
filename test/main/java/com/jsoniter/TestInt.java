package com.jsoniter;

import com.jsoniter.Jsoniter;
import junit.framework.TestCase;

import java.io.IOException;

public class TestInt extends TestCase {

    public void test_unsigned_int() throws IOException {
        Jsoniter iter = Jsoniter.parse("123");
        assertEquals(123, iter.readUnsignedInt());
    }

    public void test_int() throws IOException {
        Jsoniter iter = Jsoniter.parse("-123");
        assertEquals(-123, iter.readInt());
    }

    public void test_short() throws IOException {
        Jsoniter iter = Jsoniter.parse("-123");
        assertEquals(-123, iter.readShort());
    }

    public void test_unsigned_long() throws IOException {
        Jsoniter iter = Jsoniter.parse("123");
        assertEquals(123, iter.readUnsignedLong());
    }

    public void test_long() throws IOException {
        Jsoniter iter = Jsoniter.parse("-123");
        assertEquals(-123, iter.readLong());
    }
}
