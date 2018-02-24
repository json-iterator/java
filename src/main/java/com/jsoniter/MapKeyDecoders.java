package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Type;

class MapKeyDecoders {

    public static Decoder registerOrGetExisting(Type mapKeyType) {
        String cacheKey = JsoniterSpi.getMapKeyDecoderCacheKey(mapKeyType);
        Decoder mapKeyDecoder = JsoniterSpi.getMapKeyDecoder(cacheKey);
        if (null != mapKeyDecoder) {
            return mapKeyDecoder;
        }
        mapKeyDecoder = createMapKeyDecoder(mapKeyType);
        JsoniterSpi.addNewMapDecoder(cacheKey, mapKeyDecoder);
        return mapKeyDecoder;
    }

    private static Decoder createMapKeyDecoder(Type mapKeyType) {
        if (String.class == mapKeyType) {
            return new StringKeyDecoder();
        }
        Decoder decoder = CodegenImplNative.NATIVE_DECODERS.get(mapKeyType);
        if (decoder != null) {
            return new NumberKeyDecoder(decoder);
        }
        throw new JsonException("can not encode map key type: " + mapKeyType);
    }

    private static class StringKeyDecoder implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return iter.readString();
        }
    }

    private static class NumberKeyDecoder implements Decoder {

        private final Decoder decoder;

        private NumberKeyDecoder(Decoder decoder) {
            this.decoder = decoder;
        }

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            if (IterImpl.nextToken(iter) != '"') {
                throw iter.reportError("decode number map key", "expect \"");
            }
            Object key = decoder.decode(iter);
            if (IterImpl.nextToken(iter) != '"') {
                throw iter.reportError("decode number map key", "expect \"");
            }
            return key;
        }
    }
}
