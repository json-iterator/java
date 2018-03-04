package com.jsoniter.slice;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SlicesInterop {

    @Test
    public void equality() {
        DirectSlice direct = new DirectSlice(new byte[] { 'z', 'x', 'a', 'b', 'c' }, 2, 5);
        StringSlice str = new StringSlice("abc");
        assertEquals(direct, str);

        direct.reset(direct.data(), direct.head()-1, direct.tail());
        assertNotEquals(direct, str);

        str.reset("xabc");
        assertEquals(direct, str);
    }

    @Test
    public void hashCodes() {
        assertEquals(
                new DirectSlice(new byte[] { 'z', 'x', 'a', 'b', 'c' }, 2, 5),
                new StringSlice("abc")
        );

        assertEquals(
                new DirectSlice(new byte[] { 'w', 'h', 'a', 't', 'e', 'v', 'e', 'r' }, 0, 8).hashCode() ==
                        new StringSlice("qwerty").hashCode(),
                "whatever".hashCode() == "qwerty".hashCode()
        );

        assertEquals(
                new DirectSlice(new byte[] { 'r', 'e', 'd' }, 0, 3).hashCode() ==
                        new StringSlice("green").hashCode(),
                "red".hashCode() == "greed".hashCode()
        );
    }

    @Test
    public void strings() {
        assertEquals(
                new DirectSlice(new byte[] { 'a', 'b', 'c' }, 0, 3).string(),
                new StringSlice("abc").string()
        );
        assertEquals(
                new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 3).string(),
                new StringSlice("bc").string()
        );
        assertEquals(
                new DirectSlice(new byte[] { 'a', 'b', 'c' }, 1, 2).string(),
                new StringSlice("b").string()
        );
    }

}
