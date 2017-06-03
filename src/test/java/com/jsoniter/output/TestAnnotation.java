package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestAnnotation extends TestCase {
    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        JsoniterAnnotationSupport.enable();
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void tearDown() {
        JsoniterAnnotationSupport.disable();
    }

    public static class TestObject3 {
        @JsonIgnore
        public int field1;
    }

    public void test_ignore() throws IOException {
        TestObject3 obj = new TestObject3();
        obj.field1 = 100;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{}", baos.toString());
    }

    public static class TestObject4 {
        public int field1;

        public int getField1() {
            return field1;
        }
    }

    public void test_name_conflict() throws IOException {
        TestObject4 obj = new TestObject4();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":0}", baos.toString());
    }

    public interface TestObject6Interface<A> {
        A getHello();
    }

    public static class TestObject6 implements TestObject6Interface<Integer> {
        public Integer getHello() {
            return 0;
        }
    }

    public void test_inherited_getter_is_not_duplicate() throws IOException {
        TestObject6 obj = new TestObject6();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"hello\":0}", baos.toString());
    }
}
