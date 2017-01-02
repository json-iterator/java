package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

class NullAny extends Any {

    public final static NullAny INSTANCE = new NullAny();

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
    public void writeTo(JsonStream stream) throws IOException {
        stream.writeNull();
    }

    @Override
    public String toString() {
        return "null";
    }
}
