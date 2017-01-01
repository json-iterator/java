package com.jsoniter.any;

import com.jsoniter.ValueType;

public class NullLazyAny extends LazyAny {

    public NullLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.NULL;
    }

    @Override
    public Object asObject() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return false;
    }

    @Override
    public int toInt() {
        return 0;
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public float toFloat() {
        return 0;
    }

    @Override
    public double toDouble() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
