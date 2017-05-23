package com.jsoniter;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;

public class TestNested extends TestCase {
    public void test_array_of_objects() throws IOException {
        JsonIterator iter = JsonIterator.parse(
                "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"'));
        SimpleObject[] objects = iter.read(SimpleObject[].class);
        Assert.assertArrayEquals(new SimpleObject[]{
                new SimpleObject() {{
                    field1 = "11";
                    field2 = "12";
                }},
                new SimpleObject() {{
                    field1 = "21";
                    field2 = "22";
                }}
        }, objects);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("22", any.toString(1, "field2"));
    }

    public void test_get_all_array_elements_via_any() throws IOException {
        Any any = JsonIterator.deserialize(" [ { \"bar\": 1 }, {\"bar\": 3} ]");
        Any result = any.get('*', "bar");
        assertEquals("[ 1, 3]", result.toString());
        any = Any.rewrap(any.asList()); // make it not lazy
        result = any.get('*', "bar");
        assertEquals("[ 1, 3]", result.toString());
    }

    public void test_get_all_object_values_via_any() throws IOException {
        Any any = JsonIterator.deserialize("{\"field1\":[1,2],\"field2\":[3,4]}");
        Any result = any.get('*', 1);
        assertEquals("{\"field1\":2,\"field2\":4}", result.toString());
        any = Any.rewrap(any.asMap()); // make it not lazy
        result = any.get('*', 1);
        assertEquals("{\"field1\":2,\"field2\":4}", result.toString());
    }

    public void test_get_all_with_some_invalid_path() throws IOException {
        Any any = JsonIterator.deserialize(" [ { \"bar\": 1 }, {\"foo\": 3} ]");
        Any result = any.get('*', "bar");
        assertEquals("[ 1]", result.toString());
        any = Any.rewrap(any.asList()); // make it not lazy
        result = any.get('*', "bar");
        assertEquals("[ 1]", result.toString());
        any = JsonIterator.deserialize("{\"field1\":[1,2],\"field2\":[3]}");
        result = any.get('*', 1);
        assertEquals("{\"field1\":2}", result.toString());
        any = Any.rewrap(any.asMap()); // make it not lazy
        result = any.get('*', 1);
        assertEquals("{\"field1\":2}", result.toString());
    }

    public static class TestObject3 {
        public com.jsoniter.output.TestNested.TestObject3 reference;
    }

    public void test_recursive_class() {
        // recursive reference will not be supported
        // however recursive structure is supported
        com.jsoniter.output.TestNested.TestObject3 obj = new com.jsoniter.output.TestNested.TestObject3();
        assertNull(JsonIterator.deserialize("{\"reference\":null}", TestObject3.class).reference);
    }
}
