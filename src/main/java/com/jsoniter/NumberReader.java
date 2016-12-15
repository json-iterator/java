package com.jsoniter;

import java.io.IOException;

class NumberReader {
    
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

    public static final double readDouble(Jsoniter iter) throws IOException {
        final byte c = iter.nextToken();
        // when re-read using slowpath, it should include the first byte
        iter.unreadByte();
        if (c == '-') {
            // skip '-' by + 1
            return readNegativeDouble(iter, iter.head + 1);
        }
        return readPositiveDouble(iter, iter.head);
    }

    private static final double readPositiveDouble(Jsoniter iter, int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                iter.head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) + ind;
            if (ind < 0 || ind > 9) {
                return readDoubleSlowPath(iter);
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    iter.head = i;
                    return value / (double) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) + ind;
                if (ind < 0 || ind > 9) {
                    return readDoubleSlowPath(iter);
                }
            }
        }
        return readDoubleSlowPath(iter);
    }

    private static final double readNegativeDouble(Jsoniter iter, int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                iter.head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) - ind;
            if (ind < 0 || ind > 9) {
                return readDoubleSlowPath(iter);
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    iter.head = i;
                    return value / (double) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) - ind;
                if (ind < 0 || ind > 9) {
                    return readDoubleSlowPath(iter);
                }
            }
        }
        return readDoubleSlowPath(iter);
    }

    public static final double readDoubleSlowPath(Jsoniter iter) throws IOException {
        return Double.valueOf(readNumber(iter));
    }

    public static final float readFloat(Jsoniter iter) throws IOException {
        final byte c = iter.nextToken();
        // when re-read using slowpath, it should include the first byte
        iter.unreadByte();
        if (c == '-') {
            // skip '-' by + 1
            return readNegativeFloat(iter, iter.head + 1);
        }
        return readPositiveFloat(iter, iter.head);
    }

    private static final float readPositiveFloat(Jsoniter iter, int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                iter.head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) + ind;
            if (ind < 0 || ind > 9) {
                return readFloatSlowPath(iter);
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    iter.head = i;
                    return value / (float) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) + ind;
                if (ind < 0 || ind > 9) {
                    return readFloatSlowPath(iter);
                }
            }
        }
        return readFloatSlowPath(iter);
    }

    private static final float readNegativeFloat(Jsoniter iter, int start) throws IOException {
        long value = 0;
        byte c = ' ';
        int i = start;
        for (; i < iter.tail; i++) {
            c = iter.buf[i];
            if (c == ',' || c == '}' || c == ']' || c == ' ') {
                iter.head = i;
                return value;
            }
            if (c == '.') break;
            final int ind = digits[c];
            value = (value << 3) + (value << 1) - ind;
            if (ind < 0 || ind > 9) {
                return readFloatSlowPath(iter);
            }
        }
        if (c == '.') {
            i++;
            long div = 1;
            for (; i < iter.tail; i++) {
                c = iter.buf[i];
                if (c == ',' || c == '}' || c == ']' || c == ' ') {
                    iter.head = i;
                    return value / (float) div;
                }
                final int ind = digits[c];
                div = (div << 3) + (div << 1);
                value = (value << 3) + (value << 1) - ind;
                if (ind < 0 || ind > 9) {
                    return readFloatSlowPath(iter);
                }
            }
        }
        return readFloatSlowPath(iter);
    }

    public static final float readFloatSlowPath(Jsoniter iter) throws IOException {
        return Float.valueOf(readNumber(iter));
    }

    public static final String readNumber(Jsoniter iter) throws IOException {
        int j = 0;
        for (byte c = iter.nextToken(); !iter.eof; c = iter.readByte()) {
            if (j == iter.reusableChars.length) {
                char[] newBuf = new char[iter.reusableChars.length * 2];
                System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                iter.reusableChars = newBuf;
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
                    iter.reusableChars[j++] = (char) c;
                    break;
                default:
                    iter.unreadByte();
                    return new String(iter.reusableChars, 0, j);
            }
        }
        return new String(iter.reusableChars, 0, j);
    }

    public static final int readInt(Jsoniter iter) throws IOException {
        byte c = iter.nextToken();
        if (c == '-') {
            return -readUnsignedInt(iter);
        } else {
            iter.unreadByte();
            return readUnsignedInt(iter);
        }
    }

    public static final int readUnsignedInt(Jsoniter iter) throws IOException {
        // TODO: throw overflow
        byte c = iter.readByte();
        int v = digits[c];
        if (v == 0) {
            return 0;
        }
        if (v == -1) {
            throw iter.reportError("readUnsignedInt", "expect 0~9");
        }
        int result = 0;
        for (; ; ) {
            result = result * 10 + v;
            c = iter.readByte();
            v = digits[c];
            if (v == -1) {
                iter.unreadByte();
                break;
            }
        }
        return result;
    }

    public static final long readLong(Jsoniter iter) throws IOException {
        byte c = iter.nextToken();
        if (c == '-') {
            return -readUnsignedLong(iter);
        } else {
            iter.unreadByte();
            return readUnsignedLong(iter);
        }
    }

    public static final long readUnsignedLong(Jsoniter iter) throws IOException {
        // TODO: throw overflow
        byte c = iter.readByte();
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
            c = iter.readByte();
            v = digits[c];
            if (v == -1) {
                iter.unreadByte();
                break;
            }
        }
        return result;
    }

    public static final char readU4(Jsoniter iter) throws IOException {
        int v = digits[iter.readByte()];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        char b = (char) v;
        v = digits[iter.readByte()];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        v = digits[iter.readByte()];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        v = digits[iter.readByte()];
        if (v == -1) {
            throw iter.reportError("readU4", "bad unicode");
        }
        b = (char) (b << 4);
        b += v;
        return b;
    }
}
