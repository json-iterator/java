package com.jsoniter;

import com.jsoniter.annotation.jsoniter.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotation extends TestCase {
    public void test() throws IOException {
        JsoniterAnnotationSupport.enable();
        Jsoniter iter = Jsoniter.parse("{'field-1': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(100, obj.field1);
    }
}
