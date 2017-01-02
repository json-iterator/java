package com.jsoniter.any;

import com.jsoniter.ValueType;

class NullObjectAny extends ObjectAny {

    public final static NullObjectAny INSTANCE = new NullObjectAny();

    @Override
    public ValueType valueType() {
        return ValueType.NULL;
    }

    @Override
    public Object object() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return false;
    }

    @Override
    public String toString() {
        return "null";
    }
}
