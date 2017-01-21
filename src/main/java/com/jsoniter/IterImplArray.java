package com.jsoniter;

import java.io.IOException;

class IterImplArray {

    public static final boolean readArray(final JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        switch (c) {
            case '[':
                c = IterImpl.nextToken(iter);
                if (c != ']') {
                    iter.unreadByte();
                    return true;
                }
                return false;
            case ']':
                return false;
            case ',':
                return true;
            case 'n':
                return false;
            default:
                throw iter.reportError("readArray", "expect [ or , or n or ], but found: " + (char) c);
        }
    }

    public static final boolean readArrayCB(final JsonIterator iter, final JsonIterator.ReadArrayCallback callback, Object attachment) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '[') {
            c = IterImpl.nextToken(iter);
            if (c != ']') {
                iter.unreadByte();
                if (!callback.handle(iter, attachment)) {
                    return false;
                }
                while (IterImpl.nextToken(iter) == ',') {
                    if (!callback.handle(iter, attachment)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        if (c == 'n') {
            return true;
        }
        throw iter.reportError("readArrayCB", "expect [ or n, but found: " + (char) c);
    }
}
