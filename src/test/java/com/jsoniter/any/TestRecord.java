package com.jsoniter.any;
import junit.framework.TestCase;

import java.util.*;

public class TestRecord extends TestCase {

    record TestRecord1(int field1) {

    }

    public void test_wrap_int(){
        Any any = Any.wrap(new TestRecord1(3));
        assertEquals(3, any.get("field1").toInt());
    }

    record TestRecord2(int field1, String field2) {

    }

    public void test_iterator(){
        Any any = Any.wrap(new TestRecord2(3,"hej"));
        Any.EntryIterator iter = any.entries();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while (iter.next()) {
            if(iter.key() == "field1"){
                assertEquals(3,iter.value().toInt());
            }
            if(iter.key() == "field2"){
                assertEquals("hej",iter.value().toString());
            }
        }
    }

    public void test_size() {
        Any any = Any.wrap(new TestRecord2(7,"ho"));
        assertEquals(2, any.size());
    }

    public void test_to_string() {
        assertEquals("{\"field1\":7,\"field2\":\"hej\"}", Any.wrap(new TestRecord2(7,"hej")).toString());
    }

    record TestRecord3(){

    }

    public void test_to_boolean() {
        Any any = Any.wrap(new TestRecord3());
        assertFalse(any.toBoolean());
        any = Any.wrap(new TestRecord2(1,"hallo"));
        assertTrue(any.toBoolean());
    }

}
