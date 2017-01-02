package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

class TrueAny extends Any {

    public final static TrueAny INSTANCE = new TrueAny();

    @Override
    public ValueType valueType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public Object object() {
        return Boolean.TRUE;
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public int toInt() {
        return 1;
    }

    @Override
    public long toLong() {
        return 1;
    }

    @Override
    public float toFloat() {
        return 1;
    }

    @Override
    public double toDouble() {
        return 1;
    }

    @Override
    public String toString() {
        return "true";
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        stream.writeTrue();
    }
}
