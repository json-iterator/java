package com.jsoniter;

import java.io.IOException;

import static java.lang.Character.*;

class IterImplString {

    final static int[] hexDigits = new int['f' + 1];

    static {
        for (int i = 0; i < hexDigits.length; i++) {
            hexDigits[i] = -1;
        }
        for (int i = '0'; i <= '9'; ++i) {
            hexDigits[i] = (i - '0');
        }
        for (int i = 'a'; i <= 'f'; ++i) {
            hexDigits[i] = ((i - 'a') + 10);
        }
        for (int i = 'A'; i <= 'F'; ++i) {
            hexDigits[i] = ((i - 'A') + 10);
        }
    }

    public static final String readString(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '"') {
            // try fast path first
            int j = 0;
            fast_loop:
            for (; ; ) {
                // copy ascii to buffer
                int i = iter.head;
                for (; i < iter.tail && j < iter.reusableChars.length; i++, j++) {
                    c = iter.buf[i];
                    if (c == '"') {
                        iter.head = i + 1;
                        return new String(iter.reusableChars, 0, j);
                    }
                    // If we encounter a backslash, which is a beginning of an escape sequence
                    // or a high bit was set - indicating an UTF-8 encoded multibyte character,
                    // there is no chance that we can decode the string without instantiating
                    // a temporary buffer, so quit this loop
                    if ((c ^ '\\') < 1) {
                        iter.head = i;
                        break fast_loop;
                    }
                    iter.reusableChars[j] = (char) c;
                }
                if (i == iter.tail) {
                    if (IterImpl.loadMore(iter)) {
                        i = iter.head;
                        continue;
                    } else {
                        throw iter.reportError("readString", "incomplete string");
                    }
                }
                iter.head = i;
                // resize to copy more
                if (j == iter.reusableChars.length) {
                    char[] newBuf = new char[iter.reusableChars.length * 2];
                    System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                    iter.reusableChars = newBuf;
                } else {
                    break;
                }
            }
            return IterImpl.readStringSlowPath(iter, j);
        }
        if (c == 'n') {
            IterImpl.skipFixedBytes(iter, 3);
            return null;
        }
        throw iter.reportError("readString", "expect n or \"");
    }

    public static int translateHex(final byte b) {
        int val = hexDigits[b];
        if (val == -1) {
            throw new IndexOutOfBoundsException(b + " is not valid hex digit");
        }
        return val;
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
