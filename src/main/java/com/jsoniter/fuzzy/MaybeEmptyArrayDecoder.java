package com.jsoniter.fuzzy;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

public class MaybeEmptyArrayDecoder implements Decoder {

    @Override
    public Object decode(JsonIterator iter) throws IOException {
        if (iter.whatIsNext() == ValueType.ARRAY) {
            if (iter.readArray()) {
                throw iter.reportError("MaybeEmptyArrayDecoder", "this field is object. if input is array, it can only be empty");
            } else {
                // empty array parsed as null
                return null;
            }
        } else {
            return iter.read(iter);
        }
    }
}
