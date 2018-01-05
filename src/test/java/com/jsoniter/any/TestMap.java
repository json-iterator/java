package com.jsoniter.any;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class TestMap extends TestCase {

    public void test_size() {
        Any any = Any.wrap(mapOf("hello", 1, "world", 2));
        assertEquals(2, any.size());
    }

    public void test_to_boolean() {
        Any any = Any.wrap(mapOf());
        assertFalse(any.toBoolean());
        any = Any.wrap(mapOf("hello", 1));
        assertTrue(any.toBoolean());
    }

    public void test_to_int() {
        Any any = Any.wrap(mapOf());
        assertEquals(0, any.toInt());
        any = Any.wrap(mapOf("hello", 1));
        assertEquals(1, any.toInt());
    }

    public void test_get() {
        Any any = Any.wrap(mapOf("hello", 1, "world", 2));
        assertEquals(2, any.get("world").toInt());
    }

    public void test_get_from_nested() {
        Any any = Any.wrap(mapOf("a", mapOf("b", "c"), "d", mapOf("e", "f")));
        assertEquals("c", any.get("a", "b").toString());
        assertEquals("{\"a\":\"c\"}", any.get('*', "b").toString());
    }

    public void test_iterator() {
        Any any = Any.wrap(mapOf("hello", 1, "world", 2));
        Any.EntryIterator iter = any.entries();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while (iter.next()) {
            map.put(iter.key(), iter.value().toInt());
        }
        assertEquals(mapOf("hello", 1, "world", 2), map);
    }

    public void test_to_string() {
        assertEquals("{\"world\":2,\"hello\":1}", Any.wrap(mapOf("hello", 1, "world", 2)).toString());
        Any any = Any.wrap(mapOf("hello", 1, "world", 2));
        any.asMap().put("abc", Any.wrap(3));
        assertEquals("{\"world\":2,\"abc\":3,\"hello\":1}", any.toString());
    }

    private static Map<String, Object> mapOf(Object... args) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], args[i + 1]);
        }
        return map;
    }
}
