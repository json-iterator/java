package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.io.OutputStream;

public class JsonStream extends OutputStream {

    private OutputStream out;
    private static final byte[] NULL = "null".getBytes();
    private static final byte[] TRUE = "true".getBytes();
    private static final byte[] FALSE = "false".getBytes();
    private static final byte[] INT_MIN = "-2147483648".getBytes();
    private static final byte[] LONG_MIN = "-9223372036854775808".getBytes();
    private static final byte[] ITOA = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
    private byte buf[];
    private int count;

    public JsonStream(OutputStream out, int bufSize) {
        if (bufSize < 32) {
            throw new JsonException("buffer size must be larger than 32: " + bufSize);
        }
        this.out = out;
        this.buf = new byte[bufSize];
    }

    public void reset(OutputStream out, byte[] buf) {
        this.out = out;
        this.buf = buf;
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
            writeRaw(val);
            write((int) (byte) '"');
        }
    }

    public final void writeRaw(String val) throws IOException {
        int i = 0;
        int valLen = val.length();
        // write string, the fast path, without utf8 and escape support
        for (; i < valLen && count < buf.length; i++) {
            char c = val.charAt(i);
            if (c >= 128) {
                break;
            }
            switch (c) {
                case '"':
                case '\\':
                case '/':
                case '\b':
                case '\f':
                case '\n':
                case '\r':
                case '\t':
                    break;
                default:
                    buf[count++] = (byte) c;
                    continue;
            }
            break;
        }
        if (i == valLen) {
            return;
        }
        // for the remaining parts, we process them char by char
        writeStringSlowPath(val, i, valLen);
    }

    private void writeStringSlowPath(String val, int i, int valLen) throws IOException {
        for (; i < valLen; i++) {
            int c = val.charAt(i);
            if (c >= 128) {
                write('\\');
                write('u');
                byte b4 = (byte) (c & 0xf);
                byte b3 = (byte) (c >> 4 & 0xf);
                byte b2 = (byte) (c >> 8 & 0xf);
                byte b1 = (byte) (c >> 12 & 0xf);
                write(ITOA[b1]);
                write(ITOA[b2]);
                write(ITOA[b3]);
                write(ITOA[b4]);
            } else {
                switch (c) {
                    case '"':
                        write('\\');
                        write('"');
                        break;
                    case '\\':
                        write('\\');
                        write('\\');
                        break;
                    case '/':
                        write('\\');
                        write('/');
                        break;
                    case '\b':
                        write('\\');
                        write('b');
                        break;
                    case '\f':
                        write('\\');
                        write('f');
                        break;
                    case '\n':
                        write('\\');
                        write('n');
                        break;
                    case '\r':
                        write('\\');
                        write('r');
                        break;
                    case '\t':
                        write('\\');
                        write('t');
                        break;
                    default:
                        write(c);
                }
            }
        }
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
        writeVal((int)val);
    }

    public final void writeVal(Integer val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.intValue());
        }
    }

    private final static byte [] DigitTens = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    } ;

    private final static byte [] DigitOnes = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    } ;

    /**
     * All possible chars for representing a number as a String
     */
    private final static byte[] digits = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z'
    };

    public final void writeVal(int val) throws IOException {
        if (val == Integer.MIN_VALUE) {
            write(INT_MIN);
            return;
        }
        if (val < 0) {
            write('-');
            val = -val;
        }
        if (buf.length - count < 10) {
            flushBuffer();
        }
        int charPos = count + stringSize(val);
        count = charPos;
        int q, r;
        // Generate two digits per iteration
        while (val >= 65536) {
            q = val / 100;
            // really: r = i - (q * 100);
            r = val - ((q << 6) + (q << 5) + (q << 2));
            val = q;
            buf [--charPos] = DigitOnes[r];
            buf [--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (;;) {
            q = (val * 52429) >>> (16+3);
            r = val - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buf [--charPos] = digits [r];
            val = q;
            if (val == 0) break;
        }
    }

    private final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };
    // Requires positive x
    private static int stringSize(int x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i])
                return i+1;
    }

    public final void writeVal(Long val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.longValue());
        }
    }

    public final void writeVal(long val) throws IOException {
        if (val == Long.MIN_VALUE) {
            write(LONG_MIN);
            return;
        }
        if (val < 0) {
            write('-');
            val = -val;
        }
        if (buf.length - count < 20) {
            flushBuffer();
        }
        long q;
        int r;
        int charPos = count + stringSize(val);
        count = charPos;
        char sign = 0;

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (val > Integer.MAX_VALUE) {
            q = val / 100;
            // really: r = i - (q * 100);
            r = (int)(val - ((q << 6) + (q << 5) + (q << 2)));
            val = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int)val;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (;;) {
            q2 = (i2 * 52429) >>> (16+3);
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            buf[--charPos] = digits[r];
            i2 = q2;
            if (i2 == 0) break;
        }
    }

    private static int stringSize(long x) {
        long p = 10;
        for (int i=1; i<19; i++) {
            if (x < p)
                return i;
            p = 10*p;
        }
        return 19;
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
        Codegen.getEncoder(typeLiteral.getEncoderCacheKey(), typeLiteral.getType()).encode(obj, this);
    }
}
