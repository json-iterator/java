package com.jsoniter.any;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;

public class TestLong extends TestCase {
    public void test_to_string_should_trim() {
        Any any = Any.lazyLong(" 1000".getBytes(), 0, " 1000".length());
        assertEquals("1000", any.toString());
    }

    public void test_should_fail_with_leading_zero() {
        byte[] bytes = "01".getBytes();
        Any any = Any.lazyLong(bytes, 0, bytes.length);
        try {
            any.toLong();
            fail("This should fail.");
        } catch (JsonException e) {

        }
    }

    public void test_should_work_with_zero() {
        byte[] bytes = "0".getBytes();
        Any any = Any.lazyLong(bytes, 0, bytes.length);
        assertEquals(0L, any.toLong());
    }
}
