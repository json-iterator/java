package com.jsoniter;

import com.jsoniter.any.Any;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
        assertEquals(100, JsonIterator.deserialize("100").toInt());
        assertEquals(0, JsonIterator.deserialize("null").toInt());
        assertEquals(100, JsonIterator.deserialize("\"100\"").toInt());
        assertEquals(1, JsonIterator.deserialize("true").toInt());
        Any any = JsonIterator.deserialize("100");
        assertEquals(Long.valueOf(100), any.object());
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100F, any.toFloat());
        assertEquals(100D, any.toDouble());
        assertEquals("100", any.toString());
    }

    public void test_read_boolean() throws IOException {
        assertEquals(true, JsonIterator.deserialize("100").toBoolean());
        assertEquals(false, JsonIterator.deserialize("{}").toBoolean());
        assertEquals(true, JsonIterator.deserialize("{\"field1\":100}").toBoolean());
        assertEquals(false, JsonIterator.deserialize("null").toBoolean());
        assertEquals(true, JsonIterator.deserialize("\"100\"").toBoolean());
        assertEquals(true, JsonIterator.deserialize("true").toBoolean());
        assertEquals(1, JsonIterator.deserialize("true").toInt());
        assertEquals(0, JsonIterator.deserialize("false").toInt());
        assertEquals("false", JsonIterator.deserialize("false").toString());
        assertEquals(Boolean.FALSE, JsonIterator.deserialize("false").object());
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
    }

    public void test_read_float_as_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("[\"100.1\",\"101.1\"]");
        Any any = iter.readAny();
        assertEquals(100, any.toInt(0));
        assertEquals(101, any.toInt(1));
    }

    public void test_read_string() throws IOException {
        assertEquals("hello", JsonIterator.deserialize("\"hello\"").toString());
        assertEquals("true", JsonIterator.deserialize("true").toString());
        assertEquals("null", JsonIterator.deserialize("null").toString());
        assertEquals("100", JsonIterator.deserialize("100").toString());
        assertEquals(100, JsonIterator.deserialize("\"100\"").toInt());
        assertEquals(true, JsonIterator.deserialize("\"hello\"").toBoolean());
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
        Any any = JsonIterator.deserialize("\"100.1\"");
        assertEquals(100L, any.toLong());
        assertEquals(100L, any.toLong());
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
        assertEquals(new HashSet<Object>(Arrays.asList()), JsonIterator.deserialize("[3,5]").keys());
    }

    public void test_read_double() throws IOException {
        assertEquals(100.0D, JsonIterator.deserialize("100").toDouble());
        assertEquals(100.1D, JsonIterator.deserialize("100.1").toDouble());
        assertEquals(100.1D, JsonIterator.deserialize("\"100.1\"").toDouble());
    }

    public static class TestObject1 {
        public int field1;
    }

    public void test_read_class() throws IOException {
        TestObject1 obj = JsonIterator.deserialize("{\"field1\": 100}").as(TestObject1.class);
        assertEquals(100, obj.field1);
    }

    public void test_read_multiple_field() throws IOException {
        Any any = JsonIterator.deserialize("{\"a\":1,\"b\":2,\"c\":3}");
        assertEquals(2, any.toInt("b"));
        assertEquals(1, any.toInt("a"));
        assertEquals(3, any.toInt("c"));
        any = JsonIterator.deserialize("{\"a\":1,\"b\":2,\"c\":3}");
        assertEquals(3, any.toInt("c"));
        assertEquals(2, any.toInt("b"));
        assertEquals(1, any.toInt("a"));
    }

    public void test_require_path() throws IOException {
        assertNotNull(JsonIterator.deserialize("null").require());
        try {
            JsonIterator.deserialize("[]").require(0);
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            JsonIterator.deserialize("{}").require("hello");
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    @Category(AllTests.StreamingCategory.class)
    public void test_read_any_in_streaming() throws IOException {
        assertEquals(2, JsonIterator.parse(new ByteArrayInputStream("[1,2,3,4,5]" .getBytes()), 2).readAny().toInt(1));
        assertEquals(1, JsonIterator.parse(new ByteArrayInputStream("{\"field1\": 1}" .getBytes()), 2).readAny().size());
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("[1,2,[3, 4],5]" .getBytes()), 2);
        ArrayList<Any> elements = new ArrayList<Any>();
        while(iter.readArray()) {
            elements.add(iter.readAny());
        }
        assertEquals("[3, 4]", elements.get(2).toString());
    }
}
