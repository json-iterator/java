package com.jsoniter.any;

import com.jsoniter.*;
import com.jsoniter.spi.JsonException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ArrayLazyAny extends LazyAny {

    private List<Any> cache;
    private int lastParsedPos;

    public ArrayLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
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
            return new LazyIterator(new JsonIterator());
        }
    }

    @Override
    public Any get(int index) {
        try {
            return fillCache(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        return fillCache((Integer) keys[idx]).get(keys, idx + 1);
    }

    @Override
    public Any require(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Any result = null;
        try {
            result = fillCache((Integer) keys[idx]);
        } catch (IndexOutOfBoundsException e) {
            reportPathNotFound(keys, idx);
        }
        return result.require(keys, idx + 1);
    }

    private void fillCache() {
        if (lastParsedPos == tail) {
            return;
        }
        LazyIterator iter = new LazyIterator(JsonIterator.tlsIter.get());
        while (iter.hasNext()) {
            // cache will be filled in the process
            iter.next();
        }
    }

    private Any fillCache(int target) {
        if (lastParsedPos == tail) {
            return cache.get(target);
        }
        int i = 0;
        LazyIterator iter = new LazyIterator(JsonIterator.tlsIter.get());
        while (iter.hasNext()) {
            Any element = iter.next();
            if (i == target) {
                return element;
            }
            i++;
        }
        throw new IndexOutOfBoundsException();
    }

    private class LazyIterator implements Iterator<Any> {

        private JsonIterator jsonIter;
        private final int cacheSize;
        private int cachePos;

        public LazyIterator(JsonIterator jsonIter) {
            try {
                if (jsonIter != null) {
                    this.jsonIter = jsonIter;
                    this.jsonIter.reset(data, lastParsedPos, tail);
                }
                if (cache == null) {
                    cache = new ArrayList<Any>(4);
                }
                if (lastParsedPos == head) {
                    readHead(jsonIter);
                }
            } catch (IOException e) {
                throw new JsonException(e);
            }
            cacheSize = cache.size();
            cachePos = 0;
        }

        private void readHead(JsonIterator jsonIter) throws IOException {
            if (jsonIter == null) {
                jsonIter = JsonIterator.tlsIter.get();
                jsonIter.reset(data, lastParsedPos, tail);
            }
            if (!CodegenAccess.readArrayStart(jsonIter)) {
                lastParsedPos = tail;
            }
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
            JsonIterator iter = jsonIter;
            if (iter == null) {
                iter = JsonIterator.tlsIter.get();
                iter.reset(data, lastParsedPos, tail);
            }
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
}
