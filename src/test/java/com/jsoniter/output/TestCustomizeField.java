package com.jsoniter.output;

import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestCustomizeField extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public static class TestObject1 {
        public String field1;
    }

    public void test_customize_field_decoder() throws IOException {
        JsoniterSpi.registerPropertyEncoder(TestObject1.class, "field1", new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                String str = (String) obj;
                stream.writeVal(Integer.valueOf(str));
            }
        });
        TestObject1 obj = new TestObject1();
        obj.field1 = "100";
        stream.writeVal(obj);
        stream.close();
        assertEquals("{'field1':100}".replace('\'', '"'), baos.toString());
    }
}
