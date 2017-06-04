package com.jsoniter.output;

import com.jsoniter.spi.*;

import java.lang.reflect.Type;

class DefaultMapKeyEncoder implements MapKeyEncoder {

    public static MapKeyEncoder registerOrGetExisting(Type mapKeyType) {
        String cacheKey = JsoniterSpi.getMapKeyEncoderCacheKey(mapKeyType);
        MapKeyEncoder mapKeyEncoder = JsoniterSpi.getMapKeyEncoder(cacheKey);
        if (null != mapKeyEncoder) {
            return mapKeyEncoder;
        }
        mapKeyEncoder = new DefaultMapKeyEncoder();
        JsoniterSpi.addNewMapEncoder(cacheKey, mapKeyEncoder);
        return mapKeyEncoder;
    }

    @Override
    public String encode(Object mapKey) {
        return mapKey.toString();
    }
}
