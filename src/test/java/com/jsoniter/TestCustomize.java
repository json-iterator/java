package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;

public class TestCustomize extends TestCase {
    public void test_customize_type() throws IOException {
        Jsoniter.registerTypeDecoder(Date.class, new Decoder() {
            @Override
            public Object decode(Type type, Jsoniter iter) throws IOException {
                return new Date(iter.readLong());
            }
        });
        Jsoniter iter = Jsoniter.parse("1481365190000");
        Date date = iter.read(Date.class);
        assertEquals(1481365190000L, date.getTime());
    }

    public void test_customize_field() throws IOException {
        Jsoniter.registerFieldDecoder(CustomizedObject.class, "field1", new Decoder(){

            @Override
            public Object decode(Type type, Jsoniter iter) throws IOException {
                return Integer.toString(iter.readInt());
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_customize_all_fields() throws IOException {
        Jsoniter.registerFieldDecoderFactory(new FieldDecoderFactory() {
            @Override
            public Decoder createDecoder(Field field) {
                if (field.getDeclaringClass() == CustomizedObject.class && field.getName().equals("field1")) {
                    return new Decoder(){

                        @Override
                        public Object decode(Type type, Jsoniter iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
                }
                return null;
            }
        });
        Jsoniter iter = Jsoniter.parse("{'field1': 100}".replace('\'', '"'));
        CustomizedObject myObject = iter.read(CustomizedObject.class);
        assertEquals("100", myObject.field1);
    }

    public void test_change_field_name() throws IOException {
        Jsoniter.registerFieldDecoderFactory(new FieldDecoderFactory() {
            @Override
            public Decoder createDecoder(Field field) {
                if (field.getDeclaringClass() == CustomizedObject.class && field.getName().equals("field1")) {
                    return new FieldDecoder(){

                        @Override
                        public String[] getAlternativeFieldNames() {
                            return new String[]{"field_1", "Field1"};
                        }

                        @Override
                        public Object decode(Type type, Jsoniter iter) throws IOException {
                            return Integer.toString(iter.readInt());
                        }
                    };
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
}
