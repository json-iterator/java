package com.jsoniter;

import com.jsoniter.output.JsonStream;
import junit.framework.Test;
import junit.framework.TestCase;

import java.io.IOException;

public class TestRecord extends TestCase {

    record TestRecord1(long field1) {

    }

    public void test_record_error() throws IOException {

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1".replace('\'', '"'));
        iter.read(TestRecord1.class);
    }

    public void test_record_serialize(){
        assertEquals("{\"field1\":1}",JsonStream.serialize(new TestRecord1(1)));
    }
}
