package com.jsoniter.output;

import com.jsoniter.*;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;

public class CodegenAccess {
    public static void writeVal(String cacheKey, Object obj, JsonStream stream) throws IOException {
        JsoniterSpi.getEncoder(cacheKey).encode(obj, stream);
    }

    public static void writeVal(String cacheKey, boolean obj, JsonStream stream) throws IOException {
        Encoder.BooleanEncoder encoder = (Encoder.BooleanEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeBoolean(obj, stream);
    }

    public static void writeVal(String cacheKey, byte obj, JsonStream stream) throws IOException {
        Encoder.ShortEncoder encoder = (Encoder.ShortEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeShort(obj, stream);
    }

    public static void writeVal(String cacheKey, short obj, JsonStream stream) throws IOException {
        Encoder.ShortEncoder encoder = (Encoder.ShortEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeShort(obj, stream);
    }

    public static void writeVal(String cacheKey, int obj, JsonStream stream) throws IOException {
        Encoder.IntEncoder encoder = (Encoder.IntEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeInt(obj, stream);
    }

    public static void writeVal(String cacheKey, char obj, JsonStream stream) throws IOException {
        Encoder.IntEncoder encoder = (Encoder.IntEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeInt(obj, stream);
    }

    public static void writeVal(String cacheKey, long obj, JsonStream stream) throws IOException {
        Encoder.LongEncoder encoder = (Encoder.LongEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeLong(obj, stream);
    }

    public static void writeVal(String cacheKey, float obj, JsonStream stream) throws IOException {
        Encoder.FloatEncoder encoder = (Encoder.FloatEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeFloat(obj, stream);
    }

    public static void writeVal(String cacheKey, double obj, JsonStream stream) throws IOException {
        Encoder.DoubleEncoder encoder = (Encoder.DoubleEncoder) JsoniterSpi.getEncoder(cacheKey);
        encoder.encodeDouble(obj, stream);
    }

    public static void staticGenEncoders(TypeLiteral[] typeLiterals) {
        Codegen.staticGenEncoders(typeLiterals);
    }
}
