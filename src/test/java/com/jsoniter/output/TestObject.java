package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestObject extends TestCase {

    static {
        JsoniterAnnotationSupport.enable();
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

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
        @JsonIgnore
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

    public void test_null() throws IOException {
        stream.writeVal(new TypeLiteral<TestObject2>() {
        }, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public static class TestObject3 {
    }

    public void test_empty_object() throws IOException {
        stream.writeVal(new TestObject3());
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject4 {
        public String field1;
    }

    public void test_null_field() throws IOException {
        TestObject4 obj = new TestObject4();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':null}".replace('\'', '"'), baos.toString());
    }

    public static enum MyEnum {
        HELLO
    }

    public static class TestObject5 {
        public MyEnum field1;
    }

    public void test_enum() throws IOException {
        TestObject5 obj = new TestObject5();
        obj.field1 = MyEnum.HELLO;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'HELLO'}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject6 {
        public int[] field1;
    }

    public void test_array_field() throws IOException {
        TestObject6 obj = new TestObject6();
        obj.field1 = new int[]{1, 2, 3};
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":[1,2,3]}", baos.toString());
    }

    public void test_array_field_is_null() throws IOException {
        TestObject6 obj = new TestObject6();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":null}", baos.toString());
    }

    public static class TestObject7 {
        private int[] field1;
        public int[] getField1() {
            return field1;
        }
    }

    public void test_array_field_is_null_via_getter() throws IOException {
        TestObject7 obj = new TestObject7();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":null}", baos.toString());
    }
}
