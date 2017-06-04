package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestSpiTypeDecoder extends TestCase {

    public static class TestObject1 {
        public int field1;

        private TestObject1() {
        }
    }

    public void test_TypeDecoder() throws IOException {
        JsoniterSpi.registerTypeDecoder(TestObject1.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestObject1 obj = new TestObject1();
                obj.field1 = 101;
                return obj;
            }
        });
        TestObject1 obj = JsonIterator.deserialize(
                "{'field1': 100}".replace('\'', '"'), TestObject1.class);
        assertEquals(101, obj.field1);
    }

    public void test_TypeDecoder_for_generics() throws IOException {
        TypeLiteral<List<TestObject1>> typeLiteral = new TypeLiteral<List<TestObject1>>() {
        };
        JsoniterSpi.registerTypeDecoder(typeLiteral, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestObject1 obj = new TestObject1();
                obj.field1 = 101;
                return Arrays.asList(obj);
            }
        });
        List<TestObject1> objs = JsonIterator.deserialize(
                "{'field1': 100}".replace('\'', '"'), typeLiteral);
        assertEquals(101, objs.get(0).field1);
    }
}
