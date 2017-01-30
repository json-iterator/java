package com.jsoniter.any;

import com.jsoniter.spi.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.*;

public abstract class Any implements Iterable<Any> {

    static {
        Encoder anyEncoder = new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                Any any = (Any) obj;
                any.writeTo(stream);
            }

            @Override
            public Any wrap(Object obj) {
                return (Any) obj;
            }
        };
        JsonStream.registerNativeEncoder(Any.class, anyEncoder);
        JsonStream.registerNativeEncoder(TrueAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(FalseAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(ArrayLazyAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(DoubleAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(FloatAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(IntAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(LongAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(NullAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(LongLazyAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(DoubleLazyAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(ObjectLazyAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(StringAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(StringLazyAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(ArrayAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(ObjectAny.class, anyEncoder);
        JsonStream.registerNativeEncoder(ListWrapperAny.class, anyEncoder);
    }

    public interface EntryIterator {
        boolean next();

        String key();

        Any value();
    }

    protected final static Set<String> EMPTY_KEYS = Collections.unmodifiableSet(new HashSet<String>());
    protected final static EntryIterator EMPTY_ENTRIES_ITERATOR = new EntryIterator() {
        @Override
        public boolean next() {
            return false;
        }

        @Override
        public String key() {
            throw new NoSuchElementException();
        }

        @Override
        public Any value() {
            throw new NoSuchElementException();
        }
    };
    protected final static Iterator<Any> EMPTY_ITERATOR = new Iterator<Any>() {
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
            throw new NoSuchElementException();
        }
    };

    public abstract ValueType valueType();

    public <T> T bindTo(T obj, Object... keys) {
        return get(keys).bindTo(obj);
    }

    public <T> T bindTo(T obj) {
        return (T) object();
    }

    public <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys) {
        return get(keys).bindTo(typeLiteral, obj);
    }

    public <T> T bindTo(TypeLiteral<T> typeLiteral, T obj) {
        return (T) object();
    }

    public Object object(Object... keys) {
        return get(keys).object();
    }

    public abstract Object object();

    public Map<String, Any> asMap() {
        return (Map<String, Any>) object();
    }

    public List<Any> asList() {
        return (List<Any>) object();
    }

    public <T> T as(Class<T> clazz, Object... keys) {
        return get(keys).as(clazz);
    }

    public <T> T as(Class<T> clazz) {
        return (T) object();
    }

    public <T> T as(TypeLiteral<T> typeLiteral, Object... keys) {
        return get(keys).as(typeLiteral);
    }

    public <T> T as(TypeLiteral<T> typeLiteral) {
        return (T) object();
    }

    public final boolean toBoolean(Object... keys) {
        return get(keys).toBoolean();
    }

    public abstract boolean toBoolean();

    public final int toInt(Object... keys) {
        return get(keys).toInt();
    }

    public abstract int toInt();

    public final long toLong(Object... keys) {
        return get(keys).toLong();
    }

    public abstract long toLong();

    public final float toFloat(Object... keys) {
        return get(keys).toFloat();
    }

    public abstract float toFloat();

    public final double toDouble(Object... keys) {
        return get(keys).toDouble();
    }

    public abstract double toDouble();

    public final String toString(Object... keys) {
        return get(keys).toString();
    }

    public abstract String toString();

    public int size() {
        return 0;
    }

    public Set<String> keys() {
        return EMPTY_KEYS;
    }

    @Override
    public Iterator<Any> iterator() {
        return EMPTY_ITERATOR;
    }

    public EntryIterator entries() {
        return EMPTY_ENTRIES_ITERATOR;
    }

    public Any get(int index) {
        return new NotFoundAny(index, object());
    }

    public Any get(Object key) {
        return new NotFoundAny(key, object());
    }

    public final Any get(Object... keys) {
        return get(keys, 0);
    }

    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        return new NotFoundAny(keys, idx, object());
    }

    public Any set(int newVal) {
        return wrap(newVal);
    }

    public Any set(long newVal) {
        return wrap(newVal);
    }

    public Any set(float newVal) {
        return wrap(newVal);
    }

    public Any set(double newVal) {
        return wrap(newVal);
    }

    public Any set(String newVal) {
        return wrap(newVal);
    }

    public JsonIterator parse() {
        throw new UnsupportedOperationException();
    }

    public abstract void writeTo(JsonStream stream) throws IOException;

    protected JsonException reportUnexpectedType(ValueType toType) {
        throw new JsonException(String.format("can not convert %s to %s", valueType(), toType));
    }

    public static Any lazyString(byte[] data, int head, int tail) {
        return new StringLazyAny(data, head, tail);
    }

    public static Any lazyDouble(byte[] data, int head, int tail) {
        return new DoubleLazyAny(data, head, tail);
    }

    public static Any lazyLong(byte[] data, int head, int tail) {
        return new LongLazyAny(data, head, tail);
    }

    public static Any lazyArray(byte[] data, int head, int tail) {
        return new ArrayLazyAny(data, head, tail);
    }

    public static Any lazyObject(byte[] data, int head, int tail) {
        return new ObjectLazyAny(data, head, tail);
    }

    public static Any wrap(int val) {
        return new IntAny(val);
    }

    public static Any wrap(long val) {
        return new LongAny(val);
    }

    public static Any wrap(float val) {
        return new FloatAny(val);
    }

    public static Any wrap(double val) {
        return new DoubleAny(val);
    }

    public static Any wrap(boolean val) {
        if (val) {
            return TrueAny.INSTANCE;
        } else {
            return FalseAny.INSTANCE;
        }
    }

    public static Any wrap(String val) {
        if (val == null) {
            return NullAny.INSTANCE;
        }
        return new StringAny(val);
    }

    public static <T> Any wrap(Collection<T> val) {
        if (val == null) {
            return NullAny.INSTANCE;
        }
        return new ListWrapperAny(new ArrayList(val));
    }

    public static <T> Any wrap(Map<String, T> val) {
        if (val == null) {
            return NullAny.INSTANCE;
        }
        HashMap<String, Any> copied = new HashMap<String, Any>(val.size());
        for (Map.Entry<String, T> entry : val.entrySet()) {
            copied.put(entry.getKey(), wrap(entry.getValue()));
        }
        return new ObjectAny(copied);
    }

    public static Any wrap(Object val) {
        return JsonStream.wrap(val);
    }

    public static Any wrapNull() {
        return NullAny.INSTANCE;
    }

    public static Any wrapAnyList(List<Any> val) {
        return new ArrayAny(val);
    }

    public static Any wrapAnyMap(Map<String, Any> val) {
        return new ObjectAny(val);
    }

    private final static int wildcardHashCode = Character.valueOf('*').hashCode();
    private final static Character wildcard = '*';

    protected boolean isWildcard(Object key) {
        return wildcardHashCode == key.hashCode() && wildcard.equals(key);
    }
}
