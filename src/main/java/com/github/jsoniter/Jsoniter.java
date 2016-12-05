package com.github.jsoniter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

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

    byte readByte() throws IOException {
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

    public int ReadUnsignedInt() throws IOException {
        byte c = readByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw exp("ReadUnsignedInt", "expect 0~9");
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

    private RuntimeException exp(String op, String msg) {
        return new RuntimeException(op + ": " + msg + ", head: " + head + ", buf: " + new String(buf));
    }

    public boolean ReadArray() throws IOException {
        skipWhitespaces();
        byte c = readByte();
        switch (c) {
            case '[':
                skipWhitespaces();
                c = readByte();
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
                throw exp("ReadArray", "expect [ or , or n or ]");
        }
    }

    private void skipWhitespaces() throws IOException {
        byte c = readByte();
        for (; ; ) {
            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                    c = readByte();
                default:
                    unreadByte();
                    return;

            }
        }
    }

    public Slice ReadString() throws IOException {
        Slice result = Slice.make(0, 10);
        byte c = readByte();
        switch (c) {
            case 'n':
                throw new UnsupportedOperationException();
            case '"':
                break;
            default:
                throw exp("ReadString", "expect n or \"");
        }
        for (; ; ) {
            c = readByte();
            switch (c) {
                case '"':
                    return result;
                case '\\':
                    c = readByte();
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
                            int v = digits[readByte()];
                            if (v == -1) {
                                throw exp("ReadString", "expect 0~9 or a~f");
                            }
                            char b = (char) v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw exp("ReadString", "expect 0~9 or a~f");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw exp("ReadString", "expect 0~9 or a~f");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = digits[readByte()];
                            if (v == -1) {
                                throw exp("ReadString", "expect 0~9 or a~f");
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
                            throw exp("ReadString", "invalid escape char after \\");
                    }
                    break;
                default:
                    result.append(c);
            }
        }
    }
}
