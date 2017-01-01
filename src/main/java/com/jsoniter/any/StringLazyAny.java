package com.jsoniter.any;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;

import java.io.IOException;

public class StringLazyAny extends LazyAny {
    private final static String FALSE = "false";
    private String cache;

    public StringLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.STRING;
    }

    @Override
    public Object asObject() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        fillCache();
        int len = cache.length();
        if (len == 0) {
            return false;
        }
        if (len == 5 && FALSE.equals(cache)) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            switch (cache.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    continue;
                default:
                    return true;
            }
        }
        return false;
    }

    @Override
    public int toInt() {
        try {
            JsonIterator iter = parse();
            CodegenAccess.nextToken(iter);
            return iter.readInt();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public long toLong() {
        try {
            JsonIterator iter = parse();
            CodegenAccess.nextToken(iter);
            return iter.readLong();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public float toFloat() {
        try {
            JsonIterator iter = parse();
            CodegenAccess.nextToken(iter);
            return iter.readFloat();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public double toDouble() {
        try {
            JsonIterator iter = parse();
            CodegenAccess.nextToken(iter);
            return iter.readDouble();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public String toString() {
        fillCache();
        return cache;
    }

    private void fillCache() {
        if (cache == null) {
            try {
                cache = parse().readString();
            } catch (IOException e) {
                throw new JsonException();
            }
        }
    }
}
