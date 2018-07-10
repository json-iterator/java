package com.jsoniter.slice;


import org.junit.Test;

import static org.junit.Assert.*;

public class StringSliceTest {

    @Test
    public void hashcode() {
        StringSlice abc = new StringSlice("abc");
        StringSlice bc = new StringSlice("bc");
        StringSlice c = new StringSlice("c");
        StringSlice b = new StringSlice("b");

        assertEquals("abc".hashCode(), abc.hashCode());
        assertEquals("bc".hashCode(), bc.hashCode());
        assertEquals("c".hashCode(), c.hashCode());
        assertEquals("b".hashCode(), b.hashCode());
    }

    @Test
    public void bytes() {
        StringSlice abc = new StringSlice("abc");
        StringSlice bc = new StringSlice("bc");
        StringSlice c = new StringSlice("c");
        StringSlice b = new StringSlice("b");

        assertArrayEquals(new byte[] { 'a', 'b', 'c'}, abc.data());
        assertArrayEquals(new byte[] { 'b', 'c'}, bc.data());
        assertArrayEquals(new byte[] { 'c'}, c.data());
        assertArrayEquals(new byte[] { 'b' }, b.data());
    }

}
