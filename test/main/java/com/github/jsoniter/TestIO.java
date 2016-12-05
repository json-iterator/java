package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestIO extends TestCase {

    public void test_read_byte() throws IOException {
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream("1".getBytes()), 4096);
        assertEquals('1', iter.nextByte());
        assertEquals(0, iter.nextByte());
        assertTrue(iter.eof);
    }

    public void test_read_bytes() throws IOException {
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', iter.nextByte());
        assertEquals('2', iter.nextByte());
        assertEquals(0, iter.nextByte());
        assertTrue(iter.eof);
    }

    public void test_unread_byte() throws IOException {
        Jsoniter iter = Jsoniter.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', iter.nextByte());
        assertEquals('2', iter.nextByte());
        iter.unreadByte();
        assertEquals('2', iter.nextByte());
        iter.unreadByte();
        iter.unreadByte();
        assertEquals('1', iter.nextByte());
    }

}
