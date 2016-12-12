package com.jsoniter;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;

public class TestReflection extends TestCase {

    public void test_string() throws IOException {
        Jsoniter iter = Jsoniter.parse("'hello'".replace('\'', '"'));
        String val = iter.read(String.class);
        assertEquals("hello", val);
    }

    public void test_no_arg_list() throws IOException {
        Jsoniter iter = Jsoniter.parse("['hello']".replace('\'', '"'));
        Assert.assertArrayEquals(new String[]{"hello"}, iter.read(List.class).toArray(new String[0]));
    }

    public void test_boolean_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[true, false]");
        boolean[] val = iter.read(boolean[].class);
        assertArrayEquals(new boolean[]{true, false}, val);
    }

    public void test_int_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1,2,3]");
        int[] val = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3}, val);
    }

    public void test_Integer_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1,2,3]");
        Integer[] val = iter.read(Integer[].class);
        assertArrayEquals(new Integer[]{1, 2, 3}, val);
    }


    public void test_float_array() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1.1,2,3]");
        float[] val = iter.read(float[].class);
        assertArrayEquals(new float[]{1.1f, 2f, 3f}, val, 0.01f);
    }

    public void test_simple_object() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'));
        SimpleObject val = iter.read(SimpleObject.class);
        assertEquals("hello", val.field1);
        assertEquals("world", val.field2);
    }

    public void test_fields_skipped() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'field3': '3', 'field1': 100}".replace('\'', '"'));
        ComplexObject val = iter.read(ComplexObject.class);
        assertEquals(100, val.field1);
    }

    public void test_read_object() throws IOException {
        Jsoniter iter = Jsoniter.parse("'hello'".replace('\'', '"'));
        String val = (String) iter.read(Object.class);
        assertEquals("hello", val);
    }
}
