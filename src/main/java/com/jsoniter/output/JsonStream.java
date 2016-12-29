package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.io.OutputStream;

public class JsonStream extends OutputStream {

    public static int defaultIndentionStep = 0;
    public int indentionStep = defaultIndentionStep;
    public int indention = 0;
    private OutputStream out;
    private static final byte[] NULL = "null".getBytes();
    private static final byte[] TRUE = "true".getBytes();
    private static final byte[] FALSE = "false".getBytes();
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
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    public final void write(byte b[], int off, int len) throws IOException {
        if (len >= buf.length) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        if (len > buf.length - count) {
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
            write((int) (byte) '"');
            writeRaw(val);
            write((int) (byte) '"');
        }
    }

    public final void writeRaw(String val) throws IOException {
        StreamImplString.writeString(this, val);
    }

    public final void writeVal(Boolean val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            if (val) {
                write(TRUE);
            } else {
                write(FALSE);
            }
        }
    }

    public final void writeVal(boolean val) throws IOException {
        if (val) {
            write(TRUE);
        } else {
            write(FALSE);
        }
    }

    public final void writeVal(Short val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            write(val.intValue());
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
            writeRaw(Float.toString(val));
        }
    }

    public final void writeVal(float val) throws IOException {
        StreamImplNumber.writeFloat(this, val);
    }

    public final void writeVal(Double val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Double.toString(val));
        }
    }

    public final void writeVal(double val) throws IOException {
        StreamImplNumber.writeDouble(this, val);
    }

    public final void writeNull() throws IOException {
        write(NULL, 0, NULL.length);
    }

    public final void writeEmptyObject() throws IOException {
        write('{');
        write('}');
    }

    public final void writeEmptyArray() throws IOException {
        write('[');
        write(']');
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
        Codegen.getEncoder(typeLiteral.getEncoderCacheKey(), typeLiteral.getType()).encode(obj, this);
    }

    private final static ThreadLocal<JsonStream> tlsStream = new ThreadLocal<JsonStream>() {
        @Override
        protected JsonStream initialValue() {
            return new JsonStream(null, 4096);
        }
    };

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

    private final static ThreadLocal<AsciiOutputStream> tlsAsciiOutputStream = new ThreadLocal<AsciiOutputStream>() {
        @Override
        protected AsciiOutputStream initialValue() {
            return new AsciiOutputStream();
        }
    };

    public static String serialize(Object obj) {
        AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
        serialize(obj, asciiOutputStream);
        return asciiOutputStream.toString();
    }

    public static void setMode(EncodingMode mode) {
        Codegen.setMode(mode);
    }
}
