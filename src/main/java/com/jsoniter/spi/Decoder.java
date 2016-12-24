package com.jsoniter.spi;

import com.jsoniter.JsonIterator;

import java.io.IOException;

public interface Decoder {
    /**
     * Customized decoder to read values from iterator
     *
     * @param iter the iterator instance
     * @return the value to set
     * @throws IOException when reading from iterator triggered error
     */
    Object decode(JsonIterator iter) throws IOException;

    abstract class BooleanDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Boolean.valueOf(decodeBoolean(iter));
        }

        public abstract boolean decodeBoolean(JsonIterator iter) throws IOException;
    }

    abstract class ShortDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Short.valueOf(decodeShort(iter));
        }

        public abstract short decodeShort(JsonIterator iter) throws IOException;
    }

    abstract class IntDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Integer.valueOf(decodeInt(iter));
        }

        public abstract int decodeInt(JsonIterator iter) throws IOException;
    }

    abstract class LongDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Long.valueOf(decodeLong(iter));
        }

        public abstract long decodeLong(JsonIterator iter) throws IOException;
    }

    abstract class FloatDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Float.valueOf(decodeFloat(iter));
        }

        public abstract float decodeFloat(JsonIterator iter) throws IOException;
    }

    abstract class DoubleDecoder implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Double.valueOf(decodeDouble(iter));
        }

        public abstract double decodeDouble(JsonIterator iter) throws IOException;
    }
}
