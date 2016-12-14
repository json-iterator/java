package com.jsoniter;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.*;

public class Jsoniter implements Closeable {

    private static final boolean[] breaks = new boolean[256];
    final static int[] digits = new int[256];
    final static ValueType[] valueTypes = new ValueType[256];
    int[] base64Tbl = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2,
            3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    InputStream in;
    byte[] buf;
    int head;
    int tail;
    boolean eof;
    private final Slice reusableSlice = new Slice(null, 0, 0);
    private char[] reusableChars = new char[32];

    static {
        for (int i = 0; i < digits.length; i++) {
            digits[i] = -1;
        }
        for (int i = '0'; i <= '9'; ++i) {
            digits[i] = (i - '0');
        }
        for (int i = 'a'; i <= 'f'; ++i) {
            digits[i] = ((i - 'a') + 10);
        }
        for (int i = 'A'; i <= 'F'; ++i) {
            digits[i] = ((i - 'A') + 10);
        }
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
        breaks[' '] = true;
        breaks['\t'] = true;
        breaks['\n'] = true;
        breaks['\r'] = true;
        breaks[','] = true;
        breaks['}'] = true;
        breaks[']'] = true;
    }

    public Jsoniter(InputStream in, byte[] buf) throws IOException {
        this.in = in;
        this.buf = buf;
        if (this.in == null) {
            tail = buf.length;
        }
    }

    public static Jsoniter parse(InputStream in, int bufSize) throws IOException {
        return new Jsoniter(in, new byte[bufSize]);
    }

    public static Jsoniter parse(byte[] buf) throws IOException {
        return new Jsoniter(null, buf);
    }

    public static Jsoniter parse(String str) throws IOException {
        return parse(str.getBytes());
    }

    public final void reset(byte[] buf) throws IOException {
        this.buf = buf;
        this.head = 0;
        this.tail = buf.length;
        this.eof = false;
    }

    public final void reset(InputStream in) throws IOException {
        this.in = in;
        this.head = 0;
        this.tail = 0;
        this.eof = false;
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

    public final boolean readNull() throws IOException {
        byte c = nextToken();
        if (c == 'n') {
            skipUntilBreak();
            return true;
        }
        unreadByte();
        return false;
    }

    public final boolean readBoolean() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 't':
                skipUntilBreak();
                return true;
            case 'f':
                skipUntilBreak();
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
        byte c = nextToken();
        if (c == '-') {
            return -readUnsignedInt();
        } else {
            unreadByte();
            return readUnsignedInt();
        }
    }

    public final int readUnsignedInt() throws IOException {
        // TODO: throw overflow
        byte c = readByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw reportError("readUnsignedInt", "expect 0~9");
        }
        int result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = readByte();
            v = digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
    }

    public final long readLong() throws IOException {
        byte c = nextToken();
        if (c == '-') {
            return -readUnsignedLong();
        } else {
            unreadByte();
            return readUnsignedLong();
        }
    }

    public final long readUnsignedLong() throws IOException {
        // TODO: throw overflow
        byte c = readByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw reportError("readUnsignedLong", "expect 0~9");
        }
        long result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = readByte();
            v = digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
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

    final Slice readSlice() throws IOException {
        int end = findSliceEnd();
        if (end != -1) {
            // reuse current buffer
            reusableSlice.data = buf;
            reusableSlice.head = head;
            reusableSlice.len = end - head - 1;
            head = end;
            return reusableSlice;
        }
        byte[] part1 = new byte[tail - head];
        System.arraycopy(buf, head, part1, 0, part1.length);
        for (; ; ) {
            if (!loadMore()) {
                throw reportError("readSlice", "unmatched quote");
            }
            end = findSliceEnd();
            if (end == -1) {
                byte[] part2 = new byte[part1.length + buf.length];
                System.arraycopy(part1, 0, part2, 0, part1.length);
                System.arraycopy(buf, 0, part2, part1.length, buf.length);
                part1 = part2;
            } else {
                byte[] part2 = new byte[part1.length + end - 1];
                System.arraycopy(part1, 0, part2, 0, part1.length);
                System.arraycopy(buf, 0, part2, part1.length, end - 1);
                head = end;
                reusableSlice.data = part2;
                reusableSlice.head = 0;
                reusableSlice.len = part2.length;
                return reusableSlice;
            }
        }
    }

    public final byte[] readBase64() throws IOException {
        // from https://gist.github.com/EmilHernvall/953733
        if (nextToken() != '"') {
            throw reportError("readBase64", "expect \" for base64");
        }
        Slice slice = readSlice();
        if (slice == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int end = slice.head + slice.len;
        for (int i = slice.head; i < end; i++) {
            int b = 0;
            if (base64Tbl[slice.data[i]] != -1) {
                b = (base64Tbl[slice.data[i]] & 0xFF) << 18;
            }
            // skip unknown characters
            else {
                i++;
                continue;
            }

            int num = 0;
            if (i + 1 < end && base64Tbl[slice.data[i + 1]] != -1) {
                b = b | ((base64Tbl[slice.data[i + 1]] & 0xFF) << 12);
                num++;
            }
            if (i + 2 < end && base64Tbl[slice.data[i + 2]] != -1) {
                b = b | ((base64Tbl[slice.data[i + 2]] & 0xFF) << 6);
                num++;
            }
            if (i + 3 < end && base64Tbl[slice.data[i + 3]] != -1) {
                b = b | (base64Tbl[slice.data[i + 3]] & 0xFF);
                num++;
            }

            while (num > 0) {
                int c = (b & 0xFF0000) >> 16;
                buffer.write((char) c);
                b <<= 8;
                num--;
            }
            i += 4;
        }
        return buffer.toByteArray();
    }

    public final String readString() throws IOException {
        byte c = nextToken();
        if (c == 'n') {
            skipUntilBreak();
            return null;
        }
        if (c != '"') {
            throw reportError("readString", "expect n or \"");
        }
        // try fast path first
        for (int i = head, j = 0; i < tail && j < reusableChars.length; i++, j++) {
            c = buf[i];
            if (c == '"') {
                head = i + 1;
                return new String(reusableChars, 0, j);
            }
            // If we encounter a backslash, which is a beginning of an escape sequence
            // or a high bit was set - indicating an UTF-8 encoded multibyte character,
            // there is no chance that we can decode the string without instantiating
            // a temporary buffer, so quit this loop
            if ((c ^ '\\') < 1) break;
            reusableChars[j] = (char) c;
        }
        return readStringSlowPath();
    }

    final String readStringSlowPath() throws IOException {
        // http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/sun/nio/cs/UTF_8.java/?v=source
        // byte => char with support of escape in one pass
        int j = 0;
        int minimumCapacity = reusableChars.length - 2;
        for (; ; ) {
            if (j == minimumCapacity) {
                char[] newBuf = new char[reusableChars.length * 2];
                System.arraycopy(reusableChars, 0, newBuf, 0, reusableChars.length);
                reusableChars = newBuf;
                minimumCapacity = reusableChars.length - 2;
            }
            int b1 = readByte();
            if (b1 >= 0) {
                if (b1 == '"') {
                    return new String(reusableChars, 0, j);
                } else if (b1 == '\\') {
                    int b2 = readByte();
                    switch (b2) {
                        case '"':
                            reusableChars[j++] = '"';
                            break;
                        case '\\':
                            reusableChars[j++] = '\\';
                            break;
                        case '/':
                            reusableChars[j++] = '/';
                            break;
                        case 'b':
                            reusableChars[j++] = '\b';
                            break;
                        case 'f':
                            reusableChars[j++] = '\f';
                            break;
                        case 'n':
                            reusableChars[j++] = '\n';
                            break;
                        case 'r':
                            reusableChars[j++] = '\r';
                            break;
                        case 't':
                            reusableChars[j++] = '\t';
                            break;
                        case 'u':
                            int v = digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            char b = (char) v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            reusableChars[j++] = b;
                            break;
                        default:
                            throw new RuntimeException("unexpected escape char: " + b2);
                    }
                } else {
                    // 1 byte, 7 bits: 0xxxxxxx
                    reusableChars[j++] = (char) b1;
                }
            } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                int b2 = readByte();
                reusableChars[j++] = (char) (((b1 << 6) ^ b2)
                        ^
                        (((byte) 0xC0 << 6) ^
                                ((byte) 0x80 << 0)));
            } else if ((b1 >> 4) == -2) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int b2 = readByte();
                int b3 = readByte();
                char c = (char)
                        ((b1 << 12) ^
                                (b2 << 6) ^
                                (b3 ^
                                        (((byte) 0xE0 << 12) ^
                                                ((byte) 0x80 << 6) ^
                                                ((byte) 0x80 << 0))));
                reusableChars[j++] = c;
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int b2 = readByte();
                int b3 = readByte();
                int b4 = readByte();
                int uc = ((b1 << 18) ^
                        (b2 << 12) ^
                        (b3 << 6) ^
                        (b4 ^
                                (((byte) 0xF0 << 18) ^
                                        ((byte) 0x80 << 12) ^
                                        ((byte) 0x80 << 6) ^
                                        ((byte) 0x80 << 0))));
                reusableChars[j++] = highSurrogate(uc);
                reusableChars[j++] = lowSurrogate(uc);
            } else {
                throw new RuntimeException("unexpected input");
            }
        }
    }

    private static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
                + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
    }

    private static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
    }

    public final String readObject() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 'n':
                skipUntilBreak();
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

    final String readNumber() throws IOException {
        int j = 0;
        for (byte c = nextToken(); !eof; c = readByte()) {
            if (j == reusableChars.length) {
                char[] newBuf = new char[reusableChars.length * 2];
                System.arraycopy(reusableChars, 0, newBuf, 0, reusableChars.length);
                reusableChars = newBuf;
            }
            switch (c) {
                case '-':
                case '+':
                case '.':
                case 'e':
                case 'E':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    reusableChars[j++] = (char) c;
                    break;
                default:
                    unreadByte();
                    return new String(reusableChars, 0, j);
            }
        }
        return new String(reusableChars, 0, j);
    }

    public final float readFloat() throws IOException {
        return Float.valueOf(readNumber());
    }

    public final double readDoubleSlowPath() throws IOException {
        return Double.valueOf(readNumber());
    }

    public final double readDouble() throws IOException {
        if (head == tail) {
            if (!loadMore()) {
                throw reportError("readDouble", "no more to read");
            }
        }
        final byte ch = buf[head];
        if (ch == '-') {
            return parseNegativeDouble(head + 1);
        } else if (ch == '+') {
            return parsePositiveDouble(head + 1);
        }
        return parsePositiveDouble(head);
    }

    private final double parsePositiveDouble(int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < tail; i++) {
            c = buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) + ind;
            if (ind < 0 || ind > 9) {
                return readDoubleSlowPath();
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < tail; i++) {
                c = buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    head = i;
                    return value / (double) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) + ind;
                if (ind < 0 || ind > 9) {
                    return readDoubleSlowPath();
                }
            }
        }
        return readDoubleSlowPath();
    }

    private final double parseNegativeDouble(int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < tail; i++) {
            c = buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) - ind;
            if (ind < 0 || ind > 9) {
                return readDoubleSlowPath();
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < tail; i++) {
                c = buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    head = i;
                    return value / (double) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) - ind;
                if (ind < 0 || ind > 9) {
                    return readDoubleSlowPath();
                }
            }
        }
        return readDoubleSlowPath();
    }

    public final BigDecimal readBigDecimal() throws IOException {
        return new BigDecimal(readNumber());
    }

    public final BigInteger readBigInteger() throws IOException {
        return new BigInteger(readNumber());
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

    public final void skip() throws IOException {
        byte c = nextToken();
        switch (c) {
            case '"':
                skipString();
                return;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 't':
            case 'f':
            case 'n':
                skipUntilBreak();
                return;
            case '[':
                skipArray();
                return;
            case '{':
                skipObject();
                return;
            default:
                throw reportError("Skip", "do not know how to skip: " + c);
        }
    }

    final void skipObject() throws IOException {
        int level = 1;
        for (; ; ) {
            for (int i = head; i < tail; i++) {
                switch (buf[i]) {
                    case '"': // If inside string, skip it
                        head = i + 1;
                        skipString();
                        i = head - 1; // it will be i++ soon
                        break;
                    case '{': // If open symbol, increase level
                        level++;
                        break;
                    case '}': // If close symbol, increase level
                        level--;

                        // If we have returned to the original level, we're done
                        if (level == 0) {
                            head = i + 1;
                            return;
                        }
                        break;
                }
            }
            if (!loadMore()) {
                return;
            }
        }
    }

    final void skipArray() throws IOException {
        int level = 1;
        for (; ; ) {
            for (int i = head; i < tail; i++) {
                switch (buf[i]) {
                    case '"': // If inside string, skip it
                        head = i + 1;
                        skipString();
                        i = head - 1; // it will be i++ soon
                        break;
                    case '[': // If open symbol, increase level
                        level++;
                        break;
                    case ']': // If close symbol, increase level
                        level--;

                        // If we have returned to the original level, we're done
                        if (level == 0) {
                            head = i + 1;
                            return;
                        }
                        break;
                }
            }
            if (!loadMore()) {
                return;
            }
        }
    }

    final void skipUntilBreak() throws IOException {
        // true, false, null, number
        for (; ; ) {
            for (int i = head; i < tail; i++) {
                byte c = buf[i];
                if (breaks[c]) {
                    head = i;
                    return;
                }
            }
            if (!loadMore()) {
                return;
            }
        }
    }

    final void skipString() throws IOException {
        for (; ; ) {
            int end = findStringEnd();
            if (end == -1) {
                int j = tail - 1;
                boolean escaped = true;
                for (; ; ) {
                    if (j < head || buf[j] != '\\') {
                        // even number of backslashes
                        // either end of buffer, or " found
                        escaped = false;
                        break;
                    }
                    j--;
                    if (j < head || buf[j] != '\\') {
                        // odd number of backslashes
                        // it is \" or \\\"
                        break;
                    }
                    j--;

                }
                if (!loadMore()) {
                    return;
                }
                if (escaped) {
                    head = 1; // skip the first char as last char readAny is \
                }
            } else {
                head = end;
                return;
            }
        }
    }

    // adapted from: https://github.com/buger/jsonparser/blob/master/parser.go
    // Tries to find the end of string
    // Support if string contains escaped quote symbols.
    final int findStringEnd() {
        boolean escaped = false;
        for (int i = head; i < tail; i++) {
            byte c = buf[i];
            if (c == '"') {
                if (!escaped) {
                    return i + 1;
                } else {
                    int j = i - 1;
                    for (; ; ) {
                        if (j < head || buf[j] != '\\') {
                            // even number of backslashes
                            // either end of buffer, or " found
                            return i + 1;
                        }
                        j--;
                        if (j < head || buf[j] != '\\') {
                            // odd number of backslashes
                            // it is \" or \\\"
                            break;
                        }
                        j--;
                    }
                }
            } else if (c == '\\') {
                escaped = true;
            }
        }
        return -1;
    }

    // slice does not allow escape
    final int findSliceEnd() {
        for (int i = head; i < tail; i++) {
            byte c = buf[i];
            if (c == '"') {
                return i + 1;
            } else if (c == '\\') {
                throw reportError("findSliceEnd", "slice does not support escape char");
            }
        }
        return -1;
    }

    public ValueType whatIsNext() throws IOException {
        ValueType valueType = valueTypes[nextToken()];
        unreadByte();
        return valueType;
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
        Codegen.registerExtension(extension);
    }
}
