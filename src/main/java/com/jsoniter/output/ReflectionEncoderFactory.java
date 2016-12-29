package com.jsoniter.output;

import com.jsoniter.spi.Encoder;

import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionEncoderFactory {

    public static Encoder create(Class clazz, Type... typeArgs) {
        if (clazz.isArray()) {
            return new ReflectionArrayEncoder(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ReflectionCollectionEncoder(clazz, typeArgs);
        }
        return new ReflectionObjectEncoder(clazz);
    }
}
