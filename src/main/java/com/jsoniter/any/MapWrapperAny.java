package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class MapWrapperAny extends Any {

    private final Map val;
    private Map<String, Any> cache;

    public MapWrapperAny(Map val) {
        this.val = val;
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
        return size() != 0;
    }

    @Override
    public int toInt() {
        return size();
    }

    @Override
    public long toLong() {
        return size();
    }

    @Override
    public float toFloat() {
        return size();
    }

    @Override
    public double toDouble() {
        return size();
    }

    @Override
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(size());
    }

    @Override
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(size());
    }

    @Override
    public String toString() {
        if (cache == null) {
            return JsonStream.serialize(val);
        } else {
            fillCache();
            return JsonStream.serialize(cache);
        }
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        if (cache == null) {
            stream.writeVal(val);
        } else {
            fillCache();
            stream.writeVal(cache);
        }
    }

    @Override
    public int size() {
        return val.size();
    }

    @Override
    public Any get(Object key) {
        return fillCacheUntil(key);
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Object key = keys[idx];
        if (isWildcard(key)) {
            fillCache();
            HashMap<String, Any> result = new HashMap<String, Any>();
            for (Map.Entry<String, Any> entry : cache.entrySet()) {
                Any mapped = entry.getValue().get(keys, idx + 1);
                if (mapped.valueType() != ValueType.INVALID) {
                    result.put(entry.getKey(), mapped);
                }
            }
            return Any.rewrap(result);
        }
        Any child = fillCacheUntil(key);
        if (child == null) {
            return new NotFoundAny(keys, idx, object());
        }
        return child.get(keys, idx + 1);
    }

    @Override
    public EntryIterator entries() {
        return new WrapperIterator();
    }

    private Any fillCacheUntil(Object target) {
        if (cache == null) {
            cache = new HashMap<String, Any>();
        }
        Any element = cache.get(target);
        if (element != null) {
            return element;
        }
        Set<Map.Entry<String, Object>> entries = val.entrySet();
        int targetHashcode = target.hashCode();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if (cache.containsKey(key)) {
                continue;
            }
            element = Any.wrap(entry.getValue());
            cache.put(key, element);
            if (targetHashcode == key.hashCode() && target.equals(key)) {
                return element;
            }
        }
        return new NotFoundAny(target, val);
    }

    private void fillCache() {
        if (cache == null) {
            cache = new HashMap<String, Any>();
        }
        Set<Map.Entry<String, Object>> entries = val.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if (cache.containsKey(key)) {
                continue;
            }
            Any element = Any.wrap(entry.getValue());
            cache.put(key, element);
        }
    }

    private class WrapperIterator implements EntryIterator {

        private final Iterator<Map.Entry<String, Object>> iter;
        private String key;
        private Any value;

        private WrapperIterator() {
            Set<Map.Entry<String, Object>> entries = val.entrySet();
            iter = entries.iterator();
        }

        @Override
        public boolean next() {
            if (cache == null) {
                cache = new HashMap<String, Any>();
            }
            if (!iter.hasNext()) {
                return false;
            }
            Map.Entry<String, Object> entry = iter.next();
            key = entry.getKey();
            value = cache.get(key);
            if (value == null) {
                value = Any.wrap(entry.getValue());
                cache.put(key, value);
            }
            return true;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Any value() {
            return value;
        }
    }
}
