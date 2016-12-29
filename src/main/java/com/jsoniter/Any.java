package com.jsoniter;

import com.jsoniter.gnu.trove.map.hash.TCustomHashMap;
import com.jsoniter.gnu.trove.strategy.HashingStrategy;

import java.io.IOException;
import java.util.*;

public class Any extends Slice {

    private final static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };
    private final static HashingStrategy<Object> SLICE_HASHING_STRATEGY = new HashingStrategy<Object>() {
        @Override
        public int computeHashCode(Object object) {
            int hash = 0;
            if (object instanceof String) {
                String str = (String) object;
                for (int i = 0; i < str.length(); i++) {
                    byte b = (byte) str.charAt(i);
                    hash = hash * 31 + b;
                }
            } else {
                Slice slice = (Slice) object;
                for (int i = slice.head(); i < slice.tail(); i++) {
                    byte b = slice.data()[i];
                    hash = hash * 31 + b;
                }
            }
            return hash;
        }

        @Override
        public boolean equals(Object o1, Object o2) {
            if (o1 instanceof String) {
                return o2.equals(o1);
            } else {
                return o1.equals(o2);
            }
        }
    };
    private ValueType valueType;
    private List<Any> array;
    // key can be slice or string
    // string only support ascii
    private TCustomHashMap<Object, Any> object;

    public Any(ValueType valueType, byte[] data, int head, int tail) {
        super(data, head, tail);
        this.valueType = valueType;
    }

    public final ValueType valueType() {
        return valueType;
    }

    public final int toInt(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toInt();
    }

    public final int toInt() {
        try {
            return toInt_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private int toInt_() throws IOException {
        if (ValueType.NUMBER == valueType) {
            return createIterator().readInt();
        }
        if (ValueType.STRING == valueType) {
            JsonIterator iter = createIterator();
            iter.nextToken();
            return iter.readInt();
        }
        if (ValueType.NULL == valueType) {
            return 0;
        }
        throw unexpectedValueType(ValueType.NUMBER);
    }

    public final long toLong(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toLong();
    }

    public final long toLong() {
        try {
            return toLong_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private long toLong_() throws IOException {
        if (ValueType.NUMBER == valueType) {
            return createIterator().readLong();
        }
        if (ValueType.STRING == valueType) {
            JsonIterator iter = createIterator();
            iter.nextToken();
            return iter.readLong();
        }
        if (ValueType.NULL == valueType) {
            return 0;
        }
        throw unexpectedValueType(ValueType.NUMBER);
    }

    public final float toFloat(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toFloat();
    }

    public final float toFloat() {
        try {
            return toFloat_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private float toFloat_() throws IOException {
        if (ValueType.NUMBER == valueType) {
            return createIterator().readFloat();
        }
        if (ValueType.STRING == valueType) {
            JsonIterator iter = createIterator();
            iter.nextToken();
            return iter.readFloat();
        }
        if (ValueType.NULL == valueType) {
            return 0;
        }
        throw unexpectedValueType(ValueType.NUMBER);
    }

    public final double toDouble(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return 0;
        }
        return found.toDouble();
    }

    public final double toDouble() {
        try {
            return toDouble_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private double toDouble_() throws IOException {
        if (ValueType.NUMBER == valueType) {
            return createIterator().readDouble();
        }
        if (ValueType.STRING == valueType) {
            JsonIterator iter = createIterator();
            iter.nextToken();
            return iter.readDouble();
        }
        if (ValueType.NULL == valueType) {
            return 0;
        }
        throw unexpectedValueType(ValueType.NUMBER);
    }

    public final String toString(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.toString();
    }

    @Override
    public final String toString() {
        try {
            return toString_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private String toString_() throws IOException {
        if (ValueType.STRING == valueType) {
            return createIterator().readString();
        }
        if (ValueType.NULL == valueType) {
            return null;
        }
        if (ValueType.NUMBER == valueType) {
            char[] chars = new char[tail() - head()];
            for (int i = head(), j = 0; i < tail(); i++, j++) {
                chars[j] = (char) data()[i];
            }
            return new String(chars);
        }
        return super.toString();
    }

    public int size() {
        try {
            if (ValueType.ARRAY == valueType) {
                fillArray();
                return array.size();
            }
            if (ValueType.OBJECT == valueType) {
                fillObject();
                return object.size();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
        throw unexpectedValueType(ValueType.OBJECT);
    }

    public Set<Object> keys() {
        try {
            if (ValueType.ARRAY == valueType) {
                fillArray();
                Set<Object> keys = new HashSet<Object>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    keys.add(i);
                }
                return keys;
            }
            if (ValueType.OBJECT == valueType) {
                fillObject();
                return object.keySet();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
        throw unexpectedValueType(ValueType.OBJECT);
    }

    public final Any getValue(int index) {
        try {
            fillArray();
            return array.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final Any getValue(Object key) {
        try {
            fillObject();
            return object.get(key);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final Any get(Object... keys) {
        try {
            return get_(keys, 0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private Any get_(Object[] keys, int idx) throws IOException {
        if (idx == keys.length) {
            return this;
        }
        Any result;
        if (ValueType.OBJECT == valueType) {
            fillObject();
            result = object.get(keys[idx]);
        } else if (ValueType.ARRAY == valueType) {
            fillArray();
            result = array.get((Integer) keys[idx]);
        } else {
            result = null;
        }
        Any found = result;
        if (found == null) {
            return null;
        }
        return found.get_(keys, idx + 1);

    }

    public final Any require(Object... keys) {
        try {
            return require_(keys, 0);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private Any require_(Object[] keys, int idx) throws IOException {
        if (idx == keys.length) {
            return this;
        }
        Any result = null;
        if (ValueType.OBJECT == valueType) {
            fillObject();
            result = object.get(keys[idx]);
        } else if (ValueType.ARRAY == valueType) {
            fillArray();
            result = array.get((Integer) keys[idx]);
        }
        if (result == null) {
            throw new JsonException(String.format("failed to get path %s, because %s not found in %s",
                    Arrays.toString(keys), keys[idx], object));
        }
        return result.get_(keys, idx + 1);
    }

    private JsonException unexpectedValueType(ValueType expectedType) {
        throw new JsonException("unexpected value type: " + valueType);
    }

    private JsonIterator createIterator() {
        JsonIterator iter = tlsIter.get();
        iter.reset(this);
        return iter;
    }

    private void fillObject() throws IOException {
        if (object != null) {
            return;
        }
        JsonIterator iter = createIterator();
        object = new TCustomHashMap<Object, Any>(SLICE_HASHING_STRATEGY, 8);
        if (!CodegenAccess.readObjectStart(iter)) {
            return;
        }
        Slice field = CodegenAccess.readObjectFieldAsSlice(iter).clone();
        int start = iter.head;
        ValueType elementType = iter.skip();
        int end = iter.head;
        object.put(field, new Any(elementType, data(), start, end));
        while (iter.nextToken() == ',') {
            field = CodegenAccess.readObjectFieldAsSlice(iter).clone();
            start = iter.head;
            elementType = iter.skip();
            end = iter.head;
            object.put(field, new Any(elementType, data(), start, end));
        }
    }

    private void fillArray() throws IOException {
        if (array != null) {
            return;
        }
        JsonIterator iter = createIterator();
        array = new ArrayList<Any>(8);
        while (iter.readArray()) {
            int start = iter.head;
            ValueType elementType = iter.skip();
            int end = iter.head;
            array.add(new Any(elementType, data(), start, end));
        }
    }
}
