package com.jsoniter.any;

import com.jsoniter.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ObjectLazyAny extends LazyAny {

    private Map<Object, Any> cache;
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
    public Object object() {
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
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        try {
            Any child = fillCache(keys[idx]);
            if (child == null) {
                return null;
            }
            return child.get(keys, idx+1);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Any require(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        try {
            Any result = fillCache(keys[idx]);
            if (result == null) {
                throw reportPathNotFound(keys, idx);
            }
            return result.require(keys, idx + 1);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private Any fillCache(Object target) throws IOException {
        if (lastParsedPos == tail) {
            return cache.get(target);
        }
        if (cache != null) {
            Any value = cache.get(target);
            if (value != null) {
                return value;
            }
        }
        JsonIterator iter = tlsIter.get();
        iter.reset(data, lastParsedPos, tail);
        if (cache == null) {
            cache = new HashMap<Object, Any>(4);
        }
        if (lastParsedPos == head) {
            if (!CodegenAccess.readObjectStart(iter)) {
                lastParsedPos = tail;
                return null;
            }
            String field = CodegenAccess.readObjectFieldAsString(iter);
            Any value = iter.readAny();
            cache.put(field, value);
            if (field.hashCode() == target.hashCode() && field.equals(target)) {
                lastParsedPos = CodegenAccess.head(iter);
                return value;
            }
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            String field = CodegenAccess.readObjectFieldAsString(iter);
            Any value = iter.readAny();
            cache.put(field, value);
            if (field.hashCode() == target.hashCode() && field.equals(target)) {
                lastParsedPos = CodegenAccess.head(iter);
                return value;
            }
        }
        lastParsedPos = tail;
        return null;
    }

    private void fillCache() {
        if (lastParsedPos == tail) {
            return;
        }
        try {
            JsonIterator iter = tlsIter.get();
            iter.reset(data, lastParsedPos, tail);
            if (cache == null) {
                cache = new HashMap<Object, Any>(4);
            }
            if (!CodegenAccess.readObjectStart(iter)) {
                lastParsedPos = tail;
                return;
            }
            String field = CodegenAccess.readObjectFieldAsString(iter);
            cache.put(field, iter.readAny());
            while (CodegenAccess.nextToken(iter) == ',') {
                field = CodegenAccess.readObjectFieldAsString(iter);
                cache.put(field, iter.readAny());
            }
            lastParsedPos = tail;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
