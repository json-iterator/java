package com.jsoniter.output;

import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class JsonStream extends OutputStream {

    private OutputStream out;
    private static final Charset charset = Charset.forName("utf8");
    private static final byte[] NULL = "null".getBytes();
    private byte buf[];
    private int count;
    private char stack[] = new char[64];
    private int level = 0;

    public JsonStream(OutputStream out, int bufSize) {
        this.out = out;
        this.buf = new byte[bufSize];
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

    public synchronized void flush() throws IOException {
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
        level = 0;
    }

    private final void flushBuffer() throws IOException {
        out.write(buf, 0, count);
        count = 0;
    }

    public final void writeVal(String val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            write((int) (byte) '"');
            flushBuffer();
            writeRaw(val);
            write((int) (byte) '"');
        }
    }

    public final void writeRaw(String val) throws IOException {
        // TODO: do not allocate new buffer every time, encode utf16 into utf8 directly
        write(val.getBytes(charset));
    }

    public final void writeVal(Boolean val) throws IOException {
        // TODO: convert boolean directly into bytes
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Boolean.toString(val));
        }
    }

    public final void writeVal(boolean val) throws IOException {
        // TODO: convert boolean directly into bytes
        writeRaw(Boolean.toString(val));
    }

    public final void writeVal(Short val) throws IOException {
        // TODO: convert short directly into bytes
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Short.toString(val));
        }
    }

    public final void writeVal(short val) throws IOException {
        // TODO: convert short directly into bytes
        writeRaw(Short.toString(val));
    }

    public final void writeVal(Integer val) throws IOException {
        // TODO: convert int directly into bytes
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Integer.toString(val));
        }
    }

    public final void writeVal(int val) throws IOException {
        // TODO: convert int directly into bytes
        writeRaw(Integer.toString(val));
    }

    public final void writeVal(Long val) throws IOException {
        // TODO: convert long directly into bytes
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Long.toString(val));
        }
    }

    public final void writeVal(long val) throws IOException {
        // TODO: convert long directly into bytes
        writeRaw(Long.toString(val));
    }

    public final void writeVal(Float val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Float.toString(val));
        }
    }

    public final void writeVal(float val) throws IOException {
        writeRaw(Float.toString(val));
    }

    public final void writeVal(Double val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeRaw(Double.toString(val));
        }
    }

    public final  void writeNull() throws IOException {
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

    public final void writeVal(double val) throws IOException {
        writeRaw(Double.toString(val));
    }

    public final void startArray() throws IOException {
        write('[');
    }

    public final void writeMore() throws IOException {
        write(',');
    }

    public final void endArray() throws IOException {
        count--; // remove the last ,
        write(']');
    }

    public final void startObject() throws IOException {
        write('{');
    }

    public final void writeField(String field) throws IOException {
        writeVal(field);
        write(':');
    }

    public final void endObject() throws IOException {
        count--; // remove the last ,
        write('}');
    }

    public final void writeVal(Object obj) throws IOException {
        Class<?> clazz = obj.getClass();
        String cacheKey = TypeLiteral.generateEncoderCacheKey(clazz);
        Codegen.getEncoder(cacheKey, clazz).encode(obj, this);
    }

    public final <T> void writeVal(TypeLiteral<T> typeLiteral, T obj) throws IOException {
        Codegen.getEncoder(typeLiteral.getCacheKey(), typeLiteral.getType()).encode(obj, this);
    }
}
