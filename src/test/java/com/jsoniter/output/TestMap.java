package com.jsoniter.output;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.MapKeyEncoder;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMap extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void test() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("hello", "world");
        stream.writeVal(map);
        stream.close();
        assertEquals("{'hello':'world'}".replace('\'', '"'), baos.toString());
    }

    public void test_empty() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        stream.writeVal(map);
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public void test_null() throws IOException {
        stream.writeVal(new TypeLiteral<HashMap>() {
        }, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_value_is_null() throws IOException {
        HashMap<String, int[]> obj = new HashMap<String, int[]>();
        obj.put("hello", null);
        stream.writeVal(new TypeLiteral<Map<String, int[]>>() {
        }, obj);
        stream.close();
        assertEquals("{\"hello\":null}", baos.toString());
    }

    public void test_integer_key() throws IOException {
        HashMap<Integer, Object> obj = new HashMap<Integer, Object>();
        obj.put(100, null);
        stream.writeVal(new TypeLiteral<Map<Integer, Object>>() {
        }, obj);
        stream.close();
        assertEquals("{\"100\":null}", baos.toString());
    }

    public static class TestObject1 {
        public int Field;
    }

    public void test_MapKeyCodec() {
        JsoniterSpi.registerMapKeyEncoder(TestObject1.class, new MapKeyEncoder() {
            @Override
            public String encode(Object mapKey) {
                TestObject1 obj = (TestObject1) mapKey;
                return String.valueOf(obj.Field);
            }
        });
        HashMap<TestObject1, Object> obj = new HashMap<TestObject1, Object>();
        obj.put(new TestObject1(), null);
        String output = JsonStream.serialize(new TypeLiteral<Map<TestObject1, Object>>() {
        }, obj);
        assertEquals("{\"0\":null}", output);
    }

    public void skip_indention() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("field1", "1");
        map.put("field2", "2");
        Config dynamicCfg = new Config.Builder()
                .indentionStep(2)
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        String output = JsonStream.serialize(dynamicCfg, map);
        assertEquals("{\n" +
                "  \"field1\": \"1\",\n" +
                "  \"field2\": \"2\"\n" +
                "}", output);
        Config reflectionCfg = new Config.Builder()
                .indentionStep(2)
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .build();
        output = JsonStream.serialize(reflectionCfg, map);
        assertEquals("{\n" +
                "  \"field1\": \"1\",\n" +
                "  \"field2\": \"2\"\n" +
                "}", output);
    }

    public void test_indention_with_empty_map() {
        Config config = JsoniterSpi.getCurrentConfig().copyBuilder()
                .indentionStep(2)
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .build();
        assertEquals("{}", JsonStream.serialize(config, new HashMap<String, String>()));
        config = JsoniterSpi.getCurrentConfig().copyBuilder()
                .indentionStep(2)
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        assertEquals("{}", JsonStream.serialize(config, new HashMap<String, String>()));
    }
}
