package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class TestReflection extends TestCase {

    public void test_byte_array() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseString("[1,2,3]");
        byte[] val = new byte[3];
        jsoniter.Read(val);
        assertArrayEquals(new byte[]{1, 2, 3}, val);
    }

    public void test_int_array() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseString("[1,2,3]");
        int[] val = new int[3];
        jsoniter.Read(val);
        assertArrayEquals(new int[]{1, 2, 3}, val);
    }
}
