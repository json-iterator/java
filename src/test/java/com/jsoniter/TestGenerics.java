package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;

public class TestGenerics extends TestCase {

    public void test_int_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1,2,3]");
        List<Integer> val = iter.read(new TypeLiteral<ArrayList<Integer>>() {
        });
        assertArrayEquals(new Integer[]{1, 2, 3}, val.toArray(new Integer[0]));
    }

    public void test_string_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello', 'world']".replace('\'', '"'));
        List<String> val = iter.read(new TypeLiteral<List<String>>() {
        });
        assertArrayEquals(new String[]{"hello", "world"}, val.toArray(new String[0]));
    }

    public void test_linked_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello', 'world']".replace('\'', '"'));
        List<String> val = iter.read(new TypeLiteral<LinkedList<String>>() {
        });
        assertArrayEquals(new String[]{"hello", "world"}, val.toArray(new String[0]));
    }

    public void test_string_set() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello']".replace('\'', '"'));
        Set<String> val = iter.read(new TypeLiteral<Set<String>>() {
        });
        assertArrayEquals(new String[]{"hello"}, val.toArray(new String[0]));
    }

    public void test_string_map() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'hello': 'world'}".replace('\'', '"'));
        Map<String, String> val = iter.read(new TypeLiteral<Map<String, String>>() {
        });
        assertEquals("world", val.get("hello"));
    }

    public void test_list_of_list() throws Exception {
        JsonIterator iter = JsonIterator.parse("[[1,2],[3,4]]");
        List<List<Integer>> listOfList = iter.read(new TypeLiteral<List<List<Integer>>>() {
        });
        System.out.println(listOfList);
        assertEquals(Integer.valueOf(4), listOfList.get(1).get(1));
    }

    public void test_complex_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100, 'field2': [[1,2],[3,4]]}".replace('\'', '"'));
        ComplexObject val = iter.read(ComplexObject.class);
        assertEquals(100, val.field1);
        assertEquals(Integer.valueOf(4), val.field2.get(1).get(1));
    }
}
