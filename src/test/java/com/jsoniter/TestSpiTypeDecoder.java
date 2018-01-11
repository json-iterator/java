package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestSpiTypeDecoder extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public static class TestObject1 {
        public int field1;
    }

    public void test_TypeDecoder() throws IOException {
        JsoniterSpi.registerTypeDecoder(TestObject1.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestObject1 obj = new TestObject1();
                obj.field1 = 101;
                return obj;
            }
        });
        TestObject1 obj = JsonIterator.deserialize(
                "{'field1': 100}".replace('\'', '"'), TestObject1.class);
        assertEquals(101, obj.field1);
    }

    public void test_TypeDecoder_for_generics() throws IOException {
        TypeLiteral<List<TestObject1>> typeLiteral = new TypeLiteral<List<TestObject1>>() {
        };
        JsoniterSpi.registerTypeDecoder(typeLiteral, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestObject1 obj = new TestObject1();
                obj.field1 = 101;
                return Arrays.asList(obj);
            }
        });
        List<TestObject1> objs = JsonIterator.deserialize(
                "{'field1': 100}".replace('\'', '"'), typeLiteral);
        assertEquals(101, objs.get(0).field1);
    }

    public static class MyDate {
        Date date;
    }

    static {
        JsoniterSpi.registerTypeDecoder(MyDate.class, new Decoder() {
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

    public static class MyList {
        public List<String> list;
    }

    public void test_list_or_single_element() {
        final TypeLiteral<List<String>> listOfString = new TypeLiteral<List<String>>() {
        };
        JsoniterSpi.registerTypeDecoder(MyList.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                ValueType valueType = iter.whatIsNext();
                MyList myList = new MyList();
                switch (valueType) {
                    case ARRAY:
                        myList.list = iter.read(listOfString);
                        return myList;
                    case STRING:
                        myList.list = new ArrayList<String>();
                        myList.list.add(iter.readString());
                        return myList;
                    default:
                        throw new JsonException("unexpected input");
                }
            }
        });
        MyList list1 = JsonIterator.deserialize("\"hello\"", MyList.class);
        assertEquals("hello", list1.list.get(0));
        MyList list2 = JsonIterator.deserialize("[\"hello\",\"world\"]", MyList.class);
        assertEquals("hello", list2.list.get(0));
        assertEquals("world", list2.list.get(1));
    }
}
