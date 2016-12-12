package com.jsoniter;

import com.jsoniter.annotation.jsoniter.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotation extends TestCase {
    public void test_rename() throws IOException {
        JsoniterAnnotationSupport.enable();
        Jsoniter iter = Jsoniter.parse("{'field-1': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(100, obj.field1);
    }
    public void test_ignore() throws IOException {
        JsoniterAnnotationSupport.enable();
        Jsoniter iter = Jsoniter.parse("{'field2': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(0, obj.field2);
    }
}
