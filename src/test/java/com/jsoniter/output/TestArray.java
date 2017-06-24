package com.jsoniter.output;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

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
        stream.writeVal(new String[]{"hello", "world"});
        stream.close();
        assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        stream.writeVal(new TypeLiteral<List<String>>() {
        }, list);
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
        stream.writeVal(new TypeLiteral<String[]>() {
        }, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_empty_collection() throws IOException {
        stream.writeVal(new ArrayList());
        stream.close();
        assertEquals("[]".replace('\'', '"'), baos.toString());
    }

    public void test_null_collection() throws IOException {
        stream.writeVal(new TypeLiteral<ArrayList>() {
        }, null);
        stream.close();
        assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public static class TestObject1 {
        public List<String> field1;
    }

    public void test_list_of_objects() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = Arrays.asList("a", "b");
        stream.writeVal(new TypeLiteral<List<TestObject1>>() {
        }, Arrays.asList(obj));
        stream.close();
        assertEquals("[{\"field1\":[\"a\",\"b\"]}]", baos.toString());
    }

    public void test_array_of_null() throws IOException {
        stream.writeVal(new TestObject1[1]);
        stream.close();
        assertEquals("[null]", baos.toString());
    }

    public void test_list_of_null() throws IOException {
        TestObject1 obj = new TestObject1();
        obj.field1 = Arrays.asList("a", "b");
        ArrayList<TestObject1> list = new ArrayList<TestObject1>();
        list.add(null);
        stream.writeVal(new TypeLiteral<List<TestObject1>>() {
        }, list);
        stream.close();
        assertEquals("[null]", baos.toString());
    }

    public void test_hash_set() throws IOException {
        assertEquals("[]", JsonStream.serialize(new HashSet<Integer>()));
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        assertEquals("[1]", JsonStream.serialize(set));
    }

    public void test_arrays_as_list() throws IOException {
        assertEquals("[1,2,3]", JsonStream.serialize(Arrays.asList(1, 2, 3)));
    }

    public void test_default_empty_collection() throws IOException {
        assertEquals("[]", JsonStream.serialize(Collections.emptySet()));
    }

    public void test_indention() {
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1,\n" +
                "  2\n" +
                "]", JsonStream.serialize(cfg, new int[]{1, 2}));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[\n" +
                "  1,\n" +
                "  2\n" +
                "]", JsonStream.serialize(cfg, new int[]{1, 2}));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder()
                .encodingMode(EncodingMode.REFLECTION_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new int[]{}));
        cfg = new Config.Builder()
                .encodingMode(EncodingMode.DYNAMIC_MODE)
                .indentionStep(2)
                .build();
        assertEquals("[]", JsonStream.serialize(cfg, new int[]{}));
    }
}
