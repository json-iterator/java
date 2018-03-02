package com.jsoniter;

import java.io.IOException;

class IterImplSkip {

    private static final int[] BREAKS = new int[128/32]; // actually, we need 127 booleans

    static {
        BREAKS[ ' ' >> 5] |= 1 << ( ' ' & 0x1F);
        BREAKS['\t' >> 5] |= 1 << ('\t' & 0x1F);
        BREAKS['\n' >> 5] |= 1 << ('\n' & 0x1F);
        BREAKS['\r' >> 5] |= 1 << ('\r' & 0x1F);
        BREAKS[ ',' >> 5] |= 1 << ( ',' & 0x1F);
        BREAKS[ '}' >> 5] |= 1 << ( '}' & 0x1F);
        BREAKS[ ']' >> 5] |= 1 << ( ']' & 0x1F);
    }

    static boolean isBreak(byte character) {
        int inIndex = 1 << (character & 0x1F);
        return (BREAKS[character >> 5] & inIndex) == inIndex;
    }

    public static final void skip(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        switch (c) {
            case '"':
                IterImpl.skipString(iter);
                return;
            case '-':
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
                IterImpl.skipUntilBreak(iter);
                return;
            case 't':
            case 'n':
                IterImpl.skipFixedBytes(iter, 3); // true or null
                return;
            case 'f':
                IterImpl.skipFixedBytes(iter, 4); // false
                return;
            case '[':
                IterImpl.skipArray(iter);
                return;
            case '{':
                IterImpl.skipObject(iter);
                return;
            default:
                throw iter.reportError("IterImplSkip", "do not know how to skip: " + c);
        }
    }

    // adapted from: https://github.com/buger/jsonparser/blob/master/parser.go
    // Tries to find the end of string
    // Support if string contains escaped quote symbols.
    final static int findStringEnd(JsonIterator iter) {
        boolean escaped = false;
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];
            if (c == '"') {
                if (!escaped) {
                    return i + 1;
                } else {
                    int j = i - 1;
                    for (; ; ) {
                        if (j < iter.head || iter.buf[j] != '\\') {
                            // even number of backslashes
                            // either end of buffer, or " found
                            return i + 1;
                        }
                        j--;
                        if (j < iter.head || iter.buf[j] != '\\') {
                            // odd number of backslashes
                            // it is \" or \\\"
                            break;
                        }
                        j--;
                    }
                }
            } else if (c == '\\') {
                escaped = true;
            }
        }
        return -1;
    }
}
