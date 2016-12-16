package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

public class TestObject extends TestCase {

    static {
//        JsonIterator.enableStrictMode();
    }

    public static class EmptyClass {}

    public void test_empty_class() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(EmptyClass.class));
    }

    public void test_empty_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNull(iter.readObject());
        iter.reset();
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj.field1);
        iter.reset();
        Map obj = (Map) iter.read(Object.class);
        assertEquals(0, obj.size());
        iter.reset();
        Any any = iter.readAny();
        assertEquals(0, any.getMap().size());
    }

    public void test_one_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertNull(iter.readObject());
        iter.reset();
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertNull(simpleObj.field2);
        iter.reset();
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertFalse(any.exists("field2"));
    }

    public void test_two_fields() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' , 'field2': 'world' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertEquals("field2", iter.readObject());
        assertEquals("world", iter.readString());
        assertNull(iter.readObject());
        iter.reset();
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertEquals("world", simpleObj.field2);
        iter.reset();
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertEquals("world", any.toString("field2"));
    }

    public void test_read_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
        iter.reset();
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj);
        iter.reset();
        Any any = iter.readAny();
        assertNull(any.get());
    }

    public void test_native_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 100 }".replace('\'', '"'));
        ComplexObject complexObject = iter.read(ComplexObject.class);
        assertEquals(100, complexObject.field1);
        iter.reset();
        Any any = iter.readAny();
        assertEquals(100, any.toInt("field1"));
    }

    public void test_inheritance() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        InheritedObject inheritedObject = iter.read(InheritedObject.class);
        assertEquals("hello", inheritedObject.inheritedField);
    }
}
