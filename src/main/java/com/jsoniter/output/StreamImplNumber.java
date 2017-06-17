/*
this implementations contains significant code from https://github.com/ngs-doo/dsl-json/blob/master/LICENSE

Copyright (c) 2015, Nova Generacija Softvera d.o.o.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of Nova Generacija Softvera d.o.o. nor the names of its
      contributors may be used to endorse or promote products derived from this
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jsoniter.output;

import java.io.IOException;

class StreamImplNumber {

    private final static int[] DIGITS = new int[1000];

    static {
        for (int i = 0; i < 1000; i++) {
            DIGITS[i] = (i < 10 ? (2 << 24) : i < 100 ? (1 << 24) : 0)
                    + (((i / 100) + '0') << 16)
                    + ((((i / 10) % 10) + '0') << 8)
                    + i % 10 + '0';
        }
    }

    private static final byte[] MIN_INT = "-2147483648".getBytes();

    public static final void writeInt(final JsonStream stream, int value) throws IOException {
        stream.ensure(12);
        byte[] buf = stream.buf;
        int pos = stream.count;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                System.arraycopy(MIN_INT, 0, buf, pos, MIN_INT.length);
                stream.count = pos + MIN_INT.length;
                return;
            }
            value = -value;
            buf[pos++] = '-';
        }
        final int q1 = value / 1000;
        if (q1 == 0) {
            pos += writeFirstBuf(buf, DIGITS[value], pos);
            stream.count = pos;
            return;
        }
        final int r1 = value - q1 * 1000;
        final int q2 = q1 / 1000;
        if (q2 == 0) {
            final int v1 = DIGITS[r1];
            final int v2 = DIGITS[q1];
            int off = writeFirstBuf(buf, v2, pos);
            writeBuf(buf, v1, pos + off);
            stream.count = pos + 3 + off;
            return;
        }
        final int r2 = q1 - q2 * 1000;
        final long q3 = q2 / 1000;
        final int v1 = DIGITS[r1];
        final int v2 = DIGITS[r2];
        if (q3 == 0) {
            pos += writeFirstBuf(buf, DIGITS[q2], pos);
        } else {
            final int r3 = (int) (q2 - q3 * 1000);
            buf[pos++] = (byte) (q3 + '0');
            writeBuf(buf, DIGITS[r3], pos);
            pos += 3;
        }
        writeBuf(buf, v2, pos);
        writeBuf(buf, v1, pos + 3);
        stream.count = pos + 6;
    }

    private static int writeFirstBuf(final byte[] buf, final int v, int pos) {
        final int start = v >> 24;
        if (start == 0) {
            buf[pos++] = (byte) (v >> 16);
            buf[pos++] = (byte) (v >> 8);
        } else if (start == 1) {
            buf[pos++] = (byte) (v >> 8);
        }
        buf[pos] = (byte) v;
        return 3 - start;
    }

    private static void writeBuf(final byte[] buf, final int v, int pos) {
        buf[pos] = (byte) (v >> 16);
        buf[pos + 1] = (byte) (v >> 8);
        buf[pos + 2] = (byte) v;
    }

    private static final byte[] MIN_LONG = "-9223372036854775808".getBytes();

    public static final void writeLong(final JsonStream stream, long value) throws IOException {
        stream.ensure(22);
        byte[] buf = stream.buf;
        int pos = stream.count;
        if (value < 0) {
            if (value == Long.MIN_VALUE) {
                System.arraycopy(MIN_LONG, 0, buf, pos, MIN_LONG.length);
                stream.count = pos + MIN_LONG.length;
                return;
            }
            value = -value;
            buf[pos++] = '-';
        }
        final long q1 = value / 1000;
        if (q1 == 0) {
            pos += writeFirstBuf(buf, DIGITS[(int) value], pos);
            stream.count = pos;
            return;
        }
        final int r1 = (int) (value - q1 * 1000);
        final long q2 = q1 / 1000;
        if (q2 == 0) {
            final int v1 = DIGITS[r1];
            final int v2 = DIGITS[(int) q1];
            int off = writeFirstBuf(buf, v2, pos);
            writeBuf(buf, v1, pos + off);
            stream.count = pos + 3 + off;
            return;
        }
        final int r2 = (int) (q1 - q2 * 1000);
        final long q3 = q2 / 1000;
        if (q3 == 0) {
            final int v1 = DIGITS[r1];
            final int v2 = DIGITS[r2];
            final int v3 = DIGITS[(int) q2];
            pos += writeFirstBuf(buf, v3, pos);
            writeBuf(buf, v2, pos);
            writeBuf(buf, v1, pos + 3);
            stream.count = pos + 6;
            return;
        }
        final int r3 = (int) (q2 - q3 * 1000);
        final int q4 = (int) (q3 / 1000);
        if (q4 == 0) {
            final int v1 = DIGITS[r1];
            final int v2 = DIGITS[r2];
            final int v3 = DIGITS[r3];
            final int v4 = DIGITS[(int) q3];
            pos += writeFirstBuf(buf, v4, pos);
            writeBuf(buf, v3, pos);
            writeBuf(buf, v2, pos + 3);
            writeBuf(buf, v1, pos + 6);
            stream.count = pos + 9;
            return;
        }
        final int r4 = (int) (q3 - q4 * 1000);
        final int q5 = q4 / 1000;
        if (q5 == 0) {
            final int v1 = DIGITS[r1];
            final int v2 = DIGITS[r2];
            final int v3 = DIGITS[r3];
            final int v4 = DIGITS[r4];
            final int v5 = DIGITS[q4];
            pos += writeFirstBuf(buf, v5, pos);
            writeBuf(buf, v4, pos);
            writeBuf(buf, v3, pos + 3);
            writeBuf(buf, v2, pos + 6);
            writeBuf(buf, v1, pos + 9);
            stream.count = pos + 12;
            return;
        }
        final int r5 = q4 - q5 * 1000;
        final int q6 = q5 / 1000;
        final int v1 = DIGITS[r1];
        final int v2 = DIGITS[r2];
        final int v3 = DIGITS[r3];
        final int v4 = DIGITS[r4];
        final int v5 = DIGITS[r5];
        if (q6 == 0) {
            pos += writeFirstBuf(buf, DIGITS[q5], pos);
        } else {
            final int r6 = q5 - q6 * 1000;
            buf[pos++] = (byte) (q6 + '0');
            writeBuf(buf, DIGITS[r6], pos);
            pos += 3;
        }
        writeBuf(buf, v5, pos);
        writeBuf(buf, v4, pos + 3);
        writeBuf(buf, v3, pos + 6);
        writeBuf(buf, v2, pos + 9);
        writeBuf(buf, v1, pos + 12);
        stream.count = pos + 15;
    }

    private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

    public static final void writeFloat(JsonStream stream, float val) throws IOException {
        if (val < 0) {
            stream.write('-');
            val = -val;
        }
        if (val > 0x4ffffff) {
            stream.writeRaw(Float.toString(val));
            return;
        }
        int precision = 6;
        int exp = 1000000; // 6
        long lval = (long)(val * exp + 0.5);
        stream.writeVal(lval / exp);
        long fval = lval % exp;
        if (fval == 0) {
            return;
        }
        stream.write('.');
        stream.ensure(11);
        for (int p = precision - 1; p > 0 && fval < POW10[p]; p--) {
            stream.buf[stream.count++] = '0';
        }
        stream.writeVal(fval);
        while(stream.buf[stream.count-1] == '0') {
            stream.count--;
        }
    }

    public static final void writeDouble(JsonStream stream, double val) throws IOException {
        if (val < 0) {
            val = -val;
            stream.write('-');
        }
        if (val > 0x4ffffff) {
            stream.writeRaw(Double.toString(val));
            return;
        }
        int precision = 6;
        int exp = 1000000; // 6
        long lval = (long)(val * exp + 0.5);
        stream.writeVal(lval / exp);
        long fval = lval % exp;
        if (fval == 0) {
            return;
        }
        stream.write('.');
        stream.ensure(11);
        for (int p = precision - 1; p > 0 && fval < POW10[p]; p--) {
            stream.buf[stream.count++] = '0';
        }
        stream.writeVal(fval);
        while(stream.buf[stream.count-1] == '0') {
            stream.count--;
        }
    }

}
