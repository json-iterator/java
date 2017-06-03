package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotationJsonIgnore extends TestCase {

    public void setUp() {
        JsoniterAnnotationSupport.enable();
    }

    public void tearDown() {
        JsoniterAnnotationSupport.disable();
    }

    public static class TestObject1 {
        @JsonIgnore
        public int field1;
    }

    public void test_ignore() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = 100;
        assertEquals("{}", JsonStream.serialize(obj));
    }

    public static class TestObject2 {
        @JsonIgnore(ignoreEncoding = false)
        public int field1;
    }

    public void test_ignore_decoding_only() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = 100;
        assertEquals("{\"field1\":100}", JsonStream.serialize(obj));
    }
}
