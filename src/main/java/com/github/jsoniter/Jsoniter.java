package com.github.jsoniter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Jsoniter implements Closeable {

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
}
