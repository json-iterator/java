package com.jsoniter;

import com.jsoniter.spi.Slice;
import junit.framework.TestCase;

import java.util.HashMap;

public class TestSlice extends TestCase {

    public void test_equals() {
        assertTrue(Slice.make("hello").equals(Slice.make("hello")));
        assertTrue(Slice.make("hello").equals(new Slice("ahello".getBytes(), 1, 6)));
    }

    public void test_hashcode() {
        HashMap map = new HashMap();
        map.put(Slice.make("hello"), "hello");
        map.put(Slice.make("world"), "world");
        assertEquals("hello", map.get(Slice.make("hello")));
        assertEquals("world", map.get(Slice.make("world")));
    }
    
    public void test_equalsInputNotNullOutputFalse2() {

        // Arrange
        final byte[] byteArray = {(byte)2, (byte)1};
        final Slice objectUnderTest = new Slice(byteArray, 0, 1073741825);
        final byte[] byteArray1 = {(byte)0};
        final Slice o = new Slice(byteArray1, 0, 1073741825);
    
        // Act
        final boolean retval = objectUnderTest.equals(o);
    
        // Assert result
        assertEquals(false, retval);
      }

      public void test_equalsInputNotNullOutputFalse() {

        // Arrange
        final Slice objectUnderTest = new Slice(null, 0, -2147483646);
        final Slice o = new Slice(null, 0, 2);
    
        // Act
        final boolean retval = objectUnderTest.equals(o);
    
        // Assert result
        assertEquals(false, retval);
      }
}
