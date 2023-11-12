package com.jsoniter.extra;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;

public class TestPreciseFloat extends TestCase {
    static {
        PreciseFloatSupport.enable();
    }

    public void test_direct_encode() {
        assertEquals("0.123456789", JsonStream.serialize(0.123456789d));
        assertEquals("0.12345678", JsonStream.serialize(0.12345678f));
    }

    public static class TestObject1 {
        public Double field1;
        public double field2;
        public Float field3;
        public float field4;
    }

    public void test_indirect_encode() throws JSONException {
        TestObject1 obj = new TestObject1();
        obj.field1 = 0.12345678d;
        obj.field2 = 0.12345678d;
        obj.field3 = 0.12345678f;
        obj.field4 = 0.12345678f;
        JSONAssert.assertEquals("{\"field1\":0.12345678,\"field2\":0.12345678,\"field3\":0.12345678,\"field4\":0.12345678}", JsonStream.serialize(obj),false);
    }
}
