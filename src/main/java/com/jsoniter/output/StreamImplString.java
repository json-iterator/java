/*
this implementations contains significant code from https://github.com/ngs-doo/dsl-json/blob/master/LICENSE

Copyright (c) 2015, Nova Generacija Softvera d.o.o.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of Nova Generacija Softvera d.o.o. nor the names of its
      contributors may be used to endorse or promote products derived from this
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
                byte b4 = (byte) (c & 0xf);
                byte b3 = (byte) (c >> 4 & 0xf);
                byte b2 = (byte) (c >> 8 & 0xf);
                byte b1 = (byte) (c >> 12 & 0xf);
                stream.write((byte)'\\', (byte)'u', ITOA[b1], ITOA[b2], ITOA[b3], ITOA[b4]);
            } else {
                switch (c) {
                    case '"':
                        stream.write((byte)'\\', (byte)'"');
                        break;
                    case '\\':
                        stream.write((byte)'\\', (byte)'\\');
                        break;
                    case '\b':
                        stream.write((byte)'\\', (byte)'b');
                        break;
                    case '\f':
                        stream.write((byte)'\\', (byte)'f');
                        break;
                    case '\n':
                        stream.write((byte)'\\', (byte)'n');
                        break;
                    case '\r':
                        stream.write((byte)'\\', (byte)'r');
                        break;
                    case '\t':
                        stream.write((byte)'\\', (byte)'t');
                        break;
                    default:
                        stream.write(c);
                }
            }
        }
    }
}
