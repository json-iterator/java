package com.jsoniter.spi;

public interface MapKeyCodec {

    String encode(Object mapKey);
    Object decode(Slice encodedMapKey);
}
