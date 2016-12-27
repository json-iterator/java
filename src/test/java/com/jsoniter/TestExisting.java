package com.jsoniter;

import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TestExisting extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class TestObj1 {
        public String field1;
        public String field2;
    }

    public void test_direct_reuse() throws IOException {
        TestObj1 testObj = new TestObj1();
        testObj.field2 = "world";
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        testObj = iter.read(testObj);
        assertEquals("hello", testObj.field1);
        assertEquals("world", testObj.field2);
    }

    public static class TestObj2 {
        public String field3;
        public TestObj1 field4;
    }

    public void test_indirect_reuse() throws IOException {
        TestObj2 testObj = new TestObj2();
        testObj.field4 = new TestObj1();
        testObj.field4.field1 = "world";
        JsonIterator iter = JsonIterator.parse("{ 'field3' : 'hello', 'field4': {'field2': 'hello'} }".replace('\'', '"'));
        testObj = iter.read(testObj);
        assertEquals("hello", testObj.field3);
        assertEquals("hello", testObj.field4.field2);
        assertEquals("world", testObj.field4.field1);
    }

    public void test_reuse_list() throws IOException {
        List list1 = new ArrayList();
        JsonIterator iter = JsonIterator.parse("[1]");
        List list2= iter.read(new TypeLiteral<List<Integer>>(){}, list1);
        assertEquals(System.identityHashCode(list2), System.identityHashCode(list1));
    }

    public void test_reuse_linked_list() throws IOException {
        LinkedList list1 = new LinkedList();
        JsonIterator iter = JsonIterator.parse("[1]");
        List list2= iter.read(new TypeLiteral<LinkedList<Integer>>(){}, list1);
        assertEquals(System.identityHashCode(list2), System.identityHashCode(list1));
    }

    public void test_reuse_map() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("a", "b");
        HashMap<String, Object> map2 = iter.read(map1);
        assertEquals("b", map2.get("a"));
    }
}
