package com.jsoniter.any;

import junit.framework.TestCase;

public class TestLong extends TestCase {
    public void test_to_string_should_trim() {
        Any any = Any.lazyLong(" 1000".getBytes(), 0, " 1000".length());
        assertEquals("1000", any.toString());
    }
}
