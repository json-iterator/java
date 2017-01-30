package com.jsoniter;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.any.Any;
import com.jsoniter.fuzzy.MaybeEmptyArrayDecoder;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class TestObject extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class EmptyClass {
    }

    public void test_empty_class() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(EmptyClass.class));
    }

    public void test_empty_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj.field1);
        iter.reset(iter.buf);
        Object obj = iter.read(Object.class);
        assertEquals(0, ((Map)obj).size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(0, any.size());
    }

    public void test_one_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1'\r:\n\t'hello' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertNull(simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertEquals(ValueType.INVALID, any.get("field2").valueType());
        iter.reset(iter.buf);
        assertEquals("hello", ((Map)iter.read()).get("field1"));
    }

    public void test_two_fields() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' , 'field2': 'world' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertEquals("field2", iter.readObject());
        assertEquals("world", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertEquals("world", simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertEquals("world", any.toString("field2"));
        iter.reset(iter.buf);
        final ArrayList<String> fields = new ArrayList<String>();
        iter.readObjectCB(new JsonIterator.ReadObjectCallback() {
            @Override
            public boolean handle(JsonIterator iter, String field, Object attachment) throws IOException {
                fields.add(field);
                iter.skip();
                return true;
            }
        }, null);
        assertEquals(Arrays.asList("field1", "field2"), fields);
    }

    public void test_read_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(ValueType.NULL, any.get().valueType());
    }

    public void test_native_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 100 }".replace('\'', '"'));
        ComplexObject complexObject = iter.read(ComplexObject.class);
        assertEquals(100, complexObject.field1);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(100, any.toInt("field1"));
    }

    public static class InheritedObject extends SimpleObject {
        public String inheritedField;
    }

    public void test_inheritance() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        InheritedObject inheritedObject = iter.read(InheritedObject.class);
        assertEquals("hello", inheritedObject.inheritedField);
    }

    public void test_incomplete_field_name() throws IOException {
        try {
            JsonIterator.parse("{\"abc").read(InheritedObject.class);
            fail();
        } catch (JsonException e) {
        }
    }

    public static interface IDependenceInjectedObject {
        String getSomeService();
    }

    public static class DependenceInjectedObject implements IDependenceInjectedObject {

        private String someService;

        public DependenceInjectedObject(String someService) {
            this.someService = someService;
        }

        public String getSomeService() {
            return someService;
        }
    }

    public void test_object_creation() throws IOException {
        JsoniterSpi.registerExtension(new EmptyExtension() {
            @Override
            public boolean canCreate(Class clazz) {
                return clazz.equals(DependenceInjectedObject.class) || clazz.equals(IDependenceInjectedObject.class);
            }

            @Override
            public Object create(Class clazz) {
                return new DependenceInjectedObject("hello");
            }
        });
        IDependenceInjectedObject obj = JsonIterator.deserialize("{}", IDependenceInjectedObject.class);
        assertEquals("hello", obj.getSomeService());
    }

    public static class TestObject5 {

        public enum MyEnum {
            HELLO,
            WORLD,
            WOW
        }
        public MyEnum field1;
    }

    public void test_enum() throws IOException {
        TestObject5 obj = JsonIterator.deserialize("{\"field1\":\"HELLO\"}", TestObject5.class);
        assertEquals(TestObject5.MyEnum.HELLO, obj.field1);
        try {
            JsonIterator.deserialize("{\"field1\":\"HELLO1\"}", TestObject5.class);
            fail();
        } catch (JsonException e) {
        }
        obj = JsonIterator.deserialize("{\"field1\":null}", TestObject5.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":\"WOW\"}", TestObject5.class);
        assertEquals(TestObject5.MyEnum.WOW, obj.field1);
    }

    public static class TestObject6 {
        @JsonProperty(decoder = MaybeEmptyArrayDecoder.class)
        public Map<String, Object> field1;
    }

    public void test_maybe_empty_array_field() {
        JsoniterAnnotationSupport.enable();
        TestObject6 obj = JsonIterator.deserialize("{\"field1\":[]}", TestObject6.class);
        assertNull(obj.field1);
    }

    public void test_iterator() {
        Any any = JsonIterator.deserialize("{\"field1\":1,\"field2\":2,\"field3\":3}");
        Any.EntryIterator iter = any.entries();
        assertTrue(iter.next());
        assertEquals("field1", iter.key());
        assertEquals(1, iter.value().toInt());
        iter = any.entries();
        assertTrue(iter.next());
        assertEquals("field1", iter.key());
        assertEquals(1, iter.value().toInt());
        assertTrue(iter.next());
        assertEquals("field2", iter.key());
        assertEquals(2, iter.value().toInt());
        assertTrue(iter.next());
        assertEquals("field3", iter.key());
        assertEquals(3, iter.value().toInt());
        assertFalse(iter.next());
    }

    public static class PublicSuper {
        public String field1;
    }

    private static class PrivateSub extends PublicSuper {
    }

    public static class TestObject7 {
        public PrivateSub field1;
        public void setFieldXXX(PrivateSub obj) {
        }
    }

    public void test_private_ref() throws IOException {
        TestObject7 obj = JsonIterator.deserialize("{}", TestObject7.class);
        assertNull(obj.field1);
    }

    public void test_object_lazy_any_to_string() {
        Any any = JsonIterator.deserialize("{\"field1\":1,\"field2\":2,\"field3\":3}");
        any.asMap().put("field4", Any.wrap(4));
        assertEquals("{\"field1\":1,\"field3\":3,\"field2\":2,\"field4\":4}", any.toString());
    }
}
