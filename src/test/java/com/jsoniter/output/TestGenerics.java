package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestGenerics extends TestCase {
    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public interface TestObject6Interface<A> {
        A getHello();
    }

    public static class TestObject6 implements TestObject6Interface<Integer> {
        public Integer getHello() {
            return 0;
        }
    }

    public void test_inherited_getter_is_not_duplicate() throws IOException {
        TestObject6 obj = new TestObject6();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"hello\":0}", baos.toString());
    }
}
