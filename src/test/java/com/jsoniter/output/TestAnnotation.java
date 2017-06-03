package com.jsoniter.output;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsonUnwrapper;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.Encoder;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestAnnotation extends TestCase {
    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

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
        @JsonProperty(to = {"field-1"})
        public String field1;
    }

    public void test_property() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field-1\":\"hello\"}", baos.toString());
    }

    public static class TestObject2 {
        @JsonProperty(encoder = Encoder.StringIntEncoder.class)
        public int field1;
    }

    public void test_encoder() throws IOException {
        TestObject2 obj = new TestObject2();
        obj.field1 = 100;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":\"100\"}", baos.toString());
    }

    public static class TestObject3 {
        @JsonIgnore
        public int field1;
    }

    public void test_ignore() throws IOException {
        TestObject3 obj = new TestObject3();
        obj.field1 = 100;
        stream.writeVal(obj);
        stream.close();
        assertEquals("{}", baos.toString());
    }

    public static class TestObject4 {
        public int field1;

        public int getField1() {
            return field1;
        }
    }

    public void test_name_conflict() throws IOException {
        TestObject4 obj = new TestObject4();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"field1\":0}", baos.toString());
    }

    public static class TestObject5 {
        @JsonUnwrapper
        public void unwrap(JsonStream stream) throws IOException {
            stream.writeObjectField("hello");
            stream.writeVal("world");
        }
    }

    public void test_unwrapper() throws IOException {
        TestObject5 obj = new TestObject5();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"hello\":\"world\"}", baos.toString());
    }

    public interface TestObject6Interface<A> {
        A getHello();
    }

    public static class TestObject6 implements TestObject6Interface<Integer> {
        public Integer getHello() {
            return 0;
        }
    }

    public void test_inherited_getter_is_not_duplicate() throws IOException {
        TestObject6 obj = new TestObject6();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"hello\":0}", baos.toString());
    }

    public static class TestObject7 {
        @JsonUnwrapper
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_unwrapper_with_map() throws IOException {
        TestObject7 obj = new TestObject7();
        stream.writeVal(obj);
        stream.close();
        assertEquals("{\"100\":\"hello\"}", baos.toString());
    }
}
