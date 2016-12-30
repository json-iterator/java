package com.jsoniter;

import junit.framework.TestCase;

import java.util.HashMap;

public class TestSlice extends TestCase {

    public void test_equals() {
        assertTrue(Slice.make("hello").equals(Slice.make("hello")));
        assertTrue(Slice.make("hello").equals(new Slice("ahello".getBytes(), 1, 6)));
    }

    public void test_hashcode() {
        HashMap map = new HashMap();
        map.put(Slice.make("hello"), "hello");
        map.put(Slice.make("world"), "world");
        assertEquals("hello", map.get(Slice.make("hello")));
        assertEquals("world", map.get(Slice.make("world")));
    }
}
