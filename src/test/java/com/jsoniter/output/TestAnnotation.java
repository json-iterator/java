package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.Encoder;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestAnnotation extends TestCase {
    static {
        JsoniterAnnotationSupport.enable();
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class TestObject1 {
        @JsonProperty(to = {"field-1"})
        public String field1;
    }

    public void test_property() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field-1\":\"hello\"}", baos.toString());
    }

    public static class TestObject2 {
        @JsonProperty(encoder = Encoder.StringIntEncoder.class)
        public int field1;
    }

    public void test_encoder() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = 100;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":\"100\"}", baos.toString());
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
}
