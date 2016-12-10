package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestBase64 extends TestCase {
    public void test() throws IOException {
        Jsoniter iter = Jsoniter.parse("'YWJj'".replace('\'', '"'));
        assertEquals("abc", new String(iter.readBase64()));
    }
}
