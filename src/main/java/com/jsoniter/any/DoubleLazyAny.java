package com.jsoniter.any;

import com.jsoniter.JsonIterator;
import com.jsoniter.JsonIteratorPool;
import com.jsoniter.spi.JsonException;
import com.jsoniter.ValueType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

class DoubleLazyAny extends LazyAny {

    private boolean isCached;
    private double cache;

    public DoubleLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.NUMBER;
    }

    @Override
    public Object object() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        fillCache();
        return cache != 0;
    }

    @Override
    public int toInt() {
        fillCache();
        return (int) cache;
    }

    @Override
    public long toLong() {
        fillCache();
        return (long) cache;
    }

    @Override
    public float toFloat() {
        fillCache();
        return (float) cache;
    }

    @Override
    public double toDouble() {
        fillCache();
        return cache;
    }

    @Override
    public BigInteger toBigInteger() {
        return new BigInteger(toString());
    }

    @Override
    public BigDecimal toBigDecimal() {
        return new BigDecimal(toString());
    }

    private void fillCache() {
        if (!isCached) {
            JsonIterator iter = parse();
            try {
                cache = iter.readDouble();
            } catch (IOException e) {
                throw new JsonException(e);
            } finally {
                JsonIteratorPool.returnJsonIterator(iter);
            }
            isCached = true;
        }
    }
}
