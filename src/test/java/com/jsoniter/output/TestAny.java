package com.jsoniter.output;

import com.jsoniter.ValueType;
import com.jsoniter.any.*;
import junit.framework.TestCase;

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
        Any any = Any.wrap(null);
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
        any.set(false);
        assertEquals("false", any.toString());
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
}
