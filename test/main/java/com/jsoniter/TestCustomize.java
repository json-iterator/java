package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
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
}
