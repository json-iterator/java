package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestReadAny extends TestCase {
    public void test_read_any() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        assertEquals(3, iter.readAny().toInt(3));
    }
    public void test_bind_to_any() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field3': 100}".replace('\'', '"'));
        ComplexObject obj = iter.read(ComplexObject.class);
        System.out.println(obj.field3);
    }
    public void test_read_any_from_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'numbers': ['1', '2', ['3', '4']]}".replace('\'', '"'));
        assertEquals(3, iter.readAny().toInt("numbers", 2, 0));
    }
}
