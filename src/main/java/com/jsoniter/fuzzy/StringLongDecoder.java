package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

public class StringLongDecoder extends Decoder.LongDecoder {

    @Override
    public long decodeLong(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringLongDecoder", "expect \", but found: " + (char) c);
        }
        long val = iter.readLong();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringLongDecoder", "expect \", but found: " + (char) c);
        }
        return val;
    }
}
