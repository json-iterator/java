package com.jsoniter.any;

import com.jsoniter.*;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class ArrayLazyAny extends LazyAny {

    private final static TypeLiteral<List<Any>> typeLiteral = new TypeLiteral<List<Any>>(){};
    private List<Any> cache;
    private int lastParsedPos;

    public ArrayLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
        lastParsedPos = head;
    }

    @Override
    public ValueType valueType() {
        return ValueType.ARRAY;
    }

    @Override
    public Object object() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        try {
            return CodegenAccess.readArrayStart(parse());
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
    public Iterator<Any> iterator() {
        if (lastParsedPos == tail) {
            return cache.iterator();
        } else {
            return new LazyIterator();
        }
    }

    @Override
    public Any get(int index) {
        try {
            return fillCache(index);
        } catch (IndexOutOfBoundsException e) {
            return new NotFoundAny(index, object());
        }
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Object key = keys[idx];
        if (isWildcard(key)) {
            fillCache();
            ArrayList<Any> result = new ArrayList<Any>();
            for (Any element : cache) {
                Any mapped = element.get(keys, idx + 1);
                if (mapped.valueType() != ValueType.INVALID) {
                    result.add(mapped);
                }
            }
            return Any.wrapAnyList(result);
        }
        try {
            return fillCache((Integer) key).get(keys, idx + 1);
        } catch (IndexOutOfBoundsException e) {
            return new NotFoundAny(keys, idx, object());
        } catch (ClassCastException e) {
            return new NotFoundAny(keys, idx, object());
        }
    }

    private void fillCache() {
        if (lastParsedPos == tail) {
            return;
        }
        if (cache == null) {
            cache = new ArrayList<Any>(4);
        }
        try {
            JsonIterator iter = JsonIterator.tlsIter.get();
            iter.reset(data, lastParsedPos, tail);
            if (lastParsedPos == head) {
                if (!CodegenAccess.readArrayStart(iter)) {
                    lastParsedPos = tail;
                    return;
                }
                cache.add(iter.readAny());
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                cache.add(iter.readAny());
            }
            lastParsedPos = tail;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private Any fillCache(int target) {
        if (lastParsedPos == tail) {
            return cache.get(target);
        }
        if (cache == null) {
            cache = new ArrayList<Any>(4);
        }
        int i = cache.size();
        if (target < i) {
            return cache.get(target);
        }
        try {
            JsonIterator iter = JsonIterator.tlsIter.get();
            iter.reset(data, lastParsedPos, tail);
            if (lastParsedPos == head) {
                if (!CodegenAccess.readArrayStart(iter)) {
                    lastParsedPos = tail;
                    throw new IndexOutOfBoundsException();
                }
                Any element = iter.readAny();
                cache.add(element);
                if (target == 0) {
                    lastParsedPos = CodegenAccess.head(iter);
                    return element;
                }
                i = 1;
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                Any element = iter.readAny();
                cache.add(element);
                if (i++ == target) {
                    lastParsedPos = CodegenAccess.head(iter);
                    return element;
                }
            }
            lastParsedPos = tail;
        } catch (IOException e) {
            throw new JsonException(e);
        }
        throw new IndexOutOfBoundsException();
    }

    private class LazyIterator implements Iterator<Any> {

        private final int cacheSize;
        private int cachePos;

        public LazyIterator() {
            try {
                if (cache == null) {
                    cache = new ArrayList<Any>(4);
                }
                if (lastParsedPos == head) {
                    JsonIterator iter = JsonIterator.tlsIter.get();
                    iter.reset(data, lastParsedPos, tail);
                    if (!CodegenAccess.readArrayStart(iter)) {
                        lastParsedPos = tail;
                    } else {
                        lastParsedPos = CodegenAccess.head(iter);
                    }
                }
            } catch (IOException e) {
                throw new JsonException(e);
            }
            cacheSize = cache.size();
            cachePos = 0;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return cachePos != cacheSize || lastParsedPos != tail;
        }

        @Override
        public Any next() {
            try {
                return next_();
            } catch (IOException e) {
                throw new JsonException(e);
            }
        }

        private Any next_() throws IOException {
            if (cachePos != cacheSize) {
                return cache.get(cachePos++);
            }
            JsonIterator iter = JsonIterator.tlsIter.get();
            iter.reset(data, lastParsedPos, tail);
            Any element = iter.readAny();
            cache.add(element);
            if (CodegenAccess.nextToken(iter) == ',') {
                lastParsedPos = CodegenAccess.head(iter);
            } else {
                lastParsedPos = tail;
            }
            return element;
        }
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        if (lastParsedPos == head) {
            super.writeTo(stream);
        } else {
            // there might be modification
            fillCache();
            stream.writeVal(typeLiteral, cache);
        }
    }
}
