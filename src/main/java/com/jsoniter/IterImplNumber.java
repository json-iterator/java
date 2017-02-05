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
package com.jsoniter;

import java.io.IOException;

// TODO: make separate implementation for streaming and non-streaming
class IterImplNumber {

    private final static int[] intDigits = new int[127];
    private final static int[] floatDigits = new int[127];
    private final static int END_OF_NUMBER = -2;
    private final static int DOT_IN_NUMBER = -3;
    private final static int INVALID_CHAR_FOR_NUMBER = -1;
    private static final long POW10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000,
            1000000000, 10000000000L, 100000000000L, 1000000000000L,
            10000000000000L, 100000000000000L, 1000000000000000L};

    static {
        for (int i = 0; i < floatDigits.length; i++) {
            floatDigits[i] = INVALID_CHAR_FOR_NUMBER;
            intDigits[i] = INVALID_CHAR_FOR_NUMBER;
        }
        for (int i = '0'; i <= '9'; ++i) {
            floatDigits[i] = (i - '0');
            intDigits[i] = (i - '0');
        }
        floatDigits[','] = END_OF_NUMBER;
        floatDigits[']'] = END_OF_NUMBER;
        floatDigits['}'] = END_OF_NUMBER;
        floatDigits[' '] = END_OF_NUMBER;
        floatDigits['.'] = DOT_IN_NUMBER;
    }

    public static final double readDouble(final JsonIterator iter) throws IOException {
        final byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return -readPositiveDouble(iter);
        } else {
            iter.unreadByte();
            return readPositiveDouble(iter);
        }
    }

    private static final double readPositiveDouble(final JsonIterator iter) throws IOException {
        long value = 0; // without the dot
        byte c = ' ';
        int i = iter.head;
        non_decimal_loop:
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            final int ind = floatDigits[c];
            switch (ind) {
                case INVALID_CHAR_FOR_NUMBER:
                    return readDoubleSlowPath(iter);
                case END_OF_NUMBER:
                    iter.head = i;
                    return value;
                case DOT_IN_NUMBER:
                    break non_decimal_loop;
                default:
                    value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
                    if (value < 0) {
                        // overflow
                        return readDoubleSlowPath(iter);
                    }
            }
        }
        if (c == '.') {
            i++;
            int decimalPlaces = 0;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                final int ind = floatDigits[c];
                switch (ind) {
                    case END_OF_NUMBER:
                        if (decimalPlaces > 0 && decimalPlaces < POW10.length) {
                            iter.head = i;
                            return value / (double) POW10[decimalPlaces];
                        }
                        // too many decimal places
                        return readDoubleSlowPath(iter);
                    case INVALID_CHAR_FOR_NUMBER:
                    case DOT_IN_NUMBER:
                        return readDoubleSlowPath(iter);
                    default:
                        decimalPlaces++;
                        value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
                        if (value < 0) {
                            // overflow
                            return readDoubleSlowPath(iter);
                        }
                }
            }
        }
        return readDoubleSlowPath(iter);
    }

    public static final double readDoubleSlowPath(final JsonIterator iter) throws IOException {
        try {
            return Double.valueOf(readNumber(iter));
        } catch (NumberFormatException e) {
            throw iter.reportError("readDoubleSlowPath", e.toString());
        }
    }

    public static final float readFloat(final JsonIterator iter) throws IOException {
        final byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return -readPositiveFloat(iter);
        } else {
            iter.unreadByte();
            return readPositiveFloat(iter);
        }
    }

    private static final float readPositiveFloat(final JsonIterator iter) throws IOException {
        long value = 0; // without the dot
        byte c = ' ';
        int i = iter.head;
        non_decimal_loop:
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            final int ind = floatDigits[c];
            switch (ind) {
                case INVALID_CHAR_FOR_NUMBER:
                    return readFloatSlowPath(iter);
                case END_OF_NUMBER:
                    iter.head = i;
                    return value;
                case DOT_IN_NUMBER:
                    break non_decimal_loop;
            }
            value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
            if (value < 0) {
                // overflow
                return readFloatSlowPath(iter);
            }
        }
        if (c == '.') {
            i++;
            int decimalPlaces = 0;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                final int ind = floatDigits[c];
                switch (ind) {
                    case END_OF_NUMBER:
                        if (decimalPlaces > 0 && decimalPlaces < POW10.length) {
                            iter.head = i;
                            return (float) (value / (double) POW10[decimalPlaces]);
                        }
                        // too many decimal places
                        return readFloatSlowPath(iter);
                    case INVALID_CHAR_FOR_NUMBER:
                    case DOT_IN_NUMBER:
                        return readFloatSlowPath(iter);
                }
                decimalPlaces++;
                value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
                if (value < 0) {
                    // overflow
                    return readFloatSlowPath(iter);
                }
            }
        }
        return readFloatSlowPath(iter);
    }

    public static final float readFloatSlowPath(final JsonIterator iter) throws IOException {
        try {
            return Float.valueOf(readNumber(iter));
        } catch (NumberFormatException e) {
            throw iter.reportError("readFloatSlowPath", e.toString());
        }
    }

    public static final String readNumber(final JsonIterator iter) throws IOException {
        int j = 0;
        for (; ; ) {
            for (int i = iter.head; i < iter.tail; i++) {
                if (j == iter.reusableChars.length) {
                    char[] newBuf = new char[iter.reusableChars.length * 2];
                    System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                    iter.reusableChars = newBuf;
                }
                byte c = iter.buf[i];
                switch (c) {
                    case '-':
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
                        iter.reusableChars[j++] = (char) c;
                        break;
                    default:
                        iter.head = i;
                        return new String(iter.reusableChars, 0, j);
                }
            }
            if (!IterImpl.loadMore(iter)) {
                return new String(iter.reusableChars, 0, j);
            }
        }
    }

    public static final int readInt(final JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return -readPositiveInt(iter, IterImpl.readByte(iter));
        } else {
            return readPositiveInt(iter, c);
        }
    }

    public static final int readPositiveInt(final JsonIterator iter, byte c) throws IOException {
        int ind = intDigits[c];
        if (ind == 0) {
            return 0;
        }
        if (ind == INVALID_CHAR_FOR_NUMBER) {
            throw iter.reportError("readPositiveInt", "expect 0~9");
        }
        if (iter.tail - iter.head < 8) {
            return readIntSlowPath(iter, ind);
        }
        int i = iter.head;
        int ind2 = intDigits[iter.buf[i]];
        if (ind2 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind;
        }
        int ind3 = intDigits[iter.buf[++i]];
        if (ind3 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 10 + ind2;
        }
        int ind4 = intDigits[iter.buf[++i]];
        if (ind4 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 100 + ind2 * 10 + ind3;
        }
        int ind5 = intDigits[iter.buf[++i]];
        if (ind5 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 1000 + ind2 * 100 + ind3 * 10 + ind4;
        }
        int ind6 = intDigits[iter.buf[++i]];
        if (ind6 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 10000 + ind2 * 1000 + ind3 * 100 + ind4 * 10 + ind5;
        }
        int ind7 = intDigits[iter.buf[++i]];
        if (ind7 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 100000 + ind2 * 10000 + ind3 * 1000 + ind4 * 100 + ind5 * 10 + ind6;
        }
        int ind8 = intDigits[iter.buf[++i]];
        if (ind8 == INVALID_CHAR_FOR_NUMBER) {
            iter.head = i;
            return ind * 1000000 + ind2 * 100000 + ind3 * 10000 + ind4 * 1000 + ind5 * 100 + ind6 * 10 + ind7;
        }
        int ind9 = intDigits[iter.buf[++i]];
        int val = ind * 10000000 + ind2 * 1000000 + ind3 * 100000 + ind4 * 10000 + ind5 * 1000 + ind6 * 100 + ind7 * 10 + ind8;
        iter.head = i;
        if (ind9 == INVALID_CHAR_FOR_NUMBER) {
            return val;
        }
        return readIntSlowPath(iter, val);
    }

    private static int readIntSlowPath(JsonIterator iter, int value) throws IOException {
        for (; ; ) {
            for (int i = iter.head; i < iter.tail; i++) {
                int ind = intDigits[iter.buf[i]];
                if (ind == INVALID_CHAR_FOR_NUMBER) {
                    iter.head = i;
                    return value;
                }
                value = (value << 3) + (value << 1) + ind;
                if (value < 0) {
                    // overflow
                    if (value == Integer.MIN_VALUE) {
                        // if there is more number following, subsequent read will fail anyway
                        iter.head = i;
                        return value;
                    } else {
                        throw iter.reportError("readPositiveInt", "value is too large for int");
                    }
                }
            }
            if (!IterImpl.loadMore(iter)) {
                return value;
            }
        }
    }

    public static final long readLong(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return -readPositiveLong(iter, IterImpl.readByte(iter));
        } else {
            return readPositiveLong(iter, c);
        }
    }

    public static final long readPositiveLong(final JsonIterator iter, byte c) throws IOException {
        int ind = intDigits[c];
        if (ind == 0) {
            return 0;
        }
        if (ind == INVALID_CHAR_FOR_NUMBER) {
            throw iter.reportError("readPositiveInt", "expect 0~9");
        }
        long value = ind;
        for (; ; ) {
            for (int i = iter.head; i < iter.tail; i++) {
                ind = intDigits[iter.buf[i]];
                if (ind == INVALID_CHAR_FOR_NUMBER) {
                    iter.head = i;
                    return value;
                }
                value = (value << 3) + (value << 1) + ind;
                if (value < 0) {
                    // overflow
                    if (value == Long.MIN_VALUE) {
                        // if there is more number following, subsequent read will fail anyway
                        iter.head = i;
                        return value;
                    } else {
                        throw iter.reportError("readPositiveLong", "value is too large for long");
                    }
                }
            }
            if (!IterImpl.loadMore(iter)) {
                return value;
            }
        }
    }
}
