package com.jsoniter.spi;

import com.jsoniter.JsonException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ParameterizedTypeImpl implements ParameterizedType {
    private final Type[] actualTypeArguments;
    private final Type   ownerType;
    private final Type   rawType;

    public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType){
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType;
        this.rawType = rawType;
    }

    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    public Type getOwnerType() {
        return ownerType;
    }

    public Type getRawType() {
        return rawType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(actualTypeArguments, that.actualTypeArguments)) return false;
        if (ownerType != null ? !ownerType.equals(that.ownerType) : that.ownerType != null) return false;
        return rawType != null ? rawType.equals(that.rawType) : that.rawType == null;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(actualTypeArguments);
        result = 31 * result + (ownerType != null ? ownerType.hashCode() : 0);
        result = 31 * result + (rawType != null ? rawType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String rawTypeName = rawType.toString();
        if (rawType instanceof Class) {
            Class clazz = (Class) rawType;
            rawTypeName = clazz.getName();
        }
        return "ParameterizedTypeImpl{" +
                "actualTypeArguments=" + Arrays.toString(actualTypeArguments) +
                ", ownerType=" + ownerType +
                ", rawType=" + rawTypeName +
                '}';
    }

    public static boolean isSameClass(Type type, Class clazz) {
        if (type == clazz) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType.getRawType() == clazz;
        }
        return false;
    }

    public static Type useImpl(Type type, Class clazz) {
        if (type instanceof Class) {
            return clazz;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return new ParameterizedTypeImpl(pType.getActualTypeArguments(), pType.getOwnerType(), clazz);
        }
        throw new JsonException("can not change impl for: " + type);
    }
}
