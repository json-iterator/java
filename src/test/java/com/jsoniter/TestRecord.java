package com.jsoniter;


import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.any.Any;
import com.jsoniter.spi.ClassInfo;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class TestRecord extends TestCase {

    record TestRecord1(long field1) {}

    public record TestRecord0(Long id, String name) {

        public TestRecord0() {

            this(0L, "");
        }
    }

    public void test_print_record_reflection_info() {

        Class<TestRecord1> clazz = TestRecord1.class;

        System.out.println("Record Constructors :");
        for (Constructor<?> constructor : clazz.getConstructors()) {
            System.out.println(constructor);
        }

        System.out.println("Record Methods : ");
        for (Method method : clazz.getMethods()) {
            System.out.println(method);
        }

        System.out.println("Record Fields : ");
        for (Field field : clazz.getFields()) {
            System.out.println(field);
            System.out.println("    modifiers : " + Modifier.toString(field.getModifiers()));
        }

        System.out.println("Record Declared Fields : ");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(field);
            System.out.println("    modifiers : " + Modifier.toString(field.getModifiers()));
        }

        try {
            System.out.println("Record Default Declared Constructor : " + clazz.getDeclaredConstructor());
        } catch (Exception ex) {
            System.err.println("No Record Default Declared Constructor!");
        }

        System.out.println("Record Declared Constructors : ");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.println(constructor);
            System.out.println("    name : " + constructor.getName());
            System.out.println("    modifiers : " + Modifier.toString(constructor.getModifiers()));
            System.out.println("    input count : " + constructor.getParameterCount());
            System.out.println("    input types : ");
            for (Class<?> parameter : constructor.getParameterTypes())
                System.out.println("        " + parameter);
        }
    }

    public void test_empty_record() throws IOException {

        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(TestRecord0.class));
    }

    public void test_empty_simple_record() throws IOException {

        JsonIterator iter = JsonIterator.parse("{}");
        SimpleRecord simpleRecord = iter.read(SimpleRecord.class);
        assertNull(simpleRecord.field1());
        iter.reset(iter.buf);
        Object obj = iter.read(Object.class);
        assertEquals(0, ((Map) obj).size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(0, any.size());
    }

    public void test_record_error() throws IOException {

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1 }".replace('\'', '"'));
        try {
            TestRecord1 rec = iter.read(TestRecord1.class);
            assertEquals(1, rec.field1);
        } catch (JsonException e) {
            throw new JsonException("no constructor for: class com.jsoniter.TestRecord", e);
        }
    }


    public void test_record_withOnlyFieldDecoder() throws IOException {

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecord1.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1 }".replace('\'', '"'));
        TestRecord1 record = iter.read(TestRecord1.class);

        assertEquals(1, record.field1);
    }

    public void test_record_2_fields_withOnlyFieldDecoder() throws IOException {

        record TestRecord2(long field1, String field2) {}

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecord2.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1, 'field2' : 'hey' }".replace('\'', '"'));
        TestRecord2 record = iter.read(TestRecord2.class);

        assertEquals(1, record.field1);
        assertEquals("hey", record.field2);
    }

    public void test_record_2_fields_swapFieldOrder_withOnlyFieldDecoder() throws IOException {

        record TestRecord2(String field2, long field1) {}

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecord2.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'field2' : 'hey', 'field1' : 1 }".replace('\'', '"'));
        TestRecord2 record = iter.read(TestRecord2.class);

        assertEquals(1, record.field1);
        assertEquals("hey", record.field2);
    }

    public void test_record_recordComposition_withOnlyFieldDecoder() throws IOException {

        record TestRecordA(long fieldA) {}
        record TestRecordB(long fieldB, TestRecordA a) {}

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecordB.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'fieldB' : 1, 'a' : { 'fieldA' : 69 } }".replace('\'', '"'));
        TestRecordB record = iter.read(TestRecordB.class);

        assertEquals(1, record.fieldB);
        assertEquals(69, record.a.fieldA);
    }

    public void test_record_empty_constructor_withOnlyFieldDecoder() throws IOException {

        record TestRecord3() {}

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecord3.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'fieldB' : 1, 'a' : { 'fieldA' : 69 } }".replace('\'', '"'));
        TestRecord3 record = iter.read(TestRecord3.class);

        assertNotNull(record);
    }

    public void test_enum() throws IOException {

        record TestRecord5(MyEnum field1) {

            enum MyEnum {
                HELLO,
                WOW
            }
        }

        TestRecord5 obj = JsonIterator.deserialize("{\"field1\":\"HELLO\"}", TestRecord5.class);
        assertEquals(TestRecord5.MyEnum.HELLO, obj.field1);
        try {
            JsonIterator.deserialize("{\"field1\":\"HELLO1\"}", TestRecord5.class);
            fail();
        } catch (JsonException e) {
        }
        obj = JsonIterator.deserialize("{\"field1\":null}", TestRecord5.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":\"WOW\"}", TestRecord5.class);
        assertEquals(TestRecord5.MyEnum.WOW, obj.field1);
    }

    public void test_record_2_constructors_withOnlyFieldDecoder() throws IOException {

        record TestRecord6(long val) {

            public TestRecord6(int valInt) {
                this(Long.valueOf(valInt));
            }
        }

        assertEquals(ReflectionRecordDecoder.OnlyFieldRecord.class, ReflectionDecoderFactory.create(new ClassInfo(TestRecord6.class)).getClass());

        JsonIterator iter = JsonIterator.parse("{ 'valInt' : 1 }".replace('\'', '"'));
        TestRecord6 record = iter.read(TestRecord6.class);

        assertNotNull(record);
    }

    public void test_record_withCtorDecoder() throws IOException {

        record TestRecord2(@JsonProperty long field1) {

            @JsonCreator
            TestRecord2 {}
        }

        assertEquals(ReflectionDecoderFactory.create(new ClassInfo(TestRecord2.class)).getClass(), ReflectionObjectDecoder.WithCtor.class);

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1 }".replace('\'', '"'));
        TestRecord2 record = iter.read(TestRecord2.class);

        assertEquals(1, record.field1);
    }
}
