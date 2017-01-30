package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class ReflectionMapEncoder implements Encoder {

    private final TypeLiteral valueTypeLiteral;

    public ReflectionMapEncoder(Class clazz, Type[] typeArgs) {
        if (typeArgs.length > 1) {
            valueTypeLiteral = TypeLiteral.create(typeArgs[1]);
        } else {
            valueTypeLiteral = TypeLiteral.create(Object.class);
        }
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        if (obj == null) {
            stream.writeNull();
            return;
        }
        Map<String, Object> map = (Map<String, Object>) obj;
        stream.writeObjectStart();
        boolean notFirst = false;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (notFirst) {
                stream.writeMore();
            } else {
                notFirst = true;
            }
            stream.writeObjectField(entry.getKey());
            stream.writeVal(valueTypeLiteral, entry.getValue());
        }
        stream.writeObjectEnd();
    }

    @Override
    public Any wrap(Object obj) {
        Map<String, Object> map = (Map<String, Object>) obj;
        return Any.wrap(map);
    }
}
