package com.jsoniter.output;

import com.jsoniter.spi.EmptyEncoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestSpiTypeEncoder extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    public static class MyDate {
        Date date;
    }

    public void test_TypeEncoder() throws IOException {
        JsoniterSpi.registerTypeEncoder(MyDate.class, new EmptyEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                MyDate date = (MyDate) obj;
                stream.writeVal(date.date.getTime());
            }
        });
        System.out.println(JsoniterSpi.getCurrentConfig().configName());
        MyDate myDate = new MyDate();
        myDate.date = new Date(1481365190000L);
        String output = JsonStream.serialize(myDate);
        assertEquals("1481365190000", output);
    }

    public void test_TypeEncoder_for_type_literal() {
        TypeLiteral<List<MyDate>> typeLiteral = new TypeLiteral<List<MyDate>>() {
        };
        JsoniterSpi.registerTypeEncoder(typeLiteral, new EmptyEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                List<MyDate> dates = (List<MyDate>) obj;
                stream.writeVal(dates.get(0).date.getTime());
            }
        });
        MyDate myDate = new MyDate();
        myDate.date = new Date(1481365190000L);
        String output = JsonStream.serialize(typeLiteral, Arrays.asList(myDate));
        assertEquals("1481365190000", output);
    }
}
