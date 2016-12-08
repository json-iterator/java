package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestString extends TestCase {

    public void test_string() throws IOException {
        Jsoniter iter = Jsoniter.parseString("'hello'".replace('\'', '"'));
        assertEquals("hello", iter.readStringAsSlice().toString());
    }

    public void test_null() throws IOException {
        Jsoniter iter = Jsoniter.parseString("null".replace('\'', '"'));
        assertEquals("", iter.readStringAsSlice().toString());
    }

    public void test_escape() throws IOException {
        Jsoniter iter = Jsoniter.parseString("'hel\\'lo'".replace('\'', '"'));
        assertEquals("hel\"lo", iter.readStringAsSlice().toString());
    }

    public void test_utf8() throws IOException {
        Jsoniter iter = Jsoniter.parseString("'\\u4e2d\\u6587'".replace('\'', '"'));
        assertEquals("中文", iter.readStringAsSlice().toString());
    }
}
