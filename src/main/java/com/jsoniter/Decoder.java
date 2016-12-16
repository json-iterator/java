package com.jsoniter;

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

    interface BooleanDecoder extends Decoder {
        boolean decodeBoolean(JsonIterator iter) throws IOException;
    }

    interface ShortDecoder extends Decoder {
        short decodeShort(JsonIterator iter) throws IOException;
    }

    interface IntDecoder extends Decoder {
        int decodeInt(JsonIterator iter) throws IOException;
    }

    interface LongDecoder extends Decoder {
        long decodeLong(JsonIterator iter) throws IOException;
    }

    interface FloatDecoder extends Decoder {
        float decodeFloat(JsonIterator iter) throws IOException;
    }

    interface DoubleDecoder extends Decoder {
        double decodeDouble(JsonIterator iter) throws IOException;
    }
}
