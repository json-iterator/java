package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class TestArray extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public void test_empty_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[]");
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertEquals(0, array.length);
        iter.reset(iter.buf);
        List<String> list = iter.read(new TypeLiteral<List<String>>() {
        });
        assertEquals(0, list.size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(0, any.size());
    }

    public void test_one_element() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1]");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readInt());
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertArrayEquals(new int[]{1}, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new TypeLiteral<List<Integer>>() {
        });
        assertEquals(Arrays.asList(1), list);
        iter.reset(iter.buf);
        assertArrayEquals(new Object[]{1.0}, iter.read(Object[].class));
        iter.reset(iter.buf);
        assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        assertEquals(1, iter.readAny().toInt(0));
        iter.reset(iter.buf);
        final List<Integer> values = new ArrayList<Integer>();
        iter.readArrayCB(new JsonIterator.ReadArrayCallback() {
            @Override
            public boolean handle(JsonIterator iter, Object attachment) throws IOException {
                values.add(iter.readInt());
                return true;
            }
        }, null);
        assertEquals(Arrays.asList(1), values);
    }

    public void test_two_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2 ] ");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2}, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new TypeLiteral<List<Integer>>() {
        });
        assertEquals(Arrays.asList(1, 2), list);
        iter.reset(iter.buf);
        assertArrayEquals(new Object[]{1.0, 2.0}, iter.read(Object[].class));
        iter.reset(iter.buf);
        assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        assertEquals(1, iter.readAny().toInt(0));
        iter = JsonIterator.parse(" [ 1 , null, 2 ] ");
        assertEquals(Arrays.asList(1.0D, null, 2.0D), iter.read());
    }

    public void test_three_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3 ] ");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(3, iter.readInt());
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3}, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new TypeLiteral<List<Integer>>() {
        });
        assertEquals(Arrays.asList(1, 2, 3), list);
        iter.reset(iter.buf);
        assertArrayEquals(new Object[]{1.0, 2.0, 3.0}, iter.read(Object[].class));
        iter.reset(iter.buf);
        assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_four_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3, 4 ] ");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(3, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(4, iter.readInt());
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3, 4}, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new TypeLiteral<List<Integer>>() {
        });
        assertEquals(Arrays.asList(1, 2, 3, 4), list);
        iter.reset(iter.buf);
        assertArrayEquals(new Object[]{1.0, 2.0, 3.0, 4.0}, iter.read(Object[].class));
        iter.reset(iter.buf);
        assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_five_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3, 4, 5  ] ");
        assertTrue(iter.readArray());
        assertEquals(1, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(2, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(3, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(4, iter.readInt());
        assertTrue(iter.readArray());
        assertEquals(5, iter.readInt());
        assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new TypeLiteral<List<Integer>>() {
        });
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
        iter.reset(iter.buf);
        assertArrayEquals(new Object[]{1.0, 2.0, 3.0, 4.0, 5.0}, iter.read(Object[].class));
        iter.reset(iter.buf);
        assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null");
        assertNull(iter.read(double[].class));
    }

    public void test_boolean_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[true, false, true]");
        assertArrayEquals(new boolean[]{true, false, true}, iter.read(boolean[].class));
    }

    public void test_iterator() throws IOException {
        Any any = JsonIterator.deserialize("[1,2,3,4]");
        Iterator<Any> iter = any.iterator();
        assertEquals(1, iter.next().toInt());
        iter = any.iterator();
        assertEquals(1, iter.next().toInt());
        assertEquals(2, iter.next().toInt());
        iter = any.iterator();
        assertEquals(1, iter.next().toInt());
        assertEquals(2, iter.next().toInt());
        assertEquals(3, iter.next().toInt());
        iter = any.iterator();
        assertEquals(1, iter.next().toInt());
        assertEquals(2, iter.next().toInt());
        assertEquals(3, iter.next().toInt());
        assertEquals(4, iter.next().toInt());
        assertFalse(iter.hasNext());
    }
}
