package com.jsoniter;

import com.jsoniter.spi.JsonException;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestInteger extends TestCase {

    private boolean isStreaming;

    public void test_positive_negative() throws IOException {
        assertEquals(4321, parseInt("4321"));
        assertEquals(-4321, parseInt("-4321"));
    }

    public void test_max_min_int() throws IOException {
        assertEquals(Integer.MAX_VALUE, parseInt(Integer.toString(Integer.MAX_VALUE)));
        assertEquals(Integer.MIN_VALUE, parseInt(Integer.toString(Integer.MIN_VALUE)));
    }

    public void test_large_number() throws IOException {
        try {
            parseInt("123456789123456789");
            fail();
        } catch (JsonException e) {
        }
    }

    @Category(StreamingCategory.class)
    public void test_streaming() throws IOException {
        isStreaming = true;
        test_positive_negative();
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
}
