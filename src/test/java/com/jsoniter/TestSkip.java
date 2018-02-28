package com.jsoniter;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestSkip extends TestCase {
    

    public void test_skip_empty_string() throws IOException {
        // contract: If there's nothing in the buffer and you try to skip
        // an exception should be thrown
        JsonIterator iter = JsonIterator.parse("");
        boolean exceptionThrown = false;

        try {
            iter.skip();
        } catch (ArrayIndexOutOfBoundsException _) {
            exceptionThrown = true;
        } catch (JsonException _) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void test_skip__positive_number() throws IOException {
        // contract: It should be possible to skip a number
        JsonIterator iter = JsonIterator.parse("[1,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());

        iter = JsonIterator.parse("[0,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());

        iter = JsonIterator.parse("[6,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_negative_number() throws IOException {
        // contract: It should be possible to skip a negative number
        JsonIterator iter = JsonIterator.parse("[-15733,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_string() throws IOException {
        // contract: It should be possible to skip a string
        JsonIterator iter = JsonIterator.parse("['hello',2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_true() throws IOException {
        // contract: It should be possible to skip a true
        JsonIterator iter = JsonIterator.parse("[true,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_null() throws IOException {
        // contract: It should be possible to skip null
        JsonIterator iter = JsonIterator.parse("[null,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_form_false() throws IOException {
        // contract: It should be possible to skip a from feed
        JsonIterator iter = JsonIterator.parse("[false,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_illegal() throws IOException {
        // contract: In the event that we try to skip
        // something else an exception should be
        // thrown
        JsonIterator iter = JsonIterator.parse("[g,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        boolean exceptionThrown = false;

        try {
            iter.skip();
        } catch (JsonException _) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Category(StreamingCategory.class)
    public void test_skip_string_streaming() throws IOException {
        // contract: Functionality should still work for a stream
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
        // contract: It should be possible to skip an object
        JsonIterator iter = JsonIterator.parse("[{'hello': {'world': 'a'}},2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_array() throws IOException {
        // contract: It should be possible to skip a array
        JsonIterator iter = JsonIterator.parse("[ [1,  3] ,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_nested() throws IOException {
        // contract: It should be possible to skip a nested structure
        JsonIterator iter = JsonIterator.parse("[ [1, {'a': ['b'] },  3] ,2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
    }
}
