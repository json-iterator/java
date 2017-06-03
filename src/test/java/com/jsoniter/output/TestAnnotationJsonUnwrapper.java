package com.jsoniter.output;

import com.jsoniter.annotation.JsonUnwrapper;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestAnnotationJsonUnwrapper extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class TestObject1 {
        @JsonUnwrapper
        public void unwrap(JsonStream stream) throws IOException {
            stream.writeObjectField("hello");
            stream.writeVal("world");
        }
    }

    public void test_unwrapper() throws IOException {
        TestObject1 obj = new TestObject1();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"hello\":\"world\"}", baos.toString());
    }

    public static class TestObject2 {
        @JsonUnwrapper
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_unwrapper_with_map() throws IOException {
        TestObject2 obj = new TestObject2();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"100\":\"hello\"}", baos.toString());
    }
}
