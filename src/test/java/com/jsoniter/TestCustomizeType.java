package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

public class TestCustomizeType extends TestCase {

    public static class MyDate {
        Date date;
    }

    static {
//        JsonIterator.enableStrictMode();
        JsonIterator.registerTypeDecoder(MyDate.class, new Decoder() {
            @Override
            public Object decode(final JsonIterator iter) throws IOException {
                return new MyDate() {{
                    date = new Date(iter.readLong());
                }};
            }
        });
    }

    public void test_direct() throws IOException {
        JsonIterator iter = JsonIterator.parse("1481365190000");
        MyDate date = iter.read(MyDate.class);
        assertEquals(1481365190000L, date.date.getTime());
    }

    public static class FieldWithMyDate {
        public MyDate field;
    }

    public void test_as_field_type() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field': 1481365190000}".replace('\'', '"'));
        FieldWithMyDate obj = iter.read(FieldWithMyDate.class);
        assertEquals(1481365190000L, obj.field.date.getTime());
    }

    public void test_as_array_element() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1481365190000]");
        MyDate[] dates = iter.read(MyDate[].class);
        assertEquals(1481365190000L, dates[0].date.getTime());
    }

    public static class MyDate2 {
        Date date;
    }

    public static class FieldWithMyDate2 {
        public MyDate2 field;
    }

    public void test_customize_through_extension() throws IOException {
        JsonIterator.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(String cacheKey, Type type) {
                if (type == MyDate2.class) {
                    return new Decoder() {
                        @Override
                        public Object decode(final JsonIterator iter) throws IOException {
                            return new MyDate2() {{
                                date = new Date(iter.readLong());
                            }};
                        }
                    };
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field': 1481365190000}".replace('\'', '"'));
        FieldWithMyDate2 obj = iter.read(FieldWithMyDate2.class);
        assertEquals(1481365190000L, obj.field.date.getTime());
    }
}
