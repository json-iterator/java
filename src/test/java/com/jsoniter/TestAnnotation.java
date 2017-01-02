package com.jsoniter;

import com.jsoniter.annotation.*;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestAnnotation extends TestCase {

    static {
        JsoniterAnnotationSupport.enable();
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class TestObject1 {
        @JsonProperty("field-1")
        public int field1;

        @JsonIgnore
        public int field2;
    }

    public void test_rename() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field-1': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(100, obj.field1);
    }

    public void test_ignore() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(0, obj.field2);
    }

    public static class TestObject2 {
        private int field1;

        @JsonCreator
        public TestObject2(@JsonProperty("field1") int field1) {
            this.field1 = field1;
        }
    }

    public void test_ctor() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject2 obj = iter.read(TestObject2.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject3 {
        public int field1;

        @JsonCreator
        private TestObject3() {
        }
    }

    public void test_private_ctor() throws IOException {
        JsoniterSpi.registerTypeDecoder(TestObject3.class, ReflectionDecoderFactory.create(TestObject3.class));
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject3 obj = iter.read(TestObject3.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject4 {

        private int field1;

        private TestObject4() {
        }

        @JsonCreator
        public static TestObject4 createObject(@JsonProperty(value = "field1") int field1) {
            TestObject4 obj = new TestObject4();
            obj.field1 = field1;
            return obj;
        }
    }

    public void test_static_factory() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject4 obj = iter.read(TestObject4.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject5 {
        private int field1;

        public void setField1(int field1) {
            this.field1 = field1;
        }
    }

    public void test_single_param_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject5 obj = iter.read(TestObject5.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject6 {

        private int field1;

        @JsonWrapper
        public void initialize(@JsonProperty("field1") int field1) {
            this.field1 = field1;
        }
    }

    public void test_multi_param_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject6 obj = iter.read(TestObject6.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject7 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestObject7 obj = iter.read(TestObject7.class);
        assertEquals(Arrays.asList("field1"), obj.missingProperties);
    }

    public static class TestObject8 {
        @JsonCreator
        public TestObject8(@JsonProperty(required = true) int param1) {

        }
    }

    public void test_missing_ctor_arg() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        try {
            iter.read(TestObject8.class);
            fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    @JsonObject(asExtraForUnknownProperties = true)
    public static class TestObject9 {
        @JsonExtraProperties
        public Map<String, Any> extraProperties;
    }

    public void test_extra_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        TestObject9 obj = iter.read(TestObject9.class);
        assertEquals(100, obj.extraProperties.get("field1").toInt());
    }

    public static class TestObject10 {
        @JsonProperty(decoder = Decoder.StringIntDecoder.class)
        public int field1;
    }

    public void test_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestObject10 obj = iter.read(TestObject10.class);
        assertEquals(100, obj.field1);
    }

    public static class TestObject11 {
        @JsonProperty(decoder = Decoder.StringIntDecoder.class)
        public Integer field1;
    }

    public void test_integer_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestObject11 obj = iter.read(TestObject11.class);
        assertEquals(Integer.valueOf(100), obj.field1);
    }

    public static class TestObject12 {
        @JsonProperty(from = {"field_1", "field-1"})
        public int field1;
    }

    public void test_bind_from_multiple_names() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field-1\": 100, \"field-1\": 101}");
        TestObject12 obj = iter.read(TestObject12.class);
        assertEquals(101, obj.field1);
    }

    @JsonObject(asExtraForUnknownProperties = true)
    public static class TestObject13 {
    }

    public void test_unknown_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field-1\": 100, \"field-1\": 101}");
        try {
            iter.read(TestObject13.class);
            fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    public static class TestObject14 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties_not_missing() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        TestObject14 obj = iter.read(TestObject14.class);
        assertNull(obj.missingProperties);
        assertEquals(100, obj.field1);
    }

    @JsonObject(unknownPropertiesBlacklist = {"field1"})
    public static class TestObject15 {
    }

    public void test_unknown_properties_blacklist() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        try {
            iter.read(TestObject15.class);
            fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    public static class TestObject16 {
        @JsonProperty(implementation = LinkedList.class)
        public List<Integer> values;
    }

    public void test_specify_property() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"values\": [100]}");
        TestObject16 obj = iter.read(TestObject16.class);
        assertEquals(Arrays.asList(100), obj.values);
        assertEquals(LinkedList.class, obj.values.getClass());
    }

    public static class TestObject17 {
        public int field1;

        public void setField1(int field1) {
            this.field1 = field1;
        }

        @JsonCreator
        public void initialize(@JsonProperty("field1") int field1) {

        }
    }

    public void test_name_conflict() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(TestObject17.class));
    }
}
