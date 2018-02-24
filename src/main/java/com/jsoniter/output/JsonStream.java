package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

public class JsonStream extends OutputStream {

    public Config configCache;
    int indention = 0;
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

    final void ensure(int minimal) throws IOException {
        int available = buf.length - count;
        if (available < minimal) {
            if (count > 1024) {
                flushBuffer();
            }
            growAtLeast(minimal);
        }
    }

    private final void growAtLeast(int minimal) {
        int toGrow = buf.length;
        if (toGrow < minimal) {
            toGrow = minimal;
        }
        byte[] newBuf = new byte[buf.length + toGrow];
        System.arraycopy(buf, 0, newBuf, 0, buf.length);
        buf = newBuf;
    }

    public final void write(int b) throws IOException {
        ensure(1);
        buf[count++] = (byte) b;
    }

    public final void write(byte b1, byte b2) throws IOException {
        ensure(2);
        buf[count++] = b1;
        buf[count++] = b2;
    }

    public final void write(byte b1, byte b2, byte b3) throws IOException {
        ensure(3);
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4) throws IOException {
        ensure(4);
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5) throws IOException {
        ensure(5);
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) throws IOException {
        ensure(6);
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
        buf[count++] = b6;
    }

    public final void write(byte b[], int off, int len) throws IOException {
        if (out == null) {
            ensure(len);
        } else {
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
        if (out == null) {
            return;
        }
        if (count > 0) {
            flushBuffer();
        }
        out.close();
        this.out = null;
        count = 0;
    }

    final void flushBuffer() throws IOException {
        if (out == null) {
            return;
        }
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
        if (out == null) {
            ensure(remaining);
            val.getBytes(0, remaining, buf, count);
            count += remaining;
            return;
        }
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
        indention += currentConfig().indentionStep();
        write('[');
    }

    public final void writeMore() throws IOException {
        write(',');
        writeIndention();
    }

    public void writeIndention() throws IOException {
        writeIndention(0);
    }

    private void writeIndention(int delta) throws IOException {
        if (indention == 0) {
            return;
        }
        write('\n');
        int toWrite = indention - delta;
        ensure(toWrite);
        for (int i = 0; i < toWrite && count < buf.length; i++) {
            buf[count++] = ' ';
        }
    }

    public final void writeArrayEnd() throws IOException {
        int indentionStep = currentConfig().indentionStep();
        writeIndention(indentionStep);
        indention -= indentionStep;
        write(']');
    }

    public final void writeObjectStart() throws IOException {
        int indentionStep = currentConfig().indentionStep();
        indention += indentionStep;
        write('{');
    }

    public final void writeObjectField(String field) throws IOException {
        writeVal(field);
        if (indention > 0) {
            write((byte) ':', (byte) ' ');
        } else {
            write(':');
        }
    }

    public final void writeObjectField(Object key) throws IOException {
        Encoder encoder = MapKeyEncoders.registerOrGetExisting(key.getClass());
        writeObjectField(key, encoder);
    }

    public final void writeObjectField(Object key, Encoder keyEncoder) throws IOException {
        keyEncoder.encode(key, this);
        if (indention > 0) {
            write((byte) ':', (byte) ' ');
        } else {
            write(':');
        }
    }

    public final void writeObjectEnd() throws IOException {
        int indentionStep = currentConfig().indentionStep();
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
        String cacheKey = currentConfig().getEncoderCacheKey(clazz);
        Codegen.getEncoder(cacheKey, clazz).encode(obj, this);
    }

    public final <T> void writeVal(TypeLiteral<T> typeLiteral, T obj) throws IOException {
        if (null == obj) {
            writeNull();
        } else {
            Config config = currentConfig();
            String cacheKey = config.getEncoderCacheKey(typeLiteral.getType());
            Codegen.getEncoder(cacheKey, typeLiteral.getType()).encode(obj, this);
        }
    }

    public final <T> void writeVal(Type type, T obj) throws IOException {
        if (null == obj) {
            writeNull();
        } else {
            Config config = currentConfig();
            String cacheKey = config.getEncoderCacheKey(type);
            Codegen.getEncoder(cacheKey, type).encode(obj, this);
        }
    }

    public Config currentConfig() {
        if (configCache != null) {
            return configCache;
        }
        configCache = JsoniterSpi.getCurrentConfig();
        return configCache;
    }

    public static void serialize(Config config, Object obj, OutputStream out) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            serialize(obj, out);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }

    }

    public static void serialize(Object obj, OutputStream out) {
        JsonStream stream = JsonStreamPool.borrowJsonStream();
        try {
            try {
                stream.reset(out);
                stream.writeVal(obj);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonStreamPool.returnJsonStream(stream);
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
        JsonStream stream = JsonStreamPool.borrowJsonStream();
        try {
            try {
                stream.reset(out);
                stream.writeVal(typeLiteral, obj);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonStreamPool.returnJsonStream(stream);
        }
    }

    public static void serialize(Type type, Object obj, OutputStream out) {
        JsonStream stream = JsonStreamPool.borrowJsonStream();
        try {
            try {
                stream.reset(out);
                stream.writeVal(type, obj);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonStreamPool.returnJsonStream(stream);
        }
    }

    public static String serialize(Config config, Object obj) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return serialize(config.escapeUnicode(), obj.getClass(), obj);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static String serialize(Object obj) {
        return serialize(JsoniterSpi.getCurrentConfig().escapeUnicode(), obj.getClass(), obj);
    }

    public static String serialize(Config config, TypeLiteral typeLiteral, Object obj) {
        JsoniterSpi.setCurrentConfig(config);
        try {
            return serialize(config.escapeUnicode(), typeLiteral.getType(), obj);
        } finally {
            JsoniterSpi.clearCurrentConfig();
        }
    }

    public static String serialize(TypeLiteral typeLiteral, Object obj) {
        return serialize(JsoniterSpi.getCurrentConfig().escapeUnicode(), typeLiteral.getType(), obj);
    }

    public static String serialize(boolean escapeUnicode, Type type, Object obj) {
        JsonStream stream = JsonStreamPool.borrowJsonStream();
        try {
            stream.reset(null);
            stream.writeVal(type, obj);
            if (escapeUnicode) {
                return new String(stream.buf, 0, stream.count);
            } else {
                return new String(stream.buf, 0, stream.count, "UTF8");
            }
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonStreamPool.returnJsonStream(stream);
        }
    }

    public static void setMode(EncodingMode mode) {
        Config newConfig = JsoniterSpi.getDefaultConfig().copyBuilder().encodingMode(mode).build();
        JsoniterSpi.setDefaultConfig(newConfig);
        JsoniterSpi.setCurrentConfig(newConfig);

    }

    public static void setIndentionStep(int indentionStep) {
        Config newConfig = JsoniterSpi.getDefaultConfig().copyBuilder().indentionStep(indentionStep).build();
        JsoniterSpi.setDefaultConfig(newConfig);
        JsoniterSpi.setCurrentConfig(newConfig);
    }

    public static void registerNativeEncoder(Class clazz, Encoder.ReflectionEncoder encoder) {
        CodegenImplNative.NATIVE_ENCODERS.put(clazz, encoder);
    }

    public Slice buffer() {
        return new Slice(buf, 0, count);
    }
}
