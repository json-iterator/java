package com.jsoniter;

import com.jsoniter.annotation.JsonIgnore;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.Serializable;

public class TestAnnotationJsonIgnore extends TestCase {

    public static class TestObject1 {
        @JsonIgnore
        public int field2;
    }

    public void test_ignore() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(0, obj.field2);
    }

    public static class TestObject2 {
        @JsonIgnore
        public Serializable field2;
    }

    public void test_ignore_no_constructor_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestObject2 obj = iter.read(TestObject2.class);
        assertNull(obj.field2);
    }
}
