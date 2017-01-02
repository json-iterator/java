package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ArrayAny extends Any {

    private final List<Any> val;

    public ArrayAny(List<Any> val) {
        this.val = val;
    }

    @Override
    public ValueType valueType() {
        return ValueType.ARRAY;
    }

    @Override
    public Object object() {
        return val;
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        stream.writeArrayStart();
        Iterator<Any> iter = val.iterator();
        if (!iter.hasNext()) {
            stream.writeArrayEnd();
            return;
        }
        iter.next().writeTo(stream);
        while(iter.hasNext()) {
            stream.writeMore();
            iter.next().writeTo(stream);
        }
        stream.writeArrayEnd();
    }

    @Override
    public int size() {
        return val.size();
    }

    @Override
    public Iterator<Any> iterator() {
        return val.iterator();
    }

    @Override
    public Any get(int index) {
        return val.get(index);
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        return val.get((Integer) keys[idx]).get(keys, idx+1);
    }

    @Override
    public Any require(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Any result = null;
        try {
            result = val.get((Integer) keys[idx]);
        } catch (IndexOutOfBoundsException e) {
            reportPathNotFound(keys, idx);
        }
        return result.require(keys, idx + 1);
    }

    @Override
    public String toString() {
        return JsonStream.serialize(this);
    }

    @Override
    public boolean toBoolean() {
        return !val.isEmpty();
    }
}
