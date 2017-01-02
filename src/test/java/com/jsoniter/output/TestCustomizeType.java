package com.jsoniter.output;

import com.jsoniter.spi.EmptyEncoder;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class TestCustomizeType extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class MyDate {
        Date date;
    }

    public void test() throws IOException {
        JsoniterSpi.registerTypeEncoder(MyDate.class, new EmptyEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                MyDate date = (MyDate) obj;
                stream.writeVal(date.date.getTime());
            }
        });
        MyDate myDate = new MyDate();
        myDate.date = new Date(1481365190000L);
        stream.writeVal(myDate);
        stream.close();
        assertEquals("1481365190000", baos.toString());
    }
}
