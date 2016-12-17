package com.jsoniter.output;

import com.jsoniter.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestArray extends TestCase {

    private ByteArrayOutputStream baos;
    private JsonStream generator;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        generator = new JsonStream(baos, 4096);
    }

    public void test_gen_array() throws IOException {
        generator.writeVal(new String[] {"hello", "world"});
        generator.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        generator.writeVal(new TypeLiteral<List<String>>(){}, list);
        generator.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection_without_type() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        generator.writeVal(list);
        generator.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }
}
