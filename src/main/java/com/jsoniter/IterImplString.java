package com.jsoniter;

import java.io.IOException;

import static java.lang.Character.*;

class IterImplString {

    public static final String readString(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '"') {
            // try fast path first
            for (int i = iter.head, j = 0; i < iter.tail && j < iter.reusableChars.length; i++, j++) {
                c = iter.buf[i];
                if (c == '"') {
                    iter.head = i + 1;
                    return new String(iter.reusableChars, 0, j);
                }
                // If we encounter a backslash, which is a beginning of an escape sequence
                // or a high bit was set - indicating an UTF-8 encoded multibyte character,
                // there is no chance that we can decode the string without instantiating
                // a temporary buffer, so quit this loop
                if ((c ^ '\\') < 1) break;
                iter.reusableChars[j] = (char) c;
            }
            return readStringSlowPath(iter);
        }
        if (c == 'n') {
            IterImpl.skipUntilBreak(iter);
            return null;
        }
        throw iter.reportError("readString", "expect n or \"");
    }

    final static String readStringSlowPath(JsonIterator iter) throws IOException {
        // http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/sun/nio/cs/UTF_8.java/?v=source
        // byte => char with support of escape in one pass
        int j = 0;
        int minimumCapacity = iter.reusableChars.length - 2;
        for (; ; ) {
            if (j == minimumCapacity) {
                char[] newBuf = new char[iter.reusableChars.length * 2];
                System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                iter.reusableChars = newBuf;
                minimumCapacity = iter.reusableChars.length - 2;
            }
            int b1 = IterImpl.readByte(iter);
            if (b1 >= 0) {
                if (b1 == '"') {
                    return new String(iter.reusableChars, 0, j);
                } else if (b1 == '\\') {
                    int b2 = IterImpl.readByte(iter);
                    switch (b2) {
                        case '"':
                            iter.reusableChars[j++] = '"';
                            break;
                        case '\\':
                            iter.reusableChars[j++] = '\\';
                            break;
                        case '/':
                            iter.reusableChars[j++] = '/';
                            break;
                        case 'b':
                            iter.reusableChars[j++] = '\b';
                            break;
                        case 'f':
                            iter.reusableChars[j++] = '\f';
                            break;
                        case 'n':
                            iter.reusableChars[j++] = '\n';
                            break;
                        case 'r':
                            iter.reusableChars[j++] = '\r';
                            break;
                        case 't':
                            iter.reusableChars[j++] = '\t';
                            break;
                        case 'u':
                            iter.reusableChars[j++] = IterImplNumber.readU4(iter);
                            break;
                        default:
                            throw iter.reportError("readStringSlowPath", "unexpected escape char: " + b2);
                    }
                } else if (b1 == 0) {
                    throw iter.reportError("readStringSlowPath", "incomplete string");
                } else {
                    // 1 byte, 7 bits: 0xxxxxxx
                    iter.reusableChars[j++] = (char) b1;
                }
            } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                int b2 = IterImpl.readByte(iter);
                iter.reusableChars[j++] = (char) (((b1 << 6) ^ b2)
                        ^
                        (((byte) 0xC0 << 6) ^
                                ((byte) 0x80 << 0)));
            } else if ((b1 >> 4) == -2) {
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                int b2 = IterImpl.readByte(iter);
                int b3 = IterImpl.readByte(iter);
                char c = (char)
                        ((b1 << 12) ^
                                (b2 << 6) ^
                                (b3 ^
                                        (((byte) 0xE0 << 12) ^
                                                ((byte) 0x80 << 6) ^
                                                ((byte) 0x80 << 0))));
                iter.reusableChars[j++] = c;
            } else if ((b1 >> 3) == -2) {
                // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                int b2 = IterImpl.readByte(iter);
                int b3 = IterImpl.readByte(iter);
                int b4 = IterImpl.readByte(iter);
                int uc = ((b1 << 18) ^
                        (b2 << 12) ^
                        (b3 << 6) ^
                        (b4 ^
                                (((byte) 0xF0 << 18) ^
                                        ((byte) 0x80 << 12) ^
                                        ((byte) 0x80 << 6) ^
                                        ((byte) 0x80 << 0))));
                iter.reusableChars[j++] = highSurrogate(uc);
                iter.reusableChars[j++] = lowSurrogate(uc);
            } else {
                throw iter.reportError("readStringSlowPath", "unexpected input");
            }
        }
    }

    private static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
                + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
    }

    private static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
    }

    // slice does not allow escape
    final static int findSliceEnd(JsonIterator iter) {
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];
            if (c == '"') {
                return i + 1;
            } else if (c == '\\') {
                throw iter.reportError("findSliceEnd", "slice does not support escape char");
            }
        }
        return -1;
    }
}
