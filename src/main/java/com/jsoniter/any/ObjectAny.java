package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
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
        return val.get(key);
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Any child = val.get(keys[idx]);
        if (child == null) {
            return null;
        }
        return child.get(keys, idx+1);
    }

    @Override
    public Any require(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Any result = val.get(keys[idx]);
        if (result == null) {
            throw reportPathNotFound(keys, idx);
        }
        return result.require(keys, idx + 1);
    }
}
