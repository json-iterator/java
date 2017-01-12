package com.jsoniter.extra;

import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

public class TestNamingStrategy extends TestCase {
    public static class TestObject1 {
        public String helloWorld;
    }
    public void test() {
        NamingStrategySupport.enable(NamingStrategySupport.SNAKE_CASE);
        TestObject1 obj = new TestObject1();
        obj.helloWorld = "!!!";
        assertEquals("{\"hello_world\":\"!!!\"}", JsonStream.serialize(obj));
    }
}
