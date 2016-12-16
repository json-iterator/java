package com.jsoniter;

import junit.framework.TestCase;

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
