package com.github.jsoniter;

import junit.framework.TestCase;

public class TestUtf8 extends TestCase {
    public void test_utf8() {
        byte[] bytes = {(byte) 0xe4, (byte) 0xb8, (byte) 0xad, (byte) 0xe6, (byte) 0x96, (byte) 0x87};
        assertEquals("中文", new Slice(bytes, 0, bytes.length).toString());
    }
    public void test_normal_escape() {
        byte[] bytes = {(byte) '\\', (byte) 't'};
        assertEquals("\t", new Slice(bytes, 0, bytes.length).toString());
    }
    public void test_unicode_escape() {
        byte[] bytes = {(byte) '\\', (byte) 'u', (byte)'4', (byte)'e', (byte)'2', (byte)'d'};
        assertEquals("中", new Slice(bytes, 0, bytes.length).toString());
    }
}
