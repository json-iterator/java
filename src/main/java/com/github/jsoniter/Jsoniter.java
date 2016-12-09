package com.github.jsoniter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class Jsoniter implements Closeable {

    final InputStream in;
    byte[] buf;
    int head;
    int tail;
    boolean eof;
    private final Slice reusableSlice = new Slice(null, 0, 0);
    private char[] reusableChars = new char[256];

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

    public static Jsoniter parseBytes(byte[] buf) {
        return new Jsoniter(null, buf);
    }

    public static Jsoniter parseString(String str) {
        return parseBytes(str.getBytes());
    }

    public final void reuse(byte[] buf) {
        this.buf = buf;
        this.head = 0;
        this.tail = buf.length;
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
                throw new IOException("read returned " + n);
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
        byte c = readByte();
        if (c == 'n') {
            skipUntilBreak();
            return true;
        }
        unreadByte();
        return false;
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
        byte c = readByte();
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
        int v = Slice.digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw err("readUnsignedInt", "expect 0~9");
        }
        int result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = readByte();
            v = Slice.digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
    }

    public final long readLong() throws IOException {
        byte c = readByte();
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
        int v = Slice.digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw err("readUnsignedLong", "expect 0~9");
        }
        long result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = readByte();
            v = Slice.digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
    }

    public final RuntimeException err(String op, String msg) {
        return new RuntimeException(op + ": " + msg + ", head: " + head + ", buf: " + new String(buf));
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
                skipWhitespaces();
                return true;
            case 'n':
                skipUntilBreak();
                return false;
            default:
                throw err("readArray", "expect [ or , or n or ]");
        }
    }

    final void skipWhitespaces() throws IOException {
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
                head = i;
                return;
            }
            if (!loadMore()) {
                return;
            }
        }
    }

    final boolean skipWhitespacesWithoutLoadMore() throws IOException {
        for (int i = head; i < tail; i++) {
            byte c = buf[i];
            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    continue;
            }
            head = i;
            return false;
        }
        return true;
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

    public final Slice readSlice() throws IOException {
        byte c = readByte();
        switch (c) {
            case 'n':
                skipUntilBreak();
                return null;
            case '"':
                break;
            default:
                throw err("readSlice", "expect n or \"");
        }
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
                throw err("readSlice", "unmatched quote");
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

    public final String readString() throws IOException {
        byte c = readByte();
        switch (c) {
            case 'n':
                skipUntilBreak();
                return null;
            case '"':
                break;
            default:
                throw err("readSlice", "expect n or \"");
        }
        // try fast path first
        for (int i = head, j = 0; i < tail && j < reusableChars.length; i++, j++) {
            c = buf[i];
            if (c == '"') {
                head = i+1;
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
        for(;;) {
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
                            int v = Slice.digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            char b = (char) v;
                            v = Slice.digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = Slice.digits[readByte()];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = Slice.digits[readByte()];
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
                                (b2 <<  6) ^
                                (b3 ^
                                        (((byte) 0xE0 << 12) ^
                                                ((byte) 0x80 <<  6) ^
                                                ((byte) 0x80 <<  0))));
                reusableChars[j++] = c;
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int b2 = readByte();
                int b3 = readByte();
                int b4 = readByte();
                int uc = ((b1 << 18) ^
                        (b2 << 12) ^
                        (b3 <<  6) ^
                        (b4 ^
                                (((byte) 0xF0 << 18) ^
                                        ((byte) 0x80 << 12) ^
                                        ((byte) 0x80 <<  6) ^
                                        ((byte) 0x80 <<  0))));
                reusableChars[j++] = Character.highSurrogate(uc);
                reusableChars[j++] = Character.lowSurrogate(uc);
            } else {
                throw new RuntimeException("unexpected input");
            }
        }
    }

    public final Slice readObject() throws IOException {
        byte c = nextToken();
        switch (c) {
            case 'n':
                skipUntilBreak();
                return Slice.make(0, 0);
            case '{':
                c = nextToken();
                switch (c) {
                    case '}':
                        return null; // end of object
                    case '"':
                        unreadByte();
                        return readObjectField();
                    default:
                        throw err("readObject", "expect \" after {");
                }
            case ',':
                skipWhitespaces();
                return readObjectField();
            case '}':
                return null; // end of object
            default:
                throw err("readObject", "expect { or , or } or n");
        }
    }

    final Slice readObjectField() throws IOException {
        Slice field = readSlice();
        boolean notCopied = field != null;
        if (skipWhitespacesWithoutLoadMore()) {
            if (notCopied) {
                byte[] newBuf = new byte[field.len];
                System.arraycopy(field.data, field.head, newBuf, 0, field.len);
                field.data = newBuf;
                field.head = 0;
                field.len = newBuf.length;
            }
            if (!loadMore()) {
                throw err("readObjectField", "expect : after object field");
            }
        }
        if (buf[head] != ':') {
            throw err("readObjectField", "expect : after object field");
        }
        head++;
        if (skipWhitespacesWithoutLoadMore()) {
            if (notCopied) {
                byte[] newBuf = new byte[field.len];
                System.arraycopy(field.data, field.head, newBuf, 0, field.len);
                field.data = newBuf;
                field.head = 0;
                field.len = newBuf.length;
            }
            if (!loadMore()) {
                throw err("readObjectField", "expect : after object field");
            }
        }
        return field;
    }

    final String readNumber() throws IOException {
        int j = 0;
        for (byte c = readByte(); !eof; c = readByte()) {
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

    public final double readDouble() throws IOException {
        return Double.valueOf(readNumber());
    }

    public final <T> T read(Class<T> clazz) throws IOException {
        return (T) Codegen.gen(clazz, null).decode(clazz, this);
    }

    public final <T> T read(Class<T> clazz, Type typeArg) throws IOException {
        return (T) Codegen.gen(clazz, new Type[]{typeArg}).decode(clazz, this);
    }

    public final <T> T read(TypeLiteral<T> typeLiteral) throws IOException {
        Type type = typeLiteral.getType();
        return (T) Codegen.gen(type, null).decode(type, this);
    }

    public final void skip() throws IOException {
        byte c = readByte();
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
                throw err("Skip", "do not know how to skip: " + c);
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
                switch (c) {
                    case ' ':
                    case '\n':
                    case '\r':
                    case '\t':
                    case ',':
                    case '}':
                    case ']':
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
                    head = 1; // skip the first char as last char read is \
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
                throw err("findSliceEnd", "slice does not support escape char");
            }
        }
        return -1;
    }
}
