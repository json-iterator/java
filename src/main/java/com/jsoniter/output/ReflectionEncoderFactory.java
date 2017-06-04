package com.jsoniter.output;

import com.jsoniter.spi.ClassInfo;
import com.jsoniter.spi.Encoder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReflectionEncoderFactory {

    public static Encoder create(ClassInfo classInfo) {
        Class clazz = classInfo.clazz;
        Type[] typeArgs = classInfo.typeArgs;
        if (clazz.isArray()) {
            return new ReflectionArrayEncoder(clazz, typeArgs);
        }
        if (List.class.isAssignableFrom(clazz)) {
            return new ReflectionListEncoder(clazz, typeArgs);
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
        return new ReflectionObjectEncoder(classInfo);
    }
}
