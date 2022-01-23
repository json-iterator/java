package com.jsoniter.output;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.Config.Builder;
import com.jsoniter.spi.JsoniterSpi;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;

public class TestString extends TestCase {

    public static final String UTF8_GREETING = "Привет čau 你好 ~";

    public void test_unicode() {
        String output = JsonStream.serialize(new Config.Builder().escapeUnicode(false).build(), "中文");
        assertEquals("\"中文\"", output);
    }
    public void test_unicode_tilde() {
        final String tilde = "~";
        String output = JsonStream.serialize(new Config.Builder().escapeUnicode(false).build(), tilde);
        assertEquals("\""+tilde+"\"", output);
    }
    public void test_escape_unicode() {
        final Config config = new Builder().escapeUnicode(false).build();

        assertEquals("\""+UTF8_GREETING+"\"", JsonStream.serialize(config, UTF8_GREETING));
        assertEquals("\""+UTF8_GREETING+"\"", JsonStream.serialize(config.escapeUnicode(), UTF8_GREETING.getClass(), UTF8_GREETING));
    }
    public void test_escape_control_character() {
        String output = JsonStream.serialize(new String(new byte[]{0}));
        assertEquals("\"\\u0000\"", output);
    }
    public void test_serialize_into_output_stream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean escapeUnicode = JsoniterSpi.getCurrentConfig().escapeUnicode();
        JsoniterSpi.setCurrentConfig(JsoniterSpi.getCurrentConfig().copyBuilder().escapeUnicode(false).build());
        JsonStream.serialize(String.class, UTF8_GREETING, baos);
        JsoniterSpi.setCurrentConfig(JsoniterSpi.getCurrentConfig().copyBuilder().escapeUnicode(escapeUnicode).build());
        assertEquals("\"" + UTF8_GREETING + "\"", baos.toString());
    }
}
