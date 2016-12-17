package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestObject extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class TestObject1 {
        public String field1;
    }

    public void test_field() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'hello'}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject2 {
        private String field1;
        public String getField1() {
            return field1;
        }
    }

    public void test_getter() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'hello'}".replace('\'', '"'), baos.toString());
    }
}
