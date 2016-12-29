package com.jsoniter;

import junit.framework.TestCase;

public class TestSlice extends TestCase {

    public void test_equals() {
        assertTrue(Slice.make("hello").equals(Slice.make("hello")));
        assertTrue(Slice.make("hello").equals("hello"));
        assertTrue(Slice.make("hello").equals(new Slice("ahello".getBytes(), 1, 6)));
    }
}
