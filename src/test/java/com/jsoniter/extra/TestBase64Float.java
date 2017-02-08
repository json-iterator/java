package com.jsoniter.extra;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

public class TestBase64Float extends TestCase {

    static {
        Base64FloatSupport.enableEncodersAndDecoders();
    }

    public void test_Double() {
        String json = JsonStream.serialize(0.123456789d);
        assertEquals(0.123456789d, JsonIterator.deserialize(json, Double.class));
    }

    public void test_double() {
        String json = JsonStream.serialize(new double[]{0.123456789d});
        assertEquals(0.123456789d, JsonIterator.deserialize(json, double[].class)[0]);
    }

    public void test_Float() {
        String json = JsonStream.serialize(0.12345678f);
        assertEquals(0.12345678f, JsonIterator.deserialize(json, Float.class));
    }

    public void test_float() {
        String json = JsonStream.serialize(new float[]{0.12345678f});
        assertEquals(0.12345678f, JsonIterator.deserialize(json, float[].class)[0]);
    }
}
