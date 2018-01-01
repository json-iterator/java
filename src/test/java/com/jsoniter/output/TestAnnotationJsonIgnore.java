package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotationJsonIgnore extends TestCase {

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

    public static class TestPrivateVariables {
        @JsonIgnore
        private String field1;

        public String getField1() {
            return field1;
        }
    }

    public void test_private_serialize() throws IOException {
        TestPrivateVariables obj = new TestPrivateVariables();
        obj.field1 = "hello";
        assertEquals("{}", JsonStream.serialize(obj));
    }
}
