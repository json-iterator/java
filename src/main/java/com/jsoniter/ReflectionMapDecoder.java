package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

class ReflectionMapDecoder implements Decoder {

    private final Constructor ctor;
    private final Decoder valueTypeDecoder;
    private final MapKeyCodec mapKeyCodec;

    public ReflectionMapDecoder(Class clazz, Type[] typeArgs) {
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new JsonException(e);
        }
        Type keyType = typeArgs[0];
        if (keyType == String.class) {
            mapKeyCodec = null;
        } else {
            mapKeyCodec = MapKeyCodecs.register(keyType);
        }
        TypeLiteral valueTypeLiteral = TypeLiteral.create(typeArgs[1]);
        valueTypeDecoder = Codegen.getDecoder(valueTypeLiteral.getDecoderCacheKey(), typeArgs[1]);
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
        do {
            Object decodedMapKey = readMapKey(iter);
            map.put(decodedMapKey, valueTypeDecoder.decode(iter));
        } while(CodegenAccess.nextToken(iter) == ',');
        return map;
    }

    private Object readMapKey(JsonIterator iter) throws IOException {
        if (mapKeyCodec == null) {
            return CodegenAccess.readObjectFieldAsString(iter);
        } else {
            Slice mapKey = CodegenAccess.readObjectFieldAsSlice(iter);
            return mapKeyCodec.decode(mapKey);
        }
    }
}
