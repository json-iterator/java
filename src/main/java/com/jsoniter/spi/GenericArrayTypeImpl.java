package com.jsoniter.spi;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayTypeImpl implements GenericArrayType {

    private final Type componentType;

    GenericArrayTypeImpl(Type componentType) {
        this.componentType = componentType;
    }

    @Override
    public Type getGenericComponentType() {
        return componentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericArrayTypeImpl that = (GenericArrayTypeImpl) o;

        return componentType != null ? componentType.equals(that.componentType) : that.componentType == null;

    }

    @Override
    public int hashCode() {
        return componentType != null ? componentType.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GenericArrayTypeImpl{" +
                "componentType=" + componentType +
                '}';
    }
}
