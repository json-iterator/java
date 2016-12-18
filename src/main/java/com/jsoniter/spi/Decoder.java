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

    class EmptyDecoder implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return null;
        }
    }

    abstract class FieldDecoder extends EmptyDecoder {
        public abstract void decode(JsonIterator iter, Object obj, String fieldName) throws IOException;
    }

    abstract class BooleanDecoder extends EmptyDecoder {
        public abstract boolean decodeBoolean(JsonIterator iter) throws IOException;
    }

    abstract class ShortDecoder extends EmptyDecoder {
        public abstract short decodeShort(JsonIterator iter) throws IOException;
    }

    abstract class IntDecoder extends EmptyDecoder {
        public abstract int decodeInt(JsonIterator iter) throws IOException;
    }

    abstract class LongDecoder extends EmptyDecoder {
        public abstract long decodeLong(JsonIterator iter) throws IOException;
    }

    abstract class FloatDecoder extends EmptyDecoder {
        public abstract float decodeFloat(JsonIterator iter) throws IOException;
    }

    abstract class DoubleDecoder extends EmptyDecoder {
        public abstract double decodeDouble(JsonIterator iter) throws IOException;
    }
}
