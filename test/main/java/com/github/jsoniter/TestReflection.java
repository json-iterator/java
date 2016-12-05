package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class TestReflection extends TestCase {

    public void test_string() throws IOException {
        Jsoniter iter = Jsoniter.parseString("'hello'".replace('\'', '"'));
        String val = iter.read(String.class);
        assertEquals("hello", val);
    }

    public void test_byte_array() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1,2,3,4,5,6,7,8,9]");
        byte[] val = iter.read(byte[].class);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, val);
    }

    public void test_int_array() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1,2,3]");
        int[] val = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3}, val);
    }

    public void test_float_array() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1.1,2,3]");
        float[] val = iter.read(float[].class);
        assertArrayEquals(new float[]{1.1f, 2f, 3f}, val, 0.01f);
    }

    public void test_simple_object() throws IOException {
        Jsoniter iter = Jsoniter.parseString("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'));
        SimpleObject val = iter.read(SimpleObject.class);
        assertEquals("hello", val.field1);
        assertEquals("world", val.field2);
    }

    public void test_complex_object() throws IOException {
        Jsoniter iter = Jsoniter.parseString("{'field1': 100, 'field2': [1,2]}".replace('\'', '"'));
        ComplexObject val = iter.read(ComplexObject.class);
        assertEquals(100, val.field1);
        assertArrayEquals(new float[]{1f, 2f}, val.field2, 0.01f);
    }
}
