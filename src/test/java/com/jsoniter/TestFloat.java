package com.jsoniter;

import com.jsoniter.Jsoniter;
import junit.framework.TestCase;

import java.io.IOException;

public class TestFloat extends TestCase {

    public void test_float() throws IOException {
        Jsoniter iter = Jsoniter.parse("1.1");
        assertEquals(1.1f, iter.readFloat());
    }

    public void test_double() throws IOException {
        Jsoniter iter = Jsoniter.parse("1.1");
        assertEquals(1.1, iter.readDouble());
    }
}
