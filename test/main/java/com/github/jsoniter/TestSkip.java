package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestSkip extends TestCase {
    public void test_skip_number() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[1,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_string() throws IOException {
        Jsoniter iter = Jsoniter.parseString("['hello',2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_object() throws IOException {
        Jsoniter iter = Jsoniter.parseString("[{'hello': {'world': 'a'}},2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_find_string_end() {
        Jsoniter iter = Jsoniter.parseString("\"a");
        assertEquals(1, iter.findStringEnd());
    }
}
