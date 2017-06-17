package com.jsoniter.output;

import com.jsoniter.spi.Config;
import junit.framework.TestCase;

public class TestString extends TestCase {
    public void test_unicode() {
        String output = JsonStream.serialize(new Config.Builder().escapeUnicode(false).build(), "中文");
        assertEquals("\"中文\"", output);
    }
}
