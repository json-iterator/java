package com.github.jsoniter;

import sun.misc.FloatingDecimal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Jsoniter implements Closeable {

    final static char rune1Max = 1 << 7 - 1;
    final static char rune2Max = 1 << 11 - 1;
    final static char rune3Max = 1 << 16 - 1;
    final static char tx = 0x80; // 1000 0000
    final static char t2 = 0xC0; // 1100 0000
    final static char t3 = 0xE0; // 1110 0000
    final static char t4 = 0xF0; // 1111 0000
    final static char maskx = 0x3F; // 0011 1111
    final static int[] digits = new int[(int) 'f' + 1];
    final InputStream in;
    final byte[] buf;
    int head;
    int tail;
    boolean eof;

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
    }


    public void close() throws IOException {
        if (in != null) {
            in.close();
        }
    }

    byte nextByte() throws IOException {
        if (head == tail) {
            if (in == null) {
                eof = true;
                return 0;
            }
            int n = in.read(buf);
            if (n < 1) {
                if (n == -1) {
                    eof = true;
                    return 0;
                } else {
                    throw new IOException("read returned " + n);
                }
            } else {
                head = 0;
                tail = n;
            }
        }
        return buf[head++];
    }

    void unreadByte() throws IOException {
        if (head == 0) {
            throw new IOException("unread too many bytes");
        }
        head--;
    }

    public byte readByte() throws IOException {
        int v = readInt();
        if (Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE) {
            return (byte) v;
        } else {
            throw new RuntimeException("byte overflow: " + v);
        }
    }

    public short readShort() throws IOException {
        int v = readInt();
        if (Short.MIN_VALUE <= v && v <= Short.MAX_VALUE) {
            return (short) v;
        } else {
            throw new RuntimeException("short overflow: " + v);
        }
    }

    public int readInt() throws IOException {
        byte c = nextByte();
        if (c == '-') {
            return -readUnsignedInt();
        } else {
            unreadByte();
            return readUnsignedInt();
        }
    }

    public int readUnsignedInt() throws IOException {
        // TODO: throw overflow
        byte c = nextByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw err("readUnsignedInt", "expect 0~9");
        }
        int result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = nextByte();
            v = digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
    }

    public long readLong() throws IOException {
        byte c = nextByte();
        if (c == '-') {
            return -readUnsignedLong();
        } else {
            unreadByte();
            return readUnsignedLong();
        }
    }

    public long readUnsignedLong() throws IOException {
        // TODO: throw overflow
        byte c = nextByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw err("readUnsignedLong", "expect 0~9");
        }
        long result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = nextByte();
            v = digits[c];
            if (v == -1) {
                unreadByte();
                break;
            }
        }
        return result;
    }

    public RuntimeException err(String op, String msg) {
        return new RuntimeException(op + ": " + msg + ", head: " + head + ", buf: " + new String(buf));
    }

    public boolean readArray() throws IOException {
        skipWhitespaces();
        byte c = nextByte();
        switch (c) {
            case '[':
                skipWhitespaces();
                c = nextByte();
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
                throw new UnsupportedOperationException();
            default:
                throw err("readArray", "expect [ or , or n or ]");
        }
    }

    private void skipWhitespaces() throws IOException {
        byte c = nextByte();
        for (; ; ) {
            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                    c = nextByte();
                default:
                    unreadByte();
                    return;

            }
        }
    }

    public Slice readString() throws IOException {
        Slice result = Slice.make(0, 10);
        byte c = nextByte();
        switch (c) {
            case 'n':
                throw new UnsupportedOperationException();
            case '"':
                break;
            default:
                throw err("readString", "expect n or \"");
        }
        for (; ; ) {
            c = nextByte();
            switch (c) {
                case '"':
                    return result;
                case '\\':
                    c = nextByte();
                    switch (c) {
                        case '"':
                            result.append((byte) '"');
                            break;
                        case '\\':
                            result.append((byte) '\\');
                            break;
                        case '/':
                            result.append((byte) '/');
                            break;
                        case 'b':
                            result.append((byte) '\b');
                            break;
                        case 'f':
                            result.append((byte) '\f');
                            break;
                        case 'n':
                            result.append((byte) '\n');
                            break;
                        case 'r':
                            result.append((byte) '\r');
                            break;
                        case 't':
                            result.append((byte) '\t');
                            break;
                        case 'u':
                            int v = digits[nextByte()];
                            if (v == -1) {
                                throw err("readString", "expect 0~9 or a~f");
                            }
                            char b = (char) v;
                            v = digits[nextByte()];
                            if (v == -1) {
                                throw err("readString", "expect 0~9 or a~f");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[nextByte()];
                            if (v == -1) {
                                throw err("readString", "expect 0~9 or a~f");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[nextByte()];
                            if (v == -1) {
                                throw err("readString", "expect 0~9 or a~f");
                            }
                            b = (char) (b << 4);
                            b += v;
                            if (b <= rune1Max) {
                                result.append((byte) b);
                            } else if (b <= rune2Max) {
                                result.append((byte) (t2 | ((byte) (b >> 6))));
                                result.append((byte) (tx | ((byte) b) & maskx));
                            } else if (b <= rune3Max) {
                                result.append((byte) (t3 | ((byte) (b >> 12))));
                                result.append((byte) (tx | ((byte) (b >> 6)) & maskx));
                                result.append((byte) (tx | ((byte) b) & maskx));
                            } else {
                                result.append((byte) (t4 | ((byte) (b >> 18))));
                                result.append((byte) (tx | ((byte) (b >> 12)) & maskx));
                                result.append((byte) (tx | ((byte) (b >> 6)) & maskx));
                                result.append((byte) (tx | ((byte) b) & maskx));
                            }
                            break;
                        default:
                            throw err("readString", "invalid escape char after \\");
                    }
                    break;
                default:
                    result.append(c);
            }
        }
    }

    public Slice readObject() throws IOException {
        skipWhitespaces();
        byte c = nextByte();
        switch (c) {
            case 'n':
                throw new UnsupportedOperationException();
            case '{':
                skipWhitespaces();
                c = nextByte();
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

    private Slice readObjectField() throws IOException {
        Slice field = readString();
        skipWhitespaces();
        byte c = nextByte();
        if (c != ':') {
            throw err("readObjectField", "expect : after object field");
        }
        skipWhitespaces();
        return field;
    }

    public <T> T read(Class<T> clazz) throws IOException {
        return (T) Codegen.gen(clazz).decode(clazz, this);
    }

    private String readNumber() throws IOException {
        StringBuilder str = new StringBuilder(8);
        for (byte c = nextByte(); !eof; c = nextByte()) {
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
                    str.append(((char) c));
                    break;
                default:
                    unreadByte();
                    return str.toString();
            }
        }
        return str.toString();
    }

    public float readFloat() throws IOException {
        // TODO: remove dependency on sun.misc
        return FloatingDecimal.readJavaFormatString(readNumber()).floatValue();
    }

    public double readDouble() throws IOException {
        // TODO: remove dependency on sun.misc
        return FloatingDecimal.readJavaFormatString(readNumber()).doubleValue();
    }
}
