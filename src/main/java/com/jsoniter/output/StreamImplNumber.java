package com.jsoniter.output;

import java.io.IOException;

class StreamImplNumber {

    private final static byte[] DigitTens = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };

    private final static byte[] DigitOnes = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    /**
     * All possible chars for representing a number as a String
     */
    private final static byte[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final byte[] INT_MIN = "-2147483648".getBytes();
    private static final byte[] LONG_MIN = "-9223372036854775808".getBytes();

    public static final void writeInt(JsonStream stream, int val) throws IOException {
        if (val == Integer.MIN_VALUE) {
            stream.write(INT_MIN);
            return;
        }
        if (val < 0) {
            stream.write('-');
            val = -val;
        }
        if (stream.buf.length - stream.count < 10) {
            stream.flushBuffer();
        }
        int charPos = stream.count + stringSize(val);
        stream.count = charPos;
        int q, r;
        // Generate two digits per iteration
        while (val >= 65536) {
            q = val / 100;
            // really: r = i - (q * 100);
            r = val - ((q << 6) + (q << 5) + (q << 2));
            val = q;
            stream.buf[--charPos] = DigitOnes[r];
            stream.buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (; ; ) {
            q = (val * 52429) >>> (16 + 3);
            r = val - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            stream.buf[--charPos] = digits[r];
            val = q;
            if (val == 0) break;
        }
    }

    private final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    // Requires positive x
    private static int stringSize(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    public static final void writeLong(JsonStream stream, long val) throws IOException {
        if (val == Long.MIN_VALUE) {
            stream.write(LONG_MIN);
            return;
        }
        if (val < 0) {
            stream.write('-');
            val = -val;
        }
        if (stream.buf.length - stream.count < 20) {
            stream.flushBuffer();
        }
        long q;
        int r;
        int charPos = stream.count + stringSize(val);
        stream.count = charPos;
        char sign = 0;

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (val > Integer.MAX_VALUE) {
            q = val / 100;
            // really: r = i - (q * 100);
            r = (int)(val - ((q << 6) + (q << 5) + (q << 2)));
            val = q;
            stream.buf[--charPos] = DigitOnes[r];
            stream.buf[--charPos] = DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int)val;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            stream.buf[--charPos] = DigitOnes[r];
            stream.buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (;;) {
            q2 = (i2 * 52429) >>> (16+3);
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            stream.buf[--charPos] = digits[r];
            i2 = q2;
            if (i2 == 0) break;
        }
    }

    private static int stringSize(long x) {
        long p = 10;
        for (int i=1; i<19; i++) {
            if (x < p)
                return i;
            p = 10*p;
        }
        return 19;
    }

    private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

    public static final void writeFloat(JsonStream stream, float val) throws IOException {
        if (val < 0) {
            stream.write('-');
            val = -val;
        }
        int precision = 6;
        int exp = 1000000; // 6
        long lval = (long)val;
        stream.writeVal(lval);
        long fval = (long)((val - lval) * exp);
        if (fval == 0) {
            return;
        }
        stream.write('.');
        if (stream.buf.length - stream.count < 10) {
            stream.flushBuffer();
        }
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
        int precision = 6;
        int exp = 1000000; // 6
        long lval = (long)val;
        stream.writeVal(lval);
        long fval = (long)((val - lval) * exp);
        if (fval == 0) {
            return;
        }
        stream.write('.');
        if (stream.buf.length - stream.count < 10) {
            stream.flushBuffer();
        }
        for (int p = precision - 1; p > 0 && fval < POW10[p]; p--) {
            stream.buf[stream.count++] = '0';
        }
        stream.writeVal(fval);
        while(stream.buf[stream.count-1] == '0') {
            stream.count--;
        }
    }

}
