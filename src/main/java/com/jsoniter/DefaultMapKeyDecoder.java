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

    private final TypeLiteral mapKeyTypeLiteral;

    private DefaultMapKeyDecoder(TypeLiteral mapKeyTypeLiteral) {
        this.mapKeyTypeLiteral = mapKeyTypeLiteral;
    }

    @Override
    public Object decode(Slice encodedMapKey) {
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        iter.reset(encodedMapKey);
        try {
            return iter.read(mapKeyTypeLiteral);
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }
}
