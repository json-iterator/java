package com.jsoniter.any;

import com.jsoniter.ValueType;

class IntObjectAny extends ObjectAny {

    private int val;

    public IntObjectAny(int val) {
        this.val = val;
    }

    @Override
    public ValueType valueType() {
        return ValueType.NUMBER;
    }

    @Override
    public Object object() {
        return val;
    }

    @Override
    public boolean toBoolean() {
        return val != 0;
    }

    @Override
    public int toInt() {
        return val;
    }

    @Override
    public long toLong() {
        return val;
    }

    @Override
    public float toFloat() {
        return val;
    }

    @Override
    public double toDouble() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    public Any set(int newVal) {
        this.val = newVal;
        return this;
    }
}
