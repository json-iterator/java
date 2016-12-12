package com.jsoniter;

import java.io.IOException;
import java.lang.reflect.Type;

public interface Decoder {
    /**
     * Customized decoder to read values from iterator
     *
     * @param type the type of object we are setting value to
     * @param iter the iterator instance
     * @return the value to set
     * @throws IOException
     */
    Object decode(Type type, Jsoniter iter) throws IOException;

    interface BooleanDecoder extends Decoder {
        boolean decodeBoolean(Jsoniter iter) throws IOException;
    }

    interface ShortDecoder extends Decoder {
        short decodeShort(Jsoniter iter) throws IOException;
    }

    interface IntDecoder extends Decoder {
        int decodeInt(Jsoniter iter) throws IOException;
    }

    interface LongDecoder extends Decoder {
        long decodeLong(Jsoniter iter) throws IOException;
    }

    interface FloatDecoder extends Decoder {
        float decodeFloat(Jsoniter iter) throws IOException;
    }

    interface DoubleDecoder extends Decoder {
        double decodeDouble(Jsoniter iter) throws IOException;
    }
}
