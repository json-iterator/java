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

import java.io.IOException;

class IterImplString {

    final static int[] hexDigits = new int['f' + 1];

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

    public static final String readString(JsonIterator iter) throws IOException {
        byte c = IterImpl.readByte(iter);
        if (c != '"') {
            if (readStringIsNull(iter, c)) {
                return null;
            }
        }
        int j = parse(iter);
        return new String(iter.reusableChars, 0, j);
    }

    private static int parse(JsonIterator iter) throws IOException {
        byte c;// try fast path first
        int i = iter.head;
        // this code will trigger jvm hotspot pattern matching to highly optimized assembly
        int bound = iter.reusableChars.length;
        bound = IterImpl.updateStringCopyBound(iter, bound);
        for(int j = 0; j < bound; j++) {
            c = iter.buf[i++];
            if (c == '"') {
                iter.head = i;
                return j;
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
        return IterImpl.readStringSlowPath(iter, alreadyCopied);
    }

    private static boolean readStringIsNull(JsonIterator iter, byte c) throws IOException {
        if (c == 'n') {
            IterImpl.skipFixedBytes(iter, 3);
            return true;
        } else {
            c = IterImpl.nextToken(iter);
            if (c == 'n') {
                IterImpl.skipFixedBytes(iter, 3);
                return true;
            }
        }
        return false;
    }

    public static int translateHex(final byte b) {
        int val = hexDigits[b];
        if (val == -1) {
            throw new IndexOutOfBoundsException(b + " is not valid hex digit");
        }
        return val;
    }

    // slice does not allow escape
    final static int findSliceEnd(JsonIterator iter) {
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
