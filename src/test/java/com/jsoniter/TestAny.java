package com.jsoniter;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestAny extends TestCase {
    public void test_get() {
        assertEquals("hello", new Any("hello").get());
        assertEquals("hello", new Any(new String[]{"hello"}).get(0));
        assertNull(new Any(new String[]{"hello"}).get(1));
        assertNull(new Any(new String[]{"hello"}).get(-1));
        assertFalse(new Any(new String[]{"hello"}).exists(-1));
        assertEquals("hello", new Any(new ArrayList(){{
            add("hello");
        }}).get(0));
        assertNull(new Any(new ArrayList(){{
            add("hello");
        }}).get(2));
        assertEquals("world", new Any(new HashMap(){{
            put("hello", "world");
        }}).get("hello"));
        assertNull(new Any(new HashMap(){{
            put("hello", "world");
        }}).get(1));
        assertNull(new Any(new HashMap(){{
            put("hello", "world");
        }}).get("abc"));
        assertEquals("2", new Any(new HashMap(){{
            put("hello", new String[]{"1", "2"});
        }}).get("hello", 1));
    }

    public void test_get_value_type() {
        assertEquals(ValueType.STRING, new Any("hello").getValueType());
        assertEquals(ValueType.NULL, new Any(null).getValueType());
        assertEquals(ValueType.NUMBER, new Any(1.1).getValueType());
        assertEquals(ValueType.ARRAY, new Any(new String[0]).getValueType());
        assertEquals(ValueType.ARRAY, new Any(new ArrayList()).getValueType());
        assertEquals(ValueType.OBJECT, new Any(new SimpleObject()).getValueType());
    }

    public void test_to_string() {
        assertEquals("hello", new Any("hello").toString());
        assertEquals("null", new Any(null).toString());
    }

    public void test_equals() {
        assertEquals(new Any("hello"), new Any("hello"));
    }

    public void test_to_int() {
        assertEquals(123, new Any("123").toInt());
        assertEquals(123, new Any(123.3).toInt());
    }

    public void test_to_boolean() {
        assertTrue(new Any("123").toBoolean());
    }

    public void test_apply_to_all_element() {
        Any any = new Any(new Object[]{
                new HashMap<String, Object>() {{
                    put("hello", "world1");
                }},
                new HashMap<String, Object>() {{
                    put("hello", "world2");
                }}
        });
        List<String> objects = any.get("*", "hello");
        assertEquals(Arrays.asList("world1", "world2"), objects);
    }
}
