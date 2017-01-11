package com.jsoniter;

import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestBoolean extends TestCase {
    @Category(AllTests.StreamingCategory.class)
    public void test() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("[true,false,true]".getBytes()), 4);
        iter.readArray();
        assertTrue(iter.readBoolean());
        iter.readArray();
        assertFalse(iter.readBoolean());
        iter.readArray();
        assertTrue(iter.readBoolean());
    }
}
