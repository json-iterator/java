package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ArrayAny extends Any {

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
        while (iter.hasNext()) {
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
        try {
            return val.get(index);
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
            ArrayList<Any> result = new ArrayList<Any>();
            for (Any element : val) {
                Any mapped = element.get(keys, idx + 1);
                if (mapped.valueType() != ValueType.INVALID) {
                    result.add(mapped);
                }
            }
            return Any.rewrap(result);
        }
        try {
            return val.get((Integer) key).get(keys, idx + 1);
        } catch (IndexOutOfBoundsException e) {
            return new NotFoundAny(keys, idx, object());
        } catch (ClassCastException e) {
            return new NotFoundAny(keys, idx, object());
        }
    }

    @Override
    public String toString() {
        return JsonStream.serialize(this);
    }

    @Override
    public boolean toBoolean() {
        return !val.isEmpty();
    }

    @Override
    public int toInt() {
        return val.size();
    }

    @Override
    public long toLong() {
        return val.size();
    }

    @Override
    public float toFloat() {
        return val.size();
    }

    @Override
    public double toDouble() {
        return val.size();
    }

    @Override
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(val.size());
    }

    @Override
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(val.size());
    }
}
