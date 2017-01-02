package com.jsoniter;

import com.jsoniter.any.Any;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

public class TestObject extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public static class EmptyClass {}

    public void test_empty_class() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(EmptyClass.class));
    }

    public void test_empty_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj.field1);
        iter.reset(iter.buf);
        Map obj = (Map) iter.read(Object.class);
        assertEquals(0, obj.size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(0, any.size());
    }

    public void test_one_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertNull(simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertNull(any.get("field2"));
    }

    public void test_two_fields() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' , 'field2': 'world' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertEquals("field2", iter.readObject());
        assertEquals("world", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertEquals("world", simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        any.require("field1");
        assertEquals("hello", any.toString("field1"));
        assertEquals("world", any.toString("field2"));
    }

    public void test_read_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(ValueType.NULL, any.get().valueType());
    }

    public void test_native_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 100 }".replace('\'', '"'));
        ComplexObject complexObject = iter.read(ComplexObject.class);
        assertEquals(100, complexObject.field1);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(100, any.toInt("field1"));
    }

    public static class InheritedObject extends SimpleObject {
        public String inheritedField;
    }

    public void test_inheritance() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        InheritedObject inheritedObject = iter.read(InheritedObject.class);
        assertEquals("hello", inheritedObject.inheritedField);
    }

    public void test_incomplete_field_name() throws IOException {
        try {
            JsonIterator.parse("{\"abc").read(InheritedObject.class);
            fail();
        } catch (JsonException e) {
        }
    }
}
