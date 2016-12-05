package com.github.jsoniter;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class TestReflection extends TestCase {

    public void test_byte_array() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseString("[1,2,3,4,5,6,7,8,9]");
        byte[] val = jsoniter.Read(byte[].class);
        assertArrayEquals(new byte[]{1,2,3,4,5,6,7,8,9}, val);
    }

    public void test_int_array() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseString("[1,2,3]");
        int[] val = jsoniter.Read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3}, val);
    }

    public void test_object() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseString("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'));
        TestObj val = jsoniter.Read(TestObj.class);
        assertEquals("hello", val.field1);
        assertEquals("world", val.field2);
    }

    public void test_fastjson() {
        TestObj testObj = JSON.parseObject("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'), TestObj.class);
    }
}
