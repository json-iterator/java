package com.jsoniter;

import com.jsoniter.Jsoniter;
import com.jsoniter.ValueType;
import junit.framework.TestCase;

import java.io.IOException;

public class TestWhatIsNext extends TestCase {
    public void test() throws IOException {
        Jsoniter parser = Jsoniter.parse("{}");
        assertEquals(ValueType.OBJECT, parser.whatIsNext());
    }
}
