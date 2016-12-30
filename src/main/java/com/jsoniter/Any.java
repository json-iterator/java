package com.jsoniter;

import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.*;

public class Any extends Slice implements Iterable<Any> {

    private final static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };
    private ValueType valueType;
    private List<Any> array;
    private Map<Object, Any> object;
    private boolean objectFullyParsed;

    public Any(ValueType valueType, byte[] data, int head, int tail) {
        super(data, head, tail);
        this.valueType = valueType;
    }

    public final ValueType valueType() {
        return valueType;
    }

    public final <T> T to(Class<T> clazz, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.to(clazz);
    }

    private <T> T to(Class<T> clazz) {
        try {
            return createIterator().read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T to(TypeLiteral<T> typeLiteral, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.to(typeLiteral);
    }

    private <T> T to(TypeLiteral<T> typeLiteral) {
        try {
            return createIterator().read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
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
            return fillObject(key);
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
            result = fillObject(keys[idx]);
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
            result = fillObject(keys[idx]);
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

    private Any fillObject(Object target) throws IOException {
        if (objectFullyParsed || (object != null && object.containsKey(target))) {
            return object.get(target);
        }
        JsonIterator iter = createIterator();
        if (object == null) {
            object = new HashMap<Object, Any>(4);
        }
        if (!CodegenAccess.readObjectStart(iter)) {
            objectFullyParsed = true;
            return null;
        }
        String field = CodegenAccess.readObjectFieldAsString(iter);
        int start = iter.head;
        ValueType elementType = iter.skip();
        int end = iter.head;
        if (!object.containsKey(field)) {
            Any value = new Any(elementType, data(), start, end);
            object.put(field, value);
            if (field.hashCode() == target.hashCode() && field.equals(target)) {
                return value;
            }
        }
        while (iter.nextToken() == ',') {
            field = CodegenAccess.readObjectFieldAsString(iter);
            start = iter.head;
            elementType = iter.skip();
            end = iter.head;
            if (!object.containsKey(field)) {
                Any value = new Any(elementType, data(), start, end);
                object.put(field, value);
                if (field.hashCode() == target.hashCode() && field.equals(target)) {
                    return value;
                }
            }
        }
        objectFullyParsed = true;
        object.put(target, null);
        return null;
    }

    private void fillObject() throws IOException {
        if (objectFullyParsed) {
            return;
        }
        JsonIterator iter = createIterator();
        if (object == null) {
            object = new HashMap<Object, Any>(4);
        }
        if (!CodegenAccess.readObjectStart(iter)) {
            objectFullyParsed = true;
            return;
        }
        String field = CodegenAccess.readObjectFieldAsString(iter);
        int start = iter.head;
        ValueType elementType = iter.skip();
        int end = iter.head;
        if (!object.containsKey(field)) {
            Any value = new Any(elementType, data(), start, end);
            object.put(field, value);
        }
        while (iter.nextToken() == ',') {
            field = CodegenAccess.readObjectFieldAsString(iter);
            start = iter.head;
            elementType = iter.skip();
            end = iter.head;
            if (!object.containsKey(field)) {
                Any value = new Any(elementType, data(), start, end);
                object.put(field, value);
            }
        }
        objectFullyParsed = true;
    }

    private void fillArray() throws IOException {
        if (array != null) {
            return;
        }
        JsonIterator iter = createIterator();
        array = new ArrayList<Any>(4);
        if (!CodegenAccess.readArrayStart(iter)) {
            return;
        }
        int start = iter.head;
        ValueType elementType = iter.skip();
        int end = iter.head;
        array.add(new Any(elementType, data(), start, end));
        while (iter.nextToken() == ',') {
            start = iter.head;
            elementType = iter.skip();
            end = iter.head;
            array.add(new Any(elementType, data(), start, end));
        }
    }

    @Override
    public final Iterator<Any> iterator() {
        if (ValueType.ARRAY != valueType()) {
            throw unexpectedValueType(ValueType.ARRAY);
        }
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Any> {

        private final int size;
        private int idx;

        public ArrayIterator() {
            size = size();
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
