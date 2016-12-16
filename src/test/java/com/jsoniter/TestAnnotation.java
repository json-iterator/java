package com.jsoniter;

import com.jsoniter.annotation.jsoniter.JsonIgnore;
import com.jsoniter.annotation.jsoniter.JsonProperty;
import com.jsoniter.annotation.jsoniter.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotation extends TestCase {

    public static class AnnotatedObject {
        @JsonProperty("field-1")
        public int field1;

        @JsonIgnore
        public int field2;
    }
    public void test_rename() throws IOException {
        JsoniterAnnotationSupport.enable();
        JsonIterator iter = JsonIterator.parse("{'field-1': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(100, obj.field1);
    }
    public void test_ignore() throws IOException {
        JsoniterAnnotationSupport.enable();
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(0, obj.field2);
    }
}
