package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ObjectAny extends Any {

    private final Map<String, Any> val;

    public ObjectAny(Map<String, Any> val) {
        this.val = val;
    }

    @Override
    public ValueType valueType() {
        return ValueType.OBJECT;
    }

    @Override
    public Object object() {
        return val;
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        stream.writeObjectStart();
        boolean notFirst = false;
        for (Map.Entry<String, Any> entry : val.entrySet()) {
            if (notFirst) {
                stream.writeMore();
            } else {
                notFirst = true;
            }
            stream.writeObjectField(entry.getKey());
            entry.getValue().writeTo(stream);
        }
        stream.writeObjectEnd();
    }

    @Override
    public boolean toBoolean() {
        return !val.isEmpty();
    }

    @Override
    public String toString() {
        return JsonStream.serialize(this);
    }

    @Override
    public Any get(Object key) {
        Any element = val.get(key);
        if (element == null) {
            return new NotFoundAny(key, object());
        }
        return element;
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Object key = keys[idx];
        if (isWildcard(key)) {
            HashMap<String, Any> result = new HashMap<String, Any>();
            for (Map.Entry<String, Any> entry : val.entrySet()) {
                Any mapped = entry.getValue().get(keys, idx + 1);
                if (mapped.valueType() != ValueType.INVALID) {
                    result.put(entry.getKey(), mapped);
                }
            }
            return Any.wrapAnyMap(result);
        }
        Any element = val.get(key);
        if (element == null) {
            return new NotFoundAny(keys, idx, object());
        }
        return element.get(keys, idx + 1);
    }

    @Override
    public EntryIterator entries() {
        return new IteratorAdapter(val.entrySet().iterator());
    }

    public static class IteratorAdapter implements EntryIterator {

        private final Iterator<Map.Entry<String, Any>> iter;
        private Map.Entry<String, Any> entry;

        public IteratorAdapter(Iterator<Map.Entry<String, Any>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean next() {
            if (iter.hasNext()) {
                entry = iter.next();
                return true;
            }
            return false;
        }

        @Override
        public String key() {
            return entry.getKey();
        }

        @Override
        public Any value() {
            return entry.getValue();
        }
    }
}
