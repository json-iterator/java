package com.jsoniter;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestIO extends TestCase {

    public void test_read_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("1".getBytes()), 4096);
        assertEquals('1', iter.readByte());
        assertEquals(0, iter.readByte());
        assertTrue(iter.eof);
    }

    public void test_read_bytes() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', iter.readByte());
        assertEquals('2', iter.readByte());
        assertEquals(0, iter.readByte());
        assertTrue(iter.eof);
    }

    public void test_unread_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', iter.readByte());
        assertEquals('2', iter.readByte());
        iter.unreadByte();
        assertEquals('2', iter.readByte());
        iter.unreadByte();
        iter.unreadByte();
        assertEquals('1', iter.readByte());
    }

}
