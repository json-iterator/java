package com.jsoniter.output;

import java.io.IOException;

class StreamImplString {

    private static final byte[] ITOA = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static final void writeString(final JsonStream stream, final String val) throws IOException {
        int i = 0;
        int valLen = val.length();
        int toWriteLen = valLen;
        int bufLengthMinusTwo = stream.buf.length - 2; // make room for the quotes
        if (stream.count + toWriteLen > bufLengthMinusTwo) {
            toWriteLen = bufLengthMinusTwo - stream.count;
        }
        if (toWriteLen < 0) {
            stream.flushBuffer();
            if (stream.count + toWriteLen > bufLengthMinusTwo) {
                toWriteLen = bufLengthMinusTwo - stream.count;
            }
        }
        int n = stream.count;
        stream.buf[n++] = '"';
        // write string, the fast path, without utf8 and escape support
        for (; i < toWriteLen; i++) {
            char c = val.charAt(i);
            if (c > 31 && c != '"' && c != '\\' && c < 126) {
                stream.buf[n++] = (byte) c;
            } else {
                break;
            }
        }
        if (i == valLen) {
            stream.buf[n++] = '"';
            stream.count = n;
            return;
        }
        stream.count = n;
        // for the remaining parts, we process them char by char
        writeStringSlowPath(stream, val, i, valLen);
        stream.write('"');
    }

    public static final void writeStringWithoutQuote(final JsonStream stream, final String val) throws IOException {
        int i = 0;
        int valLen = val.length();
        int toWriteLen = valLen;
        int bufLen = stream.buf.length;
        if (stream.count + toWriteLen > bufLen) {
            toWriteLen = bufLen - stream.count;
        }
        if (toWriteLen < 0) {
            stream.flushBuffer();
            if (stream.count + toWriteLen > bufLen) {
                toWriteLen = bufLen - stream.count;
            }
        }
        int n = stream.count;
        // write string, the fast path, without utf8 and escape support
        for (; i < toWriteLen; i++) {
            char c = val.charAt(i);
            if (c > 31 && c != '"' && c != '\\' && c < 126) {
                stream.buf[n++] = (byte) c;
            } else {
                break;
            }
        }
        if (i == valLen) {
            stream.count = n;
            return;
        }
        stream.count = n;
        // for the remaining parts, we process them char by char
        writeStringSlowPath(stream, val, i, valLen);
    }

    private static void writeStringSlowPath(JsonStream stream, String val, int i, int valLen) throws IOException {
        for (; i < valLen; i++) {
            int c = val.charAt(i);
            if (c > 125) {
                stream.write('\\', 'u');
                byte b4 = (byte) (c & 0xf);
                byte b3 = (byte) (c >> 4 & 0xf);
                byte b2 = (byte) (c >> 8 & 0xf);
                byte b1 = (byte) (c >> 12 & 0xf);
                stream.write(ITOA[b1], ITOA[b2], ITOA[b3], ITOA[b4]);
            } else {
                switch (c) {
                    case '"':
                        stream.write('\\', '"');
                        break;
                    case '\\':
                        stream.write('\\', '\\');
                        break;
                    case '\b':
                        stream.write('\\', 'b');
                        break;
                    case '\f':
                        stream.write('\\', 'f');
                        break;
                    case '\n':
                        stream.write('\\', 'n');
                        break;
                    case '\r':
                        stream.write('\\', 'r');
                        break;
                    case '\t':
                        stream.write('\\', 't');
                        break;
                    default:
                        stream.write(c);
                }
            }
        }
    }
}
