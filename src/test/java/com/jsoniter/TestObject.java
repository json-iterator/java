package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestObject extends TestCase {

    public void test_empty_object() throws IOException {
        Jsoniter iter = Jsoniter.parse("{}");
        assertNull(iter.readObject());
    }

    public void test_one_field() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'hello':'world'}".replace('\'', '"'));
        assertEquals("hello", iter.readObject());
        assertEquals("world", iter.readString());
        assertNull(iter.readObject());
    }
}
