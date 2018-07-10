package com.jsoniter.slice;


import org.junit.Test;

import static org.junit.Assert.*;

public class DirectSliceTest {

    @Test
    public void invariant() {
        try {
            new DirectSlice(null, 0, 0);
            fail();
        } catch (NullPointerException ignored) {
        }

        try {
            new DirectSlice(new byte[0], -1, 0);
            fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            new DirectSlice(new byte[0], 0, -1);
            fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            new DirectSlice(new byte[0], 0, 1);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void string() {
        assertEquals("abc", new DirectSlice(new byte[] { 'a', 'b', 'c' }, 0, 3).string());
        assertEquals("bc", new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 3).string());
        assertEquals("c", new DirectSlice(new byte[] { 'a', 'b', 'c' }, 2, 3).string());
        assertEquals("b", new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 2).string());
    }

    @Test
    public void hashcode() {
        DirectSlice abc = new DirectSlice(new byte[] { 'a', 'b', 'c' }, 0, 3);
        DirectSlice bc = new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 3);
        DirectSlice c = new DirectSlice(new byte[] { 'a', 'b', 'c' }, 2, 3);
        DirectSlice b = new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 2);
        assertEquals("abc".hashCode(), abc.hashCode());
        assertEquals("bc".hashCode(), bc.hashCode());
        assertEquals("c".hashCode(), c.hashCode());
        assertEquals("b".hashCode(), b.hashCode());

        assertSame(abc.string(), abc.string());
        assertSame(bc.string(), bc.string());
        assertSame(c.string(), c.string());
        assertSame(b.string(), b.string());

        // double-check after lazy String allocation

        assertEquals("abc".hashCode(), abc.hashCode());
        assertEquals("bc".hashCode(), bc.hashCode());
        assertEquals("c".hashCode(), c.hashCode());
        assertEquals("b".hashCode(), b.hashCode());
    }

    @Test
    public void equality() {
        assertEquals(
                new DirectSlice(new byte[] { 'a', 'b', 'c' }, 0, 2),
                new DirectSlice(new byte[] { 'z', 'x', 'c', 'a', 'b', 'x'}, 3, 5)
        );

        assertNotEquals(
                new DirectSlice(new byte[] { 'a', 'b', 'c' }, 0, 2),
                new DirectSlice(new byte[] { 'z', 'x', 'c', 'a', 'b', 'x'}, 2, 4)
        );
    }

}
