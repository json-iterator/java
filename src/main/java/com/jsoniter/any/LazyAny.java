package com.jsoniter.any;

import com.jsoniter.spi.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;

abstract class LazyAny extends Any {

    protected static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };

    protected final byte[] data;
    protected final int head;
    protected final int tail;

    public LazyAny(byte[] data, int head, int tail) {
        this.data = data;
        this.head = head;
        this.tail = tail;
    }

    public abstract ValueType valueType();

    public final <T> T bindTo(T obj) {
        try {
            return parse().read(obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj) {
        try {
            return parse().read(typeLiteral, obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T as(Class<T> clazz) {
        try {
            return parse().read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T as(TypeLiteral<T> typeLiteral) {
        try {
            return parse().read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public String toString() {
        return new String(data, head, tail - head);
    }

    public final JsonIterator parse() {
        JsonIterator iter = tlsIter.get();
        iter.reset(data, head, tail);
        return iter;
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        stream.write(data, head, tail - head);
    }
}
