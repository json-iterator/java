package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestReadAny extends TestCase {
    public void test_read_any() throws IOException {
        Jsoniter iter = Jsoniter.parse("[0,1,2,3]");
        assertEquals(3, iter.readAny().toInt(3));
    }
    public void test_bind_to_any() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'field3': 100}".replace('\'', '"'));
        ComplexObject obj = iter.read(ComplexObject.class);
        System.out.println(obj.field3);
    }
}
