package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Type;

class DefaultMapKeyDecoder implements MapKeyDecoder {

    public static MapKeyDecoder registerOrGetExisting(Type mapKeyType) {
        String cacheKey = JsoniterSpi.getMapKeyDecoderCacheKey(mapKeyType);
        MapKeyDecoder mapKeyDecoder = JsoniterSpi.getMapKeyDecoder(cacheKey);
        if (null != mapKeyDecoder) {
            return mapKeyDecoder;
        }
        mapKeyDecoder = new DefaultMapKeyDecoder(TypeLiteral.create(mapKeyType));
        JsoniterSpi.addNewMapDecoder(cacheKey, mapKeyDecoder);
        return mapKeyDecoder;
    }

    // can not reuse the tlsIter in JsonIterator
    // as this will be invoked while tlsIter is in use
    private ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
        @Override
        protected JsonIterator initialValue() {
            return new JsonIterator();
        }
    };
    private final TypeLiteral mapKeyTypeLiteral;

    private DefaultMapKeyDecoder(TypeLiteral mapKeyTypeLiteral) {
        this.mapKeyTypeLiteral = mapKeyTypeLiteral;
    }

    @Override
    public Object decode(Slice encodedMapKey) {
        JsonIterator iter = tlsIter.get();
        iter.reset(encodedMapKey);
        try {
            return iter.read(mapKeyTypeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
