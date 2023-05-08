package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

class NotFoundAny extends Any {

    private Object[] keys;
    private int idx;
    private Object obj, key;
    private final int exceptionMode;

    public NotFoundAny(Object[] keys, int idx, Object obj) {
        this.keys = keys;
        this.idx = idx;
        this.obj = obj;
        exceptionMode = 0;
    }

    public NotFoundAny(int index, Object obj) {
        this.idx = index;
        this.obj = obj;
        exceptionMode = 1;
    }

    public NotFoundAny(Object key, Object obj) {
        this.key = key;
        this.obj = obj;
        exceptionMode = 2;
    }

    @Override
    public ValueType valueType() {
        return ValueType.INVALID;
    }

    @Override
    public Object object() {
        throwException();
        return null;
    }

    void throwException() {
        switch (exceptionMode) {
            case 0:
                throw new JsonException(String.format("Value not found: failed to get path %s, because #%s section of the path ( %s ) not found in %s",
                        Arrays.toString(keys), idx, keys[idx], obj));
            case 1:
                throw new JsonException(String.format("Value not found: failed to get index %s from %s",
                        idx, obj));
            case 2:
                throw new JsonException(String.format("Value not found: failed to get key %s from %s",
                        key, obj));
        }
        throw new JsonException();
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        throwException();
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
    public BigInteger toBigInteger() {
        return BigInteger.ZERO;
    }

    @Override
    public BigDecimal toBigDecimal() {
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "";
    }
}
