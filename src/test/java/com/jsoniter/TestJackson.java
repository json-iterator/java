package com.jsoniter;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

import java.io.IOException;

public class TestJackson extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    private ObjectMapper objectMapper;

    public void setUp() {
        JacksonAnnotationSupport.enable();
        objectMapper = new ObjectMapper();
    }

    public void tearDown() {
        JacksonAnnotationSupport.disable();
    }

    public static class TestObject1 {
        private int _id;
        private String _name;

        @JsonAnySetter
        public void setProperties(String key, Object value) {
            if (key.equals("name")) {
                _name = (String) value;
            } else if (key.equals("id")) {
                _id = ((Number) value).intValue();
            }
        }
    }

    public void test_JsonAnySetter() throws IOException {
        TestObject1 obj = objectMapper.readValue("{\"name\":\"hello\",\"id\":100}", TestObject1.class);
        assertEquals("hello", obj._name);
        assertEquals(100, obj._id);
        obj = JsonIterator.deserialize("{\"name\":\"hello\",\"id\":100}", TestObject1.class);
        assertEquals("hello", obj._name);
        assertEquals(100, obj._id);
    }

    public static class TestObject2 {
        @JsonProperty("field-1")
        public String field1;
    }

    public void test_JsonProperty() throws IOException {
        TestObject2 obj = objectMapper.readValue("{\"field-1\":\"hello\"}", TestObject2.class);
        assertEquals("hello", obj.field1);
        obj = JsonIterator.deserialize("{\"field-1\":\"hello\"}", TestObject2.class);
        assertEquals("hello", obj.field1);
    }

    public static class TestObject3 {
        @JsonIgnore
        public String field1;
    }

    public void test_JsonIgnore() throws IOException {
        TestObject3 obj = objectMapper.readValue("{\"field1\":\"hello\"}", TestObject3.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":\"hello\"}", TestObject3.class);
        assertNull(obj.field1);
    }
}
