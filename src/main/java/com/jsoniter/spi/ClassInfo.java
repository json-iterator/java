package com.jsoniter.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassInfo {

    public final Type type;
    public final Class clazz;
    public final Type[] typeArgs;

    public ClassInfo(Type type) {
        this.type = type;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            clazz = (Class) pType.getRawType();
            typeArgs = pType.getActualTypeArguments();
        } else {
            clazz = (Class) type;
            typeArgs = new Type[0];
        }
    }
}
