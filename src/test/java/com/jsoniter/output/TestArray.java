package com.jsoniter.output;

import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestArray extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void test_gen_array() throws IOException {
        stream.writeVal(new String[] {"hello", "world"});
        stream.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        stream.writeVal(new TypeLiteral<List<String>>(){}, list);
        stream.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection_without_type() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        stream.writeVal(list);
        stream.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_empty_array() throws IOException {
        stream.writeVal(new String[0]);
        stream.close();
        assertEquals("[]".replace('\'', '"'), baos.toString());
    }

    public void test_null_array() throws IOException {
        stream.writeVal(new TypeLiteral<String[]>(){}, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_empty_collection() throws IOException {
        stream.writeVal(new ArrayList());
        stream.close();
        assertEquals("[]".replace('\'', '"'), baos.toString());
    }

    public void test_null_collection() throws IOException {
        stream.writeVal(new TypeLiteral<ArrayList>(){}, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }
}
