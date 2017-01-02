package com.jsoniter.output;

import com.jsoniter.ValueType;
import com.jsoniter.any.*;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;

public class TestAny extends TestCase {

    public void test_int() {
        Any any = Any.wrap(100);
        assertEquals(ValueType.NUMBER, any.valueType());
        assertEquals("100", JsonStream.serialize(any));
        assertEquals(Integer.valueOf(100), any.object());
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100D, any.toDouble());
        assertEquals(100F, any.toFloat());
        assertEquals("100", any.toString());
        assertEquals(true, any.toBoolean());
        any.set(101);
        assertEquals("101", any.toString());
    }

    public void test_long() {
        Any any = Any.wrap(100L);
        assertEquals(ValueType.NUMBER, any.valueType());
        assertEquals("100", JsonStream.serialize(any));
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100D, any.toDouble());
        assertEquals(100F, any.toFloat());
        assertEquals("100", any.toString());
        assertEquals(true, any.toBoolean());
        any.set(101L);
        assertEquals("101", any.toString());
    }

    public void test_float() {
        Any any = Any.wrap(100F);
        assertEquals(ValueType.NUMBER, any.valueType());
        assertEquals("100", JsonStream.serialize(any));
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100D, any.toDouble());
        assertEquals(100F, any.toFloat());
        assertEquals("100.0", any.toString());
        assertEquals(true, any.toBoolean());
        any.set(101F);
        assertEquals("101.0", any.toString());
    }

    public void test_double() {
        Any any = Any.wrap(100D);
        assertEquals(ValueType.NUMBER, any.valueType());
        assertEquals("100", JsonStream.serialize(any));
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100D, any.toDouble());
        assertEquals(100F, any.toFloat());
        assertEquals("100.0", any.toString());
        assertEquals(true, any.toBoolean());
        any.set(101D);
        assertEquals("101.0", any.toString());
    }

    public void test_null() {
        Any any = Any.wrap((Object) null);
        assertEquals(ValueType.NULL, any.valueType());
        assertEquals("null", JsonStream.serialize(any));
        assertEquals(false, any.toBoolean());
        assertEquals("null", any.toString());
    }

    public void test_boolean() {
        Any any = Any.wrap(true);
        assertEquals(ValueType.BOOLEAN, any.valueType());
        assertEquals("true", JsonStream.serialize(any));
        assertEquals(1, any.toInt());
        assertEquals(1L, any.toLong());
        assertEquals(1F, any.toFloat());
        assertEquals(1D, any.toDouble());
        assertEquals("true", any.toString());
    }

    public void test_string() {
        Any any = Any.wrap("hello");
        assertEquals(ValueType.STRING, any.valueType());
        assertEquals("\"hello\"", JsonStream.serialize(any));
        any.set("100");
        assertEquals(100, any.toInt());
        assertEquals(100L, any.toLong());
        assertEquals(100F, any.toFloat());
        assertEquals(100D, any.toDouble());
        assertEquals(true, any.toBoolean());
        assertEquals("100", any.toString());
    }

    public void test_list() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        assertEquals(ValueType.ARRAY, any.valueType());
        assertEquals("[1,2,3]", JsonStream.serialize(any));
        assertEquals(Integer.valueOf(1), any.get(0).object());
        assertEquals(true, any.toBoolean());
        assertEquals("[1,2,3]", any.toString());
    }

    public void test_array() {
        Any any = Any.wrap(new int[]{1, 2, 3});
        assertEquals(ValueType.ARRAY, any.valueType());
        assertEquals("[1,2,3]", JsonStream.serialize(any));
        assertEquals(Integer.valueOf(1), any.get(0).object());
        assertEquals(true, any.toBoolean());
        assertEquals("[1,2,3]", any.toString());
    }

    public void test_map() {
        HashMap<String, Object> val = new HashMap<String, Object>();
        val.put("hello", 1);
        val.put("world", "!!");
        Any any = Any.wrap(val);
        assertEquals(ValueType.OBJECT, any.valueType());
        assertEquals("{\"world\":\"!!\",\"hello\":1}", JsonStream.serialize(any));
        assertEquals(Integer.valueOf(1), any.get("hello").object());
        assertEquals(true, any.toBoolean());
        assertEquals("{\"world\":\"!!\",\"hello\":1}", any.toString());
    }

    public static class MyClass {
        public Object field1;
        public Any field2;
    }

    public void test_my_class() {
        MyClass val = new MyClass();
        val.field1 = "hello";
        val.field2 = Any.wrap(new long[]{1, 2});
        Any any = Any.wrap(val);
        assertEquals(ValueType.OBJECT, any.valueType());
        assertEquals("{\"field1\":\"hello\",\"field2\":[1,2]}", JsonStream.serialize(any));
    }

    public void test_object() {
        Any any = Any.wrap(new Object());
        assertEquals(ValueType.OBJECT, any.valueType());
        assertEquals("{}", JsonStream.serialize(new Object()));
    }
}
