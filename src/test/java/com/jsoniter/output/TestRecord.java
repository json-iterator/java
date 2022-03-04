package com.jsoniter.output;

import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class TestRecord  extends TestCase {
    private ByteArrayOutputStream baos;
    private JsonStream stream;

    public void setUp() {
        baos = new ByteArrayOutputStream();
        stream = new JsonStream(baos, 4096);
    }

    record TestRecord1(float field1){

    }

    public void test_gen_record() throws IOException {
        stream.writeVal(new TestRecord1(2.5f));
        stream.close();
        assertEquals("{'field1':2.5}".replace('\'', '"'), baos.toString());
    }

    record TestRecord2(){

    }

    public void test_empty_record() throws IOException {
        stream.writeVal(new TestRecord2());
        stream.close();
        assertEquals("{}".replace('\'', '"'), baos.toString());
    }



}
