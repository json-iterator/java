package com.jsoniter.any;

import com.jsoniter.JsonException;
import com.jsoniter.ValueType;

import java.io.IOException;

class LongLazyAny extends LazyAny {

    private boolean isCached;
    private long cache;

    public LongLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.NUMBER;
    }

    @Override
    public Object object() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        fillCache();
        return cache != 0;
    }

    @Override
    public int toInt() {
        fillCache();
        return (int) cache;
    }

    @Override
    public long toLong() {
        fillCache();
        return cache;
    }

    @Override
    public float toFloat() {
        fillCache();
        return cache;
    }

    @Override
    public double toDouble() {
        fillCache();
        return cache;
    }

    private void fillCache() {
        if (!isCached) {
            try {
                cache = parse().readLong();
            } catch (IOException e) {
                throw new JsonException(e);
            }
            isCached = true;
        }
    }
}
