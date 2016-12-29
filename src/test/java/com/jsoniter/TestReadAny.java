package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

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

    public void test_read_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("100");
        assertEquals(100, iter.readAny().toInt());
    }

    public void test_read_int_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[100,101]");
        Any any = iter.readAny();
        assertEquals(100, any.toInt(0));
        assertEquals(101, any.toInt(1));
    }

    public void test_read_int_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\":100}");
        Any any = iter.readAny();
        assertEquals(100, any.toInt("field1"));
        assertEquals(100, any.toInt(Slice.make("field1")));
    }

    public void test_read_null_as_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("null");
        Any any = iter.readAny();
        assertEquals(0, any.toInt());
    }

    public void test_read_string_as_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("\"100\"");
        Any any = iter.readAny();
        assertEquals(100, any.toInt());
    }

    public void test_read_float_as_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("[\"100.1\",\"101.1\"]");
        Any any = iter.readAny();
        assertEquals(100, any.toInt(0));
        assertEquals(101, any.toInt(1));
    }

    public void test_read_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("\"hello\"");
        Any any = iter.readAny();
        assertEquals("hello", any.toString());
    }

    public void test_read_int_as_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("100.5");
        Any any = iter.readAny();
        assertEquals("100.5", any.toString());
    }

    public void test_get() throws IOException {
        assertEquals("100.5", JsonIterator.deserialize("100.5").get().toString());
        assertEquals("100.5", JsonIterator.deserialize("[100.5]").get(0).toString());
        assertNull(JsonIterator.deserialize("null").get(0));
        assertNull(JsonIterator.deserialize("[]").get(0));
        assertNull(JsonIterator.deserialize("[]").get("hello"));
        assertNull(JsonIterator.deserialize("{}").get(0));
    }

    public void test_read_long() throws IOException {
        assertEquals(100L, JsonIterator.deserialize("100").toLong());
        assertEquals(100L, JsonIterator.deserialize("100.1").toLong());
        assertEquals(100L, JsonIterator.deserialize("\"100.1\"").toLong());
    }

    public void test_read_float() throws IOException {
        assertEquals(100.0F, JsonIterator.deserialize("100").toFloat());
        assertEquals(100.1F, JsonIterator.deserialize("100.1").toFloat());
        assertEquals(100.1F, JsonIterator.deserialize("\"100.1\"").toFloat());
    }

    public void test_size() throws IOException {
        assertEquals(0, JsonIterator.deserialize("[]").size());
        assertEquals(1, JsonIterator.deserialize("[1]").size());
        assertEquals(2, JsonIterator.deserialize("[1,2]").size());
        assertEquals(1, JsonIterator.deserialize("{\"field1\":1}").size());
    }

    public void test_keys() throws IOException {
        assertEquals(new HashSet<Object>(Arrays.asList("field1")), JsonIterator.deserialize("{\"field1\":1}").keys());
        assertEquals(new HashSet<Object>(Arrays.asList(0,1)), JsonIterator.deserialize("[3,5]").keys());
    }

    public void test_read_double() throws IOException {
        assertEquals(100.0D, JsonIterator.deserialize("100").toDouble());
        assertEquals(100.1D, JsonIterator.deserialize("100.1").toDouble());
        assertEquals(100.1D, JsonIterator.deserialize("\"100.1\"").toDouble());
    }
}
