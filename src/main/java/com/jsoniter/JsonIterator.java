package com.jsoniter;

import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.any.Any;
import com.jsoniter.spi.TypeLiteral;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonIterator implements Closeable {

    private static boolean isStreamingEnabled = false;
    final static ValueType[] valueTypes = new ValueType[256];
    InputStream in;
    byte[] buf;
    int head;
    int tail;

    Map<String, Object> tempObjects = new HashMap<String, Object>();
    final Slice reusableSlice = new Slice(null, 0, 0);
    char[] reusableChars = new char[32];
    Object existingObject = null; // the set should be bind to next

    static {
        for (int i = 0; i < valueTypes.length; i++) {
            valueTypes[i] = ValueType.INVALID;
        }
        valueTypes['"'] = ValueType.STRING;
        valueTypes['-'] = ValueType.NUMBER;
        valueTypes['0'] = ValueType.NUMBER;
        valueTypes['1'] = ValueType.NUMBER;
        valueTypes['2'] = ValueType.NUMBER;
        valueTypes['3'] = ValueType.NUMBER;
        valueTypes['4'] = ValueType.NUMBER;
        valueTypes['5'] = ValueType.NUMBER;
        valueTypes['6'] = ValueType.NUMBER;
        valueTypes['7'] = ValueType.NUMBER;
        valueTypes['8'] = ValueType.NUMBER;
        valueTypes['9'] = ValueType.NUMBER;
        valueTypes['t'] = ValueType.BOOLEAN;
        valueTypes['f'] = ValueType.BOOLEAN;
        valueTypes['n'] = ValueType.NULL;
        valueTypes['['] = ValueType.ARRAY;
        valueTypes['{'] = ValueType.OBJECT;
    }

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
        enableStreamingSupport();
        this.in = in;
        this.head = 0;
        this.tail = 0;
    }

    public final void close() throws IOException {
        if (in != null) {
            in.close();
        }
    }

    final void unreadByte() throws IOException {
        if (head == 0) {
            throw new IOException("unread too many bytes");
        }
        head--;
    }

    public final JsonException reportError(String op, String msg) {
        int peekStart = head - 10;
        if (peekStart < 0) {
            peekStart = 0;
        }
        String peek = new String(buf, peekStart, head - peekStart);
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
        if (c == 'n') {
            IterImpl.skipUntilBreak(this);
            return true;
        }
        unreadByte();
        return false;
    }

    public final boolean readBoolean() throws IOException {
        byte c = IterImpl.nextToken(this);
        switch (c) {
            case 't':
                IterImpl.skipUntilBreak(this);
                return true;
            case 'f':
                IterImpl.skipUntilBreak(this);
                return false;
            default:
                throw reportError("readBoolean", "expect t or f, found: " + c);
        }
    }

    public final short readShort() throws IOException {
        int v = readInt();
        if (Short.MIN_VALUE <= v && v <= Short.MAX_VALUE) {
            return (short) v;
        } else {
            throw new JsonException("short overflow: " + v);
        }
    }

    public final int readInt() throws IOException {
        return IterImplNumber.readInt(this);
    }

    public final long readLong() throws IOException {
        return IterImplNumber.readLong(this);
    }

    public final boolean readArray() throws IOException {
        byte c = IterImpl.nextToken(this);
        switch (c) {
            case '[':
                c = IterImpl.nextToken(this);
                if (c == ']') {
                    return false;
                } else {
                    unreadByte();
                    return true;
                }
            case ']':
                return false;
            case ',':
                return true;
            case 'n':
                return false;
            default:
                throw reportError("readArray", "expect [ or , or n or ], but found: " + (char) c);
        }
    }

    public final String readString() throws IOException {
        return IterImplString.readString(this);
    }

    public final byte[] readBase64() throws IOException {
        return IterImplString.readBase64(this);
    }

    public final String readObject() throws IOException {
        byte c = IterImpl.nextToken(this);
        switch (c) {
            case 'n':
                IterImpl.skipUntilBreak(this);
                return null;
            case '{':
                c = IterImpl.nextToken(this);
                switch (c) {
                    case '}':
                        return null; // end of set
                    case '"':
                        unreadByte();
                        String field = readString();
                        if (IterImpl.nextToken(this) != ':') {
                            throw reportError("readObject", "expect :");
                        }
                        return field;
                    default:
                        throw reportError("readObject", "expect \" after {");
                }
            case ',':
                String field = readString();
                if (IterImpl.nextToken(this) != ':') {
                    throw reportError("readObject", "expect :");
                }
                return field;
            case '}':
                return null; // end of set
            default:
                throw reportError("readObject", "expect { or , or } or n");
        }
    }

    public final float readFloat() throws IOException {
        return IterImplNumber.readFloat(this);
    }

    public final double readDouble() throws IOException {
        return IterImplNumber.readDouble(this);
    }

    public final BigDecimal readBigDecimal() throws IOException {
        return new BigDecimal(IterImplNumber.readNumber(this));
    }

    public final BigInteger readBigInteger() throws IOException {
        return new BigInteger(IterImplNumber.readNumber(this));
    }

    public final Any readAny() throws IOException {
        if (in != null) {
            throw new JsonException("input can not be InputStream when readAny");
        }
        return IterImplSkip.readAny(this);
    }

    public final Object read() throws IOException {
        ValueType valueType = whatIsNext();
        switch (valueType) {
            case STRING:
                return readString();
            case NUMBER:
                return readDouble();
            case NULL:
                IterImpl.skipUntilBreak(this);
                return null;
            case BOOLEAN:
                return readBoolean();
            case ARRAY:
                ArrayList list = new ArrayList();
                while (readArray()) {
                    list.add(read());
                }
                return list;
            case OBJECT:
                Map map = new HashMap();
                for (String field = readObject(); field != null; field = readObject()) {
                    map.put(field, read());
                }
                return map;
            default:
                throw reportError("read", "unexpected value type: " + valueType);
        }
    }

    public final <T> T read(T existingObject) throws IOException {
        this.existingObject = existingObject;
        Class<?> clazz = existingObject.getClass();
        return (T) Codegen.getDecoder(TypeLiteral.create(clazz).getDecoderCacheKey(), clazz).decode(this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral, T existingObject) throws IOException {
        this.existingObject = existingObject;
        return (T) Codegen.getDecoder(typeLiteral.getDecoderCacheKey(), typeLiteral.getType()).decode(this);
    }

    public final <T> T read(Class<T> clazz) throws IOException {
        return (T) Codegen.getDecoder(TypeLiteral.create(clazz).getDecoderCacheKey(), clazz).decode(this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral) throws IOException {
        String cacheKey = typeLiteral.getDecoderCacheKey();
        return (T) Codegen.getDecoder(cacheKey, typeLiteral.getType()).decode(this);
    }

    public ValueType whatIsNext() throws IOException {
        ValueType valueType = valueTypes[IterImpl.nextToken(this)];
        unreadByte();
        return valueType;
    }

    public void skip() throws IOException {
        IterImplSkip.skip(this);
    }

    private static ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };

    public static final <T> T deserialize(String input, Class<T> clazz) {
        JsonIterator iter = tlsIter.get();
        iter.reset(input.getBytes());
        try {
            T val = iter.read(clazz);
            if (IterImpl.nextToken(iter) != 0) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static final <T> T deserialize(String input, TypeLiteral<T> typeLiteral) {
        JsonIterator iter = tlsIter.get();
        iter.reset(input.getBytes());
        try {
            T val = iter.read(typeLiteral);
            if (IterImpl.nextToken(iter) != 0) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static final <T> T deserialize(byte[] input, Class<T> clazz) {
        JsonIterator iter = tlsIter.get();
        iter.reset(input);
        try {
            T val = iter.read(clazz);
            if (IterImpl.nextToken(iter) != 0) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static final <T> T deserialize(byte[] input, TypeLiteral<T> typeLiteral) {
        JsonIterator iter = tlsIter.get();
        iter.reset(input);
        try {
            T val = iter.read(typeLiteral);
            if (IterImpl.nextToken(iter) != 0) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static final Any deserialize(String input) {
        return deserialize(input.getBytes());
    }

    public static final Any deserialize(byte[] input) {
        JsonIterator iter = tlsIter.get();
        iter.reset(input);
        try {
            Any val = iter.readAny();
            if (IterImpl.nextToken(iter) != 0) {
                throw iter.reportError("deserialize", "trailing garbage found");
            }
            return val;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static void setMode(DecodingMode mode) {
        Codegen.setMode(mode);
    }

    public static void enableStreamingSupport() {
        if (isStreamingEnabled) {
            return;
        }
        isStreamingEnabled = true;
        try {
            DynamicCodegen.enableStreamingSupport();
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static void enableAnnotationSupport() {
        JsoniterAnnotationSupport.enable();
    }
}
