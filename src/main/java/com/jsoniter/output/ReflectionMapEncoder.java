package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

class ReflectionMapEncoder implements Encoder.ReflectionEncoder {

    private final TypeLiteral valueTypeLiteral;
    private final MapKeyEncoder mapKeyEncoder;

    public ReflectionMapEncoder(Class clazz, Type[] typeArgs) {
        Type keyType = String.class;
        Type valueType = Object.class;
        if (typeArgs.length == 2) {
            keyType = typeArgs[0];
            valueType = typeArgs[1];
        }
        if (keyType == String.class) {
            mapKeyEncoder = null;
        } else {
            mapKeyEncoder = DefaultMapKeyEncoder.registerOrGetExisting(keyType);
        }
        valueTypeLiteral = TypeLiteral.create(valueType);
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        if (obj == null) {
            stream.writeNull();
            return;
        }
        Map<Object, Object> map = (Map<Object, Object>) obj;
        stream.writeObjectStart();
        boolean notFirst = false;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (notFirst) {
                stream.writeMore();
            } else {
                notFirst = true;
            }
            if (mapKeyEncoder == null) {
                stream.writeObjectField((String) entry.getKey());
            } else {
                stream.writeObjectField(mapKeyEncoder.encode(entry.getKey()));
            }
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
