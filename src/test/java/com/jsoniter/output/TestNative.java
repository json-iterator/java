package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestNative extends TestCase {

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

    public void test_escape() throws IOException {
        stream.writeVal("hel\nlo");
        stream.close();
        assertEquals("'hel\\nlo'".replace('\'', '"'), baos.toString());
    }

    public void test_utf8() throws IOException {
        stream.writeVal("中文");
        stream.close();
        assertEquals("\"\\u4e2d\\u6587\"", baos.toString());
    }

    public void test_int() throws IOException {
        stream.writeVal(100);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_negative_int() throws IOException {
        stream.writeVal(-100);
        stream.close();
        assertEquals("-100", baos.toString());
    }

    public void test_small_int() throws IOException {
        stream.writeVal(3);
        stream.close();
        assertEquals("3", baos.toString());
    }

    public void test_large_int() throws IOException {
        stream.writeVal(31415926);
        stream.close();
        assertEquals("31415926", baos.toString());
    }

    public void test_long() throws IOException {
        stream.writeVal(100L);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_negative_long() throws IOException {
        stream.writeVal(-100L);
        stream.close();
        assertEquals("-100", baos.toString());
    }

    public void test_boolean() throws IOException {
        stream.writeVal(true);
        stream.writeVal(false);
        stream.close();
        assertEquals("truefalse".replace('\'', '"'), baos.toString());
    }
}
