package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestRecord extends TestCase {

    record TestRecord1(long field1) {

    }

    public void test_record_error() throws IOException {

        JsonIterator iter = JsonIterator.parse("{ 'field1' : 1".replace('\'', '"'));
        iter.read(TestRecord1.class);
    }
}
