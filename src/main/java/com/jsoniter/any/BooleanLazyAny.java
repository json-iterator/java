package com.jsoniter.any;

import com.jsoniter.JsonException;
import com.jsoniter.ValueType;

import java.io.IOException;

public class BooleanLazyAny extends LazyAny {

    private boolean isCached;
    private boolean cache;

    public BooleanLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public Object asObject() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        fillCache();
        return cache;
    }

    @Override
    public int toInt() {
        fillCache();
        return cache ? 1 : 0;
    }

    @Override
    public long toLong() {
        fillCache();
        return cache ? 1 : 0;
    }

    @Override
    public float toFloat() {
        fillCache();
        return cache ? 1 : 0;
    }

    @Override
    public double toDouble() {
        fillCache();
        return cache ? 1 : 0;
    }

    private void fillCache() {
        if (!isCached) {
            try {
                cache = parse().readBoolean();
            } catch (IOException e) {
                throw new JsonException(e);
            }
            isCached = true;
        }
    }
}
