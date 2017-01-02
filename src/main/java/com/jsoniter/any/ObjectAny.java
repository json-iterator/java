package com.jsoniter.any;

import com.jsoniter.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

abstract class ObjectAny extends Any {

    @Override
    public <T> T bindTo(T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(obj);
    }

    public <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(typeLiteral, obj);
    }

    @Override
    public final <T> T bindTo(T obj) {
        return (T) object();
    }

    @Override
    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj) {
        return (T) object();
    }

    @Override
    public final <T> T as(Class<T> clazz) {
        return (T) object();
    }

    @Override
    public final <T> T as(TypeLiteral<T> typeLiteral) {
        return (T) object();
    }

    @Override
    public Object object(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.object();
    }

    @Override
    public <T> T as(Class<T> clazz, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(clazz);
    }

    @Override
    public <T> T as(TypeLiteral<T> typeLiteral, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(typeLiteral);
    }

    public final boolean toBoolean(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return false;
        }
        return found.toBoolean();
    }

    public boolean toBoolean() {
        throw reportUnexpectedType(ValueType.BOOLEAN);
    }

    public final int toInt(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toInt();
    }

    public int toInt() {
        throw reportUnexpectedType(ValueType.NUMBER);
    }

    public final long toLong(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toLong();
    }

    public long toLong() {
        throw reportUnexpectedType(ValueType.NUMBER);
    }

    public final float toFloat(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toFloat();
    }

    public float toFloat() {
        throw reportUnexpectedType(ValueType.NUMBER);
    }

    public final double toDouble(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toDouble();
    }

    public double toDouble() {
        throw reportUnexpectedType(ValueType.NUMBER);
    }

    public final String toString(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.toString();
    }

    public int size() {
        return 0;
    }

    public Set<String> keys() {
        return LazyAny.EMPTY_KEYS;
    }

    @Override
    public Iterator<Any> iterator() {
        return LazyAny.EMPTY_ITERATOR;
    }

    public Any get(int index) {
        return null;
    }

    public Any get(Object key) {
        return null;
    }

    public final JsonIterator parse() {
        throw new UnsupportedOperationException();
    }


}
