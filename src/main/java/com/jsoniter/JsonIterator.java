package com.jsoniter;

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

    final static ValueType[] valueTypes = new ValueType[256];
    InputStream in;
    byte[] buf;
    int head;
    int tail;
    boolean eof;

    Map<String, Object> tempObjects = new HashMap<String, Object>();
    final Slice reusableSlice = new Slice(null, 0, 0);
    char[] reusableChars = new char[32];
    Object existingObject = null; // the object should be bind to next

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

    public JsonIterator(InputStream in, byte[] buf) {
        this.in = in;
        this.buf = buf;
        if (this.in == null) {
            tail = buf.length;
        }
    }

    public JsonIterator() {
        this(null, new byte[0]);
    }

    public static JsonIterator parse(InputStream in, int bufSize) {
        return new JsonIterator(in, new byte[bufSize]);
    }

    public static JsonIterator parse(byte[] buf) {
        return new JsonIterator(null, buf);
    }

    public static JsonIterator parse(String str) {
        return parse(str.getBytes());
    }

    public final void reset(byte[] buf) {
        this.buf = buf;
        this.head = 0;
        this.tail = buf.length;
        this.eof = false;
    }

    public final void reset(InputStream in) {
        this.in = in;
        this.head = 0;
        this.tail = 0;
        this.eof = false;
    }

    public void reset() {
        reset(this.buf);
    }

    public final void close() throws IOException {
        if (in != null) {
            in.close();
        }
    }

    final byte readByte() throws IOException {
        if (head == tail) {
            if (!loadMore()) {
                return 0;
            }
        }
        return buf[head++];
    }

    final boolean loadMore() throws IOException {
        if (in == null) {
            eof = true;
            return false;
        }
        int n = in.read(buf);
        if (n < 1) {
            if (n == -1) {
                eof = true;
                return false;
            } else {
                throw reportError("loadMore", "read from input stream returned " + n);
            }
        } else {
            head = 0;
            tail = n;
        }
        return true;
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
        byte c = nextToken();
        if (c == 'n') {
            IterImplSkip.skipUntilBreak(this);
            return true;
        }
        unreadByte();
        return false;
    }

    public final boolean readBoolean() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 't':
                IterImplSkip.skipUntilBreak(this);
                return true;
            case 'f':
                IterImplSkip.skipUntilBreak(this);
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
        byte c = nextToken();
        switch (c) {
            case '[':
                c = nextToken();
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
                throw reportError("readArray", "expect [ or , or n or ], but found: " + (char)c);
        }
    }

    final byte nextToken() throws IOException {
        for (; ; ) {
            for (int i = head; i < tail; i++) {
                byte c = buf[i];
                switch (c) {
                    case ' ':
                    case '\n':
                    case '\t':
                    case '\r':
                        continue;
                }
                head = i + 1;
                return c;
            }
            if (!loadMore()) {
                return 0;
            }
        }
    }

    public final String readString() throws IOException {
        return IterImplString.readString(this);
    }

    public final byte[] readBase64() throws IOException {
        return IterImplString.readBase64(this);
    }

    public final String readObject() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 'n':
                IterImplSkip.skipUntilBreak(this);
                return null;
            case '{':
                c = nextToken();
                switch (c) {
                    case '}':
                        return null; // end of object
                    case '"':
                        unreadByte();
                        String field = readString();
                        if (nextToken() != ':') {
                            throw reportError("readObject", "expect :");
                        }
                        return field;
                    default:
                        throw reportError("readObject", "expect \" after {");
                }
            case ',':
                String field = readString();
                if (nextToken() != ':') {
                    throw reportError("readObject", "expect :");
                }
                return field;
            case '}':
                return null; // end of object
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
        return new Any(readAnyObject());
    }

    public final Object readAnyObject() throws IOException {
        ValueType valueType = whatIsNext();
        switch (valueType) {
            case STRING:
                return readString();
            case NUMBER:
                return readDouble();
            case NULL:
                return null;
            case BOOLEAN:
                return readBoolean();
            case ARRAY:
                ArrayList list = new ArrayList();
                while (readArray()) {
                    list.add(readAnyObject());
                }
                return list;
            case OBJECT:
                Map map = new HashMap();
                for (String field = readObject(); field != null; field = readObject()) {
                    map.put(field, readAnyObject());
                }
                return map;
            default:
                throw reportError("readAnyObject", "unexpected value type: " + valueType);
        }
    }

    public final <T> T read(T existingObject) throws IOException {
        this.existingObject = existingObject;
        Class<?> clazz = existingObject.getClass();
        return (T) Codegen.getDecoder(TypeLiteral.generateDecoderCacheKey(clazz), clazz).decode(this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral, T existingObject) throws IOException {
        this.existingObject = existingObject;
        return (T) Codegen.getDecoder(typeLiteral.getDecoderCacheKey(), typeLiteral.getType()).decode(this);
    }

    public final <T> T read(Class<T> clazz) throws IOException {
        return (T) Codegen.getDecoder(TypeLiteral.generateDecoderCacheKey(clazz), clazz).decode(this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral) throws IOException {
        String cacheKey = typeLiteral.getDecoderCacheKey();
        return (T) Codegen.getDecoder(cacheKey, typeLiteral.getType()).decode(this);
    }

    public final <T> T read(String input, Class<T> clazz) {
        reset(input.getBytes());
        try {
            return read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T read(String input, TypeLiteral<T> typeLiteral) {
        reset(input.getBytes());
        try {
            return read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T read(byte[] input, Class<T> clazz) {
        reset(input);
        try {
            return read(clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public final <T> T read(byte[] input, TypeLiteral<T> typeLiteral) {
        reset(input);
        try {
            return read(typeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public ValueType whatIsNext() throws IOException {
        ValueType valueType = valueTypes[nextToken()];
        unreadByte();
        return valueType;
    }

    public void skip() throws IOException {
        IterImplSkip.skip(this);
    }

    public static void setMode(DecodingMode mode) {
        Codegen.setMode(mode);
    }
}
