package com.jsoniter.output;

import java.io.IOException;

class StreamImplString {

    private static final byte[] ITOA = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static final void writeString(JsonStream stream, String val) throws IOException {
        int i = 0;
        int valLen = val.length();
        // write string, the fast path, without utf8 and escape support
        for (; i < valLen && stream.count < stream.buf.length; i++) {
            char c = val.charAt(i);
            if (c > 125 || c < 32) {
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
                    stream.buf[stream.count++] = (byte) c;
                    continue;
            }
            break;
        }
        if (i == valLen) {
            return;
        }
        // for the remaining parts, we process them char by char
        writeStringSlowPath(stream, val, i, valLen);
    }

    private static void writeStringSlowPath(JsonStream stream, String val, int i, int valLen) throws IOException {
        for (; i < valLen; i++) {
            int c = val.charAt(i);
            if (c > 125 || c < 32) {
                stream.write('\\');
                stream.write('u');
                byte b4 = (byte) (c & 0xf);
                byte b3 = (byte) (c >> 4 & 0xf);
                byte b2 = (byte) (c >> 8 & 0xf);
                byte b1 = (byte) (c >> 12 & 0xf);
                stream.write(ITOA[b1]);
                stream.write(ITOA[b2]);
                stream.write(ITOA[b3]);
                stream.write(ITOA[b4]);
            } else {
                switch (c) {
                    case '"':
                        stream.write('\\');
                        stream.write('"');
                        break;
                    case '\\':
                        stream.write('\\');
                        stream.write('\\');
                        break;
                    case '/':
                        stream.write('\\');
                        stream.write('/');
                        break;
                    case '\b':
                        stream.write('\\');
                        stream.write('b');
                        break;
                    case '\f':
                        stream.write('\\');
                        stream.write('f');
                        break;
                    case '\n':
                        stream.write('\\');
                        stream.write('n');
                        break;
                    case '\r':
                        stream.write('\\');
                        stream.write('r');
                        break;
                    case '\t':
                        stream.write('\\');
                        stream.write('t');
                        break;
                    default:
                        stream.write(c);
                }
            }
        }
    }
}
