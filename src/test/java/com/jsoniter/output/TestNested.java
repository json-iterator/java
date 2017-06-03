package com.jsoniter.output;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestNested extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        JsoniterAnnotationSupport.enable();
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void tearDown() {
        JsoniterAnnotationSupport.disable();
    }

    public static class TestObject1 {
        public String field1;
        public String field2;
    }

    public void test_array_of_objects() throws IOException {
        TestObject1 obj1 = new TestObject1();
        obj1.field1 = "1";
        obj1.field2 = "2";
        stream.writeVal(new TestObject1[]{obj1});
        stream.close();
        assertEquals("[{'field1':'1','field2':'2'}]".replace('\'', '"'), baos.toString());
    }

    public void test_collection_of_objects() throws IOException {
        final TestObject1 obj1 = new TestObject1();
        obj1.field1 = "1";
        obj1.field2 = "2";
        stream.writeVal(new TypeLiteral<List<TestObject1>>() {
        }, new ArrayList() {{
            add(obj1);
        }});
        stream.close();
        assertEquals("[{'field1':'1','field2':'2'}]".replace('\'', '"'), baos.toString());
    }

    public static class TestObject2 {
        public TestObject1[] objs;
    }

    public void test_object_of_array() throws IOException {
        stream.indentionStep = 2;
        TestObject2 obj = new TestObject2();
        obj.objs = new TestObject1[1];
        obj.objs[0] = new TestObject1();
        obj.objs[0].field1 = "1";
        obj.objs[0].field2 = "2";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\n" +
                "  \"objs\":[\n" +
                "    {\n" +
                "      \"field1\":\"1\",\n" +
                "      \"field2\":\"2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}".replace('\'', '"'), baos.toString());
    }

    public void test_map_of_objects() throws IOException {
        stream.indentionStep = 2;
        final TestObject1 obj1 = new TestObject1();
        obj1.field1 = "1";
        obj1.field2 = "2";
        stream.writeVal(new TypeLiteral<Map<String, TestObject1>>() {
        }, new HashMap() {{
            put("hello", obj1);
        }});
        stream.close();
        assertEquals("{\n" +
                "  \"hello\":{\n" +
                "    \"field1\":\"1\",\n" +
                "    \"field2\":\"2\"\n" +
                "  }\n" +
                "}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject3 {
        @JsonProperty(omitNull = false)
        public TestObject3 reference;
    }

    public void test_recursive_class() {
        // recursive reference will not be supported
        // however recursive structure is supported
        TestObject3 obj = new TestObject3();
        assertEquals("{\"reference\":null}", JsonStream.serialize(obj));
    }
}
