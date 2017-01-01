package com.jsoniter.any;

import com.jsoniter.*;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.*;

public abstract class LazyAny extends Slice implements Any {

    private final static Set<String> EMPTY_KEYS = Collections.unmodifiableSet(new HashSet<String>());
    private final static Iterator<Any> EMPTY_ITERATOR = new Iterator<Any>() {
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Any next() {
            throw new UnsupportedOperationException();
        }
    };
    protected final static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };

    public LazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    public abstract ValueType valueType();

    public final <T> T bindTo(T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(obj);
    }

    public final <T> T bindTo(T obj) {
        try {
            return parse().read(obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(typeLiteral, obj);
    }

    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj) {
        try {
            return parse().read(typeLiteral, obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final Object asObject(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.asObject();
    }

    public abstract Object asObject();

    public final <T> T as(Class<T> clazz, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(clazz);
    }

    public final <T> T as(Class<T> clazz) {
        try {
            return parse().read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T as(TypeLiteral<T> typeLiteral, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(typeLiteral);
    }

    public final <T> T as(TypeLiteral<T> typeLiteral) {
        try {
            return parse().read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
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
        return EMPTY_KEYS;
    }

    @Override
    public Iterator<Any> iterator() {
        return EMPTY_ITERATOR;
    }

    public Any get(int index) {
        return null;
    }

    public Any get(Object key) {
        return null;
    }

    public Any get(Object... keys) {
        if (keys.length == 0) {
            return this;
        }
        return null;
    }

    public Any get(Object[] keys, int idx) throws IOException {
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

    public Any require(Object[] keys, int idx) throws IOException {
        if (idx == keys.length) {
            return this;
        }
        throw reportPathNotFound(keys, idx);
    }

    protected JsonException reportPathNotFound(Object[] keys, int idx) {
        throw new JsonException(String.format("failed to get path %s, because #%s %s not found in %s",
                Arrays.toString(keys), idx, keys[idx], asObject()));
    }

    protected JsonException reportUnexpectedType(ValueType toType) {
        throw new JsonException(String.format("can not convert %s to %s", valueType(), toType));
    }

    public final JsonIterator parse() {
        JsonIterator iter = tlsIter.get();
        iter.reset(this);
        return iter;
    }
}
