package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

public class TestCustomize extends TestCase {
    public void test_customize_type() throws IOException {
        Jsoniter.registerTypeDecoder(Date.class, new Decoder() {
            @Override
            public Object decode(Jsoniter iter) throws IOException {
                return new Date(iter.readLong());
            }
        });
        Jsoniter iter = Jsoniter.parse("1481365190000");
        Date date = iter.read(Date.class);
        assertEquals(1481365190000L, date.getTime());
    }

    public void test_customize_field() throws IOException {
        Jsoniter.registerFieldDecoder(CustomizedObject.class, "field1", new Decoder() {

            @Override
            public Object decode(Jsoniter iter) throws IOException {
                return Integer.toString(iter.readInt());
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_customize_all_fields() throws IOException {
        Jsoniter.registerFieldDecoderFactory(new Extension() {
            @Override
            public Decoder createDecoder(Field field) {
                if (field.getDeclaringClass() == CustomizedObject.class && field.getName().equals("field1")) {
                    return new Decoder() {

                        @Override
                        public Object decode(Jsoniter iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
                }
                return null;
            }

            @Override
            public String[] getAlternativeFieldNames(Field field) {
                return null;
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_change_field_name() throws IOException {
        Jsoniter.registerFieldDecoderFactory(new Extension() {
            @Override
            public Decoder createDecoder(Field field) {
                if (field.getDeclaringClass() == CustomizedObject.class && field.getName().equals("field1")) {
                    return new Decoder() {
                        @Override
                        public Object decode(Jsoniter iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
                }
                return null;
            }

            @Override
            public String[] getAlternativeFieldNames(Field field) {
                if (field.getDeclaringClass() == CustomizedObject.class && field.getName().equals("field1")) {
                    return new String[]{"field_1", "Field1"};
                }
                return null;
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field_1': 100}{'Field1': 101}".replace('\'', '"'));
        CustomizedObject myObject1 = iter.read(CustomizedObject.class);
        assertEquals("100", myObject1.field1);
        CustomizedObject myObject2 = iter.read(CustomizedObject.class);
        assertEquals("101", myObject2.field1);
    }

    public void test_customized_decoder_with_int_field() throws IOException {
        Jsoniter.registerFieldDecoder(CustomizedObject.class, "field3", new Decoder.IntDecoder() {

            @Override
            public int decodeInt(Jsoniter iter) throws IOException {
                return iter.readInt() * 5;
            }

            @Override
            public Object decode(Jsoniter iter) throws IOException {
                return null;
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field3': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals(100 * 5, myObject.field3);
    }

    public void test_customized_constructor() throws IOException {
        Jsoniter iter = Jsoniter.parse("{'field1': 100}".replace('\'', '"'));
        CtorCustomizedObject myObject = iter.read(CtorCustomizedObject.class);
        assertEquals(100, myObject.getField1());
    }
}
