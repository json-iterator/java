package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestDirect extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream generator;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        generator = new JsonStream(baos, 4096);
    }

    public void test_string() throws IOException {
        generator.writeVal("hello");
        generator.reset();
        assertEquals("'hello'".replace('\'', '"'), baos.toString());
    }

    public void test_int() throws IOException {
        generator.writeVal(100);
        generator.reset();
        assertEquals("100".replace('\'', '"'), baos.toString());
    }

    public void test_boolean() throws IOException {
        generator.writeVal(true);
        generator.writeVal(false);
        generator.reset();
        assertEquals("truefalse".replace('\'', '"'), baos.toString());
    }

    public void test_array() throws IOException {
        generator.startArray();
        generator.writeVal("hello");
        generator.writeMore();
        generator.endArray();
        generator.reset();
        assertEquals("['hello']".replace('\'', '"'), baos.toString());
    }

    public void test_object() throws IOException {
        generator.startObject();
        generator.writeField("hello");
        generator.writeVal("world");
        generator.writeMore();
        generator.endObject();
        generator.reset();
        assertEquals("{'hello':'world'}".replace('\'', '"'), baos.toString());
    }
}
