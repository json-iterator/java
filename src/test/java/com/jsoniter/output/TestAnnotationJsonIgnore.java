package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestAnnotationJsonIgnore extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        JsoniterAnnotationSupport.enable();
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void tearDown() {
        JsoniterAnnotationSupport.disable();
    }

    public static class TestObject1 {
        @JsonIgnore
        public int field1;
    }

    public void test_ignore() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = 100;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{}", baos.toString());
    }
}
