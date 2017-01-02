package com.jsoniter.any;

import com.jsoniter.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.TypeLiteral;

import java.util.Arrays;
import java.util.Set;

public abstract class Any implements Iterable<Any> {

    public abstract ValueType valueType();

    public abstract <T> T bindTo(T obj, Object... keys);

    public abstract <T> T bindTo(T obj);

    public abstract <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys);

    public abstract <T> T bindTo(TypeLiteral<T> typeLiteral, T obj);

    public abstract Object object(Object... keys);

    public abstract Object object();

    public abstract <T> T as(Class<T> clazz, Object... keys);

    public abstract <T> T as(Class<T> clazz);

    public abstract <T> T as(TypeLiteral<T> typeLiteral, Object... keys);

    public abstract <T> T as(TypeLiteral<T> typeLiteral);

    public abstract boolean toBoolean(Object... keys);

    public abstract boolean toBoolean();

    public abstract int toInt(Object... keys);

    public abstract int toInt();

    public abstract long toLong(Object... keys);

    public abstract long toLong();

    public abstract float toFloat(Object... keys);

    public abstract float toFloat();

    public abstract double toDouble(Object... keys);

    public abstract double toDouble();

    public abstract String toString(Object... keys);

    public abstract int size();

    public abstract Set<String> keys();

    public abstract Any get(int index);

    public abstract Any get(Object key);

    public Any get(Object... keys) {
        if (keys.length == 0) {
            return this;
        }
        return null;
    }

    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        return null;
    }

    public Any require(Object... keys) {
        if (keys.length == 0) {
            return this;
        }
        throw reportPathNotFound(keys, 0);
    }

    public Any require(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        throw reportPathNotFound(keys, idx);
    }

    public Any set(Object newVal) {
        return wrap(newVal);
    }

    public Any set(boolean newVal) {
        return wrap(newVal);
    }

    public Any set(int newVal) {
        return wrap(newVal);
    }

    public Any set(long newVal) {
        return wrap(newVal);
    }

    public Any set(float newVal) {
        return wrap(newVal);
    }

    public Any set(double newVal) {
        return wrap(newVal);
    }

    public Any set(String newVal) {
        return wrap(newVal);
    }

    public abstract JsonIterator parse();

    protected JsonException reportPathNotFound(Object[] keys, int idx) {
        throw new JsonException(String.format("failed to get path %s, because #%s %s not found in %s",
                Arrays.toString(keys), idx, keys[idx], object()));
    }

    protected JsonException reportUnexpectedType(ValueType toType) {
        throw new JsonException(String.format("can not convert %s to %s", valueType(), toType));
    }

    public static Any lazyString(byte[] data, int head, int tail) {
        return new StringLazyAny(data, head, tail);
    }

    public static Any lazyNumber(byte[] data, int head, int tail) {
        return new NumberLazyAny(data, head, tail);
    }

    public static Any lazyBoolean(byte[] data, int head, int tail) {
        // TODO: remove lazy boolean
        return new BooleanLazyAny(data, head, tail);
    }

    public static Any lazyNull(byte[] data, int head, int tail) {
        // TODO: remove lazy null
        return new NullLazyAny(data, head, tail);
    }

    public static Any lazyArray(byte[] data, int head, int tail) {
        return new ArrayLazyAny(data, head, tail);
    }

    public static Any lazyObject(byte[] data, int head, int tail) {
        return new ObjectLazyAny(data, head, tail);
    }

    public static Any wrap(int val) {
        return new IntObjectAny(val);
    }

    public static Any wrap(long val) {
        return new LongObjectAny(val);
    }

    public static Any wrap(float val) {
        return new FloatObjectAny(val);
    }

    public static Any wrap(double val) {
        return new DoubleObjectAny(val);
    }

    public static Any wrap(boolean val) {
        return new BooleanObjectAny(val);
    }

    public static Any wrap(String val) {
        return new StringObjectAny(val);
    }

    public static Any wrap(Object val) {
        return NullObjectAny.INSTANCE;
    }
}
