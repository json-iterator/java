package com.jsoniter.output;

import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMap extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void test() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("hello", "world");
        stream.writeVal(map);
        stream.close();
        assertEquals("{'hello':'world'}".replace('\'', '"'), baos.toString());
    }

    public void test_empty() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        stream.writeVal(map);
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public void test_null() throws IOException {
        stream.writeVal(new TypeLiteral<HashMap>(){}, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_value_is_null() throws IOException {
        HashMap<String, int[]> obj = new HashMap<String, int[]>();
        obj.put("hello", null);
        stream.writeVal(new TypeLiteral<Map<String, int[]>>(){}, obj);
        stream.close();
        assertEquals("{\"hello\":null}", baos.toString());
    }
}
