package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCustomizeField extends TestCase {

    static {
//        JsonIterator.enableStrictMode();
    }

    public static class TestObject1 {
        public String field1;
    }

    public void test_customize_field_decoder() throws IOException {
        JsonIterator.registerFieldDecoder(TestObject1.class, "field1", new Decoder() {

            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return Integer.toString(iter.readInt());
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject1 myObject = iter.read(TestObject1.class);
        assertEquals("100", myObject.field1);
    }

    public static class TestObject2 {
        public int field1;
    }

    public void test_customize_int_field() throws IOException {
        JsonIterator.registerFieldDecoder(TestObject2.class, "field1", new Decoder.IntDecoder() {

            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return Integer.valueOf(iter.readString());
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': '100'}".replace('\'', '"'));
        TestObject2 myObject = iter.read(TestObject2.class);
        assertEquals(100, myObject.field1);
    }

    public static class TestObject3 {
        public int field1;
    }

    public void test_customize_using_extension() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(Binding field) {
                if (field.clazz == TestObject3.class && field.name.equals("field1")) {
                    return new Decoder.IntDecoder() {

                        @Override
                        public int decodeInt(JsonIterator iter) throws IOException {
                            return Integer.valueOf(iter.readString());
                        }
                    };
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': '100'}".replace('\'', '"'));
        TestObject3 myObject = iter.read(TestObject3.class);
        assertEquals(100, myObject.field1);
    }

    public static class TestObject4 {
        public int field1;
    }

    public void test_rename_field() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public String[] getBindFrom(Binding field) {
                if (field.clazz == TestObject4.class && field.name.equals("field1")) {
                    return new String[]{"field_1", "Field1"};
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field_1': 100}{'Field1': 101}".replace('\'', '"'));
        TestObject4 myObject1 = iter.read(TestObject4.class);
        assertEquals(100, myObject1.field1);
        TestObject4 myObject2 = iter.read(TestObject4.class);
        assertEquals(101, myObject2.field1);
    }

    public static class TestObject5 {
        private int field1;
        public TestObject5(int field1) {
            this.field1 = field1;
        }
    }

    public void test_rename_ctor_param() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public CustomizedConstructor getConstructor(Class clazz) {
                if (clazz == TestObject5.class) {
                    return new CustomizedConstructor() {{
                        parameters = (List) Arrays.asList(new Binding() {{
                            name="param1";
                            valueType = int.class;
                        }});
                    }};
                }
                return null;
            }

            @Override
            public String[] getBindFrom(Binding field) {
                if (field.clazz == TestObject5.class && "param1".equals(field.name)) {
                    return new String[]{"param2"};
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'param2': 1000}".replace('\'', '"'));
        TestObject5 obj = iter.read(TestObject5.class);
        assertEquals(1000, obj.field1);
    }

    public static class TestObject6 {
        String field;

        public void setField(String field) {
            this.field = field;
        }
    }

    public void test_rename_setter() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public String[] getBindFrom(Binding field) {
                if (field.clazz == TestObject6.class && field.name.equals("field")) {
                    return new String[]{"field_1", "Field1"};
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field_1': 'hello'}".replace('\'', '"'));
        TestObject6 obj = iter.read(TestObject6.class);
        assertEquals("hello", obj.field);
    }
}
