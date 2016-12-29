package com.jsoniter.output;

import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

class ReflectionArrayEncoder implements Encoder {

    private final TypeLiteral compTypeLiteral;

    public ReflectionArrayEncoder(Class clazz, Type[] typeArgs) {
        compTypeLiteral = TypeLiteral.create(clazz.getComponentType());
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        if (null == obj) {
            stream.writeNull();
            return;
        }
        int len = Array.getLength(obj);
        if (len == 0) {
            stream.writeEmptyArray();
            return;
        }
        stream.writeArrayStart();
        stream.writeVal(compTypeLiteral, Array.get(obj, 0));
        for (int i = 1; i < len; i++) {
            stream.writeMore();
            stream.writeVal(compTypeLiteral, Array.get(obj, i));
        }
        stream.writeArrayEnd();
    }
}
