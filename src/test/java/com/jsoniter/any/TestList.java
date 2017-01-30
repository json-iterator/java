package com.jsoniter.any;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class TestList extends TestCase {
    public void test_size() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        assertEquals(3, any.size());
    }

    public void test_to_boolean() {
        Any any = Any.wrap(Arrays.asList());
        assertFalse(any.toBoolean());
        any = Any.wrap(Arrays.asList("hello", 1));
        assertTrue(any.toBoolean());
    }

    public void test_to_int() {
        Any any = Any.wrap(Arrays.asList());
        assertEquals(0, any.toInt());
        any = Any.wrap(Arrays.asList("hello", 1));
        assertEquals(1, any.toInt());
    }

    public void test_get() {
        Any any = Any.wrap(Arrays.asList("hello", 1));
        assertEquals("hello", any.get(0).toString());
    }

    public void test_get_from_nested() {
        Any any = Any.wrap(Arrays.asList(Arrays.asList("hello"), Arrays.asList("world")));
        assertEquals("hello", any.get(0, 0).toString());
        assertEquals("[\"hello\",\"world\"]", any.get('*', 0).toString());
    }

    public void test_iterator() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Any element : any) {
            list.add(element.toInt());
        }
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    public void test_to_string() {
        assertEquals("[1,2,3]", Any.wrap(Arrays.asList(1, 2, 3)).toString());
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        any.asList().add(Any.wrap(4));
        assertEquals("[1,2,3,4]", any.toString());
    }
}
