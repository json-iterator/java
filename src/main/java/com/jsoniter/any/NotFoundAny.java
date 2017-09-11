package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;

import java.io.IOException;
import java.util.Arrays;

class NotFoundAny extends Any {

    protected final JsonException exception;

    public NotFoundAny(Object[] keys, int idx, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get path %s, because #%s section of the path ( %s ) not found in %s",
                Arrays.toString(keys), idx, keys[idx], obj));
    }

    public NotFoundAny(int index, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get index %s from %s",
                index, obj));
    }

    public NotFoundAny(Object key, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get key %s from %s",
                key, obj));
    }

    @Override
    public ValueType valueType() {
        return ValueType.INVALID;
    }

    @Override
    public Object object() {
        throw exception;
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        throw exception;
    }

    @Override
    public Any get(int index) {
        return this;
    }

    @Override
    public Any get(Object key) {
        return this;
    }

    @Override
    public Any get(Object[] keys, int idx) {
        return this;
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
        return "";
    }
}
