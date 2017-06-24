package com.jsoniter.output;

import com.jsoniter.spi.Config;
import junit.framework.TestCase;

import java.util.HashSet;

public class TestCollection extends TestCase {

    public void test_indention() {
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1\n" +
                "]", JsonStream.serialize(cfg, set));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1\n" +
                "]", JsonStream.serialize(cfg, set));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new HashSet<Integer>()));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new HashSet<Integer>()));
    }
}
