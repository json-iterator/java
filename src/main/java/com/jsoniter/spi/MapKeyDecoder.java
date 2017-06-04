package com.jsoniter.spi;

public interface MapKeyDecoder {
    Object decode(Slice encodedMapKey);
}
