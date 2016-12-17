package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestArray extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream generator;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        generator = new JsonStream(baos, 4096);
    }

    public void test_gen_array() throws IOException {
        generator.writeVal(new String[] {"hello", "world"});
        generator.reset();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }
}
