package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Type;

class MapKeyDecoders {

    public static MapKeyDecoder register(Type mapKeyType) {
        TypeLiteral typeLiteral = TypeLiteral.create(mapKeyType);
        String cacheKey = typeLiteral.getDecoderCacheKey();
        MapKeyDecoder mapKeyDecoder = JsoniterSpi.getMapKeyDecoder(cacheKey);
        if (null != mapKeyDecoder) {
            return mapKeyDecoder;
        }
        mapKeyDecoder = new DefaultMapKeyDecoder(typeLiteral);
        JsoniterSpi.addNewMapDecoder(cacheKey, mapKeyDecoder);
        return mapKeyDecoder;
    }

    private static class DefaultMapKeyDecoder implements MapKeyDecoder {

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
        public Object decode(Slice mapKey) {
            JsonIterator iter = tlsIter.get();
            iter.reset(mapKey);
            try {
                return iter.read(mapKeyTypeLiteral);
            } catch (IOException e) {
                throw new JsonException(e);
            }
        }
    }
}
