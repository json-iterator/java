package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestString extends TestCase {

    public void test_string() throws IOException {
        Jsoniter iter = Jsoniter.parse("'hello''world'".replace('\'', '"'));
        assertEquals("hello", iter.readSlice().toString());
        assertEquals("world", iter.readSlice().toString());
        iter = Jsoniter.parse("'hello''world'".replace('\'', '"'));
        assertEquals("hello", iter.readString());
        assertEquals("world", iter.readString());
    }

    public void test_string_across_buffer() throws IOException {
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream("'hello''world'".replace('\'', '"').getBytes()), 2);
        assertEquals("hello", iter.readSlice().toString());
        assertEquals("world", iter.readSlice().toString());
        iter = Jsoniter.parse(new ByteArrayInputStream("'hello''world'".replace('\'', '"').getBytes()), 2);
        assertEquals("hello", iter.readString());
        assertEquals("world", iter.readString());
    }

    public void test_utf8() throws IOException {
        byte[] bytes = {'"', (byte) 0xe4, (byte) 0xb8, (byte) 0xad, (byte) 0xe6, (byte) 0x96, (byte) 0x87, '"'};
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream(bytes), 2);
        assertEquals("中文", iter.readString());
    }

    public void test_normal_escape() throws IOException {
        byte[] bytes = {'"', (byte) '\\', (byte) 't', '"'};
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream(bytes), 2);
        assertEquals("\t", iter.readString());
    }

    public void test_unicode_escape() throws IOException {
        byte[] bytes = {'"', (byte) '\\', (byte) 'u', (byte) '4', (byte) 'e', (byte) '2', (byte) 'd', '"'};
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream(bytes), 2);
        assertEquals("中", iter.readString());
    }

}
