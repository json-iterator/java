package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Date;

public class TestCustomize extends TestCase {
    public void test_customize_type() throws IOException {
        JsonIterator.registerTypeDecoder(Date.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return new Date(iter.readLong());
            }
        });
        JsonIterator iter = JsonIterator.parse("1481365190000");
        Date date = iter.read(Date.class);
        assertEquals(1481365190000L, date.getTime());
    }

    public void test_customize_field() throws IOException {
        JsonIterator.registerFieldDecoder(CustomizedObject.class, "field1", new Decoder() {

            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return Integer.toString(iter.readInt());
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_customize_all_fields() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(Binding field) {
                if (field.clazz == CustomizedObject.class && field.name.equals("field1")) {
                    return new Decoder() {

                        @Override
                        public Object decode(JsonIterator iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_change_field_name() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(Binding field) {
                if (field.clazz == CustomizedObject.class && field.name.equals("field1")) {
                    return new Decoder() {
                        @Override
                        public Object decode(JsonIterator iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
                }
                return null;
            }

            @Override
            public String[] getBindFrom(Binding field) {
                if (field.clazz == CustomizedObject.class && field.name.equals("field1")) {
                    return new String[]{"field_1", "Field1"};
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field_1': 100}{'Field1': 101}".replace('\'', '"'));
        CustomizedObject myObject1 = iter.read(CustomizedObject.class);
        assertEquals("100", myObject1.field1);
        CustomizedObject myObject2 = iter.read(CustomizedObject.class);
        assertEquals("101", myObject2.field1);
    }

    public void test_customized_decoder_with_int_field() throws IOException {
        JsonIterator.registerFieldDecoder(CustomizedObject.class, "field3", new Decoder.IntDecoder() {

            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return iter.readInt() * 5;
            }

            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field3': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals(100 * 5, myObject.field3);
    }
}
