package com.jsoniter;

import java.io.IOException;

// only uesd by generated code to access decoder
public class CodegenAccess {

    public static byte nextToken(Jsoniter iter) throws IOException {
        return iter.nextToken();
    }

    public static final boolean readBoolean(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getBooleanDecoder(cacheKey).decodeBoolean(iter);
    }

    public static final short readShort(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getShortDecoder(cacheKey).decodeShort(iter);
    }

    public static final int readInt(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getIntDecoder(cacheKey).decodeInt(iter);
    }

    public static final long readLong(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getLongDecoder(cacheKey).decodeLong(iter);
    }

    public static final float readFloat(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getFloatDecoder(cacheKey).decodeFloat(iter);
    }

    public static final double readDouble(String cacheKey, Jsoniter iter) throws IOException {
        return Codegen.getDoubleDecoder(cacheKey).decodeDouble(iter);
    }

    public static final <T> T read(String cacheKey, Jsoniter iter) throws IOException {
        return (T) Codegen.getDecoder(cacheKey, null).decode(iter);
    }

    public static boolean readArrayStart(Jsoniter iter) throws IOException {
        byte c = iter.nextToken();
        if (c != '[') {
            throw iter.reportError("readArrayStart", "expect [ or n");
        }
        c = iter.nextToken();
        if (c == ']') {
            return false;
        }
        iter.unreadByte();
        return true;
    }

    public static boolean readObjectStart(Jsoniter iter) throws IOException {
        byte c = iter.nextToken();
        if (c != '{') {
            throw iter.reportError("readObjectStart", "expect { or n, found: " + (char)c);
        }
        c = iter.nextToken();
        if (c == '}') {
            return false;
        }
        iter.unreadByte();
        return true;
    }

    public static void reportIncompleteObject(Jsoniter iter) {
        throw iter.reportError("genObject", "expect }");
    }

    public static void reportIncompleteArray(Jsoniter iter) {
        throw iter.reportError("genArray", "expect ]");
    }

    public static final int readObjectFieldAsHash(Jsoniter iter) throws IOException {
        if (iter.nextToken() != '"') {
            throw iter.reportError("readObjectFieldAsHash", "expect \"");
        }
        long hash = 0x811c9dc5;
        for (; ; ) {
            byte c = 0;
            int i = iter.head;
            for ( ;i < iter.tail; i++) {
                c = iter.buf[i];
                if (c == '"') {
                    break;
                }
                hash ^= c;
                hash *= 0x1000193;
            }
            if (c == '"') {
                iter.head = i + 1;
                if (iter.nextToken() != ':') {
                    throw iter.reportError("readObjectFieldAsHash", "expect :");
                }
                return (int) hash;
            }
            if (!iter.loadMore()) {
                throw iter.reportError("readObjectFieldAsHash", "unmatched quote");
            }
        }
    }

    public static final Slice readObjectFieldAsSlice(Jsoniter iter) throws IOException {
        if (iter.nextToken() != '"') {
            throw iter.reportError("readObjectFieldAsSlice", "expect \"");
        }
        Slice field = StringReader.readSlice(iter);
        boolean notCopied = field != null;
        if (skipWhitespacesWithoutLoadMore(iter)) {
            if (notCopied) {
                byte[] newBuf = new byte[field.len];
                System.arraycopy(field.data, field.head, newBuf, 0, field.len);
                field.data = newBuf;
                field.head = 0;
                field.len = newBuf.length;
            }
            if (!iter.loadMore()) {
                throw iter.reportError("readObjectFieldAsSlice", "expect : after object field");
            }
        }
        if (iter.buf[iter.head] != ':') {
            throw iter.reportError("readObjectFieldAsSlice", "expect : after object field");
        }
        iter.head++;
        if (skipWhitespacesWithoutLoadMore(iter)) {
            if (notCopied) {
                byte[] newBuf = new byte[field.len];
                System.arraycopy(field.data, field.head, newBuf, 0, field.len);
                field.data = newBuf;
                field.head = 0;
                field.len = newBuf.length;
            }
            if (!iter.loadMore()) {
                throw iter.reportError("readObjectFieldAsSlice", "expect : after object field");
            }
        }
        return field;
    }

    private final static boolean skipWhitespacesWithoutLoadMore(Jsoniter iter) throws IOException {
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];
            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    continue;
            }
            iter.head = i;
            return false;
        }
        return true;
    }
}
