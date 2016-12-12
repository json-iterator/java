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
}
