package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

class ReflectionMapDecoder implements Decoder {

    private final Constructor ctor;
    private final TypeLiteral valueTypeLiteral;

    public ReflectionMapDecoder(Class clazz, Type[] typeArgs) {
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new JsonException(e);
        }
        valueTypeLiteral = TypeLiteral.create(typeArgs[1]);
    }

    @Override
    public Object decode(JsonIterator iter) throws IOException {
        try {
            return decode_(iter);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private Object decode_(JsonIterator iter) throws Exception {
        Map map = (Map) CodegenAccess.resetExistingObject(iter);
        if (iter.readNull()) {
            return null;
        }
        if (map == null) {
            map = (Map) ctor.newInstance();
        }
        if (!CodegenAccess.readObjectStart(iter)) {
            return map;
        }
        String field = CodegenAccess.readObjectFieldAsString(iter);
        map.put(field, CodegenAccess.read(iter, valueTypeLiteral));
        while (CodegenAccess.nextToken(iter) == ',') {
            field = CodegenAccess.readObjectFieldAsString(iter);
            map.put(field, CodegenAccess.read(iter, valueTypeLiteral));
        }
        return map;
    }
}
