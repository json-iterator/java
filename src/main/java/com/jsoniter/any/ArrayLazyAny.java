package com.jsoniter.any;

import com.jsoniter.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayLazyAny extends LazyAny {

    private List<LazyAny> cache;

    public ArrayLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
    }

    @Override
    public ValueType valueType() {
        return ValueType.ARRAY;
    }

    @Override
    public Object asObject() {
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
        fillCache();
        return new ArrayIterator(cache);
    }

    @Override
    public Any get(int index) {
        try {
            fillCache();
            return cache.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
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
        fillCache();
        return cache.get((Integer) keys[idx]).get(keys, idx+1);
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
        LazyAny result = null;
        try {
            fillCache();
            result = cache.get((Integer) keys[idx]);
        } catch (IndexOutOfBoundsException e) {
            reportPathNotFound(keys, idx);
        }
        return result.require(keys, idx + 1);
    }

    private void fillCache() {
        if (cache != null) {
            return;
        }
        try {
            JsonIterator iter = parse();
            cache = new ArrayList<LazyAny>(4);
            if (!CodegenAccess.readArrayStart(iter)) {
                return;
            }
            cache.add(iter.readAny());
            while (CodegenAccess.nextToken(iter) == ',') {
                cache.add(iter.readAny());
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private static class ArrayIterator implements Iterator<Any> {

        private final int size;
        private final List<LazyAny> array;
        private int idx;

        public ArrayIterator(List<LazyAny> array) {
            size = array.size();
            this.array = array;
            idx = 0;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return idx < size;
        }

        @Override
        public Any next() {
            return array.get(idx++);
        }
    }
}
