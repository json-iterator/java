package com.jsoniter;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestSkip extends TestCase {

    public void test_skip_number() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello',2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    @Category(StreamingCategory.class)
    public void test_skip_string_streaming() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("\"hello".getBytes()), 2);
        try {
            iter.skip();
            fail();
        } catch (JsonException e) {
        }
        iter = JsonIterator.parse(new ByteArrayInputStream("\"hello\"".getBytes()), 2);
        iter.skip();
        iter = JsonIterator.parse(new ByteArrayInputStream("\"hello\"1".getBytes()), 2);
        iter.skip();
        assertEquals(1, iter.readInt());
        iter = JsonIterator.parse(new ByteArrayInputStream("\"h\\\"ello\"1".getBytes()), 3);
        iter.skip();
        assertEquals(1, iter.readInt());
        iter = JsonIterator.parse(new ByteArrayInputStream("\"\\\\\"1".getBytes()), 3);
        iter.skip();
        assertEquals(1, iter.readInt());
    }

    public void test_skip_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("[{'hello': {'world': 'a'}},2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[ [1,  3] ,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_nested() throws IOException {
        JsonIterator iter = JsonIterator.parse("[ [1, {'a': ['b'] },  3] ,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }
}
