package com.jsoniter;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;

public class TestJackson extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public void setUp() {
        JacksonAnnotationSupport.enable();
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
        ObjectMapper objectMapper = new ObjectMapper();
        TestObject1 obj = objectMapper.readValue("{\"name\":\"hello\",\"id\":100}", TestObject1.class);
        assertEquals("hello", obj._name);
        assertEquals(100, obj._id);
        obj = JsonIterator.deserialize("{\"name\":\"hello\",\"id\":100}", TestObject1.class);
        assertEquals("hello", obj._name);
        assertEquals(100, obj._id);
    }
}
