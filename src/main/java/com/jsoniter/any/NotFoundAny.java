package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;

import java.io.IOException;
import java.util.Arrays;

class NotFoundAny extends Any {

    private final JsonException exception;

    public NotFoundAny(Object[] keys, int idx, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get path %s, because #%s %s not found in %s",
                Arrays.toString(keys), idx, keys[idx], obj));
    }

    public NotFoundAny(int index, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get index %s, because %s not found in %s",
                index, index, obj));
    }

    public NotFoundAny(Object key, Object obj) {
        this.exception = new JsonException(String.format("Value not found: failed to get key %s, because %s not found in %s",
                key, key, obj));
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
}
