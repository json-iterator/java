package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.spi.TypeLiteral;

class BooleanObjectAny extends ObjectAny {

    private boolean val;

    public BooleanObjectAny(boolean val) {
        this.val = val;
    }

    @Override
    public ValueType valueType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public Object object() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return val;
    }

    @Override
    public int toInt() {
        return val ? 1 : 0;
    }

    @Override
    public long toLong() {
        return val ? 1 : 0;
    }

    @Override
    public float toFloat() {
        return val ? 1 : 0;
    }

    @Override
    public double toDouble() {
        return val ? 1 : 0;
    }

    @Override
    public String toString() {
        return Boolean.toString(val);
    }

    public Any set(boolean newVal) {
        val = newVal;
        return this;
    }
}
