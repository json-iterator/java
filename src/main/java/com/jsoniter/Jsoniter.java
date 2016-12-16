package com.jsoniter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Jsoniter implements Closeable {

    final static ValueType[] valueTypes = new ValueType[256];
    InputStream in;
    byte[] buf;
    int head;
    int tail;
    boolean eof;
    final Slice reusableSlice = new Slice(null, 0, 0);
    char[] reusableChars = new char[32];

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

    public Jsoniter(InputStream in, byte[] buf) {
        this.in = in;
        this.buf = buf;
        if (this.in == null) {
            tail = buf.length;
        }
    }

    public static Jsoniter parse(InputStream in, int bufSize) {
        return new Jsoniter(in, new byte[bufSize]);
    }

    public static Jsoniter parse(byte[] buf) {
        return new Jsoniter(null, buf);
    }

    public static Jsoniter parse(String str) {
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

    public final RuntimeException reportError(String op, String msg) {
        int peekStart = head - 10;
        if (peekStart < 0) {
            peekStart = 0;
        }
        String peek = new String(buf, peekStart, head - peekStart);
        throw new RuntimeException(op + ": " + msg + ", head: " + head + ", peek: " + peek + ", buf: " + new String(buf));
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
            Skip.skipUntilBreak(this);
            return true;
        }
        unreadByte();
        return false;
    }

    public final boolean readBoolean() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 't':
                Skip.skipUntilBreak(this);
                return true;
            case 'f':
                Skip.skipUntilBreak(this);
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
            throw new RuntimeException("short overflow: " + v);
        }
    }

    public final int readInt() throws IOException {
        return NumberReader.readInt(this);
    }

    public final long readLong() throws IOException {
        return NumberReader.readLong(this);
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
                throw reportError("readArray", "expect [ or , or n or ]");
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
        return StringReader.readString(this);
    }

    public final byte[] readBase64() throws IOException {
        return StringReader.readBase64(this);
    }

    public final String readObject() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 'n':
                Skip.skipUntilBreak(this);
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
        return NumberReader.readFloat(this);
    }

    public final double readDouble() throws IOException {
        return NumberReader.readDouble(this);
    }

    public final BigDecimal readBigDecimal() throws IOException {
        return new BigDecimal(NumberReader.readNumber(this));
    }

    public final BigInteger readBigInteger() throws IOException {
        return new BigInteger(NumberReader.readNumber(this));
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

    public final <T> T read(Class<T> clazz) throws IOException {
        return (T) Codegen.getDecoder(TypeLiteral.generateCacheKey(clazz), clazz).decode(this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral) throws IOException {
        Type type = typeLiteral.getType();
        return (T) Codegen.getDecoder(typeLiteral.cacheKey, type).decode(this);
    }

    public ValueType whatIsNext() throws IOException {
        ValueType valueType = valueTypes[nextToken()];
        unreadByte();
        return valueType;
    }

    public void skip() throws IOException {
        Skip.skip(this);
    }

    public static void registerTypeDecoder(Class clazz, Decoder decoder) {
        Codegen.addNewDecoder(TypeLiteral.generateCacheKey(clazz), decoder);
    }

    public static void registerTypeDecoder(TypeLiteral typeLiteral, Decoder decoder) {
        Codegen.addNewDecoder(typeLiteral.cacheKey, decoder);
    }

    public static void registerFieldDecoder(Class clazz, String field, Decoder decoder) {
        Codegen.addNewDecoder(field + "@" + TypeLiteral.generateCacheKey(clazz), decoder);
    }

    public static void registerFieldDecoder(TypeLiteral typeLiteral, String field, Decoder decoder) {
        Codegen.addNewDecoder(field + "@" + typeLiteral.cacheKey, decoder);
    }

    public static void registerExtension(Extension extension) {
        ExtensionManager.registerExtension(extension);
    }

    public static void enableStrictMode() {
        Codegen.enableStrictMode();
    }
}
