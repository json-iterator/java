package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonIterator implements Closeable {

    // We have our own constants and a dictionary of size 256 bytes,
    // while ValueType[256] is 1024 bytes on 32-bit system.
    // If no one will call `whatIsNext`, ValueType class won't ever be loaded.
    private static final byte T_INVALID = -1;
    private static final byte T_STRING = 1;
    private static final byte T_NUMBER = 2;
    private static final byte T_NULL = 0;
    private static final byte T_BOOLEAN = 3;
    private static final byte T_ARRAY = 4;
    private static final byte T_OBJECT = 5;
    private static final byte[] types;
    static {
        byte[] _types = new byte[256];
        for (int i = 0; i < _types.length; i++) {
            _types[i] = T_INVALID;
        }
        _types['"'] = T_STRING;
        _types['-'] = T_NUMBER;
        _types['0'] = T_NUMBER;
        _types['1'] = T_NUMBER;
        _types['2'] = T_NUMBER;
        _types['3'] = T_NUMBER;
        _types['4'] = T_NUMBER;
        _types['5'] = T_NUMBER;
        _types['6'] = T_NUMBER;
        _types['7'] = T_NUMBER;
        _types['8'] = T_NUMBER;
        _types['9'] = T_NUMBER;
        _types['t'] = T_BOOLEAN;
        _types['f'] = T_BOOLEAN;
        _types['n'] = T_NULL;
        _types['['] = T_ARRAY;
        _types['{'] = T_OBJECT;
        types = _types;
    }

    public Config configCache;
    private static boolean isStreamingEnabled = false;
    InputStream in;
    byte[] buf;
    int head;
    int tail;
    int skipStartedAt = -1; // skip should keep bytes starting at this pos

    Map<String, Object> tempObjects = null; // used in reflection object decoder
    final Slice reusableSlice = new Slice(null, 0, 0);
    char[] reusableChars = new char[32];
    Object existingObject = null; // the object should be bind to next

    private JsonIterator(InputStream in, byte[] buf, int head, int tail) {
        this.in = in;
        this.buf = buf;
        this.head = head;
        this.tail = tail;
    }

    public JsonIterator() {
        this(null, new byte[0], 0, 0);
    }

    public static JsonIterator parse(InputStream in, int bufSize) {
        enableStreamingSupport();
        return new JsonIterator(in, new byte[bufSize], 0, 0);
    }

    public static JsonIterator parse(byte[] buf) {
        return new JsonIterator(null, buf, 0, buf.length);
    }

    public static JsonIterator parse(byte[] buf, int head, int tail) {
        return new JsonIterator(null, buf, head, tail);
    }

    public static JsonIterator parse(String str) {
        return parse(str.getBytes());
    }

    public static JsonIterator parse(Slice slice) {
        return new JsonIterator(null, slice.data(), slice.head(), slice.tail());
    }

    public final void reset(byte[] buf) {
        this.buf = buf;
        this.head = 0;
        this.tail = buf.length;
    }

    public final void reset(byte[] buf, int head, int tail) {
        this.buf = buf;
        this.head = head;
        this.tail = tail;
    }

    public final void reset(Slice value) {
        this.buf = value.data();
        this.head = value.head();
        this.tail = value.tail();
    }

    public final void reset(InputStream in) {
        JsonIterator.enableStreamingSupport();
        this.in = in;
        this.head = 0;
        this.tail = 0;
    }

    public final void close() throws IOException {
        if (in != null) {
            in.close();
        }
    }

    final void unreadByte() {
        if (head == 0) {
            throw reportError("unreadByte", "unread too many bytes");
        }
        head--;
    }

    public final JsonException reportError(String op, String msg) {
        int peekStart = head - 10;
        if (peekStart < 0) {
            peekStart = 0;
        }
        int peekSize = head - peekStart;
        if (head > tail) {
            peekSize = tail - peekStart;
        }
        String peek = new String(buf, peekStart, peekSize);
        throw new JsonException(op + ": " + msg + ", head: " + head + ", peek: " + peek + ", buf: " + new String(buf));
    }

    public final String currentBuffer() {
        int peekStart = head - 10;
        if (peekStart < 0) {
            peekStart = 0;
        }
        String peek = new String(buf, peekStart, head - peekStart);
        return "head: " + head + ", peek: " + peek + ", buf: " + new String(buf);
    }

    public final boolean readNull() throws IOException {
        byte c = IterImpl.nextToken(this);
        if (c != 'n') {
            unreadByte();
            return false;
        }
        IterImpl.skipFixedBytes(this, 3); // null
        return true;
    }

    public final boolean readBoolean() throws IOException {
        byte c = IterImpl.nextToken(this);
        if ('t' == c) {
            IterImpl.skipFixedBytes(this, 3); // true
            return true;
        }
        if ('f' == c) {
            IterImpl.skipFixedBytes(this, 4); // false
            return false;
        }
        throw reportError("readBoolean", "expect t or f, found: " + c);
    }

    public final short readShort() throws IOException {
        int v = readInt();
        if (Short.MIN_VALUE <= v && v <= Short.MAX_VALUE) {
            return (short) v;
        } else {
            throw reportError("readShort", "short overflow: " + v);
        }
    }

    public final int readInt() throws IOException {
        return IterImplNumber.readInt(this);
    }

    public final long readLong() throws IOException {
        return IterImplNumber.readLong(this);
    }

    public final boolean readArray() throws IOException {
        return IterImplArray.readArray(this);
    }

    public String readNumberAsString() throws IOException {
        return IterImplForStreaming.readNumber(this);
    }

    public static interface ReadArrayCallback {
        boolean handle(JsonIterator iter, Object attachment) throws IOException;
    }

    public final boolean readArrayCB(ReadArrayCallback callback, Object attachment) throws IOException {
        return IterImplArray.readArrayCB(this, callback, attachment);
    }

    public final String readString() throws IOException {
        return IterImplString.readString(this);
    }

    public final Slice readStringAsSlice() throws IOException {
        return IterImpl.readSlice(this);
    }

    public final String readObject() throws IOException {
        return IterImplObject.readObject(this);
    }

    public static interface ReadObjectCallback {
        boolean handle(JsonIterator iter, String field, Object attachment) throws IOException;
    }

    public final void readObjectCB(ReadObjectCallback cb, Object attachment) throws IOException {
        IterImplObject.readObjectCB(this, cb, attachment);
    }

    public final float readFloat() throws IOException {
        return IterImplNumber.readFloat(this);
    }

    public final double readDouble() throws IOException {
        return IterImplNumber.readDouble(this);
    }

    public final BigDecimal readBigDecimal() throws IOException {
        // skip whitespace by read next
        byte type = whatsNext();
        if (type == T_NULL) {
            skip();
            return null;
        }
        if (type != T_NUMBER) {
            throw reportError("readBigDecimal", "not number");
        }
        return new BigDecimal(IterImplForStreaming.readNumber(this));
    }

    public final BigInteger readBigInteger() throws IOException {
        // skip whitespace by read next
        byte type = whatsNext();
        if (type == T_NULL) {
            skip();
            return null;
        }
        if (type != T_NUMBER) {
            throw reportError("readBigDecimal", "not number");
        }
        return new BigInteger(IterImplForStreaming.readNumber(this));
    }

    public final Any readAny() throws IOException {
        try {
            return IterImpl.readAny(this);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw reportError("read", "premature end");
        }
    }

    private static final class ReadArrayObjectCallback implements ReadArrayCallback, ReadObjectCallback {

        static final ReadArrayObjectCallback INSTANCE = new ReadArrayObjectCallback();

        private ReadArrayObjectCallback() {}

        @Override
        public boolean handle(JsonIterator iter, Object attachment) throws IOException {
            List list = (List) attachment;
            list.add(iter.read());
            return true;
        }
        @Override
        public boolean handle(JsonIterator iter, String field, Object attachment) throws IOException {
            Map map = (Map) attachment;
            map.put(field, iter.read());
            return true;
        }
    }

    public final Object read() throws IOException {
        try {
            byte type = whatsNext();
            switch (type) {
                case T_STRING:
                    return readString();
                case T_NUMBER:
                    double number = readDouble();
                    if (number == Math.floor(number) && !Double.isInfinite(number)) {
                        long longNumber = (long) number;
                        if (longNumber <= Integer.MAX_VALUE && longNumber >= Integer.MIN_VALUE) {
                            return (int) longNumber;
                        }
                        return longNumber;
                    }
                    return number;
                case T_NULL:
                    IterImpl.skipFixedBytes(this, 4);
                    return null;
                case T_BOOLEAN:
                    return readBoolean();
                case T_ARRAY:
                    ArrayList list = new ArrayList(4);
                    readArrayCB(ReadArrayObjectCallback.INSTANCE, list);
                    return list;
                case T_OBJECT:
                    Map map = new HashMap(4);
                    readObjectCB(ReadArrayObjectCallback.INSTANCE, map);
                    return map;
                default:
                    throw reportError("read", "unexpected value type: " + type);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw reportError("read", "premature end");
        }
    }

    /**
     * try to bind to existing object, returned object might not the same instance
     *
     * @param existingObject the object instance to reuse
     * @param <T>            object type
     * @return data binding result, might not be the same object
     * @throws IOException if I/O went wrong
     */
    public final <T> T read(T existingObject) throws IOException {
        try {
            this.existingObject = existingObject;
            Class<?> clazz = existingObject.getClass();
            String cacheKey = currentConfig().getDecoderCacheKey(clazz);
            return (T) Codegen.getDecoder(cacheKey, clazz).decode(this);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw reportError("read", "premature end");
        }
    }

    private Config currentConfig() {
        if (configCache == null) {
            configCache = JsoniterSpi.getCurrentConfig();
        }
        return configCache;
    }

    /**
     * try to bind to existing object, returned object might not the same instance
     *
     * @param typeLiteral    the type object
     * @param existingObject the object instance to reuse
     * @param <T>            object type
     * @return data binding result, might not be the same object
     * @throws IOException if I/O went wrong
     */
    public final <T> T read(TypeLiteral<T> typeLiteral, T existingObject) throws IOException {
        try {
            this.existingObject = existingObject;
            String cacheKey = currentConfig().getDecoderCacheKey(typeLiteral.getType());
            return (T) Codegen.getDecoder(cacheKey, typeLiteral.getType()).decode(this);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw reportError("read", "premature end");
        }
    }

    public final <T> T read(Class<T> clazz) throws IOException {
        return (T) read((Type) clazz);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral) throws IOException {
        return (T) read(typeLiteral.getType());
    }

    public final Object read(Type type) throws IOException {
        try {
            String cacheKey = currentConfig().getDecoderCacheKey(type);
            return Codegen.getDecoder(cacheKey, type).decode(this);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw reportError("read", "premature end");
        }
    }

    public ValueType whatIsNext() throws IOException {
        ValueType valueType = ValueType.ofCharacter(IterImpl.nextToken(this));
        unreadByte();
        return valueType;
    }

    private byte whatsNext() throws IOException {
        byte type = types[IterImpl.nextToken(this)];
        unreadByte();
        return type;
    }

    public void skip() throws IOException {
        IterImplSkip.skip(this);
    }

    public static final <T> T deserialize(Config config, String input, Class<T> clazz) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input.getBytes(), clazz);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final <T> T deserialize(String input, Class<T> clazz) {
        return deserialize(input.getBytes(), clazz);
    }

    public static final <T> T deserialize(Config config, String input, TypeLiteral<T> typeLiteral) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input.getBytes(), typeLiteral);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final <T> T deserialize(String input, TypeLiteral<T> typeLiteral) {
        return deserialize(input.getBytes(), typeLiteral);
    }

    public static final <T> T deserialize(Config config, byte[] input, Class<T> clazz) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input, clazz);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final <T> T deserialize(byte[] input, Class<T> clazz) {
        int lastNotSpacePos = findLastNotSpacePos(input);
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        iter.reset(input, 0, lastNotSpacePos);
        try {
            T val = iter.read(clazz);
            if (iter.head != lastNotSpacePos) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw iter.reportError("deserialize", "premature end");
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }

    public static final <T> T deserialize(Config config, byte[] input, TypeLiteral<T> typeLiteral) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input, typeLiteral);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final <T> T deserialize(byte[] input, TypeLiteral<T> typeLiteral) {
        int lastNotSpacePos = findLastNotSpacePos(input);
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        iter.reset(input, 0, lastNotSpacePos);
        try {
            T val = iter.read(typeLiteral);
            if (iter.head != lastNotSpacePos) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw iter.reportError("deserialize", "premature end");
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }

    public static final Any deserialize(Config config, String input) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input.getBytes());
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final Any deserialize(String input) {
        return deserialize(input.getBytes());
    }

    public static final Any deserialize(Config config, byte[] input) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return deserialize(input);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static final Any deserialize(byte[] input) {
        int lastNotSpacePos = findLastNotSpacePos(input);
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        iter.reset(input, 0, lastNotSpacePos);
        try {
            Any val = iter.readAny();
            if (iter.head != lastNotSpacePos) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw iter.reportError("deserialize", "premature end");
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }

    private static int findLastNotSpacePos(byte[] input) {
        for (int i = input.length - 1; i >= 0; i--) {
            byte c = input[i];
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                return i + 1;
            }
        }
        return 0;
    }

    public static void setMode(DecodingMode mode) {
        Config newConfig = JsoniterSpi.getDefaultConfig().copyBuilder().decodingMode(mode).build();
        JsoniterSpi.setDefaultConfig(newConfig);
        JsoniterSpi.setCurrentConfig(newConfig);
    }

    public static void enableStreamingSupport() {
        if (isStreamingEnabled) {
            return;
        }
        isStreamingEnabled = true;
        try {
            DynamicCodegen.enableStreamingSupport();
        }  catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }
}
