package com.jsoniter.output;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.Encoder;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestAnnotationJsonProperty extends TestCase {

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
}
