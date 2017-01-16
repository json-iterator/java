package com.jsoniter;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestInteger extends TestCase {

    private boolean isStreaming;

    public void test_positive_negative_int() throws IOException {
        assertEquals(4321, parseInt("4321"));
        assertEquals(-4321, parseInt("-4321"));
    }

    public void test_positive_negative_long() throws IOException {
        assertEquals(4321L, parseLong("4321"));
        assertEquals(-4321L, parseLong("-4321"));
    }

    public void test_max_min_int() throws IOException {
        assertEquals(Integer.MAX_VALUE, parseInt(Integer.toString(Integer.MAX_VALUE)));
        assertEquals(Integer.MAX_VALUE - 1, parseInt(Integer.toString(Integer.MAX_VALUE - 1)));
        assertEquals(Integer.MIN_VALUE + 1, parseInt(Integer.toString(Integer.MIN_VALUE + 1)));
        assertEquals(Integer.MIN_VALUE, parseInt(Integer.toString(Integer.MIN_VALUE)));
    }

    public void test_max_min_long() throws IOException {
        assertEquals(Long.MAX_VALUE, parseLong(Long.toString(Long.MAX_VALUE)));
        assertEquals(Long.MAX_VALUE - 1, parseLong(Long.toString(Long.MAX_VALUE - 1)));
        assertEquals(Long.MIN_VALUE + 1, parseLong(Long.toString(Long.MIN_VALUE + 1)));
        assertEquals(Long.MIN_VALUE, parseLong(Long.toString(Long.MIN_VALUE)));
    }

    public void test_large_number() throws IOException {
        try {
            JsonIterator.deserialize(Integer.toString(Integer.MIN_VALUE) + "1", Integer.class);
            fail();
        } catch (JsonException e) {
        }
        try {
            JsonIterator.deserialize(Long.toString(Long.MAX_VALUE) + "1", Long.class);
            fail();
        } catch (JsonException e) {
        }
    }

    @Category(StreamingCategory.class)
    public void test_streaming() throws IOException {
        isStreaming = true;
        test_positive_negative_int();
        test_positive_negative_long();
        test_max_min_int();
        test_max_min_long();
        test_large_number();
    }

    private int parseInt(String input) throws IOException {
        if (isStreaming) {
            JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream(input.getBytes()), 2);
            return iter.readInt();
        } else {
            JsonIterator iter = JsonIterator.parse(input);
            return iter.readInt();
        }
    }

    private long parseLong(String input) throws IOException {
        if (isStreaming) {
            JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream(input.getBytes()), 2);
            return iter.readLong();
        } else {
            JsonIterator iter = JsonIterator.parse(input);
            return iter.readLong();
        }
    }
}
