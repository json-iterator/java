package com.jsoniter.output;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.jsoniter.extra.GsonCompatibilityMode;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TestGson extends TestCase {

    public static class TestObject1 {
        @SerializedName("field-1")
        public String field1;
    }

    public void test_SerializedName_on_field() {
        Gson gson = new GsonBuilder().create();
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{\"field-1\":\"hello\"}", output);
        output = JsonStream.serialize(new GsonCompatibilityMode.Builder().build(), obj);
        assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject2 {
        @Expose(serialize = false)
        public String field1;
    }

    public void test_Expose() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestObject2 obj = new TestObject2();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{}", output);

        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .excludeFieldsWithoutExposeAnnotation().build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{}", output);
    }

    public static class TestObject3 {
        public String getField1() {
            return "hello";
        }
    }

    public void test_getter_should_be_ignored() {
        Gson gson = new GsonBuilder().create();
        TestObject3 obj = new TestObject3();
        String output = gson.toJson(obj);
        assertEquals("{}", output);
        output = JsonStream.serialize(new GsonCompatibilityMode.Builder().build(), obj);
        assertEquals("{}", output);
    }

    public static class TestObject4 {
        public String field1;
    }

    public void test_excludeFieldsWithoutExposeAnnotation() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestObject4 obj = new TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{}", output);

        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .excludeFieldsWithoutExposeAnnotation().build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{}", output);
    }

    public static class TestObject5 {
        public String field1;
        public int field2;
    }

    public void test_serializeNulls() {
        TestObject5 obj = new TestObject5();
        Gson gson = new GsonBuilder().create();
        String output = gson.toJson(obj);
        assertEquals("{\"field2\":0}", output);

        gson = new GsonBuilder().serializeNulls().create();
        output = gson.toJson(obj);
        assertEquals("{\"field1\":null,\"field2\":0}", output);

        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"field2\":0}", output);

        config = new GsonCompatibilityMode.Builder()
                .serializeNulls().build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"field1\":null,\"field2\":0}", output);
    }

//    public void test_setDateFormat_no_op() {
//        TimeZone orig = TimeZone.getDefault();
//        try {
//            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//            Gson gson = new GsonBuilder().create();
//            String output = gson.toJson(new Date(0));
//            assertEquals("\"Jan 1, 1970 12:00:00 AM\"", output);
//            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
//                    .build();
//            output = JsonStream.serialize(config, new Date(0));
//            assertEquals("\"Jan 1, 1970 12:00:00 AM\"", output);
//        } finally {
//            TimeZone.setDefault(orig);
//        }
//    }

    public void test_setDateFormat_with_style() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.LONG, DateFormat.LONG)
                    .create();
            String output = gson.toJson(new Date(0));
            assertEquals("\"January 1, 1970 12:00:00 AM UTC\"", output);
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                    .setDateFormat(DateFormat.LONG, DateFormat.LONG)
                    .build();
            output = JsonStream.serialize(config, new Date(0));
            assertEquals("\"January 1, 1970 12:00:00 AM UTC\"", output);
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public void test_setDateFormat_with_format() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder()
                    .setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z")
                    .create();
            String output = gson.toJson(new Date(0));
            assertEquals("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", output);
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                    .setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z")
                    .build();
            output = JsonStream.serialize(config, new Date(0));
            assertEquals("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", output);
        } finally {
            TimeZone.setDefault(orig);
        }
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
        TestObject4 obj = new TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{\"_field1\":\"hello\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setFieldNamingStrategy(fieldNamingStrategy)
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"_field1\":\"hello\"}", output);
    }

    public void test_setFieldNamingPolicy() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        TestObject4 obj = new TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{\"Field1\":\"hello\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"Field1\":\"hello\"}", output);
    }

    public void test_setPrettyPrinting() {
        if (JsoniterSpi.getCurrentConfig().encodingMode() != EncodingMode.REFLECTION_MODE) {
            return;
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        TestObject4 obj = new TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        assertEquals("{\n" +
                "  \"field1\": \"hello\"\n" +
                "}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setPrettyPrinting()
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\n" +
                "  \"field1\": \"hello\"\n" +
                "}", output);
    }

    public void test_disableHtmlEscaping_off() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        String output = gson.toJson("<html>中文</html>");
        assertEquals("\"<html>中文</html>\"", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .disableHtmlEscaping()
                .build();
        output = JsonStream.serialize(config, "<html>中文</html>");
        assertEquals("\"<html>中文</html>\"", output);
    }

    public void test_disableHtmlEscaping_on() {
        Gson gson = new GsonBuilder()
                .create();
        String output = gson.toJson("<html>&nbsp;</html>");
        assertEquals("\"\\u003chtml\\u003e\\u0026nbsp;\\u003c/html\\u003e\"", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .build();
        output = JsonStream.serialize(config, "<html>&nbsp;</html>");
        assertEquals("\"\\u003chtml\\u003e\\u0026nbsp;\\u003c/html\\u003e\"", output);
    }

    public static class TestObject6 {
        @Since(3.0)
        public String field1 = "field1";
        @Until(1.0)
        public String field2 = "field2";
        @Since(2.0)
        public String field3 = "field3";
        @Until(2.0)
        public String field4 = "field4";
    }

    public void test_setVersion() {
        TestObject6 obj = new TestObject6();
        Gson gson = new GsonBuilder()
                .setVersion(2.0)
                .create();
        String output = gson.toJson(obj);
        assertEquals("{\"field3\":\"field3\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .setVersion(2.0)
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"field3\":\"field3\"}", output);
    }

    public void test_addSerializationExclusionStrategy() {
        TestObject6 obj = new TestObject6();
        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !f.getName().equals("field3");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(exclusionStrategy)
                .create();
        String output = gson.toJson(obj);
        assertEquals("{\"field3\":\"field3\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
                .addSerializationExclusionStrategy(exclusionStrategy)
                .build();
        output = JsonStream.serialize(config, obj);
        assertEquals("{\"field3\":\"field3\"}", output);
    }


    private static class TestObject {
        private String test;
    }

    public void test_surrogate() {
        GsonCompatibilityMode gsonConfig =
                new GsonCompatibilityMode.Builder()
                        .disableHtmlEscaping()
                        .build();

        String input = "{\"test\":\"lorem-\uD83D\uDC44\uD83D\uDC40\"}";
        TestObject testObject = new Gson().fromJson(input, TestObject.class);

        System.out.println("Gson: " + new Gson().toJson(testObject));
        System.out.println("jsoniter: " + JsonStream.serialize(gsonConfig, testObject));
    }
}
