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

class IterImplNumber {

    final static int[] intDigits = new int[127];
    final static int[] floatDigits = new int[127];
    final static int END_OF_NUMBER = -2;
    final static int DOT_IN_NUMBER = -3;
    final static int INVALID_CHAR_FOR_NUMBER = -1;
    static final long POW10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000,
            1000000000, 10000000000L, 100000000000L, 1000000000000L,
            10000000000000L, 100000000000000L, 1000000000000000L};

    static {
        for (int i = 0; i < floatDigits.length; i++) {
            floatDigits[i] = INVALID_CHAR_FOR_NUMBER;
            intDigits[i] = INVALID_CHAR_FOR_NUMBER;
        }
        for (int i = '0'; i <= '9'; ++i) {
            floatDigits[i] = (i - '0');
            intDigits[i] = (i - '0');
        }
        floatDigits[','] = END_OF_NUMBER;
        floatDigits[']'] = END_OF_NUMBER;
        floatDigits['}'] = END_OF_NUMBER;
        floatDigits[' '] = END_OF_NUMBER;
        floatDigits['.'] = DOT_IN_NUMBER;
    }

    public static final double readDouble(final JsonIterator iter) throws IOException {
        boolean negative = IterImpl.nextToken(iter) == '-';
        if (!negative) {
            iter.unreadByte();
        }
        return IterImpl.readDouble(iter, negative);
    }

    public static final float readFloat(final JsonIterator iter) throws IOException {
        return (float) IterImplNumber.readDouble(iter);
    }

    public static final int readInt(final JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '-') {
            return IterImpl.readInt(iter, IterImpl.readByte(iter));
        } else {
            int val = IterImpl.readInt(iter, c);
            if (val == Integer.MIN_VALUE) {
                throw iter.reportError("readInt", "value is too large for int");
            }
            return -val;
        }
    }

    public static final long readLong(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        boolean negative = c == '-';
        return IterImpl.readLong(iter, negative ? IterImpl.readByte(iter) : c, negative);
    }
}
