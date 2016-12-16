package com.jsoniter;

import com.jsoniter.annotation.*;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotation extends TestCase {

    static {
        JsoniterAnnotationSupport.enable();
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
        public static StaticFactory createObject(@JsonProperty("field1") int field1) {
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

    public static class WithSetter {

        private int field1;

        @JsonSetter
        public void initialize(@JsonProperty("field1") int field1) {
            this.field1 = field1;
        }
    }

    public void test_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        WithSetter obj = iter.read(WithSetter.class);
        assertEquals(100, obj.field1);
    }
}
