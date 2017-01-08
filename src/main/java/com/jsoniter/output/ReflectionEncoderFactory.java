package com.jsoniter.output;

import com.jsoniter.spi.Encoder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class ReflectionEncoderFactory {

    public static Encoder create(Class clazz, Type... typeArgs) {
        if (clazz.isArray()) {
            return new ReflectionArrayEncoder(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ReflectionCollectionEncoder(clazz, typeArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return new ReflectionMapEncoder(clazz, typeArgs);
        }
        if (clazz.isEnum()) {
            return new ReflectionEnumEncoder(clazz);
        }
        return new ReflectionObjectEncoder(clazz);
    }
}
