package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestArray extends TestCase {

    public void test_empty_array() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[]");
        assertFalse(iter.readArray());
    }

    public void test_one_element() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_two_elements() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1,2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_in_head() throws IOException {
        Jsoniter iter = Jsoniter.parseString(" [1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_after_array_start() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[ 1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_before_array_end() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1 ]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_before_comma() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1 ,2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_whitespace_after_comma() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1, 2]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readUnsignedInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }
}
