package com.jsoniter.datetime;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

import java.util.Date;


public class TestJdkDatetime extends TestCase {

    public void test() {
        JdkDatetimeSupport.enable("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        assertEquals("\"1970-01-01T08:00:00.000+0800\"", JsonStream.serialize(new Date(0)));
        Date obj = JsonIterator.deserialize("\"1970-01-01T08:00:00.000+0800\"", Date.class);
        assertEquals(0, obj.getTime());
    }
}
