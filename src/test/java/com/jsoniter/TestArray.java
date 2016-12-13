package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class TestArray extends TestCase {

    public void test_empty_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[]");
        assertFalse(iter.readArray());
    }

    public void test_one_element() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_two_elements() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1,2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_in_head() throws IOException {
        Jsoniter iter = Jsoniter.parse(" [1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_after_array_start() throws IOException {
        Jsoniter iter = Jsoniter.parse("[ 1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_before_array_end() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1 ]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_before_comma() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1 ,2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_after_comma() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1, 2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_null() throws IOException {
        Jsoniter iter = Jsoniter.parse("null");
        assertNull(iter.read(double[].class));
    }

    public void test_boolean_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[true, false, true]");
        assertArrayEquals(new boolean[]{true, false, true}, iter.read(boolean[].class));
    }
}
