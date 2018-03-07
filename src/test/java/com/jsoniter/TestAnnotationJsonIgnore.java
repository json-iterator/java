package com.jsoniter;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.DecodingMode;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import junit.framework.TestCase;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;

public class TestAnnotationJsonIgnore extends TestCase {

    public static class TestObject1 {
        @JsonIgnore
        public int field2;
    }

    public void test_ignore() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(0, obj.field2);
    }

    public static class TestObject2 {
        @JsonIgnore
        public Serializable field2;
    }

    public void test_ignore_no_constructor_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestObject2 obj = iter.read(TestObject2.class);
        assertNull(obj.field2);
    }

    public static class TestObject3 {
        String field1;
        @JsonIgnore
        ActionListener fieldXXX;

        @JsonCreator
        public TestObject3(@JsonProperty("field2") final String field) {
            field1 = null;
            fieldXXX = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("field2 is " + field);
                }
            };
        }

        @Override
        public String toString() {
            return "field1=" + field1 + ", field2=" + fieldXXX;
        }
    }

    public void test_json_ignore_with_creator() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field2\": \"test\"}");
        TestObject3 t = iter.read(TestObject3.class);
        assertNotNull(t.fieldXXX);
    }

    public static class TestObject4 {

        @JsonIgnore
        public String field1 = "Orange";
        private double field2 = 0.0;

        @Setter
        public void setField2(double d) {
            this.field2 = d;
        }
    }

    public void test_ignore_with_setter() throws IOException {
        // contract: If a parameter has annotated tag @JsonIgnore
        // it should not be possible to change the parameter using
        // a the json iterator
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"Apple\"}");
        TestObject4 t = iter.read(TestObject4.class);
        assertEquals("Orange",t.field1);
    }
}
