package com.jsoniter.output;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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

    public static enum EnumKey {
        KeyA, KeyB
    }

    public void test_enum_key() throws IOException {
        HashMap<EnumKey, Object> obj = new HashMap<EnumKey, Object>();
        obj.put(EnumKey.KeyA, null);
        stream.writeVal(new TypeLiteral<Map<EnumKey, Object>>() {
        }, obj);
        stream.close();
        assertEquals("{\"KeyA\":null}", baos.toString());
    }

    public static class TestObject1 {
        public int Field;
    }

    public void test_MapKeyCodec() {
        JsoniterSpi.registerMapKeyEncoder(TestObject1.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                TestObject1 mapKey = (TestObject1) obj;
                stream.writeVal(String.valueOf(mapKey.Field));
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

    public void test_int_as_map_key() {
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "2");
        assertEquals("{\"1\":\"2\"}", JsonStream.serialize(new TypeLiteral<Map<Integer, String>>() {
        }, m));
    }

    public void test_object_key() {
        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        m.put(1, 2);
        assertEquals("{\"1\":2}", JsonStream.serialize(m));
    }

    public void test_multiple_keys() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("destination", "test_destination_value");
        map.put("amount", new BigDecimal("0.0000101101"));
        map.put("password", "test_pass");
        final String serialized = JsonStream.serialize(map);
        assertEquals(-1, serialized.indexOf("::"));
    }
}
