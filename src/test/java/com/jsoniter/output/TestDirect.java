package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestDirect extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void test_string() throws IOException {
        stream.writeVal("hello");
        stream.close();
        assertEquals("'hello'".replace('\'', '"'), baos.toString());
    }

    public void test_int() throws IOException {
        stream.writeVal(100);
        stream.close();
        assertEquals("100".replace('\'', '"'), baos.toString());
    }

    public void test_boolean() throws IOException {
        stream.writeVal(true);
        stream.writeVal(false);
        stream.close();
        assertEquals("truefalse".replace('\'', '"'), baos.toString());
    }

    public void test_array() throws IOException {
        stream.startArray();
        stream.writeVal("hello");
        stream.writeMore();
        stream.endArray();
        stream.close();
        assertEquals("['hello']".replace('\'', '"'), baos.toString());
    }

    public void test_object() throws IOException {
        stream.startObject();
        stream.writeField("hello");
        stream.writeVal("world");
        stream.writeMore();
        stream.endObject();
        stream.close();
        assertEquals("{'hello':'world'}".replace('\'', '"'), baos.toString());
    }
}
