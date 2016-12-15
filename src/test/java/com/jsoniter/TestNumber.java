package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestNumber extends TestCase {

    public void test_byte() throws IOException {
        for (String input : new String[]{"-123", " -123", "-123,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(Byte.valueOf((byte) -123), iter.read(byte.class));
            iter.reset();
            assertEquals(Byte.valueOf((byte) -123), iter.read(Byte.class));
        }
    }

    public void test_char() throws IOException {
        for (String input : new String[]{"-123", " -123", "-123,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(Character.valueOf((char) -123), iter.read(char.class));
            iter.reset();
            assertEquals(Character.valueOf((char) -123), iter.read(Character.class));
        }
    }

    public void test_short() throws IOException {
        for (String input : new String[]{"-123", " -123", "-123,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(-123, iter.readShort());
            iter.reset();
            assertEquals(Short.valueOf((short) -123), iter.read(short.class));
            iter.reset();
            assertEquals(Short.valueOf((short) -123), iter.read(Short.class));
            iter.reset();
            assertEquals(-123, iter.readAny().toShort());
        }
    }

    public void test_int() throws IOException {
        for (String input : new String[]{"-123", " -123", "-123,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(-123, iter.readInt());
            iter.reset();
            assertEquals(Integer.valueOf(-123), iter.read(int.class));
            iter.reset();
            assertEquals(Integer.valueOf(-123), iter.read(Integer.class));
            iter.reset();
            assertEquals(-123, iter.readAny().toInt());
        }
    }

    public void test_long() throws IOException {
        for (String input : new String[]{"-123", " -123", "-123,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(-123, iter.readLong());
            iter.reset();
            assertEquals(Long.valueOf(-123), iter.read(long.class));
            iter.reset();
            assertEquals(Long.valueOf(-123), iter.read(Long.class));
            iter.reset();
            assertEquals(-123, iter.readAny().toLong());
        }
    }

    public void test_float() throws IOException {
        for (String input : new String[]{"1.12345", " 1.12345", "1.12345,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(1.12345f, iter.readFloat());
            iter.reset();
            assertEquals(1.12345f, iter.read(float.class));
            iter.reset();
            assertEquals(1.12345f, iter.read(Float.class));
            iter.reset();
            assertEquals(1.12345f, iter.readAny().toFloat());
        }
    }

    public void test_negative_float() throws IOException {
        for (String input : new String[]{"-1.12345", " -1.12345", "-1.12345,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(-1.12345f, iter.readFloat());
            iter.reset();
            assertEquals(-1.12345f, iter.read(float.class));
            iter.reset();
            assertEquals(-1.12345f, iter.read(Float.class));
            iter.reset();
            assertEquals(-1.12345f, iter.readAny().toFloat());
        }
    }

    public void test_double() throws IOException {
        for (String input : new String[]{"1.12345", " 1.12345", "1.12345,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(1.12345d, iter.readDouble());
            iter.reset();
            assertEquals(1.12345d, iter.read(double.class));
            iter.reset();
            assertEquals(1.12345d, iter.read(Double.class));
            iter.reset();
            assertEquals(1.12345d, iter.readAny().toDouble());
        }
    }

    public void test_negative_double() throws IOException {
        for (String input : new String[]{"-1.12345", " -1.12345", "-1.12345,"}) {
            Jsoniter iter = Jsoniter.parse(input);
            assertEquals(-1.12345d, iter.readDouble());
            iter.reset();
            assertEquals(-1.12345d, iter.read(double.class));
            iter.reset();
            assertEquals(-1.12345d, iter.read(Double.class));
            iter.reset();
            assertEquals(-1.12345d, iter.readAny().toDouble());
        }
    }
}
