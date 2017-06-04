package com.jsoniter.output;

import java.io.IOException;
import java.io.OutputStream;

class AsciiOutputStream extends OutputStream {
    private char[] buf = new char[512];
    private int count = 0;

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int i = off;
        for (; ; ) {
            for (; i < off + len && count < buf.length; i++) {
                buf[count++] = (char) b[i];
            }
            if (count == buf.length) {
                char[] newBuf = new char[buf.length * 2];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                buf = newBuf;
            } else {
                break;
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (count == buf.length) {
            char[] newBuf = new char[buf.length * 2];
            System.arraycopy(buf, 0, newBuf, 0, buf.length);
            buf = newBuf;
        }
        buf[count++] = (char) b;
    }

    @Override
    public String toString() {
        return new String(buf, 0, count);
    }

    public void reset() {
        count = 0;
    }
}
