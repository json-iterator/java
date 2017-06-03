package com.jsoniter.output;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestJackson extends TestCase {

    public static class TestObject1 {
        @JsonAnyGetter
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_JsonAnyGetter() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String output = objectMapper.writeValueAsString(new TestObject1());
        assertEquals("{\"100\":\"hello\"}", output);
        JacksonAnnotationSupport.enable();
        output = JsonStream.serialize(new TestObject1());
        assertEquals("{\"100\":\"hello\"}", output);
    }

    public static class TestObject2 {
        private int id;
        private String name;

        @JsonAnySetter
        public void setProperties(String key, Object value) {
            if (key.equals("name")) {
                name = (String) value;
            } else if (key.equals("id")) {
                id = (Integer) value;
            }
        }
    }

    public void test_JsonAnySetter() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TestObject2 obj = objectMapper.readValue("{\"name\":\"hello\",\"id\":100}", TestObject2.class);
        assertEquals("hello", obj.name);
        assertEquals(100, obj.id);
    }
}
