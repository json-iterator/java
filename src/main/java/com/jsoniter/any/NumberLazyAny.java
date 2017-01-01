package com.jsoniter.any;

import com.jsoniter.JsonException;
import com.jsoniter.ValueType;

import java.io.IOException;

public class NumberLazyAny extends LazyAny {

    private boolean isCached;
    private double cache;

    public NumberLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.NUMBER;
    }

    @Override
    public Object asObject() {
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
        return (long) cache;
    }

    @Override
    public float toFloat() {
        fillCache();
        return (float) cache;
    }

    @Override
    public double toDouble() {
        fillCache();
        return cache;
    }

    private void fillCache() {
        if (!isCached) {
            try {
                cache = parse().readDouble();
            } catch (IOException e) {
                throw new JsonException(e);
            }
            isCached = true;
        }
    }
}
