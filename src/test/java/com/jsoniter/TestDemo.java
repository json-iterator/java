package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

public class TestDemo extends TestCase {
    public void test_bind_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        int[] val = iter.read(int[].class);
        System.out.println(val[3]);
    }

    public void test_any_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        System.out.println(iter.readAny().toInt(3));
    }

    public void test_iterator_api() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        int total = 0;
        while (iter.readArray()) {
            total += iter.readInt();
        }
        System.out.println(total);
    }

    public static class ABC {
        public Any a;
    }

    public void test_abc() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'a': {'b': {'c': 'd'}}}".replace('\'', '"'));
        ABC abc = iter.read(ABC.class);
        System.out.println(abc.a.get("b", "c"));
    }

    public void test_iterator_api_and_bind() throws IOException {
        JsonIterator iter = JsonIterator.parse("[123, {'name': 'taowen', 'tags': ['crazy', 'hacker']}]".replace('\'', '"'));
        iter.readArray();
        int userId = iter.readInt();
        iter.readArray();
        User user = iter.read(User.class);
        user.userId = userId;
        iter.readArray(); // end of array
        System.out.println(user);
    }

    public void test_empty_array_as_null() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public Decoder createDecoder(final String cacheKey, final Type type) {
                if (cacheKey.endsWith(".original")) {
                    // avoid infinite loop
                    return null;
                }
                if (type != Date.class) {
                    return null;
                }
                return new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter1) throws IOException {
                        if (iter1.whatIsNext() == ValueType.ARRAY) {
                            if (iter1.readArray()) {
                                // none empty array
                                throw iter1.reportError("decode [] as null", "only empty array is expected");
                            } else {
                                return null;
                            }
                        } else {
                            // just use original decoder
                            TypeLiteral typeLiteral = new TypeLiteral(type, cacheKey + ".original",
                                    TypeLiteral.generateEncoderCacheKey(type));
                            return iter1.read(typeLiteral);
                        }
                    }
                };
            }
        });
        JsonIterator iter = JsonIterator.parse("[]");
        assertNull(iter.read(Date.class));
    }
}
