package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class TestObject extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class TestObject1 {
        public String field1;
    }

    public void test_field() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'hello'}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject2 {
        @JsonIgnore
        private String field1;

        public String getField1() {
            return field1;
        }
    }

    public void test_getter() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'hello'}".replace('\'', '"'), baos.toString());
    }

    public void test_null() throws IOException {
        stream.writeVal(new TypeLiteral<TestObject2>() {
        }, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public static class TestObject3 {
    }

    public void test_empty_object() throws IOException {
        stream.writeVal(new TestObject3());
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject4 {
        public String field1;
    }

    public void test_null_field() throws IOException {
        TestObject4 obj = new TestObject4();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public static enum MyEnum {
        HELLO
    }

    public static class TestObject5 {
        public MyEnum field1;
    }

    public void test_enum() throws IOException {
        TestObject5 obj = new TestObject5();
        obj.field1 = MyEnum.HELLO;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':'HELLO'}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject6 {
        public int[] field1;
    }

    public void test_array_field() throws IOException {
        TestObject6 obj = new TestObject6();
        obj.field1 = new int[]{1, 2, 3};
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":[1,2,3]}", baos.toString());
    }

    public void test_array_field_is_null() throws IOException {
        TestObject6 obj = new TestObject6();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{}", baos.toString());
    }

    public static class TestObject7 {
        private int[] field1;

        @JsonProperty(omitNull = false)
        public int[] getField1() {
            return field1;
        }
    }

    public void test_array_field_is_null_via_getter() throws IOException {
        TestObject7 obj = new TestObject7();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":null}", baos.toString());
    }

    public static class TestObject8 {
        @JsonProperty(nullable = false)
        public String[] field1;
    }

    public void test_not_nullable() {
        TestObject8 obj = new TestObject8();
        obj.field1 = new String[]{"hello"};
        Config config = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        assertEquals("{\"field1\":[\"hello\"]}",
                JsonStream.serialize(config, obj));
        try {
            JsonStream.serialize(config, new TestObject8());
            fail();
        } catch (NullPointerException e) {
        }
    }

    public static class TestObject9 {
        @JsonProperty(collectionValueNullable = false)
        public String[] field1;
        @JsonProperty(collectionValueNullable = false)
        public List<String> field2;
        @JsonProperty(collectionValueNullable = false)
        public Set<String> field3;
        @JsonProperty(collectionValueNullable = false)
        public Map<String, String> field4;
    }

    public void test_collection_value_not_nullable() {
        TestObject9 obj = new TestObject9();
        obj.field1 = new String[]{"hello"};
        assertEquals("{\"field1\":[\"hello\"]}", JsonStream.serialize(obj));

        Config config = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        obj = new TestObject9();
        obj.field1 = new String[]{null};
        try {
            JsonStream.serialize(config, obj);
            fail();
        } catch (NullPointerException e) {
        }

        obj = new TestObject9();
        obj.field2 = new ArrayList();
        obj.field2.add(null);
        try {
            JsonStream.serialize(config, obj);
            fail();
        } catch (NullPointerException e) {
        }

        obj = new TestObject9();
        obj.field3 = new HashSet<String>();
        obj.field3.add(null);
        try {
            JsonStream.serialize(config, obj);
            fail();
        } catch (NullPointerException e) {
        }

        obj = new TestObject9();
        obj.field4 = new HashMap<String, String>();
        obj.field4.put("hello", null);
        try {
            JsonStream.serialize(config, obj);
            fail();
        } catch (NullPointerException e) {
        }
    }

    public static class TestObject10 {
        @JsonProperty(omitNull = false)
        public String field1;
    }

    public void test_not_omit_null() {
        assertEquals("{\"field1\":null}", JsonStream.serialize(new TestObject10()));
    }

    public static class TestObject11 {
        public String field1;
        public String field2;
        @JsonProperty(nullable = false)
        public Integer field3;
    }

    public void test_omit_null() {
        assertEquals("{\"field3\":null}", JsonStream.serialize(new TestObject11()));
        TestObject11 obj = new TestObject11();
        obj.field1 = "hello";
        assertEquals("{\"field1\":\"hello\",\"field3\":null}", JsonStream.serialize(obj));
        obj = new TestObject11();
        obj.field2 = "hello";
        assertEquals("{\"field2\":\"hello\",\"field3\":null}", JsonStream.serialize(obj));
        obj = new TestObject11();
        obj.field3 = 3;
        assertEquals("{\"field3\":3}", JsonStream.serialize(obj));
    }


    public static class TestObject12 {
        public int field1;

        public int getField1() {
            return field1;
        }
    }

    public void test_name_conflict() throws IOException {
        TestObject12 obj = new TestObject12();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":0}", baos.toString());
    }

    private static class TestObject13 {
    }

    public void test_private_class() {
        EncodingMode encodingMode = JsoniterSpi.getCurrentConfig().encodingMode();
        if (EncodingMode.REFLECTION_MODE.equals(encodingMode)) {
            return;
        }
        try {
            JsonStream.serialize(new TestObject13());
            fail("should throw JsonException");
        } catch (JsonException e) {

        }
    }

    public static class TestObject14 {
        @JsonProperty(nullable = true, omitNull = true)
        public String field1;
        @JsonProperty(nullable = false)
        public String field2;
        @JsonProperty(nullable = true, omitNull = false)
        public String field3;
    }

    public void test_indention() {
        Config dynamicCfg = new Config.Builder()
                .indentionStep(2)
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        TestObject14 obj = new TestObject14();
        obj.field1 = "1";
        obj.field2 = "2";
        String output = JsonStream.serialize(dynamicCfg, obj);
        assertEquals("{\n" +
                "  \"field1\": \"1\",\n" +
                "  \"field2\": \"2\",\n" +
                "  \"field3\": null\n" +
                "}", output);
        Config reflectionCfg = new Config.Builder()
                .indentionStep(2)
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .build();
        output = JsonStream.serialize(reflectionCfg, obj);
        assertEquals("{\n" +
                "  \"field1\": \"1\",\n" +
                "  \"field2\": \"2\",\n" +
                "  \"field3\": null\n" +
                "}", output);
    }

    public static class TestObject15 {
        public Integer i1;
        public Integer i2;
    }

    public void test_indention_with_empty_object() {
        Config config = JsoniterSpi.getCurrentConfig().copyBuilder()
                .indentionStep(2)
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .build();
        assertEquals("{}", JsonStream.serialize(config, new TestObject15()));
        config = JsoniterSpi.getCurrentConfig().copyBuilder()
                .indentionStep(2)
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .build();
        assertEquals("{}", JsonStream.serialize(config, new TestObject15()));
    }

    public static class TestObject16 {
        @JsonProperty(omitNull = false)
        public Integer i;
    }

    public void test_missing_notFirst() {
        Config cfg = JsoniterSpi.getCurrentConfig().copyBuilder()
            .indentionStep(2)
            .encodingMode(EncodingMode.DYNAMIC_MODE)
            .build();
        assertEquals("{\n" +
                "  \"i\": null\n" +
                "}", JsonStream.serialize(cfg, new TestObject16()));
    }
}
