package com.jsoniter.extra;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

public class TestBinaryFloat extends TestCase {
    static {
        BinaryFloatSupport.enable();
    }

    public void test_Double() {
        String json = JsonStream.serialize(0.123456789d);
        assertEquals(0.123456789d, JsonIterator.deserialize(json, Double.class));
    }

    public void test_double() {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        String json = JsonStream.serialize(new double[]{0.123456789d});
        assertEquals(0.123456789d, JsonIterator.deserialize(json, double[].class)[0]);
    }
}
