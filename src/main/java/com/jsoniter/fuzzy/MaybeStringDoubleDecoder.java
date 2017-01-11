package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

public class MaybeStringDoubleDecoder extends Decoder.DoubleDecoder {

    @Override
    public double decodeDouble(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            CodegenAccess.unreadByte(iter);
            return iter.readDouble();
        }
        double val = iter.readDouble();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringDoubleDecoder", "expect \", but found: " + (char) c);
        }
        return val;
    }
}
