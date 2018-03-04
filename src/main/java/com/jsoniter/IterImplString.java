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
package com.jsoniter;

import com.jsoniter.slice.DirectSlice;
import com.jsoniter.slice.Slice;
import com.jsoniter.slice.StringSlice;
import com.jsoniter.spi.JsonException;

import java.io.IOException;

class IterImplString {

    private static final int[] hexDigits = new int['f' + 1];

    static {
        for (int i = 0; i < hexDigits.length; i++) {
            hexDigits[i] = -1;
        }
        for (int i = '0'; i <= '9'; ++i) {
            hexDigits[i] = (i - '0');
        }
        for (int i = 'a'; i <= 'f'; ++i) {
            hexDigits[i] = ((i - 'a') + 10);
        }
        for (int i = 'A'; i <= 'F'; ++i) {
            hexDigits[i] = ((i - 'A') + 10);
        }
    }

    public static String readString(JsonIterator iter) throws IOException {
        Slice slice = readSlice(iter);
        return slice == null ? null : slice.string();
    }

    static Slice readSlice(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c != '"') {
            if (c == 'n') {
                IterImpl.skipFixedBytes(iter, 3);
                return null;
            }
            throw iter.reportError("readString", "expect string or null, but " + (char) c);
        }
        return parse(iter);
    }

    private static Slice parse(JsonIterator iter) {
        final int head = iter.head;
        byte c;// try fast path first
        int i = iter.head;
        // this code will trigger jvm hotspot pattern matching to highly optimized assembly
        int bound = iter.reusableChars.length;
        bound = IterImpl.updateStringCopyBound(iter, bound);
        for(int j = 0; j < bound; j++) {
            c = iter.buf[i++];
            if (c == '"') {
                iter.head = i;
                DirectSlice ds = iter.rDirectSlice;
                ds.reset(iter.buf, head, i - 1);
                return ds;
            }
            // If we encounter a backslash, which is a beginning of an escape sequence
            // or a high bit was set - indicating an UTF-8 encoded multibyte character,
            // there is no chance that we can decode the string without instantiating
            // a temporary buffer, so quit this loop
            if ((c ^ '\\') < 1) {
                break;
            }
            iter.reusableChars[j] = (char) c;
        }
        int alreadyCopied = 0;
        if (i > iter.head) {
            alreadyCopied = i - iter.head - 1;
            iter.head = i - 1;
        }
        return readStringSlowPath(iter, alreadyCopied);
    }

    private static Slice readStringSlowPath(JsonIterator iter, int j) {
        try {
            boolean isExpectingLowSurrogate = false;
            for (int i = iter.head; i < iter.tail; ) {
                int bc = iter.buf[i++];
                if (bc == '"') {
                    iter.head = i;
                    StringSlice ss = iter.rStringSlice;
                    ss.reset(new String(iter.reusableChars, 0, j));
                    return ss;
                }
                if (bc == '\\') {
                    bc = iter.buf[i++];
                    switch (bc) {
                        case 'b':
                            bc = '\b';
                            break;
                        case 't':
                            bc = '\t';
                            break;
                        case 'n':
                            bc = '\n';
                            break;
                        case 'f':
                            bc = '\f';
                            break;
                        case 'r':
                            bc = '\r';
                            break;
                        case '"':
                        case '/':
                        case '\\':
                            break;
                        case 'u':
                            bc = (IterImplString.translateHex(iter.buf[i++]) << 12) +
                                    (IterImplString.translateHex(iter.buf[i++]) << 8) +
                                    (IterImplString.translateHex(iter.buf[i++]) << 4) +
                                    IterImplString.translateHex(iter.buf[i++]);
                            if (Character.isHighSurrogate((char) bc)) {
                                if (isExpectingLowSurrogate) {
                                    throw new JsonException("invalid surrogate");
                                } else {
                                    isExpectingLowSurrogate = true;
                                }
                            } else if (Character.isLowSurrogate((char) bc)) {
                                if (isExpectingLowSurrogate) {
                                    isExpectingLowSurrogate = false;
                                } else {
                                    throw new JsonException("invalid surrogate");
                                }
                            } else {
                                if (isExpectingLowSurrogate) {
                                    throw new JsonException("invalid surrogate");
                                }
                            }
                            break;

                        default:
                            throw iter.reportError("readStringSlowPath", "invalid escape character: " + bc);
                    }
                } else if ((bc & 0x80) != 0) {
                    final int u2 = iter.buf[i++];
                    if ((bc & 0xE0) == 0xC0) {
                        bc = ((bc & 0x1F) << 6) + (u2 & 0x3F);
                    } else {
                        final int u3 = iter.buf[i++];
                        if ((bc & 0xF0) == 0xE0) {
                            bc = ((bc & 0x0F) << 12) + ((u2 & 0x3F) << 6) + (u3 & 0x3F);
                        } else {
                            final int u4 = iter.buf[i++];
                            if ((bc & 0xF8) == 0xF0) {
                                bc = ((bc & 0x07) << 18) + ((u2 & 0x3F) << 12) + ((u3 & 0x3F) << 6) + (u4 & 0x3F);
                            } else {
                                throw iter.reportError("readStringSlowPath", "invalid unicode character");
                            }

                            if (bc >= 0x10000) {
                                // check if valid unicode
                                if (bc >= 0x110000)
                                    throw iter.reportError("readStringSlowPath", "invalid unicode character");

                                // split surrogates
                                final int sup = bc - 0x10000;
                                if (iter.reusableChars.length == j) {
                                    char[] newBuf = new char[iter.reusableChars.length * 2];
                                    System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                                    iter.reusableChars = newBuf;
                                }
                                iter.reusableChars[j++] = (char) ((sup >>> 10) + 0xd800);
                                if (iter.reusableChars.length == j) {
                                    char[] newBuf = new char[iter.reusableChars.length * 2];
                                    System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                                    iter.reusableChars = newBuf;
                                }
                                iter.reusableChars[j++] = (char) ((sup & 0x3ff) + 0xdc00);
                                continue;
                            }
                        }
                    }
                }
                if (iter.reusableChars.length == j) {
                    char[] newBuf = new char[iter.reusableChars.length * 2];
                    System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
                    iter.reusableChars = newBuf;
                }
                iter.reusableChars[j++] = (char) bc;
            }
            throw iter.reportError("readStringSlowPath", "incomplete string");
        } catch (IndexOutOfBoundsException e) {
            throw iter.reportError("readString", "incomplete string");
        }
    }

    public static int translateHex(final byte b) {
        int val = hexDigits[b];
        if (val == -1) {
            throw new IndexOutOfBoundsException(b + " is not valid hex digit");
        }
        return val;
    }

    // slice does not allow escape
    static int findSliceEnd(JsonIterator iter) {
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];
            if (c == '"') {
                return i + 1;
            } else if (c == '\\') {
                throw iter.reportError("findSliceEnd", "slice does not support escape char");
            }
        }
        return -1;
    }
}
