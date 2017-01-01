package com.jsoniter.any;

import com.jsoniter.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectLazyAny extends LazyAny {

    private Map<Object, LazyAny> cache;
    private int lastParsedPos;

    public ObjectLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
        lastParsedPos = head;
    }

    @Override
    public ValueType valueType() {
        return ValueType.OBJECT;
    }

    @Override
    public Object asObject() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        try {
            return CodegenAccess.readObjectStart(parse());
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public int size() {
        fillCache();
        return cache.size();
    }

    @Override
    public Set<String> keys() {
        fillCache();
        return (Set) cache.keySet();
    }

    @Override
    public Any get(Object key) {
        try {
            return fillCache(key);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Any get(Object... keys) {
        try {
            return get(keys, 0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Any get(Object[] keys, int idx) throws IOException {
        if (idx == keys.length) {
            return this;
        }
        return fillCache(keys[idx]).get(keys, idx+1);
    }

    @Override
    public Any require(Object... keys) {
        try {
            return require(keys, 0);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Any require(Object[] keys, int idx) throws IOException {
        if (idx == keys.length) {
            return this;
        }
        LazyAny result = fillCache(keys[idx]);
        if (result == null) {
            throw reportPathNotFound(keys, idx);
        }
        return result.require(keys, idx + 1);
    }

    private LazyAny fillCache(Object target) throws IOException {
        if (lastParsedPos == tail()) {
            return cache.get(target);
        }
        if (cache != null) {
            LazyAny value = cache.get(target);
            if (value != null) {
                return value;
            }
        }
        JsonIterator iter = tlsIter.get();
        iter.reset(data(), lastParsedPos, tail());
        if (cache == null) {
            cache = new HashMap<Object, LazyAny>(4);
        }
        if (lastParsedPos == head()) {
            if (!CodegenAccess.readObjectStart(iter)) {
                lastParsedPos = tail();
                return null;
            }
            String field = CodegenAccess.readObjectFieldAsString(iter);
            LazyAny value = iter.readAny();
            cache.put(field, value);
            if (field.hashCode() == target.hashCode() && field.equals(target)) {
                lastParsedPos = CodegenAccess.head(iter);
                return value;
            }
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            String field = CodegenAccess.readObjectFieldAsString(iter);
            LazyAny value = iter.readAny();
            cache.put(field, value);
            if (field.hashCode() == target.hashCode() && field.equals(target)) {
                lastParsedPos = CodegenAccess.head(iter);
                return value;
            }
        }
        lastParsedPos = tail();
        return null;
    }

    private void fillCache() {
        if (lastParsedPos == tail()) {
            return;
        }
        try {
            JsonIterator iter = tlsIter.get();
            iter.reset(data(), lastParsedPos, tail());
            if (cache == null) {
                cache = new HashMap<Object, LazyAny>(4);
            }
            if (!CodegenAccess.readObjectStart(iter)) {
                lastParsedPos = tail();
                return;
            }
            String field = CodegenAccess.readObjectFieldAsString(iter);
            cache.put(field, iter.readAny());
            while (CodegenAccess.nextToken(iter) == ',') {
                field = CodegenAccess.readObjectFieldAsString(iter);
                cache.put(field, iter.readAny());
            }
            lastParsedPos = tail();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
