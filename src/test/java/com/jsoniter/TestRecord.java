package com.jsoniter;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;
import junit.framework.Test;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

public class TestRecord extends TestCase {

    record TestRecord1(long field1) {

    }

    public void test_record_error() throws IOException {

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1".replace('\'', '"'));
        try{
            TestRecord1 rec = iter.read(TestRecord1.class);
            assertEquals(1, rec.field1);
        }catch (JsonException e) {
            throw new JsonException("no constructor for: class com.jsoniter.TestRecord", e);
        }
    }

    public void test_record_serialize(){
        assertEquals("{\"field1\":1}",JsonStream.serialize(new TestRecord1(1)));
    }
}
