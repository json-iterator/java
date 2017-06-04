package com.jsoniter;

import com.jsoniter.spi.Decoder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

class ReflectionDecoderFactory {
    public static Decoder create(Class clazz, Type... typeArgs) {
        if (clazz.isArray()) {
            return new ReflectionArrayDecoder(clazz);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ReflectionCollectionDecoder(clazz, typeArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return new ReflectionMapDecoder(clazz, typeArgs);
        }
        if (clazz.isEnum()) {
            return new ReflectionEnumDecoder(clazz);
        }
        return new ReflectionObjectDecoder(clazz).create();
    }
}
