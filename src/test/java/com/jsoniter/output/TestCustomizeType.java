package com.jsoniter.output;

import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.Encoder;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Date;

public class TestCustomizeType extends TestCase {

    public static class MyDate {
        Date date;
    }

    public void test() {
        ExtensionManager.registerTypeEncoder(MyDate.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                MyDate date = (MyDate) obj;
                stream.writeVal(date.date.getTime());
            }
        });
    }
}
