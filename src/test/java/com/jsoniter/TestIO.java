package com.jsoniter;

import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Category(AllTests.StreamingCategory.class)
public class TestIO extends TestCase {

    public void test_read_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("1".getBytes()), 4096);
        assertEquals('1', IterImpl.readByte(iter));
        assertEquals(0, IterImpl.readByte(iter));
    }

    public void test_read_bytes() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', IterImpl.readByte(iter));
        assertEquals('2', IterImpl.readByte(iter));
        assertEquals(0, IterImpl.readByte(iter));
    }

    public void test_unread_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', IterImpl.readByte(iter));
        assertEquals('2', IterImpl.readByte(iter));
        iter.unreadByte();
        assertEquals('2', IterImpl.readByte(iter));
        iter.unreadByte();
        iter.unreadByte();
        assertEquals('1', IterImpl.readByte(iter));
    }

}
