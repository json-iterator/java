package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestWhatIsNext extends TestCase {
    public void test() throws IOException {
        Jsoniter parser = Jsoniter.parse("{}");
        assertEquals(ValueType.OBJECT, parser.whatIsNext());
    }
}
