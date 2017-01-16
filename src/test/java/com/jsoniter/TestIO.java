package com.jsoniter;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@org.junit.experimental.categories.Category(StreamingCategory.class)
public class TestIO extends TestCase {

    public void test_read_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("1".getBytes()), 4096);
        assertEquals('1', IterImpl.readByte(iter));
        try {
            IterImpl.readByte(iter);
            fail();
        } catch (JsonException e) {
        }
    }

    public void test_read_bytes() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        assertEquals('1', IterImpl.readByte(iter));
        assertEquals('2', IterImpl.readByte(iter));
        try {
            IterImpl.readByte(iter);
            fail();
        } catch (JsonException e) {
        }
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
