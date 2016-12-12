package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestInheritance extends TestCase {
    public void test() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        InheritedObject inheritedObject = iter.read(InheritedObject.class);
        assertEquals("hello", inheritedObject.inheritedField);
    }
}
