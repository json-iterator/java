package com.jsoniter.any;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class TestArray extends TestCase {
    public void test_size() {
        Any any = Any.wrap(new int[]{1, 2, 3});
        assertEquals(3, any.size());
    }

    public void test_to_boolean() {
        Any any = Any.wrap(new int[0]);
        assertFalse(any.toBoolean());
        any = Any.wrap(new Object[]{"hello", 1});
        assertTrue(any.toBoolean());
    }

    public void test_to_int() {
        Any any = Any.wrap(new int[0]);
        assertEquals(0, any.toInt());
        any = Any.wrap(new Object[]{"hello", 1});
        assertEquals(2, any.toInt());
    }

    public void test_get() {
        Any any = Any.wrap(new Object[]{"hello", 1});
        assertEquals("hello", any.get(0).toString());
    }

    public void test_get_from_nested() {
        Any any = Any.wrap(new Object[]{new String[]{"hello"}, new String[]{"world"}});
        assertEquals("hello", any.get(0, 0).toString());
        assertEquals("[\"hello\",\"world\"]", any.get('*', 0).toString());
    }

    public void test_iterator() {
        Any any = Any.wrap(new long[]{1, 2, 3});
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Any element : any) {
            list.add(element.toInt());
        }
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    public void test_to_string() {
        assertEquals("[1,2,3]", Any.wrap(new long[]{1, 2, 3}).toString());
        Any any = Any.wrap(new long[]{1, 2, 3});
        any.asList().add(Any.wrap(4));
        assertEquals("[1,2,3,4]", any.toString());
    }

    public void test_fill_partial_then_iterate() {
        Any obj = JsonIterator.deserialize("[1,2,3]");
        assertEquals(1, obj.get(0).toInt());
        Iterator<Any> iter = obj.iterator();
        assertEquals(1, iter.next().toInt());
        assertEquals(2, iter.next().toInt());
        assertEquals(3, iter.next().toInt());
        assertFalse(iter.hasNext());
    }

    public void test_equals_and_hashcode() {
        Any obj1 = JsonIterator.deserialize("[1,2,3]");
        Any obj2 = JsonIterator.deserialize("[1, 2, 3]");
        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), obj2.hashCode());
    }

    public void test_null() {
        Any x = JsonIterator.deserialize("{\"test\":null}");
        assertFalse(x.get("test").iterator().hasNext());
    }
}
