package com.jsoniter;

import com.jsoniter.annotation.*;
import com.jsoniter.any.Any;
import com.jsoniter.fuzzy.StringIntDecoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestAnnotation extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
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

    public static class TestObject8 {
        @JsonCreator
        public TestObject8(@JsonProperty(required = true) int param1) {

        }
    }

    public void skip_missing_ctor_arg() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        try {
            iter.read(TestObject8.class);
            fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
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

    public interface TestObject18Interface<A> {
        void setHello(A val);
    }

    public static class TestObject18 implements TestObject18Interface<Integer> {

        public int _val;

        @Override
        public void setHello(Integer val) {
            _val = val;
        }
    }

    public void test_inherited_setter_is_not_duplicate() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"hello\":1}");
        TestObject18 obj = iter.read(TestObject18.class);
        assertNotNull(obj);
        assertEquals(1, obj._val);
    }
}
