package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestString extends TestCase {

    public void test_string() throws IOException {
        Jsoniter iter = Jsoniter.parseString("'hello''world'".replace('\'', '"'));
        assertEquals("hello", iter.readStringAsSlice().toString());
        assertEquals("world", iter.readStringAsSlice().toString());
    }

    public void test_string_across_buffer() throws IOException {
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream("'hello''world'".replace('\'', '"').getBytes()), 2);
        assertEquals("hello", iter.readStringAsSlice().toString());
        assertEquals("world", iter.readStringAsSlice().toString());
    }

}
