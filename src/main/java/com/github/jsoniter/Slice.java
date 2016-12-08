package com.github.jsoniter;

import java.nio.CharBuffer;

public class Slice {

    final static int[] digits = new int[256];
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

    public byte[] data;
    public int head;
    public int len;

    public Slice(byte[] data, int head, int len) {
        this.data = data;
        this.head = head;
        this.len = len;
    }

    public static Slice make(int len, int cap) {
        return new Slice(new byte[cap], 0, len);
    }

    public static Slice make(String str) {
        byte[] data = str.getBytes();
        return new Slice(data, 0, data.length);
    }

    public final void append(byte c) {
        if (len == data.length) {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[len++] = c;
    }

    public final byte at(int pos) {
        return data[head+pos];
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slice slice = (Slice) o;

        if (len != slice.len) return false;

        for (int i = head; i < len; i++)
            if (data[i] != slice.data[i])
                return false;
        return true;

    }

    @Override
    public final int hashCode() {
        int result = 1;
        for (int i = head; i < len; i++) {
            result = 31 * result + data[i];
        }
        return result;
    }

    @Override
    public final String toString() {
        // http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/sun/nio/cs/UTF_8.java/?v=source
        // byte => char with support of escape in one pass
        CharBuffer dst = CharBuffer.allocate(len);
        int limit = head + len;
        int i = head;
        while (i < limit) {
            int b1 = data[i++];
            if (b1 >= 0) {
                if (b1 == '\\') {
                    int b2 = data[i++];
                    switch (b2) {
                        case '"':
                            dst.put('"');
                            break;
                        case '\\':
                            dst.put('\\');
                            break;
                        case '/':
                            dst.put('/');
                            break;
                        case 'b':
                            dst.put('\b');
                            break;
                        case 'f':
                            dst.put('\f');
                            break;
                        case 'n':
                            dst.put('\n');
                            break;
                        case 'r':
                            dst.put('\r');
                            break;
                        case 't':
                            dst.put('\t');
                            break;
                        case 'u':
                            int v = Slice.digits[data[i++]];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            char b = (char) v;
                            v = Slice.digits[data[i++]];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = Slice.digits[data[i++]];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            v = Slice.digits[data[i++]];
                            if (v == -1) {
                                throw new RuntimeException("bad unicode");
                            }
                            b = (char) (b << 4);
                            b += v;
                            dst.put(b);
                            break;
                        default:
                            throw new RuntimeException("unexpected escape char: " + b2);
                    }
                } else {
                    // 1 byte, 7 bits: 0xxxxxxx
                    dst.put((char) b1);
                }
            } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                int b2 = data[i++];
                dst.put((char) (((b1 << 6) ^ b2)
                        ^
                        (((byte) 0xC0 << 6) ^
                                ((byte) 0x80 << 0))));
            } else if ((b1 >> 4) == -2) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int b2 = data[i++];
                int b3 = data[i++];
                char c = (char)
                        ((b1 << 12) ^
                                (b2 <<  6) ^
                                (b3 ^
                                        (((byte) 0xE0 << 12) ^
                                                ((byte) 0x80 <<  6) ^
                                                ((byte) 0x80 <<  0))));
                dst.put(c);
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int b2 = data[i++];
                int b3 = data[i++];
                int b4 = data[i++];
                int uc = ((b1 << 18) ^
                        (b2 << 12) ^
                        (b3 <<  6) ^
                        (b4 ^
                                (((byte) 0xF0 << 18) ^
                                        ((byte) 0x80 << 12) ^
                                        ((byte) 0x80 <<  6) ^
                                        ((byte) 0x80 <<  0))));
                dst.put(Character.highSurrogate(uc));
                dst.put(Character.lowSurrogate(uc));
            } else {
                throw new RuntimeException("unexpected input");
            }
        }
        dst.flip();
        return dst.toString();
    }
}
