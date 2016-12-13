package com.jsoniter;

import java.io.IOException;

// only uesd by generated code to access decoder
public class CodegenAccess {

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
}
