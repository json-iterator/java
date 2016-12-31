package com.jsoniter;

import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.*;

public class Any extends Slice implements Iterable<Any> {

    private final static Set<String> EMPTY_KEYS = Collections.unmodifiableSet(new HashSet<String>());
    private final static Iterator<Any> EMPTY_ITERATOR = new Iterator<Any>() {
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Any next() {
            throw new UnsupportedOperationException();
        }
    };
    private final static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };
    private ValueType valueType;
    private Object objVal;
    private int intVal;

    public Any(ValueType valueType, byte[] data, int head, int tail) {
        super(data, head, tail);
        this.valueType = valueType;
        intVal = head;
    }

    public final ValueType valueType() {
        return valueType;
    }

    public final <T> T bindTo(T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(obj);
    }

    public final <T> T bindTo(T obj) {
        try {
            return createIterator().read(obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.bindTo(typeLiteral, obj);
    }

    public final <T> T bindTo(TypeLiteral<T> typeLiteral, T obj) {
        try {
            return createIterator().read(typeLiteral, obj);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final Object asObject(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.asObject();
    }

    public final Object asObject() {
        try {
            return createIterator().read();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T as(Class<T> clazz, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(clazz);
    }

    private <T> T as(Class<T> clazz) {
        try {
            return createIterator().read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T as(TypeLiteral<T> typeLiteral, Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return null;
        }
        return found.as(typeLiteral);
    }

    private <T> T as(TypeLiteral<T> typeLiteral) {
        try {
            return createIterator().read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final boolean toBoolean(Object... keys) {
        Any found = get(keys);
        if (found == null) {
            return false;
        }
        return found.toBoolean();
    }

    public final boolean toBoolean() {
        try {
            return toBoolean_();
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private boolean toBoolean_() throws IOException {
        if (ValueType.BOOLEAN == valueType) {
            return createIterator().readBoolean();
        }
        switch (valueType) {
            case STRING:
                return !createIterator().readString().trim().isEmpty();
            case NULL:
                return false;
            case ARRAY:
                return fillArray().isEmpty() ? false : true;
            case OBJECT:
                return fillObject().isEmpty() ? false : true;
            case NUMBER:
                return createIterator().readDouble() != 0;
            default:
                return false;
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
        switch (valueType) {
            case STRING:
                JsonIterator iter = createIterator();
                iter.nextToken();
                return iter.readInt();
            case NULL:
                return 0;
            case ARRAY:
                return fillArray().isEmpty() ? 0 : 1;
            case OBJECT:
                return fillObject().isEmpty() ? 0 : 1;
            case BOOLEAN:
                return createIterator().readBoolean() ? 1 : 0;
            default:
                return 0;
        }
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
        switch (valueType) {
            case STRING:
                JsonIterator iter = createIterator();
                iter.nextToken();
                return iter.readLong();
            case NULL:
                return 0;
            case OBJECT:
                return fillObject().isEmpty() ? 0 : 1;
            case ARRAY:
                return fillArray().isEmpty() ? 0 : 1;
            case BOOLEAN:
                return createIterator().readBoolean() ? 1 : 0;
            default:
                return 0;
        }
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
        switch (valueType) {
            case STRING:
                JsonIterator iter = createIterator();
                iter.nextToken();
                return iter.readFloat();
            case NULL:
                return 0;
            case OBJECT:
                return fillObject().isEmpty() ? 0 : 1;
            case ARRAY:
                return fillArray().isEmpty() ? 0 : 1;
            case BOOLEAN:
                return createIterator().readBoolean() ? 1 : 0;
            default:
                return 0;
        }
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
        switch (valueType) {
            case STRING:
                JsonIterator iter = createIterator();
                iter.nextToken();
                return iter.readDouble();
            case NULL:
                return 0;
            case OBJECT:
                return fillObject().isEmpty() ? 0 : 1;
            case ARRAY:
                return fillArray().isEmpty() ? 0 : 1;
            case BOOLEAN:
                return createIterator().readBoolean() ? 1 : 0;
            default:
                return 0;
        }
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
        if (ValueType.NUMBER == valueType || ValueType.NULL == valueType || ValueType.BOOLEAN == valueType) {
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
                return fillArray().size();
            }
            if (ValueType.OBJECT == valueType) {
                return fillObject().size();
            }
            return 0;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public Set<String> keys() {
        try {
            if (ValueType.OBJECT == valueType) {
                return fillObject().keySet();
            }
            return EMPTY_KEYS;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public final Iterator<Any> iterator() {
        if (ValueType.ARRAY != valueType()) {
            try {
                return new ArrayIterator(fillArray());
            } catch (IOException e) {
                throw new JsonException(e);
            }
        }
        return EMPTY_ITERATOR;
    }

    public final Any get(int index) {
        try {
            if (ValueType.ARRAY == valueType) {
                return fillArray().get(index);
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final Any get(Object key) {
        try {
            if (ValueType.OBJECT == valueType) {
                return fillObject(key);
            }
            return null;
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
            result = fillArray().get((Integer) keys[idx]);
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
            try {
                result = fillArray().get((Integer) keys[idx]);
            } catch (IndexOutOfBoundsException e) {
                reportPathNotFound(keys, idx);
            }
        }
        if (result == null) {
            return reportPathNotFound(keys, idx);
        }
        return result.get_(keys, idx + 1);
    }

    private Any reportPathNotFound(Object[] keys, int idx) {
        throw new JsonException(String.format("failed to get path %s, because #%s %s not found in %s",
                Arrays.toString(keys), idx, keys[idx], objVal));
    }

    // TODO: add value caching
    private JsonIterator createIterator() {
        JsonIterator iter = tlsIter.get();
        iter.reset(this);
        return iter;
    }

    private Any fillObject(Object target) throws IOException {
        Map<Object, Any> object = (Map<Object, Any>) objVal;
        if (intVal == tail() || (object != null && object.containsKey(target))) {
            return object.get(target);
        }
        JsonIterator iter = tlsIter.get();
        iter.reset(data(), intVal, tail());
        if (object == null) {
            objVal = object = new HashMap<Object, Any>(4);
        }
        if (intVal == head()) {
            if (!CodegenAccess.readObjectStart(iter)) {
                intVal = tail();
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
                    intVal = iter.head;
                    return value;
                }
            }
        }
        while (iter.nextToken() == ',') {
            String field = CodegenAccess.readObjectFieldAsString(iter);
            int start = iter.head;
            ValueType elementType = iter.skip();
            int end = iter.head;
            if (!object.containsKey(field)) {
                Any value = new Any(elementType, data(), start, end);
                object.put(field, value);
                if (field.hashCode() == target.hashCode() && field.equals(target)) {
                    intVal = iter.head;
                    return value;
                }
            }
        }
        intVal = tail();
        object.put(target, null);
        return null;
    }

    private Map<String, Any> fillObject() throws IOException {
        Map<String, Any> object = (Map<String, Any>) objVal;
        if (intVal == tail()) {
            return object;
        }
        JsonIterator iter = tlsIter.get();
        iter.reset(data(), intVal, tail());
        if (object == null) {
            objVal = object = new HashMap<String, Any>(4);
        }
        if (!CodegenAccess.readObjectStart(iter)) {
            intVal = tail();
            return object;
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
        intVal = tail();
        return object;
    }

    private List<Any> fillArray() throws IOException {
        List<Any> array = (List<Any>) objVal;
        if (array != null) {
            return array;
        }
        JsonIterator iter = createIterator();
        objVal = array = new ArrayList<Any>(4);
        if (!CodegenAccess.readArrayStart(iter)) {
            return array;
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
        return array;
    }

    private class ArrayIterator implements Iterator<Any> {

        private final int size;
        private final List<Any> array;
        private int idx;

        public ArrayIterator(List<Any> array) {
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
