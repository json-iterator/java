package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.io.OutputStream;

public class JsonStream extends OutputStream {

    public static int defaultIndentionStep = 0;
    public int indentionStep = defaultIndentionStep;
    private int indention = 0;
    private OutputStream out;
    byte buf[];
    int count;

    public JsonStream(OutputStream out, int bufSize) {
        if (bufSize < 32) {
            throw new JsonException("buffer size must be larger than 32: " + bufSize);
        }
        this.out = out;
        this.buf = new byte[bufSize];
    }

    public void reset(OutputStream out) {
        this.out = out;
        this.count = 0;
    }

    public final void write(int b) throws IOException {
        if (count == buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    public final void write(byte b1, byte b2) throws IOException {
        if (count >= buf.length - 1) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
    }

    public final void write(byte b1, byte b2, byte b3) throws IOException {
        if (count >= buf.length - 2) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4) throws IOException {
        if (count >= buf.length - 3) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5) throws IOException {
        if (count >= buf.length - 4) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) throws IOException {
        if (count >= buf.length - 5) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
        buf[count++] = b6;
    }

    public final void write(byte b[], int off, int len) throws IOException {
        if (len >= buf.length - count) {
            if (len >= buf.length) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
                flushBuffer();
                out.write(b, off, len);
                return;
            }
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (count > 0) {
            flushBuffer();
        }
        out.close();
        this.out = null;
        count = 0;
    }

    final void flushBuffer() throws IOException {
        out.write(buf, 0, count);
        count = 0;
    }

    public final void writeVal(String val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            StreamImplString.writeString(this, val);
        }
    }

    public final void writeRaw(String val) throws IOException {
        writeRaw(val, val.length());
    }

    public final void writeRaw(String val, int remaining) throws IOException {
        int i = 0;
        for (; ; ) {
            int available = buf.length - count;
            if (available < remaining) {
                remaining -= available;
                int j = i + available;
                val.getBytes(i, j, buf, count);
                count = buf.length;
                flushBuffer();
                i = j;
            } else {
                int j = i + remaining;
                val.getBytes(i, j, buf, count);
                count += remaining;
                return;
            }
        }
    }

    public final void writeVal(Boolean val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            if (val) {
                writeTrue();
            } else {
                writeFalse();
            }
        }
    }

    public final void writeVal(boolean val) throws IOException {
        if (val) {
            writeTrue();
        } else {
            writeFalse();
        }
    }

    public final void writeTrue() throws IOException {
        write((byte) 't', (byte) 'r', (byte) 'u', (byte) 'e');
    }

    public final void writeFalse() throws IOException {
        write((byte) 'f', (byte) 'a', (byte) 'l', (byte) 's', (byte) 'e');
    }

    public final void writeVal(Short val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.intValue());
        }
    }

    public final void writeVal(short val) throws IOException {
        writeVal((int) val);
    }

    public final void writeVal(Integer val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.intValue());
        }
    }

    public final void writeVal(int val) throws IOException {
        StreamImplNumber.writeInt(this, val);
    }


    public final void writeVal(Long val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.longValue());
        }
    }

    public final void writeVal(long val) throws IOException {
        StreamImplNumber.writeLong(this, val);
    }


    public final void writeVal(Float val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.floatValue());
        }
    }

    public final void writeVal(float val) throws IOException {
        StreamImplNumber.writeFloat(this, val);
    }

    public final void writeVal(Double val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.doubleValue());
        }
    }

    public final void writeVal(double val) throws IOException {
        StreamImplNumber.writeDouble(this, val);
    }

    public final void writeVal(Any val) throws IOException {
        val.writeTo(this);
    }

    public final void writeNull() throws IOException {
        write((byte) 'n', (byte) 'u', (byte) 'l', (byte) 'l');
    }

    public final void writeEmptyObject() throws IOException {
        write((byte) '{', (byte) '}');
    }

    public final void writeEmptyArray() throws IOException {
        write((byte) '[', (byte) ']');
    }

    public final void writeArrayStart() throws IOException {
        indention += indentionStep;
        write('[');
        writeIndention();
    }

    public final void writeMore() throws IOException {
        write(',');
        writeIndention();
    }

    private void writeIndention() throws IOException {
        writeIndention(0);
    }

    private void writeIndention(int delta) throws IOException {
        if (indention == 0) {
            return;
        }
        write('\n');
        int toWrite = indention - delta;
        int i = 0;
        for (; ; ) {
            for (; i < toWrite && count < buf.length; i++) {
                buf[count++] = ' ';
            }
            if (i == toWrite) {
                break;
            } else {
                flushBuffer();
            }
        }
    }

    public final void writeArrayEnd() throws IOException {
        writeIndention(indentionStep);
        indention -= indentionStep;
        write(']');
    }

    public final void writeObjectStart() throws IOException {
        indention += indentionStep;
        write('{');
        writeIndention();
    }

    public final void writeObjectField(String field) throws IOException {
        writeVal(field);
        write(':');
    }

    public final void writeObjectEnd() throws IOException {
        writeIndention(indentionStep);
        indention -= indentionStep;
        write('}');
    }

    public final void writeVal(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        Class<?> clazz = obj.getClass();
        String cacheKey = TypeLiteral.create(clazz).getEncoderCacheKey();
        Codegen.getEncoder(cacheKey, clazz).encode(obj, this);
    }

    public final <T> void writeVal(TypeLiteral<T> typeLiteral, T obj) throws IOException {
        if (null == obj) {
            writeNull();
        } else {
            Codegen.getEncoder(typeLiteral.getEncoderCacheKey(), typeLiteral.getType()).encode(obj, this);
        }
    }

    private final static ThreadLocal<JsonStream> tlsStream = new ThreadLocal<JsonStream>() {
        @Override
        protected JsonStream initialValue() {
            return new JsonStream(null, 512);
        }
    };

    public static void serialize(Config config, Object obj, OutputStream out) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            serialize(obj, out);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }

    }

    public static void serialize(Object obj, OutputStream out) {
        JsonStream stream = tlsStream.get();
        try {
            try {
                stream.reset(out);
                stream.writeVal(obj);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static void serialize(Config config, TypeLiteral typeLiteral, Object obj, OutputStream out) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            serialize(typeLiteral, obj, out);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static void serialize(TypeLiteral typeLiteral, Object obj, OutputStream out) {
        JsonStream stream = tlsStream.get();
        try {
            try {
                stream.reset(out);
                stream.writeVal(typeLiteral, obj);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private final static ThreadLocal<AsciiOutputStream> tlsAsciiOutputStream = new ThreadLocal<AsciiOutputStream>() {
        @Override
        protected AsciiOutputStream initialValue() {
            return new AsciiOutputStream();
        }
    };

    public static String serialize(Config config, Object obj) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return serialize(obj);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static String serialize(Object obj) {
        AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
        serialize(obj, asciiOutputStream);
        return asciiOutputStream.toString();
    }

    public static String serialize(Config config, TypeLiteral typeLiteral, Object obj) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return serialize(typeLiteral, obj);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static String serialize(TypeLiteral typeLiteral, Object obj) {
        AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
        serialize(typeLiteral, obj, asciiOutputStream);
        return asciiOutputStream.toString();
    }

    public static void setMode(EncodingMode mode) {
        Codegen.setMode(mode);
    }

    public static void registerNativeEncoder(Class clazz, Encoder encoder) {
        CodegenImplNative.NATIVE_ENCODERS.put(clazz, encoder);
    }
}
