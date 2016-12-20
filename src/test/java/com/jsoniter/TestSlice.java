package com.jsoniter;

import junit.framework.TestCase;
import org.junit.Assert;

public class TestSlice extends TestCase {

    public void test_append() {
        Slice slice = Slice.make(0, 1);
        slice.append((byte) 1);
        Assert.assertArrayEquals(new byte[]{1}, slice.data);
        slice.append((byte) 2);
        Assert.assertArrayEquals(new byte[]{1,2}, slice.data);
    }

    public void test_hash_code() {
        Slice slice1 = Slice.make(0, 1);
        slice1.append((byte) 1);
        Slice slice2 = Slice.make(0, 1);
        slice2.append((byte) 1);
        assertEquals(slice1, slice2);
        assertEquals(slice1.hashCode(), slice2.hashCode());
    }

    public void test_equals() {
        assertTrue(Slice.make("hello").equals(Slice.make("hello")));
        assertTrue(Slice.make("hello").equals(new Slice("ahello".getBytes(), 1, 5)));
    }

    public void test_to_string() {
        Slice slice = Slice.make(0, 2);
        slice.append((byte) 'a');
        assertEquals("a", slice.toString());
    }
}
