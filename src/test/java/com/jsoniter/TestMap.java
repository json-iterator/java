package com.jsoniter;

import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMap extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public void test_object_key() throws IOException {
        Map<Object, Object> map = JsonIterator.deserialize("{\"中文\":null}", new TypeLiteral<Map<Object, Object>>() {
        });
        assertEquals(new HashMap<Object, Object>() {{
            put("中文", null);
        }}, map);
    }

    public void test_string_key() throws IOException {
        Map<String, Object> map = JsonIterator.deserialize("{\"中文\":null}", new TypeLiteral<Map<String, Object>>() {
        });
        assertEquals(new HashMap<String, Object>() {{
            put("中文", null);
        }}, map);
    }

    public void test_integer_key() throws IOException {
        Map<Integer, Object> map = JsonIterator.deserialize("{\"100\":null}", new TypeLiteral<Map<Integer, Object>>() {
        });
        assertEquals(new HashMap<Integer, Object>() {{
            put(100, null);
        }}, map);
    }
}
