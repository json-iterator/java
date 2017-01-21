package com.jsoniter.output;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class TestNative extends TestCase {

    static {
//        JsonStream.setMode(EncodingMode.REFLECTION_MODE);
    }

    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    public void test_string() throws IOException {
        stream = new JsonStream(baos, 32);
        stream.writeVal("1234567890123456789012345678901234567890");
        stream.close();
        assertEquals("'1234567890123456789012345678901234567890'".replace('\'', '"'), baos.toString());
    }

    public void test_slash() throws IOException {
        stream.writeVal("/\\");
        stream.close();
        assertEquals("\"/\\\\\"", baos.toString());
    }

    public void test_escape() throws IOException {
        stream.writeVal("hel\nlo");
        stream.close();
        assertEquals("'hel\\nlo'".replace('\'', '"'), baos.toString());
    }

    public void test_utf8() throws IOException {
        stream.writeVal("中文");
        stream.close();
        assertEquals("\"\\u4e2d\\u6587\"", baos.toString());
    }

    public void test_int() throws IOException {
        stream.writeVal(100);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_boxed_int() throws IOException {
        Object val = Integer.valueOf(100);
        stream.writeVal(val);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_negative_int() throws IOException {
        stream.writeVal(-100);
        stream.close();
        assertEquals("-100", baos.toString());
    }

    public void test_small_int() throws IOException {
        stream.writeVal(3);
        stream.close();
        assertEquals("3", baos.toString());
    }

    public void test_large_int() throws IOException {
        stream.writeVal(31415926);
        stream.close();
        assertEquals("31415926", baos.toString());
    }

    public void test_long() throws IOException {
        stream.writeVal(100L);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_negative_long() throws IOException {
        stream.writeVal(-100L);
        stream.close();
        assertEquals("-100", baos.toString());
    }

    public void test_short() throws IOException {
        stream.writeVal(((short)555));
        stream.close();
        assertEquals("555", baos.toString());
        assertEquals("555", JsonStream.serialize(new Short((short)555)));
    }

    public void test_no_decimal_float() throws IOException {
        stream.writeVal(100f);
        stream.close();
        assertEquals("100", baos.toString());
    }

    public void test_float2() throws IOException {
        stream.writeVal(0.000001f);
        stream.close();
        assertEquals("0.000001", baos.toString());
    }

    public void test_float3() throws IOException {
        stream.writeVal(0.00001f);
        stream.close();
        assertEquals("0.00001", baos.toString());
    }

    public void test_big_float() throws IOException {
        stream.writeVal((float)0x4ffffff);
        stream.close();
        assertEquals("83886080", baos.toString());
    }

    public void test_double() throws IOException {
        stream.writeVal(1.001d);
        stream.close();
        assertEquals("1.001", baos.toString());
    }

    public void test_large_double() throws IOException {
        stream.writeVal(Double.MAX_VALUE);
        stream.close();
        assertEquals("1.7976931348623157E308", baos.toString());
    }

    public void test_boolean() throws IOException {
        stream.writeVal(true);
        stream.writeVal(false);
        stream.close();
        assertEquals("truefalse".replace('\'', '"'), baos.toString());
    }

    public void test_big_decimal() throws IOException {
        stream.writeVal(new BigDecimal("12.34"));
        stream.close();
        assertEquals("12.34".replace('\'', '"'), baos.toString());
    }

    public void test_big_integer() throws IOException {
        stream.writeVal(new BigInteger("1234"));
        stream.close();
        assertEquals("1234".replace('\'', '"'), baos.toString());
    }

    public void test_raw() throws IOException {
        stream = new JsonStream(baos, 32);
        String val = "1234567890123456789012345678901234567890";
        stream.writeRaw(val, val.length());
        stream.close();
        assertEquals(val.replace('\'', '"'), baos.toString());
    }
}
