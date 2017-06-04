package com.jsoniter.spi;

import com.jsoniter.JsonIterator;

import java.io.IOException;
import java.lang.reflect.Type;

public class MapKeyCodecs {

    public static MapKeyCodec register(Type mapKeyType) {
        TypeLiteral typeLiteral = TypeLiteral.create(mapKeyType);
        String cacheKey = typeLiteral.getDecoderCacheKey();
        MapKeyCodec mapKeyCodec = JsoniterSpi.getMapKeyDecoder(cacheKey);
        if (null != mapKeyCodec) {
            return mapKeyCodec;
        }
        mapKeyCodec = new DefaultMapKeyCodec(typeLiteral);
        JsoniterSpi.addNewMapCodec(cacheKey, mapKeyCodec);
        return mapKeyCodec;
    }

    public static String getDecoderCacheKey(Type keyType) {
        return TypeLiteral.create(keyType).getDecoderCacheKey();
    }

    public static String getEncoderCacheKey(Type keyType) {
        return TypeLiteral.create(keyType).getDecoderCacheKey();
    }

    private static class DefaultMapKeyCodec implements MapKeyCodec {

        // can not reuse the tlsIter in JsonIterator
        // as this will be invoked while tlsIter is in use
        private ThreadLocal<JsonIterator> tlsIter = new ThreadLocal<JsonIterator>() {
            @Override
            protected JsonIterator initialValue() {
                return new JsonIterator();
            }
        };
        private final TypeLiteral mapKeyTypeLiteral;

        private DefaultMapKeyCodec(TypeLiteral mapKeyTypeLiteral) {
            this.mapKeyTypeLiteral = mapKeyTypeLiteral;
        }

        @Override
        public String encode(Object mapKey) {
            return mapKey.toString();
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
