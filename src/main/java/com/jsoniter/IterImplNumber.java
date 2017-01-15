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
    
    final static int[] digits = new int[256];
    private final static int[] intDigits = new int[256];
    private final static int[] floatDigits = new int[256];
    private final static int END_OF_NUMBER = -2;
    private final static int DOT_IN_NUMBER = -3;
    private final static int INVALID_CHAR_FOR_NUMBER = -1;
    private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};
    private final static long LONG_SAFE_TO_MULTIPLY_10 = (Long.MAX_VALUE / 10) - 10;
    private final static int INT_SAFE_TO_MULTIPLY_10 = (Integer.MAX_VALUE / 10) - 10;

    static {
        for (int i = 0; i < digits.length; i++) {
            digits[i] = INVALID_CHAR_FOR_NUMBER;
            floatDigits[i] = INVALID_CHAR_FOR_NUMBER;
            intDigits[i] = INVALID_CHAR_FOR_NUMBER;
        }
        for (int i = '0'; i <= '9'; ++i) {
            digits[i] = (i - '0');
            floatDigits[i] = (i - '0');
            intDigits[i] = (i - '0');
        }
        for (int i = 'a'; i <= 'f'; ++i) {
            digits[i] = ((i - 'a') + 10);
        }
        for (int i = 'A'; i <= 'F'; ++i) {
            digits[i] = ((i - 'A') + 10);
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
            }
            if (value > LONG_SAFE_TO_MULTIPLY_10) {
                return readDoubleSlowPath(iter);
            }
            value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
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
                }
                decimalPlaces++;
                if (value > LONG_SAFE_TO_MULTIPLY_10) {
                    return readDoubleSlowPath(iter);
                }
                value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
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
            if (value > LONG_SAFE_TO_MULTIPLY_10) {
                return readFloatSlowPath(iter);
            }
            value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
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
                if (value > LONG_SAFE_TO_MULTIPLY_10) {
                    return readFloatSlowPath(iter);
                }
                value = (value << 3) + (value << 1) + ind; // value = value * 10 + ind;
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
        for (;;) {
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
            return -readUnsignedInt(iter, IterImpl.readByte(iter));
        } else {
            return readUnsignedInt(iter, c);
        }
    }

    public static final int readUnsignedInt(final JsonIterator iter, byte c) throws IOException {
        int result = intDigits[c];
        if (result == 0) {
            return 0;
        }
        if (result == INVALID_CHAR_FOR_NUMBER) {
            throw iter.reportError("readUnsignedInt", "expect 0~9");
        }
        for (;;) {
            for (int i = iter.head; i < iter.tail; i++) {
                int ind = intDigits[iter.buf[i]];
                if (ind == INVALID_CHAR_FOR_NUMBER) {
                    iter.head = i;
                    return result;
                }
                if (result > INT_SAFE_TO_MULTIPLY_10) {
                    throw iter.reportError("readUnsignedInt", "value is too large for int");
                }
                result = (result << 3) + (result << 1) + ind;
            }
            if (!IterImpl.loadMore(iter)) {
                return result;
            }
        }
    }

    public static final long readLong(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return -readUnsignedLong(iter);
        } else {
            iter.unreadByte();
            return readUnsignedLong(iter);
        }
    }

    public static final long readUnsignedLong(JsonIterator iter) throws IOException {
        // TODO: throw overflow
        byte c = IterImpl.readByte(iter);
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw iter.reportError("readUnsignedLong", "expect 0~9");
        }
        long result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = IterImpl.readByte(iter);
            v = digits[c];
            if (v == -1) {
                iter.unreadByte();
                break;
            }
        }
        return result;
    }

    public static final char readU4(JsonIterator iter) throws IOException {
        int v = digits[IterImpl.readByte(iter)];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        char b = (char) v;
        v = digits[IterImpl.readByte(iter)];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        v = digits[IterImpl.readByte(iter)];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        v = digits[IterImpl.readByte(iter)];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        return b;
    }
}
