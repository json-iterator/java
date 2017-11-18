package com.jsoniter.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

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
        } else if (type instanceof WildcardType) {
            clazz = Object.class;
            typeArgs = new Type[0];
        } else {
            clazz = (Class) type;
            typeArgs = new Type[0];
        }
    }
}
