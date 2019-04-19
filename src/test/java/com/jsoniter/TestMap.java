package com.jsoniter;

import com.jsoniter.extra.GsonCompatibilityMode;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
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

    public static enum EnumKey {
        KeyA, KeyB
    }

    public void test_enum_key() {
        Map<EnumKey, Object> map = JsonIterator.deserialize("{\"KeyA\":null}", new TypeLiteral<Map<EnumKey, Object>>() {
        });
        assertEquals(new HashMap<EnumKey, Object>() {{
            put(EnumKey.KeyA, null);
        }}, map);
    }

    public static class TestObject1 {
        public int Field;
    }

    public void test_MapKeyCodec() {
        JsoniterSpi.registerMapKeyDecoder(TestObject1.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                TestObject1 obj = new TestObject1();
                obj.Field = Integer.valueOf(iter.readString());
                return obj;
            }
        });
        Map<TestObject1, Object> map = JsonIterator.deserialize("{\"100\":null}", new TypeLiteral<Map<TestObject1, Object>>() {
        });
        ArrayList<TestObject1> keys = new ArrayList<TestObject1>(map.keySet());
        assertEquals(1, keys.size());
        assertEquals(100, keys.get(0).Field);
        // in new config
        map = JsonIterator.deserialize(
                new GsonCompatibilityMode.Builder().build(),
                "{\"100\":null}", new TypeLiteral<Map<TestObject1, Object>>() {
                });
        keys = new ArrayList<TestObject1>(map.keySet());
        assertEquals(1, keys.size());
        assertEquals(100, keys.get(0).Field);
    }
}
