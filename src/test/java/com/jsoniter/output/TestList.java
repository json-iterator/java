package com.jsoniter.output;

import com.jsoniter.spi.Config;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class TestList extends TestCase {

    public void test_indention() {
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1,\n" +
                "  2\n" +
                "]", JsonStream.serialize(cfg, Arrays.asList(1, 2)));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1,\n" +
                "  2\n" +
                "]", JsonStream.serialize(cfg, Arrays.asList(1, 2)));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new ArrayList<Integer>()));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new ArrayList<Integer>()));
    }
}
