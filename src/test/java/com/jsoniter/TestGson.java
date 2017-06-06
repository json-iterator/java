package com.jsoniter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jsoniter.extra.GsonCompatibilityMode;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.TimeZone;

public class TestGson extends TestCase {

    public static class TestObject1 {
        @SerializedName("field-1")
        public String field1;
    }

    public void test_SerializedName() {
        Gson gson = new Gson();
        TestObject1 obj = gson.fromJson("{\"field-1\":\"hello\"}", TestObject1.class);
        assertEquals("hello", obj.field1);
        obj = JsonIterator.deserialize(new GsonCompatibilityMode.Builder().build(),
                "{\"field-1\":\"hello\"}", TestObject1.class);
        assertEquals("hello", obj.field1);
    }

    public static class TestObject2 {
        @Expose(deserialize = false)
        public String field1;
    }

    public void test_Expose() {
        // test if the iterator reuse will keep right config cache
        JsonIterator.deserialize(new GsonCompatibilityMode.Builder().build(),
                "{\"field-1\":\"hello\"}", TestObject2.class);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestObject2 obj = gson.fromJson("{\"field1\":\"hello\"}", TestObject2.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize(new GsonCompatibilityMode.Builder()
                        .excludeFieldsWithoutExposeAnnotation().build(),
                "{\"field1\":\"hello\"}", TestObject2.class);
        assertNull(obj.field1);
    }

    public void test_setDateFormat_no_op() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder().create();
            Date obj = gson.fromJson("\"Jan 1, 1970, 12:00:00 AM\"", Date.class);
            assertEquals(0, obj.getTime());
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                    .build();
            obj = JsonIterator.deserialize(config, "\"Jan 1, 1970, 12:00:00 AM\"", Date.class);
            assertEquals(0, obj.getTime());
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public void test_setDateFormat_format() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder().setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z").create();
            Date obj = gson.fromJson("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", Date.class);
            assertEquals(0, obj.getTime());
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                    .setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z")
                    .build();
            obj = JsonIterator.deserialize(config, "\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", Date.class);
            assertEquals(0, obj.getTime());
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public static class TestObject3 {
        public String field1;
    }

    public void test_setFieldNamingStrategy() {
        FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return "_" + f.getName();
            }
        };
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(fieldNamingStrategy)
                .create();
        TestObject3 obj = gson.fromJson("{\"_field1\":\"hello\"}", TestObject3.class);
        assertEquals("hello", obj.field1);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setFieldNamingStrategy(fieldNamingStrategy)
                .build();
        obj = JsonIterator.deserialize(config, "{\"_field1\":\"hello\"}", TestObject3.class);
        assertEquals("hello", obj.field1);
    }

    public void test_setFieldNamingPolicy() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        TestObject3 obj = gson.fromJson("{\"Field1\":\"hello\"}", TestObject3.class);
        assertEquals("hello", obj.field1);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .build();
        obj = JsonIterator.deserialize(config, "{\"Field1\":\"hello\"}", TestObject3.class);
        assertEquals("hello", obj.field1);
    }
}
