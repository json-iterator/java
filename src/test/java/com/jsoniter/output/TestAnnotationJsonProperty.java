package com.jsoniter.output;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.Encoder;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotationJsonProperty extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    public static class TestObject1 {
        @JsonProperty(to = {"field-1"})
        public String field1;
    }

    public void test_property() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        String output = JsonStream.serialize(obj);
        assertEquals("{\"field-1\":\"hello\"}", output);
    }


    public static class TestObject2 {
        @JsonProperty(encoder = Encoder.StringIntEncoder.class)
        public int field1;
    }

    public void test_encoder() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = 100;
        String output = JsonStream.serialize(obj);
        assertEquals("{\"field1\":\"100\"}", output);
    }

    public static class TestObject3 {
        public String field1 = "hello";

        @JsonProperty(to = {"field-1"})
        public String getField1() {
            return field1;
        }
    }

    public void test_getter() throws IOException {
        String output = JsonStream.serialize(new TestObject3());
        assertEquals("{\"field-1\":\"hello\"}", output);
    }
}
