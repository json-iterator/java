package com.jsoniter.output;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Config;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestString extends TestCase {
    public void test_unicode() {
        String output = JsonStream.serialize(new Config.Builder().escapeUnicode(false).build(), "中文");
        assertEquals("\"中文\"", output);
    }

    public void test_escape_control_character() {
        String output = JsonStream.serialize(new String(new byte[]{0}));
        assertEquals("\"\\u0000\"", output);
    }

    public void test_encoding_different_than_default() throws NoSuchFieldException, IllegalAccessException {
        Charset defaultEncoding = Charset.defaultCharset();
        try {
            // Sets the default encoding
            setDefaultEncoding("US-ASCII");

            Any output = JsonIterator.deserialize("{\"name\":\"Thomas Müller\"}", StandardCharsets.UTF_8);
            assertEquals("{\"name\":\"Thomas Müller\"}", JsonStream.serialize(output, StandardCharsets.UTF_8));
        } finally {
            // Sets the default encoding  back to its real default
            setDefaultEncoding(defaultEncoding.name());
        }
    }

    private void setDefaultEncoding(String defaultEncoding) throws NoSuchFieldException, IllegalAccessException {
        // This block sets the Default Charset
        System.setProperty("file.encoding", defaultEncoding);
        // Unfortunately this "hack" is necessary (https://stackoverflow.com/questions/361975/setting-the-default-java-character-encoding)
        Field cs = Charset.class.getDeclaredField("defaultCharset");
        cs.setAccessible(true);
        cs.set(null, null);
    }
}
