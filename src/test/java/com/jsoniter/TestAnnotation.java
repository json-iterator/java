package com.jsoniter;

import com.jsoniter.annotation.*;
import com.jsoniter.spi.ExtensionManager;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestAnnotation extends TestCase {

    static {
        JsoniterAnnotationSupport.enable();
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class AnnotatedObject {
        @JsonProperty("field-1")
        public int field1;

        @JsonIgnore
        public int field2;
    }

    public void test_rename() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field-1': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(100, obj.field1);
    }

    public void test_ignore() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        AnnotatedObject obj = iter.read(AnnotatedObject.class);
        assertEquals(0, obj.field2);
    }

    public static class NoDefaultCtor {
        private int field1;

        @JsonCreator
        public NoDefaultCtor(@JsonProperty("field1") int field1) {
            this.field1 = field1;
        }
    }

    public void test_ctor() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        NoDefaultCtor obj = iter.read(NoDefaultCtor.class);
        assertEquals(100, obj.field1);
    }

    public static class StaticFactory {

        private int field1;

        private StaticFactory() {
        }

        @JsonCreator
        public static StaticFactory createObject(@JsonProperty(value = "field1") int field1) {
            StaticFactory obj = new StaticFactory();
            obj.field1 = field1;
            return obj;
        }
    }

    public void test_static_factory() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        StaticFactory obj = iter.read(StaticFactory.class);
        assertEquals(100, obj.field1);
    }

    public static class StaticFactory2 {

        private int _field1;

        private StaticFactory2() {
        }

        @JsonCreator
        public static StaticFactory2 createObject(@JsonProperty(value = "field1") int field1) {
            StaticFactory2 obj = new StaticFactory2();
            obj._field1 = field1;
            return obj;
        }
    }

    public void test_static_factory_with_reflection() throws IOException {
        ExtensionManager.registerTypeDecoder(StaticFactory2.class, new ReflectionObjectDecoder(StaticFactory2.class));
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        StaticFactory2 obj = iter.read(StaticFactory2.class);
        assertEquals(100, obj._field1);
    }

    public static class WithSingleParamSetter {
        private int field1;

        public void setField1(int field1) {
            this.field1 = field1;
        }
    }

    public void test_single_param_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        WithSingleParamSetter obj = iter.read(WithSingleParamSetter.class);
        assertEquals(100, obj.field1);
    }

    public static class WithMultiParamSetter {

        private int field1;

        @JsonSetter
        public void initialize(@JsonProperty("field1") int field1) {
            this.field1 = field1;
        }
    }

    public void test_multi_param_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        WithMultiParamSetter obj = iter.read(WithMultiParamSetter.class);
        assertEquals(100, obj.field1);
    }

    public static class WithRequiredProperties {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        WithRequiredProperties obj = iter.read(WithRequiredProperties.class);
        assertEquals(Arrays.asList("field1"), obj.missingProperties);
    }

    public static class MissingCtorArg {
        @JsonCreator
        public MissingCtorArg(@JsonProperty(required = true)int param1) {

        }
    }

    public void test_missing_ctor_arg() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        try {
            iter.read(MissingCtorArg.class);
            fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    @JsonObject(asExtraForUnknownProperties = true)
    public static class WithExtraProperties {
        @JsonExtraProperties
        public Any extraProperties;
    }

    public void test_extra_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        WithExtraProperties obj = iter.read(WithExtraProperties.class);
        assertEquals(100, obj.extraProperties.toInt("field1"));
    }
}
