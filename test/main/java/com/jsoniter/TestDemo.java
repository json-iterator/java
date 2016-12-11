package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestDemo extends TestCase {
    public void test_bind_api() throws IOException {
        Jsoniter iter = Jsoniter.parse("[0,1,2,3]");
        int[] val = iter.read(int[].class);
        System.out.println(val[3]);
    }
    public void test_any_api() throws IOException {
        Jsoniter iter = Jsoniter.parse("[0,1,2,3]");
        System.out.println(iter.readAny().toInt(3));
    }
    public void test_iterator_api() throws IOException {
        Jsoniter iter = Jsoniter.parse("[0,1,2,3]");
        int total = 0;
        while(iter.readArray()) {
            total += iter.readInt();
        }
        System.out.println(total);
    }
}
