package com.jsoniter.output;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jsoniter.extra.JacksonCompatibilityMode;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class TestJackson extends TestCase {

    private ObjectMapper objectMapper;

    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    public static class TestObject1 {
        @JsonAnyGetter
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_JsonAnyGetter() throws JsonProcessingException {
        String output = objectMapper.writeValueAsString(new TestObject1());
        assertEquals("{\"100\":\"hello\"}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), new TestObject1());
        assertEquals("{\"100\":\"hello\"}", output);
    }

    public static class TestObject2 {
        @JsonProperty("field-1")
        public String field1;
    }

    public void test_JsonProperty() throws JsonProcessingException {
        TestObject2 obj = new TestObject2();
        obj.field1 = "hello";
        String output = objectMapper.writeValueAsString(obj);
        assertEquals("{\"field-1\":\"hello\"}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), obj);
        assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject3 {
        @JsonIgnore
        public String field1;
    }

    public void test_JsonIgnore() throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        TestObject3 obj = new TestObject3();
        obj.field1 = "hello";
        String output = objectMapper.writeValueAsString(obj);
        assertEquals("{}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), obj);
        assertEquals("{}", output);
    }
}
