package com.jsoniter.output;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import junit.framework.TestCase;

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
}
